package com.vsl700.nitflex.controllers;

import com.vsl700.nitflex.exceptions.BadRequestException;
import com.vsl700.nitflex.exceptions.InternalServerErrorException;
import com.vsl700.nitflex.exceptions.NotFoundException;
import com.vsl700.nitflex.models.User;
import com.vsl700.nitflex.models.dto.DeviceSessionDTO;
import com.vsl700.nitflex.repo.DeviceSessionRepository;
import com.vsl700.nitflex.repo.UserRepository;
import com.vsl700.nitflex.services.AuthenticationService;
import com.vsl700.nitflex.services.DeviceSessionService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class DeviceSessionController {
    private UserRepository userRepo;
    private DeviceSessionRepository deviceSessionRepo;
    private DeviceSessionService deviceSessionService;
    private AuthenticationService authenticationService;
    private ModelMapper modelMapper;

    @GetMapping("/users/sessions")
    public List<DeviceSessionDTO> getDeviceSessionsForCurrentUser(){
        User user = userRepo.findByUsername(authenticationService.getCurrentUserName()).orElseThrow(() -> new InternalServerErrorException("Authorized user not found in database!"));
        return deviceSessionRepo.findAllByUser(user).stream()
                .map(d -> modelMapper.map(d, DeviceSessionDTO.class))
                .toList();
    }

    @Secured("ROLE_MANAGE_USERS_PRIVILEGE")
    @GetMapping("/users/sessions/{userId}")
    public List<DeviceSessionDTO> getDeviceSessionsForUser(@PathVariable String userId){
        User user = userRepo.findById(userId).orElseThrow(() -> new NotFoundException("User not found!"));
        return deviceSessionRepo.findAllByUser(user).stream()
                .map(d -> modelMapper.map(d, DeviceSessionDTO.class))
                .toList();
    }

    @DeleteMapping("/users/sessions/{sessionId}")
    public void deleteDeviceSessionForCurrentUser(@PathVariable String sessionId){
        User currentUser = userRepo.findByUsername(authenticationService.getCurrentUserName()).orElseThrow(() -> new InternalServerErrorException("Authorized user not found in database!"));
        if(!deviceSessionRepo.findById(sessionId).orElseThrow(() -> new NotFoundException("Session doesn't exist!")).getUser().getId().equals(currentUser.getId()))
            throw new BadRequestException("Given session does not belong to currently authenticated user!");

        deviceSessionService.removeDeviceSession(deviceSessionRepo.findById(sessionId).orElseThrow(() -> new NotFoundException("Device session not found!")));
    }

    @Secured("ROLE_MANAGE_USERS_PRIVILEGE")
    @DeleteMapping("/users/sessions/{userId}/{sessionId}")
    public void deleteDeviceSessionForUser(@PathVariable String userId, @PathVariable String sessionId){
        User user = userRepo.findById(userId).orElseThrow(() -> new NotFoundException("User not found!"));
        if(!deviceSessionRepo.findById(sessionId).orElseThrow(() -> new NotFoundException("Session doesn't exist!")).getUser().getId().equals(user.getId()))
            throw new BadRequestException("Given session does not belong to given user!");

        deviceSessionService.removeDeviceSession(deviceSessionRepo.findById(sessionId).orElseThrow(() -> new NotFoundException("Device session not found!")));
    }
}
