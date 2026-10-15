package org.example.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Contexte web (enfant du contexte racine) : Spring MVC.
 * Voit les beans du Root (services...), mais pas l'inverse.
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {
        "org.example.controller",
        "org.example.exception",
        "org.example.security"
})
public class WebConfig implements WebMvcConfigurer {

    // Validateur Jakarta Validation (Hibernate Validator) utilisé par @Valid
    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    @Override
    public Validator getValidator() {
        return validator();
    }

    // API JSON uniquement : Jackson avec dates ISO-8601 (createdAt, updatedAt)
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        JsonMapper mapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
        converters.add(new MappingJackson2HttpMessageConverter(mapper));
    }

    // TODO : à activer quand CurrentUserArgumentResolver existera (routes /me)
    // @Override
    // public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    //     resolvers.add(new CurrentUserArgumentResolver());
    // }
}