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

import com.jmathanim.Animations.Animation;
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.FunctionGraph;

/**
 * This function interpolates 2 graph functions.
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class FunctionSimpleInterpolateTransform extends Animation {

    public final FunctionGraph gfObj, gfDst;
    private final MODrawProperties mpBase;

    public FunctionSimpleInterpolateTransform(double runtime, FunctionGraph gfObj, FunctionGraph gfDst) {
        super(runtime);
        this.gfObj = gfObj;
        this.gfDst = gfDst;
        mpBase = this.gfObj.getMp().copy();
    }

    @Override
    public void initialize(JMathAnimScene scene) {
        super.initialize(scene);
        addObjectsToscene(gfObj);
    }

    @Override
    public void doAnim(double t) {
        double lt = lambda.applyAsDouble(t);
        this.gfObj.function = (x) -> (1 - lt) * this.gfObj.functionBase.applyAsDouble(x)
                + lt * this.gfDst.function.applyAsDouble(x);
        this.gfObj.updatePoints();
        if (isShouldInterpolateStyles()) {
            this.gfObj.getMp().interpolateFrom(mpBase, gfDst.getMp(), lt);
        }
    }

    @Override
    public void finishAnimation() {
        super.finishAnimation();
        // Base function is now the new function
        this.gfObj.functionBase = this.gfDst.function;
    }

}
