package com.nx.lifesyncbackend.model.dto;

import lombok.Data;

/**
 * 用户登录请求
 *
 * @author nx-xn2002
 * @date 2025-01-20
 */
@Data
public class UserLoginRequest {
    /**
     * 用户名
     */
    private String username;
    /**
     * 用户密码
     */
    private String password;
}