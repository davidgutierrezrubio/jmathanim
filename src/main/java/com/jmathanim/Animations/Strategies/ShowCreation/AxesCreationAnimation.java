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
import com.jmathanim.Animations.AnimationGroup;
import com.jmathanim.Animations.Commands;
import com.jmathanim.Animations.Concatenate;
import com.jmathanim.Animations.ShowCreation;
import com.jmathanim.Animations.WaitAnimation;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Axes;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.TickAxes;
import java.util.ArrayList;

/**
 * Animation to crate axes. This strategy is automatically chosen by the
 * ShowCreation class when passed an Axes object.
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class AxesCreationAnimation extends Animation {

    private Axes axes;
    ArrayList<MathObject> objectsToRemoveWhenFinished;
    Concatenate anim;

    public AxesCreationAnimation(double runTime, Axes axes) {
        super(runTime);
        this.axes = axes;
        objectsToRemoveWhenFinished = new ArrayList<>();
    }

    @Override
    public void initialize(JMathAnimScene scene) {
        super.initialize(scene);
        anim = new Concatenate();
        axes.update(scene);

        //Axes
        AnimationGroup ag = new AnimationGroup(new ShowCreation(.5 * runTime, axes.getxAxis()), new ShowCreation(runTime, axes.getyAxis()));
        anim.add(ag);
        objectsToRemoveWhenFinished.add(axes.getxAxis());
        objectsToRemoveWhenFinished.add(axes.getyAxis());

        ag = new AnimationGroup();
        //X Ticks
        int n = 0;
        for (TickAxes t : axes.getXticks()) {
            if (t.shouldDraw(scene.getCamera())) {
                ag.add(new Concatenate(new WaitAnimation(.15 * .5 * runTime * n / axes.getXticks().size()), Commands.fadeIn(.85 * .5 * runTime, t.getLegend())));
                ag.add(new Concatenate(new WaitAnimation(.15 * .5 * runTime * n / axes.getXticks().size()), Commands.fadeIn(.85 * .5 * runTime, t.getTick())));
                objectsToRemoveWhenFinished.add(t.getLegend());
                objectsToRemoveWhenFinished.add(t.getTick());
            }
            n++;
        }

        //Y Ticks
        n = 0;
        for (TickAxes t : axes.getYticks()) {
            if (t.shouldDraw(scene.getCamera())) {
                ag.add(new Concatenate(new WaitAnimation(.15 * .5 * runTime * n / axes.getXticks().size()), Commands.fadeIn(.85 * .5 * runTime, t.getLegend())));
                ag.add(new Concatenate(new WaitAnimation(.15 * .5 * runTime * n / axes.getXticks().size()), Commands.fadeIn(.85 * .5 * runTime, t.getTick())));
                objectsToRemoveWhenFinished.add(t.getLegend());
                objectsToRemoveWhenFinished.add(t.getTick());
            }
            n++;
        }
        anim.add(ag);

        anim.initialize(scene);
    }

    @Override
    public boolean processAnimation() {
        return anim.processAnimation(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doAnim(double t) {
    }

    @Override
    public void finishAnimation() {
        super.finishAnimation();
        anim.finishAnimation();
        scene.add(axes);
        for (MathObject ob : objectsToRemoveWhenFinished) {
            scene.remove(ob);
        }
    }

}
