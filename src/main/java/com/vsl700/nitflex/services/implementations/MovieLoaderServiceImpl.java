package com.vsl700.nitflex.services.implementations;

import com.vsl700.nitflex.components.Settings;
import com.vsl700.nitflex.models.Episode;
import com.vsl700.nitflex.models.Movie;
import com.vsl700.nitflex.repo.EpisodeRepository;
import com.vsl700.nitflex.repo.MovieRepository;
import com.vsl700.nitflex.services.MovieLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MovieLoaderServiceImpl implements MovieLoaderService {

    @Autowired
    private MovieRepository movieRepo;

    @Autowired
    private EpisodeRepository episodeRepo;

    @Autowired
    private Settings settings;

    private BiFunction<String, String, String> relativizePaths =
            (homePath, fullPath) -> Paths.get(homePath).relativize(Paths.get(fullPath)).toString();

    @Override
    public void load(String path) {
        // Check if it is a collection
        var allFiles = getFiles(path, (dir, name) -> true, false);
        var dirs = allFiles.stream().filter(File::isDirectory).toList();
        long dirsCount = dirs.size();
        long moviesCount = getFiles(path, (dir, name) -> !isTrailer(name) && isVideoFile(name), false)
                .stream().filter(File::isFile).count();
        if(dirsCount >= 2 && moviesCount == 0){
            // Handle the collection
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
        String name = relativizePaths
                .apply(settings.getMoviesFolder(), path);
        Movie movie = new Movie(name, type, path);

        // Load the Movie
        switch(type){
            case Film -> loadFilm(movie);
            case Series -> loadSeries(movie);
        }

        // Save the Movie
        movieRepo.save(movie);
    }

    private void loadFilm(Movie movie) {
        String parentPath = movie.getPath();

        // Film path (MKV file)
        getFilePaths(parentPath, (dir, name) ->
                !isTrailer(name) && isVideoFile(name)
        , true).stream().findFirst().ifPresentOrElse(movie::setFilmPath,
                () -> { throw new UnsupportedOperationException("Movie \"%s\" has no film file!".formatted(movie.getName())); });

        // Trailer path (if trailer is present)
        getFilePaths(parentPath, (dir, name) ->
                isTrailer(name)
        , true).stream().findFirst().ifPresent(movie::setTrailerPath);
    }

    private void loadSeries(Movie movie) {
        String parentPath = movie.getPath();

        // Trailer path (if trailer is present)
        getFilePaths(parentPath, (dir, name) ->
                        isTrailer(name)
                , true).stream().findFirst().ifPresent(movie::setTrailerPath);

        // Load episodes
        var episodePaths = getFilePaths(parentPath, (dir, name) ->
                        !isTrailer(name) && isVideoFile(name)
                , true).stream().map(e -> relativizePaths.apply(parentPath, e)).toList();

        episodePaths.forEach(episodePath -> {
            int episodeNumber = getEpisodeNumber(episodePath);

            Episode episode = new Episode(movie.getId(), episodeNumber, episodePath);
            episodeRepo.save(episode);
        });
    }

    private int getEpisodeNumber(String episodePath){
        String seasonEpisode = getMatcher(episodePath, "S\\d\\dE\\d\\d",
                "S\\d\\dE\\d\\d\\d", "S\\d\\dE\\d\\d\\d\\d");

        if(seasonEpisode == null)
            throw new UnsupportedOperationException("%s: Episode has no number!");

        String episode = seasonEpisode.substring(seasonEpisode.indexOf("E") + 1);

        return Integer.getInteger(episode);
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
        return getFiles(parentPath, filenameFilter, checkNestedFiles).stream().map(File::getAbsolutePath).toList();
    }

    private List<File> getFiles(String parentPath, FilenameFilter filenameFilter, boolean checkNestedFiles){
        File file = new File(parentPath);

        List<File> temp = List.of(Objects.requireNonNull(file.listFiles(filenameFilter)));
        List<File> result = new ArrayList<>(temp);

        result.stream().filter(File::isDirectory).forEach(e -> {
            if(e.isDirectory() && checkNestedFiles){
                result.addAll(getFiles(e.getAbsolutePath(), filenameFilter, checkNestedFiles).stream().toList());
            }else{
                result.add(e);
            }
        });

        return result;
    }
}
