package io.bluman.javacourse.datasourceconfig.aop;

import io.bluman.javacourse.datasourceconfig.config.DynamicDataSource;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Aspect
@Component
@ConditionalOnClass(value = DynamicDataSource.class)
@Order(0)   // 需要在@Transaction之前执行
public class DynamicDSSelector {
    @Resource(name = "dataSource")
    private DynamicDataSource dynamicDS;

    @Before("@annotation(io.bluman.javacourse.datasourceconfig.annotation.DataRead)")
    public void setDataRead(JoinPoint joinPoint) {
        // 解决写完读的问题
        if (!DynamicDataSource.WRITE_SOURCE.equals(dynamicDS.DS_TYPE.get())) {
            dynamicDS.DS_TYPE.set(DynamicDataSource.READ_SOURCE);
        }
    }

    @Before("@annotation(io.bluman.javacourse.datasourceconfig.annotation.DataWrite)")
    public void setDataWrite(JoinPoint joinPoint) {
        dynamicDS.DS_TYPE.set(DynamicDataSource.WRITE_SOURCE);
    }

}
