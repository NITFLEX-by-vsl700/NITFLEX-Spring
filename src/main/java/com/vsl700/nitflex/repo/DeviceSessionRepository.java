package com.vsl700.nitflex.repo;

import com.vsl700.nitflex.models.DeviceSession;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DeviceSessionRepository extends MongoRepository<DeviceSession, String> {
    Optional<DeviceSession> findByDeviceName(String deviceName);
    void deleteByDeviceName(String deviceName);
}
