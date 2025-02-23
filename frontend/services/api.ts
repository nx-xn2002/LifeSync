import apiClient from "../utils/axios";
import axios from "axios";

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

/** 更新基本健康信息接口 POST /basic_health/update */
export async function updateBasicHealth(body: USER.BasicHealth): Promise<API.BaseResponseBoolean> {
    const response = await apiClient.post('/basic_health/update', body);
    return response.data;
}

/** 检测接口 POST /monitor/analyse */
export async function analyse(body: API.AnalyseRequest): Promise<API.BaseResponseDetectionRecord> {
    const response = await apiClient.post('/monitor/analyse', body);
    return response.data;
}

/** 查询接口 POST /monitor/listDetectionRecord */
export async function listDetectionRecord(body: API.ListRecordRequest): Promise<API.BaseResponseDetectionRecordList> {
    const response = await apiClient.post('/monitor/listDetectionRecord', body);
    return response.data;
}
