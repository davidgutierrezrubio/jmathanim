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
import com.jmathanim.Styling.DrawStyleProperties;
import com.jmathanim.Styling.RendererEffects;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Rect;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MathObjectGroup;
import com.jmathanim.mathobjects.DebugTools;
import com.jmathanim.mathobjects.Stateable;

/**
 * This class representas a constructible object, derived from another ones. For example a circle that pass for 3 points
 * is a constructible object. It cannot be transformed nor animated by itself, only changing the objects from which
 * depend. It acts as a container of a MathObject that will be updated and drawn every frame.
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class Constructible<T extends Constructible<T>> extends MathObject<T> {

    private boolean isMathObjectFree;
    private String label = "";

    protected Constructible() {
        super();
        isMathObjectFree = false;
    }

    /**
     * Returns the free object flag value. If this flag is set to true, MathObject is not updated and can be freely
     * transformed.
     *
     * @return True if drawn MathObject is not updated. False otherwise.
     */
    public boolean isFreeMathObject() {
        return isMathObjectFree;
    }

    /**
     * Sets the behaviour of the graphical representation of this Constructible object. If true, drawn MathObject will
     * not be updated and can be freely animated. Altering the drawn MathObject does not affect the Constructible
     * parameters.
     *
     * @param isMathObjectFree Boolean flag. True if drawn MathObject is no longer to be updated with constructible
     *                         parameters.
     * @return This object
     */
    public T setFreeMathObject(boolean isMathObjectFree) {
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
    public abstract MathObject<?> getMathObject();

    @Override
    public abstract T copy();

    @Override
    public DrawStyleProperties getMp() {

        return getMathObject().getMp();

    }

    @Override
    public void update(JMathAnimScene scene) {
//        super.update(scene);
        rebuildShape();
        setHasBeenUpdated(true);
    }

    abstract public void rebuildShape();

    @Override
    protected Rect computeBoundingBox() {
        rebuildShape();
        return getMathObject().getBoundingBox();
    }

    @Override
    public boolean isEmpty() {
        return getMathObject().isEmpty();
    }

    @Override
    public T applyAffineTransform(AffineJTransform transform) {
        getMathObject().applyAffineTransform(transform);
        return (T) this;
    }
    @Override
    public void copyStateFrom(Stateable obj) {
        if (!(obj instanceof Constructible)) return;
        Constructible<?> cnst = (Constructible<?>) obj;
        super.copyStateFrom(obj);
        MathObject<?> mathObject = cnst.getMathObject();

        getMathObject().copyStateFrom(mathObject);
        this.setFreeMathObject(cnst.isFreeMathObject());
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r, Camera cam) {
        MathObject<?> obj = getMathObject();

        if (obj != null) {
            //As MathObjectGroup does not have a draw method implemented by design, do a recursive search
            if (obj instanceof MathObjectGroup) {
                processDrawMathObjectGroup((MathObjectGroup) obj, scene, r, cam);
            } else {
                obj.draw(scene, r, cam);
            }
        }
    }

    private void processDrawMathObjectGroup(MathObjectGroup group, JMathAnimScene scene, Renderer r, Camera cam) {
        for (MathObject<?> obj : group) {
            if (obj != null) {
                if (obj instanceof MathObjectGroup) {
                    processDrawMathObjectGroup((MathObjectGroup) obj, scene, r, cam);
                } else {
                    obj.draw(scene, r, cam);
                }
            }
        }
    }


    public String getObjectLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public RendererEffects getRendererEffects() {
        return getMathObject().getRendererEffects();
    }

    @Override
    protected void addToSceneHook(JMathAnimScene scene) {
        super.addToSceneHook(scene);
        DebugTools.addToSceneHook(getMathObject(), scene);
    }

    @Override
    protected void removedFromSceneHook(JMathAnimScene scene) {
        super.removedFromSceneHook(scene);
        DebugTools.removedFromSceneHook(getMathObject(), scene);
    }

}
