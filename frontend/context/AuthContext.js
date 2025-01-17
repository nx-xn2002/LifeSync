import React, { createContext, useState, useEffect } from 'react';
import * as SecureStore from 'expo-secure-store';
import { Platform } from 'react-native';

// 创建 Context
export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);

    // 从 SecureStore 获取用户信息
    useEffect(() => {
        const fetchUser = async () => {
            const storedUser = await SecureStore.getItemAsync('user');
            if (storedUser) {
                setUser(JSON.parse(storedUser));  // 设置用户信息
            }
        };
        fetchUser();
    }, []);

    // 存储用户信息到 SecureStore
    const storeUser = async (user) => {
        try {
            if (Platform.OS === 'web') {
                localStorage.setItem('user', JSON.stringify(user));  // Web环境使用localStorage
            } else {
                await SecureStore.setItemAsync('user', JSON.stringify(user));  // 非Web环境使用expo-secure-store
            }
            setUser(user);
        } catch (error) {
            console.error('Error storing user:', error);  // 捕捉错误
        }
    };

    // 清除用户信息
    const clearUser = async () => {
        if (Platform.OS === 'web') {
            localStorage.removeItem('user');  // Web环境使用localStorage
        } else {
            await SecureStore.deleteItemAsync('user');  // 非Web环境使用expo-secure-store
        }
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, storeUser, clearUser }}>
            {children}
        </AuthContext.Provider>
    );
};
