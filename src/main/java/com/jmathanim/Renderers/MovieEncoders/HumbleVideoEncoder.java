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

import com.jmathanim.Renderers.Java2DAwtRenderer;
import com.jmathanim.Utils.JMathAnimConfig;
import io.humble.video.Codec;
import io.humble.video.Encoder;
import io.humble.video.MediaPacket;
import io.humble.video.MediaPicture;
import io.humble.video.Muxer;
import io.humble.video.MuxerFormat;
import io.humble.video.PixelFormat;
import io.humble.video.Rational;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class HumbleVideoEncoder extends VideoEncoder {

    private Muxer muxer;
    private MuxerFormat format;
    private Codec codec;
    private Encoder encoder;
    private PixelFormat.Type pixelformat;
    private Rational rationalFrameRate;
    private MediaPacket packet;
    private MediaPicture picture;

    @Override
    public void createEncoder(File saveFilePath, JMathAnimConfig config) throws IOException {
        muxer = Muxer.make(saveFilePath.getCanonicalPath(), null, "mp4");
        format = muxer.getFormat();
        codec = Codec.findEncodingCodec(format.getDefaultVideoCodecId());
        encoder = Encoder.make(codec);
        encoder.setWidth(config.mediaW);
        encoder.setHeight(config.mediaH);
        pixelformat = PixelFormat.Type.PIX_FMT_YUV420P;
        encoder.setPixelFormat(pixelformat);

        rationalFrameRate = Rational.make(1, config.fps);
        encoder.setTimeBase(rationalFrameRate);
        if (format.getFlag(MuxerFormat.Flag.GLOBAL_HEADER)) {
            encoder.setFlag(Encoder.Flag.FLAG_GLOBAL_HEADER, true);
        }
        /**
         * Open the encoder.
         */
        encoder.open(null, null);
        /**
         * Add this stream to the muxer.
         */
        muxer.addNewStream(encoder);

        try {
            /**
             * And open the muxer for business.
             */
            muxer.open(null, null);
        } catch (InterruptedException | IOException ex) {
            Logger.getLogger(Java2DAwtRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
        packet = MediaPacket.make();
        picture = MediaPicture.make(
                encoder.getWidth(),
                encoder.getHeight(),
                pixelformat);
        picture.setTimeBase(rationalFrameRate);
    }


@Override
        public void writeFrame(BufferedImage finalImage,int frameCount) {
            BufferedImage screen = MediaPictureConverterFactory.convertToType(finalImage, BufferedImage.TYPE_3BYTE_BGR);
            MediaPictureConverter converter = MediaPictureConverterFactory.createConverter(screen, picture);
            converter.toPicture(picture, screen, frameCount);
            do {
                encoder.encode(packet, picture);
                if (packet.isComplete()) {
                    muxer.write(packet, false);
                }
            } while (packet.isComplete());
    }

    @Override
        public void finish() {
             do {
                encoder.encode(packet, null);
                if (packet.isComplete()) {
                    muxer.write(packet, false);
                }
            } while (packet.isComplete());

            muxer.close();
    }

  
    
}
