/*
 * Copyright (C) 2021 David
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
package com.jmathanim.Utils;

import com.jmathanim.MathObjects.Coordinates;

/**
 * Represents an empty rect. Should be returned and managed in special cases
 * (for example, when computing the bounding box of an empty shape, unions...)
 *
 */
public class EmptyRect extends Rect {

    public EmptyRect() {
        super(0, 0, 0, 0);
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public String toString() {
        return "EmptyRect";
    }

    @Override
    public Rect addGap(double rightGap, double upperGap, double leftGap, double lowerGap, double zMinGap, double zMaxGap) {
        return this;
    }

    @Override
    public Rect scale(double xs, double ys, double zs) {
        return this;
    }

    @Override
    public Rect shift(Vec v) {
        return this;
    }

    @Override
    public Rect getRotatedRect(double rotateAngle) {
        return this;
    }

    @Override
    public Rect centerAt(Coordinates<?> dstCenter) {
        return this;
    }

    @Override
    public Rect getTransformedRect(AffineJTransform tr) {
        return this;
    }
}
