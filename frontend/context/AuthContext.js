import React, { createContext, useState, useContext } from 'react';

// 创建 AuthContext
const AuthContext = createContext();

// 创建一个自定义 hook 用于访问和修改登录信息
export const useAuth = () => {
    return useContext(AuthContext);
};

// 创建 AuthProvider 组件来包装应用，提供全局状态
export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null); // 初始为未登录状态

    const login = (username, password) => {
        setUser({ username, password });
    };

    const logout = () => {
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};
