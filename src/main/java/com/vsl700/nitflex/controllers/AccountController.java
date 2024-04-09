package com.vsl700.nitflex.controllers;

import com.vsl700.nitflex.models.User;
import com.vsl700.nitflex.models.dto.UserDTO;
import com.vsl700.nitflex.repo.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("account/user")
    public UserDTO getUser(){
        var user = userRepo.findAll().stream().findFirst().orElse(null);
        return modelMapper.map(user, UserDTO.class);
    }

    @GetMapping("account/users")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<UserDTO> getAllUsers(){
        return userRepo.findAll().stream().map(user -> modelMapper.map(user, UserDTO.class)).toList();
    }
}