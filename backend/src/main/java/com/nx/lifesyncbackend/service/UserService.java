package com.nx.lifesyncbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nx.lifesyncbackend.common.BaseResponse;
import com.nx.lifesyncbackend.domain.User;
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
    User register(User user);

    /**
     * login
     *
     * @param user    user
     * @param request request
     * @return {@link User }
     */
    User login(User user, HttpServletRequest request);
}
