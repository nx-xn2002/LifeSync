package com.nx.lifesyncbackend.service;

import com.nx.lifesyncbackend.domain.BasicHealth;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nx.lifesyncbackend.domain.User;

/**
 * basic health service
 *
 * @author 18702
 * @date 2025-01-11
 */
public interface BasicHealthService extends IService<BasicHealth> {

    /**
     * select basic health
     *
     * @param loginUser login user
     * @return {@link BasicHealth }
     */
    BasicHealth select(User loginUser);

    /**
     * update by login user
     *
     * @param basicHealth basic health
     * @param loginUser   login user
     * @return {@link Boolean }
     */
    Boolean updateByLoginUser(BasicHealth basicHealth, User loginUser);
}
