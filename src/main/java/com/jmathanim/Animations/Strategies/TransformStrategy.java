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
package com.jmathanim.Animations.Strategies;

import com.jmathanim.Animations.Strategies.Transform.Optimizers.NullOptimizationStrategy;
import com.jmathanim.Animations.Strategies.Transform.Optimizers.OptimizePathsStrategy;
import com.jmathanim.jmathanim.JMathAnimScene;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class TransformStrategy {

	protected final JMathAnimScene scene;
	protected OptimizePathsStrategy optimizeStrategy;

	public TransformStrategy(JMathAnimScene scene) {
		this.scene = scene;
		optimizeStrategy = new NullOptimizationStrategy();
	}

	/**
	 * Sets the strategy to properly optimize paths in order to make transition
	 * smooth This strategy should be invoked in some moment at prepareObjects
	 * method.
	 *
	 * @param strategy Strategy to use
	 */
	public void setOptimizationStrategy(OptimizePathsStrategy strategy) {
		if (strategy != null) {
			optimizeStrategy = strategy;
		} else {
			optimizeStrategy = new NullOptimizationStrategy();
		}
	}

	/**
	 * Prepare necessary objects to perform transformation. This method is called
	 * immediately before playing the first frame.
	 */
	abstract public void prepareObjects();

	/**
	 * Apply current transform
	 *
	 * @param t  Time of the animation 0&lt;=t&lt;=1
	 * @param lt lambda(t), where lambda is a "smooth" function. Actual animation is
	 *           computed for this value.
	 */
	abstract public void applyTransform(double t, double lt);

	/**
	 * Performs clean up and finishing methods. This method is called right after
	 * the animation ends.
	 */
	abstract public void finish();

	/**
	 * Add necessary objects to scene
	 */
	abstract public void addObjectsToScene();

}
