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

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class StreamingController {
    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private SharedProperties sharedProperties;

    @GetMapping("stream/film/{id}/{dashFilePath}")
    public ResponseEntity<Resource> getFilmDashFile(@PathVariable String id, @PathVariable String dashFilePath){
        Movie movie = movieRepository.findById(id)
                .orElseThrow();

        // Validate the given dash file path
        try {
            dashFilePath = Path.of(dashFilePath).getFileName().toString();
        }catch (InvalidPathException e){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build(); // TODO Add custom exception (InvalidDashFileException)
        }

        Path fullDashFilePath = Paths.get(sharedProperties.getMoviesFolder(), movie.getPath(), movie.getFilmPath(), dashFilePath);

        // Prevent any 500 Internal Server errors if resulting path is invalid for Nitflex streaming
        if(!Files.exists(fullDashFilePath) || Files.isDirectory(fullDashFilePath))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build(); // TODO Add custom exception (InvalidDashFileException)

        MediaType contentType = null;
        if(dashFilePath.endsWith(".mpd"))
            contentType = MediaType.parseMediaType("application/dash+xml");
        else if(dashFilePath.endsWith(".m4s"))
            contentType = MediaType.parseMediaType("video/mp4");

        assert contentType != null; // TODO Substitute with the Bad Request snippet, later with a custom exception (InvalidDashFileException)
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(contentType)
                .body(new FileSystemResource(fullDashFilePath));
    }

    /*@GetMapping("stream/film/{id}")
    public ResponseEntity<Resource> getFilmManifest(@PathVariable String id){
        Movie movie = movieRepository.findById(id)
                .orElseThrow();
        Path moviePath = Paths.get(sharedProperties.getMoviesFolder(), movie.getPath(), movie.getFilmPath(), "manifest.mpd");

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_XML)
                .body(new FileSystemResource(moviePath));
    }*/

    @GetMapping("stream/raw/film/{id}")
    public ResponseEntity<Resource> getFilmById(@PathVariable String id) throws URISyntaxException {
        Movie movie = movieRepository.findById(id)
                .orElseThrow();

        if(movie.isTranscoded())
            return ResponseEntity
                    .status(HttpStatus.MOVED_PERMANENTLY)
                    .location(new URI("/stream/film/%s/manifest.mpd".formatted(id)))
                    .build();

        Path moviePath = Paths.get(sharedProperties.getMoviesFolder(), movie.getPath(), movie.getFilmPath());

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new FileSystemResource(moviePath));
    }
}