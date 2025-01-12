package com.nx.lifesyncbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nx.lifesyncbackend.common.ErrorCode;
import com.nx.lifesyncbackend.common.utils.CheckUtils;
import com.nx.lifesyncbackend.common.utils.PasswordUtils;
import com.nx.lifesyncbackend.constant.UserConstant;
import com.nx.lifesyncbackend.domain.User;
import com.nx.lifesyncbackend.exception.BusinessException;
import com.nx.lifesyncbackend.mapper.UserMapper;
import com.nx.lifesyncbackend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * user service impl
 *
 * @author 18702
 * @date 2025-01-04
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Override
    public Boolean register(User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        String email = user.getEmail();
        if (StringUtils.isAnyBlank(username, password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Username or password can't be null");
        }
        if (!CheckUtils.isValidUsername(username)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Invalid username!");
        }
        //username shouldn't be the same
        synchronized (username.intern()) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", username);
            Long l = this.baseMapper.selectCount(queryWrapper);
            if (l > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "Same username existed");
            }
            User newUser = new User();
            newUser.setUsername(username);
            //加密密码
            newUser.setPassword(PasswordUtils.encode(password));
            newUser.setEmail(email);
            if (!this.save(newUser)) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Register failed, database error");
            }
            log.info("Attempting to register user: {}", username);
            return true;
        }
    }

    @Override
    public User login(User user, HttpServletRequest request) {
        if (user.getUsername() == null || user.getPassword() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Username or password can't be null");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        queryWrapper.eq("password", PasswordUtils.encode(user.getPassword()));
        User loginUser = this.baseMapper.selectOne(queryWrapper);
        if (loginUser == null) {
            log.info("Login failed with username[{}]", user.getUsername());
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "User is not existed or wrong password");
        }
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, loginUser);
        return user;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        return currentUser;
    }
}




