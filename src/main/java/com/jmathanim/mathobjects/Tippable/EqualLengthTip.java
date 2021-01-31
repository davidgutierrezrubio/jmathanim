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
package com.jmathanim.mathobjects.Tippable;

import com.jmathanim.mathobjects.MultiShapeObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class EqualLengthTip extends TippableObject {

    int numberOfTips;

    public static EqualLengthTip make(int numberOfTips) {
        EqualLengthTip resul = new EqualLengthTip(numberOfTips);
        MultiShapeObject parallelSign = new MultiShapeObject();
        for (int i = 0; i < numberOfTips; i++) {
            parallelSign.add(Shape.segment(Point.at(0,.25*i), Point.at(1,.25*i)));
        }
        resul.setTip(parallelSign);
        return resul;
    }

    private EqualLengthTip(int numberOfTips) {
        super();
        this.numberOfTips = numberOfTips;

    }

}
