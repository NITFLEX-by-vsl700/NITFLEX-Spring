package com.vsl700.nitflex.services;

import com.vsl700.nitflex.models.dto.RegisterDTO;
import com.vsl700.nitflex.models.dto.UserStatusDTO;

public interface AuthenticationService {
    String getCurrentUserName();
    UserStatusDTO getUserStatus();
}
