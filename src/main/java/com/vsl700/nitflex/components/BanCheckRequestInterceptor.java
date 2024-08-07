package com.vsl700.nitflex.components;

import com.vsl700.nitflex.models.User;
import com.vsl700.nitflex.repo.UserRepository;
import com.vsl700.nitflex.services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

@Component
@AllArgsConstructor
public class BanCheckRequestInterceptor implements HandlerInterceptor {
    private static final String[] ALWAYS_PERMITTED_PATHS = { "/logout" };

    private UserRepository userRepo;
    private AuthenticationService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        if(Arrays.stream(ALWAYS_PERMITTED_PATHS).anyMatch(s -> s.equals(request.getRequestURI())))
            return true;

        if(authService.getCurrentUserName() != null && userRepo.findByUsername(authService.getCurrentUserName()).orElseThrow().getStatus().equals(User.UserStatus.BANNED)){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        return true;
    }
}
