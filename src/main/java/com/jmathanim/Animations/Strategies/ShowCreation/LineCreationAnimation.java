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

import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Shape;

/**
 * Animation to create infinite lines. Temporarily replaces the line with a
 * segment to create it.
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class LineCreationAnimation extends AbstractCreationStrategy {

    Shape segment;
    Line line;
    SimpleShapeCreationAnimation anim;

    public LineCreationAnimation(double runtime, Line line) {
        super(runtime);
        this.line = line;

    }

    @Override
    public MathObject getIntermediateObject() {
        return segment;
    }

    @Override
    public boolean doInitialization() {
        super.doInitialization();
        segment = line.toSegment(line.getCamera());
        anim = new SimpleShapeCreationAnimation(this.runTime, segment);
        anim.setLambda(getTotalLambda());
        return anim.initialize(scene);
    }

    @Override
    public boolean processAnimation() {
        return anim.processAnimation();
    }

    @Override
    public void doAnim(double t) {
        super.doAnim(t);
        anim.doAnim(t);
    }

    @Override
    public void finishAnimation() {
        anim.finishAnimation();
        super.finishAnimation();

    }

    @Override
    public void cleanAnimationAt(double t) {
        double lt = getLT(t);
        if (lt == 0) {//Ended at t=0, nothing remains...
            removeObjectsFromScene(segment, line);
            return;
        }
        if (lt == 1) {//Only remains the full line
            removeObjectsFromScene(segment);
            addObjectsToscene(line);
            return;
        }
        //0<t<1, only remains the created segment
        removeObjectsFromScene(line);
        addObjectsToscene(segment);
    }

    @Override
    public void prepareForAnim(double t) {
        removeObjectsFromScene(line);
        addObjectsToscene(segment);
    }

    @Override
    public void reset() {
        super.reset();
        anim.reset();
    }
}
