/*
 * Copyright (C) 2022 David Gutierrez Rubio davidgutierrezrubio@gmail.com
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

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a sound element to be inserted in the video
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class SoundItem {

    private long timeStamp;
    private URL soundUrl;
    private double pitch;

    /**
     * Creates a new SoundItem
     *
     * @param timeStamp Time stamp of sound (in miliseconds)
     * @param soundUrl Url of the sound file
     * @param pitch Pitch of the sound
     * @return
     */
    public static SoundItem make(URL soundUrl, long timeStamp, double pitch) {
        return new SoundItem(soundUrl,timeStamp, pitch);
    }

    /**
     * Creates a new SoundItem
     *
     * @param timeStamp Time stamp of sound (in miliseconds)
     * @param soundUrl Url of the sound file
     * @return
     */
    public static SoundItem make(URL soundUrl, long timeStamp) {
        return new SoundItem(soundUrl, timeStamp, 1);
    }

    private SoundItem(URL soundUrl, long timeStamp, double pitch) {
        this.timeStamp = timeStamp;
        this.soundUrl = soundUrl;
        this.pitch = pitch;
    }

    /**
     * Returns the current pitch of the sound
     *
     * @return The pitch. Default value is 1
     */
    public double getPitch() {
        return pitch;
    }

    /**
     * Sets the pitch of the sound.
     *
     * @param pitch New pitch. A value of 1 means sound unaltered
     * @return This object
     */
    public SoundItem setPitch(double pitch) {
        this.pitch = pitch;
        return this;
    }

    public String getPath() {
        String path = "";
        try {
            path = Paths.get(soundUrl.toURI()).toString();
        } catch (URISyntaxException ex) {
            Logger.getLogger(SoundItem.class.getName()).log(Level.SEVERE, null, ex);
        }
        return path;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public URL getSoundUrl() {
        return soundUrl;
    }

    public void setSoundUrl(URL soundUrl) {
        this.soundUrl = soundUrl;
    }

}
