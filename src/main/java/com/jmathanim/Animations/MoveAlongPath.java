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

import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.Anchor.Type;
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

/**
 * This class animates an object moving it through a given path. An anchor
 * determines what point of the object will locate at the moving point of the
 * path
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class MoveAlongPath extends Animation {

    private final JMPath path;
    private final MathObject mobjTransformed;
    private final Type anchorType;
    boolean shouldRotate;
    boolean parametrized;

    /**
     * Creates an animation of an object moving it through a given path.An
     * anchor determines what point of the object will locate at the moving
     * point of the path
     *
     * @param runtime Duration in seconds
     * @param path
     * @param mobjTransformed Object to move
     * @param anchorType Anchor that determines which point of the object will
     * lie in the path
     * @param shouldRotate True if object should rotate according to tangent
     * line of path
     * @param parametrized If true, a unit parametrization will be used, so
     * that, with a linear lambda, object should move at constant speed along
     * the path. If false, speeds may vary depending on the cubic Bezier
     * segments.
     * @return The created animation
     */
    public static MoveAlongPath make(double runtime, JMPath path, MathObject mobjTransformed, Type anchorType, boolean shouldRotate, boolean parametrized) {
        MoveAlongPath resul = new MoveAlongPath(runtime, path, mobjTransformed, anchorType, shouldRotate, parametrized);
        return resul;
    }

    /**
     * Creates an animation of an object moving it through a given Shape.An
     * anchor determines what point of the object will locate at the moving
     * point of the path
     *
     * @param runtime Duration in seconds
     * @param sh Shape whose path will be the trajectory
     * @param mobjTransformed Object to move
     * @param anchorType Anchor that determines which point of the object will
     * lie in the path
     * @param shouldRotate True if object should rotate according to tangent
     * line of path
     * @param parametrized If true, a unit parametrization will be used, so
     * that, with a linear lambda, object should move at constant speed along
     * the path. If false, speeds may vary depending on the cubic Bezier
     * segments.
     * @return The created animation
     */
    public static MoveAlongPath make(double runtime, Shape sh, MathObject mobjTransformed, Type anchorType, boolean shouldRotate, boolean parametrized) {
        MoveAlongPath resul = new MoveAlongPath(runtime, sh.getPath(), mobjTransformed, anchorType, shouldRotate, parametrized);
        return resul;
    }

    private MoveAlongPath(double runtime, JMPath path, MathObject mobjTransformed, Type anchorType, boolean shouldRotate, boolean parametrized) {
        super(runtime);
        this.path = path;
        this.mobjTransformed = mobjTransformed;
        this.anchorType = anchorType;//Anchor.reverseAnchorPoint(anchorType);
        this.parametrized = parametrized;
        this.shouldRotate = shouldRotate;
    }

      @Override
    public boolean doInitialization() {
        super.doInitialization();
        saveStates(mobjTransformed);
        return true;
    }

    @Override
    public void doAnim(double t) {
        super.doAnim(t);
        double lt = getLT(t);
        restoreStates(mobjTransformed);
        Point destinyPoint = (parametrized ? path.getParametrizedPointAt(lt) : path.getJMPointAt(lt).p);
        Point anchPoint = Anchor.getAnchorPoint(mobjTransformed, anchorType);
        mobjTransformed.shift(anchPoint.to(destinyPoint));

        if (shouldRotate) {
            JMPathPoint pp = path.getJMPointAt(lt);
            Vec tangent = pp.p.to(pp.cpExit);
            mobjTransformed.rotate(destinyPoint, tangent.getAngle());
        }
    }

    @Override
    public void finishAnimation() {
        super.finishAnimation();
        doAnim(1);
    }

    @Override
    public void cleanAnimationAt(double t) {
    }

    @Override
    public void prepareForAnim(double t) {
        addObjectsToscene(mobjTransformed);
    }

    @Override
    public MathObject getIntermediateObject() {
        return mobjTransformed;
    }
}
