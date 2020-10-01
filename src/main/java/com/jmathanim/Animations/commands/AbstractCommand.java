/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations.commands;

import com.jmathanim.jmathanim.JMathAnimScene;

/**
 * A command to execute to perform some action in a single Object
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class AbstractCommand {

    public abstract void initialize();

    public abstract void execute(double t);

    public abstract void finish();

    public abstract void addObjectsToScene(JMathAnimScene scene);
}
