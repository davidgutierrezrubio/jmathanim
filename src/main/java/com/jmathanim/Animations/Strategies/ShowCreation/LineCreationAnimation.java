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

import com.jmathanim.Animations.Animation;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.Shape;

/**
 * Animation to create infinite lines. Temporarily replaces the line with a
 * segment to create it.
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class LineCreationAnimation extends CreationStrategy {

    Shape segment;
    Line line;
    SimpleShapeCreationAnimation anim;

    public LineCreationAnimation(double runtime, Line line) {
        super(runtime);
        this.line = line;

    }

    @Override
    public void initialize(JMathAnimScene scene) {
        super.initialize(scene);
        segment = line.toSegment(scene.getCamera());
        anim = new SimpleShapeCreationAnimation(this.runTime, segment);
        anim.setLambda(lambda);
        anim.initialize(scene);
        scene.remove(line);
        addObjectsToscene(segment);

    }

    @Override
    public boolean processAnimation() {
        return anim.processAnimation();
    }

    @Override
    public void doAnim(double t) {
    }

    @Override
    public void finishAnimation() {
        super.finishAnimation();
        anim.finishAnimation();
        scene.remove(segment);
        addObjectsToscene(line);

    }

}
