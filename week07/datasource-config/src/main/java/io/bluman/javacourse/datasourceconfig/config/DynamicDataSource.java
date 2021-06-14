package io.bluman.javacourse.datasourceconfig.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DynamicDataSource extends AbstractRoutingDataSource {
    public static final String READ_SOURCE = "read_source";
    public static final String WRITE_SOURCE = "write_source";
    public final ThreadLocal<String> DS_TYPE = new ThreadLocal<>();

    public DynamicDataSource(DataSource selectDataSource, DataSource updateDataSource) {
        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put(READ_SOURCE, selectDataSource);
        dataSourceMap.put(WRITE_SOURCE, updateDataSource);
        setTargetDataSources(dataSourceMap);
        setDefaultTargetDataSource(updateDataSource);   // 默认走主库
    }


    @Override
    protected Object determineCurrentLookupKey() {
        log.info("data source is:{}", DS_TYPE.get());
        return DS_TYPE.get();
    }

}
