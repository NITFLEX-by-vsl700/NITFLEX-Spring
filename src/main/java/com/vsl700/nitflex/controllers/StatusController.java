package com.vsl700.nitflex.controllers;

import com.vsl700.nitflex.models.dto.UserStatusDTO;
import com.vsl700.nitflex.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/userStatus")
    public UserStatusDTO getUserStatus(){
        if(userRepository.count() == 0)
            return new UserStatusDTO("no-users");

        if(!(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails))
            return new UserStatusDTO("unauthenticated");

        return new UserStatusDTO("authenticated");
    }
}
