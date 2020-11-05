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

import com.jmathanim.Animations.Strategies.TransformStrategy;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Arrow2D;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class ArrowCreationStrategy extends TransformStrategy {

    private final Arrow2D obj;


    public ArrowCreationStrategy(Arrow2D obj, double runTime, JMathAnimScene scene) {
        super(scene);
        this.obj=obj;
    }

    @Override
    public void prepareObjects() {
        obj.saveState();
    }

    @Override
    public void applyTransform(double t, double lt) {
        obj.restoreState();
        obj.scale(obj.getBody().getPoint(0), lt, lt);
        obj.scaleArrowHead(lt);
    }

    @Override
    public void finish() {
        applyTransform(1, 1);
    }

    @Override
    public void addObjectsToScene() {
        scene.add(obj);
    }

}
