package com.nx.lifesyncbackend.common.utils;

import cn.hutool.core.convert.NumberWithFormat;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.nx.lifesyncbackend.model.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * 加密解密工具类
 *
 * @author Ni Xiang
 */
@Component
public class SecurityUtils {
    @Value("${common.salt}")
    private String salt;
    @Value("${common.expire}")
    private long expire;

    /**
     * 加密
     *
     * @param strToEncode str to encode
     * @return {@link String }
     * @author Ni Xiang
     */
    public String encode(String strToEncode) {
        return SecureUtil.md5(strToEncode + salt);
    }

    /**
     * get user jwt
     *
     * @param user user
     * @return {@link String }
     */
    public String getUserJwt(User user) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("expire_time", (Long) (System.currentTimeMillis() + expire));
        return JWTUtil.createToken(map, salt.getBytes());
    }

    /**
     * valid token
     *
     * @param token token
     * @return {@link User }
     */
    public User validToken(String token) {
        if (!JWTUtil.verify(token, salt.getBytes())) {
            return null;
        }
        JWT jwt = JWTUtil.parseToken(token);
        // 获取 "expire_time" 时，首先确认它是正确的类型
        Object timeObj = jwt.getPayload("expire_time");
        // 如果是 NumberWithFormat 类型，调用 longValue() 获取其值
        long time = 0;
        if (timeObj instanceof NumberWithFormat) {
            time = ((NumberWithFormat) timeObj).longValue();
        } else if (timeObj instanceof Long) {
            time = (Long) timeObj;
        }
        if (time < System.currentTimeMillis()) {
            return null;
        }
        JSONObject userPayload = (JSONObject) jwt.getPayload("user");
        if (userPayload == null) {
            return null;
        }
        return JSONUtil.toBean(userPayload, User.class);
    }
}
