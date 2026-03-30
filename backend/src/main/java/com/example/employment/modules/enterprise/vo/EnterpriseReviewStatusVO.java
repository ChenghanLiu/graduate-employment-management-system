package com.example.employment.modules.enterprise.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EnterpriseReviewStatusVO {

    private final Integer reviewStatus;

    private final String reviewStatusName;

    private final String reviewRemark;
}
