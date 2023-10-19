package com.vsl700.nitflex.components;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

// Here goes movies discovering & loading on server start up
@Component
public class InitialMoviesLoader {
    @Bean
    public CommandLineRunner run(){
        return args -> { System.out.println("CLR2"); };
    }
}
