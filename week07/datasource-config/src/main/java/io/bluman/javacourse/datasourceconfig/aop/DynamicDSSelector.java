package io.bluman.javacourse.datasourceconfig.aop;

import io.bluman.javacourse.datasourceconfig.config.DynamicDataSource;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(0)   // 需要在@Transaction之前执行
public class DynamicDSSelector {
    @Autowired
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
