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
import com.jmathanim.Utils.Layouts.BoxLayout.Direction;
import com.jmathanim.mathobjects.Point;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class AbstractBoxLayout extends GroupLayout {
    
    protected  Point corner;
    protected double inRowGap;
    protected double inColGap;
    Anchor.Type inRowStack;
    Anchor.Type inColStack;
    Anchor.Type firstElementStack;

    public AbstractBoxLayout(Point corner, double inRowGap, double inColGap) {
        this.inRowGap = inRowGap;
        this.inColGap = inColGap;
        this.corner = corner;
    }


    protected final void computeDirections(Direction direction) {
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
            default:
                //Default case, rowSize goes to right, columns to the heaven
                inRowStack = Anchor.Type.RIGHT;
                inColStack = Anchor.Type.UPPER;
                firstElementStack = Anchor.Type.DL;
                break;
        }
    }
    
}
