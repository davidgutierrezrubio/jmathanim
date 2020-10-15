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

package com.jmathanim.Animations;

import com.jmathanim.Animations.commands.Commands;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;

/**
 * Performs a short scale up and down to highlight an object
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Highlight extends Animation {

    ApplyCommand scale;
    public double standOutFactor = 1.1;

    public Highlight(double runTime, MathObject... objs) {
        super(runTime);
        scale = Commands.scale(1, null, standOutFactor, objs);
    }

    @Override
    public void initialize() {
        scale.initialize();
    }

    @Override
    public void doAnim(double t, double lt) {
        double tt = 4 * t * (1 - t);
        double ltt = 4 * lt * (1 - lt);
        scale.doAnim(tt, ltt);
    }

    @Override
    public void finishAnimation() {
        scale.doAnim(0, 0);
    }

    @Override
    public void addObjectsToScene(JMathAnimScene scene) {
    }

}
