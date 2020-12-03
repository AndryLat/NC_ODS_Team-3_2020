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
@PropertySource("classpath:connectionBD.properties")
@ComponentScan("com.netcracker.odstc.logviewer")
public class WebConfig {

    @Value("${connectionBD.url}")
    private String urlBd;

    @Value("${connectionBD.login}")
    private String login;

    @Value("${connectionBD.password}")
    private String pass;

    @Bean
    public JdbcTemplate getJdbcTemplate(){

        return  new JdbcTemplate(getDataSource());

    }

    @Bean
    public DataSource getDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(urlBd);
        dataSource.setUsername(login);
        dataSource.setPassword(pass);
        dataSource.setDriverClassName(oracle.jdbc.driver.OracleDriver.class.getName());
        return dataSource;
    }
}
