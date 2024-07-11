package com.vsl700.nitflex.services.implementations;

import com.vsl700.nitflex.exceptions.DataUniquenessException;
import com.vsl700.nitflex.exceptions.DeviceLimitException;
import com.vsl700.nitflex.exceptions.InternalServerErrorException;
import com.vsl700.nitflex.models.DeviceSession;
import com.vsl700.nitflex.models.User;
import com.vsl700.nitflex.models.dto.UserPrincipalDTO;
import com.vsl700.nitflex.repo.DeviceSessionRepository;
import com.vsl700.nitflex.repo.UserRepository;
import com.vsl700.nitflex.services.AuthenticationService;
import com.vsl700.nitflex.services.DeviceSessionService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DeviceSessionServiceImpl implements DeviceSessionService {
    private AuthenticationService authenticationService;
    private UserRepository userRepository;
    private DeviceSessionRepository deviceSessionRepository;

    @Override
    public void addNewDeviceSession(String deviceName) {
        // Get current user
        User currentUser = userRepository.findByUsername(authenticationService.getCurrentUserName())
                .orElseThrow(() -> new InternalServerErrorException("Authenticated user not found in database!"));

        // Check for exceeding device limit
        if(deviceSessionRepository.findAllByUser(currentUser).size() >= currentUser.getDeviceLimit())
            throw new DeviceLimitException();

        // Check for already existing device with the given name
        if(deviceSessionRepository.findByDeviceName(deviceName).isPresent())
            throw new DataUniquenessException("A device with such a name already exists!");

        // Add device
        String userAgent = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        var deviceSession = new DeviceSession(currentUser, deviceName, userAgent);
        deviceSessionRepository.save(deviceSession);
    }

    @Override
    public DeviceSession getCurrentDeviceSession() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication.getPrincipal() instanceof UserPrincipalDTO userPrincipalDTO))
            return null;

        if(userPrincipalDTO.getDeviceName() == null)
            return null;

        return deviceSessionRepository.findByDeviceName(userPrincipalDTO.getDeviceName())
                .orElseThrow(); // TODO Add custom exception
    }

    @Override
    public void removeDeviceSession(DeviceSession deviceSession) {
        deviceSessionRepository.deleteByDeviceName(deviceSession.getDeviceName());
    }
}
