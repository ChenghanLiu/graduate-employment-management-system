package com.example.employment.common.enums;

import java.time.LocalDate;
import java.util.Arrays;

public enum JobPostStatusEnum {

    DRAFT(0, "待发布"),
    RECRUITING(1, "招聘中"),
    OFFLINE(2, "已下线"),
    ENDED(3, "已结束");

    private final int code;

    private final String description;

    JobPostStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public boolean isApplicable(LocalDate deadline) {
        return this == RECRUITING && (deadline == null || !deadline.isBefore(LocalDate.now()));
    }

    public static JobPostStatusEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(item -> item.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("未知岗位状态: " + code));
    }
}
