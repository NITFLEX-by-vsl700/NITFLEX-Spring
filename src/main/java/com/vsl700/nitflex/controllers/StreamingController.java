package com.vsl700.nitflex.controllers;

import com.vsl700.nitflex.components.SharedProperties;
import com.vsl700.nitflex.models.Movie;
import com.vsl700.nitflex.repo.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Paths;

@RestController
public class StreamingController {
    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private SharedProperties sharedProperties;

    @CrossOrigin
    @GetMapping("stream/{id}")
    public ResponseEntity<Resource> getVideoById(@PathVariable String id){
        // Films only!!!
        // MovieStreamingService::getVideoFilePathById
        Movie movie = movieRepository.findById(id)
                .orElseThrow();
        String moviePath = Paths.get(sharedProperties.getMoviesFolder(), movie.getPath(), movie.getFilmPath())
                .toString();

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new FileSystemResource(moviePath));
    }
}