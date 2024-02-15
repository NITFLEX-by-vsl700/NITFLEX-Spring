package com.vsl700.nitflex.services.implementations;

import com.vsl700.nitflex.models.Episode;
import com.vsl700.nitflex.models.Movie;
import com.vsl700.nitflex.models.Subtitle;
import com.vsl700.nitflex.repo.EpisodeRepository;
import com.vsl700.nitflex.repo.MovieRepository;
import com.vsl700.nitflex.repo.SubtitleRepository;
import com.vsl700.nitflex.services.MovieTranscoderService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import ws.schild.jave.DefaultFFMPEGLocator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MovieTranscoderServiceImpl implements MovieTranscoderService {
    private static final String extractedSubtitlesNameStandard = "extracted_subtitles_%d.vtt";
    private static final String videoFileTranscodeCommandTemplate = "-i \"%s\" -c:a aac -c:v libx264 -pix_fmt yuv420p -vf format=yuv420p -ac 2 -map 0:v:0 -map 0:a -adaptation_sets \"id=0,streams=v id=1,streams=a\" -f dash -init_seg_name \"%s^init-$RepresentationID$.m4s\" -media_seg_name \"%s^chunk-$RepresentationID$-$Number%s$.m4s\" \"%s^manifest.mpd\""
            .replace("^", File.pathSeparator);
    private static final String subtitleStreamTranscodeCommandTemplate = "-i \"%s\" -vn -an -c:s webvtt -map 0:s:%s -f webvtt \"%s^%s\""
            .formatted("%s", "%d", "%s", extractedSubtitlesNameStandard)
            .replace("^", File.pathSeparator);
    private static final String subtitleFileTranscodeCommandTemplate = "-i \"%s\" -c:s webvtt -f webvtt \"%s\"";

    private MovieRepository movieRepo;
    private EpisodeRepository episodeRepo;
    private SubtitleRepository subtitleRepo;

    @Override
    public void transcode(Movie movie) {
        if(movie.isTranscoded())
            return;

        // Save all original video and subtitle file paths
        List<String> videoFiles = new ArrayList<>();
        List<String> subtitleFiles = subtitleRepo.findAllByMovieId(movie.getId()).stream().map(Subtitle::getPath).toList();
        switch (movie.getType()){
            case Film -> videoFiles.add(movie.getFilmPath());
            case Series -> videoFiles.addAll(episodeRepo.findAllBySeriesId(movie.getId()).stream().map(Episode::getEpisodePath).toList());
        }

        if(movie.getTrailerPath() != null)
            videoFiles.add(movie.getTrailerPath());

        // Transcode subtitle files
        transcodeSubtitles(movie);

        // Transcoding the video files
        switch (movie.getType()){
            case Film -> transcodeFilm(movie);
            case Series -> transcodeSeries(movie);
        }

        // Assign Movie as transcoded
        movie.setTranscoded(true);
        movieRepo.save(movie);

        // Remove original files
        videoFiles.forEach(p -> {
            try {
                Files.delete(Path.of(p));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        subtitleFiles.forEach(p -> {
            try {
                Files.delete(Path.of(p));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void transcodeFilm(Movie movie){
        // Transcode subtitles from film file
        extractAndSaveSubtitlesFromVideoFile(movie.getFilmPath(), movie.getId());

        // Transcoding the film file
        movie.setFilmPath(transcodeVideoFile(movie.getFilmPath()));

        // If there's no trailer, return
        if(movie.getTrailerPath() == null)
            return;

        // Transcode subtitles from trailer file
        extractAndSaveSubtitlesFromVideoFile(movie.getTrailerPath(), movie.getId());

        // Transcoding the trailer file
        movie.setTrailerPath(transcodeVideoFile(movie.getTrailerPath()));
    }

    private void transcodeSeries(Movie movie){
        // Transcoding each episode
        episodeRepo.findAllBySeriesId(movie.getId()).forEach(e -> {
            // Transcode subtitles from episode file
            extractAndSaveSubtitlesFromVideoFile(e.getEpisodePath(), movie.getId());

            // Transcode episode file
            if(!isPathOfTranscodedVideoFile(e.getEpisodePath())) {
                e.setEpisodePath(transcodeVideoFile(e.getEpisodePath()));
                episodeRepo.save(e);
            }
        });

        // If there's no trailer, return
        if(movie.getTrailerPath() == null)
            return;

        // Transcode subtitles from trailer file
        extractAndSaveSubtitlesFromVideoFile(movie.getTrailerPath(), movie.getId());

        // Transcoding the trailer file
        movie.setTrailerPath(transcodeVideoFile(movie.getTrailerPath()));
    }

    private void transcodeSubtitles(Movie movie){
        subtitleRepo.findAllByMovieId(movie.getId()).stream()
                .filter(s -> isPathOfTranscodedSubtitleFile(s.getPath()))
                .forEach(s -> {
                    s.setPath(transcodeSubtitleFile(s.getPath()));
                    subtitleRepo.save(s);
                });
    }

    private void extractAndSaveSubtitlesFromVideoFile(String path, String movieId){
        String outputVideoFilePath = generateVideoFileOutputPath(path);
        String outputSubtitleFilePath;
        for(int i = 0; subtitleRepo.findByPath(outputSubtitleFilePath = Path.of(outputVideoFilePath, extractedSubtitlesNameStandard.formatted(i)).toString()).isEmpty() && extractAndTranscodeSubtitlesFromVideoFile(path, i); i++){
            Subtitle extractedSubtitle = new Subtitle(movieId, "Extracted (%d)".formatted(i), outputSubtitleFilePath);
            subtitleRepo.save(extractedSubtitle);
        }
    }

    private boolean isPathOfTranscodedVideoFile(String path){ // TODO Improve the method's logic
        return Files.isDirectory(Path.of(path));
    }

    private boolean isPathOfTranscodedSubtitleFile(String path){
        return path.endsWith(".vtt");
    }

    private String generateVideoFileOutputPath(String path){
        return createDirAndReturn(path.substring(0, path.lastIndexOf('.')));
    }

    private String transcodeVideoFile(String path){
        String outputPath = generateVideoFileOutputPath(path);

        int exitCode = execFFmpegCommand(videoFileTranscodeCommandTemplate.formatted(path, outputPath, outputPath, "%05d", outputPath));
        if(exitCode != 0)
            throw new RuntimeException("Transcoding of video file \"%s\" failed!".formatted(path)); // TODO Add custom exception

        return outputPath;
    }

    private String transcodeSubtitleFile(String path){
        String outputPath = path.substring(0, path.lastIndexOf('.')) + ".vtt";

        int exitCode = execFFmpegCommand(subtitleFileTranscodeCommandTemplate.formatted(path, outputPath));
        if(exitCode != 0)
            throw new RuntimeException("Transcoding of subtitle file \"%s\" failed!".formatted(path)); // TODO Add custom exception

        return outputPath;
    }

    private boolean extractAndTranscodeSubtitlesFromVideoFile(String path, int subtitleStreamNumber){
        String outputPathStr = generateVideoFileOutputPath(path);

        // Remove output file if already exists (to prevent FFmpeg executable from crashing)
        Path outputPath = Path.of(outputPathStr);
        if(Files.exists(outputPath)) {
            try {
                Files.delete(outputPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return execFFmpegCommand(subtitleStreamTranscodeCommandTemplate.formatted(path, subtitleStreamNumber, outputPathStr, subtitleStreamNumber)) == 0;
    }

    @SneakyThrows
    private int execFFmpegCommand(String options) {
        // Get the path of the FFmpeg executable, provided by the Jave2 library and appropriate for the OS
        Path executablePath = Path.of(new DefaultFFMPEGLocator().getFFMPEGExecutablePath());

        // Create the FFmpeg process by invoking the executable with the provided options
        List<String> commandPieces = new ArrayList<>();
        commandPieces.add(executablePath.toString());
        commandPieces.addAll(List.of(options.split(" ")));

        ProcessBuilder pb = new ProcessBuilder(commandPieces);
        Process process = pb.start();

        // Terminate the child process when the Java application is being terminated
        Runtime.getRuntime().addShutdownHook(new Thread(process::destroy));

        String line;
        BufferedReader reader2 = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        while ((line = reader2.readLine()) != null) {
            System.out.println(line);
        }
        reader2.close();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        reader.close();

        return process.waitFor();
    }

    private String createDirAndReturn(String path){
        File file = new File(path);
        if(!file.exists() && file.mkdir())
            throw new RuntimeException("Directory at %s could not be created!".formatted(path)); // TODO Change that or add custom exception
        return path;
    }
}