package com.example.employment.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.example.employment.entity.StudentProfile;
import java.time.LocalDateTime;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MybatisPlusMetaObjectHandlerIntegrationTest {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private MybatisPlusMetaObjectHandler metaObjectHandler;

    @Test
    void shouldRegisterMetaObjectHandlerInSqlSessionFactory() {
        GlobalConfig globalConfig = GlobalConfigUtils.getGlobalConfig(sqlSessionFactory.getConfiguration());
        assertThat(globalConfig.getMetaObjectHandler()).isSameAs(metaObjectHandler);
    }

    @Test
    void shouldFillCommonTimestampsForBaseEntity() {
        StudentProfile profile = new StudentProfile();

        metaObjectHandler.insertFill(SystemMetaObject.forObject(profile));

        assertThat(profile.getCreateTime()).isNotNull();
        assertThat(profile.getUpdateTime()).isNotNull();

        LocalDateTime createdAt = profile.getCreateTime();
        LocalDateTime updatedAt = profile.getUpdateTime();

        profile.setUpdateTime(null);
        metaObjectHandler.updateFill(SystemMetaObject.forObject(profile));

        assertThat(profile.getCreateTime()).isEqualTo(createdAt);
        assertThat(profile.getUpdateTime()).isNotNull();
        assertThat(profile.getUpdateTime()).isAfterOrEqualTo(updatedAt);
    }
}
