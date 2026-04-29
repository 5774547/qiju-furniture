package com.qiju.furniture.module.user.dto;

import lombok.Data;

/**
 * User Profile Update Data Transfer Object
 *
 * @author Qiju Team
 */
@Data
public class UserUpdateDTO {

    private String nickname;
    private String email;
    private String phone;
    private String avatar;
}
