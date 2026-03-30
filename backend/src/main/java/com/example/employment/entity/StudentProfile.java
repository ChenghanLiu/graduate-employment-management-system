package com.example.employment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.employment.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("student_profile")
public class StudentProfile extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String studentNo;

    private Integer gender;

    private String collegeName;

    private String majorName;

    private String className;

    private String educationLevel;

    private Integer graduationYear;

    private String nativePlace;

    /**
     * 当前就业状态快照，真实就业登记业务记录以 employment_record 为准。
     */
    private Integer employmentStatus;
}
