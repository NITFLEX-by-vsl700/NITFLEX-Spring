package com.vsl700.nitflex.controllers;

import com.vsl700.nitflex.configs.UserAuthenticationProvider;
import com.vsl700.nitflex.exceptions.BadRequestException;
import com.vsl700.nitflex.exceptions.InitialRegisterClosedException;
import com.vsl700.nitflex.exceptions.NotFoundException;
import com.vsl700.nitflex.models.Privilege;
import com.vsl700.nitflex.models.Role;
import com.vsl700.nitflex.models.User;
import com.vsl700.nitflex.models.dto.*;
import com.vsl700.nitflex.repo.RoleRepository;
import com.vsl700.nitflex.repo.UserRepository;
import com.vsl700.nitflex.services.AuthenticationService;
import com.vsl700.nitflex.services.DeviceSessionService;
import com.vsl700.nitflex.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AccountController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthenticationService authService;

    @Autowired
    private UserAuthenticationProvider userAuthenticationProvider;

    @Autowired
    private DeviceSessionService deviceSessionService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDTO loginDTO) {
        userService.login(loginDTO);
        return ResponseEntity.ok(userAuthenticationProvider.createToken(loginDTO.getUsername()));
    }

    @PostMapping("/login/deviceName")
    public ResponseEntity<String> loginWithDeviceName(@RequestBody LoginDeviceNameDTO loginDeviceNameDTO) {
        String currentUsername = authService.getCurrentUserName();
        String deviceName = loginDeviceNameDTO.getDeviceName();

        deviceSessionService.addNewDeviceSession(deviceName);
        return ResponseEntity.ok(userAuthenticationProvider.createToken(currentUsername, deviceName));
    }

    @GetMapping("/logout")
    public void logout(){
        var deviceSession = deviceSessionService.getCurrentDeviceSession();

        if(deviceSession != null)
            deviceSessionService.removeDeviceSession(deviceSession);
    }

    @PostMapping("/welcome")
    public ResponseEntity<String> initialRegister(@RequestBody RegisterDTO registerDTO) {
        if(userRepo.count() > 0) {
            throw new InitialRegisterClosedException();
        }

        userService.register(registerDTO);

        return ResponseEntity.ok(userAuthenticationProvider.createToken(registerDTO.getUsername()));
    }

    @Secured("ROLE_MANAGE_USERS_PRIVILEGE")
    @GetMapping("/users")
    public List<UserDTO> getAllUsers(){
        return userRepo.findAll().stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .toList();
    }

    @Secured("ROLE_MANAGE_USERS_PRIVILEGE")
    @GetMapping("/users/{id}")
    public UserDTO getUserById(@PathVariable String id){
        return modelMapper.map(userRepo.findById(id), UserDTO.class);
    }

    @GetMapping("/currentUser")
    public UserDTO getCurrentUser(){
        String username = authService.getCurrentUserName();
        return modelMapper.map(userRepo.findByUsername(username), UserDTO.class);
    }

    @GetMapping("/users/privileges")
    public List<String> getCurrentUserPrivileges(){
        return userRepo.findByUsername(authService.getCurrentUserName()).orElseThrow().getRole().getPrivileges().stream()
                .map(Privilege::getName)
                .toList();
    }

    @Secured("ROLE_DELETE_USERS_PRIVILEGE")
    @DeleteMapping("/users/{id}")
    public void deleteUserById(@PathVariable String id){
        userRepo.deleteById(id);
    }

    @Secured("ROLE_REGISTER_USERS_PRIVILEGE")
    @PostMapping("/register")
    public void addNewUser(@RequestBody RegisterDTO registerDTO){
        userService.register(registerDTO);
    }

    @GetMapping("/users/settings/{id}")
    public UserSettingsDTO getUserSettings(@PathVariable String id){
        User user = userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id '%s' not found!".formatted(id)));

        return modelMapper.map(user, UserSettingsDTO.class);
    }

    @Secured("ROLE_MANAGE_USERS_PRIVILEGE")
    @PutMapping("/users/settings/{id}")
    public void updateUserSettings(@PathVariable String id, @RequestBody UserSettingsDTO userSettingsDTO){
        User user = userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id '%s' not found!".formatted(id)));

        Role role = roleRepo.findByName(userSettingsDTO.getRole())
                .orElseThrow(() -> new BadRequestException("Role with name '%s' doesn't exist!".formatted(userSettingsDTO.getRole())));
        user.setStatus(User.UserStatus.valueOf(userSettingsDTO.getStatus()));
        user.setRole(role);
        user.setDeviceLimit(userSettingsDTO.getDeviceLimit());

        userRepo.save(user);
    }
}