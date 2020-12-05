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

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class TransformMathExpressionParameters {

    private double jumpHeight;
    private double alphaMult;
    private double scale;
    private int numTurns;
    private TransformMathExpression.RemoveType removingStyle;
    private TransformMathExpression.AddType addingStyle;
    private TransformMathExpression.TransformType transformStyle;

    public TransformMathExpressionParameters() {
        jumpHeight = 0d;
        alphaMult=1d;
        scale=1d;
        numTurns = 0;
        removingStyle=TransformMathExpression.RemoveType.FADE_OUT;
        addingStyle=TransformMathExpression.AddType.FADE_IN;
        transformStyle=TransformMathExpression.TransformType.INTERPOLATION;
    }

    public int getNumTurns() {
        return numTurns;
    }

    public TransformMathExpressionParameters setNumTurns(int numTurns) {
        this.numTurns = numTurns;
        return this;
    }

    public double getJumpHeight() {
        return jumpHeight;
    }

    public TransformMathExpressionParameters setJumpHeight(double radius) {
        this.jumpHeight = radius;
        return this;
    }

    public TransformMathExpression.RemoveType getRemovingStyle() {
        return removingStyle;
    }

    public void setRemovingStyle(TransformMathExpression.RemoveType removingStyle) {
        this.removingStyle = removingStyle;
    }

    public TransformMathExpression.AddType getAddingStyle() {
        return addingStyle;
    }

    public void setAddingStyle(TransformMathExpression.AddType addingStyle) {
        this.addingStyle = addingStyle;
    }

    public double getAlphaMult() {
        return alphaMult;
    }

    public TransformMathExpressionParameters setAlphaMult(double alphaMult) {
        this.alphaMult = alphaMult;
         return this;
    }

    public double getScale() {
        return scale;
    }

    public TransformMathExpressionParameters setScale(double scale) {
        this.scale = scale;
         return this;
    }

    public TransformMathExpression.TransformType getTransformStyle() {
        return transformStyle;
    }

    public void setTransformStyle(TransformMathExpression.TransformType transformStyle) {
        this.transformStyle = transformStyle;
    }

}
