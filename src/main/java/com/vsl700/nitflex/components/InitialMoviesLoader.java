package com.vsl700.nitflex.components;

import com.vsl700.nitflex.models.Movie;
import com.vsl700.nitflex.repo.EpisodeRepository;
import com.vsl700.nitflex.repo.MovieRepository;
import com.vsl700.nitflex.services.MovieLoaderService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

// Here goes movies discovering & loading on server start up
@Component
public class InitialMoviesLoader {
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private EpisodeRepository episodeRepository;

    @PostConstruct
    public void init(MovieLoaderService movieLoaderService, SharedProperties sharedProperties){

        System.out.println("CLR2");

        // TESTING
        //movieRepository.deleteAll();
        //episodeRepository.deleteAll();
        // TESTING


        File file = new File(sharedProperties.getMoviesFolder());
        List<File> movieFolders = Arrays.stream(Objects.requireNonNull(file.listFiles()))
                .filter(File::isDirectory)
                .filter(f -> Objects.requireNonNull(f.listFiles()).length != 0)
                .toList();

        // FIXME: THE OPERATIONS BELOW (adding and removing movies from db) DON'T WORK RIGHT WITH COLLECTIONS!

        // Remove records of no longer existing movies
        List<Movie> movieRecords = movieRepository.findAll().stream()
                .filter(m -> movieFolders.stream()
                        .noneMatch(mf -> Paths.get(file.getAbsolutePath()).relativize(Paths.get(mf.getAbsolutePath()))
                                .toString().equals(m.getPath())))
                .toList();
        movieRepository.deleteAll(movieRecords);
        movieRecords.stream()
                .filter(m -> m.getType().equals(Movie.MovieType.Series))
                .forEach(m -> episodeRepository.deleteBySeriesId(m.getId()));

        // Register new-found movies
        movieFolders.stream()                           // Have to give relativized path every time!
                .filter(f -> movieRepository.findByPath(Paths.get(file.getAbsolutePath()).relativize(Paths.get(f.getAbsolutePath())).toString()).isEmpty())
                .forEach(f -> movieLoaderService.load(f.getAbsolutePath()));

    }
}
