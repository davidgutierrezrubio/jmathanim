/*
 * Copyright (C) 2021 David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
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

import com.jmathanim.Utils.Vec;
import static com.jmathanim.jmathanim.JMathAnimScene.PI;
import com.jmathanim.mathobjects.MathObject;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class AnimationWithEffects extends Animation {

    protected double jumpHeight;
    protected int numTurns;
    protected double alphaScaleEffect;
    protected double scaleEffect;

    public AnimationWithEffects(double runTime) {
        super(runTime);
        this.jumpHeight = 0;
        this.numTurns = 0;
        this.scaleEffect = 1;
        this.alphaScaleEffect = 1;
    }

    @Override
    public void doAnim(double t) {
    }

    public <T extends AnimationWithEffects> T addJumpEffect(double height) {
        this.jumpHeight = height;
        return (T) this;
    }

    public <T extends AnimationWithEffects> T addRotationEffect(int numturns) {
        this.numTurns = numturns;
        return (T) this;
    }

    public <T extends AnimationWithEffects> T addAlphaEffect(double alphaScale) {
        this.alphaScaleEffect = alphaScale;
        return (T) this;
    }

    public <T extends AnimationWithEffects> T addScaleEffect(double scale) {
        this.scaleEffect = scale;
        return (T) this;
    }

    protected void applyScaleEffect(double t, MathObject obj) {
        if (scaleEffect != 1) {
            double L = 4 * (1 - scaleEffect);
            double scalelt = 1 - t * (1 - t) * L;
            obj.scale(scalelt);
        }
    }

    protected void applyRotationEffect(double t, MathObject obj) {
        if (numTurns != 0) {
            double rotateAngle = 2 * PI * numTurns;
            obj.rotate(rotateAngle * t);
        }
    }

    protected void applyAlphaScaleEffect(double t, MathObject obj) {
        if (alphaScaleEffect != 1) {
            double L = 4 * (1 - alphaScaleEffect);
            double alphaScalelt = 1 - t * (1 - t) * L;
            obj.drawAlpha(alphaScalelt);
            obj.fillAlpha(alphaScalelt);
        }
    }

    protected void applyJumpEffect(double t, Vec jumpVector, MathObject obj) {
        if (jumpHeight != 0) {
            double jlt = 4 * t * (1 - t);
            obj.shift(jumpVector.mult(jumpHeight*jlt));
        }
    }

}
