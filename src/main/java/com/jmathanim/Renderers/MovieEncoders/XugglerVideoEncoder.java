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
import com.xuggle.xuggler.IAudioResampler;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class XugglerVideoEncoder extends VideoEncoder {

    private IContainer containerAudio;
    IStreamCoder audioCoder;
    private int offset;
    IMediaWriter writer;
    private long startTime;
    private double fps;
    private boolean framesGenerated;
    private boolean playSound;

    @Override
    public void createEncoder(File output, JMathAnimConfig config) throws IOException {
        framesGenerated = false;
        writer = ToolFactory.makeWriter(output.getCanonicalPath());
        writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, config.mediaW, config.mediaH);

        writer.addAudioStream(1, 1, 2, 48000);
//        writer.addAudioStream(1, 1, audioCoder.getChannels(), audioCoder.getSampleRate());

        fps = config.fps;

    }

    @Override
    public void addSound(File soundFile) throws IOException {
        if (playSound) {
            return;//Don't add a sound while another one is playing
        }
        playSound = true;
        offset = 0;
        containerAudio = IContainer.make();
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

        audioCoder = containerAudio.getStream(audiostreamt).getStreamCoder();
        System.out.println("Channels: " + audioCoder.getChannels());
        System.out.println("Sample rate: " + audioCoder.getSampleRate());
        if (audioCoder.open() < 0) {
            throw new RuntimeException("Cant open audio coder");
        }
    }

    @Override
    public void writeFrame(BufferedImage image, int frameCount) {

        BufferedImage bgrScreen = convertToType(image, BufferedImage.TYPE_3BYTE_BGR);
        long nanosecondsElapsed = (long) (1000000000d * frameCount / fps);
        writer.encodeVideo(0, bgrScreen, nanosecondsElapsed, TimeUnit.NANOSECONDS);

        if (playSound) {
            IPacket packetaudio = IPacket.make();
            if (containerAudio.readNextPacket(packetaudio) >= 0) {
                IAudioSamples samples = IAudioSamples.make(512,
                        audioCoder.getChannels(),
                        IAudioSamples.Format.FMT_S32);
                int offset = 0;
                while (offset < packetaudio.getSize()) {
                    int bytesDecodedaudio = audioCoder.decodeAudio(samples,
                            packetaudio,
                            offset);
                    if (bytesDecodedaudio < 0) {
                        throw new RuntimeException("could not detect audio");
                    }
                    offset += bytesDecodedaudio;

                    if (samples.isComplete()) {
                        IAudioResampler resampler = IAudioResampler.make(2, audioCoder.getChannels(), 48000, audioCoder.getSampleRate());
                        long sampleCount = samples.getNumSamples();
                        IAudioSamples out = IAudioSamples.make(sampleCount, 2);
                        resampler.resample(out, samples, sampleCount);
//                        long ts = (nanosecondsElapsed - 2100000000l) * (512000l - 21333l) / (2866666666l - 2100000000l) + 21333l;
                        out.setTimeStamp((long) (21333*frameCount*30/fps));
//                        out.setTimeBase(IRational.make((int) fps,1));
                        System.out.println("Timestamp\t" + out.getTimeStamp() + "\t" + frameCount+"\t"+fps);
//                        out.setTimeStamp(nanosecondsElapsed);
//                        out.setTimeBase(TimeUnit.NANOSECONDS);
                        writer.encodeAudio(1, out);
//                        writer.encodeAudio(1, out,nanosecondsElapsed, TimeUnit.NANOSECONDS);

                    }
                }
            } else {
                playSound = false;
            }
        } else System.out.println("Timestamp only video\t" +  + nanosecondsElapsed);
        framesGenerated = true;
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

}
