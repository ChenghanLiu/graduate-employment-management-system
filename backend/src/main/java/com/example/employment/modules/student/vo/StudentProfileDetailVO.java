package com.example.employment.modules.student.vo;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudentProfileDetailVO {

    private final Long id;

    private final Long userId;

    private final String studentNo;

    private final Integer gender;

    private final String collegeName;

    private final String majorName;

    private final String className;

    private final String educationLevel;

    private final Integer graduationYear;

    private final String nativePlace;

    /**
     * 当前就业状态快照展示字段，真实登记记录以 employment_record 业务为准。
     */
    private final Integer employmentStatus;

    private final String employmentStatusName;

    private final Integer status;

    private final LocalDateTime createTime;

    private final LocalDateTime updateTime;
}
