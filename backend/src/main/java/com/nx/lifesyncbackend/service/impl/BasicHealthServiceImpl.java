package com.nx.lifesyncbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nx.lifesyncbackend.common.ErrorCode;
import com.nx.lifesyncbackend.domain.BasicHealth;
import com.nx.lifesyncbackend.domain.User;
import com.nx.lifesyncbackend.exception.BusinessException;
import com.nx.lifesyncbackend.service.BasicHealthService;
import com.nx.lifesyncbackend.mapper.BasicHealthMapper;
import org.springframework.stereotype.Service;

/**
 * basic health service impl
 *
 * @author 18702
 * @date 2025-01-11
 */
@Service
public class BasicHealthServiceImpl extends ServiceImpl<BasicHealthMapper, BasicHealth>
        implements BasicHealthService {

    @Override
    public BasicHealth selectBasicHealth(User loginUser) {
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "Please Login First");
        }
        QueryWrapper<BasicHealth> basicHealthQueryWrapper = new QueryWrapper<>();
        basicHealthQueryWrapper.eq("user_id", loginUser.getId());
        BasicHealth basicHealth = this.baseMapper.selectOne(basicHealthQueryWrapper);
        if (basicHealth == null) {
            BasicHealth newBasicHealth = new BasicHealth();
            newBasicHealth.setUserId(loginUser.getId());
            this.save(newBasicHealth);
            return newBasicHealth;
        }
        return basicHealth;
    }
}




