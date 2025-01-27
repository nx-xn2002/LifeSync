import React, {createContext, ReactNode, useEffect, useState} from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';

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
        email: '',
    });

    // 从 SecureStore 获取用户信息
    useEffect(() => {
        const fetchUser = async () => {
            const storedUser = await AsyncStorage.getItem('user');
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
                username: newUser.username || user.username,
                email: newUser.email || user.email,
            };
            await AsyncStorage.setItem('user', JSON.stringify(updatedUser));
            setUser(updatedUser);
        } catch (error) {
            console.error('Error storing user:', error);
        }
    };

    // 清除用户信息
    const clearUser = async () => {
        await AsyncStorage.removeItem('user');
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
