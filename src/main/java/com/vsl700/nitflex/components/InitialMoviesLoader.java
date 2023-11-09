package com.vsl700.nitflex.components;

import com.vsl700.nitflex.services.MovieLoaderService;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// Here goes movies loading on server start up
@Component
public class InitialMoviesLoader {

    private static final Logger LOG = LogManager.getLogger(InitialMoviesLoader.class);

    @Autowired
    private MovieLoaderService movieLoaderService;

    @PostConstruct
    public void init(){
        LOG.info("Syncing the DB with file system...");
        movieLoaderService.unloadNonExisting();
        movieLoaderService.loadNewlyAdded();
        LOG.info("Syncing is done!");
    }
}
