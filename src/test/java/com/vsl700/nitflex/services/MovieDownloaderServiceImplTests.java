package com.vsl700.nitflex.services;

import com.vsl700.nitflex.components.SharedProperties;
import com.vsl700.nitflex.services.implementations.MovieDownloaderServiceImpl;
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

    private MovieDownloaderService movieDownloaderService;

    @BeforeEach
    public void setUp(){
        movieDownloaderService = new MovieDownloaderServiceImpl(sharedProperties);

        when(sharedProperties.getMoviesFolder()).then(invocation -> "D:\\Videos");
    }

    @Test
    public void test(){
        //movieDownloaderService.download("D:\\Downloads\\Retribution.2023.1080p.WEB-DL.DDP5.1.Atmos.H.264-IWiLLFiNDyouANDiWiLLKiLLYOU");
    }

}
