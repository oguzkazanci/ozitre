package com.trend.ozitre.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpaForwardingConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/{path:^(?!api|actuator|assets|static|favicon\\.ico|robots\\.txt|h2-console).*$}")
                .setViewName("forward:/index.html");
        registry.addViewController("/**/{path:^(?!api|actuator|assets|static).*$}")
                .setViewName("forward:/index.html");
    }
}
