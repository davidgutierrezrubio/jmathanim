/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Renderers.MovieEncoders;

import com.jmathanim.Renderers.Java2DRenderer;
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
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
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
            Logger.getLogger(Java2DRenderer.class.getName()).log(Level.SEVERE, null, ex);
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
