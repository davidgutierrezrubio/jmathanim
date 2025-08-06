/*
 * Copyright (C) 2021 David
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
package com.jmathanim.Animations.Strategies.Transform;

import com.jmathanim.Animations.AnimationWithEffects;
import com.jmathanim.Animations.Strategies.Transform.Optimizers.OptimizePathsStrategy;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David
 */
public abstract class TransformStrategy extends AnimationWithEffects {

    OptimizePathsStrategy optimizeStrategy = null;
    private MathObject origin;
    private MathObject destiny;
    private MathObject intermediate;
    private boolean destinyWasAddedAtFirst, originWasAddedAtFirst;

    public TransformStrategy(double runTime) {
        super(runTime);
    }

    @Override
    public <T extends MathObject> T getIntermediateObject() {
        return (T) intermediate;
    }

    public <T extends MathObject> T  getOriginObject() {
        return (T) origin;
    }

    public <T extends MathObject> T  getDestinyObject() {
        return (T) destiny;
    }

    public void setOrigin(MathObject origin) {
        this.origin = origin;
    }

    public void setDestiny(MathObject destiny) {
        this.destiny = destiny;
    }

    public void setIntermediate(MathObject intermediate) {
        this.intermediate = intermediate;
    }

    /**
     * Sets the optimization strategy.If null, the animation will try to find
     * the most suitable optimization.
     *
     * @param strategy Optimization strategy
     * @return This object
     */
    public TransformStrategy setOptimizationStrategy(OptimizePathsStrategy strategy) {
        optimizeStrategy = strategy;
        return this;
    }

    @Override
    public boolean doInitialization() {
        super.doInitialization();
        destinyWasAddedAtFirst = scene.isInScene(getDestinyObject());
        originWasAddedAtFirst = scene.isInScene(getOriginObject());
        return true;
    }

    @Override
    public void cleanAnimationAt(double t) {
        double lt = getLT(t);
        if (lt == 0) {
            removeObjectsFromScene(getIntermediateObject(), getDestinyObject());
            if (originWasAddedAtFirst) {
                addObjectsToscene(getOriginObject());
            } else {
                removeObjectsFromScene(getOriginObject());
            }
            return;
        }
        if (lt == 1) {
            removeObjectsFromScene(getIntermediateObject(), getOriginObject());
            addObjectsToscene(getDestinyObject());
            return;
        }
        removeObjectsFromScene(getDestinyObject(), getOriginObject());
        addObjectsToscene(getIntermediateObject());
    }

    @Override
    public void prepareForAnim(double t) {
        removeObjectsFromScene(getOriginObject());
        addObjectsToscene(getIntermediateObject());
        if (destinyWasAddedAtFirst) {
            addObjectsToscene(getDestinyObject());
        } else {
            removeObjectsFromScene(getDestinyObject());
        }
    }

}
