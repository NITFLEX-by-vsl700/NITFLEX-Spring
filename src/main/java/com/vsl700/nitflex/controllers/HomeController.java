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
        return modelMapper.map(movieRepository.findById(movieId), MovieDTO.class);
    }

    @GetMapping("/episodes/{movieId}")
    public List<EpisodeDTO> getEpisodesByMovieId(@PathVariable String movieId){
        return episodeRepository.findAllBySeriesId(movieId).stream()
                .map(e -> modelMapper.map(e, EpisodeDTO.class))
                .toList();
    }

    @GetMapping("/subtitles/{movieId}")
    public List<SubtitleDTO> getSubtitlesByMovieId(@PathVariable String movieId){
        return subtitleRepository.findAllByMovieId(movieId).stream()
                .map(s -> modelMapper.map(s, SubtitleDTO.class))
                .toList();
    }

    @GetMapping("/subtitles/{movieId}/{episodeId}")
    public List<SubtitleDTO> getSeriesSubtitlesByMovieId(@PathVariable String movieId, @PathVariable String episodeId){
        Episode episode = episodeRepository.findById(episodeId)
                .orElseThrow(); // TODO Add custom exception

        List<SubtitleDTO> result = subtitleRepository.findAllByMovieId(movieId).stream()
                .filter(s -> areSubtitlesForGivenEpisode(s, episode))
                .map(s -> modelMapper.map(s, SubtitleDTO.class))
                .toList();

        if(result.isEmpty())
            return subtitleRepository.findAllByMovieId(movieId).stream()
                    .map(s -> modelMapper.map(s, SubtitleDTO.class))
                    .toList();

        return result;
    }

    private boolean areSubtitlesForGivenEpisode(Subtitle subtitle, Episode episode){
        Path subtitlePath = Path.of(subtitle.getPath());
        Path episodePath = Path.of(episode.getEpisodePath());

        if(subtitlePath.getNameCount() > 1) {
            Path subtitleParentFolder = subtitlePath.subpath(0, subtitlePath.getNameCount() - 1);
            if (subtitleParentFolder.toString().equals(episodePath.toString()))
                return true;
        }

        return subtitle.getPath().contains("S0%dE0%d".formatted(episode.getSeasonNumber(), episode.getEpisodeNumber()))
                || subtitle.getPath().contains("S%dE%d".formatted(episode.getSeasonNumber(), episode.getEpisodeNumber()));
    }
}