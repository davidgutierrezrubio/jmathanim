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
import com.jmathanim.mathobjects.Arrow2D;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class ArrowCreationAnimation extends AbstractCreationStrategy {

    private final Arrow2D obj;

    public ArrowCreationAnimation(double runTime, Arrow2D obj) {
        super(runTime);
        this.obj = obj;
    }

   

    @Override
    public void initialize(JMathAnimScene scene) {
        super.initialize(scene);
        obj.saveState();
    }

    @Override
    public void doAnim(double t) {
        super.doAnim(t);
        double lt = getLT(t);
        obj.restoreState();
        // If there is only head 1 (the ending point), scale from the beginning
        // if there are 2 heads (ending and beginning point), better scale from the
        // center
        Point scaleCenter = (obj.getArrowHead2().size() > 0 ? obj.getCenter() : obj.getBody().getPoint(0));

        obj.scale(scaleCenter, lt, lt);
        obj.scaleArrowHead1(lt * obj.getMp().getScaleArrowHead1());
        obj.scaleArrowHead2(lt * obj.getMp().getScaleArrowHead2());
    }

    @Override
    public void finishAnimation() {
        super.finishAnimation();
        doAnim(1);
    }

    @Override
    public void cleanAnimationAt(double t) {
    }

    @Override
    public void prepareForAnim(double t) {
        addObjectsToscene(obj);
    }
     @Override
    public MathObject getIntermediateObject() {
        return this.obj;
    }
}
