package com.trend.ozitre.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
/*
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("oracle.jdbc.OracleDriver");
        dataSource.setUrl("jdbc:oracle:thin:@//localhost:1521/XEPDB1");
        dataSource.setUsername("TRNDDRS");
        dataSource.setPassword("trnddrs");
        return dataSource;
    }
*/
 /*   @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://tdadb.che2ugqiy5fw.us-east-2.rds.amazonaws.com:3306/tda?useSSL=false");
        dataSource.setUsername("admin");
        dataSource.setPassword("Devosoft1!");

        return dataSource;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://35.225.151.93:3306/tda?useSSL=false");
        dataSource.setUsername("root");
        dataSource.setPassword("devo");

        return dataSource;
    }
*/

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/TDA");
        dataSource.setUsername("root");
        dataSource.setPassword("devosoft1");
        return dataSource;
    }
}
