/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.Vec;
import java.util.ArrayList;

/**
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

    @Override
    public Point getCenter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        for (JMPathMathObject jmp : jmps) {
            jmp.draw(r);
        }
    }

}
