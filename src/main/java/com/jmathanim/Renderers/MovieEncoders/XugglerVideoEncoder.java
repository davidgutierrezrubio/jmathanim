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
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class XugglerVideoEncoder extends VideoEncoder {

    IMediaWriter writer;
    private double fps;
    private boolean framesGenerated;
    private boolean playSound;
    public static int BITRATE = 48000;
    public static int NUM_CHANNELS = 2;
    private long soundFrame;

    @Override
    public void createEncoder(File output, JMathAnimConfig config) throws IOException {
        framesGenerated = false;
        writer = ToolFactory.makeWriter(output.getCanonicalPath());
        writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, config.mediaW, config.mediaH);
        writer.addAudioStream(1, 0, NUM_CHANNELS, BITRATE);
        soundFrame = 0;
        fps = config.fps;

    }

    @Override
    public void addSound(File soundFile, int frameCount) throws IOException {
//        long microsecondsElapsed = (long) (1000000d * frameCount / fps);

        IContainer containerAudio = IContainer.make();
        containerAudio.open(soundFile.getCanonicalPath(), IContainer.Type.READ, null);

        int audiostreamt = -1;
        int numStreamAudio = containerAudio.getNumStreams();
        for (int i = 0; i < numStreamAudio; i++) {
            IStream stream = containerAudio.getStream(i);
            IStreamCoder code = stream.getStreamCoder();

            if (code.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO) {

                audiostreamt = i;
                break;
            }
        }

        IStreamCoder audioCoder = containerAudio.getStream(audiostreamt).getStreamCoder();
        System.out.println("Channels: " + audioCoder.getChannels());
        System.out.println("Sample rate: " + audioCoder.getSampleRate());
        if (audioCoder.open() < 0) {
            throw new RuntimeException("Cant open audio coder");
        }

        addSilence(frameCount - soundFrame);

        IPacket packetaudio = IPacket.make();
        while (containerAudio.readNextPacket(packetaudio) >= 0) {
            IAudioSamples samples = IAudioSamples.make(512,
                    audioCoder.getChannels(),
                    IAudioSamples.Format.FMT_S32);
            ByteBuffer aa = packetaudio.getData().getByteBuffer(0, packetaudio.getSize());

            byte[] myBytes = new byte[aa.remaining()];
            aa.get(myBytes);
            short[] mySamples = new short[myBytes.length / 2];
            for (int i = 0; i < mySamples.length; i++) {
                mySamples[i] = (short) ((myBytes[2 * i] << 8) | myBytes[2 * i + 1]);
            }

            writer.encodeAudio(1, mySamples);
            soundFrame += mySamples.length * fps / NUM_CHANNELS / BITRATE;

//            int offset = 0;
//            while (offset < packetaudio.getSize()) {
//                int bytesDecodedaudio = audioCoder.decodeAudio(samples,
//                        packetaudio,
//                        offset);
//                if (bytesDecodedaudio < 0) {
//                    throw new RuntimeException("could not detect audio");
//                }
//                offset += bytesDecodedaudio;
//
//                if (samples.isComplete()) {
//                    IAudioResampler resampler = IAudioResampler.make(2, audioCoder.getChannels(), 48000, audioCoder.getSampleRate());
//                    long sampleCount = samples.getNumSamples();
//                    IAudioSamples out = IAudioSamples.make(sampleCount, 2);
//                    resampler.resample(out, samples, sampleCount);
//
////                    out.setTimeStamp(microsecondsElapsed);
//                    writer.encodeAudio(1, out);
//
//                }
//            }
        }
        containerAudio.close();
        audioCoder.close();

    }

    private void addSilence(long numFrame) {
        //Generate silence for the given number of frames
        int size = (int) (numFrame / fps * NUM_CHANNELS * BITRATE);
        short[] silence = new short[size];
        for (int i = 0; i < silence.length; i++) {
//            silence[i] = (short) (32767 * Math.sin(5000 * Math.PI * i / silence.length));
            silence[i] = 0;
        }
        writer.encodeAudio(1, silence);
        soundFrame += numFrame;

    }

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
            writer.close();
        } else {
            JMathAnimScene.logger.info("No frames generated. Empty movie created.");
        }
    }

    public static BufferedImage convertToType(BufferedImage sourceImage, int targetType) {

        BufferedImage image;

        // if the source image is already the target type, return the source image
        if (sourceImage.getType() == targetType) {

            image = sourceImage;

        } // otherwise create a new image of the target type and draw the new image
        else {

            image = new BufferedImage(sourceImage.getWidth(),
                    sourceImage.getHeight(), targetType);

            image.getGraphics().drawImage(sourceImage, 0, 0, null);

        }

        return image;

    }

    @Override
    public boolean isFramesGenerated() {
        return framesGenerated;
    }

    public void getSampleFromAudioFile(File fileIn) {
        int totalFramesRead = 0;
        try {
            AudioInputStream audioInputStream
                    = AudioSystem.getAudioInputStream(fileIn);
            int bytesPerFrame
                    = audioInputStream.getFormat().getFrameSize();
            if (bytesPerFrame == AudioSystem.NOT_SPECIFIED) {
                // some audio formats may have unspecified frame size
                // in that case we may read any amount of bytes
                bytesPerFrame = 1;
            }
            // Set an arbitrary buffer size of 1024 frames.
            int numBytes = 1024 * bytesPerFrame;
            byte[] audioBytes = new byte[numBytes];
            try {
                int numBytesRead = 0;
                int numFramesRead = 0;
                // Try to read numBytes bytes from the file.
                while ((numBytesRead
                        = audioInputStream.read(audioBytes)) != -1) {
                    // Calculate the number of frames actually read.
                    numFramesRead = numBytesRead / bytesPerFrame;
                    totalFramesRead += numFramesRead;
                    // Here, do something useful with the audio data that's 
                    // now in the audioBytes array...
                }
            } catch (Exception ex) {
                // Handle the error...
            }
        } catch (Exception e) {
            // Handle the error...
        }
    }

}
