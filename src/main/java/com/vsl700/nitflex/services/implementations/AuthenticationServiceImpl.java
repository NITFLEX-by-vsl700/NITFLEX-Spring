package com.vsl700.nitflex.services.implementations;

import com.vsl700.nitflex.configs.UserAuthenticationProvider;
import com.vsl700.nitflex.models.dto.UserPrincipalDTO;
import com.vsl700.nitflex.models.dto.UserStatusDTO;
import com.vsl700.nitflex.repo.RoleRepository;
import com.vsl700.nitflex.repo.UserRepository;
import com.vsl700.nitflex.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private UserAuthenticationProvider userAuthenticationProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Authentication getAuthentication(){
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public String getCurrentUserName() {
        var authentication = getAuthentication();

        if(!(authentication.getPrincipal() instanceof UserPrincipalDTO))
            return null;

        return ((UserPrincipalDTO) authentication.getPrincipal()).getUsername();
    }

    @Override
    public UserStatusDTO getUserStatus() {
        if(userRepo.count() == 0)
            return new UserStatusDTO("no-users");

        if(!(getAuthentication().getPrincipal() instanceof UserPrincipalDTO userPrincipalDTO))
            return new UserStatusDTO("unauthenticated");

        if(userPrincipalDTO.getDeviceName() == null)
            return new UserStatusDTO("no-device-name");

        return new UserStatusDTO("authenticated");
    }
}
