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
import com.jmathanim.Enum.RowAlignType;
import com.jmathanim.mathobjects.Point;

/**
 * An abstract class to implement boxed-like layouts, like BoxLayout and
 * FlowLayout
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class AbstractBoxLayout extends GroupLayout {




    protected Point corner;
    protected double inRowGap;
    protected double inColGap;
    AnchorType inRowStack;
    AnchorType inColStack;
    AnchorType firstElementStack;
    RowAlignType rowAlignType;

    public AbstractBoxLayout(Point corner, double inRowGap, double inColGap) {
        this.inRowGap = inRowGap;
        this.inColGap = inColGap;
        this.corner = corner;
    }

    public <T extends AbstractBoxLayout> T setDirection(BoxDirection dir) {
        computeDirections(dir);
        return (T) this;
    }

    protected final void computeDirections(BoxDirection boxDirection) {
        switch (boxDirection) {
            case RIGHT_UP:
                inRowStack = AnchorType.RIGHT;
                inColStack = AnchorType.UPPER;
                firstElementStack = AnchorType.DLEFT;
                break;
            case RIGHT_DOWN:
                inRowStack = AnchorType.RIGHT;
                inColStack = AnchorType.LOWER;
                firstElementStack = AnchorType.ULEFT;
                break;
            case LEFT_UP:
                inRowStack = AnchorType.LEFT;
                inColStack = AnchorType.UPPER;
                firstElementStack = AnchorType.DRIGHT;
                break;
            case LEFT_DOWN:
                inRowStack = AnchorType.LEFT;
                inColStack = AnchorType.LOWER;
                firstElementStack = AnchorType.URIGHT;
                break;
            case UP_RIGHT:
                inRowStack = AnchorType.UPPER;
                inColStack = AnchorType.RIGHT;
                firstElementStack = AnchorType.DLEFT;
                break;
            case UP_LEFT:
                inRowStack = AnchorType.UPPER;
                inColStack = AnchorType.LEFT;
                firstElementStack = AnchorType.DRIGHT;
                break;
            case DOWN_RIGHT:
                inRowStack = AnchorType.LOWER;
                inColStack = AnchorType.RIGHT;
                firstElementStack = AnchorType.ULEFT;
                break;
            case DOWN_LEFT:
                inRowStack = AnchorType.LOWER;
                inColStack = AnchorType.LEFT;
                firstElementStack = AnchorType.URIGHT;
                break;
            default:
                // Default case, rowSize goes to right, columns to the heaven
                inRowStack = AnchorType.RIGHT;
                inColStack = AnchorType.UPPER;
                firstElementStack = AnchorType.DLEFT;
                break;
        }
    }

    public void setRowAlign(RowAlignType type) {
        switch (type) {
            case UPPER:
                if (inRowStack == AnchorType.LEFT) {
                    inRowStack = AnchorType.ULEFT;
                }
                if (inRowStack == AnchorType.RIGHT) {
                    inRowStack = AnchorType.URIGHT;
                }
                if (inRowStack == AnchorType.UPPER) {
                    inRowStack = AnchorType.ULEFT;
                }
                if (inRowStack == AnchorType.LOWER) {
                    inRowStack = AnchorType.DLEFT;
                }

                break;
            case MIDDLE:
                break;
            case LOWER:
                if (inRowStack == AnchorType.LEFT) {
                    inRowStack = AnchorType.DLEFT;
                }
                if (inRowStack == AnchorType.RIGHT) {
                    inRowStack = AnchorType.DRIGHT;
                }
                if (inRowStack == AnchorType.UPPER) {
                    inRowStack = AnchorType.URIGHT;
                }
                if (inRowStack == AnchorType.LOWER) {
                    inRowStack = AnchorType.DRIGHT;
                }
                break;
        }
    }

    public Point getCorner() {
        return corner;
    }

    public void setCorner(Point corner) {
        this.corner = corner;
    }

    public double getInRowGap() {
        return inRowGap;
    }

    public <T extends AbstractBoxLayout> T setInRowGap(double inRowGap) {
        this.inRowGap = inRowGap;
        return (T) this;
    }

    public double getInColGap() {
        return inColGap;
    }

    public <T extends AbstractBoxLayout> T setInColGap(double inColGap) {
        this.inColGap = inColGap;
        return (T) this;
    }

}
