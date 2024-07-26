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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class DeviceSessionServiceImpl implements DeviceSessionService {
    private AuthenticationService authenticationService;
    private UserRepository userRepository;
    private DeviceSessionRepository deviceSessionRepository;

    private static final String VERSION_NUMBER_REGEX = "\\b\\d+\\b";
    private static final Pattern VERSION_NUMBER_PATTERN = Pattern.compile(VERSION_NUMBER_REGEX);

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
    public boolean validateCurrentDeviceSession() {
        DeviceSession currentDeviceSession = getCurrentDeviceSession();

        String givenUA = (String) SecurityContextHolder.getContext().getAuthentication().getDetails(); // Incoming UA
        String actualUA = currentDeviceSession.getUserAgent(); // Saved UA

        // TODO Finish implementing this method, after which write some tests!
        // Check equality in amount of version numbers
        int givenUAVersionNumbers = getUAVersionNumbersAmount(givenUA);
        int actualUAVersionNumbers = getUAVersionNumbersAmount(actualUA);
        if(givenUAVersionNumbers != actualUAVersionNumbers)
            return false;

        // Check equality in version numbers
        Matcher givenUAMatcher = VERSION_NUMBER_PATTERN.matcher(givenUA);
        Matcher actualUAMatcher = VERSION_NUMBER_PATTERN.matcher(givenUA);

        StringBuilder updatedActualUA = new StringBuilder(actualUA);
        while(givenUAMatcher.find() && actualUAMatcher.find()){ // Assuming that both matchers return either true or false at the same time
            int givenUAVersion = Integer.parseInt(givenUAMatcher.group());
            int actualUAVersion = Integer.parseInt(actualUAMatcher.group());

            if(givenUAVersion > actualUAVersion){
                // Update the version number in the saved UA
                updatedActualUA.replace(actualUAMatcher.start(), actualUAMatcher.end(), actualUAVersion + "");
            }else if(givenUAVersion < actualUAVersion)
                return false;
        }

        // After updating saved UA string, check if both strings are the same
        if(updatedActualUA.compareTo(new StringBuilder(givenUA)) != 0)
            return false;

        // Save updated string to DB
        currentDeviceSession.setUserAgent(updatedActualUA.toString());
        deviceSessionRepository.save(currentDeviceSession);

        return true;
    }

    private int getUAVersionNumbersAmount(String ua){
        Matcher matcher = VERSION_NUMBER_PATTERN.matcher(ua);

        int matches = 0;
        while (matcher.find()) {
            matches++;
        }

        return matches;
    }

    @Override
    public void removeDeviceSession(DeviceSession deviceSession) {
        deviceSessionRepository.delete(deviceSession);
    }
}
