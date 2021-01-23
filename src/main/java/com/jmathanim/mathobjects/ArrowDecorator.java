/*
 * Copyright (C) 2021 David Gutierrez Rubio davidgutierrezrubio@gmail.com
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

import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.JMColor;
import com.jmathanim.Utils.MODrawProperties;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.updateableObjects.Updateable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class ArrowDecorator extends MathObject {

    private final Shape shape;
    private HashMap<Double, ArrowTip> arrows;

    public static ArrowDecorator make(Shape shape) {
        return new ArrowDecorator(shape);
    }

    public ArrowDecorator(Shape shape) {
        this.shape = shape;
        arrows = new HashMap<>();
    }

    public void addArrow(Arrow2D.ArrowType type, double location) {
        addArrow(type, location, ArrowTip.slopeDirection.POSITIVE);
    }

    public void addArrow(Arrow2D.ArrowType type, double location, ArrowTip.slopeDirection dir) {
        final ArrowTip arrowTip = new ArrowTip(type, dir);
        arrowTip.getArrowtip().fillColor(shape.mp.getDrawColor());
        arrowTip.getArrowtip().drawColor(shape.mp.getDrawColor());
        arrows.put(location, arrowTip);
    }

    public Shape getShape() {
        return shape;
    }

    @Override
    public void draw(Renderer r) {
        if (!scene.getObjects().contains(shape)) {
            return;
        }
        shape.draw(r);
        for (double t : arrows.keySet()) {
            arrows.get(t).draw(r);
        }
    }

    @Override
    public void update(JMathAnimScene scene) {
        if (!scene.getObjects().contains(shape)) {
            return;
        }
        for (double t : arrows.keySet()) {
            JMPathPoint loc = shape.getPath().getPointAt(t);
            arrows.get(t).updateLocations(loc);
        }
    }

    @Override
    public int getUpdateLevel() {
        return shape.getUpdateLevel() + 1;
    }

    @Override
    public <T extends MathObject> T copy() {
        return null;//Not supported
    }

    @Override
    public Rect getBoundingBox() {
        update(scene);
        Rect resul = shape.getBoundingBox();
        for (double t : arrows.keySet()) {
            resul = Rect.union(resul, arrows.get(t).getArrowtipDrawableCopy().getBoundingBox());
        }
        return resul;
    }

    @Override
    public <T extends MathObject> T fillAlpha(double alpha) {
        super.fillAlpha(alpha);
        for (double t : arrows.keySet()) {
            arrows.get(t).getArrowtip().fillAlpha(alpha);
        }
        return (T) this;
    }

    @Override
    public <T extends MathObject> T fillColor(JMColor fc) {
        super.fillColor(fc);
        for (double t : arrows.keySet()) {
            arrows.get(t).getArrowtip().fillColor(fc);
        }
        return (T) this;
    }

    @Override
    public <T extends MathObject> T drawColor(JMColor dc) {
        super.drawColor(dc);
        for (double t : arrows.keySet()) {
            arrows.get(t).getArrowtip().drawColor(dc);
        }
        return (T) this;
    }

    @Override
    public <T extends MathObject> T drawAlpha(double alpha) {
        super.drawAlpha(alpha);
        for (double t : arrows.keySet()) {
            arrows.get(t).getArrowtip().drawAlpha(alpha);
        }
        return (T) this;
    }

    @Override
    public void interpolateMPFrom(MODrawProperties mpDst, double alpha) {
        super.interpolateMPFrom(mpDst, alpha);
        for (double t : arrows.keySet()) {
            arrows.get(t).getArrowtip().interpolateMPFrom(mpDst, alpha);
        }
    }

    @Override
    public <T extends MathObject> T applyLinearTransform(AffineJTransform tr) {
        //Nothing to do
        return (T) this;
    }

}
