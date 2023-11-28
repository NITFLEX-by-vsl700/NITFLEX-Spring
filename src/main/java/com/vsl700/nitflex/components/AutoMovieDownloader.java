package com.vsl700.nitflex.components;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class AutoMovieDownloader {
    @Scheduled(initialDelayString = "${nitflex.download-interval}",
            fixedRateString = "${nitflex.download-interval}",
            timeUnit = TimeUnit.DAYS)
    public void run(){
        System.out.println("It works!");
    }
}
