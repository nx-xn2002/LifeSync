import React, {createContext, useState, useEffect} from 'react';
import * as SecureStore from 'expo-secure-store';
import {Platform} from 'react-native';

// 创建 Context
export const AuthContext = createContext();

export const AuthProvider = ({children}) => {
    const [user, setUser] = useState({
        username: '',
        password: '',
        email: '',
        basicHealth: {
            height: 0,
            weight: 0,
            age: 0,
            gender: '',
        }
    });

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
    const storeUser = async (newUser) => {
        try {
            // 合并旧的用户信息和新的用户信息，只替换非空字段
            const updatedUser = {
                ...user,  // 保留原来的字段
                ...Object.fromEntries(
                    Object.entries(newUser).map(([key, value]) =>
                        value !== undefined && value !== '' ? [key, value] : [key, user[key]]
                    )
                ),
            };

            // 保存更新后的用户信息
            if (Platform.OS === 'web') {
                localStorage.setItem('user', JSON.stringify(updatedUser));  // Web环境使用localStorage
            } else {
                await SecureStore.setItemAsync('user', JSON.stringify(updatedUser));  // 非Web环境使用expo-secure-store
            }

            setUser(updatedUser);  // 更新状态
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
        setUser({
            username: '',
            password: '',
            email: '',
            baseInfo: {}
        });  // 清空用户信息
    };

    return (
        <AuthContext.Provider value={{user, storeUser, clearUser}}>
            {children}
        </AuthContext.Provider>
    );
};
