/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations.Strategies.ShowCreation;

import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class LineCreationStrategy extends SimpleShapeCreationStrategy {

    public LineCreationStrategy(Line mobj, JMathAnimScene scene) {
        super(Shape.segment(mobj).setMp(mobj.getMp()), scene);
    }

}
