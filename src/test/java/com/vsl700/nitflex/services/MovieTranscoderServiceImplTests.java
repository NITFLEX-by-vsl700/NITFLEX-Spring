package com.vsl700.nitflex.services;

import com.vsl700.nitflex.components.SharedProperties;
import com.vsl700.nitflex.models.Episode;
import com.vsl700.nitflex.models.Movie;
import com.vsl700.nitflex.models.Subtitle;
import com.vsl700.nitflex.repo.EpisodeRepository;
import com.vsl700.nitflex.repo.MovieRepository;
import com.vsl700.nitflex.repo.SubtitleRepository;
import com.vsl700.nitflex.services.implementations.MovieTranscoderServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Disabled
@ExtendWith(MockitoExtension.class)
public class MovieTranscoderServiceImplTests {
    private MovieTranscoderServiceImpl movieTranscoderService;

    private MovieRepository movieRepo;
    private EpisodeRepository episodeRepo;
    private SubtitleRepository subtitleRepo;
    private SharedProperties sharedProperties;

    private ArrayList<Movie> movies;
    private ArrayList<Episode> episodes;
    private ArrayList<Subtitle> subtitles;

    @BeforeEach
    public void setUp(){
        movieRepo = mock(MovieRepository.class,
                Mockito.withSettings().useConstructor()
                        .defaultAnswer(CALLS_REAL_METHODS)
                        .strictness(Strictness.LENIENT));
        episodeRepo = mock(EpisodeRepository.class,
                Mockito.withSettings().useConstructor()
                        .defaultAnswer(CALLS_REAL_METHODS)
                        .strictness(Strictness.LENIENT));
        subtitleRepo = mock(SubtitleRepository.class,
                Mockito.withSettings().useConstructor()
                        .defaultAnswer(CALLS_REAL_METHODS)
                        .strictness(Strictness.LENIENT));
        sharedProperties = mock(SharedProperties.class,
                Mockito.withSettings().useConstructor()
                        .defaultAnswer(CALLS_REAL_METHODS)
                        .strictness(Strictness.LENIENT));

        movieTranscoderService = mock(MovieTranscoderServiceImpl.class,
                Mockito.withSettings().useConstructor(movieRepo, episodeRepo, subtitleRepo, sharedProperties)
                        .defaultAnswer(CALLS_REAL_METHODS)
                        .strictness(Strictness.LENIENT));

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

        when(episodeRepo.findAllBySeriesId(anyString())).then(invocation -> {
            String seriesId = invocation.getArgument(0, String.class);
            return episodes.stream().filter(e -> e.getSeriesId().equals(seriesId)).toList();
        });

        subtitles = new ArrayList<>();
        when(subtitleRepo.save(any())).then((invocation) -> {
            Subtitle subtitle = invocation.getArgument(0, Subtitle.class);
            subtitles.add(subtitle);
            return subtitle;
        });

        doAnswer(invocation -> {
            String movieId = invocation.getArgument(0, String.class);
            subtitles.removeIf(s -> s.getMovieId().equals(movieId));

            return null;
        }).when(subtitleRepo).deleteByMovieId(any(String.class));

        when(subtitleRepo.findAllByMovieId(anyString())).then(invocation -> {
            String movieId = invocation.getArgument(0, String.class);
            return subtitles.stream().filter(s -> s.getMovieId().equals(movieId)).toList();
        });

        when(subtitleRepo.findByPath(anyString())).then(invocation -> {
            String path = invocation.getArgument(0, String.class);
            return subtitles.stream().filter(s -> s.getPath().equals(path)).findFirst();
        });

        when(sharedProperties.getMoviesFolder()).thenReturn("D:\\Videos\\nitflex");
    }

    @Test
    public void film_withTrailer_Test(){
        Movie movie = new Movie("filmTrailer", Movie.MovieType.Film, "filmTrailer", 10L);
        movie.setFilmPath("2024-01-28 14-35-19.mkv");
        movie.setTrailerPath("sample.mkv");
        movies.add(movie);

        movieTranscoderService.transcode(movie);

        Assertions.assertAll(() -> {
            assertThat(movie.isTranscoded()).isTrue();
            assertThat(movie.getTrailerPath()).isEqualTo("sample");
            assertThat(movie.getFilmPath()).isEqualTo("2024-01-28 14-35-19");
            assertThat(movie.getSize()).isEqualTo(429472L);
        });
    }

    @Test
    public void film_withTrailer_MPDValidation_Test() throws IOException {
        Movie movie = new Movie("filmTrailer", Movie.MovieType.Film, "filmTrailer", 10L);
        movie.setFilmPath("2024-01-28 14-35-19.mkv");
        movie.setTrailerPath("sample.mkv");
        movies.add(movie);

        movieTranscoderService.transcode(movie);

        String filmManifestContent = Files.readString(Path.of("D:\\Videos\\nitflex\\filmTrailer\\2024-01-28 14-35-19\\manifest.mpd"));
        String trailerManifestContent = Files.readString(Path.of("D:\\Videos\\nitflex\\filmTrailer\\sample\\manifest.mpd"));

        Assertions.assertAll(() -> {
            assertThat(countWordOccurrences(filmManifestContent, "initialization=\"init-$RepresentationID$.m4s\"")).isEqualTo(2);
            assertThat(countWordOccurrences(filmManifestContent, "media=\"chunk-$RepresentationID$-$Number%05d$.m4s\"")).isEqualTo(2);
            assertThat(countWordOccurrences(trailerManifestContent, "initialization=\"init-$RepresentationID$.m4s\"")).isEqualTo(2);
            assertThat(countWordOccurrences(trailerManifestContent, "media=\"chunk-$RepresentationID$-$Number%05d$.m4s\"")).isEqualTo(2);
        });
    }

    @Test
    public void film_withSubtitles_Test(){
        Movie movie = new Movie("filmSubtitles", Movie.MovieType.Film, "filmSubtitles", 10L);
        movie.setFilmPath("2024-01-28 14-35-19.mkv");
        movieRepo.save(movie);

        Subtitle nestedSubtitle1 = new Subtitle(movie.getId(), "English", "Subs\\English.srt");
        Subtitle nestedSubtitle2 = new Subtitle(movie.getId(), "French", "Subs\\French.srt");
        Subtitle nestedSubtitle3 = new Subtitle(movie.getId(), "Chinese", "Subs\\Chinese.srt");
        Subtitle subtitle = new Subtitle(movie.getId(), "2024-01-28 14-35-19", "2024-01-28 14-35-19.srt");
        subtitleRepo.save(nestedSubtitle1);
        subtitleRepo.save(nestedSubtitle2);
        subtitleRepo.save(nestedSubtitle3);
        subtitleRepo.save(subtitle);

        movieTranscoderService.transcode(movie);

        Assertions.assertAll(() -> {
            assertThat(movie.isTranscoded()).isTrue();
            assertThat(movie.getTrailerPath()).isNull();
            assertThat(movie.getFilmPath()).isEqualTo("2024-01-28 14-35-19");
            assertThat(movie.getSize()).isEqualTo(208592L);

            assertThat(nestedSubtitle1.getPath()).isEqualTo("Subs\\English.vtt");
            assertThat(nestedSubtitle2.getPath()).isEqualTo("Subs\\French.vtt");
            assertThat(nestedSubtitle3.getPath()).isEqualTo("Subs\\Chinese.vtt");
            assertThat(subtitle.getPath()).isEqualTo("2024-01-28 14-35-19.vtt");
        });
    }

    /**
     * Counts the occurences of a word. Every word must be space-separated!
     * @param str the string to count word occurrences from
     * @param word the word which occurrence we want to count
     * @return the amount of occurrences of the given word in the given string
     */
    private int countWordOccurrences(String str, String word) {
        String[] words = str.split(" ");

        int count = 0;
        for (String s : words) {
            if (word.equals(s))
                count++;
        }

        return count;
    }
}
