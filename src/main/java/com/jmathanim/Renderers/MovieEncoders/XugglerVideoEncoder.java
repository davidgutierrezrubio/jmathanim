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

import com.jmathanim.jmathanim.JMathAnimConfig;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

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
    private final ArrayList<SoundItem> soundItems;
    private JMathAnimConfig config;
    private FFmpegAudioEncoder FfmpegAudioEncoder;

    public XugglerVideoEncoder() {
        super();
        soundItems = new ArrayList<>();

    }

    @Override
    public void createEncoder(JMathAnimConfig config) throws IOException {
        this.config = config;
        FfmpegAudioEncoder = new FFmpegAudioEncoder(config);
        framesGenerated = false;
        writer = ToolFactory.makeWriter(config.getSaveFilePath().getCanonicalPath());
        writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, config.getMediaWidth(), config.getMediaHeight());
        writer.addAudioStream(1, 0, NUM_CHANNELS, BITRATE);
        fps = config.getFps();
    }

    @Override
    public void addSound(SoundItem soundItem) {
        soundItems.add(soundItem);
    }

    @Override
    public void addSound(URL soundURL, long miliSeconds) {
        soundItems.add(SoundItem.make(soundURL,miliSeconds));
    }

    @Override
    public void writeFrame(BufferedImage image, int frameCount) {
        framesGenerated = true;
        BufferedImage bgrScreen = convertToType(image, BufferedImage.TYPE_3BYTE_BGR);
        long nanosecondsElapsed = (long) (1000000000d * frameCount / fps);
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
        FfmpegAudioEncoder.processSounds(soundItems);
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
}
