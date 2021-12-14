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

import com.jmathanim.jmathanim.JMathAnimScene;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

/**
 * Stores 2 or more animations and play them in sequential order. The total
 * runtime of this animation is the sum of runtimes of all animations played
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Concatenate extends Animation {

	private final ArrayList<Animation> animations;
	private int currentAnim;

        /**
         * Creates a new Concatenate animation with the given animations
         * @param anims Animations to concatenate (varargs)
         * @return The created anuimation
         */
	public static Concatenate make(Animation... anims) {
		return new Concatenate(anims);
	}

	/**
	 * Creates a new instance, with no animations added
	 */
	public Concatenate() {
		this(new ArrayList<Animation>());
	}

	/**
	 * Creates a new instance of this animation, with the specified list of
	 * animations
	 *
	 * @param anims Animations to add (varargs)
	 */
	public Concatenate(Animation... anims) {
		this(Arrays.asList(anims));
	}

	/**
	 * Creates a new instance, with a the especified animations added to the list
	 *
	 * @param anims List object of animations to add
	 */
	public Concatenate(List<Animation> anims) {
		super(0);
		this.animations = new ArrayList<>();
		this.animations.addAll(anims);
		currentAnim = 0;

	}

	/**
	 * Add the given animation to the list of animations to concatenate
	 *
	 * @param e Animation to add
	 * @return true if the collection changed as a result of this method
	 */
	public final boolean add(Animation e) {
		return animations.add(e);
	}

	@Override
	public void initialize(JMathAnimScene scene) {
		super.initialize(scene);
		// Initialize the first...
		animations.get(0).initialize(scene);
	}

	@Override
	public boolean processAnimation() {
		if (currentAnim == this.animations.size()) {// If I already finished...
			return true;
		}
		boolean resul = animations.get(currentAnim).processAnimation();
		if (resul) {
			animations.get(currentAnim).finishAnimation();
			currentAnim++;
			// It is important to call the initialize of the next animation immediately,
			// before any draws are done to the screen
			if (currentAnim < this.animations.size()) {
				animations.get(currentAnim).initialize(scene);
				resul = animations.get(currentAnim).processAnimation();
			}
		}
		if (currentAnim == this.animations.size() - 1) {// If I am processing the last animation...
			return resul;
		} else {
			return false;
		}
	}

	@Override
	public void finishAnimation() {
		super.finishAnimation();
		// ...and finish the last one
//        animations.get(animations.size() - 1).finishAnimation();
		for (Animation an : animations) {
			if (an.getStatus() == Status.NOT_INITIALIZED) {
				an.initialize(scene);
			}
			if (an.getStatus() != Status.FINISHED) {
				an.finishAnimation();
			}
		}
	}

	@Override
	public void doAnim(double t) {
	}

	/**
	 * Sets the lambda function. Adjusting this will change the lambda for all
	 * animations stored in this class
	 *
	 * @param lambda
	 */
	@Override
	public <T extends Animation> T setLambda(DoubleUnaryOperator lambda) {
		super.setLambda(lambda);
		for (Animation anim : animations) {
			anim.setLambda(lambda);
		}
		return (T) this;
	}
}
