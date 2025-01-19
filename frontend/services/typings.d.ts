declare namespace USER {
    type UserInfo = {
        username?: string;
        password?: string;
        email?: string;
        basicHealth?: BasicHealth;
    };
    type BaseResponseUser = {
        code?: number;
        data?: User;
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

