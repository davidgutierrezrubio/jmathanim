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

import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class TransformedJMPath extends Shape {

    private final AffineJTransform transform;
    private final Shape srcOBj;

    public TransformedJMPath(Shape jmpobj, AffineJTransform tr) {
        super();
        this.transform = tr;
        this.srcOBj = jmpobj;
        this.copyStateFrom(jmpobj);
    }

    @Override
    public TransformedJMPath copy() {
        return new TransformedJMPath(srcOBj.copy(), transform.copy());
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        setUpdateLevel(srcOBj.getUpdateLevel() + 1);
    }

    @Override
    public void update(JMathAnimScene scene) {
        super.update(scene);
        int size = srcOBj.getPath().size();
        for (int n = 0; n < size; n++) {
            JMPathPoint jmPDst = get(n);

            jmPDst.v.copyFrom(jmPDst.v);
            jmPDst.vExit.copyFrom(jmPDst.vExit);
            jmPDst.vEnter.copyFrom(jmPDst.vEnter);

            jmPDst.v.applyAffineTransform(transform);
            jmPDst.vExit.applyAffineTransform(transform);
            jmPDst.vEnter.applyAffineTransform(transform);

        }
    }

}
