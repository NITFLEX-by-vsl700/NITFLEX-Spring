package com.vsl700.nitflex.services.implementations;

import com.vsl700.nitflex.components.Settings;
import com.vsl700.nitflex.models.Episode;
import com.vsl700.nitflex.models.Movie;
import com.vsl700.nitflex.repo.EpisodeRepository;
import com.vsl700.nitflex.repo.MovieRepository;
import com.vsl700.nitflex.services.MovieLoaderService;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MovieLoaderServiceImpl implements MovieLoaderService {

    private MovieRepository movieRepo;

    private EpisodeRepository episodeRepo;

    private Settings settings;

    private final BiFunction<String, String, String> relativizePaths =
            (homePath, fullPath) -> Paths.get(homePath).relativize(Paths.get(fullPath)).toString();

    public MovieLoaderServiceImpl(MovieRepository movieRepo, EpisodeRepository episodeRepo, Settings settings) {
        this.movieRepo = movieRepo;
        this.episodeRepo = episodeRepo;
        this.settings = settings;
    }

    @Override
    public void load(String path) {
        // Check if it is either a collection or a nested folder
        var allFiles = getFiles(path, (dir, name) -> true, false);
        var dirs = allFiles.stream().filter(File::isDirectory).toList();
        long dirsCount = dirs.size();
        long moviesCount = getFiles(path, (dir, name) -> !isTrailer(name) && isVideoFile(name), false)
                .stream().filter(File::isFile).count();
        if(dirsCount >= 1 && moviesCount == 0){
            // Handle the collection/nested folder
            // Each folder is treated as a separate Movie
            dirs.forEach(f -> load(f.getAbsolutePath()));
            return;
        }

        // Determine movie type
        Movie.MovieType type;
        if(moviesCount > 1){
            type = Movie.MovieType.Series;
        }else{
            type = Movie.MovieType.Film;
        }

        // Create Movie object
        String relativePath = relativizePaths
                .apply(settings.getMoviesFolder(), path);
        String name = relativePath.substring(relativePath.lastIndexOf("\\") + 1);
        Movie movie = new Movie(name, type, relativePath);

        movie = movieRepo.save(movie); // Obtain a Movie.Id

        // Load the Movie
        switch(type){
            case Film -> loadFilm(movie, path);
            case Series -> loadSeries(movie, path);
        }

        // Save the Movie
        movieRepo.save(movie);
    }

    private void loadFilm(Movie movie, String absPath) {
        // Film path (MKV file)
        getFilePaths(absPath, (dir, name) ->
                !isTrailer(name) && isVideoFile(name)
        , true).stream().findFirst().ifPresentOrElse(movie::setFilmPath,
                () -> { throw new UnsupportedOperationException("Movie \"%s\" has no film file!".formatted(movie.getName())); });

        // Trailer path (if trailer is present)
        getFilePaths(absPath, (dir, name) ->
                isTrailer(name)
        , true).stream().findFirst().ifPresent(movie::setTrailerPath);
    }

    private void loadSeries(Movie movie, String absPath) {
        // Trailer path (if trailer is present)
        getFilePaths(absPath, (dir, name) ->
                        isTrailer(name)
                , true).stream().findFirst().ifPresent(movie::setTrailerPath);

        // Load episodes
        var episodePaths = getFilePaths(absPath, (dir, name) ->
                        !isTrailer(name) && isVideoFile(name)
                , true).stream().toList();

        episodePaths.forEach(episodePath -> {
            int episodeNumber = getEpisodeNumber(episodePath);

            Episode episode = new Episode(movie.getId(), episodeNumber, episodePath);
            episodeRepo.save(episode);
        });
    }

    private int getEpisodeNumber(String episodePath){
        String seasonEpisode = getMatcher(episodePath, "S\\d+E\\d+");

        if(seasonEpisode == null)
            return 0;

        String episode = seasonEpisode.substring(seasonEpisode.indexOf("E") + 1);

        return Integer.parseInt(episode);
    }

    private String getMatcher(String text, String... regexes){
        for(String regex : regexes){
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(text);

            if(matcher.find())
                return matcher.group();
        }

        return null;
    }

    private boolean isVideoFile(String fileName){
        return fileName.endsWith(".mp4") || fileName.endsWith(".mkv") || fileName.endsWith(".avi");
    }

    private boolean isTrailer(String fileName){
        return fileName.equals("sample.mkv")
                || fileName.equals("sample.mp4")
                || fileName.equals("sample.avi");
    }

    private List<String> getFilePaths(String parentPath, FilenameFilter filenameFilter, boolean checkNestedFiles){
        return getFiles(parentPath, filenameFilter, checkNestedFiles).stream().map(f -> relativizePaths.apply(parentPath, f.getAbsolutePath())).toList();
    }

    private List<File> getFiles(String parentPath, FilenameFilter filenameFilter, boolean checkNestedFiles){
        File file = new File(parentPath);

        var listFiles = file.listFiles(filenameFilter);
        if(listFiles == null)
            return List.of();

        List<File> result = new ArrayList<>(List.of(listFiles));

        if(!checkNestedFiles)
            return result;

        result.stream().filter(File::isDirectory).toList().forEach(e -> {
            result.addAll(getFiles(e.getAbsolutePath(), filenameFilter, checkNestedFiles).stream().toList());
        });

        return result;
    }
}
