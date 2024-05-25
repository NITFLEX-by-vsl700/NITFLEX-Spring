package com.vsl700.nitflex.services;

import com.vsl700.nitflex.models.dto.LoginDTO;
import com.vsl700.nitflex.models.dto.RegisterDTO;
import com.vsl700.nitflex.models.dto.UserDTO;
import com.vsl700.nitflex.models.dto.UserStatusDTO;

public interface UserService {
    UserDTO login(LoginDTO credentialsDto);
    UserDTO register(RegisterDTO userDto);
    UserDTO findByUsername(String login);
    UserStatusDTO getUserStatus();
}
