package com.vsl700.nitflex.services;

import com.vsl700.nitflex.components.WebsiteCredentials;
import com.vsl700.nitflex.services.implementations.MovieSeekerServiceImpl;
import com.vsl700.nitflex.services.implementations.WebClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static reactor.core.publisher.Mono.when;

@Disabled
@ExtendWith(MockitoExtension.class)
public class MovieSeekerServiceImplTests {

    private MovieSeekerService movieSeekerService;
    private WebClientService webClientService;

    @Mock
    private WebsiteCredentials.Zamunda zamundaCredentials;

    @BeforeEach
    public void setUp(){
        webClientService = new WebClientServiceImpl();
        movieSeekerService = new MovieSeekerServiceImpl(webClientService, zamundaCredentials);

        Mockito.when(zamundaCredentials.getUsername()).then(invocation -> "username");
        Mockito.when(zamundaCredentials.getPassword()).then(invocation -> "password");
    }

    @Test
    public void doesNotThrow_Test(){
        assertDoesNotThrow(() -> {
            URI link = movieSeekerService.findMovieURL();
            System.out.println(link);
        });
    }
}
