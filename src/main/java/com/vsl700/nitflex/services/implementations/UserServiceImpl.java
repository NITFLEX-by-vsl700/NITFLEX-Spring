package com.vsl700.nitflex.services.implementations;

import com.vsl700.nitflex.exceptions.*;
import com.vsl700.nitflex.models.Role;
import com.vsl700.nitflex.models.User;
import com.vsl700.nitflex.models.dto.LoginDTO;
import com.vsl700.nitflex.models.dto.RegisterDTO;
import com.vsl700.nitflex.repo.DeviceSessionRepository;
import com.vsl700.nitflex.repo.MovieRepository;
import com.vsl700.nitflex.repo.RoleRepository;
import com.vsl700.nitflex.repo.UserRepository;
import com.vsl700.nitflex.services.DeviceSessionService;
import com.vsl700.nitflex.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private DeviceSessionRepository deviceSessionRepository;

    @Autowired
    private DeviceSessionService deviceSessionService;

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
    public void logout(){
        var deviceSession = deviceSessionService.getCurrentDeviceSession();

        if(deviceSession != null)
            deviceSessionService.removeDeviceSession(deviceSession);
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

    @Override
    public void delete(String userId) {
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found!"));
        var movies = movieRepository.findAllByRequester(user);
        movies.forEach(m -> m.setRequester(null));
        movieRepository.saveAll(movies);

        userRepository.delete(user);
    }
}
