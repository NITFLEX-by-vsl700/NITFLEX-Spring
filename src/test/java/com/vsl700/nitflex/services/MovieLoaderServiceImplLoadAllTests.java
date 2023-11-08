package com.vsl700.nitflex.services;

import com.vsl700.nitflex.components.SharedProperties;
import com.vsl700.nitflex.models.Episode;
import com.vsl700.nitflex.models.Movie;
import com.vsl700.nitflex.repo.EpisodeRepository;
import com.vsl700.nitflex.repo.MovieRepository;
import com.vsl700.nitflex.services.implementations.MovieLoaderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MovieLoaderServiceImplLoadAllTests {

    private MovieRepository movieRepo;

    private EpisodeRepository episodeRepo;

    private SharedProperties sharedProperties;

    private MovieLoaderService service;

    @BeforeEach
    public void setUp(){
        movieRepo = mock(MovieRepository.class);
        episodeRepo = mock(EpisodeRepository.class);
        sharedProperties = mock(SharedProperties.class);

        service = new MovieLoaderServiceImpl(movieRepo, episodeRepo, sharedProperties);
    }

    @Test
    public void test(){
        ArrayList<Movie> movies = new ArrayList<>();
        doAnswer((invocation) -> {
            Movie movie = invocation.getArgument(0, Movie.class);
            if(movies.contains(movie))
                return movie;

            movie.setId("850fd9g9b90gibgf0dju9" + Math.random());
            movies.add(movie);
            return movie;
        }).when(movieRepo).save(any(Movie.class));

        doReturn(movies).when(movieRepo).findAll();

        doAnswer(invocation -> {
            var moviesArg = invocation.getArgument(0, Iterable.class);
            movies.removeIf(m -> Stream.of(moviesArg).anyMatch(m1 -> ((Movie) m1).getId().equals(m.getId())));

            return null;
        }).when(movieRepo).deleteAll(any(Iterable.class));

        ArrayList<Episode> episodes = new ArrayList<>();
        when(episodeRepo.save(any())).then((invocation) -> {
            Episode episode = invocation.getArgument(0, Episode.class);
            episodes.add(episode);
            return episode;
        });

        doAnswer(invocation -> {
            String seriesId = invocation.getArgument(0, String.class);
            episodes.removeIf(e -> e.getSeriesId().equals(seriesId));

            return null;
        }).when(episodeRepo).deleteBySeriesId(any(String.class));

        when(sharedProperties.getMoviesFolder()).thenReturn("D:\\Videos");

        service.loadAll();

        assertThat(movies.size()).isEqualTo(34);

        // TODO: Test by adding and removing movies and then running 'service.loadAll()' again
    }
}
