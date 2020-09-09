/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations;

import com.jmathanim.Animations.commands.Command;
import com.jmathanim.mathobjects.MathObject;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class ApplyCommand extends Animation{

    private final Command command;


    public ApplyCommand(MathObject mobj, Command command,double runTime) {
        super(mobj, runTime);
        this.command=command;
    }


    
    @Override
    public void initialize() {
    }

    @Override
    public void doAnim(double t) {
    }

    @Override
    public void finishAnimation() {
    }
    
}
