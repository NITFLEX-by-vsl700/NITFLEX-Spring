package com.vsl700.nitflex.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
//        registry.addResourceHandler("/static/**")
//                .addResourceLocations("/build/static/");
        registry.addResourceHandler("/*.css")
                .addResourceLocations("/static/build/static/css/");
        registry.addResourceHandler("/*.js")
                .addResourceLocations("/static/build/static/js/");
        registry.addResourceHandler("/*.json")
                .addResourceLocations("/static/build/");
        registry.addResourceHandler("/*.ico")
                .addResourceLocations("/static/build/");
//        registry.addResourceHandler("/*.svg") //For SVGs
//                .addResourceLocations("/build/media");
//        registry.addResourceHandler("/*.png") // For big logo PNGs
//                .addResourceLocations("/build/static");
        registry.addResourceHandler("/index.html")
                .addResourceLocations("/static/build/index.html");
        registry.addResourceHandler("/*")
                .addResourceLocations("/static/build/index.html");
    }
}
