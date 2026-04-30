package com.qiju.furniture.module.inquiry.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@TableName("inquiry_list")
public class InquiryList implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private String remark;
    @Builder.Default
    private Integer status = 0;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

