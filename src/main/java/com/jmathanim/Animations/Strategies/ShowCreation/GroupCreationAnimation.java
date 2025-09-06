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
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.Coordinates;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MathObjectGroup;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class GroupCreationAnimation extends AnimationGroup implements CreationStrategy {


    public GroupCreationAnimation(double runtime, MathObjectGroup group) {
        super();
        for (MathObject<?> obj : group.getObjects()) {
           add(new ShowCreation(runtime, obj));
        }
      addDelayEffect(.2);
    }

//    @Override
//    public void initialize(JMathAnimScene scene) {
//        super.initialize(scene);
//        anim.setLambda(lambda);
//        anim.initialize(scene);
//
//    }
//
//    @Override
//    public boolean processAnimation() {
//        return anim.processAnimation();
//    }
//
//    @Override
//    public void finishAnimation() {
//        super.finishAnimation();
//        anim.finishAnimation();
//    }
//
//    @Override
//    public void doAnim(double t) {
//        anim.doAnim(t);
//    }

    @Override
    public void setPencilPosition(Coordinates previous, Coordinates current) {
    }

    @Override
    public Vec[] getPencilPosition() {
        return null;
    }

}
