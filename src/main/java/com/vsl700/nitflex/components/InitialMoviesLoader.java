package com.vsl700.nitflex.components;

import com.vsl700.nitflex.services.MovieLoaderService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

// Here goes movies discovering & loading on server start up
@Component
public class InitialMoviesLoader {
    @Bean
    public CommandLineRunner run(MovieLoaderService movieLoaderService){
        return args -> {
            System.out.println("CLR2");
            //movieLoaderService.load("D:\\Videos\\some_movie");
        };
    }
}
