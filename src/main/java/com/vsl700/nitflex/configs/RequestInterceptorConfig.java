package com.vsl700.nitflex.configs;

import com.vsl700.nitflex.components.BanCheckRequestInterceptor;
import com.vsl700.nitflex.repo.UserRepository;
import com.vsl700.nitflex.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class RequestInterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private AuthenticationService authService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new BanCheckRequestInterceptor(userRepo, authService));
    }
}
