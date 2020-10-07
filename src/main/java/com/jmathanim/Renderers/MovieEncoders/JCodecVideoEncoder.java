/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Renderers.MovieEncoders;

import com.jmathanim.Utils.JMathAnimConfig;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jcodec.common.io.FileChannelWrapper;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Rational;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class JCodecVideoEncoder extends VideoEncoder {

    private SeekableByteChannel out;
    private AWTSequenceEncoder encoder;

    @Override
    public void createEncoder(File output, JMathAnimConfig config) throws IOException {
        out = NIOUtils.writableFileChannel(output.getCanonicalPath());
        encoder = new AWTSequenceEncoder(out, Rational.R(config.fps, 1));

    }

    @Override
    public void writeFrame(BufferedImage image, int frameCount) {
        try {
            encoder.encodeImage(image);
        } catch (IOException ex) {
            Logger.getLogger(JCodecVideoEncoder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void finish() {
        try {
            encoder.finish();
        } catch (IOException ex) {
            Logger.getLogger(JCodecVideoEncoder.class.getName()).log(Level.SEVERE, null, ex);
        }
        NIOUtils.closeQuietly(out);
    }

}
