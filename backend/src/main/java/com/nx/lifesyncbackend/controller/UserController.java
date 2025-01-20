package com.nx.lifesyncbackend.controller;

import com.nx.lifesyncbackend.common.BaseResponse;
import com.nx.lifesyncbackend.common.ErrorCode;
import com.nx.lifesyncbackend.common.utils.ResultUtils;
import com.nx.lifesyncbackend.model.entity.User;
import com.nx.lifesyncbackend.exception.BusinessException;
import com.nx.lifesyncbackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

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
    public BaseResponse<Boolean> register(@RequestBody User user) {
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean result = userService.register(user);
        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<User> login(@RequestBody User user, HttpServletRequest request) {
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User result = userService.login(user, request);
        return ResultUtils.success(result);
    }

    @GetMapping("/loginUser")
    public BaseResponse<User> getLoginUser(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(loginUser);
    }
}
