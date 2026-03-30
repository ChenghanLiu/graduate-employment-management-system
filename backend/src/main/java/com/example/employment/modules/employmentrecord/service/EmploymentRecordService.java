package com.example.employment.modules.employmentrecord.service;

import com.example.employment.modules.employmentrecord.dto.EmploymentRecordCreateRequest;
import com.example.employment.modules.employmentrecord.dto.EmploymentRecordReviewRequest;
import com.example.employment.modules.employmentrecord.dto.EmploymentRecordUpdateRequest;
import com.example.employment.modules.employmentrecord.vo.EmploymentRecordDetailVO;
import com.example.employment.modules.employmentrecord.vo.EmploymentRecordReviewVO;
import java.util.List;

public interface EmploymentRecordService {

    EmploymentRecordDetailVO getCurrentRecord(Long studentUserId);

    EmploymentRecordDetailVO createCurrentRecord(Long studentUserId, EmploymentRecordCreateRequest request);

    EmploymentRecordDetailVO updateCurrentRecord(Long studentUserId, EmploymentRecordUpdateRequest request);

    List<EmploymentRecordReviewVO> listReviewRecords(Long reviewerUserId);

    EmploymentRecordDetailVO getRecordDetail(Long reviewerUserId, Long recordId);

    EmploymentRecordDetailVO reviewRecord(Long reviewerUserId, Long recordId, EmploymentRecordReviewRequest request);
}
