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

import com.jmathanim.Utils.Anchor.Type;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Shape;

/**
 * This class animates an object moving it through a given path. An anchor
 * determines what point of the object will locate at the moving point of the
 * path
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class MoveAlongPath extends Animation {

    private JMPath path;
    private MathObject mobjTransformed;
    private Type anchorType;

    /**
     * Animates an object moving it through a given path. An anchor determines
     * what point of the object will locate at the moving point of the path
     *
     * @param runtime Duration in seconds
     * @param sh Shape whose path will be the trajectory
     * @param mobjTransformed Object to move
     * @param anchorType Anchor that determines which point of the object will
     * lie in the path
     */
    public MoveAlongPath(double runtime, Shape sh, MathObject mobjTransformed, Type anchorType) {
        this(runtime, sh.getPath(), mobjTransformed, anchorType);
    }

    /**
     * Animates an object moving it through a given path. An anchor determines
     * what point of the object will locate at the moving point of the path
     *
     *
     * @param runtime Duration in seconds
     * @param path Trajectory path
     * @param mobjTransformed Object to move
     * @param anchorType Anchor that determines which point of the object will
     * lie in the path
     */
    public MoveAlongPath(double runtime, JMPath path, MathObject mobjTransformed, Type anchorType) {
        super(runtime);
        this.path = path;
        this.mobjTransformed = mobjTransformed;
        this.anchorType = anchorType;
    }

    @Override
    public void initialize(JMathAnimScene scene) {
        super.initialize(scene);
        saveStates(mobjTransformed);
        addObjectsToscene(mobjTransformed);
    }

    @Override
    public void doAnim(double t) {
        double lt = lambda.applyAsDouble(t);
        restoreStates(mobjTransformed);
        mobjTransformed.putAt(path.getPointAt(lt), this.anchorType);
    }

    @Override
    public void finishAnimation() {
         super.finishAnimation();
        doAnim(1);
    }


}
