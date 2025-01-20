import apiClient from "../utils/axios";

/** 登录接口 POST /user/login */
export async function login(
    body: USER.UserLoginRequest,
    options?: { [key: string]: any }
): Promise<USER.BaseResponseUser> {
    const response = await apiClient.post('/user/login', body, {
        headers: {
            'Content-Type': 'application/json',
        },
        ...(options || {}),
    });

    return response.data;  // 假设返回的数据符合 UserInfo 类型
}

