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

import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class AnimationWithEffects extends Animation {

    AnimationEffect effect;

    public AnimationWithEffects(double runTime) {
        super(runTime);
        effect = new AnimationEffect();

    }

    public AnimationEffect getEffect() {
        return effect;
    }

    public <T extends AnimationWithEffects> T addJumpEffect(double height) {
        effect.addJumpEffect(height);
        return (T) this;
    }

    public <T extends AnimationWithEffects> T addJumpEffect(double height, AnimationEffect.JumpType type) {
        effect.addJumpEffect(height, type);
        return (T) this;
    }

    public <T extends AnimationWithEffects> T addRotationEffect(int numTurns) {
        effect.addRotationEffect(numTurns);
        return (T) this;
    }

    public <T extends AnimationWithEffects> T addAlphaEffect(double alphaScale) {
        effect.addAlphaEffect(alphaScale);
        return (T) this;
    }

    public <T extends AnimationWithEffects> T addScaleEffect(double scale) {
        effect.addScaleEffect(scale);
        return (T) this;
    }

    public void prepareJumpPath(Point A, Point B, MathObject obj) {
        effect.prepareJumpPath(A, B, obj);
    }

    protected void applyAnimationEffects(double lt, MathObject obj) {
        effect.applyAnimationEffects(lt, obj);
    }

    protected void copyEffectParametersFrom(AnimationWithEffects anim) {
        effect.copyEffectParametersFrom(anim.getEffect());
    }

    protected void copyEffectParametersTo(AnimationWithEffects anim) {
        anim.copyEffectParametersFrom(this);
    }

    public abstract <T extends MathObject> T getIntermediateObject();
}
