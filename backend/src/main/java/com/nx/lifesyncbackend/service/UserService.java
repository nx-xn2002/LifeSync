package com.nx.lifesyncbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nx.lifesyncbackend.common.BaseResponse;
import com.nx.lifesyncbackend.model.dto.UserRegisterRequest;
import com.nx.lifesyncbackend.model.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * user service
 *
 * @author 18702
 * @date 2025-01-04
 */
public interface UserService extends IService<User> {

    /**
     * register
     *
     * @param user user
     * @return {@link BaseResponse }<{@link ? }>
     */
    Boolean register(UserRegisterRequest user);

    /**
     * login
     *
     * @param username username
     * @param password password
     * @param request  request
     * @param response response
     * @return {@link User }
     */
    User login(String username, String password, HttpServletRequest request, HttpServletResponse response);

    /**
     * get login user
     *
     * @param request request
     * @return {@link User }
     */
    User getLoginUser(HttpServletRequest request);
}
