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
package com.jmathanim.mathobjects.updateableObjects;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Styling.DrawStyleProperties;
import com.jmathanim.Utils.Rect;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Shape;

/**
 * Shape representing the trail drawn by a moving a point
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Trail extends MathObject<Trail> {

    MathObject<?> marker;
    private boolean cutNext = true;
    private boolean draw = true;
    private final Shape shapeTrail;


    /**
     * Builds new Trail object. A trail is a updateable Shape that adds a copy
     * of a marker point every frame.
     *
     * @param marker Point to be followed
     * @return The new Trail object
     */
    public static Trail make(MathObject<?> marker) {
        return new Trail(marker);
    }

    /**
     * Returns a new Trail object. A trail is a updateable Shape that adds a
     * copy of a marker point every frame.
     *
     * @param marker Point to be followed
     */
    public Trail(MathObject<?> marker) {
        this.marker = marker;
        shapeTrail = new Shape();
        shapeTrail.getPath().addPoint(marker.getCenter());
        shapeTrail.get(0).setThisSegmentVisible(false);
    }


    @Override
    public DrawStyleProperties getMp() {
        return shapeTrail.getMp();
    }

    @Override
    public Trail copy() {
        return new Trail(marker.copy());
    }

    @Override
    protected Rect computeBoundingBox() {
        return null;
    }

    @Override
    public void update(JMathAnimScene scene) {
        super.update(scene);
        if (draw) {
            JMPathPoint pa = JMPathPoint.lineTo(marker.getCenter());
            pa.setThisSegmentVisible(!cutNext);
            cutNext = false;
            shapeTrail.getPath().addJMPoint(pa);
        }
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        dependsOn(scene, marker);
    }

    public Shape getShapeTrail() {
        return shapeTrail;
    }

    /**
     * Disables adding new elements to the trail, until a call to
     *  lowerPen is made.
     */
    public void raisePen() {
        draw = false;
    }

    /**
     * Enables adding new elements to the trail. By default this is set.
     */
    public void lowerPen() {
        draw = true;
        cutNext = true;
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r, Camera camera) {
        shapeTrail.draw(scene,r,camera);
    }
}
