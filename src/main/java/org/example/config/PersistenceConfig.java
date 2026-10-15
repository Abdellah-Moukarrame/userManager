package org.example.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Configuration de la persistance : DataSource H2 (Hikari), JPA/Hibernate, transactions.
 * Importée par RootConfig.
 */
@Configuration
@PropertySource("classpath:application.properties")
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "org.example.repository")
@EnableJpaAuditing
public class PersistenceConfig {

    @Bean
    public DataSource dataSource(Environment env) {
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(env.getRequiredProperty("db.url"));
        cfg.setDriverClassName(env.getRequiredProperty("db.driver"));
        cfg.setUsername(env.getRequiredProperty("db.username"));
        cfg.setPassword(env.getProperty("db.password", ""));
        cfg.setMaximumPoolSize(env.getProperty("db.pool.maxSize", Integer.class, 10));
        cfg.setMinimumIdle(env.getProperty("db.pool.minIdle", Integer.class, 2));
        return new HikariDataSource(cfg);
    }

    // Le nom du bean doit rester "entityManagerFactory" (cherché par Spring Data)
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, Environment env) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("org.example.entity");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Properties jpa = new Properties();
        jpa.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto", "update"));
        jpa.put("hibernate.show_sql", env.getProperty("hibernate.show_sql", "false"));
        jpa.put("hibernate.format_sql", env.getProperty("hibernate.format_sql", "false"));
        emf.setJpaProperties(jpa);
        return emf;
    }

    // Le nom du bean doit rester "transactionManager"
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    // Traduit les exceptions JPA en DataAccessException de Spring
    @Bean
    public static PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }
}