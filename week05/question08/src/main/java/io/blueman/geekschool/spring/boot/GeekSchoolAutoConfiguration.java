package io.blueman.geekschool.spring.boot;

import io.blueman.geekschool.spring.boot.core.School;
import io.blueman.geekschool.spring.boot.core.SchoolFactory;
import io.blueman.geekschool.spring.boot.prop.GeekSchoolProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(GeekSchoolProperties.class)  // 指定配置类
@ConditionalOnProperty(prefix = "spring.geekschool", name = "enable", havingValue = "true")     //只在有相关配置的时候装配该组件
@RequiredArgsConstructor
public class GeekSchoolAutoConfiguration {
    private final GeekSchoolProperties props;

    @Bean
    public School geekSchool() {
        return SchoolFactory.createSchool(props.getName());
    }
}
