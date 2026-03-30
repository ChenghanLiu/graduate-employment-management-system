package com.example.employment.common;

public final class ApiCode {

    public static final int SUCCESS = 0;
    public static final int BAD_REQUEST = 4000;
    public static final int VALIDATION_ERROR = 4001;
    public static final int UNAUTHORIZED = 4010;
    public static final int FORBIDDEN = 4030;
    public static final int NOT_FOUND = 4040;
    public static final int CONFLICT = 4090;
    public static final int ILLEGAL_STATE = 4091;
    public static final int INTERNAL_ERROR = 5000;
    public static final int DATABASE_UNAVAILABLE = 5030;

    private ApiCode() {
    }
}
