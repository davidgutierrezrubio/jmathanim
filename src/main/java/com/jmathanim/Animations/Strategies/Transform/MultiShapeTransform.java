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
package com.jmathanim.Animations.Strategies.Transform;

import com.jmathanim.Animations.AnimationGroup;
import com.jmathanim.Animations.Transform;
import com.jmathanim.mathobjects.MultiShapeObject;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class MultiShapeTransform extends AnimationGroup {

    public MultiShapeTransform(double runtime, MultiShapeObject mobjTransformed, MultiShapeObject mobjDestiny) {
        super();
        int numAnims = Math.min(mobjTransformed.shapes.size(), mobjDestiny.shapes.size());
        for (int n = 0; n < numAnims - 1; n++) {
            add(new Transform(runtime, mobjTransformed.get(n), mobjDestiny.get(n)));
        }
        //Now, merge the remaining with the last
        for (int n = numAnims-1; n < mobjTransformed.shapes.size(); n++) {
            for (int m = numAnims-1; m < mobjDestiny.shapes.size(); m++) {
                add(new Transform(runtime, mobjTransformed.get(n), mobjDestiny.get(m)));
            }
        }

    }

}
