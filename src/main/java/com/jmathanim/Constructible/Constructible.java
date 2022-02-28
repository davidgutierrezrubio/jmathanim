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
package com.jmathanim.Constructible;

import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Styling.Stylable;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Rect;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;

/**
 * This class representas a constructible object, derived from another ones. For
 * example a circle that pass for 3 points is a constructible object. It cannot
 * be transformed nor animated by itself, only changing the objects from which
 * depend
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class Constructible extends MathObject {

    private boolean isMathObjectFree;

    public Constructible() {
        isMathObjectFree = false;
    }

    public boolean isThisMathObjectFree() {
        return isMathObjectFree;
    }

    public void freeMathObject(boolean isMathObjectFree) {
        this.isMathObjectFree = isMathObjectFree;
        rebuildShape();
    }

    /**
     * Returns the computed Mathobject that will be showed on screen
     *
     * @return The MathObject
     */
    public abstract MathObject getMathObject();
    @Override
    public abstract Constructible copy();

    @Override
    public Stylable getMp() {
        return getMathObject().getMp();
    }

    @Override
    public void update(JMathAnimScene scene) {
        rebuildShape();
    }

    abstract public void rebuildShape();

    @Override
    public Rect getBoundingBox() {
        rebuildShape();
        return getMathObject().getBoundingBox();
    }

    @Override
    public Constructible applyAffineTransform(AffineJTransform transform) {
        getMathObject().applyAffineTransform(transform);
        return this;
    }

    @Override
    public void copyStateFrom(MathObject obj) {
        if (obj instanceof Constructible) {
            Constructible cnst = (Constructible) obj;
            getMathObject().copyStateFrom(cnst.getMathObject());
        } else {
            getMathObject().copyStateFrom(obj);
        }
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r) {
        getMathObject().draw(scene, r);
    }

}
