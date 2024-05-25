package com.vsl700.nitflex.controllers;

import com.vsl700.nitflex.models.dto.UserStatusDTO;
import com.vsl700.nitflex.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {
    @Autowired
    private UserService userService;

    @GetMapping("/userStatus")
    public UserStatusDTO getUserStatus(){
        return userService.getUserStatus();
    }
}
