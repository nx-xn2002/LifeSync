package com.nx.lifesyncbackend.model.dto;

import lombok.Data;

/**
 * 用户登录请求
 *
 * @author nx-xn2002
 */
@Data
public class UserRegisterRequest {
    /**
     * 用户名
     */
    private String username;
    /**
     * 用户密码
     */
    private String password;
    /**
     * 重输密码
     */
    private String checkPassword;
    /**
     * 邮箱
     */
    private String email;
}