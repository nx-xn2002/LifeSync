package com.nx.lifesyncbackend.controller;

import com.nx.lifesyncbackend.common.BaseResponse;
import com.nx.lifesyncbackend.common.ErrorCode;
import com.nx.lifesyncbackend.common.utils.ResultUtils;
import com.nx.lifesyncbackend.domain.User;
import com.nx.lifesyncbackend.exception.BusinessException;
import com.nx.lifesyncbackend.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * user controller
 *
 * @author nx-xn2002
 * @date 2025-01-04
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<User> register(@RequestBody User user) {
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User result = userService.register(user);
        return ResultUtils.success(result);
    }
}
