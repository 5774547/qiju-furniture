package com.qiju.furniture.module.contact.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 咨询提交DTO
 */
@Data
@Schema(description = "咨询提交参数")
public class ContactSubmitDTO {

    @NotBlank(message = "姓名不能为空")
    @Schema(description = "姓名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(description = "电话")
    private String phone;

    @NotBlank(message = "留言内容不能为空")
    @Schema(description = "留言内容", requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;
}
