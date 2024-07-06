package com.vsl700.nitflex.models.dto;

import com.vsl700.nitflex.models.Movie;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class MovieDTO {
    private String id;
    private String name;
    private Movie.MovieType type;
    private Date dateAdded;
    private long size;
    private boolean hasTrailer;
    private String requester;
}