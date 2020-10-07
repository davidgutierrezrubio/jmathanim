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

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public abstract class VideoEncoder {
    public abstract void createEncoder(File output,JMathAnimConfig config) throws IOException;
    public abstract void writeFrame(BufferedImage image,int frameCount);
    public abstract void finish();
}
