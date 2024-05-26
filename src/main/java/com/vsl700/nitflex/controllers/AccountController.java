package com.vsl700.nitflex.controllers;

import com.vsl700.nitflex.models.dto.RegisterDTO;
import com.vsl700.nitflex.models.dto.UserDTO;
import com.vsl700.nitflex.repo.UserRepository;
import com.vsl700.nitflex.services.AuthenticationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AccountController {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthenticationService authService;

    /*@PostMapping("/test")
    public ResponseEntity<String> fakeLogin(@RequestBody RegisterDTO registerDTO){
        authenticate(registerDTO.getUsername(), registerDTO.getPassword());

        if(getAuthentication() == null)
            return ResponseEntity.notFound()
                    .build();

        return ResponseEntity.ok()
                .build();
    }*/

    /*@PostMapping("/auth")
    public ResponseEntity<String> login(@RequestBody LoginDTO loginDTO){
        authenticate(loginDTO.getUsername(), loginDTO.getPassword());

        if(getAuthentication() == null && !(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails))
            return ResponseEntity.notFound()
                    .build();

        return ResponseEntity.ok()
                .build();
    }*/

    @PostMapping("/welcome")
    public ResponseEntity<String> initialRegister(@RequestBody RegisterDTO registerDTO) {
        if(userRepo.count() > 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("This server already has users!"); // TODO Create a custom exception
        }

        authService.register(registerDTO);

        //authenticate(registerDTO.getUsername(), registerDTO.getPassword());

        return ResponseEntity.ok()
                .build();
    }

    @GetMapping("/users")
    public List<UserDTO> getAllUsers(){
        return userRepo.findAll().stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .toList();
    }

    @DeleteMapping("/users/{id}")
    public void deleteUserById(@PathVariable String id){
        userRepo.deleteById(id);
    }

    @PostMapping("/register")
    public void addNewUser(@RequestBody RegisterDTO registerDTO){
        authService.register(registerDTO);
    }
}