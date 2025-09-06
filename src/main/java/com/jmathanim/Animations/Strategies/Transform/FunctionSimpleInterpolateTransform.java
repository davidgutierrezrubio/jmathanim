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
package com.jmathanim.Animations.Strategies.Transform;

import com.jmathanim.mathobjects.FunctionGraph;

import java.util.function.DoubleBinaryOperator;

/**
 * This function interpolates 2 graph functions.
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class FunctionSimpleInterpolateTransform extends TransformStrategy<FunctionGraph> {

    public final FunctionGraph origin, destiny;
    private final FunctionGraph intermediate;
    private final DoubleBinaryOperator destinyFunction, originFunction;

    public FunctionSimpleInterpolateTransform(double runtime, FunctionGraph origin, FunctionGraph destiny) {
        super(runtime);
        this.origin = origin;
        this.intermediate = origin.copy();
        this.destiny = destiny;
        this.originFunction = this.origin.function;
        this.destinyFunction = this.destiny.function;
    }

    @Override
    public void doAnim(double t) {
        super.doAnim(t);
        double lt = getLT(t);
        double w1 = this.origin.getValue();
        double w2 = this.destiny.getValue();
        this.intermediate.function = (x, w) -> (1 - lt) * originFunction.applyAsDouble(x, w1)
                + lt * destinyFunction.applyAsDouble(x, w2);
        this.intermediate.updatePoints();
        if (isShouldInterpolateStyles()) {
            this.intermediate.getMp().interpolateFrom(origin.getMp(), destiny.getMp(), lt);
        }
    }

    @Override
    public FunctionGraph getOriginObject() {
        return origin;
    }

    @Override
    public FunctionGraph getDestinyObject() {
        return destiny;
    }

    @Override
    public void cleanAnimationAt(double t) {
        double lt = getLT(t);
        if (lt == 0) {//If ends at t=0, keep original
            removeObjectsFromScene(destiny, intermediate);
            addObjectsToscene(origin);
            return;
        }
        if (lt == 1) {//If ends at t=1 keep destiny
            removeObjectsFromScene(origin, intermediate);
            addObjectsToscene(destiny);
            return;
        }
        //Case 0<t<1
        removeObjectsFromScene(origin, destiny);
        addObjectsToscene(intermediate);
    }

    @Override
    public void prepareForAnim(double t) {
        removeObjectsFromScene(origin);
        addObjectsToscene(intermediate);
    }

    @Override
    public FunctionGraph getIntermediateObject() {
        return intermediate;
    }
}
