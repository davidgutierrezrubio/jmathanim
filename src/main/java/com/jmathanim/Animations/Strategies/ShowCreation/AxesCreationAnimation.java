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

import com.jmathanim.Animations.AnimationGroup;
import com.jmathanim.Animations.ShowCreation;
import com.jmathanim.MathObjects.Axes.Axes;
import com.jmathanim.MathObjects.Axes.TickAxes;
import com.jmathanim.MathObjects.Coordinates;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimConfig;

/**
 * Animation to crate axes. This strategy is automatically chosen by the
 * ShowCreation class when passed an Axes object.
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public final class AxesCreationAnimation extends AnimationGroup implements CreationStrategy {

    private final Axes axes;
//	ArrayList<MathObject<?>> objectsToRemoveWhenFinished;

    public AxesCreationAnimation(double runTime, Axes axes) {
        super();
        this.runTime = runTime;
        this.axes = axes;
//		objectsToRemoveWhenFinished = new ArrayList<>();
        axes.update(JMathAnimConfig.getConfig().getScene());//In case we need to update elements
        add(ShowCreation.make(runTime, axes.getxAxis()));
        add(ShowCreation.make(runTime, axes.getyAxis()));
        for (TickAxes t : axes.getXticks()) {
            add(ShowCreation.make(runTime, t.getTick()));
            add(ShowCreation.make(runTime, t.getLegend()));
        }
        for (TickAxes t : axes.getYticks()) {
            add(ShowCreation.make(runTime, t.getTick()));
            add(ShowCreation.make(runTime, t.getLegend()));
        }
        addDelayEffect(.4);
    }

    @Override
    public void setPencilPosition(Coordinates previous, Coordinates current) {
    }

    @Override
    public Vec[] getPencilPosition() {
        return null;
    }

}
