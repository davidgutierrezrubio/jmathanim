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

import com.jmathanim.Animations.Animation;
import com.jmathanim.Animations.AnimationEffect;
import com.jmathanim.Animations.Commands;
import com.jmathanim.Utils.Vec;
import static com.jmathanim.jmathanim.JMathAnimScene.PI;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class TransformMathExpressionParameters {

    private double jumpHeight;
    private double alphaMult;
    private AnimationEffect.JumpType jumptype;
    private double scale;
    private int numTurns;
    private TransformMathExpression.RemoveType removingStyle;
    private TransformMathExpression.AddType addingStyle;
    private TransformMathExpression.TransformType transformStyle;

    public TransformMathExpressionParameters() {
        jumpHeight = 0d;
        alphaMult = 1d;
        scale = 1d;
        numTurns = 0;
        removingStyle = TransformMathExpression.RemoveType.FADE_OUT;
        addingStyle = TransformMathExpression.AddType.FADE_IN;
        transformStyle = TransformMathExpression.TransformType.INTERPOLATION;
    }

    protected int getNumTurns() {
        return numTurns;
    }

    /**
     * Adds a rotation effect, rotating the transformed shape the specified
     * number of turns.
     *
     * @param numTurns Number of turns. Positive is counterclockwise, negative
     * clockwise.
     * @return This object
     */
    public TransformMathExpressionParameters addRotateEffect(int numTurns) {
        this.numTurns = numTurns;
        return this;
    }

    protected double getJumpHeightFromJumpEffect() {
        return jumpHeight;
    }

    /**
     * Adds a jump effect to the transformed shape. The trajectory is a
     * semicirlce. The jump vector is the rotated vector 90 degrees clockwise
     * from the vector from the original and destiny shape.
     *
     * @param heightJump Height jump
     * @return This object
     */
    public TransformMathExpressionParameters addJumpEffect(double heightJump) {
        this.jumpHeight = heightJump;
        this.jumptype = AnimationEffect.JumpType.SEMICIRCLE;
        return this;
    }

    /**
     * Adds a jump effect to the transformed shape, with the trajectory
     * specified and with a total height of the given parameter.The jump vector
     * is the rotated vector 90 degrees clockwise from the vector from the
     * original and destiny shape.
     *
     * @param heightJump Height jump
     * @param jumpType Jump type, a value of the enum AnimationEffect.JumpType
     * @return This object
     */
    public TransformMathExpressionParameters addJumpEffect(double heightJump, AnimationEffect.JumpType jumpType) {
        this.jumpHeight = heightJump;
        this.jumptype = jumpType;
        return this;
    }

    protected TransformMathExpression.RemoveType getRemovingStyle() {
        return removingStyle;
    }

    public void setRemovingStyle(TransformMathExpression.RemoveType removingStyle) {
        this.removingStyle = removingStyle;
    }

    protected TransformMathExpression.AddType getAddingStyle() {
        return addingStyle;
    }

    public void setAddingStyle(TransformMathExpression.AddType addingStyle) {
        this.addingStyle = addingStyle;
    }

    protected double getAlphaMultFromAlphaEffect() {
        return alphaMult;
    }

    /**
     * Adds an alpha effect when transforming, multiplying the transformed shape
     * alpha up to a given value, back and forth.
     *
     * @param alphaMult Alpha value to multiply.
     * @return This object
     */
    public TransformMathExpressionParameters addAlphaEffect(double alphaMult) {
        this.alphaMult = alphaMult;
        return this;
    }

    protected double getScaleFromScaleEffect() {
        return scale;
    }

    /**
     * Sets a scale effect when transforming, scaling the transformed shape by a
     * given factor, back and forth.
     *
     * @param scale Scale to reach.
     * @return This object
     */
    public TransformMathExpressionParameters addScaleEffect(double scale) {
        this.scale = scale;
        return this;
    }

    protected TransformMathExpression.TransformType getTransformStyle() {
        return transformStyle;
    }

    public TransformMathExpressionParameters setTransformStyle(TransformMathExpression.TransformType transformStyle) {
        this.transformStyle = transformStyle;
        return this;
    }

    public Animation createJumpAnimation(double runTime, Vec v, Shape sh) {
        Vec shiftVector = Vec.to(-v.y, v.x).normalize().mult(getJumpHeightFromJumpEffect());

        final Animation jumpShift = Commands.shift(runTime, shiftVector, sh);
        jumpShift.setLambda(t -> Math.sin(PI * t));
        jumpShift.setUseObjectState(false);
        return jumpShift;
    }

    public Animation createRotateAnimation(double runTime, Shape sh) {
        Animation rotation = Commands.rotate(runTime, 2 * PI * getNumTurns(), sh);
        rotation.setUseObjectState(false);
        return rotation;
    }

    public Animation createAlphaMultAnimation(double runTime, Shape sh) {
        //Parabola parameter so that mininum reaches at (.5,par.getAlphaMultFromAlphaEffect())
        double L = 4 * (1 - getAlphaMultFromAlphaEffect());
        Animation changeAlpha = new Animation(runTime) {
            @Override
            public void doAnim(double t) {
                double lt = 1 - t * (1 - t) * L;
                sh.fillAlpha(lt * sh.getMp().getFillColor().alpha);
                sh.drawAlpha(lt * sh.getMp().getDrawColor().alpha);
            }

            @Override
            public void finishAnimation() {
                super.finishAnimation();
                doAnim(1);
            }
        };
        return changeAlpha;
    }

    public Animation createScaleAnimation(double runTime, Shape sh) {
        //Parabola parameter so that mininum reaches at (.5,par.getAlphaMultFromAlphaEffect())
        double L = 4 * (1 - getScaleFromScaleEffect());
        return new Animation(runTime) {

            @Override
            public void doAnim(double t) {
                double lt = 1 - t * (1 - t) * L;
                sh.scale(lt);
            }

            @Override
            public void finishAnimation() {
                super.finishAnimation();
                doAnim(1);
            }
        };
    }

    public AnimationEffect.JumpType getJumptype() {
        return jumptype;
    }

}
