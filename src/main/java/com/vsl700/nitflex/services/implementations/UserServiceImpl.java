package com.vsl700.nitflex.services.implementations;

import com.vsl700.nitflex.configs.UserAuthenticationProvider;
import com.vsl700.nitflex.exceptions.BadRequestException;
import com.vsl700.nitflex.exceptions.DataUniquenessException;
import com.vsl700.nitflex.exceptions.DeviceLimitException;
import com.vsl700.nitflex.exceptions.LoginException;
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
    public void login(LoginDTO loginDTO) {
        User user = userRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(LoginException::new);

        if(deviceSessionRepository.findAllByUser(user).size() >= user.getDeviceLimit())
            throw new DeviceLimitException();

        if(!passwordEncoder.matches(CharBuffer.wrap(loginDTO.getPassword()), user.getPassword()))
            throw new LoginException();
    }

    @Override
    public void register(RegisterDTO registerDTO) {
        if(registerDTO.getUsername().isBlank() || registerDTO.getPassword().isBlank())
            throw new BadRequestException("Username/password cannot be empty!");

        if(userRepository.findByUsername(registerDTO.getUsername()).isPresent())
            throw new DataUniquenessException("User with such username already exists!");

        User user = new User(registerDTO.getUsername(), passwordEncoder.encode(registerDTO.getPassword()), registerDTO.getDeviceLimit());
        Role role = roleRepo.findByName(registerDTO.getRole()).orElseThrow();
        user.setRole(role);

        userRepository.save(user);
    }
}
