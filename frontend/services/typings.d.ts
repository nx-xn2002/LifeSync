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
    type BaseResponseBasicHealth = {
        code?: number;
        data?: USER.BasicHealth;
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

