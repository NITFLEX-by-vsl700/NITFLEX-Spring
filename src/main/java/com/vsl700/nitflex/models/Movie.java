package com.vsl700.nitflex.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Document
@RequiredArgsConstructor
@Getter
@Setter
public class Movie{
    @Id
    private String id;

    @DocumentReference
    private User requester;

    @NonNull
    private String name;

    @NonNull
    private MovieType type;

    @NonNull
    private String path;

    @NonNull
    private long size;

    private String filmPath;

    private String trailerPath;

    @Setter(AccessLevel.NONE)
    private Date dateAdded = Date.from(Instant.now());

    private boolean transcoded = false;

    public enum MovieType{
        Film,
        Series
    }
}
