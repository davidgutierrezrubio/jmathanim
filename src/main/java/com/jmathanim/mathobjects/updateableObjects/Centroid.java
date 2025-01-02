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
package com.jmathanim.mathobjects.updateableObjects;

import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

/**
 * This class represents middle point computed from a set of given ones. This
 * class implements the interface updateable, which automatically updates its
 * components. This object represents the centroid when a pure polgonal shape is
 * given.
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Centroid extends Point implements Updateable {

    private final Shape shape;

    public Centroid(Shape shape) {
        super();
        this.shape = shape;
    }

    @Override
    public void update(JMathAnimScene scene) {
        super.update(scene);
        Vec resul = new Vec(0, 0);
        for (int n = 0; n < shape.size(); n++) {
            resul.addInSite(shape.get(n).p.v);
        }
        resul.multInSite(1.0d / shape.size());
        this.v.copyFrom(resul);
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        setUpdateLevel(shape.getUpdateLevel() + 1);
    }

}
