/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.Vec;
import java.util.Properties;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class MathObjectContainer extends MathObject {

    private MathObject mathObject;

    public MathObjectContainer(MathObject mathObject) {
        this.mathObject = mathObject;
    }

    public MathObject getMathObject() {
        return mathObject;
    }

    public void setMathObject(MathObject mathObject) {
        this.mathObject = mathObject;
    }

    public MathObjectContainer(MathObject mathObject, Properties configParam) {
        super(configParam);
        this.mathObject = mathObject;
    }

    @Override
    public Point getCenter() {
        return mathObject.getCenter();
    }

    @Override
    public void moveTo(Vec coords) {
        mathObject.moveTo(coords);
    }

    @Override
    public void shift(Vec shiftVector) {
        mathObject.shift(shiftVector);
    }

    @Override
    public void draw(Renderer r) {
        mathObject.draw(r);
    }

    @Override
    public MathObject copy() {
        return new MathObjectContainer(mathObject.copy());
        
    }

    @Override
    public void scale(Point scaleCenter, double sx, double sy, double sz) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void prepareForNonLinearAnimation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void processAfterNonLinearAnimation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
