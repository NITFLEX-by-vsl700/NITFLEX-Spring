package com.vsl700.nitflex.controllers;

import com.vsl700.nitflex.components.SharedProperties;
import com.vsl700.nitflex.models.Movie;
import com.vsl700.nitflex.repo.MovieRepository;
import com.vsl700.nitflex.services.MovieStreamingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
public class StreamingController {
    @Autowired
    private MovieStreamingService movieStreamingService;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private SharedProperties sharedProperties;

    @GetMapping("stream/{id}")
    public ResponseEntity<Resource> streamVideoFileById(@PathVariable String id){
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

    @GetMapping("stream/video/{id}")
    public List<byte[]> streamVideoById(@PathVariable String id, @RequestParam int beginFrame, @RequestParam int length){
        // Films only!!!
        // MovieStreamingService::getVideoFilePathById
        Movie movie = movieRepository.findById(id)
                .orElseThrow();
        Path moviePath = Paths.get(sharedProperties.getMoviesFolder(), movie.getPath(), movie.getFilmPath());

        return movieStreamingService.grabFrames(moviePath, beginFrame, length);
    }

    @GetMapping("stream/audio/{id}")
    public List<short[]> streamAudioById(@PathVariable String id, @RequestParam int beginFrame, @RequestParam int length){
        // Films only!!!
        // MovieStreamingService::getVideoFilePathById
        Movie movie = movieRepository.findById(id)
                .orElseThrow();
        Path moviePath = Paths.get(sharedProperties.getMoviesFolder(), movie.getPath(), movie.getFilmPath());

        return movieStreamingService.grabAudioFromFrames(moviePath, beginFrame, length);
    }
}
