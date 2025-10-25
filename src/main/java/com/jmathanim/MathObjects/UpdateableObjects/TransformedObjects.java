/*
 * Copyright (C) 2023 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jmathanim.MathObjects.UpdateableObjects;

import com.jmathanim.MathObjects.MathObject;
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.jmathanim.JMathAnimScene;

import java.util.HashMap;

/**
 * Updates an object to be always the transformed of another object
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class TransformedObjects implements Updateable {

    private final HashMap<MathObject, MathObject> dictObjects;
    private JMathAnimScene scene;
    private AffineJTransform transform;
    private final HashMap<MathObject, Boolean> dictWasInTheScene;

    public static TransformedObjects make(MathObject<?>... base) {
        TransformedObjects resul = new TransformedObjects();
        for (MathObject obj : base) {
            resul.add(obj);
        }
        return resul;
    }

    public TransformedObjects() {
        dictObjects = new HashMap<>();
        dictWasInTheScene = new HashMap<>();
        transform = new AffineJTransform();//Identity
    }

    public AffineJTransform getTransform() {
        return transform;
    }

    public void setTransform(AffineJTransform transform) {
        this.transform = transform;
    }

    /**
     * Add a new object and automatically creates a copy of this object to be
     * the transformed
     *
     * @param base Base object
     * @return This object
     */
    public TransformedObjects add(MathObject base) {

        return add(base, base.copy());
    }

    /**
     * Removes this object from the updateable. Transformed object is not
     * automatically removed from the scene
     *
     * @param base Base object to remove
     * @return This object
     */
    public TransformedObjects remove(MathObject base) {
        if (!dictObjects.containsKey(base)) {
            return this;
        }

        MathObject transformed = dictObjects.get(base);
        dictObjects.remove(base);

        if (scene != null) {

            if (!dictWasInTheScene.get(transformed)) {
                scene.remove(transformed);
            }
        }
        return this;
    }

    /**
     * Returns the transformed object associated with the given one
     *
     * @param base Base object
     * @return The associated transformed object
     */
    public MathObject getTransformed(MathObject base) {
        return dictObjects.get(base);
    }

    /**
     * Add a new object and sets the transformed object. Both objects should be
     * of the same class
     *
     * @param base Base object
     * @param transformed Transformed object
     * @return This object
     */
    public TransformedObjects add(MathObject base, MathObject transformed) {
        if (!base.getClass().equals(transformed.getClass())) {
            JMathAnimScene.logger.warn("Base and transformed are not the same class " + base.getClass().getName() + " vs. " + transformed.getClass().getName());
            return this;
        }
        dictObjects.put(base, transformed);

        return this;
    }

    @Override
    public int getUpdateLevel() {//Do this at the end of the times
        return Integer.MAX_VALUE;
    }

    @Override
    public void setUpdateLevel(int level) {
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        this.scene = scene;
        for (MathObject base : dictObjects.keySet()) {
            MathObject transformed = dictObjects.get(base);
            dictWasInTheScene.put(base, base.getProperty("scene") != null);
            dictWasInTheScene.put(transformed, transformed.getProperty("scene") != null);
        }
    }

    @Override
    public void unregisterUpdateableHook(JMathAnimScene scene) {
        dictWasInTheScene.clear();
    }

    @Override
    public void update(JMathAnimScene scene) {
        for (MathObject base : dictObjects.keySet()) {
            MathObject basec = base.copy();
            MathObject transformed = dictObjects.get(base);

            if (scene.getMathObjects().contains(base)) {
                scene.add(transformed);
            } else {
                scene.remove(transformed);
            }
            basec.applyAffineTransform(transform);

            MODrawProperties aa = transformed.getMp().copy();
            transformed.copyStateFrom(basec);
            transformed.getMp().copyFrom(aa);

        }

    }
}
