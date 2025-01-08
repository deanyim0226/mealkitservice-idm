package com.example.idmservice.config;


import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.sql.DataSource;
import java.util.Properties;



@Configuration
public class AppConfig {

    /*
    configure mysql username/password/url/drivername in application.properties
     */
    @Value("${mysql.username}")
    private String username;
    @Value("${mysql.password}")
    private String password;
    @Value("${mysql.url}")
    private String url;
    @Value("${mysql.driverclassname}")
    private String driverclassname;


    /*
    Think of Bean as Object, setting up MYSql database
     */
    @Bean
    DataSource dataSource(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverclassname);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return dataSource;
    }


    /*
    Setting up entityManagerFactory (JPA) as primary
    Java Persistence API(JPA) is a specification for managing data in Java.
    It provides a standard way to map Java objects to database tables, allowing us to work with database data as Objects.
    It also supports relationships between entities. EX) @OneToOne, @OneToMany, @ManyToOne, and @ManyToMany
     */
    @Bean
    @Primary
    LocalContainerEntityManagerFactoryBean entityManagerFactory(){

        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setJpaProperties(properties());
        entityManagerFactory.setDataSource(dataSource());
        entityManagerFactory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        entityManagerFactory.setPackagesToScan("com.example.idmservice.domain");

        return entityManagerFactory;
    }

    /*
    Setting up SessionFactory (Hibernate)
    Hibernate implements JPA, but it also offers additional features beyond JPA standard
    CRUD operations + hql (hql is similar to sql, but hql is fully object-oriented)
    Think of Hibernate as abstraction layer between application and database
    */
    @Bean
    LocalSessionFactoryBean sessionFactory(){
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setAnnotatedPackages("com.example.idmservice.domain");
        sessionFactory.setPackagesToScan("com.example.idmservice.domain");
        sessionFactory.setHibernateProperties(properties());

        return sessionFactory;
    }

    Properties properties(){
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "update");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");

        return properties;
    }

    /*
    to encode the password for the security purpose
     */

    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder(){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder;
    }



    @Bean
    ViewResolver viewResolver(){

        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/");
        viewResolver.setSuffix(".jsp");

        return viewResolver;
    }
}
