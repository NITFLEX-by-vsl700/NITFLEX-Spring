package com.vsl700.nitflex.controllers;

import com.vsl700.nitflex.models.Privilege;
import com.vsl700.nitflex.models.Role;
import com.vsl700.nitflex.models.User;
import com.vsl700.nitflex.models.dto.LoginDTO;
import com.vsl700.nitflex.models.dto.RegisterDTO;
import com.vsl700.nitflex.models.dto.UserDTO;
import com.vsl700.nitflex.models.dto.UserSettingsDTO;
import com.vsl700.nitflex.repo.RoleRepository;
import com.vsl700.nitflex.repo.UserRepository;
import com.vsl700.nitflex.services.AuthenticationService;
import com.vsl700.nitflex.configs.UserAuthenticationProvider;
import com.vsl700.nitflex.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import java.net.URI;

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
        authService.register(registerDTO);
    }

    @GetMapping("/users/settings/{id}")
    public UserSettingsDTO getUserSettings(@PathVariable String id){
        User user = userRepo.findById(id).orElseThrow(); // TODO Use a custom exception

        return modelMapper.map(user, UserSettingsDTO.class);
    }

    @Secured("ROLE_MANAGE_USERS_PRIVILEGE")
    @PutMapping("/users/settings/{id}")
    public void updateUserSettings(@PathVariable String id, @RequestBody UserSettingsDTO userSettingsDTO){
        User user = userRepo.findById(id).orElseThrow(); // TODO Use a custom exception

        Role role = roleRepo.findByName(userSettingsDTO.getRole()).orElseThrow(); // TODO Use a custom exception
        user.setStatus(User.UserStatus.valueOf(userSettingsDTO.getStatus()));
        user.setRole(role);
        user.setDeviceLimit(userSettingsDTO.getDeviceLimit());

        userRepo.save(user);
    }
}