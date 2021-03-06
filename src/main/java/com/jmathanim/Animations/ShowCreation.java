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

import com.jmathanim.Animations.Strategies.ShowCreation.ArrowCreationAnimation;
import com.jmathanim.Animations.Strategies.ShowCreation.AxesCreationAnimation;
import com.jmathanim.Animations.Strategies.ShowCreation.CreationStrategy;
import com.jmathanim.Animations.Strategies.ShowCreation.FirstDrawThenFillAnimation;
import com.jmathanim.Animations.Strategies.ShowCreation.GroupCreationAnimation;
import com.jmathanim.Animations.Strategies.ShowCreation.LineCreationAnimation;
import com.jmathanim.Animations.Strategies.ShowCreation.SimpleShapeCreationAnimation;
import com.jmathanim.Constructible.Constructible;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Arrow2D;
import com.jmathanim.mathobjects.Axes.Axes;
import com.jmathanim.mathobjects.CanonicalJMPath;
import com.jmathanim.mathobjects.Delimiter;
import com.jmathanim.mathobjects.LaTeXMathObject;
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MathObjectGroup;
import com.jmathanim.mathobjects.MultiShapeObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.SVGMathObject;
import com.jmathanim.mathobjects.Shape;

/**
 * Animation that shows the creation of a MathObject. The precise strategy for
 * creating depends on the type of MathObject
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class ShowCreation extends Animation {

	public enum ShowCreationStrategy {
		NONE, FIRST_DRAW_AND_THEN_FILL, SIMPLE_SHAPE_CREATION, MULTISHAPE_CREATION, LATEX_CREATION, LINE_CREATION,
		ARROW_CREATION, DELIMITER_CREATION, GROUP_CREATION, AXES_CREATION
	}

	public final Point[] pencilPosition;
	MathObject mobj;
	CanonicalJMPath canonPath;
	private CreationStrategy creationStrategy;
	private ShowCreationStrategy strategyType = ShowCreationStrategy.NONE;
	private MathObject removeThisAtTheEnd = null;
	private MathObject addThisAtTheEnd = null;

	/**
	 * Creates an animation that shows the creation of the specified MathObject
	 *
	 * @param runtime Run time in seconds
	 * @param mobj    Mathobject to animate
	 */
	public ShowCreation(double runtime, MathObject mobj) {
		super(runtime);
		this.mobj = mobj;

		// If the object is a constructible one, get its visible object to animate
		if (mobj instanceof Constructible) {
			this.mobj = ((Constructible) mobj).getMathObject();
			removeThisAtTheEnd = this.mobj;
			addThisAtTheEnd = mobj;
		}
		pencilPosition = new Point[2];
	}

	@Override
	public void initialize(JMathAnimScene scene) {
		super.initialize(scene);
		try {
			if (strategyType == ShowCreationStrategy.NONE) {
				determineCreationStrategy(this.mobj);
			}
			createStrategy();
			creationStrategy.setLambda(lambda);
			creationStrategy.setAddObjectsToScene(this.isShouldAddObjectsToScene());
			creationStrategy.setShouldInterpolateStyles(this.isShouldInterpolateStyles());
			creationStrategy.setUseObjectState(this.isUseObjectState());
			creationStrategy.initialize(scene);
		} catch (NullPointerException | ClassCastException e) {
			JMathAnimScene.logger.error("Couldn't create ShowCreation strategy for "
					+ this.mobj.getClass().getCanonicalName() + ". Animation will not be done. (" + e.toString() + ")");
		}
	}

	@Override
	public void doAnim(double t) {
		// This should't be called, all process through processAnimation
	}

	@Override
	public boolean processAnimation() {
		if (creationStrategy != null) {
			boolean ret = creationStrategy.processAnimation();
			pencilPosition[0] = creationStrategy.getPencilPosition()[0];
			pencilPosition[1] = creationStrategy.getPencilPosition()[1];
			return ret;
		} else {
			return true;
		}
	}

	@Override
	public void finishAnimation() {
		super.finishAnimation();
		if (creationStrategy != null) {
			creationStrategy.finishAnimation();
		}
		scene.remove(removeThisAtTheEnd);
                addObjectsToscene(addThisAtTheEnd);
	}

	/**
	 * Determines the strategy to animate the creation of the object
	 *
	 * @param mobj MathObject which will be animated. Its type determines the type
	 *             of animation to perform.
	 */
	private void determineCreationStrategy(MathObject mobj) {

		if (mobj instanceof Axes) {
			this.strategyType = ShowCreationStrategy.AXES_CREATION;
			return;
		}
		if (mobj instanceof MathObjectGroup) {
			this.strategyType = ShowCreationStrategy.GROUP_CREATION;
			return;
		}
		if (mobj instanceof LaTeXMathObject) {
			this.strategyType = ShowCreationStrategy.LATEX_CREATION;
			return;
		}
		if (mobj instanceof SVGMathObject) {
			this.strategyType = ShowCreationStrategy.FIRST_DRAW_AND_THEN_FILL;
			return;
		}
		if (mobj instanceof Arrow2D) {
			this.strategyType = ShowCreationStrategy.ARROW_CREATION;
			return;
		}
		if (mobj instanceof Delimiter) {
			this.strategyType = ShowCreationStrategy.DELIMITER_CREATION;
			return;
		}
		if (mobj instanceof MultiShapeObject) {
			this.strategyType = ShowCreationStrategy.MULTISHAPE_CREATION;
			return;
		}
		if (mobj instanceof Line) {
			this.strategyType = ShowCreationStrategy.LINE_CREATION;
			return;
		}
		if (mobj instanceof Shape) {
			this.strategyType = ShowCreationStrategy.SIMPLE_SHAPE_CREATION;
			return;
		}

	}

	/**
	 * Sets the animation strategy
	 *
	 * @param <T>          This class
	 * @param strategyType Strategy, chosen from enum ShowCreationStrategy
	 * @return This object
	 */
	public <T extends ShowCreation> T setStrategy(ShowCreationStrategy strategyType) {
		this.strategyType = strategyType;
		return (T) this;
	}

	/**
	 * Creates the strategy object
	 *
	 * @throws ClassCastException If the current object cannot be cast to the
	 *                            required class.
	 */
	private void createStrategy() throws ClassCastException {
		switch (this.strategyType) {
		case GROUP_CREATION:
			creationStrategy = new GroupCreationAnimation(this.runTime, (MathObjectGroup) mobj);
			JMathAnimScene.logger.debug("ShowCreation method: GroupCreationStrategy");
			break;
		case LINE_CREATION:
			creationStrategy = new LineCreationAnimation(this.runTime, (Line) mobj);
			JMathAnimScene.logger.debug("ShowCreation method: LineCreationStrategy");
			break;
		case ARROW_CREATION:
			creationStrategy = new ArrowCreationAnimation(this.runTime, (Arrow2D) mobj);
			JMathAnimScene.logger.debug("ShowCreation method: ArrowCreationStrategy");
			break;
		case DELIMITER_CREATION:
			Delimiter del = (Delimiter) mobj;
			creationStrategy = new CreationStrategy(runTime) {
				@Override
				public void initialize(JMathAnimScene scene) {
					super.initialize(scene);
					addObjectsToscene(del);
				}

				@Override
				public void doAnim(double t) {
					del.setDelimiterScale(lambda.applyAsDouble(t));
				}

			};
			JMathAnimScene.logger.debug("ShowCreation method: Delimiter (growIn)");
			break;

		case SIMPLE_SHAPE_CREATION:
			creationStrategy = new SimpleShapeCreationAnimation(runTime, (Shape) mobj);
			JMathAnimScene.logger.debug("ShowCreation method: SimpleShapeCreationStrategy");
			break;
		case MULTISHAPE_CREATION:
			creationStrategy = new FirstDrawThenFillAnimation(runTime, (MultiShapeObject) mobj);
			JMathAnimScene.logger.debug("ShowCreation method: MultiShapeCreationStrategy");
			break;
		case FIRST_DRAW_AND_THEN_FILL:
			creationStrategy = new FirstDrawThenFillAnimation(runTime, mobj);
			JMathAnimScene.logger.debug("ShowCreation method: FirstDrawThenFillStrategy");
			break;
		case LATEX_CREATION:
			creationStrategy = new FirstDrawThenFillAnimation(runTime, (MultiShapeObject) mobj);
			JMathAnimScene.logger.debug("ShowCreation method: FirstDrawThenFillStrategy (LaTeXMathObject)");
			break;
		case AXES_CREATION:
			creationStrategy = new AxesCreationAnimation(runTime, (Axes) mobj);

		default:
			break;
		}
	}

	/**
	 * Sets the strategy used to create the object
	 * 
	 * @param <T>          Calling subclass
	 * @param strategyType Strategy type. A value from the enum ShowCreationStrategy
	 * @return This object
	 */
	public <T extends ShowCreation> T setStrategyType(ShowCreationStrategy strategyType) {
		this.strategyType = strategyType;
		return (T) this;
	}

	/**
	 * Returns the "pencil" position.
	 *
	 * @return An array with 2 point objects. The 0 index stores the previous
	 *         position of the pencil and 1 stores the current.
	 */
	public Point[] getPencilPosition() {
		return pencilPosition;

	}

}
