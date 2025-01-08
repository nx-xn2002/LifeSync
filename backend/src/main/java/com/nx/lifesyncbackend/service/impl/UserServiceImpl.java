package com.nx.lifesyncbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nx.lifesyncbackend.common.ErrorCode;
import com.nx.lifesyncbackend.common.utils.CheckUtils;
import com.nx.lifesyncbackend.common.utils.PasswordUtils;
import com.nx.lifesyncbackend.domain.User;
import com.nx.lifesyncbackend.exception.BusinessException;
import com.nx.lifesyncbackend.mapper.UserMapper;
import com.nx.lifesyncbackend.service.UserService;
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
    public User register(User user) {
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
            return this.baseMapper.selectOne(queryWrapper);
        }
    }
}




