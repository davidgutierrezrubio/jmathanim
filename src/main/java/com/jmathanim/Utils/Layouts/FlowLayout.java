/*
 * Copyright (C) 2021 David Gutierrez Rubio davidgutierrezrubio@gmail.com
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
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MathObjectGroup;
import com.jmathanim.mathobjects.Point;

import java.util.ArrayList;
import java.util.function.IntToDoubleFunction;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class FlowLayout extends AbstractBoxLayout {

	public IntToDoubleFunction rowLength;
	BoxLayout.Direction direction;

	public FlowLayout(Point corner, double width, BoxLayout.Direction direction, double inRowGap, double inColGap) {
		super(corner, inRowGap, inColGap);
		rowLength = (int row) -> width;
		this.direction = direction;
		computeDirections(direction);
	}

	public FlowLayout(Point corner, IntToDoubleFunction widthFunction, BoxLayout.Direction direction, double inRowGap,
			double inColGap) {
		super(corner, inRowGap, inColGap);
		rowLength = widthFunction;
		this.direction = direction;
		computeDirections(direction);
	}

	private double getAppropiateSize(MathObject obj) {
		double resul = 0;
		switch (direction) {
		case DOWN_LEFT:
		case DOWN_RIGHT:
		case UP_LEFT:
		case UP_RIGHT:
			resul = obj.getHeight();
			break;
		case LEFT_DOWN:
		case LEFT_UP:
		case RIGHT_DOWN:
		case RIGHT_UP:
			resul = obj.getWidth();
			break;
		}
		return resul;
	}

	@Override
	public void executeLayout(MathObjectGroup group) {
		ArrayList<MathObjectGroup> rowGroups = getRowGroups(group);

		rowGroups.get(0).get(0).stackTo(firstElementStack, this.corner, Anchor.Type.CENTER, 0);
		for (int n = 1; n < rowGroups.get(0).size(); n++) {
			rowGroups.get(0).get(n).stackTo(rowGroups.get(0).get(n - 1), inRowStack, inRowGap);
		}

		for (int k = 1; k < rowGroups.size(); k++) {
			rowGroups.get(k).get(0).stackTo(rowGroups.get(k - 1).get(0), inColStack, inColGap);
			for (int n = 1; n < rowGroups.get(k).size(); n++) {
				rowGroups.get(k).get(n).stackTo(rowGroups.get(k).get(n - 1), inRowStack, inRowGap);
			}
			MathObject.Align align = null;
			switch (direction) {
			case RIGHT_UP:
			case RIGHT_DOWN:
				align = MathObject.Align.LEFT;
				break;
			case LEFT_UP:
			case LEFT_DOWN:
				align = MathObject.Align.RIGHT;
				break;
			case UP_RIGHT:
			case UP_LEFT:
				align = MathObject.Align.LOWER;
				break;
			case DOWN_RIGHT:
			case DOWN_LEFT:
				align = MathObject.Align.UPPER;
				break;
			}
			rowGroups.get(k).align(rowGroups.get(0), align);
		}
	}

	public ArrayList<MathObjectGroup> getRowGroups(MathObjectGroup group) {
		ArrayList<MathObjectGroup> resul = new ArrayList<>();
		MathObject firstOfTheRow = group.get(0);
		MathObjectGroup currentRow = MathObjectGroup.make(firstOfTheRow);
		resul.add(currentRow);
		int rowNumber = 0;
		double totalWidth = getAppropiateSize(firstOfTheRow);// when this variable is greater than size, go to a new
																// line
		// Puts the first element in the corner point
		firstOfTheRow.stackTo(corner, firstElementStack);
		// Now the rest
		for (int n = 1; n < group.size(); n++) {
			totalWidth += getAppropiateSize(group.get(n)) + inRowGap;
			if (totalWidth <= rowLength.applyAsDouble(rowNumber)) {
				currentRow.add(group.get(n));
			} else {
				rowNumber++;
				firstOfTheRow = group.get(n);
				totalWidth = getAppropiateSize(firstOfTheRow);
				currentRow = MathObjectGroup.make(firstOfTheRow);
				resul.add(currentRow);
			}

		}
		return resul;
	}

	@Override
	public FlowLayout copy() {
		if (this.corner != null) {
			return new FlowLayout(corner.copy(), this.rowLength, direction, inRowGap, inColGap);
		} else {
			return new FlowLayout(null, this.rowLength, direction, inRowGap, inColGap);
		}
	}
}
