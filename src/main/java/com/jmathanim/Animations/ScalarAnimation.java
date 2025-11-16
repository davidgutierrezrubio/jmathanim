/*
 * Copyright (C) 2022 David
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

import com.jmathanim.MathObjects.MathObject;
import com.jmathanim.MathObjects.hasScalarParameter;

/**
 * Animates a Scalar object
 *
 * @author David Gutierrez Rubio
 */
public class ScalarAnimation extends Animation {

    hasScalarParameter[] scalarsToAnimate;
    private final double b;
    private final double a;

    /**
     * Creates a new Scalar animation that animates the Scalar object between a
     * and b
     *
     * @param runTime Runtime in seconds
     * @param objects Any object that implements the interface
     * hasScalarParameter that will be animated
     * @param a Initial value
     * @param b End value
     * @return The animation created
     */
    public static ScalarAnimation make(double runTime, double a, double b, hasScalarParameter... objects) {
        return new ScalarAnimation(runTime, a, b, objects);
    }

    private ScalarAnimation(double runTime, double a, double b, hasScalarParameter... scalarsToAnimate) {
        super(runTime);
        this.scalarsToAnimate = scalarsToAnimate;
        this.a = a;
        this.b = b;

    }

    @Override
    public void doAnim(double t) {
        super.doAnim(t);
        double lt = getLT(t);
        for (hasScalarParameter obj : scalarsToAnimate) {
            obj.setValue(a + (b - a) * lt);
        }

    }

    @Override
    public void cleanAnimationAt(double t) {
    }

    @Override
    public void prepareForAnim(double t) {
    }

    @Override
    public MathObject<?>  getIntermediateObject() {
        return null;
    }
}
