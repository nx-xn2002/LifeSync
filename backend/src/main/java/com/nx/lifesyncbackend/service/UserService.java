package com.nx.lifesyncbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nx.lifesyncbackend.common.BaseResponse;
import com.nx.lifesyncbackend.model.entity.User;
import jakarta.servlet.http.HttpServletRequest;

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
    Boolean register(User user);

    /**
     * login
     *
     * @param username username
     * @param password password
     * @param request  request
     * @return {@link User }
     */
    User login(String username,String password, HttpServletRequest request);

    /**
     * get login user
     *
     * @param request request
     * @return {@link User }
     */
    User getLoginUser(HttpServletRequest request);
}
