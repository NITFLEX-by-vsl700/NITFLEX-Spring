package com.vsl700.nitflex.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@RequiredArgsConstructor
@Getter
@Setter
public class Episode {
    @Id
    @Setter(AccessLevel.NONE)
    private String id;
    @NonNull
    private String seriesId;
    @NonNull
    private int episodeNumber;
    @NonNull
    private String episodePath;
}
