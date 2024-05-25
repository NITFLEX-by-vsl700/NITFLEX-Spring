package com.vsl700.nitflex.controllers;

import com.vsl700.nitflex.configs.UserAuthenticationProvider;
import com.vsl700.nitflex.models.dto.LoginDTO;
import com.vsl700.nitflex.models.dto.RegisterDTO;
import com.vsl700.nitflex.models.dto.UserDTO;
import com.vsl700.nitflex.repo.UserRepository;
import com.vsl700.nitflex.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class AccountController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserAuthenticationProvider userAuthenticationProvider;

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody LoginDTO loginDTO) {
        UserDTO userDTO = userService.login(loginDTO);
        userDTO.setToken(userAuthenticationProvider.createToken(userDTO.getUsername()));
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/welcome")
    public ResponseEntity<UserDTO> initialRegister(@RequestBody RegisterDTO registerDTO) {
        if(userRepo.count() > 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .build(); // TODO Create a custom exception
        }

        UserDTO createdUser = userService.register(registerDTO);
        createdUser.setToken(userAuthenticationProvider.createToken(registerDTO.getUsername()));
        return ResponseEntity.created(URI.create("/users/" + createdUser.getId())).body(createdUser);
    }
}