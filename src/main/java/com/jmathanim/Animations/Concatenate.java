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
package com.jmathanim.Animations;

/**
 * Stores 2 or more animations and play them in sequential order. The total runtime of this animation is the sum of
 * runtimes of all animations played
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Concatenate extends JoinAnimation {




    /**
     * Creates a new Concatenate animation with the given animations
     *
     * @param anims Animations to concatenate (varargs)
     * @return The created anuimation
     */
    public static Concatenate make(Animation... anims) {
        return new Concatenate(anims);
    }

    protected Concatenate(Animation... anims) {
        super(0d, anims);
    }

    @Override
    public boolean doInitialization() {
        //First I must compute the total runtime
        double runt = 0;
        for (Animation animation : animations) {
            runt += animation.getRunTime();
        }
        setRunTime(runt);
        return super.doInitialization();
    }
}
