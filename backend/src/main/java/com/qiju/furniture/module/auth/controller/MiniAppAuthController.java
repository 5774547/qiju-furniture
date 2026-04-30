package com.qiju.furniture.module.auth.controller;

import com.qiju.furniture.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

/**
 * 微信小程序登录 Controller
 */
@Tag(name = "微信小程序登录")
@RestController
@RequestMapping("/api/auth")
public class MiniAppAuthController {

    private static final Logger log = LoggerFactory.getLogger(MiniAppAuthController.class);

    @Value("${wechat.appid:}")
    private String appId;

    @Value("${wechat.secret:}")
    private String secret;

    @Value("${wechat.dev-mode:true}")
    private boolean devMode;

    private final com.qiju.furniture.module.user.service.UserService userService;
    private final com.qiju.furniture.common.security.JwtUtil jwtUtil;

    public MiniAppAuthController(com.qiju.furniture.module.user.service.UserService userService,
                                  com.qiju.furniture.common.security.JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Operation(summary = "微信小程序一键登录")
    @PostMapping("/miniapp-login")
    public Result<Map<String, Object>> miniAppLogin(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        String nickName = body.getOrDefault("nickName", "");
        String avatarUrl = body.getOrDefault("avatarUrl", "");

        if (code == null || code.isEmpty()) {
            return Result.error(400, "code is required");
        }

        String openid;
        if (devMode || appId.isEmpty() || secret.isEmpty()) {
            // 开发模式：用 code 作为 openid 前缀
            openid = "dev_" + code.substring(0, Math.min(8, code.length()));
            log.info("Dev mode: using openid={} (code={})", openid, code);
        } else {
            // 生产模式：调微信接口换取 openid
            try {
                HttpClient client = HttpClient.newHttpClient();
                String url = String.format(
                    "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                    appId, secret, code
                );
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();
                HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
                @SuppressWarnings("unchecked")
                Map<String, Object> wechatResp = new com.fasterxml.jackson.databind.ObjectMapper()
                        .readValue(resp.body(), Map.class);
                if (wechatResp.containsKey("errcode")) {
                    log.error("WeChat login failed: {}", wechatResp);
                    return Result.error(500, "微信登录失败: " + wechatResp.get("errmsg"));
                }
                openid = (String) wechatResp.get("openid");
            } catch (Exception e) {
                log.error("WeChat API call failed", e);
                return Result.error(500, "微信服务调用失败");
            }
        }

        // 查找或创建用户
        com.qiju.furniture.module.user.entity.User user = userService.lambdaQuery()
                .eq(com.qiju.furniture.module.user.entity.User::getUsername, openid)
                .one();

        if (user == null) {
            // 创建新用户
            user = com.qiju.furniture.module.user.entity.User.builder()
                    .username(openid)
                    .nickname(nickName.isEmpty() ? "微信用户" : nickName)
                    .avatar(avatarUrl)
                    .password("") // 微信登录不需要密码
                    .role("user")
                    .status(1)
                    .build();
            userService.save(user);
        } else if (!nickName.isEmpty()) {
            // 更新昵称和头像
            user.setNickname(nickName);
            if (!avatarUrl.isEmpty()) user.setAvatar(avatarUrl);
            userService.updateById(user);
        }

        // 生成 JWT
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        return Result.ok(Map.of(
            "token", token,
            "user", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "nickname", user.getNickname(),
                "avatar", user.getAvatar() != null ? user.getAvatar() : "",
                "role", user.getRole(),
                "companyName", user.getCompanyName() != null ? user.getCompanyName() : ""
            )
        ));
    }
}
