package com.qiju.furniture.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO 配置属性
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {

    /** MinIO 服务端点 */
    private String endpoint = "http://localhost:9000";

    /** 访问密钥 */
    private String accessKey = "minioadmin";

    /** 秘密密钥 */
    private String secretKey = "minioadmin123";

    /** 存储桶名称 */
    private String bucket = "qiju-furniture";

    /** 公开访问URL前缀（默认与endpoint一致） */
    private String publicUrl = "http://localhost:9000";
}
