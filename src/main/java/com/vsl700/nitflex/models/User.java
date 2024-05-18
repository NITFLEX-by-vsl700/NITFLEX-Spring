package com.vsl700.nitflex.models;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Document
@RequiredArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @Setter(AccessLevel.NONE)
    private String id;
    @NonNull
    private String username;
    @NonNull
    private String password;

    @DocumentReference
    private Role role;
}
