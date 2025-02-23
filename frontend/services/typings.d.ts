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
    type DetectionRecord = {
        systolicBp?: number;
        diastolicBp?: number;
        heartRate?: number;
        bmi?: number;
        createTime?: Date;
        updateTime?: Date;
    }
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
    type  AnalyseRequest = {
        images?: string[];
        username?: string;
        basicHealth?: USER.BasicHealth;
    }
    type  ListRecordRequest = {
        username?: string;
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
    type BaseResponseDetectionRecord = {
        code?: number;
        data?: USER.DetectionRecord;
        message?: string;
        description?: string;
    }
    type BaseResponseDetectionRecordList = {
        code?: number;
        data?: USER.DetectionRecord[];
        message?: string;
        description?: string;
    }
}

