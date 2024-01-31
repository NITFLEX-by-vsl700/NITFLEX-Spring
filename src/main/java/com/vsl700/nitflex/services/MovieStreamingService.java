package com.vsl700.nitflex.services;

import java.nio.file.Path;
import java.util.List;

public interface MovieStreamingService {
    byte[] grabFramesAsMP4(Path moviePath, int beginFrame, int length);
    List<byte[]> grabFrames(Path moviePath, int beginFrame, int length);
    List<short[]> grabAudioFromFrames(Path moviePath, int beginFrame, int length);
    long getMovieDuration(Path moviePath);
    int getMovieFramesCount(Path moviePath);
    int getMovieImageWidth(Path moviePath);
    int getMovieImageHeight(Path moviePath);
    double getMovieFrameRate(Path moviePath);
}
