/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects.MOProperties;

import com.jmathanim.Animations.AffineJTransform;
import com.jmathanim.mathobjects.Stateable;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public abstract class MathObjectAttributes implements Stateable{
    
   public abstract void applyTransform(AffineJTransform tr);

}
