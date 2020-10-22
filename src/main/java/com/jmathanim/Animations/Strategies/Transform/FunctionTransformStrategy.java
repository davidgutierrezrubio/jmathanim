/*
 * Copyright (C) 2020 David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
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

import com.jmathanim.Animations.Strategies.TransformStrategy;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.FunctionGraph;

/**
 * This function interpolates 2 graph functions.
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class FunctionTransformStrategy extends TransformStrategy {

    public final FunctionGraph gfObj, gfDst;

    public FunctionTransformStrategy(FunctionGraph gfObj, FunctionGraph gfDst, JMathAnimScene scene) {
        super(scene);
        this.gfObj = gfObj;
        this.gfDst = gfDst;
    }

    @Override
    public void prepareObjects() {
//        this.gfObj.saveState();
    }

    @Override
    public void applyTransform(double t, double lt) {
        this.gfObj.function = (x) -> (1 - lt) * this.gfObj.functionBase.applyAsDouble(x) + lt * this.gfDst.function.applyAsDouble(x);
        this.gfObj.updatePoints();
    }

    @Override
    public void finish() {
        //Base function is now the new function 
        this.gfObj.functionBase = this.gfDst.function;
    }

    @Override
    public void addObjectsToScene() {
    }

}
