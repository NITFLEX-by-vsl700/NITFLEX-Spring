package com.vsl700.nitflex.services.implementations;

import com.vsl700.nitflex.services.MovieStreamingService;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ShortBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovieStreamingServiceImpl implements MovieStreamingService {
    @Override
    public List<byte[]> grabFrames(Path moviePath, int beginFrame, int length) {
        List<byte[]> result = new ArrayList<>();
        String moviePathStr = moviePath.toString();
        try(FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(moviePathStr);
            OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
            /*VideoCapture videoCapture = new VideoCapture(moviePathStr)*/){
            /*videoCapture.set(1, beginFrame);
            for(int i = beginFrame; i < beginFrame + length; i++){
                Mat mat = new Mat();
                videoCapture.read(mat);
                BufferedImage bufferedImage = Java2DFrameUtils.toBufferedImage(mat);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "jpg", outputStream);

                byte[] imageData = outputStream.toByteArray();

                result.add(imageData);
            }*/
            grabber.start();

            int framesCount = grabber.getLengthInVideoFrames();
            if(beginFrame + length > framesCount)
                throw new RuntimeException("Frames count: %d; beginFrame: %d; length: %d"
                        .formatted(framesCount, beginFrame, length)); // TODO: Add custom exception

            grabber.setVideoFrameNumber(beginFrame); // WARNING! Different from grabber.setVideoFrameNumber()
            for(int i = beginFrame; i < beginFrame + length; i++){
                Frame frame = grabber.grabImage();

                Mat mat = converter.convertToMat(frame);
                BufferedImage bufferedImage = Java2DFrameUtils.toBufferedImage(mat);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "jpg", outputStream);

                byte[] imageData = outputStream.toByteArray();

                result.add(imageData);
            }// TODO: Dispose the frame object

            grabber.stop();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    @Override
    public List<short[]> grabAudioFromFrames(Path moviePath, int beginFrame, int length) {
        List<short[]> result = new ArrayList<>();
        String moviePathStr = moviePath.toString();
        try(FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(moviePathStr)){
            grabber.start();

            int framesCount = grabber.getLengthInVideoFrames();
            if(beginFrame > framesCount || beginFrame + length > framesCount)
                throw new RuntimeException("Frames count: %d; beginFrame: %d; length: %d"
                        .formatted(framesCount, beginFrame, length)); // TODO: Add custom exception

            grabber.setVideoFrameNumber(beginFrame); // WARNING! May be different from grabber.setAudioFrameNumber()
            for(int i = beginFrame; i < beginFrame + length; i++){
                Frame frame = grabber.grabSamples();
                ShortBuffer buffer = (ShortBuffer) frame.samples[0]; // Buffer stays the same as well
                var arr = new short[buffer.remaining()];
                buffer.get(arr);
                buffer.flip();

                result.add(arr);
            }// TODO: Dispose the frame object

            grabber.stop();
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    @Override
    public long getMovieDuration(Path moviePath) {
        long result;
        String moviePathStr = moviePath.toString();
        try(FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(moviePathStr)){
            grabber.start();
            result = grabber.getLengthInTime();
            grabber.stop();
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    @Override
    public int getMovieFramesCount(Path moviePath) {
        int result;
        String moviePathStr = moviePath.toString();
        try(FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(moviePathStr)){
            grabber.start();
            result = grabber.getLengthInVideoFrames();
            grabber.stop();
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    @Override
    public int getMovieImageWidth(Path moviePath) {
        int result;
        String moviePathStr = moviePath.toString();
        try(FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(moviePathStr)){
            grabber.start();
            result = grabber.getImageWidth();
            grabber.stop();
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    @Override
    public int getMovieImageHeight(Path moviePath) {
        int result;
        String moviePathStr = moviePath.toString();
        try(FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(moviePathStr)){
            grabber.start();
            result = grabber.getImageHeight();
            grabber.stop();
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    @Override
    public double getMovieFrameRate(Path moviePath) {
        double result;
        String moviePathStr = moviePath.toString();
        try(FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(moviePathStr)){
            grabber.start();
            result = grabber.getFrameRate();
            grabber.stop();
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}
