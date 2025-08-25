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
package com.jmathanim.mathobjects.updateableObjects;

import com.jmathanim.Enum.BooleanOperation;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.Shape;
import com.jmathanim.mathobjects.Stateable;

/**
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class BooleanShape extends Shape {

    Shape shape1, shape2;
    private BooleanOperation booleanOperation;

    public BooleanShape(BooleanOperation booleanOperation, Shape shape1, Shape shape2) {
        this.shape1 = shape1;
        this.shape2 = shape2;
        this.booleanOperation = booleanOperation;
    }

    @Override
    public void update(JMathAnimScene scene) {
        super.update(scene);
        JMPath newPath = null;
        switch (this.booleanOperation) {
            case UNION:
                newPath = shape1.getUnionPath(shape2);
                break;
            case INTERSECTION:
                newPath = shape1.getIntersectionPath(shape2);
                break;
            case SUBSTRACTION:
                newPath = shape1.getSubstractPath(shape2);
                break;
        }
        this.getPath().clear();
        this.getPath().addJMPointsFrom(newPath);
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        scene.registerUpdateable(shape1, shape2);
        setUpdateLevel(Math.max(shape1.getUpdateLevel(), shape2.getUpdateLevel()) + 1);
    }

    @Override
    public Shape copy() {
        BooleanShape copy = new BooleanShape(this.booleanOperation, shape1.copy(), shape2.copy());
        copy.copyStateFrom(this);
        return this;
    }

    @Override
    public void copyStateFrom(Stateable obj) {
        if (!(obj instanceof BooleanShape)) return;
        super.copyStateFrom(obj);
        BooleanShape bs = (BooleanShape) obj;
        this.shape1.copyStateFrom(bs.shape1);
        this.shape2.copyStateFrom(bs.shape2);
        this.booleanOperation = bs.booleanOperation;
    }

}
