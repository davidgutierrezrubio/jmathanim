/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations.commands;

import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;

/**
 * Represents an Animation command performed on a single object
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class MathObjectsCommand extends AbstractCommand {

    protected MathObject[] mathObjects;

    public MathObjectsCommand(MathObject...mathObjects) {
        this.mathObjects = mathObjects;
    }

    @Override
    public void addObjectsToScene(JMathAnimScene scene) {
        scene.add(mathObjects);
    }

}
