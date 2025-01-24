declare namespace USER {
    type UserInfo = {
        username?: string;
        password?: string;
        email?: string;
        basicHealth?: BasicHealth;
    };

    type BasicHealth = {
        height?: number;
        weight?: number;
        age?: number;
        gender?: string;
    };
}
declare namespace API {
    type UserLoginRequest = {
        username?: string;
        password?: string;
    };
    type BaseResponse = {
        code?: number;
        data?: any;
        message?: string;
        description?: string;
    };
    type BaseResponseUser = {
        code?: number;
        data?: USER.UserInfo;
        message?: string;
        description?: string;
    };
}

