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
package com.jmathanim.Constructible.Conics;

import com.jmathanim.Constructible.Points.ConstrPoint;
import com.jmathanim.Constructible.Constructible;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.Rect;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

/**
 * Represents a Circle imported from Geogebra with 2 points (center and another
 * one in the perimeter)
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class ConstrCircleCenterPoint extends Constructible {

	// Circle(point,point)
	// Circle(point,number)
	// Circle(point,Segment)
	// Circle(point,point,point)
	ConstrPoint A, B;
	protected double radius;
	public final Point circleCenter;
	private final Shape originalCircle;
	private final Shape circleToDraw;

	public static ConstrCircleCenterPoint make(ConstrPoint A, ConstrPoint B) {
		ConstrCircleCenterPoint resul = new ConstrCircleCenterPoint(A, B);
		resul.rebuildShape();
		return resul;
	}

	protected ConstrCircleCenterPoint(ConstrPoint A, ConstrPoint B) {
		super();
		this.A = A;
		this.B = B;
		originalCircle = Shape.circle();
		circleToDraw = new Shape();
		circleCenter = Point.at(0, 0);
	}

	@Override
	public <T extends MathObject> T copy() {
		return (T) ConstrCircleCenterPoint.make(A.copy(), B.copy());
	}

	@Override
	public void draw(JMathAnimScene scene, Renderer r) {
		circleToDraw.draw(scene, r);
	}

	@Override
	public Rect getBoundingBox() {
		rebuildShape();
		return circleToDraw.getBoundingBox();
	}

	@Override
	public MathObject getMathObject() {
		return circleToDraw;
	}

	@Override
	public final void rebuildShape() {
		computeCircleCenterRadius();
		circleToDraw.getPath().jmPathPoints.clear();
		circleToDraw.getPath().addJMPointsFrom(originalCircle.copy().getPath());
		circleToDraw.scale(this.radius);
		circleToDraw.shift(this.circleCenter.v);
	}

	public void computeCircleCenterRadius() {
		this.radius = A.to(B).norm();
		this.circleCenter.v.x = A.v.x;
		this.circleCenter.v.y = A.v.y;
	}

	@Override
	public int getUpdateLevel() {
		return Math.max(A.getUpdateLevel(), B.getUpdateLevel()) + 1;
	}

}
