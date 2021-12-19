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
package com.jmathanim.Animations.Strategies.ShowCreation;

import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Shape;

/**
 * An animation that draws a Shape
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class SimpleShapeCreationAnimation extends AbstractCreationStrategy {

    private final Shape mobj;

    public SimpleShapeCreationAnimation(double runtime, Shape mobj) {
        super(runtime);
        this.mobj = mobj;
    }

    @Override
    public void initialize(JMathAnimScene scene) {
        super.initialize(scene);
//        removeObjectsFromScene(this.mobj);

    }

    @Override
    public void doAnim(double t) {
        this.mobj.visible(false);
        double lt = getLambda().applyAsDouble(t);
        intermediateShape = this.mobj.getSubShape(0, lt).visible(lt > 0);
        scene.addOnce(intermediateShape);
    }
    private MathObject intermediateShape;

    @Override
    public void finishAnimation() {
        super.finishAnimation();
        this.mobj.visible(true);
        removeObjectsFromScene(intermediateShape);
        double lt = getLambda().applyAsDouble(1);
        if (lt == 1) {
            addObjectsToscene(mobj);
        } else if (lt == 0) {
            removeObjectsFromScene(mobj);
        }
        if ((lt > 0) && (lt < 1)) {
            Shape sh = this.mobj.getSubShape(0, lt);
            this.mobj.getPath().clear();
            this.mobj.getPath().addJMPointsFrom(sh.getPath());
            addObjectsToscene(this.mobj);
        }

    }

}
