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

import com.jmathanim.MathObjects.Shape;
import com.jmathanim.MathObjects.Shapes.Line;

/**
 * Animation to create infinite lines. Temporarily replaces the line with a
 * segment to create it.
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class LineCreationAnimation extends SimpleShapeCreationAnimation {

    Line line;


    public LineCreationAnimation(double runtime, Line line) {
        super(runtime,new Shape());
        this.line = line;

    }

    @Override
    public boolean doInitialization() {
        originShape.copyStateFrom(line.toSegment(line.getCamera()));
        return super.doInitialization();
    }

    @Override
    public void cleanAnimationAt(double t) {
        super.cleanAnimationAt(t);
        double lt = getLT(t);
        if (lt == 0) {//Ended at t=0, nothing remains...
            removeObjectsFromScene(originShape, line);
            return;
        }
        if (lt == 1) {//Only remains the full line
            removeObjectsFromScene(originShape);
            addObjectsToscene(line);
            return;
        }
        //0<t<1, only remains the created segment
        removeObjectsFromScene(line);

    }

    @Override
    public void prepareForAnim(double t) {
        removeObjectsFromScene(line);
       super.prepareForAnim(t);
    }
}
