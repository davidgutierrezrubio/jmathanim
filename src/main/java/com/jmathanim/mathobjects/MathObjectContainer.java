/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.MathObjectDrawingProperties;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class MathObjectContainer extends MathObject {

    protected MathObject mathObject;

    public MathObjectContainer(MathObject mathObject) {
        this.mathObject = mathObject;
    }

    public MathObject getMathObject() {
        return mathObject;
    }

    public void setMathObject(MathObject mathObject) {
        this.mathObject = mathObject;
    }

    public MathObjectContainer(MathObject mathObject, MathObjectDrawingProperties mp) {
        super(mp);
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
    public void prepareForNonLinearAnimation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void processAfterNonLinearAnimation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setDrawParam(double t,int sliceType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Rect getBoundingBox() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setDrawAlpha(double t) {
        this.mathObject.setDrawAlpha(t);
    }
@Override
    public void setFillAlpha(double t) {
        this.mathObject.setFillAlpha(t);
    }


    @Override
    public void registerChildrenToBeUpdated(JMathAnimScene scene) {
        this.mathObject.registerChildrenToBeUpdated(scene);
    }
 @Override
    public void unregisterChildrenToBeUpdated(JMathAnimScene scene) {
        this.mathObject.unregisterChildrenToBeUpdated(scene);
    }
    @Override
    public void update() {
        this.mathObject.update();
    }
}
