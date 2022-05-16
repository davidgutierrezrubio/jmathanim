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
import com.jmathanim.mathobjects.MathObject;
import java.util.function.DoubleBinaryOperator;

/**
 * This function interpolates 2 graph functions.
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class FunctionSimpleInterpolateTransform extends TransformStrategy {

    public final FunctionGraph origin, destiny, intermediate;
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
        double lt = lambda.applyAsDouble(t);
        double w1 = this.origin.getScalar();
        double w2 = this.destiny.getScalar();
        this.intermediate.function = (x, w) -> (1 - lt) * originFunction.applyAsDouble(x, w1)
                + lt * destinyFunction.applyAsDouble(x, w1);
        this.intermediate.updatePoints();
        if (isShouldInterpolateStyles()) {
            this.intermediate.getMp().interpolateFrom(origin.getMp(), destiny.getMp(), lt);
        }
    }

    @Override
    public MathObject getIntermediateTransformedObject() {
        return intermediate;
    }

    @Override
    public MathObject getOriginObject() {
        return origin;
    }

    @Override
    public MathObject getDestinyObject() {
        return destiny;
    }

}
