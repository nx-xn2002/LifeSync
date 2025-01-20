declare namespace USER {
    type UserInfo = {
        username?: string;
        password?: string;
        email?: string;
        basicHealth?: BasicHealth;
    };
    type UserLoginRequest = {
        username?: string;
        password?: string;
    };
    type BaseResponseUser = {
        code?: number;
        data?: UserInfo;
        message?: string;
    };
    type BasicHealth = {
        height?: number;
        weight?: number;
        age?: number;
        gender?: string;
    };
}

declare namespace ERROR {
    type RegisterError = {
        username?: string;
        password?: string;
    };
}

