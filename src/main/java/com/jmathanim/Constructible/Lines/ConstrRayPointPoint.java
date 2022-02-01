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
package com.jmathanim.Constructible.Lines;

import com.jmathanim.Constructible.ConstrPoint;
import com.jmathanim.Constructible.Constructible;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Ray;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class ConstrRayPointPoint extends Constructible implements HasDirection {

    private final Ray rayToDraw;
    ConstrPoint A, B;

    public static ConstrRayPointPoint make(ConstrPoint A, ConstrPoint B) {
        ConstrRayPointPoint resul = new ConstrRayPointPoint(A, B);
        resul.rebuildShape();
        return resul;
    }

    private ConstrRayPointPoint(ConstrPoint A, ConstrPoint B) {
        this.A = A;
        this.B = B;
        rayToDraw = Ray.make(A.getMathObject(), B.getMathObject());
    }

    @Override
    public <T extends MathObject> T copy() {
        return (T) ConstrRayPointPoint.make(A.copy(), B.copy());
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r) {
        rayToDraw.draw(scene, r);

    }

    @Override
    public MathObject getMathObject() {
        return rayToDraw;
    }

    @Override
    public void rebuildShape() {
        // Nothing is needed, the line is updated by itself
    }

    @Override
    public Vec getDirection() {
        return A.to(B);
    }
}
