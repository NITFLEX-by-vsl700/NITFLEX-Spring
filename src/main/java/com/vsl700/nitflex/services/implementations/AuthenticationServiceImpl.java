package com.vsl700.nitflex.services.implementations;

import com.vsl700.nitflex.models.Role;
import com.vsl700.nitflex.models.User;
import com.vsl700.nitflex.models.dto.RegisterDTO;
import com.vsl700.nitflex.models.dto.UserStatusDTO;
import com.vsl700.nitflex.repo.RoleRepository;
import com.vsl700.nitflex.repo.UserRepository;
import com.vsl700.nitflex.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void register(RegisterDTO registerDTO) {
        User user = new User(registerDTO.getUsername(), passwordEncoder.encode(registerDTO.getPassword()), registerDTO.getDeviceLimit());
        Role role = roleRepo.findByName(registerDTO.getRole()).orElseThrow();
        user.setRole(role);

        userRepo.save(user);
    }

    @Override
    public UserStatusDTO getUserStatus() {
        if(userRepo.count() == 0)
            return new UserStatusDTO("no-users");

        if(!(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails))
            return new UserStatusDTO("unauthenticated");

        return new UserStatusDTO("authenticated");
    }
}