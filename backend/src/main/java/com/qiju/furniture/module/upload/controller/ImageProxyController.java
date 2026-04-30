package com.qiju.furniture.module.upload.controller;

import com.qiju.furniture.common.service.MinioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;

/**
 * 图片代理 Controller
 * 将 MinIO 的 HTTP 图片通过 Spring Boot 代理输出
 * 解决微信小程序禁止 HTTP 图片的问题
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

    @Operation(summary = "获取产品图片")
    @GetMapping("/{filename}")
    public void getImage(@PathVariable String filename, HttpServletResponse response) {
        try {
            String bucket = "qiju-furniture";
            String objectName = "products/" + filename;

            // 检查文件是否存在
            if (!minioService.objectExists(bucket, objectName)) {
                log.warn("Image not found: {}/{}", bucket, objectName);
                response.setStatus(404);
                return;
            }

            // 设置 Content-Type
            String contentType = "image/jpeg";
            if (filename.endsWith(".png")) contentType = "image/png";
            else if (filename.endsWith(".gif")) contentType = "image/gif";
            else if (filename.endsWith(".webp")) contentType = "image/webp";
            response.setContentType(contentType);

            // 代理输出图片
            try (InputStream is = minioService.getObject(bucket, objectName)) {
                is.transferTo(response.getOutputStream());
            }
        } catch (Exception e) {
            log.error("Failed to proxy image: {}", filename, e);
            response.setStatus(500);
        }
    }
}
