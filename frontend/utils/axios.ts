import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';

export const apiClient: Axios.AxiosInstance = axios.create({
    baseURL: 'http://localhost:8888/api',
    timeout: 10000, // 请求超时设置（可根据需要调整）
    headers: {
        'Content-Type': 'application/json',
    },
});

// 请求拦截器
apiClient.interceptors.request.use(
    (config) => {
        const storeToken = async () => {
            const token = await AsyncStorage.getItem('token'); // 使用 await 等待异步结果
            if (token) {
                console.log('token', token);
                config.headers['token'] = token;
            }
        };
        storeToken().then(() => {
        });
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// 响应拦截器
apiClient.interceptors.response.use(
    (response) => {
        const token = response.headers['token'];
        console.log('token', token)
        if (token) {
            AsyncStorage.setItem('token', token).then(() => {
                console.log('token store success', token)
                return response
            });
        }
        return response;
    },
    (error) => {
        return Promise.reject(error);
    }
);

export default apiClient;
