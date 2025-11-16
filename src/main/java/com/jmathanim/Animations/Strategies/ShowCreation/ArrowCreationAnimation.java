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

import com.jmathanim.MathObjects.Delimiters.Arrow;
import com.jmathanim.MathObjects.MathObject;
import com.jmathanim.Utils.Vec;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class ArrowCreationAnimation extends AbstractCreationStrategy {

    private final Arrow obj;

    public ArrowCreationAnimation(double runTime, Arrow obj) {
        super(runTime);
        this.obj = obj;
    }

    @Override
    public boolean doInitialization() {
        super.doInitialization();
        saveStates(obj);
        return true;
    }

    @Override
    public void doAnim(double t) {
        super.doAnim(t);
        double lt = getLT(t);
        restoreStates(obj);
        // If there is only head 1 (the ending point), scale from the beginning
        // if there are 2 heads (ending and beginning point), better scale from the
        // center
//        Point scaleCenter = (obj.getArrowHead2().size() > 0 ? obj.getCenter() : obj.getBody().getPoint(0));
        Vec scaleCenter = obj.getStart().getVec();
//        obj.scale(scaleCenter, lt, lt);
        obj.setAmplitudeScale(lt);
//        obj.setArrowThickness(obj.getArrowThickness() * lt);
//        obj.scaleArrowHead1(lt * obj.getMp().getScaleArrowHead1());
//        obj.scaleArrowHead2(lt * obj.getMp().getScaleArrowHead2());
    }

    @Override
    public void finishAnimation() {
        super.finishAnimation();
    }

    @Override
    public void cleanAnimationAt(double t) {
        double lt = getLT(t);
        if (lt == 0) {
            removeObjectsFromScene(obj);
        } else {
            addObjectsToscene(obj);
        }
    }

    @Override
    public void prepareForAnim(double t) {
        addObjectsToscene(obj);
    }

    @Override
    public MathObject<?>  getIntermediateObject() {
        return this.obj;
    }
}
