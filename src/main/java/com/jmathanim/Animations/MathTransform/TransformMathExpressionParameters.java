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
		removingStyle = null;
		addingStyle = TransformMathExpression.AddType.FADE_IN;
		transformStyle = TransformMathExpression.TransformType.INTERPOLATION;
	}

	/**
	 * Return the number of turns associated to the rotation effect
	 * 
	 * @return The number of turns
	 */
	protected int getNumTurns() {
		return numTurns;
	}

	/**
	 * Adds a rotation effect, rotating the transformed shape the specified number
	 * of turns.
	 *
	 * @param numTurns Number of turns. Positive is counterclockwise, negative
	 *                 clockwise.
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
	 * Adds a jump effect to the transformed shape. The trajectory is a semicirlce.
	 * The jump vector is the rotated vector 90 degrees clockwise from the vector
	 * from the original and destiny shape.
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
	 * Adds a jump effect to the transformed shape, with the trajectory specified
	 * and with a total height of the given parameter.The jump vector is the rotated
	 * vector 90 degrees clockwise from the vector from the original and destiny
	 * shape.
	 *
	 * @param heightJump Height jump
	 * @param jumpType   Jump type, a value of the enum AnimationEffect.JumpType
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

	/**
	 * Sets the type of transformation to apply to a part of a math expression.
	 *
	 * @param transformStyle A value of TransformMathExpression.TransformType enum
	 * @return This object
	 */
	public TransformMathExpressionParameters setTransformStyle(TransformMathExpression.TransformType transformStyle) {
		this.transformStyle = transformStyle;
		return this;
	}

	protected AnimationEffect.JumpType getJumptype() {
		return jumptype;
	}

}
