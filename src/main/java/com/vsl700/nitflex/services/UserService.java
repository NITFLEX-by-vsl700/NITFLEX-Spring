package com.vsl700.nitflex.services;

import com.vsl700.nitflex.models.dto.LoginDTO;
import com.vsl700.nitflex.models.dto.RegisterDTO;

public interface UserService {
    void login(LoginDTO credentialsDto);
    void logout();
    void register(RegisterDTO userDto);
    void delete(String userId);
}
