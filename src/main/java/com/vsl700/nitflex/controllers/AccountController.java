package com.vsl700.nitflex.controllers;

import com.vsl700.nitflex.models.Role;
import com.vsl700.nitflex.models.User;
import com.vsl700.nitflex.models.dto.LoginDTO;
import com.vsl700.nitflex.models.dto.RegisterDTO;
import com.vsl700.nitflex.repo.RoleRepository;
import com.vsl700.nitflex.repo.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {
    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

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

        User user = new User(registerDTO.getUsername(), passwordEncoder.encode(registerDTO.getPassword()));
        Role role = roleRepo.findByName("ROLE_OWNER").orElseThrow();
        user.setRole(role);

        userRepo.save(user);

        //authenticate(registerDTO.getUsername(), registerDTO.getPassword());

        return ResponseEntity.ok()
                .build();
    }

    /*private void authenticate(String username, String password){
        Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private Authentication getAuthentication(){
        return SecurityContextHolder.getContext().getAuthentication();
    }*/
}