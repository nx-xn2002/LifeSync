package com.nx.lifesyncbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nx.lifesyncbackend.common.ErrorCode;
import com.nx.lifesyncbackend.common.utils.CheckUtils;
import com.nx.lifesyncbackend.common.utils.PasswordUtils;
import com.nx.lifesyncbackend.constant.UserConstant;
import com.nx.lifesyncbackend.model.entity.User;
import com.nx.lifesyncbackend.exception.BusinessException;
import com.nx.lifesyncbackend.mapper.UserMapper;
import com.nx.lifesyncbackend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import static com.nx.lifesyncbackend.constant.UserConstant.USER_LOGIN_STATE;

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
    public User login(String username, String password, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(username, password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (username.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        if (username.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 2. 加密
        String encryptPassword = PasswordUtils.encode(password);
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        queryWrapper.eq("password", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        log.info("user login success username[{}] userId[{}]", user.getUsername(), user.getId());
        return user;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
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




