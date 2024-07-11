package com.vsl700.nitflex.repo;

import com.vsl700.nitflex.models.DeviceSession;
import com.vsl700.nitflex.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceSessionRepository extends MongoRepository<DeviceSession, String> {
    Optional<DeviceSession> findByDeviceName(String deviceName);
    List<DeviceSession> findAllByUser(User user);
    void deleteByDeviceName(String deviceName);
}
