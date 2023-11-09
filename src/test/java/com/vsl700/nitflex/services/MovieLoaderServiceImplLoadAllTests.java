package com.vsl700.nitflex.services;

import com.vsl700.nitflex.components.SharedProperties;
import com.vsl700.nitflex.models.Episode;
import com.vsl700.nitflex.models.Movie;
import com.vsl700.nitflex.repo.EpisodeRepository;
import com.vsl700.nitflex.repo.MovieRepository;
import com.vsl700.nitflex.services.implementations.MovieLoaderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Disabled // These tests will only work on my machine!
public class MovieLoaderServiceImplLoadAllTests {

    private MovieRepository movieRepo;
    private EpisodeRepository episodeRepo;
    private SharedProperties sharedProperties;
    private MovieLoaderService service;

    private ArrayList<Movie> movies;
    private ArrayList<Episode> episodes;

    @BeforeEach
    public void setUp(){
        movieRepo = mock(MovieRepository.class);
        episodeRepo = mock(EpisodeRepository.class);
        sharedProperties = mock(SharedProperties.class);

        service = new MovieLoaderServiceImpl(movieRepo, episodeRepo, sharedProperties);

        movies = new ArrayList<>();
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
            String path = invocation.getArgument(0, String.class);

            return movies.stream().filter(m -> m.getPath().equals(path)).findFirst();
        }).when(movieRepo).findByPath(any(String.class));

        doAnswer(invocation -> {
            Iterable<Movie> moviesArg = invocation.getArgument(0);
            movies.removeIf(m -> StreamSupport.stream(moviesArg.spliterator(), false)
                    .anyMatch(m1 -> m1.getId().equals(m.getId())));

            return null;
        }).when(movieRepo).deleteAll(any());

        episodes = new ArrayList<>();
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
    }

    @Test
    public void loadNewlyAdded_test(){
        service.loadNewlyAdded();

        assertThat(movies.size()).isEqualTo(34);
    }

    @Test
    public void unloadNonExisting_test(){
        service.loadNewlyAdded();

        movieRepo.save(new Movie("Not existing movie", Movie.MovieType.Film, "not.existing.movie", 45L));
        movieRepo.save(new Movie("Not existing movie 2", Movie.MovieType.Film, "not.existing.movie2", 45L));
        movieRepo.save(new Movie("Not existing Matrix movie", Movie.MovieType.Film, "The.Matrix.Collection.1080p.BluRay.x265.DD5.1-WAR\\not.existing.matrix.movie", 45L));
        Movie series =
                movieRepo.save(new Movie("Not existing series", Movie.MovieType.Series, "not.existing.series", 45L));
        episodeRepo.save(new Episode(series.getId(), 1, 1, "S01E01.mkv"));
        episodeRepo.save(new Episode(series.getId(), 1, 2, "S01E02.mkv"));
        episodeRepo.save(new Episode(series.getId(), 1, 3, "S01E03.mkv"));
        episodeRepo.save(new Episode(series.getId(), 2, 1, "S02E01.mkv"));
        episodeRepo.save(new Episode(series.getId(), 2, 2, "S02E02.mkv"));
        Movie series2 =
                movieRepo.save(new Movie("Not existing series 2", Movie.MovieType.Series, "not.existing.series2", 45L));
        episodeRepo.save(new Episode(series2.getId(), 1, 1, "S01E01.mkv"));
        episodeRepo.save(new Episode(series2.getId(), 1, 2, "S01E02.mkv"));
        episodeRepo.save(new Episode(series2.getId(), 1, 3, "S02E01.mkv"));
        episodeRepo.save(new Episode(series2.getId(), 2, 1, "S02E02.mkv"));
        episodeRepo.save(new Episode(series2.getId(), 2, 2, "S02E03.mkv"));

        service.unloadNonExisting();

        assertThat(movies.size()).isEqualTo(34);
    }

    @Test
    public void unloadNonExisting_thenCheckForNewlyAdded_test(){
        service.loadNewlyAdded();

        movieRepo.save(new Movie("Not existing movie", Movie.MovieType.Film, "not.existing.movie", 45L));
        movieRepo.save(new Movie("Not existing movie 2", Movie.MovieType.Film, "not.existing.movie2", 45L));
        movieRepo.save(new Movie("Not existing Matrix movie", Movie.MovieType.Film, "The.Matrix.Collection.1080p.BluRay.x265.DD5.1-WAR\\not.existing.matrix.movie", 45L));
        Movie series =
                movieRepo.save(new Movie("Not existing series", Movie.MovieType.Series, "not.existing.series", 45L));
        episodeRepo.save(new Episode(series.getId(), 1, 1, "S01E01.mkv"));
        episodeRepo.save(new Episode(series.getId(), 1, 2, "S01E02.mkv"));
        episodeRepo.save(new Episode(series.getId(), 1, 3, "S01E03.mkv"));
        episodeRepo.save(new Episode(series.getId(), 2, 1, "S02E01.mkv"));
        episodeRepo.save(new Episode(series.getId(), 2, 2, "S02E02.mkv"));
        Movie series2 =
                movieRepo.save(new Movie("Not existing series 2", Movie.MovieType.Series, "not.existing.series2", 45L));
        episodeRepo.save(new Episode(series2.getId(), 1, 1, "S01E01.mkv"));
        episodeRepo.save(new Episode(series2.getId(), 1, 2, "S01E02.mkv"));
        episodeRepo.save(new Episode(series2.getId(), 1, 3, "S02E01.mkv"));
        episodeRepo.save(new Episode(series2.getId(), 2, 1, "S02E02.mkv"));
        episodeRepo.save(new Episode(series2.getId(), 2, 2, "S02E03.mkv"));

        service.unloadNonExisting();
        service.loadNewlyAdded();

        assertThat(movies.size()).isEqualTo(34);
    }

    @RepeatedTest(30)
    public void loadNewlyAdded_thenLoadSomeMoreNewlyAdded_test(){
        service.loadNewlyAdded();

        for(int i = 0; i < 10; i++){ // Remove 10 (random) movies
            Movie movie = movies.stream().findAny().orElseThrow();
            movies.remove(movie);
        }

        service.loadNewlyAdded();

        assertThat(movies.size()).isEqualTo(34);
    }

    @RepeatedTest(30)
    public void loadNewlyAdded_thenUnloadNonExisting_thenLoadSomeMoreNewlyAdded_test(){
        service.loadNewlyAdded();

        for(int i = 0; i < 10; i++){ // Remove 10 (random) movies
            Movie movie = movies.stream().findAny().orElseThrow();
            movies.remove(movie);
        }

        movieRepo.save(new Movie("Not existing movie", Movie.MovieType.Film, "not.existing.movie", 45L));
        movieRepo.save(new Movie("Not existing movie 2", Movie.MovieType.Film, "not.existing.movie2", 45L));
        movieRepo.save(new Movie("Not existing Matrix movie", Movie.MovieType.Film, "The.Matrix.Collection.1080p.BluRay.x265.DD5.1-WAR\\not.existing.matrix.movie", 45L));
        Movie series =
                movieRepo.save(new Movie("Not existing series", Movie.MovieType.Series, "not.existing.series", 45L));
        episodeRepo.save(new Episode(series.getId(), 1, 1, "S01E01.mkv"));
        episodeRepo.save(new Episode(series.getId(), 1, 2, "S01E02.mkv"));
        episodeRepo.save(new Episode(series.getId(), 1, 3, "S01E03.mkv"));
        episodeRepo.save(new Episode(series.getId(), 2, 1, "S02E01.mkv"));
        episodeRepo.save(new Episode(series.getId(), 2, 2, "S02E02.mkv"));
        Movie series2 =
                movieRepo.save(new Movie("Not existing series 2", Movie.MovieType.Series, "not.existing.series2", 45L));
        episodeRepo.save(new Episode(series2.getId(), 1, 1, "S01E01.mkv"));
        episodeRepo.save(new Episode(series2.getId(), 1, 2, "S01E02.mkv"));
        episodeRepo.save(new Episode(series2.getId(), 1, 3, "S02E01.mkv"));
        episodeRepo.save(new Episode(series2.getId(), 2, 1, "S02E02.mkv"));
        episodeRepo.save(new Episode(series2.getId(), 2, 2, "S02E03.mkv"));

        service.loadNewlyAdded();
        service.unloadNonExisting();

        assertThat(movies.size()).isEqualTo(34);
    }
}
