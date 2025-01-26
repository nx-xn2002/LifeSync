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
    type  UserRegisterRequest = {
        username?: string;
        checkPassword?: string;
        password?: string;
        email?: string;
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

}

