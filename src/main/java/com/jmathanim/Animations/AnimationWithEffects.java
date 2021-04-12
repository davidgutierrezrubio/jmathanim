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
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class AnimationWithEffects extends Animation {

    protected Double jumpHeight;
    protected Integer numTurns;
    protected Double alphaScaleEffect;
    protected Double scaleEffect;

    public enum JumpType {
        PARABOLICAL, ELLIPTICAL, TRIANGULAR, SINUSOIDAL2, SINUSOIDAL, PARABOLLICALRECT
    }
    JumpType jumpType;

    public AnimationWithEffects(double runTime) {
        super(runTime);
        this.jumpHeight = null;
        this.numTurns = null;
        this.scaleEffect = null;
        this.alphaScaleEffect = null;
        jumpType = null;
    }

    @Override
    public void doAnim(double t) {
    }

    public <T extends AnimationWithEffects> T addJumpEffect(double height) {
        this.jumpHeight = height;
        jumpType = JumpType.PARABOLICAL;
        return (T) this;
    }

    public <T extends AnimationWithEffects> T addJumpEffect(double height, JumpType type) {
        this.jumpHeight = height;
        jumpType = type;
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
        if ((scaleEffect != null) && (scaleEffect != 1)) {
            double L = 4 * (1 - scaleEffect);
            double scalelt = 1 - t * (1 - t) * L;
            obj.scale(scalelt);
        }
    }

    protected void applyRotationEffect(double t, MathObject obj) {
        if ((numTurns != null) && (numTurns != 0)) {
            double rotateAngle = 2 * PI * numTurns;
            obj.rotate(rotateAngle * t);
        }
    }

    protected void applyAlphaScaleEffect(double t, MathObject obj) {
        if ((alphaScaleEffect != null) && (alphaScaleEffect != 1)) {
            double L = 4 * (1 - alphaScaleEffect);
            double alphaScalelt = 1 - t * (1 - t) * L;
            obj.drawAlpha(alphaScalelt);
            obj.fillAlpha(alphaScalelt);
        }
    }

    protected void applyJumpEffect(double t, Vec jumpVector, MathObject obj) {
        try {
            if (jumpHeight == 0) {
                return;
            }

            double jlt = 0;
            switch (jumpType) {
                case PARABOLICAL:
                    jlt = 4 * t * (1 - t) * jumpHeight;
                    break;
                case ELLIPTICAL:
                    jlt = 2 * Math.sqrt(t * (1 - t)) * jumpHeight;
                    break;
                case TRIANGULAR:
                    jlt = (t < .5 ? t : 1 - t) * jumpHeight;
                    break;
                case SINUSOIDAL2:
                    jlt = Math.sin(2 * t * PI) * jumpHeight;
                    break;
                case SINUSOIDAL:
                    jlt = Math.sin(t * PI) * jumpHeight;
                    break;
                case PARABOLLICALRECT:
                    double a = .2;
                    if (t < a) {
                        jlt = 2 * t / a * (1 - 0.5 * t / a);
                    } else if (t > 1 - a) {
                        jlt = 2 * (1 - t) / a * (1 - 0.5 * (1 - t) / a);
                    }
                    if ((t >= a) && (t <= 1 - a)) {
                        jlt = 1;
                    }
                    jlt*=jumpHeight;
                    break;

            }

            obj.shift(jumpVector.mult(jlt));
        } catch (NullPointerException e) {
        }
    }

    public boolean shouldApplyEffects() {
        return ((0 != jumpHeight) || (1 != alphaScaleEffect) || (1 != scaleEffect) || (0 != numTurns));
    }

    public void copyEffectParametersFrom(AnimationWithEffects obj) {
        if (obj.jumpHeight != null) {
            this.addJumpEffect(obj.jumpHeight, obj.jumpType);
        }
        if (obj.alphaScaleEffect != null) {
            this.addAlphaEffect(obj.alphaScaleEffect);
        }
        if (obj.numTurns != null) {
            this.addRotationEffect(obj.numTurns);
        }
        if (obj.scaleEffect != null) {
            this.addScaleEffect(obj.scaleEffect);
        }
    }

}
