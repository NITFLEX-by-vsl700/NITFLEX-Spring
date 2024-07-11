package com.vsl700.nitflex.services;

import com.vsl700.nitflex.models.DeviceSession;

public interface DeviceSessionService {
    boolean addNewDeviceSession(String deviceName);
    DeviceSession getCurrentDeviceSession();
    void removeDeviceSession(DeviceSession deviceSession);
}
