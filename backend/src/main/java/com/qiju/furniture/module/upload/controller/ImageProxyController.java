package com.qiju.furniture.module.upload.controller;

import com.qiju.furniture.common.result.Result;
import com.qiju.furniture.common.service.MinioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 图片代理 Controller
 *
 * 双通道：
 * 1. GET /api/images/{filename} — 字节流（浏览器/普通场景）
 * 2. GET /api/images/data/{filename} — Base64 JSON（微信小程序通过 wx.request 获取）
 */
@Tag(name = "图片代理")
@RestController
@RequestMapping("/api/images")
public class ImageProxyController {

    private static final Logger log = LoggerFactory.getLogger(ImageProxyController.class);

    private final MinioService minioService;

    public ImageProxyController(MinioService minioService) {
        this.minioService = minioService;
    }

    @Operation(summary = "获取产品图片（字节流）")
    @GetMapping("/raw/**")
    public void getImage(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 从 URL 提取文件名: /api/images/raw/product_1.jpg
            String path = request.getRequestURI();
            String filename = path.substring(path.lastIndexOf('/') + 1);

            String bucket = "qiju-furniture";
            String objectName = "products/" + filename;

            if (!minioService.objectExists(bucket, objectName)) {
                response.setStatus(404);
                return;
            }

            String contentType = "image/jpeg";
            if (filename.endsWith(".png")) contentType = "image/png";
            else if (filename.endsWith(".gif")) contentType = "image/gif";
            else if (filename.endsWith(".webp")) contentType = "image/webp";
            response.setContentType(contentType);

            try (InputStream is = minioService.getObject(bucket, objectName)) {
                is.transferTo(response.getOutputStream());
            }
        } catch (Exception e) {
            log.error("Failed to proxy image: {}", filename, e);
            response.setStatus(500);
        }
    }

    @Operation(summary = "获取产品图片（Base64 JSON）")
    @GetMapping("/data/{filename}")
    public Result<Map<String, Object>> getImageData(@PathVariable String filename) {
        try {
            String bucket = "qiju-furniture";
            String objectName = "products/" + filename;

            if (!minioService.objectExists(bucket, objectName)) {
                return Result.error(404, "图片不存在");
            }

            String contentType = "image/jpeg";
            if (filename.endsWith(".png")) contentType = "image/png";
            else if (filename.endsWith(".gif")) contentType = "image/gif";

            try (var is = minioService.getObject(bucket, objectName)) {
                byte[] bytes = is.readAllBytes();
                String base64 = Base64.getEncoder().encodeToString(bytes);
                String dataUri = "data:" + contentType + ";base64," + base64;

                Map<String, Object> result = new HashMap<>();
                result.put("dataUri", dataUri);
                result.put("size", bytes.length);
                result.put("filename", filename);
                return Result.ok(result);
            }
        } catch (Exception e) {
            log.error("Failed to get image data: {}", filename, e);
            return Result.error(500, "获取图片失败");
        }
    }
}
