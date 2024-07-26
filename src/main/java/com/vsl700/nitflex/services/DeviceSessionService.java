package com.vsl700.nitflex.services;

import com.vsl700.nitflex.models.DeviceSession;

public interface DeviceSessionService {
    void addNewDeviceSession(String deviceName);
    DeviceSession getCurrentDeviceSession();
    boolean validateCurrentDeviceSession();
    void removeDeviceSession(DeviceSession deviceSession);
}
