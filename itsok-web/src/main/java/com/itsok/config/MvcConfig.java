package com.itsok.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
public class MvcConfig {

    @Bean
    public ItsokWebmvcConfigurer webmvcConfigurer() {
        return new ItsokWebmvcConfigurer();
    }

}
