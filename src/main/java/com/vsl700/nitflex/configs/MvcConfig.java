package com.vsl700.nitflex.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
//        registry.addResourceHandler("/static/**")
//                .addResourceLocations("/build/static/");
//        registry.addResourceHandler("/index.html/**", "/**")
//                .addResourceLocations("classpath:/static/build/index.html");
//        registry.addResourceHandler("/static/css/*")
//                .addResourceLocations("classpath:/static/build/static/css/");
//        registry.addResourceHandler("/static/js/*")
//                .addResourceLocations("classpath:/static/build/static/js/");
//        registry.addResourceHandler("/manifest.json", "/asset-manifest.json")
//                .addResourceLocations("classpath:/static/build/");
//        registry.addResourceHandler("/favicon.ico")
//                .addResourceLocations("classpath:/static/build/");
//        registry.addResourceHandler("/*.svg") //For SVGs
//                .addResourceLocations("/build");
//        registry.addResourceHandler("/logo192.png", "/logo512.png") // For big logo PNGs
//                .addResourceLocations("classpath:/static/build/");

        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/build/index.html");
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/build");
    }
}
