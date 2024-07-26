package com.vsl700.nitflex.components;

import com.vsl700.nitflex.exceptions.InternalServerErrorException;
import com.vsl700.nitflex.models.User;
import com.vsl700.nitflex.repo.UserRepository;
import com.vsl700.nitflex.services.AuthenticationService;
import com.vsl700.nitflex.services.DeviceSessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@AllArgsConstructor
public class DeviceSessionCheckRequestInterceptor implements HandlerInterceptor {
    private UserRepository userRepository;
    private AuthenticationService authenticationService;
    private DeviceSessionService deviceSessionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        if(deviceSessionService.getCurrentDeviceSession() == null)
            return true;

        if(!deviceSessionService.validateCurrentDeviceSession()){
            User user = userRepository.findByUsername(authenticationService.getCurrentUserName())
                    .orElseThrow(() -> new InternalServerErrorException("Current user not found!"));
            user.setStatus(User.UserStatus.BANNED);
            userRepository.save(user);

            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        return true;
    }
}
