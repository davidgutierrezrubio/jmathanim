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

    public TransformStrategy(double runTime) {
        super(runTime);
    }

    abstract public MathObject getOriginObject();

    abstract public MathObject getDestinyObject();

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
    public void initialize(JMathAnimScene scene) {
        super.initialize(scene);
        //Remove origin object from scene and add intermediate
        removeObjectsFromScene(getOriginObject());
        addObjectsToscene(getIntermediateObject());
    }

    @Override
    public void finishAnimation() {
        super.finishAnimation();
        final MathObject intermediateTransformedObject = getIntermediateObject();
        getDestinyObject().copyStateFrom(intermediateTransformedObject);
        // Remove fist object and add the second to the scene
        addObjectsToscene(getDestinyObject());
        removeObjectsFromScene(getOriginObject(), intermediateTransformedObject);
    }
}
