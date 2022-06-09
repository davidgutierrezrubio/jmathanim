/*
 * Copyright (C) 2020 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.jmathanim.Renderers.MovieEncoders;

import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class XugglerVideoEncoder extends VideoEncoder {

    IMediaWriter writer;
    private double fps;
    private boolean framesGenerated;
    public static int BITRATE = 48000;
    public static int NUM_CHANNELS = 2;
    private long soundFrame;
    private final ArrayList<Long> timeSoundStamps;
    private final ArrayList<URL> soundURLs;
    private JMathAnimConfig config;
    private File movieFilename;

    public XugglerVideoEncoder() {
        super();
        timeSoundStamps = new ArrayList<>();
        soundURLs = new ArrayList<>();
    }

    @Override
    public void createEncoder(File output, JMathAnimConfig config) throws IOException {
        this.movieFilename = output;
        this.config = config;
        framesGenerated = false;
        writer = ToolFactory.makeWriter(output.getCanonicalPath());
        writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, config.mediaW, config.mediaH);
        writer.addAudioStream(1, 0, NUM_CHANNELS, BITRATE);
        soundFrame = 0;
        fps = config.fps;
    }

    @Override
    public void addSound(URL soundURL, long frameCount, int fps) {
        //Compute miliseconds
        long miliSeconds = (frameCount * 1000) / fps;
        timeSoundStamps.add(miliSeconds);
        soundURLs.add(soundURL);
    }

//    public void addSound(File soundFile, int frameCount) throws IOException {
////        long microsecondsElapsed = (long) (1000000d * frameCount / fps);
//
//        IContainer containerAudio = IContainer.make();
//        containerAudio.open(soundFile.getCanonicalPath(), IContainer.Type.READ, null);
//
//        int audiostreamt = -1;
//        int numStreamAudio = containerAudio.getNumStreams();
//        for (int i = 0; i < numStreamAudio; i++) {
//            IStream stream = containerAudio.getStream(i);
//            IStreamCoder code = stream.getStreamCoder();
//
//            if (code.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO) {
//
//                audiostreamt = i;
//                break;
//            }
//        }
//
//        IStreamCoder audioCoder = containerAudio.getStream(audiostreamt).getStreamCoder();
//        if (audioCoder.open() < 0) {
//            throw new RuntimeException("Cant open audio coder");
//        }
//
//        addSilence(frameCount - soundFrame);
//
//        IPacket packetaudio = IPacket.make();
//        while (containerAudio.readNextPacket(packetaudio) >= 0) {
//            IAudioSamples samples = IAudioSamples.make(512, audioCoder.getChannels(), IAudioSamples.Format.FMT_S32);
//            ByteBuffer aa = packetaudio.getData().getByteBuffer(0, packetaudio.getSize());
//
//            byte[] myBytes = new byte[aa.remaining()];
//            aa.get(myBytes);
//            short[] mySamples = new short[myBytes.length / 2];
//            for (int i = 0; i < mySamples.length; i++) {
//                mySamples[i] = (short) ((myBytes[2 * i] << 8) | myBytes[2 * i + 1]);
//            }
//
//            writer.encodeAudio(1, mySamples);
//            soundFrame += mySamples.length * fps / NUM_CHANNELS / BITRATE;
//
////            int offset = 0;
////            while (offset < packetaudio.getSize()) {
////                int bytesDecodedaudio = audioCoder.decodeAudio(samples,
////                        packetaudio,
////                        offset);
////                if (bytesDecodedaudio < 0) {
////                    throw new RuntimeException("could not detect audio");
////                }
////                offset += bytesDecodedaudio;
////
////                if (samples.isComplete()) {
////                    IAudioResampler resampler = IAudioResampler.make(2, audioCoder.getChannels(), 48000, audioCoder.getSampleRate());
////                    long sampleCount = samples.getNumSamples();
////                    IAudioSamples out = IAudioSamples.make(sampleCount, 2);
////                    resampler.resample(out, samples, sampleCount);
////
//////                    out.setTimeStamp(microsecondsElapsed);
////                    writer.encodeAudio(1, out);
////
////                }
////            }
//        }
//        containerAudio.close();
//        audioCoder.close();
//
//    }
//    private void addSilence(long numFrame) {
//        // Generate silence for the given number of frames
//        int size = (int) (numFrame / fps * NUM_CHANNELS * BITRATE);
//        short[] silence = new short[size];
//        for (int i = 0; i < silence.length; i++) {
////            silence[i] = (short) (32767 * Math.sin(5000 * Math.PI * i / silence.length));
//            silence[i] = 0;
//        }
//        writer.encodeAudio(1, silence);
//        soundFrame += numFrame;
//
//    }
    @Override
    public void writeFrame(BufferedImage image, int frameCount) {
        framesGenerated = true;
        BufferedImage bgrScreen = convertToType(image, BufferedImage.TYPE_3BYTE_BGR);
        long nanosecondsElapsed = (long) (1000000000d * frameCount / fps);
        if (frameCount == 60) {
            long nanosecondsElapsed2 = (long) (1000000000d * frameCount / fps);
        }

        writer.encodeVideo(0, bgrScreen, nanosecondsElapsed, TimeUnit.NANOSECONDS);

    }

    @Override
    public void finish() {
        if (framesGenerated) {
            writer.flush();
            writer.close();

        } else {
            JMathAnimScene.logger.info("No frames generated. Empty movie created.");
        }

        processSounds();
    }

    private void processSounds() {

        if (timeSoundStamps.isEmpty()) {
            return;
        }
        try {
            String dir = config.getOutputDir().getCanonicalPath() + File.separatorChar;
            String tempSoundName = config.getOutputFileName();
            String outputName = movieFilename.getName();
            final File tmpFile = new File(dir + "_" + outputName);

            //Copy output file to temp
            FileUtils.copyFile(new File(dir + outputName), tmpFile);
            FileUtils.forceDeleteOnExit(tmpFile);

            File sound1 = new File(dir + tempSoundName + "0.flac");
            File sound2 = new File(dir + tempSoundName + "1.flac");
            FileUtils.forceDeleteOnExit(sound1);
            FileUtils.forceDeleteOnExit(sound2);
            String soundPath = Paths.get(soundURLs.get(0).toURI()).toString();
            //First encode
            final String firstEncode = config.getFfmpegBinDir() + "ffmpeg.exe -y -loglevel quiet -i " + soundPath + " -filter_complex \"[0:0]adelay=" + timeSoundStamps.get(0) +"|"+timeSoundStamps.get(0)+ "[mixout]\" -map [mixout] -c:a flac " + dir + tempSoundName + "1.flac";
            JMathAnimScene.logger.info("Processing sound: [1/"+timeSoundStamps.size()+"]: " + firstEncode);
            Runtime.getRuntime().exec(firstEncode).waitFor();
            int index = 1;
            for (int i = 1; i < timeSoundStamps.size(); i++) {
                Long timeStamp = timeSoundStamps.get(i);
                soundPath = Paths.get(soundURLs.get(i).toURI()).toString();
                final String cmd = config.getFfmpegBinDir() + "ffmpeg.exe -y -loglevel quiet -i " + dir + tempSoundName + index + ".flac -i " + soundPath + " -filter_complex \"[1:0]adelay=" + timeStamp +"|"+timeStamp+ "[delayed];[delayed][0:0]amix=inputs=2:duration=longest[mixin];[mixin]volume=6.0201dB[mixout]\" -map [mixout] -c:a flac " + dir + tempSoundName + (1 - index) + ".flac";
                JMathAnimScene.logger.info("Processing sound: ["+(i+1)+"/"+timeSoundStamps.size()+"]: " + cmd);
                Runtime.getRuntime().exec(cmd).waitFor();
                index = 1 - index;
            }
            //Join everything
            final String cmdFinal = config.getFfmpegBinDir() + "ffmpeg.exe -y -loglevel quiet -i " + dir + "_" + outputName + " -i " + dir + tempSoundName + "" + index + ".flac " + dir + outputName;

            JMathAnimScene.logger.info("Joining everything: " + cmdFinal);
            Runtime.getRuntime().exec(cmdFinal).waitFor();

        } catch (IOException ex) {
            JMathAnimScene.logger.error("I/O error processing sounds. Maybe I cannot find the ffmpeg executable. Set it"
                    + " with the config.setFfmpegBinDir method.");
        } catch (InterruptedException ex) {
            JMathAnimScene.logger.error("InterruptedException error processing sounds");
            Logger.getLogger(XugglerVideoEncoder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
             JMathAnimScene.logger.error("Malformed sound URL. Check if all sound files are correctly referenced.");
            Logger.getLogger(XugglerVideoEncoder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private BufferedImage convertToType(BufferedImage sourceImage, int targetType) {

        BufferedImage image;

        // if the source image is already the target type, return the source image
        if (sourceImage.getType() == targetType) {

            image = sourceImage;

        } // otherwise create a new image of the target type and draw the new image
        else {

            image = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), targetType);

            image.getGraphics().drawImage(sourceImage, 0, 0, null);

        }

        return image;

    }

    @Override
    public boolean isFramesGenerated() {
        return framesGenerated;
    }

//    private void getSampleFromAudioFile(File fileIn) {
//        int totalFramesRead = 0;
//        try {
//            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(fileIn);
//            int bytesPerFrame = audioInputStream.getFormat().getFrameSize();
//            if (bytesPerFrame == AudioSystem.NOT_SPECIFIED) {
//                // some audio formats may have unspecified frame size
//                // in that case we may read any amount of bytes
//                bytesPerFrame = 1;
//            }
//            // Set an arbitrary buffer size of 1024 frames.
//            int numBytes = 1024 * bytesPerFrame;
//            byte[] audioBytes = new byte[numBytes];
//            try {
//                int numBytesRead = 0;
//                int numFramesRead = 0;
//                // Try to read numBytes bytes from the file.
//                while ((numBytesRead = audioInputStream.read(audioBytes)) != -1) {
//                    // Calculate the number of frames actually read.
//                    numFramesRead = numBytesRead / bytesPerFrame;
//                    totalFramesRead += numFramesRead;
//                    // Here, do something useful with the audio data that's
//                    // now in the audioBytes array...
//                }
//            } catch (IOException ex) {
//                // Handle the error...
//            }
//        } catch (IOException | UnsupportedAudioFileException e) {
//            // Handle the error...
//        }
//    }
//
//    private void joinAudioVideo(File video, File audio) throws IOException {
//
//        String filenamevideo = video.getCanonicalPath();
//        String filenameaudio = audio.getCanonicalPath();
//
//        IMediaWriter mWriter = ToolFactory.makeWriter("c:\\ffmpeg\\bin\\conSonido.mp4"); //output file
//
//        IContainer containerVideo = IContainer.make();
//        IContainer containerAudio = IContainer.make();
//
//        if (containerVideo.open(filenamevideo, IContainer.Type.READ, null) < 0) {
//            throw new IllegalArgumentException("Cant find " + filenamevideo);
//        }
//
//        if (containerAudio.open(filenameaudio, IContainer.Type.READ, null) < 0) {
//            throw new IllegalArgumentException("Cant find " + filenameaudio);
//        }
//
//        int numStreamVideo = containerVideo.getNumStreams();
//        int numStreamAudio = containerAudio.getNumStreams();
//
//        System.out.println("Number of video streams: " + numStreamVideo + "\n" + "Number of audio streams: " + numStreamAudio);
//
//        int videostreamt = -1; //this is the video stream id
//        int audiostreamt = -1;
//
//        IStreamCoder videocoder = null;
//
//        for (int i = 0; i < numStreamVideo; i++) {
//            IStream stream = containerVideo.getStream(i);
//            IStreamCoder code = stream.getStreamCoder();
//
//            if (code.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
//                videostreamt = i;
//                videocoder = code;
//                break;
//            }
//
//        }
//
//        for (int i = 0; i < numStreamAudio; i++) {
//            IStream stream = containerAudio.getStream(i);
//            IStreamCoder code = stream.getStreamCoder();
//
//            if (code.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO) {
//                audiostreamt = i;
//                break;
//            }
//
//        }
//
//        if (videostreamt == -1) {
//            throw new RuntimeException("No video steam found");
//        }
//        if (audiostreamt == -1) {
//            throw new RuntimeException("No audio steam found");
//        }
//
//        if (videocoder.open() < 0) {
//            throw new RuntimeException("Cant open video coder");
//        }
//        IPacket packetvideo = IPacket.make();
//
//        IStreamCoder audioCoder = containerAudio.getStream(audiostreamt).getStreamCoder();
//
//        if (audioCoder.open() < 0) {
//            throw new RuntimeException("Cant open audio coder");
//        }
//        mWriter.addAudioStream(0, 0, audioCoder.getChannels(), audioCoder.getSampleRate());
//
//        mWriter.addVideoStream(1, 1, videocoder.getWidth(), videocoder.getHeight());
//
//        IPacket packetaudio = IPacket.make();
//
//        while (containerVideo.readNextPacket(packetvideo) >= 0
//                || containerAudio.readNextPacket(packetaudio) >= 0) {
//
//            if (packetvideo.getStreamIndex() == videostreamt) {
//
//                //video packet
//                IVideoPicture picture = IVideoPicture.make(videocoder.getPixelType(),
//                        videocoder.getWidth(),
//                        videocoder.getHeight());
//                int offset = 0;
//                while (offset < packetvideo.getSize()) {
//                    int bytesDecoded = videocoder.decodeVideo(picture,
//                            packetvideo,
//                            offset);
//                    if (bytesDecoded < 0) {
//                        throw new RuntimeException("bytesDecoded not working");
//                    }
//                    offset += bytesDecoded;
//
//                    if (picture.isComplete()) {
//                        System.out.println(picture.getPixelType());
//                        mWriter.encodeVideo(1, picture);
//
//                    }
//                }
//            }
//
//            if (packetaudio.getStreamIndex() == audiostreamt) {
//                //audio packet
//
//                IAudioSamples samples = IAudioSamples.make(512,
//                        audioCoder.getChannels(),
//                        IAudioSamples.Format.FMT_S32);
//                int offset = 0;
//                while (offset < packetaudio.getSize()) {
//                    int bytesDecodedaudio = audioCoder.decodeAudio(samples,
//                            packetaudio,
//                            offset);
//                    if (bytesDecodedaudio < 0) {
//                        throw new RuntimeException("could not detect audio");
//                    }
//                    offset += bytesDecodedaudio;
//
//                    if (samples.isComplete()) {
//                        mWriter.encodeAudio(0, samples);
//
//                    }
//                }
//
//            }
//
//        }
//    }
}
