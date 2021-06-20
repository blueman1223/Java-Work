/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.example.transaction.xa.raw.jdbc;

import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.apache.shardingsphere.transaction.core.TransactionTypeHolder;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Order service.
 *
 */
class XAOrderService {
    
    private final DataSource dataSource;
    
    XAOrderService(final String yamlConfigFile) throws IOException, SQLException, URISyntaxException {
        dataSource = YamlShardingSphereDataSourceFactory.createDataSource(getFile(yamlConfigFile));
    }
    
    private File getFile(final String fileName) throws URISyntaxException {
        return new File(XAOrderService.class.getResource(fileName).toURI().getPath());
    }
    
    /**
     * Init.
     */
    void init() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute("DROP TABLE IF EXISTS t_order");
            statement.execute("create table `t_order` (\n" +
                    "`id` char(50) not null comment '订单ID',\n" +
                    "`create_by` char(50) not null comment '订单创建人',\n" +
                    "`goods_id` char(50) comment '商品id',\n" +
                    "`goods_num` int comment '商品数量',\n" +
                    "`create_time` bigint comment '创建时间',\n" +
                    "`pay_time` bigint comment '付款时间',\n" +
                    "`delivery_time` bigint comment '发货时间',\n" +
                    "`finish_time` bigint comment '订单结束时间',\n" +
                    "primary key (`id`)\n" +
                    ")engine=InnoDB  default charset=utf8mb4; \n");
            // for PostgreSQL
//            statement.execute("CREATE TABLE IF NOT EXISTS t_order (order_id BIGINT PRIMARY KEY NOT NULL, user_id INT NOT NULL, status VARCHAR(50))");
        }
    }
    
    /**
     * Clean up.
     */
    void cleanup() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute("DROP TABLE IF EXISTS t_order");
        }
    }
    
    /**
     * Execute XA.
     *
     * @throws SQLException SQL exception
     */
    void insert(){
        TransactionTypeHolder.set(TransactionType.XA);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement("insert into t_order(`create_by`, `goods_id`, `goods_num`, `create_time`) values (?,?,?,?);");
            doInsert(new String[]{"204043318363111424", "204043318363111425"}, preparedStatement);
            connection.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            TransactionTypeHolder.clear();
        }
    }
    
    /**
     * Execute XA with exception.
     *
     * @throws SQLException SQL exception
     */
    void insertFailed() throws SQLException {
        TransactionTypeHolder.set(TransactionType.XA);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement("insert into t_order(`create_by`, `goods_id`, `goods_num`, `create_time`) values (?,?,?,?);");
            doInsert(new String[]{"204043318363111424", "204043318363111425"}, preparedStatement);
            connection.rollback();
        } finally {
            TransactionTypeHolder.clear();
        }
    }
    
    private void doInsert(final String[] userArr, final PreparedStatement preparedStatement) throws SQLException {
        for (int i = 0; i < 1000; i++) {
            preparedStatement.setLong(1, Long.parseLong(userArr[i%2]));
            preparedStatement.setString(2, "g12345");
            preparedStatement.setInt(3, i % 10);
            preparedStatement.setLong(4, System.currentTimeMillis());
            preparedStatement.executeUpdate();
        }
    }
    
    /**
     * Select all.
     *
     * @return record count
     */
    int selectCount() throws SQLException {
        int result = 0;
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            statement.executeQuery("SELECT COUNT(1) AS count FROM t_order");
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                result = resultSet.getInt(1);
            }
        }
        return result;
    }
}
