package com.vsl700.nitflex.services;

public interface MovieDownloaderService {
    default void downloadFromPageURLAsync(String pageUrl, Runnable onDownloadFinished){
        new Thread(() -> {
            downloadFromPageURL(pageUrl);
            onDownloadFinished.run();
        }).start();
    }

    void downloadFromPageURL(String pageUrl);

    default void downloadFromTorrentFilePathAsync(String torrentFilePath, Runnable onDownloadFinished){
        new Thread(() -> {
            downloadFromTorrentFilePath(torrentFilePath);
            onDownloadFinished.run();
        }).start();
    }

    void downloadFromTorrentFilePath(String torrentFilePath);
}
