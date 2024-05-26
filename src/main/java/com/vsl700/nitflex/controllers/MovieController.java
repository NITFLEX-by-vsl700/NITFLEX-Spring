package com.vsl700.nitflex.controllers;

import com.vsl700.nitflex.models.Movie;
import com.vsl700.nitflex.models.dto.EpisodeDTO;
import com.vsl700.nitflex.models.dto.MovieDTO;
import com.vsl700.nitflex.models.dto.SubtitleDTO;
import com.vsl700.nitflex.services.MovieAPIService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MovieController {
    @Autowired
    private MovieAPIService movieAPIService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/movies")
    public List<MovieDTO> getAllAvailableMovies(@RequestParam(required = false) String search){
        List<Movie> movies;
        if(search == null)
            movies = movieAPIService.getAllAvailableMovies();
        else movies = movieAPIService.searchMovies(search);

        return movies.stream()
                .map(m -> modelMapper.map(m, MovieDTO.class))
                .toList();
    }

    @GetMapping("/movies/{movieId}")
    public MovieDTO getMovieById(@PathVariable String movieId){
        return modelMapper.map(movieAPIService.getMovieById(movieId), MovieDTO.class);
    }

    @GetMapping("/episodes/{movieId}")
    public List<EpisodeDTO> getEpisodesByMovieId(@PathVariable String movieId){
        return movieAPIService.getEpisodesByMovieId(movieId).stream()
                .map(e -> modelMapper.map(e, EpisodeDTO.class))
                .toList();
    }

    @GetMapping("/subtitles/{movieId}")
    public List<SubtitleDTO> getAllSubtitlesByMovieId(@PathVariable String movieId){
        return movieAPIService.getAllSubtitlesByMovieId(movieId).stream()
                .map(s -> modelMapper.map(s, SubtitleDTO.class))
                .toList();
    }

    @GetMapping("/subtitles/{movieId}/trailer")
    public List<SubtitleDTO> getTrailerSubtitlesByMovieId(@PathVariable String movieId){
        return movieAPIService.getTrailerSubtitlesByMovieId(movieId).stream()
                .map(s -> modelMapper.map(s, SubtitleDTO.class))
                .toList();
    }

    @GetMapping("/subtitles/{movieId}/film")
    public List<SubtitleDTO> getFilmSubtitlesByMovieId(@PathVariable String movieId){
        return movieAPIService.getFilmSubtitlesByMovieId(movieId).stream()
                .map(s -> modelMapper.map(s, SubtitleDTO.class))
                .toList();
    }

    @GetMapping("/subtitles/{movieId}/episode/{episodeId}")
    public List<SubtitleDTO> getEpisodeSubtitlesByMovieAndEpisodeId(@PathVariable String movieId, @PathVariable String episodeId){
        return movieAPIService.getEpisodeSubtitlesByMovieAndEpisodeId(movieId, episodeId).stream()
                .map(s -> modelMapper.map(s, SubtitleDTO.class))
                .toList();
    }
}