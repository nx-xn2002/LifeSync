import apiClient from "../utils/axios";

/** 登录接口 POST /user/login */
export async function login(
    body: API.UserLoginRequest,
    options?: { [key: string]: any }
): Promise<API.BaseResponseUser> {
    const response = await apiClient.post('/user/login', body, {
        headers: {
            'Content-Type': 'application/json',
        },
        ...(options || {}),
    });
    return response.data;
}

/** 获取基本健康信息接口 POST /basic_health/select */
export async function selectBasicHealth(
    body: API.UserLoginRequest,
    options?: { [key: string]: any }
): Promise<API.BaseResponseUser> {
    const response = await apiClient.post('/user/login', body, {
        headers: {
            'Content-Type': 'application/json',
        },
        ...(options || {}),
    });

    return response.data;
}

