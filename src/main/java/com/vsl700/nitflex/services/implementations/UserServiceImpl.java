package com.vsl700.nitflex.services.implementations;

import com.vsl700.nitflex.configs.UserAuthenticationProvider;
import com.vsl700.nitflex.models.Role;
import com.vsl700.nitflex.models.User;
import com.vsl700.nitflex.models.dto.LoginDTO;
import com.vsl700.nitflex.models.dto.RegisterDTO;
import com.vsl700.nitflex.models.dto.UserDTO;
import com.vsl700.nitflex.models.dto.UserStatusDTO;
import com.vsl700.nitflex.repo.DeviceSessionRepository;
import com.vsl700.nitflex.repo.RoleRepository;
import com.vsl700.nitflex.repo.UserRepository;
import com.vsl700.nitflex.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    @Autowired
    private DeviceSessionRepository deviceSessionRepository;

    @Override
    public boolean login(LoginDTO loginDTO) {
        User user = userRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(() -> new RuntimeException("Unknown user")); // TODO Create custom exception (401 Unauthorized)

        if(deviceSessionRepository.findAllByUser(user).size() >= user.getDeviceLimit())
            return false;

        return passwordEncoder.matches(CharBuffer.wrap(loginDTO.getPassword()), user.getPassword());// TODO Create custom exception (401 Unauthorized)
    }

    @Override
    public void register(RegisterDTO registerDTO) {
        if(registerDTO.getUsername().isBlank() || registerDTO.getPassword().isBlank())
            throw new RuntimeException("Username/password cannot be empty!"); // TODO Add custom breakpoint

        if(userRepository.findByUsername(registerDTO.getUsername()).isPresent())
            throw new RuntimeException("User with such username already exists!"); // TODO Add custom breakpoint

        User user = new User(registerDTO.getUsername(), passwordEncoder.encode(registerDTO.getPassword()), registerDTO.getDeviceLimit());
        Role role = roleRepo.findByName(registerDTO.getRole()).orElseThrow();
        user.setRole(role);

        userRepository.save(user);
    }
}
