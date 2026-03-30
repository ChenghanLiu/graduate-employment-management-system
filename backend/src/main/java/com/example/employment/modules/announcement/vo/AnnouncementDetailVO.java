package com.example.employment.modules.announcement.vo;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AnnouncementDetailVO {

    private final Long id;

    private final String title;

    private final String content;

    private final String type;

    private final Integer status;

    private final String statusName;

    private final LocalDateTime publishTime;

    private final LocalDateTime createTime;

    private final LocalDateTime updateTime;
}
