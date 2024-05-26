package com.vsl700.nitflex.services;

import com.vsl700.nitflex.models.Episode;
import com.vsl700.nitflex.models.Movie;
import com.vsl700.nitflex.models.Subtitle;

import java.util.Collection;
import java.util.List;

public interface MovieAPIService {
    List<Movie> getAllAvailableMovies();
    List<Movie> searchMovies(String search);
    Movie getMovieById(String movieId);
    List<Episode> getEpisodesByMovieId(String movieId);
    List<Subtitle> getAllSubtitlesByMovieId(String movieId);
    List<Subtitle> getTrailerSubtitlesByMovieId(String movieId);
    List<Subtitle> getFilmSubtitlesByMovieId(String movieId);
    List<Subtitle> getEpisodeSubtitlesByMovieAndEpisodeId(String movieId, String episodeId);
}
