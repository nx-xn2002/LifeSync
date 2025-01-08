package com.nx.lifesyncbackend.common.utils;

import cn.hutool.crypto.SecureUtil;
import org.springframework.beans.factory.annotation.Value;

/**
 * 加密解密工具类
 *
 * @author Ni Xiang
 */
public class PasswordUtils {
    @Value("${common.salt}")
    private static String salt;

    /**
     * 加密
     *
     * @param strToEncode str to encode
     * @return {@link String }
     * @author Ni Xiang
     */
    public static String encode(String strToEncode) {
        return SecureUtil.md5(strToEncode + salt);
    }

    /**
     * equals
     *
     * @param encoded encoded
     * @param str     str
     * @return boolean
     * @author Ni Xiang
     */
    public static boolean equals(String encoded, String str) {
        if (str == null || encoded == null) {
            return false;
        }
        return encoded.equals(encode(str));
    }
}
