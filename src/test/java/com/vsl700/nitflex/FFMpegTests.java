package com.vsl700.nitflex;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@Disabled
public class FFMpegTests {
    @Test
    public void frameGrabber_imagePresent_Test() throws FrameGrabber.Exception {
        Frame frame;
        try(FFmpegFrameGrabber grabber = new FFmpegFrameGrabber("D:\\Videos\\Retribution.2023.1080p.WEB-DL.DDP5.1.Atmos.H.264-IWiLLFiNDyouANDiWiLLKiLLYOU\\Retribution.2023.1080p.WEB-DL.DDP5.1.Atmos.H.264-IWiLLFiNDyouANDiWiLLKiLLYOU.mkv")){
            grabber.start();

            frame = grabber.grabImage();
            for(int i = 0; i < 9; i++){
                grabber.grabImage();
            }

            grabber.stop();
        }

        assertThat(frame.image.length).isGreaterThan(0);
    }

    @Test
    public void frameGrabber_imagePresent_Test2() throws FrameGrabber.Exception {
        Frame frame;
        try(FFmpegFrameGrabber grabber = new FFmpegFrameGrabber("D:\\Videos\\The.Commuter.2018.BRRip.XviD.AC3.DUAL-SlzD\\The.Commuter.2018.BRRip.XviD.AC3.DUAL-SlzD.avi")){
            grabber.start();

            frame = grabber.grabImage();
            for(int i = 0; i < 9; i++){
                grabber.grabImage();
            }

            grabber.stop();
        }

        assertThat(frame.image.length).isGreaterThan(0);
    }

    @Test
    public void frameGrabber_imageChanges_Test() throws FrameGrabber.Exception {
        byte[] arr1;
        byte[] arr2;
        try(FFmpegFrameGrabber grabber = new FFmpegFrameGrabber("D:\\Videos\\Retribution.2023.1080p.WEB-DL.DDP5.1.Atmos.H.264-IWiLLFiNDyouANDiWiLLKiLLYOU\\Retribution.2023.1080p.WEB-DL.DDP5.1.Atmos.H.264-IWiLLFiNDyouANDiWiLLKiLLYOU.mkv")){
            grabber.start();

            Frame frame = grabber.grabImage(); // The Frame object stays the same, its data becomes different
            for(int i = 0; i < 50; i++){
                grabber.grab();
                grabber.grabImage();
            }

            ByteBuffer buffer = (ByteBuffer) frame.image[0]; // Buffer stays the same as well
            arr1 = new byte[buffer.remaining()];
            buffer.get(arr1);
            buffer.flip();

            grabber.grab();
            grabber.grabImage();

            arr2 = new byte[buffer.remaining()];
            buffer.get(arr2);

            grabber.stop();
        }

        assertThat(Arrays.equals(arr1, arr2)).isFalse();
    }

    @Test
    public void frameGrabber_imageChanges_Test2() throws FrameGrabber.Exception {
        byte[] arr1;
        byte[] arr2;
        try(FFmpegFrameGrabber grabber = new FFmpegFrameGrabber("D:\\Videos\\The.Commuter.2018.BRRip.XviD.AC3.DUAL-SlzD\\The.Commuter.2018.BRRip.XviD.AC3.DUAL-SlzD.avi")){
            grabber.start();

            Frame frame = grabber.grabImage(); // The Frame object stays the same, its data becomes different
            for(int i = 0; i < 50; i++){
                grabber.grab();
                grabber.grabImage();
            }

            ByteBuffer buffer = (ByteBuffer) frame.image[0]; // Buffer stays the same as well
            arr1 = new byte[buffer.remaining()];
            buffer.get(arr1);
            buffer.flip();

            grabber.grab();
            grabber.grabImage();

            arr2 = new byte[buffer.remaining()];
            buffer.get(arr2);

            grabber.stop();
        }

        assertThat(Arrays.equals(arr1, arr2)).isFalse();
    }

    @Test
    public void frameGrabber_audio_Test() throws FrameGrabber.Exception {
        short[] arr;
        try(FFmpegFrameGrabber grabber = new FFmpegFrameGrabber("D:\\Videos\\Tetris.2023.1080p.WEBRip.x264-LAMA\\Tetris.2023.1080p.WEBRip.x264-LAMA.mp4")){
            grabber.start();

            Frame frame = grabber.grab();
            for(int i = 0; i < 100; i++){
                grabber.grab();
                grabber.grabSamples();
            }

            ShortBuffer buffer = (ShortBuffer) frame.samples[0];
            arr = new short[buffer.remaining()];
            buffer.get(arr);
            buffer.flip();

            grabber.stop();
        }

        for(short s : arr){
            if(s != 0) // If everything is fine, there will be at least one number that's different from 0
                return;
        }

        fail(); // If everything is 0, the test should fail
    }

    @Test
    public void frameGrabber_audio_Test2() throws FrameGrabber.Exception {
        short[] arr;
        try(FFmpegFrameGrabber grabber = new FFmpegFrameGrabber("D:\\Videos\\Retribution.2023.1080p.WEB-DL.DDP5.1.Atmos.H.264-IWiLLFiNDyouANDiWiLLKiLLYOU\\Retribution.2023.1080p.WEB-DL.DDP5.1.Atmos.H.264-IWiLLFiNDyouANDiWiLLKiLLYOU.mkv")){
            grabber.start();

            Frame frame = grabber.grab();
            for(int i = 0; i < 100; i++){
                grabber.grab();
                grabber.grabSamples();
            }

            ShortBuffer buffer = (ShortBuffer) frame.samples[0];
            arr = new short[buffer.remaining()];
            buffer.get(arr);
            buffer.flip();

            grabber.stop();
        }

        for(short s : arr){
            if(s != 0) // If everything is fine, there will be at least one number that's different from 0
                return;
        }

        fail(); // If everything is 0, the test should fail
    }

    @Test
    public void frameGrabber_audio_Test3() throws FrameGrabber.Exception {
        short[] arr;
        try(FFmpegFrameGrabber grabber = new FFmpegFrameGrabber("D:\\Videos\\The.Commuter.2018.BRRip.XviD.AC3.DUAL-SlzD\\The.Commuter.2018.BRRip.XviD.AC3.DUAL-SlzD.avi")){
            grabber.start();

            Frame frame = grabber.grab();
            for(int i = 0; i < 100; i++){
                grabber.grab();
                grabber.grabSamples();
            }

            ShortBuffer buffer = (ShortBuffer) frame.samples[0];
            arr = new short[buffer.remaining()];
            buffer.get(arr);
            buffer.flip();

            grabber.stop();
        }

        for(short s : arr){
            if(s != 0) // If everything is fine, there will be at least one number that's different from 0
                return;
        }

        fail(); // If everything is 0, the test should fail
    }

    @Test
    public void frameGrabber_audio_performance_Test() throws FrameGrabber.Exception { // 26 secs
        short[] arr;
        try(FFmpegFrameGrabber grabber = new FFmpegFrameGrabber("D:\\Videos\\The.Commuter.2018.BRRip.XviD.AC3.DUAL-SlzD\\The.Commuter.2018.BRRip.XviD.AC3.DUAL-SlzD.avi")){
            grabber.start();

            Frame frame;
            while((frame = grabber.grab()) != null){
                grabber.grabSamples();
            }

            grabber.stop();
        }
    }
}
