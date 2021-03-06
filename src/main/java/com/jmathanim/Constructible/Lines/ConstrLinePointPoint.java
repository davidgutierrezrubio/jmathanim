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

import com.jmathanim.Constructible.Constructible;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class ConstrLinePointPoint extends Constructible implements HasDirection {

	private final Line lineToDraw;
	Point A, B;

	public static ConstrLinePointPoint make(Point A, Point B) {
		ConstrLinePointPoint resul = new ConstrLinePointPoint(A, B);
		resul.rebuildShape();
		return resul;
	}

	private ConstrLinePointPoint(Point A, Point B) {
		this.A = A;
		this.B = B;
		lineToDraw = Line.make(A, B);
	}

	@Override
	public <T extends MathObject> T copy() {
		return (T) ConstrLinePointPoint.make(A.copy(), B.copy());
	}

	@Override
	public void draw(JMathAnimScene scene, Renderer r) {
		lineToDraw.draw(scene, r);

	}

	@Override
	public MathObject getMathObject() {
		return lineToDraw;
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
