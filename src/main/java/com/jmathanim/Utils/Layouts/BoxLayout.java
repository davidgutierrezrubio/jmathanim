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
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MathObjectGroup;
import com.jmathanim.mathobjects.Point;

/**
 * A box layout. This layout allocates the objects following a row-column
 * strategy.
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class BoxLayout extends AbstractBoxLayout {

    int rowSize;

    /**
     * Creates a new BoxLayout. The default direction is RIGHT_UP.
     *
     * @param rowSize Number of elements per row
     * @param colGap Column gap
     * @param rowGap Row gap
     * @return The created layout
     */
    public static BoxLayout make(int rowSize, double colGap, double rowGap) {
        return new BoxLayout(rowSize, Direction.RIGHT_UP, colGap, rowGap);
    }

    /**
     * Creates a new BoxLayout, with no gaps, and direction RIGHT_UP
     *
     * @param rowSize Number of elements per row
     * @return The created layout
     */
    public static BoxLayout make(int rowSize) {
        return new BoxLayout(rowSize);
    }
    

    public BoxLayout(int rowSize) {
        this(null, rowSize, Direction.RIGHT_UP, 0, 0);
    }

    /**
     * Creates a box layout with specified corner point, no gaps, and RIGHT_UP
     * direction
     *
     * @param corner A Point object, the lower left corner of the box
     * @param rowSize Numbers of elements in a row
     */
    public BoxLayout(Point corner, int rowSize) {
        this(corner, rowSize, Direction.RIGHT_UP, 0, 0);
    }

    /**
     * Creates a box layout
     *
     * @param corner A point object, the corner of the box. The precise corner
     * depends on the direction chosen. For example if direction is RIGHT_UP,
     * the corner will be the lower left one. If direction is DOWN_LEFT, the
     * corner will be the upper right one.
     * @param rowSize Numbers of element in each row. Note that "row" becomes
     * "columns" if direction is UP_LEFT, UP_RIGHT, DOWN_LEFT or DOWN_RIGHT
     * @param direction Direction of the box. Specifies the direction to stack
     * the elements. A direction of RIGHT_UP will stack the row in the RIGHT
     * direction and then UP to allocate the next row
     * @param inRowGap Gap between 2 consecutive elements in the same row
     * @param inColGap Gap between 2 consecutive elements in the same column
     */
    public BoxLayout(Point corner, int rowSize, Direction direction, double inRowGap, double inColGap) {
        super(corner, inRowGap, inColGap);
        computeDirections(direction);
        this.rowSize = rowSize;
    }

    /**
     * Overloaded method. Creates a box layout with no corner
     *
     * @param rowSize Numbers of element in each row. Note that "row" becomes
     * "columns" if direction is UP_LEFT, UP_RIGHT, DOWN_LEFT or DOWN_RIGHT
     * @param direction Direction of the box. Specifies the direction to stack
     * the elements. A direction of RIGHT_UP will stack the row in the RIGHT
     * direction and then UP to allocate the next row
     * @param inRowGap Gap between 2 consecutive elements in the same row
     * @param inColGap Gap between 2 consecutive elements in the same column
     */
    public BoxLayout(int rowSize, Direction direction, double inRowGap, double inColGap) {
        this(null, rowSize, direction, inRowGap, inColGap);
    }

    @Override
    public void applyLayout(MathObjectGroup group) {
        int rowCounter = 0; // This counter checks if another row is necessary
        MathObject firstOfTheRow = group.get(0);
        // Stacks the first object to the corner point, if it exists
        if (this.corner != null) {
            group.get(0).stackTo(firstElementStack, this.corner, Anchor.Type.CENTER, 0);
        }

        for (int n = 1; n < group.size(); n++) {// n=0 gets unaltered
            rowCounter++;
            if (rowCounter < this.rowSize) {
                group.get(n).stackTo(group.get(n - 1), inRowStack, this.inRowGap);
            }

            if (rowCounter == this.rowSize) {
                rowCounter = 0;
                group.get(n).stackTo(firstOfTheRow, inColStack, this.inColGap);
                firstOfTheRow = group.get(n);
            }
        }

    }

    /**
     * Returns a MathObjectGroup containing other MathObjectGroup objects, one
     * for each row. So for example, getRowGroups(group).get(0) will return a
     * MathObjectGroup with all objects from the first row.
     *
     * @param group The group to get the rows from
     * @return A MathObjectGroup with all rows
     */
    public MathObjectGroup getRowGroups(MathObjectGroup group) {
        MathObjectGroup rowGroups = MathObjectGroup.make();
        int rowCounter = 0;
        MathObjectGroup currentRow = MathObjectGroup.make();
        for (int n = 0; n < group.size(); n++) {
            if (rowCounter < this.rowSize) {
                rowCounter++;
            } else {
                rowCounter = 1;
                rowGroups.add(currentRow);
                currentRow = MathObjectGroup.make();
            }
            currentRow.add(group.get(n));
        }
        // Check if last row is added (this is neeeded if last row is incomplete)
        if (!rowGroups.getObjects().contains(currentRow)) {
            rowGroups.add(currentRow);
        }
        return rowGroups;
    }

    /**
     * Returns a MathObjectGroup containing other MathObjectGroup objects, one
     * for each column. So for example, getColGroups(group).get(0) will return a
     * MathObjectGroup with all objects from the first column.
     *
     * @param group The group to get the columns from
     * @return A MathObjectGroup with all columns
     */
    public MathObjectGroup getColumnGroups(MathObjectGroup group) {
        MathObjectGroup colGroup = MathObjectGroup.make();
        MathObjectGroup currentCol = MathObjectGroup.make();
        for (int n = 0; n < this.rowSize; n++) {// There will be exactly this columns
            int index = n;// First index to look for columns
            while (index < group.size()) {
                currentCol.add(group.get(index));
                index += this.rowSize;
            }
            colGroup.add(currentCol);
            currentCol = MathObjectGroup.make();
        }
        return colGroup;
    }

    @Override
    public BoxLayout copy() {
        if (this.corner != null) {
            return new BoxLayout(corner.copy(), rowSize, Direction.RIGHT_UP, inRowGap, inColGap);
        } else {
            return new BoxLayout(rowSize, Direction.RIGHT_UP, inRowGap, inColGap);
        }
    }

}
