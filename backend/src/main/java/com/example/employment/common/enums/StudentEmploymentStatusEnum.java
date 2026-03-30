package com.example.employment.common.enums;

import java.util.Arrays;

public enum StudentEmploymentStatusEnum {

    UNEMPLOYED(0, "未就业"),
    SEEKING(1, "求职中"),
    SIGNED_PENDING_ONBOARDING(2, "已签约待入职"),
    EMPLOYED(3, "已就业"),
    FURTHER_STUDY(4, "升学"),
    ENTREPRENEURSHIP(5, "自主创业");

    private final int code;

    private final String description;

    StudentEmploymentStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static StudentEmploymentStatusEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(item -> item.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("未知学生就业状态: " + code));
    }
}
