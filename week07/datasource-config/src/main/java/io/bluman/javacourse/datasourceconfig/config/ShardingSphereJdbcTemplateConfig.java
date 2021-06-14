package io.bluman.javacourse.datasourceconfig.config;

import lombok.RequiredArgsConstructor;
import org.apache.shardingsphere.driver.jdbc.core.datasource.ShardingSphereDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;

@Configuration
public class ShardingSphereJdbcTemplateConfig {
    @Resource(name = "shardingSphereDataSource")
    private ShardingSphereDataSource dataSource;

    @Bean(name = "jdbcTemplate")
    public JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(dataSource);
    }
}
