package com.nx.lifesyncbackend.controller;

import com.nx.lifesyncbackend.common.BaseResponse;
import com.nx.lifesyncbackend.common.utils.ResultUtils;
import com.nx.lifesyncbackend.model.entity.BasicHealth;
import com.nx.lifesyncbackend.model.entity.User;
import com.nx.lifesyncbackend.service.BasicHealthService;
import com.nx.lifesyncbackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

/**
 * basic health controller
 *
 * @author nx-xn2002
 * @date 2025-01-12
 */
@RestController
@RequestMapping("/basic_health")
public class BasicHealthController {
    @Resource
    private BasicHealthService basicHealthService;
    @Resource
    private UserService userService;

    @GetMapping("/select")
    public BaseResponse<BasicHealth> selectBasicHealth(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        BasicHealth basicHealth = basicHealthService.select(loginUser);
        return ResultUtils.success(basicHealth);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateBasicHealth(@RequestBody BasicHealth basicHealth, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Boolean result = basicHealthService.updateByLoginUser(basicHealth, loginUser);
        return ResultUtils.success(result);
    }
}
