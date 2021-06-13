package io.bluman.javacourse.datasourceconfig.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@Primary    // 自动注入时选择该数据源
public class DynamicDataSource extends AbstractRoutingDataSource {
    public static final String READ_SOURCE = "read_source";
    public static final String WRITE_SOURCE = "write_source";
    public final ThreadLocal<String> DS_TYPE = new ThreadLocal<>();

    @Resource(name = "selectDatasource")
    private DataSource selectDataSource;
    @Resource(name = "updateDatasource")
    private DataSource updateDataSource;

    @Override
    protected Object determineCurrentLookupKey() {
        log.info("data source is:{}", DS_TYPE.get());
        return DS_TYPE.get();
    }

    @Override
    public void afterPropertiesSet() {
        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put(READ_SOURCE, selectDataSource);
        dataSourceMap.put(WRITE_SOURCE, updateDataSource);
        setTargetDataSources(dataSourceMap);
        setDefaultTargetDataSource(updateDataSource);   // 默认走主库
        super.afterPropertiesSet();
    }
}
