package com.nx.lifesyncbackend.common.utils;

/**
 * 校验工具类
 *
 * @author Ni Xiang
 * @date 2025-01-06
 */
public class CheckUtils {
    /**
     * check username
     * <p>
     * 1. 4 <= length <= 16
     * 2. only contains number, letter and underline(_)
     *
     * @param username username
     * @return boolean
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.length() < 4 || username.length() > 16) {
            return false;
        }
        for (int i = 0; i < username.length(); i++) {
            char c = username.charAt(i);
            if (c >= '0' && c <= '9') {
                continue;
            }
            if (c >= 'a' && c <= 'z') {
                continue;
            }
            if (c >= 'A' && c <= 'Z') {
                continue;
            }
            if (c == '_') {
                continue;
            }
            return false;
        }
        return true;
    }
}
