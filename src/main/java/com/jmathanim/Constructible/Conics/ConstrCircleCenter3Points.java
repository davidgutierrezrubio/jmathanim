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

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class ConstrCircleCenter3Points extends ConstrCircleCenterPoint {

	public final Point P0, P1, P2;

	public static ConstrCircleCenter3Points make(Point P0, Point P1, Point P2) {
		ConstrCircleCenter3Points resul = new ConstrCircleCenter3Points(P0, P1, P2);
		resul.rebuildShape();
		return resul;
	}

	private ConstrCircleCenter3Points(Point P0, Point P1, Point P2) {
		super(Point.origin(), Point.origin());
		this.P0 = P0;
		this.P1 = P1;
		this.P2 = P2;
	}

	@Override
	public void computeCircleCenterRadius() {
		findCircle(P0.v.x, P0.v.y, P1.v.x, P1.v.y, P2.v.x, P2.v.y);
	}

	@Override
	public int getUpdateLevel() {
		return Math.max(Math.max(P0.getUpdateLevel(), P1.getUpdateLevel()), P2.getUpdateLevel()) + 1;
	}

// Function to find the circle on
// which the given three points lie
//Found in https://www.geeksforgeeks.org/equation-of-circle-when-three-points-on-the-circle-are-given/
	private void findCircle(double x1, double y1, double x2, double y2, double x3, double y3) {
		double x12 = x1 - x2;
		double x13 = x1 - x3;

		double y12 = y1 - y2;
		double y13 = y1 - y3;

		double y31 = y3 - y1;
		double y21 = y2 - y1;

		double x31 = x3 - x1;
		double x21 = x2 - x1;

		// x1^2 - x3^2
		double sx13 = ((x1 * x1) - (x3 * x3));

		// y1^2 - y3^2
		double sy13 = ((y1 * y1) - (y3 * y3));

		double sx21 = ((x2 * x2) - (x1 * x1));

		double sy21 = ((y2 * y2) - (y1 * y1));

		double f = ((sx13) * (x12) + (sy13) * (x12) + (sx21) * (x13) + (sy21) * (x13))
				/ (2 * ((y31) * (x12) - (y21) * (x13)));
		double g = ((sx13) * (y12) + (sy13) * (y12) + (sx21) * (y13) + (sy21) * (y13))
				/ (2 * ((x31) * (y12) - (x21) * (y13)));

		double c = -(int) (x1 * x1) - (int) (y1 * y1) - 2 * g * x1 - 2 * f * y1;

		// eqn of circle be x^2 + y^2 + 2*g*x + 2*f*y + c = 0
		// where centre is (h = -g, k = -f) and radius r
		// as r^2 = h^2 + k^2 - c
		double h = -g;
		double k = -f;

		// r is the radius

//        this.radius = Math.sqrt(sqr_of_r);//this doesn't work
		this.circleCenter.v.x = h;
		this.circleCenter.v.y = k;
		this.radius = this.circleCenter.to(P0).norm();
		// Center (h,k)
	}
// This code is contributed by chandan_jnu
}
