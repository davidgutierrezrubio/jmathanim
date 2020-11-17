/*
 * Copyright (C) 2020 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.jmathanim.Animations;

import com.jmathanim.Animations.commands.AbstractCommand;
import com.jmathanim.jmathanim.JMathAnimScene;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class ApplyCommand extends Animation {
    
    private final AbstractCommand command;
    
    public ApplyCommand(AbstractCommand command, double runTime) {
        super(runTime);
        this.command = command;
    }
    
    public ApplyCommand(AbstractCommand abstractCommand) {
        this.command=abstractCommand;
    }
    
    @Override
    public void initialize() {
        command.initialize();
    }
    
    public void doAnim(double t) {
        command.execute(lambda.applyAsDouble(t));
    }
    
    @Override
    public void finishAnimation() {
        command.execute(lambda.applyAsDouble(1));
        command.finish();
    }
    
    @Override
    public void addObjectsToScene(JMathAnimScene scene) {
        command.addObjectsToScene(scene);
    }
    
}
