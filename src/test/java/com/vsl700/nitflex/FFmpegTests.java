package com.vsl700.nitflex;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import ws.schild.jave.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
public class FFmpegTests {
    @Test
    public void video_dashingTest(){
        /* Step 1. Declaring source file and Target file */
        File source = new File("D:\\Videos\\video-1622183701.mp4");
        File target = new File("D:\\Videos\\Conversion_Tests\\video-1622183701\\output_manifest.mpd");

        /* Step 2. Set Audio Attrributes for conversion*/
        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("aac");
        // here 64kbit/s is 64000
        /*audio.setBitRate(64000);*/
        /*audio.setChannels(2);*/
        /*audio.setSamplingRate(44100);*/

        /* Step 3. Set Video Attributes for conversion*/
        VideoAttributes video = new VideoAttributes();
        video.setCodec("h264");
        video.setX264Profile(VideoAttributes.X264_PROFILE.BASELINE);
        // Here 160 kbps video is 160000
        /*video.setBitRate(160000);*/
        // More the frames more quality and size, but keep it low based on devices like mobile
        /*video.setFrameRate(15);*/
        /*video.setSize(new VideoSize(400, 300));*/

        /* Step 4. Set Encoding Attributes*/
        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setFormat("dash");
        attrs.setAudioAttributes(audio);
        attrs.setVideoAttributes(video);

        /* Step 5. Do the Encoding*/
        try {
            Encoder encoder = new Encoder();
            encoder.encode(new MultimediaObject(source), target, attrs);
        } catch (Exception e) {
            /*Handle here the video failure*/
            throw new RuntimeException(e);
        }
    }

    @Test
    public void video_dashingTest2() throws IOException, InterruptedException {
        Path path = Path.of(new DefaultFFMPEGLocator().getFFMPEGExecutablePath());
        String inputPath = "D:\\Videos\\video-1622183701.mp4";
        String outputPath = "D:\\Videos\\Conversion_Tests\\video-1622183701";
        String ffmpegCommand = "%s -re -i %s -map 0 -map 0 -c:a aac -c:v libx264 -b:v:0 800k -b:v:1 300k -s:v:1 320x170 -profile:v:1 baseline -profile:v:0 main -bf 1 -keyint_min 120 -g 120 -sc_threshold 0 -b_strategy 0 -ar:a:1 22050 -use_timeline 1 -use_template 1 -window_size 5 -adaptation_sets \"id=0,streams=v id=1,streams=a\" -f dash -init_seg_name %s\\init-$RepresentationID$.m4s -media_seg_name %s\\chunk-$RepresentationID$-$Number%s$.m4s %s\\output_manifest.mpd".formatted(path.toString(), inputPath, outputPath, outputPath, "%05d", outputPath);

        ProcessBuilder pb = new ProcessBuilder(ffmpegCommand.split(" "));

        Process process = pb.start();

        String line;
        BufferedReader reader2 = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        while ((line = reader2.readLine()) != null) {
            System.out.println(line);
        }
        reader2.close();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        reader.close();

        assertThat(process.waitFor()).isEqualTo(0);
    }
}
