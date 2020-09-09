/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations.commands;

import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.MathObject;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class Commands {

    public static Command shift() {
        return new Command() {
            @Override
            public void execute(CommandArguments args) {
                Vec vShift=args.vec1.mult(args.lambda);
//                args.mobj1.shift(args.vShift);
            }

            @Override
            public void reset(CommandArguments args) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };

    }
}
