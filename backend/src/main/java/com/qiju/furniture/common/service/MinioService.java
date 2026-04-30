package com.qiju.furniture.common.service;

import com.qiju.furniture.common.config.MinioConfig;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;

/**
 * MinIO 文件存储服务
 */
@Service
public class MinioService {

    private static final Logger log = LoggerFactory.getLogger(MinioService.class);

    private final MinioConfig minioConfig;
    private S3Client s3Client;

    public MinioService(MinioConfig minioConfig) {
        this.minioConfig = minioConfig;
    }

    @PostConstruct
    public void init() {
        try {
            s3Client = S3Client.builder()
                    .endpointOverride(URI.create(minioConfig.getEndpoint()))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(minioConfig.getAccessKey(), minioConfig.getSecretKey())))
                    .region(Region.US_EAST_1) // Region is irrelevant for MinIO but required by SDK
                    .forcePathStyle(true) // Required for MinIO (not virtual hosted)
                    .build();

            // Ensure bucket exists
            try {
                s3Client.headBucket(b -> b.bucket(minioConfig.getBucket()));
                log.info("MinIO 桶已存在: {}", minioConfig.getBucket());
            } catch (NoSuchBucketException e) {
                s3Client.createBucket(b -> b.bucket(minioConfig.getBucket()));
                log.info("MinIO 桶已创建: {}", minioConfig.getBucket());
            }

            log.info("MinIO 客户端初始化成功: {}", minioConfig.getEndpoint());
        } catch (Exception e) {
            log.error("MinIO 客户端初始化失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 上传文件
     *
     * @param file     上传的文件
     * @param pathPrefix 路径前缀，如 "products"、"avatars"
     * @return 文件的公开访问URL
     */
    public String uploadFile(MultipartFile file, String pathPrefix) {
        try {
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String ext = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String objectName = pathPrefix + "/" + UUID.randomUUID().toString().replace("-", "") + ext;

            // 上传到MinIO
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(minioConfig.getBucket())
                    .key(objectName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // 返回公开访问URL
            String publicUrl = minioConfig.getPublicUrl() + "/" + minioConfig.getBucket() + "/" + objectName;
            log.info("文件上传成功: {}", publicUrl);
            return publicUrl;
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传文件（通过InputStream）
     */
    public String uploadFile(InputStream inputStream, String objectName, String contentType) {
        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(minioConfig.getBucket())
                    .key(objectName)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromInputStream(inputStream, inputStream.available()));

            String publicUrl = minioConfig.getPublicUrl() + "/" + minioConfig.getBucket() + "/" + objectName;
            log.info("文件上传成功: {}", publicUrl);
            return publicUrl;
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 删除文件
     */
    public void deleteFile(String objectUrl) {
        try {
            String objectName = extractObjectName(objectUrl);
            if (objectName == null) {
                return;
            }
            s3Client.deleteObject(d -> d.bucket(minioConfig.getBucket()).key(objectName));
            log.info("文件删除成功: {}", objectName);
        } catch (Exception e) {
            log.warn("文件删除失败: {}", e.getMessage());
        }
    }

    /**
     * 生成预签名URL（临时访问）
     */
    public String generatePresignedUrl(String objectName, Duration duration) {
        try (S3Presigner presigner = S3Presigner.builder()
                .endpointOverride(URI.create(minioConfig.getEndpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(minioConfig.getAccessKey(), minioConfig.getSecretKey())))
                .region(Region.US_EAST_1)
                .build()) {

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .getObjectRequest(g -> g.bucket(minioConfig.getBucket()).key(objectName))
                    .signatureDuration(duration)
                    .build();

            return presigner.presignGetObject(presignRequest).url().toString();
        }
    }

    /**
     * 从公开URL中提取对象名
     */
    private String extractObjectName(String objectUrl) {
        String prefix = minioConfig.getPublicUrl() + "/" + minioConfig.getBucket() + "/";
        if (objectUrl.startsWith(prefix)) {
            return objectUrl.substring(prefix.length());
        }
        return null;
    }

    /**
     * 检查对象是否存在
     */
    public boolean objectExists(String bucket, String objectName) {
        try {
            s3Client.headObject(h -> h.bucket(bucket).key(objectName));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取对象输入流
     */
    public InputStream getObject(String bucket, String objectName) {
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(objectName)
                .build();
        return s3Client.getObject(getRequest);
    }
}
