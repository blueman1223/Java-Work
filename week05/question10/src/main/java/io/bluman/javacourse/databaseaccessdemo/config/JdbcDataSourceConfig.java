package io.bluman.javacourse.databaseaccessdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
public class JdbcDataSourceConfig {
    @Bean(name = "simpleConnection", destroyMethod = "close")
    public Connection simpleConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        return DriverManager.getConnection("jdbc:h2:mem:");
    }
}
