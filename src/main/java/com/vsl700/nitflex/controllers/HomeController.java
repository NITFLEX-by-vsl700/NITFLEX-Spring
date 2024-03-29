package com.vsl700.nitflex.controllers;

import com.vsl700.nitflex.components.SharedProperties;
import com.vsl700.nitflex.models.Episode;
import com.vsl700.nitflex.models.Subtitle;
import com.vsl700.nitflex.models.dto.EpisodeDTO;
import com.vsl700.nitflex.models.dto.MovieDTO;
import com.vsl700.nitflex.models.dto.SubtitleDTO;
import com.vsl700.nitflex.repo.EpisodeRepository;
import com.vsl700.nitflex.repo.MovieRepository;
import com.vsl700.nitflex.repo.SubtitleRepository;
import jdk.jshell.spi.ExecutionControl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.util.List;

@RestController
public class HomeController {
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private EpisodeRepository episodeRepository;
    @Autowired
    private SubtitleRepository subtitleRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SharedProperties sharedProperties;

    @GetMapping("/movies")
    public List<MovieDTO> getAllAvailableMovies(){
        return movieRepository.findAll().stream()
                .filter(m -> m.isTranscoded() || !sharedProperties.isTranscodingEnabled())
                .map(m -> modelMapper.map(m, MovieDTO.class))
                .toList();
    }

    @GetMapping("/movies/{movieId}")
    public MovieDTO getMovieById(@PathVariable String movieId){
        if(movieRepository.findById(movieId).isEmpty())
            throw new RuntimeException("Movie with id %s not found!".formatted(movieId)); // TODO Add custom exception

        return modelMapper.map(movieRepository.findById(movieId), MovieDTO.class);
    }

    @GetMapping("/episodes/{movieId}")
    public List<EpisodeDTO> getEpisodesByMovieId(@PathVariable String movieId){
        if(movieRepository.findById(movieId).isEmpty())
            throw new RuntimeException("Movie with id %s not found!".formatted(movieId)); // TODO Add custom exception

        return episodeRepository.findAllBySeriesId(movieId).stream()
                .map(e -> modelMapper.map(e, EpisodeDTO.class))
                .toList();
    }

    @GetMapping("/subtitles/{movieId}")
    public List<SubtitleDTO> getAllSubtitlesByMovieId(@PathVariable String movieId){
        if(movieRepository.findById(movieId).isEmpty())
            throw new RuntimeException("Movie with id %s not found!".formatted(movieId)); // TODO Add custom exception

        return subtitleRepository.findAllByMovieId(movieId).stream()
                .map(s -> modelMapper.map(s, SubtitleDTO.class))
                .toList();
    }

    @GetMapping("/subtitles/{movieId}/trailer")
    public List<SubtitleDTO> getTrailerSubtitlesByMovieId(@PathVariable String movieId){
        if(movieRepository.findById(movieId).isEmpty())
            throw new RuntimeException("Movie with id %s not found!".formatted(movieId)); // TODO Add custom exception

        return subtitleRepository.findAllByMovieId(movieId).stream()
                .filter(s -> s.getType().equals(Subtitle.SubtitleType.Trailer))
                .map(s -> modelMapper.map(s, SubtitleDTO.class))
                .toList();
    }

    @GetMapping("/subtitles/{movieId}/film")
    public List<SubtitleDTO> getFilmSubtitlesByMovieId(@PathVariable String movieId){
        if(movieRepository.findById(movieId).isEmpty())
            throw new RuntimeException("Movie with id %s not found!".formatted(movieId)); // TODO Add custom exception

        return subtitleRepository.findAllByMovieId(movieId).stream()
                .filter(s -> s.getType().equals(Subtitle.SubtitleType.Film) || s.getType().equals(Subtitle.SubtitleType.Undetermined))
                .map(s -> modelMapper.map(s, SubtitleDTO.class))
                .toList();
    }

    @GetMapping("/subtitles/{movieId}/episode/{episodeId}")
    public List<SubtitleDTO> getSeriesSubtitlesByMovieId(@PathVariable String movieId, @PathVariable String episodeId){
        if(episodeRepository.findAllBySeriesId(movieId).stream().noneMatch(e -> e.getId().equals(episodeId)))
            throw new RuntimeException("Episode %s not found in movie %s!".formatted(episodeId, movieId)); // TODO Add custom exception

        return subtitleRepository.findAllByMovieId(movieId).stream()
                .filter(s -> episodeId.equals(s.getEpisodeId()) || s.getType().equals(Subtitle.SubtitleType.Undetermined))
                .map(s -> modelMapper.map(s, SubtitleDTO.class))
                .toList();
    }
}