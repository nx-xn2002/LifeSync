import React, {createContext, ReactNode, useEffect, useState} from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';

// 定义 BasicHealthContext 的值类型
interface BasicHealthContextType {
    basicHealth: USER.BasicHealth;
    storeBasicHealth: (newBasicHealth: USER.BasicHealth) => Promise<void>;
    clearBasicHealth: () => Promise<void>;
}

// 创建 Context
export const BasicHealthContext = createContext<BasicHealthContextType | undefined>(undefined);

interface BasicHealthProviderProps {
    children: ReactNode;
}

export const BasicHealthProvider = ({children}: BasicHealthProviderProps) => {
    const [basicHealth, setBasicHealth] = useState<USER.BasicHealth>({
        height: 0,
        weight: 0,
        age: 0,
        gender: 0,
    });

    // 从 SecureStore 获取用户信息
    useEffect(() => {
        const fetchBasicHealth = async () => {
            const storedBasicHealth = await AsyncStorage.getItem('basicHealth');
            if (storedBasicHealth) {
                setBasicHealth(JSON.parse(storedBasicHealth));  // 设置用户信息
            }
        };
        fetchBasicHealth();
    }, []);

    // 存储用户信息到 SecureStore
    const storeBasicHealth = async (newBasicHealth: USER.BasicHealth) => {
        try {
            const updatedBasicHealth = {
                height: newBasicHealth?.height ?? basicHealth.height,
                weight: newBasicHealth?.weight ?? basicHealth.weight,
                age: newBasicHealth?.age ?? basicHealth.age,
                gender: newBasicHealth?.gender ?? basicHealth.gender,
            };
            await AsyncStorage.setItem('basicHealth', JSON.stringify(updatedBasicHealth));
            setBasicHealth(updatedBasicHealth);
        } catch (error) {
            console.error('Error storing basicHealth:', error);
        }
    };

    // 清除用户信息
    const clearBasicHealth = async () => {
        await AsyncStorage.removeItem('basicHealth');
        setBasicHealth({
            height: 0,
            weight: 0,
            age: 0,
            gender: 0,
        });
    };

    return (
        <BasicHealthContext.Provider value={{basicHealth, storeBasicHealth, clearBasicHealth}}>
            {children}
        </BasicHealthContext.Provider>
    );
};
