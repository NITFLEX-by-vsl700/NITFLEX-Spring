package com.vsl700.nitflex.services;

public interface MovieDownloaderService {
    void downloadFromPageURL(String pageUrl);
    void downloadFromTorrentFilePath(String torrentFilePath);
}
