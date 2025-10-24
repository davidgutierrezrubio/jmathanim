/*
 * Copyright (C) 2020 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
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
package com.jmathanim.MathObjects.Shapes;

import com.jmathanim.MathObjects.AbstractMultiShapeObject;
import com.jmathanim.MathObjects.Shape;

import java.util.Arrays;
import java.util.List;

/**
 * This class stores multiple Shapes, and properly apply transforms and animations to them
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class MultiShapeObject extends AbstractMultiShapeObject<MultiShapeObject, Shape> {

    public MultiShapeObject() {
        super(Shape.class);
    }


    protected MultiShapeObject(List<Shape> shapes) {
        super(Shape.class, shapes);

    }

    public static MultiShapeObject make(Shape... shapes) {
        return new MultiShapeObject(Arrays.asList(shapes));
    }


    @Override
    public MultiShapeObject copy() {
        MultiShapeObject copy = MultiShapeObject.make();
        copy.copyStateFrom(this);
        return copy;
    }

    @Override
    protected Shape createEmptyShapeAt(int index) {
        Shape sh = new Shape();
        if (index < size()) {
            shapes.set(index, sh);
        } else {
            shapes.add(index, sh);
        }
        mpMultiShape.add(sh);
        return sh;
    }

    @Override
    public MultiShapeObject makeNewEmptyInstance() {
        return MultiShapeObject.make();
    }
}
