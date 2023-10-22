package com.vsl700.nitflex.services.implementations;

import com.vsl700.nitflex.components.Settings;
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

@Service
public class MovieLoaderServiceImpl implements MovieLoaderService {

    @Autowired
    private MovieRepository movieRepo;

    @Autowired
    private EpisodeRepository episodeRepo;

    @Autowired
    private Settings settings;

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
            dirs.forEach(d -> load(d.getAbsolutePath()));
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
        String name = Paths.get(settings.getMoviesFolder()).relativize(Paths.get(path)).toString();
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
                !isTrailer(name)
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

    }

    private boolean isVideoFile(String fileName){
        return fileName.endsWith(".mp4") || fileName.endsWith(".mkv") || fileName.endsWith(".avi");
    }

    private boolean isTrailer(String fileName){
        return fileName.equals("sample.mkv")
                || fileName.equals("sample.mp4")
                || fileName.equals("sample.avi");
    }

    private List<String> getFilePaths(String parentPath, FilenameFilter filenameFilter, boolean recursion){
        return getFiles(parentPath, filenameFilter, recursion).stream().map(File::getAbsolutePath).toList();
    }

    private List<File> getFiles(String parentPath, FilenameFilter filenameFilter, boolean recursion){
        File file = new File(parentPath);

        List<File> temp = List.of(Objects.requireNonNull(file.listFiles(filenameFilter)));
        List<File> result = new ArrayList<>(temp);

        result.stream().filter(File::isDirectory).forEach(e -> {
            if(e.isDirectory() && recursion){
                result.addAll(getFiles(e.getAbsolutePath(), filenameFilter, recursion).stream().toList());
            }else{
                result.add(e);
            }
        });

        return result;
    }
}
