package com.nx.lifesyncbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nx.lifesyncbackend.model.entity.BasicHealth;
import com.nx.lifesyncbackend.model.entity.DetectionRecord;

import java.io.IOException;
import java.util.List;

/**
 * detection record service
 *
 * @author nx-xn2002
 */
public interface DetectionRecordService extends IService<DetectionRecord> {
    /**
     * analyse blood pressure
     *
     * @param imageBase64List image base64 list
     * @param basicHealth     basic health
     * @param username        username
     * @return {@link Double }
     * @throws IOException ioexception
     */
    DetectionRecord analyse(List<String> imageBase64List, BasicHealth basicHealth, String username) throws IOException;

    /**
     * list by username
     *
     * @param username username
     * @return {@link List }<{@link DetectionRecord }>
     */
    List<DetectionRecord> listByUsername(String username);
}
