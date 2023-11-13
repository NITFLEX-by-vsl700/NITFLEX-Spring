package com.vsl700.nitflex.services;

import com.vsl700.nitflex.components.SharedProperties;
import com.vsl700.nitflex.components.WebsiteCredentials;
import com.vsl700.nitflex.services.implementations.MovieDownloaderServiceImpl;
import com.vsl700.nitflex.services.implementations.WebClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

@Disabled
@ExtendWith(MockitoExtension.class)
public class MovieDownloaderServiceImplTests {

    @Mock
    private SharedProperties sharedProperties;
    @Mock
    private WebsiteCredentials.Zamunda zamundaCredentials;

    private WebClientService webClientService;
    private MovieDownloaderService movieDownloaderService;

    @BeforeEach
    public void setUp(){
        webClientService = new WebClientServiceImpl(zamundaCredentials);
        movieDownloaderService = new MovieDownloaderServiceImpl(webClientService, sharedProperties);

        when(sharedProperties.getMoviesFolder()).then(invocation -> "D:\\Videos");
    }

    @Test
    public void test(){
        System.out.println(sharedProperties.getMoviesFolder()); // Remove this line
        //movieDownloaderService.download("D:\\Downloads\\Retribution.2023.1080p.WEB-DL.DDP5.1.Atmos.H.264-IWiLLFiNDyouANDiWiLLKiLLYOU");
    }

}
