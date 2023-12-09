package com.vsl700.nitflex.components;

import com.vsl700.nitflex.services.MovieLoaderService;
import com.vsl700.nitflex.services.MovieSeekerService;
import com.vsl700.nitflex.services.URLMovieDownloaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class AutoMovieDownloader {
    private static final Logger LOG = LoggerFactory.getLogger(AutoMovieDownloader.class);

    @Autowired
    private MovieSeekerService movieSeekerService;
    @Autowired
    private URLMovieDownloaderService urlMovieDownloaderService;
    @Autowired
    private MovieLoaderService movieLoaderService;

    @Scheduled(initialDelayString = "${nitflex.download-interval}",
            fixedRateString = "${nitflex.download-interval}",
            timeUnit = TimeUnit.DAYS)
    public void run(){
        LOG.info("Auto-download triggered!");
        movieLoaderService.load(urlMovieDownloaderService.downloadFromPageURL(movieSeekerService.findMovieURL()));
    }
}
