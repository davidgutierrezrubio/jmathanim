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

import com.jmathanim.Enum.AnchorType;
import com.jmathanim.Enum.BoxDirection;
import com.jmathanim.mathobjects.AbstractMathGroup;
import com.jmathanim.mathobjects.Coordinates;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MathObjectGroup;

import java.util.ArrayList;
import java.util.function.IntToDoubleFunction;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class FlowLayout extends AbstractBoxLayout {

	public IntToDoubleFunction rowLength;
	BoxDirection boxDirection;

    public static FlowLayout make(Coordinates<?> corner, double width, double inRowGap, double inColGap){
        return new FlowLayout(corner, width,BoxDirection.RIGHT_UP,inRowGap,inColGap);
    }
	/**
	 * Creates a new FlowLayout
	 * @param corner
	 * @param width
	 * @param boxDirection
	 * @param inRowGap
	 * @param inColGap
	 */
	protected FlowLayout(Coordinates<?> corner, double width, BoxDirection boxDirection, double inRowGap, double inColGap) {
		super(corner, inRowGap, inColGap);
		rowLength = (int row) -> width;
		this.boxDirection = boxDirection;
		computeDirections(boxDirection);
	}

    protected FlowLayout(Coordinates<?>  corner, IntToDoubleFunction widthFunction, BoxDirection boxDirection, double inRowGap,
					  double inColGap) {
		super(corner, inRowGap, inColGap);
		rowLength = widthFunction;
		this.boxDirection = boxDirection;
		computeDirections(boxDirection);
	}

	private double getAppropiateSize(MathObject<?> obj) {
		double resul = 0;
		switch (boxDirection) {
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
	public void executeLayout(AbstractMathGroup<?> group) {
		ArrayList<MathObjectGroup> rowGroups = getRowGroups(group);

//		rowGroups.get(0).get(0).stackTo(firstElementStack, this.corner, AnchorType.CENTER, 0);
		rowGroups.get(0).get(0).stack()
				.withOriginAnchor(firstElementStack)
				.withDestinyAnchor(AnchorType.CENTER)
				.toObject(this.corner);

		for (int n = 1; n < rowGroups.get(0).size(); n++) {
//			rowGroups.get(0).get(n).stackTo(rowGroups.get(0).get(n - 1), inRowStack, inRowGap);
			rowGroups.get(0).get(n).stack()
					.withDestinyAnchor(inRowStack)
					.withGaps(this.inRowGap,this.inRowGap)
					.toObject(rowGroups.get(0).get(n - 1));
		}

		for (int k = 1; k < rowGroups.size(); k++) {
//			rowGroups.get(k).get(0).stackTo(rowGroups.get(k - 1).get(0), inColStack, inColGap);
			rowGroups.get(k).get(0).stack()
					.withGaps(this.inColGap,this.inColGap)
					.withDestinyAnchor(inColStack)
					.toObject(rowGroups.get(k - 1).get(0));

			for (int n = 1; n < rowGroups.get(k).size(); n++) {
//				rowGroups.get(k).get(n).stackTo(rowGroups.get(k).get(n - 1), inRowStack, inRowGap);
				rowGroups.get(k).get(n).stack()
						.withGaps(this.inRowGap,this.inRowGap)
						.withDestinyAnchor(inRowStack)
						.toObject(rowGroups.get(k).get(n - 1));
			}
			MathObject.Align align = null;
			switch (boxDirection) {
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

	public ArrayList<MathObjectGroup> getRowGroups(AbstractMathGroup<?> group) {
		ArrayList<MathObjectGroup> resul = new ArrayList<>();
		MathObject<?> firstOfTheRow = group.get(0);
		MathObjectGroup currentRow = MathObjectGroup.make(firstOfTheRow);
		resul.add(currentRow);
		int rowNumber = 0;
		double totalWidth = getAppropiateSize(firstOfTheRow);// when this variable is greater than size, go to a new
																// line
		// Puts the first element in the corner point
//		firstOfTheRow.stackTo(corner, firstElementStack);
		firstOfTheRow.stack()
				.withDestinyAnchor(firstElementStack)
				.toObject(corner);
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
			return new FlowLayout(corner.copy(), this.rowLength, boxDirection, inRowGap, inColGap);
		} else {
			return new FlowLayout(null, this.rowLength, boxDirection, inRowGap, inColGap);
		}
	}
}
