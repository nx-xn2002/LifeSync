import React, {createContext, useState, useEffect, ReactNode} from 'react';
import * as SecureStore from 'expo-secure-store';
import {Platform} from 'react-native';

// 定义 AuthContext 的值类型
interface AuthContextType {
    user: USER.UserInfo;
    storeUser: (newUser: USER.UserInfo) => Promise<void>;
    clearUser: () => Promise<void>;
}

// 创建 Context
export const AuthContext = createContext<AuthContextType | undefined>(undefined);

interface AuthProviderProps {
    children: ReactNode;
}

export const AuthProvider = ({children}: AuthProviderProps) => {
    const [user, setUser] = useState<USER.UserInfo>({
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
    const storeUser = async (newUser: USER.UserInfo) => {
        try {
            const updatedUser = {
                ...user,
                ...Object.fromEntries(
                    Object.entries(newUser).map(([key, value]) =>
                        value !== undefined && value !== '' ? [key, value] : [key, user[key as keyof USER.UserInfo]]
                    )
                ),
            };

            if (Platform.OS === 'web') {
                localStorage.setItem('user', JSON.stringify(updatedUser));
            } else {
                await SecureStore.setItemAsync('user', JSON.stringify(updatedUser));
            }

            setUser(updatedUser);
        } catch (error) {
            console.error('Error storing user:', error);
        }
    };

    // 清除用户信息
    const clearUser = async () => {
        if (Platform.OS === 'web') {
            localStorage.removeItem('user');
        } else {
            await SecureStore.deleteItemAsync('user');
        }
        setUser({
            username: '',
            password: '',
            email: '',
        });
    };

    return (
        <AuthContext.Provider value={{user, storeUser, clearUser}}>
            {children}
        </AuthContext.Provider>
    );
};
