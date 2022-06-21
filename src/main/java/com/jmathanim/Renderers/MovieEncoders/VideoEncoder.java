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
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class VideoEncoder {

    /**
     * Creates and initializes the encoder. This method should be called before
     * using the encoder
     *
     * @param config Config class
     * @throws IOException
     */
    public abstract void createEncoder(JMathAnimConfig config) throws IOException;

    /**
     * Write the current generated frame to the encoder queue
     *
     * @param image Frame image
     * @param frameCount Frame number
     */
    public abstract void writeFrame(BufferedImage image, int frameCount);

    /**
     * Finish the encoding, closing the file and doing all necessary operations.
     * If sounds were added to the scene, they are processed now.
     */
    public abstract void finish();

    /**
     * Check if any frame is generated at all
     *
     * @return True if at least one frame was generated, false otherwise.
     */
    public abstract boolean isFramesGenerated();

    /**
     * Adds a sound to the video encoder queue
     *
     * @param soundURL URL of sound file
     * @param miliSeconds Time stamp in miliseconds
     */
    public abstract void addSound(URL soundURL, long miliSeconds);

    /**
     * Add a sound to the video encode queue
     *
     * @param soundItem A SoundItem object with the sound info
     */
    public abstract void addSound(SoundItem soundItem);
}
