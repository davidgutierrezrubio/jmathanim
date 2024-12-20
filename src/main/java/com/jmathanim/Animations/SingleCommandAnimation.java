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

import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Scalar;
import com.jmathanim.mathobjects.Text.LaTeXMathObject;

/**
 * This animation execute a single command. It is used to encapsulate ceratin
 * commands into an Animation container like AnimationGroup or Concatenate
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class SingleCommandAnimation extends Animation {

    enum cmdStatusType {
        NEVER_DONE, DONE, UNDONE
    }

    public cmdStatusType cmdStatus;

    public static SingleCommandAnimation changeLaTeX(LaTeXMathObject latex, String newText) {
        SingleCommandAnimation sc = new SingleCommandAnimation() {
            String oldText = null;

            @Override
            public void command() {
                oldText = latex.getText();
                latex.setLaTeX(newText);
            }

            @Override
            public void undo() {
                if (oldText != null) {
                    latex.setLaTeX(oldText);
                }
            }
        };
        return sc;
    }

    public static SingleCommandAnimation changeScalar(Scalar scalar, double value) {
        SingleCommandAnimation sc = new SingleCommandAnimation() {
            Double oldValue = null;

            @Override
            public void command() {
                oldValue = scalar.getScalar();
                scalar.setScalar(value);
            }

            @Override
            public void undo() {
                if (oldValue != null) {
                    scalar.setScalar(oldValue);
                }
            }
        };
        return sc;
    }

    public static SingleCommandAnimation increaseScalar(Scalar scalar,  double delta) {
        SingleCommandAnimation sc = new SingleCommandAnimation() {
            Double oldValue = null;

            @Override
            public void command() {
                oldValue = scalar.getScalar();
                scalar.setScalar(oldValue+delta);
            }

            @Override
            public void undo() {
                if (oldValue != null) {
                    scalar.setScalar(oldValue);
                }
            }
        };
        return sc;
    }

    /**
     * Creates a new SingleCommandAnimation. The default duration of this
     * animation is 0.
     */
    public SingleCommandAnimation() {
        super(0);
        cmdStatus = cmdStatusType.NEVER_DONE;
    }

    @Override
    public boolean processAnimation() {
        super.processAnimation();
        this.t = 1;
        return true;// Finish the animation inmediately
    }

    /**
     * Command to execute. This should be implemented in the implementing class
     */
    public abstract void command();

    public abstract void undo();

    @Override
    public void doAnim(double t) {
        super.doAnim(t);
        if ((cmdStatus == cmdStatusType.NEVER_DONE) || (cmdStatus == cmdStatusType.UNDONE)) {
                command();
                cmdStatus = cmdStatusType.DONE;
            }
    }

    @Override
    public void cleanAnimationAt(double t) {
        if (t == 1) {
            if ((cmdStatus == cmdStatusType.NEVER_DONE) || (cmdStatus == cmdStatusType.UNDONE)) {
                command();
                cmdStatus = cmdStatusType.DONE;
            }
        }
        if (t == 0) {
            if (cmdStatus == cmdStatusType.DONE) {
                undo();
                cmdStatus = cmdStatusType.UNDONE;
            }
        }
    }

    @Override
    public void prepareForAnim(double t) {
    }

    @Override
    public MathObject getIntermediateObject() {
        return null;
    }

}
