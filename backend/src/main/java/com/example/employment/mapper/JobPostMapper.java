package com.example.employment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.employment.entity.JobPost;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface JobPostMapper extends BaseMapper<JobPost> {
}
