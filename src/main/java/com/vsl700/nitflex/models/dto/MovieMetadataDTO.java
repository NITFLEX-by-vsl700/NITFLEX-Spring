package com.vsl700.nitflex.models.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MovieMetadataDTO {
    private long duration;
    private int frames;
    private double frameRate;
    private int frameWidth;
    private int frameHeight;
}
