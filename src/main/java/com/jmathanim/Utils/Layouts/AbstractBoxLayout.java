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
import com.jmathanim.mathobjects.Point;

/**
 * An abstract class to implement boxed-like layouts, like BoxLayout and
 * FlowLayout
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class AbstractBoxLayout extends GroupLayout {

    public enum Direction {
        RIGHT_UP, RIGHT_DOWN, LEFT_UP, LEFT_DOWN, UP_RIGHT, UP_LEFT, DOWN_RIGHT, DOWN_LEFT
    }

    public enum RowAlignType {
        UPPER, LOWER, MIDDLE
    }

    protected Point corner;
    protected double inRowGap;
    protected double inColGap;
    Anchor.Type inRowStack;
    Anchor.Type inColStack;
    Anchor.Type firstElementStack;
    RowAlignType rowAlignType;

    public AbstractBoxLayout(Point corner, double inRowGap, double inColGap) {
        this.inRowGap = inRowGap;
        this.inColGap = inColGap;
        this.corner = corner;
    }

    public <T extends AbstractBoxLayout> T setDirection(Direction dir) {
        computeDirections(dir);
        return (T) this;
    }

    protected final void computeDirections(Direction direction) {
        switch (direction) {
            case RIGHT_UP:
                inRowStack = Anchor.Type.RIGHT;
                inColStack = Anchor.Type.UPPER;
                firstElementStack = Anchor.Type.DLEFT;
                break;
            case RIGHT_DOWN:
                inRowStack = Anchor.Type.RIGHT;
                inColStack = Anchor.Type.LOWER;
                firstElementStack = Anchor.Type.ULEFT;
                break;
            case LEFT_UP:
                inRowStack = Anchor.Type.LEFT;
                inColStack = Anchor.Type.UPPER;
                firstElementStack = Anchor.Type.DRIGHT;
                break;
            case LEFT_DOWN:
                inRowStack = Anchor.Type.LEFT;
                inColStack = Anchor.Type.LOWER;
                firstElementStack = Anchor.Type.URIGHT;
                break;
            case UP_RIGHT:
                inRowStack = Anchor.Type.UPPER;
                inColStack = Anchor.Type.RIGHT;
                firstElementStack = Anchor.Type.DLEFT;
                break;
            case UP_LEFT:
                inRowStack = Anchor.Type.UPPER;
                inColStack = Anchor.Type.LEFT;
                firstElementStack = Anchor.Type.DRIGHT;
                break;
            case DOWN_RIGHT:
                inRowStack = Anchor.Type.LOWER;
                inColStack = Anchor.Type.RIGHT;
                firstElementStack = Anchor.Type.ULEFT;
                break;
            case DOWN_LEFT:
                inRowStack = Anchor.Type.LOWER;
                inColStack = Anchor.Type.LEFT;
                firstElementStack = Anchor.Type.URIGHT;
                break;
            default:
                // Default case, rowSize goes to right, columns to the heaven
                inRowStack = Anchor.Type.RIGHT;
                inColStack = Anchor.Type.UPPER;
                firstElementStack = Anchor.Type.DLEFT;
                break;
        }
    }

    public void setRowAlign(RowAlignType type) {
        switch (type) {
            case UPPER:
                if (inRowStack == Anchor.Type.LEFT) {
                    inRowStack = Anchor.Type.ULEFT;
                }
                if (inRowStack == Anchor.Type.RIGHT) {
                    inRowStack = Anchor.Type.URIGHT;
                }
                if (inRowStack == Anchor.Type.UPPER) {
                    inRowStack = Anchor.Type.ULEFT;
                }
                if (inRowStack == Anchor.Type.LOWER) {
                    inRowStack = Anchor.Type.DLEFT;
                }

                break;
            case MIDDLE:
                break;
            case LOWER:
                if (inRowStack == Anchor.Type.LEFT) {
                    inRowStack = Anchor.Type.DLEFT;
                }
                if (inRowStack == Anchor.Type.RIGHT) {
                    inRowStack = Anchor.Type.DRIGHT;
                }
                if (inRowStack == Anchor.Type.UPPER) {
                    inRowStack = Anchor.Type.URIGHT;
                }
                if (inRowStack == Anchor.Type.LOWER) {
                    inRowStack = Anchor.Type.DRIGHT;
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
