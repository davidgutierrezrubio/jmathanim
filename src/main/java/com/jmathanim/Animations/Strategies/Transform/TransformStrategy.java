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
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;

/**
 *
 * @author David
 */
public abstract class TransformStrategy extends AnimationWithEffects {

    OptimizePathsStrategy optimizeStrategy = null;
    protected MathObject origin;
    protected MathObject destiny;
    protected MathObject intermediate;
    private boolean destinyWasAddedAtFirst, originWasAddedAtFirst;

    public TransformStrategy(double runTime) {
        super(runTime);
    }

    @Override
    public MathObject getIntermediateObject() {
        return intermediate;
    }

    public MathObject getOriginObject() {
        return origin;
    }

    public MathObject getDestinyObject() {
        return destiny;
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
        destinyWasAddedAtFirst = scene.isInScene(destiny);
        originWasAddedAtFirst = scene.isInScene(origin);
        return true;
    }

    @Override
    public void cleanAnimationAt(double t) {
        double lt = getLT(t);
        if (lt == 0) {
            removeObjectsFromScene(intermediate, destiny);
            if (originWasAddedAtFirst) {
                addObjectsToscene(origin);
            } else {
                removeObjectsFromScene(origin);
            }
            return;
        }
        if (lt == 1) {
            removeObjectsFromScene(intermediate, origin);
            addObjectsToscene(destiny);
            return;
        }
        removeObjectsFromScene(destiny, origin);
        addObjectsToscene(intermediate);
    }

    @Override
    public void prepareForAnim(double t) {
        removeObjectsFromScene(origin);
        addObjectsToscene(intermediate);
        if (destinyWasAddedAtFirst) {
            addObjectsToscene(destiny);
        } else {
            removeObjectsFromScene(destiny);
        }
    }

}
