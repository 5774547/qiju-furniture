package com.qiju.furniture.module.upload.controller;

import com.qiju.furniture.common.result.Result;
import com.qiju.furniture.common.service.MinioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件上传 Controller
 */
@Tag(name = "文件上传")
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private final MinioService minioService;

    public UploadController(MinioService minioService) {
        this.minioService = minioService;
    }

    @Operation(summary = "上传单张图片")
    @PostMapping("/image")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        String url = minioService.uploadFile(file, "images");
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        return Result.ok(result);
    }

    @Operation(summary = "上传多张图片")
    @PostMapping("/images")
    public Result<List<String>> uploadImages(@RequestParam("files") List<MultipartFile> files) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            String url = minioService.uploadFile(file, "images");
            urls.add(url);
        }
        return Result.ok(urls);
    }

    @Operation(summary = "上传商品图片")
    @PostMapping("/product-image")
    public Result<Map<String, String>> uploadProductImage(@RequestParam("file") MultipartFile file) {
        String url = minioService.uploadFile(file, "products");
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        return Result.ok(result);
    }

    @Operation(summary = "上传头像")
    @PostMapping("/avatar")
    public Result<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        String url = minioService.uploadFile(file, "avatars");
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        return Result.ok(result);
    }
}
