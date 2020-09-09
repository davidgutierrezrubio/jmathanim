/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations.commands;

import com.jmathanim.mathobjects.MathObject;

/**
 * A command to execute to perform some action in a single Object
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public interface Command {
    public void execute(CommandArguments args);
    public void reset(CommandArguments args);
}
