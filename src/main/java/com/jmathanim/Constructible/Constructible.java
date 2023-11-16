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

import com.jmathanim.Cameras.Camera;
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
 * depend. It acts as a container of a MathObject that will be updated and
 * drawed every frame.
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class Constructible extends MathObject {

    private boolean isMathObjectFree;
    private String label;

    protected Constructible() {
        isMathObjectFree = false;
    }

    /**
     * Returns the free object flag value. If this flag is set to true,
     * MathObject is not updated and can be freely transformed.
     *
     * @return True if drawed MathObject is not updated. False otherwise.
     */
    public boolean isThisMathObjectFree() {
        return isMathObjectFree;
    }

    /**
     * Sets the behaviour of the graphical representation of this Constructible
     * object. If true, drawed MathObject will not be updated and can be freely
     * animated. Altering the drawed MathObject does not affect the
     * Constructible parameters.
     *
     * @param <T> Class object
     * @param isMathObjectFree Boolean flag. True if drawed MathObject is no
     * longer to be updated with constructible parameters.
     * @return This object
     */
    public <T extends Constructible> T freeMathObject(boolean isMathObjectFree) {
        if (!isMathObjectFree) {
            if (this.isMathObjectFree) {
                rebuildShape();
            }
        }
        this.isMathObjectFree = isMathObjectFree;
        return (T) this;
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
    public Rect computeBoundingBox() {
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
        super.copyStateFrom(obj);
        if (obj == null) {
            return;
        }
        if (obj instanceof Constructible) {
            Constructible cnst = (Constructible) obj;
            getMathObject().copyStateFrom(cnst.getMathObject());
            this.freeMathObject(cnst.isThisMathObjectFree());
        } else {
            getMathObject().copyStateFrom(obj);
        }
        getMp().copyFrom(obj.getMp());
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r, Camera cam) {
        getMathObject().draw(scene, r, cam);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
