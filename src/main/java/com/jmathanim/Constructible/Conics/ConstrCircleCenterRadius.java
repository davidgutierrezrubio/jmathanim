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

import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Scalar;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class ConstrCircleCenterRadius extends ConstrCircleCenterPoint {
	Scalar scalarRadius;
	private final Point A;

	public static ConstrCircleCenterRadius make(Point A, Scalar radius) {
		ConstrCircleCenterRadius resul = new ConstrCircleCenterRadius(A, radius);

		resul.rebuildShape();
		return resul;
	}

	private ConstrCircleCenterRadius(Point A, Scalar radius) {
		super(A, Point.origin());
		this.A = A;
		this.scalarRadius = radius;

	}

	@Override
	public void computeCircleCenterRadius() {
		this.radius = scalarRadius.value;
		this.circleCenter.v.x = A.v.x;
		this.circleCenter.v.y = A.v.y;
	}

	@Override
	public int getUpdateLevel() {
		return Math.max(scalarRadius.getUpdateLevel(), this.A.getUpdateLevel()) + 1;
	}

}
