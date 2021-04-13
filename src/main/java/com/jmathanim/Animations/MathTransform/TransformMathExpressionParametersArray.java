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
package com.jmathanim.Animations.MathTransform;

import com.jmathanim.Animations.AnimationEffect;
import java.util.ArrayList;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class TransformMathExpressionParametersArray {

    private final ArrayList<TransformMathExpressionParameters> pars;

    public TransformMathExpressionParametersArray() {
        this.pars = new ArrayList<>();
    }

    public boolean add(TransformMathExpressionParameters e) {
        return pars.add(e);
    }

    /**
     * Adds a rotation effect, rotating the transformed shape the specified
     * number of turns.
     *
     * @param numTurns Number of turns. Positive is counterclockwise, negative
     * clockwise.
     * @return This object
     */
    public TransformMathExpressionParametersArray addRotateEffect(int numTurns) {
        for (TransformMathExpressionParameters par : pars) {
            par.addRotateEffect(numTurns);
        }
        return this;
    }

    /**
     * Adds a jump effect to the transformed shape. The trajectory is
     * sinusoidal, with a total height of the given parameter. The jump vector
     * is the rotated vector 90 degrees clockwise from the vector from the
     * original and destiny shape.
     *
     * @param heightJump Height jump
     * @return This object
     */
    public TransformMathExpressionParametersArray addJumpEffect(double heightJump) {
        for (TransformMathExpressionParameters par : pars) {
            par.addJumpEffect(heightJump);
        }
        return this;
    }

     public TransformMathExpressionParametersArray addJumpEffect(double heightJump, AnimationEffect.JumpType jumpType) {
       for (TransformMathExpressionParameters par : pars) {
            par.addJumpEffect(heightJump,jumpType);
        }
        return this;
    }
    
    
    
    /**
     * Adds an alpha effect when transforming, multiplying the transformed shape
     * alpha up to a given value, back and forth.
     *
     * @param alpha Alpha value to multiply.
     * @return This object
     */
    public TransformMathExpressionParametersArray addAlphaEffect(double alpha) {
        for (TransformMathExpressionParameters par : pars) {
            par.addAlphaEffect(alpha);
        }
        return this;
    }

    /**
     * Sets a scale effect when transforming, scaling the transformed shape by a
     * given factor, back and forth.
     *
     * @param scale Scale to reach.
     * @return This object
     */
    public TransformMathExpressionParametersArray addScaleEffect(double scale) {
        for (TransformMathExpressionParameters par : pars) {
            par.addScaleEffect(scale);
        }
        return this;
    }

}
