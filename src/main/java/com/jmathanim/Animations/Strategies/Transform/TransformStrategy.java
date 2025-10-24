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
import com.jmathanim.MathObjects.MathObject;

/**
 * @author David
 */
public abstract class TransformStrategy<T extends MathObject<?>> extends AnimationWithEffects {

    protected boolean destinyWasAddedAtFirst, originWasAddedAtFirst;
    OptimizePathsStrategy optimizeStrategy = null;
    private T origin;
    private T destiny;
    private T intermediate;

    public TransformStrategy(double runTime) {
        super(runTime);
    }

    @Override
    public T getIntermediateObject() {
        return intermediate;
    }

    public T getOriginObject() {
        return origin;
    }

    public T getDestinyObject() {
        return destiny;
    }

    public void setOrigin(T origin) {
        this.origin = origin;
    }

    public void setDestiny(T destiny) {
        this.destiny = destiny;
    }

    public void setIntermediate(T intermediate) {
        this.intermediate = intermediate;
    }

    /**
     * Sets the optimization strategy.If null, the animation will try to find the most suitable optimization.
     *
     * @param strategy Optimization strategy
     * @return This object
     */
    public TransformStrategy<T> setOptimizationStrategy(OptimizePathsStrategy strategy) {
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
            cleanAt0();
            return;
        }
        if (lt == 1) {
            cleanAt1();
            return;
        }
        cleanAtIntermediate();
    }

    protected void cleanAtIntermediate() {
        removeObjectsFromScene(getDestinyObject(), getOriginObject());
        addObjectsToscene(getIntermediateObject());
    }

    protected void cleanAt1() {
        removeObjectsFromScene(getIntermediateObject(), getOriginObject());
        addObjectsToscene(getDestinyObject());
    }

    protected void cleanAt0() {
        removeObjectsFromScene(getIntermediateObject(), getDestinyObject());
        if (originWasAddedAtFirst) {
            addObjectsToscene(getOriginObject());
        } else {
            removeObjectsFromScene(getOriginObject());
        }
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
