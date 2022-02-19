/*
 * Copyright (C) 2021 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
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
package com.jmathanim.Constructible.Lines;

import com.jmathanim.Constructible.Points.CTPoint;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

/**
 * A straight segment,given by 2 points
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTSegment extends CTLine {

    private final Shape segmentToDraw;

    /**
     * Creates a Constructible segment between 2 points
     *
     * @param A First point
     * @param B Second point
     * @return The created object
     */
    public static CTSegment make(Point A, Point B) {
        return CTSegment.make(CTPoint.make(A), CTPoint.make(B));
    }

    /**
     * Creates a Constructible segment between 2 points
     *
     * @param A First point
     * @param B Second point
     * @return The created object
     */
    public static CTSegment make(CTPoint A, CTPoint B) {
        CTSegment resul = new CTSegment(A, B);
        resul.rebuildShape();
        return resul;
    }

    private CTSegment(CTPoint A, CTPoint B) {
        super(A, B);
        segmentToDraw = Shape.segment(this.A.getMathObject(), this.B.getMathObject());
    }

    /**
     * Creates a Constructible line from a Shape, considering only first and
     * last point
     *
     * @param shape Shape object
     * @return The created object
     */
    public static CTSegment make(Shape shape) {
        return make(shape.getPoint(0), shape.getPoint(-1));
    }

    @Override
    public CTSegment copy() {
        CTSegment copy = CTSegment.make(this.A.copy(), this.B.copy());
        copy.getMp().copyFrom(this.getMp());
        return copy;
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r) {
        segmentToDraw.draw(scene, r);
    }

    @Override
    public Vec getDirection() {
        return getP1().to(getP2());
    }

    @Override
    public Point getP1() {
        return this.A.getMathObject();
    }

    @Override
    public Point getP2() {
        return this.B.getMathObject();
    }

    @Override
    public Shape getMathObject() {
        return segmentToDraw;
    }

    @Override
    public void rebuildShape() {
        // Nothing to do here...
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        scene.registerUpdateable(this.A, this.B);
        setUpdateLevel(Math.max(this.A.getUpdateLevel(), this.B.getUpdateLevel()) + 1);
    }

}
