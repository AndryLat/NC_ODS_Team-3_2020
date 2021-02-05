package com.netcracker.odstc.logviewer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:connectionDB.properties")
@ComponentScan("com.netcracker.odstc.logviewer")
public class DataSourceConfig {

    @Value("${connectionDB.url}")
    private String urlDb;

    @Value("${connectionDB.login}")
    private String login;

    @Value("${connectionDB.password}")
    private String password;

    @Value("${connectionDB.driverClassName}")
    private String driverClassName;

    @Bean
    public JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(getDataSource());
    }

    @Bean
    public DataSource getDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(urlDb);
        dataSource.setUsername(login);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);
        return dataSource;
    }
}
