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
package com.jmathanim.Animations;

import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class MoveAlongPath extends Animation {

    JMPath path;
    MathObject mobjTransformed;

     public MoveAlongPath(double runtime,Shape sh, MathObject mobjTransformed) {
         this(runtime,sh.getPath(),mobjTransformed);
     }
    
    public MoveAlongPath(double runtime,JMPath path, MathObject mobjTransformed) {
        super(runtime);
        this.path = path;
        this.mobjTransformed = mobjTransformed;
    }

    
    @Override
    public void initialize() {
        mobjTransformed.saveState();
    }

    @Override
    public void doAnim(double t, double lt) {
        mobjTransformed.restoreState();
        mobjTransformed.moveTo(path.getPointAt(lt).v);
    }

    @Override
    public void finishAnimation() {
        doAnim(1, 1);
    }

    @Override
    public void addObjectsToScene(JMathAnimScene scene) {
        scene.add(mobjTransformed);
    }

}
