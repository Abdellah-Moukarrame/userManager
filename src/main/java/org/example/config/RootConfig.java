package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Contexte racine (parent) : couche métier et données.
 * Ne scanne volontairement AUCUN composant web (controller, exception, security).
 */
@Configuration
@Import(PersistenceConfig.class)
@ComponentScan(basePackages = {
        "org.example.service",
        "org.example.repository",
        "org.example.mapper"
})
public class RootConfig {

    // Permet d'utiliser @Value("${...}") ; static obligatoire
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    // Hachage des mots de passe (BCrypt imposé par le cahier des charges)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}