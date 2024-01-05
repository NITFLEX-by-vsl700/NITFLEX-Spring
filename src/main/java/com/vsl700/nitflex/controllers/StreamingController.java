package com.vsl700.nitflex.controllers;

import com.vsl700.nitflex.components.SharedProperties;
import com.vsl700.nitflex.models.Movie;
import com.vsl700.nitflex.models.dto.MovieMetadataDTO;
import com.vsl700.nitflex.repo.MovieRepository;
import com.vsl700.nitflex.services.MovieStreamingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
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
        // TODO (maybe): MovieStreamingService::getVideoFilePathById
        Movie movie = movieRepository.findById(id)
                .orElseThrow();
        String moviePath = Paths.get(sharedProperties.getMoviesFolder(), movie.getPath(), movie.getFilmPath())
                .toString();

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new FileSystemResource(moviePath));
    }

    @GetMapping(value="stream/video/{id}")
    public List<String> streamVideoById(@PathVariable String id, @RequestParam int beginFrame, @RequestParam int length){
        // Films only!!!
        // TODO (maybe): MovieStreamingService::getVideoFilePathById
        Movie movie = movieRepository.findById(id)
                .orElseThrow();
        Path moviePath = Paths.get(sharedProperties.getMoviesFolder(), movie.getPath(), movie.getFilmPath());

        return movieStreamingService.grabFrames(moviePath, beginFrame, length).stream()
                .map(bytes -> Base64.getEncoder().encodeToString(bytes))
                .toList();
    }

    @GetMapping("stream/audio/{id}")
    public List<short[]> streamAudioById(@PathVariable String id, @RequestParam int beginFrame, @RequestParam int length){
        // Films only!!!
        // TODO (maybe): MovieStreamingService::getVideoFilePathById
        Movie movie = movieRepository.findById(id)
                .orElseThrow();
        Path moviePath = Paths.get(sharedProperties.getMoviesFolder(), movie.getPath(), movie.getFilmPath());

        return movieStreamingService.grabAudioFromFrames(moviePath, beginFrame, length);
    }

    @GetMapping("stream/info/{id}")
    public MovieMetadataDTO movieMetadata(@PathVariable String id){
        // Films only!!!
        // TODO (maybe): MovieStreamingService::getVideoFilePathById
        Movie movie = movieRepository.findById(id)
                .orElseThrow();
        Path moviePath = Paths.get(sharedProperties.getMoviesFolder(), movie.getPath(), movie.getFilmPath());

        var result = new MovieMetadataDTO();
        result.setDuration(movieStreamingService.getMovieDuration(moviePath));
        result.setFrames(movieStreamingService.getMovieFramesCount(moviePath));
        result.setFrameRate(movieStreamingService.getMovieFrameRate(moviePath));
        result.setFrameWidth(movieStreamingService.getMovieImageWidth(moviePath));
        result.setFrameHeight(movieStreamingService.getMovieImageHeight(moviePath));

        return result;
    }
}
