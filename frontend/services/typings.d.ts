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
        gender?: number;
    };
}
declare namespace API {
    type UserLoginRequest = {
        username?: string;
        password?: string;
    };
    type  UserRegisterRequest = {
        username?: string;
        checkPassword?: string;
        password?: string;
        email?: string;
    }
    type  HeartRateRequest = {
        images?: string[]
    }

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
    type BaseResponseBoolean = {
        code?: number;
        data?: boolean;
        message?: string;
        description?: string;
    };
    type BaseResponseDouble = {
        code?: number;
        data?: number;
        message?: string;
        description?: string;
    };
}

