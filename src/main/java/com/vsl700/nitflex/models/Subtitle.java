package com.vsl700.nitflex.models;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@RequiredArgsConstructor
@Getter
@Setter
public class Subtitle {
    @Id
    private String id;
    @NonNull
    private String movieId;
    @NonNull
    private String name;
    @NonNull
    private String path;
}
