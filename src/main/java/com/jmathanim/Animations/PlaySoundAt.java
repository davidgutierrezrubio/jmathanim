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
package com.jmathanim.Animations;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class PlaySoundAt extends Animation {

    double timeToPlay;
    boolean isPlayed;
    String soundResourceName;
    boolean greaterOrEqual;

    /**
     * Creates an animation that will play a given sound after runtime is strictly greater than a given one
     * @param runTime Duration of animations (in seconds)
     * @param timeToPlay Time parameter to play sound. A value between 0 and 1.
     * @param soundResourceName Resource name of sound to play. Use the conventions of ResourceLoader class.
     * @return The created animation
     */
    public static PlaySoundAt makeStrict(double runTime, double timeToPlay, String soundResourceName) {
        PlaySoundAt resul = new PlaySoundAt(runTime, timeToPlay, soundResourceName);
        resul.greaterOrEqual=false;
        return resul;
    }
    /**
     * Creates an animation that will play a given sound after runtime is greater or equal than a given one
     * @param runTime Duration of animations (in seconds)
     * @param timeToPlay Time parameter to play sound. A value between 0 and 1.
     * @param soundResourceName Resource name of sound to play. Use the conventions of ResourceLoader class.
     * @return The created animation
     */
    public static PlaySoundAt make(double runTime, double timeToPlay, String soundResourceName) {
        PlaySoundAt resul = new PlaySoundAt(runTime, timeToPlay, soundResourceName);
        resul.greaterOrEqual=true;
        return resul;
    }
    
    private PlaySoundAt(double runTime, double timeToPlay, String soundResourceName) {
        super(runTime);
        this.timeToPlay = timeToPlay;
        this.isPlayed = false;
        this.soundResourceName = soundResourceName;
        this.greaterOrEqual = false;
    }

    @Override
    public void doAnim(double t) {
        if (isPlayed) {
            return;
        }
        double lt = getLambda().applyAsDouble(t);
        if (checkCondition(lt)) {
            scene.playSound(soundResourceName);
            isPlayed = true;
        }
    }

    private boolean checkCondition(double lt) {
        if (greaterOrEqual) {
            return (lt >= timeToPlay);
        } else {
            return (lt > timeToPlay);
        }
    }
}
