package com.qiju.furniture.module.auth.dto;

import com.qiju.furniture.module.user.dto.UserVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Authentication Response - returned on login/register
 *
 * @author Qiju Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * JWT token
     */
    private String token;

    /**
     * User information (no password)
     */
    private UserVO user;
}
