import apiClient from "../utils/axios";

/** 登录接口 POST /user/login */
export async function login(body: API.UserLoginRequest): Promise<API.BaseResponseUser> {
    const response = await apiClient.post('/user/login', body);
    return response.data;
}

/** 注册接口 POST /user/register */
export async function register(body: API.UserRegisterRequest): Promise<API.BaseResponseBoolean> {
    const response = await apiClient.post('/user/register', body);
    return response.data;
}

/** 获取基本健康信息接口 GET /basic_health/select */
export async function selectBasicHealth(): Promise<API.BaseResponseBasicHealth> {
    const response = await apiClient.get('/basic_health/select');
    return response.data;
}
