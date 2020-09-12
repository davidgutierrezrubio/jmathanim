/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Renderers.Java2DRenderer;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import java.awt.Color;
import java.util.ArrayList;

/**
 * This class stores multiple JMPathObjects, and properly apply transforms and
 * animations to them
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
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
    public void moveTo(Vec coords) {
        for (Shape jmp : shapes) {
            jmp.moveTo(coords);
        }
    }

    @Override
    public <T extends MathObject> T fillColor(Color fc) {
        for (Shape jmp : shapes) {
            jmp.fillColor(fc);
        }
        return super.fillColor(fc);
    }

    @Override
    public <T extends MathObject> T drawColor(Color dc) {
        for (Shape jmp : shapes) {
            jmp.drawColor(dc);
        }
        return super.drawColor(dc);
    }

    @Override
    public void shift(Vec shiftVector) {
        for (Shape jmp : shapes) {
            jmp.shift(shiftVector);
        }
    }

    @Override
    public void scale(Point scaleCenter, double sx, double sy, double sz) {
        for (Shape jmp : shapes) {
            jmp.scale(scaleCenter, sx, sy, sz);
        }
    }

    @Override
    public MathObject copy() {
        MultiShapeObject resul=new MultiShapeObject();
        for (Shape sh:shapes)
        {
            resul.addShape(sh.copy());
        }
        resul.mp.copyFrom(mp);
        return resul;
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
    public void setDrawParam(double t, int sliceType) {
        for (Shape jmp : shapes) {
            jmp.setDrawParam(t, sliceType);//TODO:Change this
        }
    }

    @Override
    public void draw(Renderer r) {
        int n = 0;
        for (Shape jmp : shapes) {
//            Rect re=jmp.getBoundingBox();
//            double[] xx={re.xmin, re.ymax};
//            int[] mx = r.getCamera().mathToScreen(xx[0], xx[1]);
//            ((Java2DRenderer)r).debugText(Integer.toString(n), mx[0], mx[1]);
//            n++;
            jmp.draw(r);

        }
    }

    @Override
    public Rect getBoundingBox() {
        Rect resul = shapes.get(0).getBoundingBox();
        for (Shape jmp : shapes) {
            resul = resul.union(jmp.getBoundingBox());
        }
        return resul;
    }

    public void setDrawAlpha(double t) {
        for (Shape jmp : shapes) {
            jmp.setDrawAlpha(t);
        }
    }

    public void setFillAlpha(double t) {
        for (Shape jmp : shapes) {
            jmp.setFillAlpha(t);
        }
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
    public void unregisterChildrenToBeUpdated(JMathAnimScene scene) {
        for (Shape o : shapes) {
            o.unregisterChildrenToBeUpdated(scene);
        }
    }
}
