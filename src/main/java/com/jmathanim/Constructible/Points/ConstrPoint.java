/*
 * Copyright (C) 2022 David
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR point PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.jmathanim.Constructible.Points;

import com.jmathanim.Constructible.Constructible;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;

/**
 *
 * @author David
 */
public class ConstrPoint extends Constructible {

    private final Point point;
    public final Vec v;
    public static ConstrPoint make(Point A) {
        return new ConstrPoint(A);
    }

    private ConstrPoint(Point A) {
        this.point=A;
        this.v=A.v;
    }
    @Override
    public Point getMathObject() {
        return point;
    }

    @Override
    public void rebuildShape() {
    }

    @Override
    public ConstrPoint copy() {
        return make(point.copy());
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r) {
        point.draw(scene, r);
    }

    public Vec to(ConstrPoint B) {
        return point.to(B.getMathObject());
    }
    
    public ConstrPoint add(Vec v) {
        return ConstrPoint.make(point.add(v));
    }


}
