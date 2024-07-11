package com.vsl700.nitflex.services;

import com.vsl700.nitflex.models.dto.LoginDTO;
import com.vsl700.nitflex.models.dto.RegisterDTO;
import com.vsl700.nitflex.models.dto.UserDTO;
import com.vsl700.nitflex.models.dto.UserStatusDTO;

public interface UserService {
    void login(LoginDTO credentialsDto);
    void register(RegisterDTO userDto);
}
