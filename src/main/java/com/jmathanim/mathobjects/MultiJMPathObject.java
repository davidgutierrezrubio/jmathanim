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
import java.util.ArrayList;

/**
 * This class stores multiple JMPathObjects, and properly apply transforms and
 * animations to them
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class MultiJMPathObject extends MathObject {

    public final ArrayList<Shape> jmps;

    public MultiJMPathObject() {
        this(new ArrayList<Shape>());
    }

    public MultiJMPathObject(ArrayList<Shape> jmps) {
        super();
        this.jmps = jmps;
    }

    public boolean addJMPathObject(Shape e) {
        return jmps.add(e);
    }

    public boolean addJMPathObject(JMPath p) {
        return jmps.add(new Shape(p, null));
    }

    @Override
    public Point getCenter() {
        return getBoundingBox().getCenter();
    }

    @Override
    public void moveTo(Vec coords) {
        for (Shape jmp : jmps) {
            jmp.moveTo(coords);
        }
    }

    @Override
    public void shift(Vec shiftVector) {
        for (Shape jmp : jmps) {
            jmp.shift(shiftVector);
        }
    }

    @Override
    public void scale(Point scaleCenter, double sx, double sy, double sz) {
        for (Shape jmp : jmps) {
            jmp.scale(scaleCenter, sx, sy, sz);
        }
    }

    @Override
    public MathObject copy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

   
    @Override
    public void prepareForNonLinearAnimation() {
        for (Shape jmp : jmps) {
            jmp.prepareForNonLinearAnimation();
        }
    }

    @Override
    public void processAfterNonLinearAnimation() {
        for (Shape jmp : jmps) {
            jmp.processAfterNonLinearAnimation();
        }
    }

    @Override
    public void setDrawParam(double t, int sliceType) {
        for (Shape jmp : jmps) {
            jmp.setDrawParam(t, sliceType);//TODO:Change this
        }
    }

    @Override
    public void draw(Renderer r) {
        int n = 0;
        for (Shape jmp : jmps) {
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
        Rect resul = jmps.get(0).getBoundingBox();
        for (Shape jmp : jmps) {
            resul = resul.union(jmp.getBoundingBox());
        }
        return resul;
    }

    public void setDrawAlpha(double t) {
        for (Shape jmp : jmps) {
            jmp.setDrawAlpha(t);
        }
    }

    public void setFillAlpha(double t) {
        for (Shape jmp : jmps) {
            jmp.setFillAlpha(t);
        }
    }

    public Shape get(int n) {
        return jmps.get(n);
    }

    @Override
    public void registerChildrenToBeUpdated(JMathAnimScene scene) {
        for (Shape o : jmps) {
            o.registerChildrenToBeUpdated(scene);
        }
    }

    @Override
    public void update() {
       //Nothing to do here
    }

    @Override
    public void unregisterChildrenToBeUpdated(JMathAnimScene scene) {
        for (Shape o : jmps) {
            o.unregisterChildrenToBeUpdated(scene);
        }
    }
}
