package com.vsl700.nitflex.models;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Document
@RequiredArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @Setter(AccessLevel.NONE)
    private String id;
    @Setter(AccessLevel.NONE)
    private Date dateCreated = Date.from(Instant.now());
    @NonNull
    private String username;
    @NonNull
    private String password;
    @NonNull
    private int deviceLimit;

    private UserStatus status = UserStatus.ACTIVE;

    @DocumentReference
    private Role role;

    @DocumentReference
    @Setter(AccessLevel.NONE)
    private List<DeviceSession> deviceSessions;

    public enum UserStatus{
        ACTIVE,
        BANNED
    }
}
