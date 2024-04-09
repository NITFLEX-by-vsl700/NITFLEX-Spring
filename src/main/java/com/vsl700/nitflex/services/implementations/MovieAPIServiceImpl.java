package com.vsl700.nitflex.services.implementations;

import com.vsl700.nitflex.components.SharedProperties;
import com.vsl700.nitflex.models.Episode;
import com.vsl700.nitflex.models.Movie;
import com.vsl700.nitflex.models.Subtitle;
import com.vsl700.nitflex.repo.EpisodeRepository;
import com.vsl700.nitflex.repo.MovieRepository;
import com.vsl700.nitflex.repo.SubtitleRepository;
import com.vsl700.nitflex.services.MovieAPIService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MovieAPIServiceImpl implements MovieAPIService {
    private MovieRepository movieRepository;
    private EpisodeRepository episodeRepository;
    private SubtitleRepository subtitleRepository;
    private SharedProperties sharedProperties;

    @Override
    public List<Movie> getAllAvailableMovies() {
        return movieRepository.findAll().stream()
                .filter(m -> m.isTranscoded() || !sharedProperties.isTranscodingEnabled())
                .toList();
    }

    @Override
    public Movie getMovieById(String movieId) {
        return movieRepository.findById(movieId).orElseThrow(); // TODO Add custom exception
    }

    @Override
    public List<Episode> getEpisodesByMovieId(String movieId) {
        if(movieRepository.findById(movieId).isEmpty())
            throw new RuntimeException("Movie with id %s not found!".formatted(movieId)); // TODO Add custom exception

        return episodeRepository.findAllBySeriesId(movieId);
    }

    @Override
    public List<Subtitle> getAllSubtitlesByMovieId(String movieId) {
        if(movieRepository.findById(movieId).isEmpty())
            throw new RuntimeException("Movie with id %s not found!".formatted(movieId)); // TODO Add custom exception

        return subtitleRepository.findAllByMovieId(movieId);
    }

    @Override
    public List<Subtitle> getTrailerSubtitlesByMovieId(String movieId) {
        if(movieRepository.findById(movieId).isEmpty())
            throw new RuntimeException("Movie with id %s not found!".formatted(movieId)); // TODO Add custom exception

        return subtitleRepository.findAllByMovieId(movieId).stream()
                .filter(s -> s.getType().equals(Subtitle.SubtitleType.Trailer))
                .toList();
    }

    @Override
    public List<Subtitle> getFilmSubtitlesByMovieId(String movieId) {
        if(movieRepository.findById(movieId).isEmpty())
            throw new RuntimeException("Movie with id %s not found!".formatted(movieId)); // TODO Add custom exception

        return subtitleRepository.findAllByMovieId(movieId).stream()
                .filter(s -> s.getType().equals(Subtitle.SubtitleType.Film) || s.getType().equals(Subtitle.SubtitleType.Undetermined))
                .toList();
    }

    @Override
    public List<Subtitle> getEpisodeSubtitlesByMovieAndEpisodeId(String movieId, String episodeId) {
        if(episodeRepository.findAllBySeriesId(movieId).stream().noneMatch(e -> e.getId().equals(episodeId)))
            throw new RuntimeException("Episode %s not found in movie %s!".formatted(episodeId, movieId)); // TODO Add custom exception

        return subtitleRepository.findAllByMovieId(movieId).stream()
                .filter(s -> episodeId.equals(s.getEpisodeId()) || s.getType().equals(Subtitle.SubtitleType.Undetermined))
                .toList();
    }
}
