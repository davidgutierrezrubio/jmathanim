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
import com.jmathanim.Utils.Rect;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MathObjectGroup;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class PascalLayout extends GroupLayout {

	private final Point top;// Upper point of the triangle
	private final double horizontalGap;
	private final double verticalGap;

	public PascalLayout() {
		this(0, 0);
	}

	public PascalLayout(double horizontalGap, double verticalGap) {
		this.top = null;
		this.horizontalGap = horizontalGap;
		this.verticalGap = verticalGap;
	}

	public PascalLayout(Point top, double horizontalGap, double verticalGap) {
		this.top = top;
		this.horizontalGap = horizontalGap;
		this.verticalGap = verticalGap;
	}

	@Override
	public void executeLayout(MathObjectGroup group) {
		if (group.isEmpty()) {
			return; // Nothing to do here...
		}
		double w = 0;
		double h = 0;
		for (MathObject g : group) {
			w = Math.max(w, g.getWidth());
			h = Math.max(h, g.getHeight());
		}
		Rect r = new Rect(0, 0, w, h);
		r = r.addGap(.5 * this.horizontalGap, .5 * this.verticalGap);
		MathObjectGroup workGroup = MathObjectGroup.make();

		// Complete the last row in case it is not complete
		// Find the first triangular number bigger than the group size
		int num = 0;
		int k = 0;
		while (num < group.size()) {
			num = k * (k + 1) / 2;
			k++;
		}
		for (int n = 0; n < num; n++) {

			workGroup.add(Shape.rectangle(r));

		}
		// Add the first element
		int rowSize = 1;
		int counter = 0;
		int rowCounter = 0;
		MathObjectGroup rows = MathObjectGroup.make();
		while (counter < workGroup.size()) {// Main loop
			MathObjectGroup grRow = MathObjectGroup.make();
			while (rowCounter < rowSize) {
				if (counter < workGroup.size()) {
					grRow.add(workGroup.get(counter));
				}
				counter++;
				rowCounter++;
			}
			grRow.setLayout(MathObjectGroup.Layout.RIGHT, 0);
			rows.add(grRow);
			rowSize++;
			rowCounter = 0;
		}
		rows.setLayout(MathObjectGroup.Layout.LOWER, 0);

		// Now that the rectangles are properly located, move the original objects so
		// that their centers match those of the workgroup
		for (int n = 0; n < group.size(); n++) {
			group.get(n).stackTo(workGroup.get(n), Anchor.Type.CENTER);
		}
		// Move now so that the top center point of first element match the Point top.
		Point A = Anchor.getAnchorPoint(group.get(0), Anchor.Type.UPPER);
		if (top != null) {
			group.shift(A.to(top));
		}
	}

	/**
	 * Returns a MathObjectGroup containing other MathObjectGroup objects, one for
	 * each row. So for example, getRowGroups(group).get(3) will return a
	 * MathObjectGroup with all objects from the fourth row (that will hold 4
	 * objects)
	 *
	 * @param group The group to get the rows from
	 * @return A MathObjectGroup with all rows
	 */
	public MathObjectGroup getRowGroups(MathObjectGroup group) {
		int rowSize = 1;
		int counter = 0;
		int rowCounter = 0;
		MathObjectGroup rows = MathObjectGroup.make();
		while (counter < group.size()) {// Main loop
			MathObjectGroup grRow = MathObjectGroup.make();
			while (rowCounter < rowSize) {
				if (counter < group.size()) {
					grRow.add(group.get(counter));
				}
				counter++;
				rowCounter++;
			}
			rows.add(grRow);
			rowSize++;
			rowCounter = 0;
		}
		return rows;
	}

	@Override
	public PascalLayout copy() {
		if (top != null) {
			return new PascalLayout(top.copy(), horizontalGap, verticalGap);
		} else {
			return new PascalLayout(null, horizontalGap, verticalGap);
		}
	}
}
