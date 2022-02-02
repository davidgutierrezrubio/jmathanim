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

import com.jmathanim.Constructible.Points.ConstrPoint;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;

/**
 * A Constructible Line that pass through A and is orthogonal to a given direction
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class ConstrLineOrthogonal extends ConstrLine {

    public static ConstrLineOrthogonal make(ConstrPoint A, HasDirection dir) {
        ConstrLineOrthogonal resul = new ConstrLineOrthogonal(A, dir);
        resul.rebuildShape();
        return resul;
    }

    private ConstrLineOrthogonal(ConstrPoint A, HasDirection dir) {
        super(A,A.add(dir.getDirection()));
    }

    @Override
    public ConstrLineOrthogonal copy() {
        ConstrLineOrthogonal copy = make(A.copy(), dir);
        copy.getMp().copyFrom(this.getMp());
        return copy;
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r) {
        lineToDraw.draw(scene, r);
    }

    @Override
    public void rebuildShape() {
        Vec v = A.getMathObject().v;
        Vec direction = dir.getDirection();
        lineToDraw.getP1().v.x = v.x;
        lineToDraw.getP1().v.y = v.y;

        lineToDraw.getP2().v.x = v.x - direction.y;
        lineToDraw.getP2().v.y = v.y + direction.x;
    }

}
