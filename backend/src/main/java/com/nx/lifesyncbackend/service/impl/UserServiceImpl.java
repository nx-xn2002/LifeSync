package com.nx.lifesyncbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nx.lifesyncbackend.domain.User;
import com.nx.lifesyncbackend.service.UserService;
import com.nx.lifesyncbackend.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
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

}




