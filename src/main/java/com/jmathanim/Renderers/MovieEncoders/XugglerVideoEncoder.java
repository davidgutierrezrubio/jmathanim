/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Renderers.MovieEncoders;

import com.jmathanim.Utils.JMathAnimConfig;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class XugglerVideoEncoder extends VideoEncoder {

    IMediaWriter writer;
    private long startTime;
    private double fps;

    @Override
    public void createEncoder(File output, JMathAnimConfig config) throws IOException {
        writer = ToolFactory.makeWriter(output.getCanonicalPath());
        writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, config.mediaW, config.mediaH);
        fps=config.fps;
    }

    @Override
    public void writeFrame(BufferedImage image, int frameCount) {

        BufferedImage bgrScreen = convertToType(image, BufferedImage.TYPE_3BYTE_BGR);
        long nanosecondsElapsed=(long) (1000000000d*frameCount/fps);
        writer.encodeVideo(0, bgrScreen, nanosecondsElapsed, TimeUnit.NANOSECONDS);
        
    }

    @Override
    public void finish() {
        writer.close();
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

}
