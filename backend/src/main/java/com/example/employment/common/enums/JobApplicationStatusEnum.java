package com.example.employment.common.enums;

import java.util.Arrays;

public enum JobApplicationStatusEnum {

    APPLIED(0, "已投递"),
    VIEWED(1, "已查看"),
    SHORTLISTED(2, "通过初筛"),
    HIRED(3, "已录用"),
    REJECTED(4, "不合适"),
    CANCELLED(5, "已取消");

    private final int code;

    private final String description;

    JobApplicationStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static JobApplicationStatusEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(item -> item.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("未知岗位申请状态: " + code));
    }
}
