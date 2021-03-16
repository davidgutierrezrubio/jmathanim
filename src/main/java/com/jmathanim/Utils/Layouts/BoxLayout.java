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
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class BoxLayout extends GroupLayout {

    private final Point corner;

    int rowSize;
    private double horizontalGap, verticalGap;

    public enum Direction {
        RIGHT_UP, RIGHT_DOWN, LEFT_UP, LEFT_DOWN, UP_RIGHT, UP_LEFT, DOWN_RIGHT, DOWN_LEFT
    }

    Anchor.Type inRowStack, inColStack, firstElementStack;

    public BoxLayout(int rowSize) {
        this(null, rowSize, Direction.RIGHT_UP, 0, 0);
    }

    public BoxLayout(Point corner, int rowSize) {
        this(corner, rowSize, Direction.RIGHT_UP, 0, 0);
    }

    public BoxLayout(Point corner, int rows, Direction direction, double hgap, double vgap) {
        this.rowSize = rows;
        this.horizontalGap = hgap;
        this.verticalGap = vgap;
        this.corner = corner;

        switch (direction) {
            case RIGHT_UP:
                inRowStack = Anchor.Type.RIGHT;
                inColStack = Anchor.Type.UPPER;
                firstElementStack = Anchor.Type.DL;
                break;
            case RIGHT_DOWN:
                inRowStack = Anchor.Type.RIGHT;
                inColStack = Anchor.Type.LOWER;
                firstElementStack = Anchor.Type.UL;
                break;
            case LEFT_UP:
                inRowStack = Anchor.Type.LEFT;
                inColStack = Anchor.Type.UPPER;
                firstElementStack = Anchor.Type.DR;
                break;
            case LEFT_DOWN:
                inRowStack = Anchor.Type.LEFT;
                inColStack = Anchor.Type.LOWER;
                firstElementStack = Anchor.Type.UR;
                break;
            case UP_RIGHT:
                inRowStack = Anchor.Type.UPPER;
                inColStack = Anchor.Type.RIGHT;
                firstElementStack = Anchor.Type.DL;
                break;
            case UP_LEFT:
                inRowStack = Anchor.Type.UPPER;
                inColStack = Anchor.Type.LEFT;
                firstElementStack = Anchor.Type.DR;
                break;
            case DOWN_RIGHT:
                inRowStack = Anchor.Type.LOWER;
                inColStack = Anchor.Type.RIGHT;
                firstElementStack = Anchor.Type.UL;
                break;
            case DOWN_LEFT:
                inRowStack = Anchor.Type.LOWER;
                inColStack = Anchor.Type.LEFT;
                firstElementStack = Anchor.Type.UR;
                break;
            default://Default case, rowSize goes to right, columns to the heaven
                inRowStack = Anchor.Type.RIGHT;
                inColStack = Anchor.Type.UPPER;
                firstElementStack = Anchor.Type.DL;
                break;
        }
    }

    @Override
    public void applyLayout(MathObjectGroup group) {
        int rowCounter = 0; //This counter checks if another row is necessary
        MathObject firstOfTheRow = group.get(0);
        //Stacks the first object to the corner point, if it exists
        if (this.corner != null) {
            group.get(0).stackTo(firstElementStack, this.corner, Anchor.Type.CENTER, 0);
        }

        for (int n = 1; n < group.size(); n++) {//n=0 gets unaltered
            rowCounter++;
            if (rowCounter < this.rowSize) {
                group.get(n).stackTo(group.get(n - 1), inRowStack, this.horizontalGap);
            }

            if (rowCounter == this.rowSize) {
                rowCounter = 0;
                group.get(n).stackTo(firstOfTheRow, inColStack, this.verticalGap);
                firstOfTheRow = group.get(n);
            }

        }

    }

}
