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

import com.jmathanim.mathobjects.MathObject;

/**
 * This animation execute a single command. It is used to encapsulate ceratin
 * commands into an Animation container like AnimationGroup or Concatenate
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class SingleCommandAnimation extends Animation {

    /**
     * Creates a new SingleCommandAnimation. The default duration of this
     * animation is 0.
     */
    public SingleCommandAnimation() {
        super(0);
    }

    @Override
    public boolean processAnimation() {
        super.processAnimation();
        command();
        return true;// Finish the animation inmediately
    }

    /**
     * Command to execute. This should be implemented in the implementing class
     */
    public abstract void command();

    @Override
    public void doAnim(double t) {
         super.doAnim(t);
    }

    @Override
    public void cleanAnimationAt(double t) {
    }

    @Override
    public void prepareForAnim(double t) {
    }

    @Override
    public MathObject getIntermediateObject() {
        return null;
    }
    

}
