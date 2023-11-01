package com.vsl700.nitflex.services;

import com.vsl700.nitflex.components.Settings;
import com.vsl700.nitflex.models.Episode;
import com.vsl700.nitflex.models.Movie;
import com.vsl700.nitflex.repo.EpisodeRepository;
import com.vsl700.nitflex.repo.MovieRepository;
import com.vsl700.nitflex.services.implementations.MovieLoaderServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Disabled // These tests will only work on my machine!
@ExtendWith(MockitoExtension.class)
public class MovieLoaderServiceImplTests {

    @Mock
    private MovieRepository movieRepo;

    @Mock
    private EpisodeRepository episodeRepo;

    @Mock
    private Settings settings;

    private MovieLoaderService service;

    @BeforeEach
    public void setUp(){
        service = new MovieLoaderServiceImpl(movieRepo, episodeRepo, settings);
    }

    @Test
    public void regularMovie_withTrailer_Test(){
        AtomicReference<Movie> resultMovie = new AtomicReference<>();

        when(movieRepo.save(any())).then((invocation) -> {
            Movie movie = invocation.getArgument(0, Movie.class);
            resultMovie.set(movie);
            return movie;
        });

        when(settings.getMoviesFolder()).thenReturn("D:\\Videos");

        service.load("D:\\Videos\\The.Marksman.2021.BDRip.x264.AC3.BGAUDiO-SiSO");

        Assertions.assertAll(() -> {
            assertThat(resultMovie.get()).isNotNull();
            assertThat(resultMovie.get().getType()).isEqualTo(Movie.MovieType.Film);
            assertThat(resultMovie.get().getName()).isEqualTo("The.Marksman.2021.BDRip.x264.AC3.BGAUDiO-SiSO");
            assertThat(resultMovie.get().getPath()).isEqualTo("The.Marksman.2021.BDRip.x264.AC3.BGAUDiO-SiSO");
            assertThat(resultMovie.get().getTrailerPath()).isEqualTo("sample.mkv");
            assertThat(resultMovie.get().getFilmPath()).isEqualTo("The.Marksman.2021.BDRip.x264.AC3.BGAUDiO-SiSO.mkv");
            assertThat(resultMovie.get().getSize()).isEqualTo(1086293770L);
        });
    }

    @Test
    public void regularMovie_withTrailer_Test2(){
        AtomicReference<Movie> resultMovie = new AtomicReference<>();

        when(movieRepo.save(any())).then((invocation) -> {
            var movie = invocation.getArgument(0, Movie.class);
            resultMovie.set(movie);

            return movie;
        });

        when(settings.getMoviesFolder()).thenReturn("D:\\Videos");

        service.load("D:\\Videos\\Ex.Machina.2014.BRRip.XviD.BGAUDiO-SiSO");

        Assertions.assertAll(() -> {
            assertThat(resultMovie.get()).isNotNull();
            assertThat(resultMovie.get().getType()).isEqualTo(Movie.MovieType.Film);
            assertThat(resultMovie.get().getName()).isEqualTo("Ex.Machina.2014.BRRip.XviD.BGAUDiO-SiSO");
            assertThat(resultMovie.get().getPath()).isEqualTo("Ex.Machina.2014.BRRip.XviD.BGAUDiO-SiSO");
            assertThat(resultMovie.get().getTrailerPath()).isEqualTo("sample.avi");
            assertThat(resultMovie.get().getFilmPath()).isEqualTo("Ex.Machina.2014.BRRip.XviD.BGAUDiO-SiSO.avi");
            assertThat(resultMovie.get().getSize()).isEqualTo(881577837L);
        });
    }

    @Test
    public void regularMovie_withoutTrailer_Test(){
        AtomicReference<Movie> resultMovie = new AtomicReference<>();

        when(movieRepo.save(any())).then((invocation) -> {
            var movie = invocation.getArgument(0, Movie.class);
            resultMovie.set(movie);

            return movie;
        });

        when(settings.getMoviesFolder()).thenReturn("D:\\Videos");

        service.load("D:\\Videos\\Baby.Driver.2017.1080p.Bluray.x265");

        Assertions.assertAll(() -> {
            assertThat(resultMovie.get()).isNotNull();
            assertThat(resultMovie.get().getType()).isEqualTo(Movie.MovieType.Film);
            assertThat(resultMovie.get().getName()).isEqualTo("Baby.Driver.2017.1080p.Bluray.x265");
            assertThat(resultMovie.get().getPath()).isEqualTo("Baby.Driver.2017.1080p.Bluray.x265");
            assertThat(resultMovie.get().getTrailerPath()).isNull();
            assertThat(resultMovie.get().getFilmPath()).isEqualTo("Baby.Driver.2017.1080p.Bluray.x265.mkv");
            assertThat(resultMovie.get().getSize()).isEqualTo(5062776855L);
        });
    }

    @Test
    public void regularMovie_withoutTrailer_Test2(){
        AtomicReference<Movie> resultMovie = new AtomicReference<>();

        when(movieRepo.save(any())).then((invocation) -> {
            var movie = invocation.getArgument(0, Movie.class);
            resultMovie.set(movie);

            return movie;
        });

        when(settings.getMoviesFolder()).thenReturn("D:\\Videos");

        service.load("D:\\Videos\\Tetris.2023.1080p.WEBRip.x264-LAMA");

        Assertions.assertAll(() -> {
            assertThat(resultMovie.get()).isNotNull();
            assertThat(resultMovie.get().getType()).isEqualTo(Movie.MovieType.Film);
            assertThat(resultMovie.get().getName()).isEqualTo("Tetris.2023.1080p.WEBRip.x264-LAMA");
            assertThat(resultMovie.get().getPath()).isEqualTo("Tetris.2023.1080p.WEBRip.x264-LAMA");
            assertThat(resultMovie.get().getTrailerPath()).isNull();
            assertThat(resultMovie.get().getFilmPath()).isEqualTo("Tetris.2023.1080p.WEBRip.x264-LAMA.mp4");
            assertThat(resultMovie.get().getSize()).isEqualTo(2413763371L);
        });
    }

    @Test
    public void movieCollection_withTrailers_Test(){
        ArrayList<Movie> movies = new ArrayList<>();

        when(movieRepo.save(any())).then((invocation) -> {
            var movie = invocation.getArgument(0, Movie.class);
            if(movies.contains(movie))
                return movie;

            movies.add(movie);
            return movie;
        });

        when(settings.getMoviesFolder()).thenReturn("D:\\Videos");

        service.load("D:\\Videos\\The.Matrix.Collection.1080p.BluRay.x265.DD5.1-WAR");

        // Test 1st movie from collection
        Movie resultMovie1 = movies.stream().filter(m -> m.getName().equals("The.Matrix.1999.1080p.BluRay.x265.DD5.1-WAR"))
                .findFirst()
                .orElseThrow();

        Assertions.assertAll(() -> {
            assertThat(resultMovie1).isNotNull();
            assertThat(resultMovie1.getType()).isEqualTo(Movie.MovieType.Film);
            //assertThat(resultMovie1.getName()).isEqualTo("The.Matrix.1999.1080p.BluRay.x265.DD5.1-WAR");
            assertThat(resultMovie1.getPath()).isEqualTo("The.Matrix.Collection.1080p.BluRay.x265.DD5.1-WAR\\The.Matrix.1999.1080p.BluRay.x265.DD5.1-WAR");
            assertThat(resultMovie1.getTrailerPath()).isEqualTo("sample.mkv");
            assertThat(resultMovie1.getFilmPath()).isEqualTo("war-thematrix.mkv");
            assertThat(resultMovie1.getSize()).isEqualTo(4532892434L);
        });

        // Test 2nd movie from collection
        Movie resultMovie2 = movies.stream().filter(m -> m.getName().equals("The.Matrix.Reloaded.2003.1080p.BluRay.x265.DD5.1-WAR"))
                .findFirst()
                .orElseThrow();

        Assertions.assertAll(() -> {
            assertThat(resultMovie2).isNotNull();
            assertThat(resultMovie2.getType()).isEqualTo(Movie.MovieType.Film);
            //assertThat(resultMovie2.getName()).isEqualTo("The.Matrix.Reloaded.2003.1080p.BluRay.x265.DD5.1-WAR");
            assertThat(resultMovie2.getPath()).isEqualTo("The.Matrix.Collection.1080p.BluRay.x265.DD5.1-WAR\\The.Matrix.Reloaded.2003.1080p.BluRay.x265.DD5.1-WAR");
            assertThat(resultMovie2.getTrailerPath()).isEqualTo("sample.mkv");
            assertThat(resultMovie2.getFilmPath()).isEqualTo("war-mreloaded.mkv");
            assertThat(resultMovie2.getSize()).isEqualTo(4887740817L);
        });

        Assertions.assertEquals(4, movies.size());
    }

    @Test
    public void nestedMovie_withoutTrailer_Test(){
        AtomicReference<Movie> resultMovie = new AtomicReference<>();

        when(movieRepo.save(any())).then((invocation) -> {
            Movie movie = invocation.getArgument(0, Movie.class);
            resultMovie.set(movie);
            return movie;
        });

        when(settings.getMoviesFolder()).thenReturn("D:\\Videos");

        service.load("D:\\Videos\\TEST_NESTED");

        Assertions.assertAll(() -> {
            assertThat(resultMovie.get()).isNotNull();
            assertThat(resultMovie.get().getType()).isEqualTo(Movie.MovieType.Film);
            assertThat(resultMovie.get().getName()).isEqualTo("NESTED_FOLDER");
            assertThat(resultMovie.get().getPath()).isEqualTo("TEST_NESTED\\NESTED_FOLDER");
            assertThat(resultMovie.get().getTrailerPath()).isNull();
            assertThat(resultMovie.get().getFilmPath()).isEqualTo("cube snake.mkv");
            assertThat(resultMovie.get().getSize()).isEqualTo(16138966L);
        });
    }

    @Test
    public void regularSeries_withoutTrailer_Test(){
        AtomicReference<Movie> resultMovie = new AtomicReference<>();

        when(movieRepo.save(any())).then((invocation) -> {
            Movie movie = invocation.getArgument(0, Movie.class);
            movie.setId("850fd9g9b90gibgf0dju9");
            resultMovie.set(movie);
            return movie;
        });

        ArrayList<Episode> episodes = new ArrayList<>();
        when(episodeRepo.save(any())).then((invocation) -> {
            Episode episode = invocation.getArgument(0, Episode.class);
            episodes.add(episode);
            return episode;
        });

        when(settings.getMoviesFolder()).thenReturn("D:\\Videos");

        service.load("D:\\Videos\\Wednesday.S01.1080p.NF.WEBRip.DDP5.1.Atmos.x264-SMURF");

        Assertions.assertAll(() -> {
            assertThat(resultMovie.get()).isNotNull();
            assertThat(resultMovie.get().getType()).isEqualTo(Movie.MovieType.Series);
            assertThat(resultMovie.get().getName()).isEqualTo("Wednesday.S01.1080p.NF.WEBRip.DDP5.1.Atmos.x264-SMURF");
            assertThat(resultMovie.get().getPath()).isEqualTo("Wednesday.S01.1080p.NF.WEBRip.DDP5.1.Atmos.x264-SMURF");
            assertThat(resultMovie.get().getTrailerPath()).isNull();
            assertThat(resultMovie.get().getFilmPath()).isNull();
            assertThat(resultMovie.get().getSize()).isEqualTo(18812351667L);
        });

        Assertions.assertEquals(8, episodes.size());

        var numberlessEpisodes = episodes.stream().filter(e -> e.getEpisodeNumber() == 0).toList();
        Assertions.assertEquals(0, numberlessEpisodes.size());

        Episode episode = episodes.stream().filter(e -> e.getEpisodeNumber() == 1).findFirst()
                .orElseThrow();

        Assertions.assertAll(() -> {
            assertThat(episode.getSeriesId()).isEqualTo(resultMovie.get().getId());
            assertThat(episode.getEpisodePath()).isEqualTo("Wednesday.S01E01.Wednesdays.Child.is.Full.of.Woe.1080p.NF.WEB-DL.DDP5.1.Atmos.H.264-SMURF.mkv");
            assertThat(episode.getSeasonNumber()).isEqualTo(1);
        });
    }

    @Test
    public void regularSeries_multipleSeasons_inOneFolder_withTrailer_Test(){
        AtomicReference<Movie> resultMovie = new AtomicReference<>();

        when(movieRepo.save(any())).then((invocation) -> {
            Movie movie = invocation.getArgument(0, Movie.class);
            movie.setId("850fd9g9b90gibgf0dju9");
            resultMovie.set(movie);
            return movie;
        });

        ArrayList<Episode> episodes = new ArrayList<>();
        when(episodeRepo.save(any())).then((invocation) -> {
            Episode episode = invocation.getArgument(0, Episode.class);
            episodes.add(episode);
            return episode;
        });

        when(settings.getMoviesFolder()).thenReturn("D:\\Videos");

        service.load("D:\\Videos\\TEST_SERIES");

        Assertions.assertAll(() -> {
            assertThat(resultMovie.get()).isNotNull();
            assertThat(resultMovie.get().getType()).isEqualTo(Movie.MovieType.Series);
            assertThat(resultMovie.get().getName()).isEqualTo("TEST_SERIES");
            assertThat(resultMovie.get().getPath()).isEqualTo("TEST_SERIES");
            assertThat(resultMovie.get().getTrailerPath()).isEqualTo("sample.mkv");
            assertThat(resultMovie.get().getFilmPath()).isNull();
            assertThat(resultMovie.get().getSize()).isEqualTo(104L);
        });

        Assertions.assertEquals(5, episodes.size());

        var numberlessEpisodes = episodes.stream().filter(e -> e.getEpisodeNumber() == 0 || e.getSeasonNumber() == 0)
                .toList();
        Assertions.assertEquals(0, numberlessEpisodes.size());

        Episode episode_S01E03 = episodes.stream().filter(e -> e.getEpisodeNumber() == 3 && e.getSeasonNumber() == 1).findFirst()
                .orElseThrow();

        Assertions.assertAll(() -> {
            assertThat(episode_S01E03.getSeriesId()).isEqualTo(resultMovie.get().getId());
            assertThat(episode_S01E03.getEpisodePath()).isEqualTo("Нов текстов документ.S01E03.mkv");
        });

        Episode episode_S02E01 = episodes.stream().filter(e -> e.getEpisodeNumber() == 1 && e.getSeasonNumber() == 2).findFirst()
                .orElseThrow();

        Assertions.assertAll(() -> {
            assertThat(episode_S02E01.getSeriesId()).isEqualTo(resultMovie.get().getId());
            assertThat(episode_S02E01.getEpisodePath()).isEqualTo("Нов текстов документ.S02E01.mkv");
        });
    }

    @Test
    public void regularSeries_multipleSeasons_inSeparateFolders_withTrailer_Test(){
        ArrayList<Movie> movies = new ArrayList<>();
        when(movieRepo.save(any())).then((invocation) -> {
            Movie movie = invocation.getArgument(0, Movie.class);
            if(movies.contains(movie))
                return movie;

            movie.setId("850fd9g9b90gibgf0dju9" + Math.random());
            movies.add(movie);
            return movie;
        });

        ArrayList<Episode> episodes = new ArrayList<>();
        when(episodeRepo.save(any())).then((invocation) -> {
            Episode episode = invocation.getArgument(0, Episode.class);
            episodes.add(episode);
            return episode;
        });

        when(settings.getMoviesFolder()).thenReturn("D:\\Videos");

        service.load("D:\\Videos\\TEST_SERIES_SEP_FOLDER");

        // Test 1st movie from collection
        Movie resultMovie1 = movies.stream().filter(m -> m.getName().equals("Season 1")).findFirst()
                .orElseThrow();

        Assertions.assertAll(() -> {
            assertThat(resultMovie1).isNotNull();
            assertThat(resultMovie1.getType()).isEqualTo(Movie.MovieType.Series);
            assertThat(resultMovie1.getName()).isEqualTo("Season 1");
            assertThat(resultMovie1.getPath()).isEqualTo("TEST_SERIES_SEP_FOLDER\\Season 1");
            assertThat(resultMovie1.getTrailerPath()).isEqualTo("sample.mkv");
            assertThat(resultMovie1.getFilmPath()).isNull();
            assertThat(resultMovie1.getSize()).isEqualTo(80L);
        });

        // Test 2nd movie from collection
        Movie resultMovie2 = movies.stream().filter(m -> m.getName().equals("Season 2")).findFirst()
                .orElseThrow();

        Assertions.assertAll(() -> {
            assertThat(resultMovie2).isNotNull();
            assertThat(resultMovie2.getType()).isEqualTo(Movie.MovieType.Series);
            assertThat(resultMovie2.getName()).isEqualTo("Season 2");
            assertThat(resultMovie2.getPath()).isEqualTo("TEST_SERIES_SEP_FOLDER\\Season 2");
            assertThat(resultMovie2.getTrailerPath()).isEqualTo("sample.mkv");
            assertThat(resultMovie2.getFilmPath()).isNull();
            assertThat(resultMovie2.getSize()).isEqualTo(58L);
        });

        Assertions.assertEquals(2, movies.size());

        // Test episodes
        Assertions.assertEquals(5, episodes.size());

        var numberlessEpisodes = episodes.stream().filter(e -> e.getEpisodeNumber() == 0 || e.getSeasonNumber() == 0)
                .toList();
        Assertions.assertEquals(0, numberlessEpisodes.size());

        Episode episode_S01E03 = episodes.stream().filter(e -> e.getEpisodeNumber() == 3 && e.getSeasonNumber() == 1).findFirst()
                .orElseThrow();

        Assertions.assertAll(() -> {
            assertThat(episode_S01E03.getSeriesId()).isEqualTo(resultMovie1.getId());
            assertThat(episode_S01E03.getEpisodePath()).isEqualTo("Нов текстов документ.S01E03.mkv");
        });

        Episode episode_S02E01 = episodes.stream().filter(e -> e.getEpisodeNumber() == 1 && e.getSeasonNumber() == 2).findFirst()
                .orElseThrow();

        Assertions.assertAll(() -> {
            assertThat(episode_S02E01.getSeriesId()).isEqualTo(resultMovie2.getId());
            assertThat(episode_S02E01.getEpisodePath()).isEqualTo("Нов текстов документ.S02E01.mkv");
        });
    }

}