package com.vsl700.nitflex.services.implementations;

import com.vsl700.nitflex.models.Role;
import com.vsl700.nitflex.models.User;
import com.vsl700.nitflex.models.dto.LoginDTO;
import com.vsl700.nitflex.models.dto.RegisterDTO;
import com.vsl700.nitflex.models.dto.UserDTO;
import com.vsl700.nitflex.models.dto.UserStatusDTO;
import com.vsl700.nitflex.repo.RoleRepository;
import com.vsl700.nitflex.repo.UserRepository;
import com.vsl700.nitflex.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserDTO login(LoginDTO loginDTO) {
        User user = userRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(() -> new RuntimeException("Unknown user")); // TODO Create custom exception

        if (passwordEncoder.matches(CharBuffer.wrap(loginDTO.getPassword()), user.getPassword())) {
            return modelMapper.map(user, UserDTO.class);
        }
        throw new RuntimeException("Invalid password"); // TODO Create custom exception
    }

    @Override
    public UserDTO register(RegisterDTO registerDTO) {
        Optional<User> optionalUser = userRepository.findByUsername(registerDTO.getUsername());

        if (optionalUser.isPresent()) {
            throw new RuntimeException("Login already exists"); // TODO Create custom exception
        }

        User user = new User(registerDTO.getUsername(), passwordEncoder.encode(registerDTO.getPassword()), registerDTO.getDeviceLimit());
        Role role = roleRepo.findByName(registerDTO.getRole()).orElseThrow();
        user.setRole(role);

        User savedUser = userRepository.save(user);

        return modelMapper.map(savedUser, UserDTO.class);
    }

    @Override
    public UserDTO findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Unknown user")); // TODO Create custom exception
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public UserStatusDTO getUserStatus() {
        if(userRepository.count() == 0)
            return new UserStatusDTO("no-users");

        System.out.println(SecurityContextHolder.getContext().getAuthentication());
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
//        if(!(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails))
//            return new UserStatusDTO("unauthenticated");

        return new UserStatusDTO("authenticated");
    }
}
