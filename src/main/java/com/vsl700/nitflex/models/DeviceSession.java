package com.vsl700.nitflex.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Document
@RequiredArgsConstructor
@Getter
@Setter
public class DeviceSession {
    @Id
    @Setter(AccessLevel.NONE)
    private String id;
    @NonNull
    private String userId;
    @NonNull
    private String deviceName;
    @NonNull
    private String userAgent;
}
