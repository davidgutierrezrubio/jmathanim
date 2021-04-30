/*
 * Copyright (C) 2021 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
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
package com.jmathanim.Utils.Layouts;

import com.jmathanim.Utils.Anchor;
import com.jmathanim.mathobjects.MathObjectGroup;
import com.jmathanim.mathobjects.Point;

/**
 * A Layout following a spiral form. The first object is the center of the
 * spiral and then next ones are allocated in a clockwise or counterclockwise
 * orientation.
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class SpiralLayout extends GroupLayout {

	private final Point center;
	private double horizontalGap, verticalGap;
	private int spiralGap;

	public enum Orientation {
		UPPER_CLOCKWISE, UPPER_COUNTERCLOCKWISE, RIGHT_CLOCKWISE, RIGHT_COUNTERCLOCKWISE, LOWER_CLOCKWISE,
		LOWER_COUNTERCLOCKWISE, LEFT_CLOCKWISE, LEFT_COUNTERCLOCKWISE
	}

	Orientation orientation;
	Anchor.Type[] stacks;

	/**
	 * Constructor with default values. A spiral layout with no gaps, the second
	 * object is put at the right of the first one, and follows a clockwise
	 * orientation. The reference point is the center of the first object, so this
	 * is unaffected.
	 *
	 */
	public SpiralLayout() {
		this(null, Orientation.RIGHT_CLOCKWISE, 0, 0);
	}

	/**
	 * Creates a spiral layout
	 *
	 * @param center      Center of the spiral. The center of the first object will
	 *                    be this point.
	 * @param orientation Orientation of the spiral and where to put the second
	 *                    object. A orientation of LEFT_CLOCKWISE puts the second
	 *                    block at the left of the first one, following a clockwise
	 *                    pattern for the next ones.
	 * @param hgap        Horizontal gap
	 * @param vgap        Vertical gap
	 */
	public SpiralLayout(Point center, Orientation orientation, double hgap, double vgap) {
		this.center = center;
		this.orientation = orientation;
		this.horizontalGap = hgap;
		this.verticalGap = vgap;
		this.spiralGap = 0;
		switch (orientation) {
		case LEFT_CLOCKWISE:
			stacks = new Anchor.Type[] { Anchor.Type.LEFT, Anchor.Type.UPPER, Anchor.Type.RIGHT, Anchor.Type.LOWER };
			break;
		case RIGHT_CLOCKWISE:
			stacks = new Anchor.Type[] { Anchor.Type.RIGHT, Anchor.Type.LOWER, Anchor.Type.LEFT, Anchor.Type.UPPER };
			break;
		case UPPER_CLOCKWISE:
			stacks = new Anchor.Type[] { Anchor.Type.UPPER, Anchor.Type.RIGHT, Anchor.Type.LOWER, Anchor.Type.LEFT };
			break;
		case LOWER_CLOCKWISE:
			stacks = new Anchor.Type[] { Anchor.Type.LOWER, Anchor.Type.LEFT, Anchor.Type.UPPER, Anchor.Type.RIGHT };
			break;
		case LEFT_COUNTERCLOCKWISE:
			stacks = new Anchor.Type[] { Anchor.Type.LEFT, Anchor.Type.LOWER, Anchor.Type.RIGHT, Anchor.Type.UPPER };
			break;
		case RIGHT_COUNTERCLOCKWISE:
			stacks = new Anchor.Type[] { Anchor.Type.RIGHT, Anchor.Type.UPPER, Anchor.Type.LEFT, Anchor.Type.LOWER };
			break;
		case UPPER_COUNTERCLOCKWISE:
			stacks = new Anchor.Type[] { Anchor.Type.UPPER, Anchor.Type.LEFT, Anchor.Type.LOWER, Anchor.Type.RIGHT };
			break;
		case LOWER_COUNTERCLOCKWISE:
			stacks = new Anchor.Type[] { Anchor.Type.LOWER, Anchor.Type.RIGHT, Anchor.Type.UPPER, Anchor.Type.LEFT };
			break;
		}

	}

	@Override
	public void applyLayout(MathObjectGroup group) {
		if (this.center != null) {// Stack first element to the center
			group.get(0).stackTo(this.center, Anchor.Type.CENTER);
		}
		int stackType = 0;// Index to the array of used stacks
		int numberOfStacks = Math.max(1, spiralGap);// This variable holds how many objects should I stack before doing
													// a "turn"
		int turnNumber = 1;
		for (int n = 1; n < group.size(); n++) {
			Anchor.Type stack = stacks[stackType];
			group.get(n).stackTo(Anchor.reverseAnchorPoint(stack), group.get(n - 1), stack, this.horizontalGap,
					this.verticalGap);
			numberOfStacks--;
			if (numberOfStacks == 0) {// Ok, time to turn...
				turnNumber++;
				numberOfStacks = ((spiralGap + 1) * turnNumber + 1) / 2;// integer division: 1,1,2,2,3,3,4,4,....
				stackType = (stackType + 1) % 4;
			}
		}

	}

	@Override
	public SpiralLayout copy() {
		if (this.center != null) {
			return new SpiralLayout(center.copy(), orientation, verticalGap, verticalGap);
		} else {
			return new SpiralLayout(null, orientation, verticalGap, verticalGap);
		}
	}

	/**
	 * Sets the spiral gap
	 *
	 * @param <T>       Calling class
	 * @param spiralGap The spiral gap. A value of 0 means all revolutions of the
	 *                  spiral are glued together. A value of 1 or greater leaves a
	 *                  proportional space between revolutions depending on the
	 *                  sizes of the objects.
	 * @return This object
	 */
	public <T extends SpiralLayout> T setSpiralGap(int spiralGap) {
		this.spiralGap = spiralGap;
		return (T) this;
	}

}
