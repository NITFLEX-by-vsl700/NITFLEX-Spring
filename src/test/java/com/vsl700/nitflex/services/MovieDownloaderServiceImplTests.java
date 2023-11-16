package com.vsl700.nitflex.services;

import com.vsl700.nitflex.components.SharedProperties;
import com.vsl700.nitflex.components.WebsiteCredentials;
import com.vsl700.nitflex.services.implementations.MovieDownloaderServiceImpl;
import com.vsl700.nitflex.services.implementations.WebClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Disabled
@ExtendWith(MockitoExtension.class)
public class MovieDownloaderServiceImplTests {

    //@Mock
    private SharedProperties sharedProperties;
    //@Mock
    private WebsiteCredentials.Zamunda zamundaCredentials;
    //@Mock
    private MovieDownloaderService movieDownloaderService;

    private WebClientService webClientService;

    @BeforeEach
    public void setUp(){
        sharedProperties = mock(SharedProperties.class,
                Mockito.withSettings().strictness(Strictness.LENIENT));
        zamundaCredentials = mock(WebsiteCredentials.Zamunda.class,
                Mockito.withSettings().strictness(Strictness.LENIENT));
        webClientService = new WebClientServiceImpl();

        when(sharedProperties.getMoviesFolder()).then(invocation -> "D:\\Videos\\Torrent Test");

        when(zamundaCredentials.getUsername()).then(invocation -> "username");
        when(zamundaCredentials.getPassword()).then(invocation -> "password");

        movieDownloaderService = mock(MovieDownloaderServiceImpl.class,
                Mockito.withSettings().useConstructor(webClientService, sharedProperties, zamundaCredentials)
                        .defaultAnswer(CALLS_REAL_METHODS)
                        .strictness(Strictness.LENIENT));
        //doNothing().when(movieDownloaderService).downloadFromTorrentFilePath(anyString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"https://zamunda.net/banan?id=747087&hit=1&t=movie",
                            "https://zamunda.net/banan?id=747384&hit=1&t=movie",
                            "https://zamunda.net/banan?id=747134&hit=1&t=movie"})
    public void downloadFromPageURL_Only_Test(String url){
        AtomicBoolean flag = new AtomicBoolean(false);
        doAnswer((invocation) -> {
            flag.set(true);
            return null;
        }).when(movieDownloaderService).downloadFromTorrentFilePath(anyString());

        assertDoesNotThrow(() ->
                movieDownloaderService.downloadFromPageURL(url));

        assertThat(flag.get()).isTrue();
    }

    @Test
    public void downloadFromPageURL_Full_Test() {
        assertDoesNotThrow(() ->
                movieDownloaderService.downloadFromPageURL("https://zamunda.net/banan?id=747087&hit=1&t=movie"));
    }

    @Test
    public void downloadFromTorrentFilePath_Test() throws InterruptedException {
        movieDownloaderService.downloadFromTorrentFilePath("D:\\Videos\\Torrent Test\\Oppenheimer.2023.1080p.BluRay.x265.DTS.HD.MA.5.1-DiN.torrent");

        while(true) {Thread.sleep(1000);}
    }

    @Test
    public void downloadFromTorrentFilePath_Test2() throws InterruptedException {
        movieDownloaderService.downloadFromTorrentFilePath("D:\\Videos\\Torrent Test\\The.Creator.2023.1080p.AMZN.WEB-DL.DDP5.1.H.264-LessConfusingThanTenet.mkv.torrent");

        while(true) {Thread.sleep(1000);}
    }

}