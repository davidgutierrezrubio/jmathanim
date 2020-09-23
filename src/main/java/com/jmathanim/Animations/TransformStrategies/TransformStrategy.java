/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations.TransformStrategies;

import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public interface TransformStrategy {
    
    public void prepareObjects(Shape ob1,Shape ob2);
    public void applyTransform(double t);
    public void finish();
    
}
