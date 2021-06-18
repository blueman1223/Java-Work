## 环境配置

### Mysql服务
* 在windows powershell中启动mysql服务
> .\bin\mysqld.exe --defaults-file=mysql3316.ini
* 配置文件如下
```
[mysqld]
# set basedir to your installation path
basedir=D:\workspace\database\mysql3316
# set datadir to the location of your data directory
datadir=D:\workspace\database\mysql3316\data
port=3316
server_id = 1

sql_mode=NO_ENGINE_SUBSTITUTION,STRICT_TRANS_TABLES 
log_bin=mysql-bin
binlog-format=Row

```
### Shardingsphere-proxy服务
* 在powershell中启动mysql服务
> .\bin\start.bat
* 配置文件 config-sharding.yaml
```

schemaName: sharding_db

dataSourceCommon:
 username: root
 password: 123456
 connectionTimeoutMilliseconds: 30000
 idleTimeoutMilliseconds: 60000
 maxLifetimeMilliseconds: 1800000
 maxPoolSize: 50
 minPoolSize: 1
 maintenanceIntervalMilliseconds: 30000

dataSources:
 ds_0:
   url: jdbc:mysql://127.0.0.1:3316/ds_0?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true
 ds_1:
   url: jdbc:mysql://127.0.0.1:3316/ds_1?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true

rules:
- !SHARDING
 tables:
   t_order:
     actualDataNodes: ds_${0..1}.t_order_${0..15}
     tableStrategy:
       standard:
         shardingColumn: id
         shardingAlgorithmName: t_order_inline
     keyGenerateStrategy:
       column: id
       keyGeneratorName: snowflake
 bindingTables:
   - t_order
 defaultDatabaseStrategy:
   standard:
     shardingColumn: create_by
     shardingAlgorithmName: database_inline
 defaultTableStrategy:
   none:
 
 shardingAlgorithms:
   database_inline:
     type: INLINE
     props:
       algorithm-expression: ds_${create_by % 2}
   t_order_inline:
     type: INLINE
     props:
       algorithm-expression: t_order_${id % 16}
 
 keyGenerators:
   snowflake:
     type: SNOWFLAKE
     props:
       worker-id: 123
```
* 配置文件 server.yaml
```
authentication:
 users:
   root:
     password: root
   sharding:
     password: sharding 
     authorizedSchemas: sharding_db

props:
 sql-show: true

```
### 测试
#### 插入
* 见 database-test/src/main/java/InsertTest.java

#### 查询
> select * from t_order limit 10;
* 结果：
```
+--------------------+--------------------+----------+-----------+---------------+----------+---------------+-------------+
| id                 | create_by          | goods_id | goods_num | create_time   | pay_time | delivery_time | finish_time |
+--------------------+--------------------+----------+-----------+---------------+----------+---------------+-------------+
| 612797031815557120 | 204043318363111424 | g12345   |         8 | 1624031795440 |     NULL |          NULL |        NULL |
| 612797031857500160 | 204043318363111424 | g12345   |         0 | 1624031795440 |     NULL |          NULL |        NULL |
| 612797031870083072 | 204043318363111424 | g12345   |         2 | 1624031795440 |     NULL |          NULL |        NULL |
| 612797031899443200 | 204043318363111424 | g12345   |         4 | 1624031795440 |     NULL |          NULL |        NULL |
| 612797031912026112 | 204043318363111424 | g12345   |         6 | 1624031795440 |     NULL |          NULL |        NULL |
| 612797031924609024 | 204043318363111424 | g12345   |         8 | 1624031795440 |     NULL |          NULL |        NULL |
| 612797031937191936 | 204043318363111424 | g12345   |         0 | 1624031795441 |     NULL |          NULL |        NULL |
| 612797031970746368 | 204043318363111424 | g12345   |         2 | 1624031795441 |     NULL |          NULL |        NULL |
| 612797031987523584 | 204043318363111424 | g12345   |         4 | 1624031795441 |     NULL |          NULL |        NULL |
| 612797032000106496 | 204043318363111424 | g12345   |         6 | 1624031795441 |     NULL |          NULL |        NULL |
+--------------------+--------------------+----------+-----------+---------------+----------+---------------+-------------+
```
* 后台日志：
```
 ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_0 limit 10
 ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_1 limit 10
 ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_2 limit 10
 ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_3 limit 10
 ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_4 limit 10
 ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_5 limit 10
 ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_6 limit 10
 ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_7 limit 10
 ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_8 limit 10
 ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_9 limit 10
 ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_10 limit 10
 ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_11 limit 10
 ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_12 limit 10
 ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_13 limit 10
 ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_14 limit 10
 ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_15 limit 10
 ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_0 limit 10
 ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_1 limit 10
 ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_2 limit 10
 ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_3 limit 10
 ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_4 limit 10
 ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_5 limit 10
 ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_6 limit 10
 ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_7 limit 10
 ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_8 limit 10
 ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_9 limit 10
 ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_10 limit 10
 ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_11 limit 10
 ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_12 limit 10
 ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_13 limit 10
 ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_14 limit 10
 ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_15 limit 10
```

#### 修改：
> update t_order set goods_num=10 where id = 612797031870083072  and create_by = 204043318363111424 ;
* 后台日志：
```
 ShardingSphere-SQL - Actual SQL: ds_0 ::: update t_order_0 set goods_num=10 where id = 612797031870083072  and create_by = 204043318363111424
```

#### 删除：
> delete from t_order where id = '612797031815557120';
* 后台日志：
```
Actual SQL: ds_0 ::: delete from t_order_0 where id = 612797031815557120
Actual SQL: ds_1 ::: delete from t_order_0 where id = 612797031815557120
Logic SQL: update t_order set goods_num=10 where id = 612797031870083072  and create_by = 204043318363111424
````