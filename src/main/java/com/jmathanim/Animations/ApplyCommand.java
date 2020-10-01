/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations;

import com.jmathanim.Animations.commands.AbstractCommand;
import com.jmathanim.jmathanim.JMathAnimScene;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class ApplyCommand extends Animation{

    private final AbstractCommand command;


    public ApplyCommand(AbstractCommand command,double runTime) {
        super(runTime);
        this.command=command;
    }


    
    @Override
    public void initialize() {
        command.initialize();
    }

    @Override
    public void doAnim(double t) {
        command.execute(t);
    }

    @Override
    public void finishAnimation() {
        command.finish();
    }

     @Override
    public void addObjectsToScene(JMathAnimScene scene) {
        command.addObjectsToScene(scene);
    }
    
}
