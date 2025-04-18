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
package com.jmathanim.mathobjects;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Constructible.Constructible;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.EmptyRect;
import com.jmathanim.Utils.Rect;
import com.jmathanim.jmathanim.JMathAnimScene;

/**
 * A value
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Scalar extends Constructible implements hasScalarParameter{

    public double value;

    public static Scalar make(double scalar) {
        return new Scalar(scalar);
    }

    private Scalar(double scalar) {
        this.value = scalar;
    }

    @Override
    public Scalar copy() {
        double sc = this.value;
        return new Scalar(sc);
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r, Camera cam) {
        // Nothing to do here
    }

    @Override
    public Rect computeBoundingBox() {
        return new EmptyRect();// Nothing
    }

    @Override
    public String toString() {
        return "Scalar{" + "value=" + value + '}';
    }

    @Override
    public void copyStateFrom(MathObject obj) {
         super.copyStateFrom(obj);
        if (!(obj instanceof Scalar)) {
            return;
        }
        Scalar sc = (Scalar) obj;
        this.value = sc.value;
    }

    @Override
    public MathObject getMathObject() {
        return new NullMathObject();
    }

    @Override
    public void rebuildShape() {
    }

    @Override
    public Constructible applyAffineTransform(AffineJTransform transform) {
        return this;
    }

    @Override
    public double getScalar() {
        return value;
    }

    @Override
    public void setScalar(double scalar) {
        value=scalar;
    }

    @Override
    public boolean isVisible() {
        return false;
    }
}
