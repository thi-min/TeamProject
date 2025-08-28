package com.project.common.config;  

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    	//포토게시판
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:///C:/Image/");
        //배너 
        registry.addResourceHandler("/banner-uploads/**")
        .addResourceLocations("file:///C:/banner-uploads/");
    }
}
