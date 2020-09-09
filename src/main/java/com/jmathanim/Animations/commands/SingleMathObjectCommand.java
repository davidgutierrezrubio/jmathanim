/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations.commands;

import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public abstract class SingleMathObjectCommand extends AbstractCommand {

    protected MathObject mathObject;

    public SingleMathObjectCommand(MathObject mathObject) {
        this.mathObject = mathObject;
    }

    
    @Override
    public void addObjectsToScene(JMathAnimScene scene) {
        scene.add(mathObject);
    }

}
