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

package com.jmathanim.Animations.Strategies.ShowCreation;

import com.jmathanim.Animations.Animation;
import com.jmathanim.Animations.Concatenate;
import com.jmathanim.Animations.ShowCreation;
import com.jmathanim.Animations.Strategies.TransformStrategy;
import com.jmathanim.Animations.WaitAnimation;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MultiShapeObject;
import com.jmathanim.mathobjects.Shape;
import java.util.ArrayList;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class MultiShapeCreationStrategy extends TransformStrategy {

    private final double timegap;
    private final double runtime;
    private final MultiShapeObject shapes;
    private final ArrayList<Concatenate> creators;

    public MultiShapeCreationStrategy(MultiShapeObject shapes, double timegap, double runtime, JMathAnimScene scene) {
        super(scene);
        this.timegap = timegap;
        this.runtime = runtime;
        this.shapes = shapes;
        creators = new ArrayList<>();
    }

    @Override
    public void prepareObjects() {
        double realRuntime = this.runtime - this.timegap * shapes.shapes.size();
        scene.remove(shapes);
        int n = 0;
        for (Shape sh : shapes.shapes) {
            this.scene.remove(sh);
            WaitAnimation wait = new WaitAnimation(this.timegap * n);
            ShowCreation cr = new ShowCreation(realRuntime, sh);
            Concatenate anim = new Concatenate();
            anim.add(wait);
            anim.add(cr);
            n++;
            creators.add(anim);
            anim.initialize();
        }
    }

    @Override
    public void applyTransform(double t, double lt) {
        for (Animation anim : creators) {
            anim.doAnim(t, lt);
        }
    }

    @Override
    public void finish() {
//       for (Animation anim:creators)
//        {
//            anim.finishAnimation();
//        }
        scene.add(shapes);
    }

    @Override
    public void addObjectsToScene() {
    }

}
