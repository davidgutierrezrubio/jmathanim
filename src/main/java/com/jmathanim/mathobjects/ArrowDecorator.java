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
import com.jmathanim.Utils.JMColor;
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
        final ArrowTip arrowTip = new ArrowTip(type);
        arrowTip.getArrowtip().fillColor(shape.mp.getDrawColor());
        arrowTip.getArrowtip().drawColor(shape.mp.getDrawColor());
        arrows.put(location, arrowTip);
    }

    public Shape getShape() {
        return shape;
    }

    @Override
    public void draw(Renderer r) {
        shape.draw(r);
        for (double t : arrows.keySet()) {
            arrows.get(t).draw(r);
        }
    }

    @Override
    public void update(JMathAnimScene scene) {
        for (double t : arrows.keySet()) {
            JMPathPoint loc = shape.getPath().getPointAt(t);
            arrows.get(t).updateLocations(loc.p, loc.p.to(loc.cpExit));
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
        return super.drawColor(dc); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T extends MathObject> T drawAlpha(double alpha) {
        return super.drawAlpha(alpha); //To change body of generated methods, choose Tools | Templates.
    }

}
