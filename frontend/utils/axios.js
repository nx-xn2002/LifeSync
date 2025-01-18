import axios from 'axios';

// 创建一个 axios 实例
const apiClient = axios.create({
    baseURL: 'http://localhost:8888', // 设置你的 baseUrl
    timeout: 10000, // 请求超时设置（可根据需要调整）
    headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer YOUR_ACCESS_TOKEN', // 如果需要认证，可以在这里添加 Authorization 头
    },
    withCredentials: true, // 确保跨域请求时携带 cookie
});

// 请求拦截器（可选）
apiClient.interceptors.request.use(
    config => {
        // 在这里可以添加请求前的一些逻辑，比如请求头的修改等
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

// 响应拦截器（可选）
apiClient.interceptors.response.use(
    response => {
        // 在这里可以对响应数据进行统一处理
        return response;
    },
    error => {
        // 在这里可以进行错误处理
        return Promise.reject(error);
    }
);

export default apiClient;
