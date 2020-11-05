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

import com.jmathanim.Animations.AffineJTransform;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class HomothecyStrategyTransform extends MatrixTransformStrategy {

    public HomothecyStrategyTransform(Shape mobjTransformed, Shape mobjDestiny,JMathAnimScene scene) {
        super(mobjTransformed, mobjDestiny,scene);
    }

    @Override
    public void applyTransform(double t,double lt) {

        Point A = originalShapeBaseCopy.jmpath.getJMPoint(0).p;
        Point B = originalShapeBaseCopy.jmpath.getJMPoint(1).p;
        Point C = mobjDestiny.jmpath.getJMPoint(0).p;
        Point D = mobjDestiny.jmpath.getJMPoint(1).p;

        AffineJTransform tr = AffineJTransform.createDirect2DHomothecy(A, B, C, D, lt);

        applyMatrixTransform(tr, lt);
    }

    @Override
    public void addObjectsToScene() {
    }

}
