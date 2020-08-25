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
import java.util.ArrayList;

/**
 * This class stores multiple JMPathObjects, and properly apply transforms and
 * animations to them
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class MultiJMPathObject extends MathObject {

    public final ArrayList<JMPathMathObject> jmps;

    public MultiJMPathObject() {
        this(new ArrayList<JMPathMathObject>());
    }

    public MultiJMPathObject(ArrayList<JMPathMathObject> jmps) {
        super();
        this.jmps = jmps;
    }

    public boolean addJMPathObject(JMPathMathObject e) {
        return jmps.add(e);
    }

    public boolean addJMPathObject(JMPath p) {
        return jmps.add(new JMPathMathObject(p, null));
    }

    @Override
    public Point getCenter() {
        return getBoundingBox().getCenter();
    }

    @Override
    public void moveTo(Vec coords) {
        for (JMPathMathObject jmp : jmps) {
            jmp.moveTo(coords);
        }
    }

    @Override
    public void shift(Vec shiftVector) {
        for (JMPathMathObject jmp : jmps) {
            jmp.shift(shiftVector);
        }
    }

    @Override
    public void scale(Point scaleCenter, double sx, double sy, double sz) {
        for (JMPathMathObject jmp : jmps) {
            jmp.scale(scaleCenter, sx, sy, sz);
        }
    }

    @Override
    public MathObject copy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update() {
        for (JMPathMathObject jmp : jmps) {
            jmp.update();
        }
    }

    @Override
    public void prepareForNonLinearAnimation() {
        for (JMPathMathObject jmp : jmps) {
            jmp.prepareForNonLinearAnimation();
        }
    }

    @Override
    public void processAfterNonLinearAnimation() {
        for (JMPathMathObject jmp : jmps) {
            jmp.processAfterNonLinearAnimation();
        }
    }

    @Override
    public void setDrawParam(double t, int sliceType) {
        for (JMPathMathObject jmp : jmps) {
            jmp.setDrawParam(t, sliceType);//TODO:Change this
        }
    }

    @Override
    public void draw(Renderer r) {
        int n=0;
        for (JMPathMathObject jmp : jmps) {
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
        for (JMPathMathObject jmp : jmps) {
            resul = resul.union(jmp.getBoundingBox());
        }
        return resul;
    }

}
