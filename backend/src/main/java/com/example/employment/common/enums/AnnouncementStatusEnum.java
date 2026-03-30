package com.example.employment.common.enums;

import java.util.Arrays;

public enum AnnouncementStatusEnum {

    OFFLINE(0, "下线"),
    PUBLISHED(1, "已发布");

    private final int code;

    private final String description;

    AnnouncementStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static AnnouncementStatusEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(item -> item.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("未知公告状态: " + code));
    }
}
