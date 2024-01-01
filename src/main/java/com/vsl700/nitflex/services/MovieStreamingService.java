package com.vsl700.nitflex.services;

import java.nio.file.Path;
import java.util.List;

public interface MovieStreamingService {
    List<byte[]> grabFrames(Path moviePath, int beginFrame, int length);
    List<short[]> grabAudioFromFrames(Path moviePath, int beginFrame, int length);
    long getMovieDuration(Path moviePath);
    int getMovieFramesCount(Path moviePath);
}
