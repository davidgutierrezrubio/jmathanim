/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Renderers.Java2DRenderer;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.JMColor;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import java.awt.Color;
import java.util.ArrayList;

/**
 * This class stores multiple JMPathObjects, and properly apply transforms and
 * animations to them
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class MultiShapeObject extends MathObject {

    public final ArrayList<Shape> shapes;

    public MultiShapeObject() {
        this(new ArrayList<Shape>());
    }

    public MultiShapeObject(ArrayList<Shape> jmps) {
        super();
        this.shapes = jmps;
    }

    public boolean addShape(Shape e) {
        return shapes.add(e);
    }

    public boolean addJMPathObject(JMPath p) {
        return shapes.add(new Shape(p, null));
    }

    @Override
    public Point getCenter() {
        return getBoundingBox().getCenter();
    }

    @Override
    public <T extends MathObject> T moveTo(Vec coords) {
        for (Shape jmp : shapes) {
            jmp.moveTo(coords);
        }
        return (T) this;
    }

    @Override
    public <T extends MathObject> T fillColor(JMColor fc) {
        for (Shape jmp : shapes) {
            jmp.fillColor(fc);
        }
        return super.fillColor(fc);
    }

    @Override
    public <T extends MathObject> T drawColor(JMColor dc) {
        for (Shape jmp : shapes) {
            jmp.drawColor(dc);
        }
        return super.drawColor(dc);
    }

    @Override
    public <T extends MathObject> T shift(Vec shiftVector) {
        for (Shape jmp : shapes) {
            jmp.shift(shiftVector);
        }
        return (T) this;
    }

    @Override
    public <T extends MathObject> T copy() {
        MultiShapeObject resul = new MultiShapeObject();
        for (Shape sh : shapes) {
            final Shape copy = sh.copy();
            resul.addShape(copy);
        }
        resul.mp.copyFrom(mp);
        return (T) resul;
    }

    @Override
    public void prepareForNonLinearAnimation() {
        for (Shape jmp : shapes) {
            jmp.prepareForNonLinearAnimation();
        }
    }

    @Override
    public void processAfterNonLinearAnimation() {
        for (Shape jmp : shapes) {
            jmp.processAfterNonLinearAnimation();
        }
    }

    @Override
    public void draw(Renderer r) {

        int n = 0;
        for (Shape jmp : shapes) {
            if (jmp.visible) {
                if (absoluteSize) {
                    r.drawAbsoluteCopy(jmp, getAbsoluteAnchorPoint().v);
                } else {
                    r.drawPath(jmp);
                }
            }
        }
    }

    @Override
    public Rect getBoundingBox() {
        if (shapes.size() > 0) {
            Rect resul = shapes.get(0).getBoundingBox();
            for (Shape jmp : shapes) {
                resul = resul.union(jmp.getBoundingBox());
            }
            return resul;
        } else {
            return null;
        }
    }

    @Override
    public <T extends MathObject> T drawAlpha(double t) {
        for (Shape jmp : shapes) {
            jmp.drawAlpha(t);
        }
        return (T) this;
    }

    @Override
    public <T extends MathObject> T fillAlpha(double t) {
        for (Shape jmp : shapes) {
            jmp.fillAlpha(t);
        }
        return (T) this;
    }

    @Override
    public <T extends MathObject> T multDrawAlpha(double t) {
        for (Shape jmp : shapes) {
            jmp.multDrawAlpha(t);
        }
        return (T) this;
    }

    @Override
    public <T extends MathObject> T multFillAlpha(double t) {
        for (Shape jmp : shapes) {
            jmp.multFillAlpha(t);
        }
        return (T) this;
    }

    public Shape get(int n) {
        return shapes.get(n);
    }

    @Override
    public void registerChildrenToBeUpdated(JMathAnimScene scene) {
        for (Shape o : shapes) {
            o.registerChildrenToBeUpdated(scene);
        }
    }

    @Override
    public void update() {
        //Nothing to do here
    }

    @Override
    public void restoreState() {
        super.restoreState();
        for (Shape o : shapes) {
            o.restoreState();
        }
    }

    @Override
    public void saveState() {
        super.saveState();
        for (Shape o : shapes) {
            o.saveState();
        }
    }

    @Override
    public void unregisterChildrenToBeUpdated(JMathAnimScene scene) {
        for (Shape o : shapes) {
            o.unregisterChildrenToBeUpdated(scene);
        }
    }
}
