package com.qiju.furniture.module.upload.controller;

import com.qiju.furniture.common.result.Result;
import com.qiju.furniture.common.service.MinioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 图片代理 Controller
 * 同时支持：
 * 1. GET /api/images/{filename} — 字节流（普通浏览器）
 * 2. GET /api/images/data/{filename} — Base64 JSON（微信小程序）
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

    @Operation(summary = "获取产品图片（Base64 JSON，供 wx.request 使用）")
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
