package com.vsl700.nitflex.services;

import java.io.File;
import java.util.function.Consumer;

public interface TorrentMovieDownloaderService {
    /**
     * Downloads a movie from the given torrent file at the specified path asynchronously, and then invokes the
     * given Consumer method.
     * @param torrentFile the file path of the torrent file to download with
     * @param onDownloadFinished the method to be invoked after movie download completes. Arguments accepted:<br>
     *                           <li> movieFolder - the just-downloaded movie's folder (absolute path)
     */
    default void downloadFromTorrentFilePathAsync(File torrentFile, Consumer<String> onDownloadFinished){
        new Thread(() -> {
            String movieFolder = downloadFromTorrentFilePath(torrentFile);
            onDownloadFinished.accept(movieFolder);
        }).start();
    }

    /**
     * Downloads a movie from the given torrent file at the specified path
     * @param torrentFile the file path of the torrent file to download with
     * @return the full path to the newly downloaded movie
     */
    String downloadFromTorrentFilePath(File torrentFile);
}
