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

import com.jmathanim.mathobjects.MathObjectGroup;
import com.jmathanim.mathobjects.Point;

import java.util.ArrayList;

/**
 * A layout resembling a triangular heap of objects
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class HeapLayout extends GroupLayout {

	private final ArrayList<MathObjectGroup> rightSide, leftSide;
	MathObjectGroup center;
	private final Point base;
	private final double horizontalGap, verticalGap;

	public HeapLayout() {
		this(0, 0);
	}

	public HeapLayout(double hgap, double vgap) {
		this(null, hgap, vgap);
	}

	/**
	 * Creates a new heap layout
	 *
	 * @param base Reference point. This will be the lower centered point of the
	 *             heap
	 * @param hgap Horizontal gap
	 * @param vgap Vertical gap
	 */
	public HeapLayout(Point base, double hgap, double vgap) {
		center = new MathObjectGroup();
		rightSide = new ArrayList<>();
		leftSide = new ArrayList<>();
		this.base = base;
		this.horizontalGap = hgap;
		this.verticalGap = vgap;
	}

	@Override
	public void executeLayout(MathObjectGroup group) {
		center.clear();
		rightSide.clear();
		leftSide.clear();
		int n = group.size();
		// Center column
		int k = 0;
		int step = 2;
		while (k < n) {
			center.add(group.get(k));
			k += step;
			step += 2;
		}
		// Left columns
		int colIndex = 1;
		while (colIndex * colIndex < n) {// first element of this column is colIndex*colIndex
			k = colIndex * colIndex;
			MathObjectGroup col = new MathObjectGroup();
			step = (colIndex + 1) * 2;
			while (k < n) {
				col.add(group.get(k));
				k += step;
				step += 2;
			}
			leftSide.add(col);
			colIndex++;
		}
		// Right columns
		colIndex = 2;// Begins at 2
		while (colIndex * colIndex - 1 < n) {// first element of this column is colIndex*colIndex-1
			k = colIndex * colIndex - 1;
			MathObjectGroup col = new MathObjectGroup();
			step = (colIndex) * 2;
			while (k < n) {
				col.add(group.get(k));
				k += step;
				step += 2;
			}
			rightSide.add(col);
			colIndex++;
		}
		// Now that I have created the appropiate groups, stack them
		center.setLayout(MathObjectGroup.Layout.UPPER, this.verticalGap);
		for (MathObjectGroup cols : leftSide) {
			cols.setLayout(MathObjectGroup.Layout.UPPER, this.verticalGap);
		}
		for (MathObjectGroup cols : rightSide) {
			cols.setLayout(MathObjectGroup.Layout.UPPER, this.verticalGap);
		}

		// This group holds all objects, grouped by columns
		MathObjectGroup whole = MathObjectGroup.make();
		for (int col = 0; col < leftSide.size(); col++) {
			MathObjectGroup get = leftSide.get(leftSide.size() - 1 - col);
			whole.add(get);
		}

		whole.add(center);
        for (MathObjectGroup get : rightSide) {
            whole.add(get);
        }
		// Stack them horizontally
		whole.setLayout(MathObjectGroup.Layout.DRIGHT, this.horizontalGap);
		// Allocates them properly
		Point a = center.get(0).getBoundingBox().getLower();// The center
		if (this.base != null) {
			whole.shift(a.to(this.base));
		}

	}

	@Override
	@SuppressWarnings("unchecked")
	public HeapLayout copy() {
		return new HeapLayout(base.copy(), horizontalGap, verticalGap);
	}

}
