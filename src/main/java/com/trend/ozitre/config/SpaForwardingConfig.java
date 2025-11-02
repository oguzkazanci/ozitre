package com.trend.ozitre.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpaForwardingConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/{path:^(?!api|actuator|static).*$}")
                .setViewName("forward:/index.html");
        registry.addViewController("/**/{path:^(?!api|actuator|static).*$}")
                .setViewName("forward:/index.html");
    }
}
