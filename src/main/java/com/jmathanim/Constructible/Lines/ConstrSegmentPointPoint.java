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
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class ConstrSegmentPointPoint extends Constructible implements HasDirection {
	public final Point A, B;
	private final Shape segmentToDraw;

	public static ConstrSegmentPointPoint make(Point A, Point B) {
		ConstrSegmentPointPoint resul = new ConstrSegmentPointPoint(A, B);
		resul.rebuildShape();
		return resul;
	}

	private ConstrSegmentPointPoint(Point A, Point B) {
		this.A = A;
		this.B = B;
		segmentToDraw = Shape.segment(A, B);
	}

	@Override
	public <T extends MathObject> T copy() {
		return (T) ConstrSegmentPointPoint.make(this.A.copy(), this.B.copy());
	}

	@Override
	public void draw(JMathAnimScene scene, Renderer r) {
		segmentToDraw.draw(scene, r);
	}

	@Override
	public Vec getDirection() {
		return A.to(B);
	}

	@Override
	public MathObject getMathObject() {
		return segmentToDraw;
	}

	public Point getA() {
		return A;
	}

	public Point getB() {
		return B;
	}

	@Override
	public void rebuildShape() {
		// Nothing to do here...
	}

}
