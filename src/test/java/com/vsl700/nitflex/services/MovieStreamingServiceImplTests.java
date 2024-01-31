package com.vsl700.nitflex.services;

import com.vsl700.nitflex.services.implementations.MovieStreamingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@Disabled
public class MovieStreamingServiceImplTests {
    private MovieStreamingServiceImpl movieStreamingService;

    @BeforeEach
    public void setUp(){
        movieStreamingService = new MovieStreamingServiceImpl();
    }

    @Test
    public void streamVideo_Test(){
        var actual = movieStreamingService.grabFrames(
                Path.of("D:\\Videos\\Retribution.2023.1080p.WEB-DL.DDP5.1.Atmos.H.264-IWiLLFiNDyouANDiWiLLKiLLYOU\\Retribution.2023.1080p.WEB-DL.DDP5.1.Atmos.H.264-IWiLLFiNDyouANDiWiLLKiLLYOU.mkv"),
                100,
                50);

        assertThat(actual.size()).isEqualTo(50);
    }

    @Test
    public void streamVideoPiece_Test(){
        var actual = movieStreamingService.grabFramesAsMP4(
                Path.of("D:\\Videos\\Retribution.2023.1080p.WEB-DL.DDP5.1.Atmos.H.264-IWiLLFiNDyouANDiWiLLKiLLYOU\\Retribution.2023.1080p.WEB-DL.DDP5.1.Atmos.H.264-IWiLLFiNDyouANDiWiLLKiLLYOU.mkv"),
                10000,
                50);

        System.out.println(actual.length);
        assertThat(actual.length).isGreaterThan(0);
    }

    @Test
    public void streamVideo_Test2(){
        var actual = movieStreamingService.grabFrames(
                Path.of("D:\\Videos\\L Homme Orchestre (1970)\\L Homme Orchestre.avi"),
                100,
                50);

        assertThat(actual.size()).isEqualTo(50);
    }

    @Test
    public void streamVideo_framesNotEmpty_Test(){
        var actual = movieStreamingService.grabFrames(
                Path.of("D:\\Videos\\Retribution.2023.1080p.WEB-DL.DDP5.1.Atmos.H.264-IWiLLFiNDyouANDiWiLLKiLLYOU\\Retribution.2023.1080p.WEB-DL.DDP5.1.Atmos.H.264-IWiLLFiNDyouANDiWiLLKiLLYOU.mkv"),
                100,
                50);

        for(byte[] bArr : actual){
            for(byte b : bArr){
                if(b != 0)
                    return;
            }
        }

        fail();
    }

    @Test
    public void streamVideo_framesAreNotSame_Test(){
        Path moviePath = Path.of("D:\\Videos\\Retribution.2023.1080p.WEB-DL.DDP5.1.Atmos.H.264-IWiLLFiNDyouANDiWiLLKiLLYOU\\Retribution.2023.1080p.WEB-DL.DDP5.1.Atmos.H.264-IWiLLFiNDyouANDiWiLLKiLLYOU.mkv");
        int beginFrame = 10000;
        int length = 50;
        var framesArray1 = movieStreamingService.grabFrames(
                moviePath,
                beginFrame,
                length);

        var framesArray2 = movieStreamingService.grabFrames(
                moviePath,
                beginFrame + length,
                length);

        for(byte[] frameBytes : framesArray1){
            if(framesArray2.stream().anyMatch(arr -> Arrays.equals(frameBytes, arr))) {
                fail();
            }
        }
    }

    @Test
    public void streamAudio_Test(){
        var actual = movieStreamingService.grabAudioFromFrames(
                Path.of("D:\\Videos\\Retribution.2023.1080p.WEB-DL.DDP5.1.Atmos.H.264-IWiLLFiNDyouANDiWiLLKiLLYOU\\Retribution.2023.1080p.WEB-DL.DDP5.1.Atmos.H.264-IWiLLFiNDyouANDiWiLLKiLLYOU.mkv"),
                100,
                50);

        assertThat(actual.size()).isEqualTo(50);
    }

    @Test
    public void streamAudio_samplesNotEmpty_Test(){
        var actual = movieStreamingService.grabAudioFromFrames(
                Path.of("D:\\Videos\\Retribution.2023.1080p.WEB-DL.DDP5.1.Atmos.H.264-IWiLLFiNDyouANDiWiLLKiLLYOU\\Retribution.2023.1080p.WEB-DL.DDP5.1.Atmos.H.264-IWiLLFiNDyouANDiWiLLKiLLYOU.mkv"),
                100,
                50);

        for(short[] bArr : actual){
            for(short b : bArr){
                if(b != 0)
                    return;
            }
        }

        fail();
    }

    @Test
    public void framesCount_Test(){
        var actual = movieStreamingService.getMovieFramesCount(Path.of("D:\\Videos\\Retribution.2023.1080p.WEB-DL.DDP5.1.Atmos.H.264-IWiLLFiNDyouANDiWiLLKiLLYOU\\Retribution.2023.1080p.WEB-DL.DDP5.1.Atmos.H.264-IWiLLFiNDyouANDiWiLLKiLLYOU.mkv"));

        assertThat(actual).isEqualTo(131120);
    }

    @Test
    public void movieDuration_Test(){
        var actual = movieStreamingService.getMovieDuration(
                Path.of("D:\\Videos\\Retribution.2023.1080p.WEB-DL.DDP5.1.Atmos.H.264-IWiLLFiNDyouANDiWiLLKiLLYOU\\Retribution.2023.1080p.WEB-DL.DDP5.1.Atmos.H.264-IWiLLFiNDyouANDiWiLLKiLLYOU.mkv"));

        assertThat(actual).isEqualTo(5468802000L);
    }

    @Test
    public void movieImageWidth_Test(){
        var actual = movieStreamingService.getMovieImageWidth(
                Path.of("D:\\Videos\\Retribution.2023.1080p.WEB-DL.DDP5.1.Atmos.H.264-IWiLLFiNDyouANDiWiLLKiLLYOU\\Retribution.2023.1080p.WEB-DL.DDP5.1.Atmos.H.264-IWiLLFiNDyouANDiWiLLKiLLYOU.mkv"));

        assertThat(actual).isEqualTo(1920);
    }

    @Test
    public void movieImageHeight_Test(){
        var actual = movieStreamingService.getMovieImageHeight(
                Path.of("D:\\Videos\\Retribution.2023.1080p.WEB-DL.DDP5.1.Atmos.H.264-IWiLLFiNDyouANDiWiLLKiLLYOU\\Retribution.2023.1080p.WEB-DL.DDP5.1.Atmos.H.264-IWiLLFiNDyouANDiWiLLKiLLYOU.mkv"));

        assertThat(actual).isEqualTo(802);
    }

    @Test
    public void movieFrameRate_Test(){
        var actual = movieStreamingService.getMovieFrameRate(
                Path.of("D:\\Videos\\Retribution.2023.1080p.WEB-DL.DDP5.1.Atmos.H.264-IWiLLFiNDyouANDiWiLLKiLLYOU\\Retribution.2023.1080p.WEB-DL.DDP5.1.Atmos.H.264-IWiLLFiNDyouANDiWiLLKiLLYOU.mkv"));

        assertThat(actual).isEqualTo(23.976);
    }
}
