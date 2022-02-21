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
package com.jmathanim.Animations;

import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class TransformStrategyChecker {

	private static boolean testTransform(Shape shORig, Shape shDest, AffineJTransform tr, double epsilon) {
		Shape sh = tr.getTransformedObject(shORig);
		return sh.getPath().isEquivalentTo(shDest.getPath(), epsilon);
	}

	/**
	 * Test of one shape can be transformed to another one using an homothecy
	 *
	 * @param shORig  Origin Shape
	 * @param shDest  Destinty Shape
	 * @param epsilon Max error
	 * @return True if a homothecy is supported. False otherwise
	 */
	public static boolean testDirectHomothecyTransform(Shape shORig, Shape shDest, double epsilon) {
		if (!checkMinimalPathRequirements(shORig, 2, shDest, 2)) {
			return false;
		}
		Point[] points = getIdealPoints(4, shORig, shDest);
		AffineJTransform tr = AffineJTransform.createDirect2DIsomorphic(points[0], points[1], points[2], points[3], 1);
		return testTransform(shORig, shDest, tr, epsilon);
	}

	private static boolean checkMinimalPathRequirements(Shape shORig, int n1, Shape shDest, int n2) {
		boolean mininumPoints = ((shORig.getPath().size() >= n1) && (shDest.getPath().size() >= n2));
		boolean sameSize = (shORig.getPath().size() == shDest.getPath().size());
		return mininumPoints & sameSize;
	}

	/**
	 * Test of one shape can be transformed to another one using a general affine
	 * transform
	 *
	 * @param shORig  Origin Shape
	 * @param shDest  Destinty Shape
	 * @param epsilon Max error
	 * @return True if a general affine transform is supported. False otherwise
	 */
	public static boolean testGeneralAffineTransform(Shape shORig, Shape shDest, double epsilon) {
		if (!checkMinimalPathRequirements(shORig, 3, shDest, 3)) {
			return false;
		}
		Point[] p = getIdealPoints(6, shORig, shDest);
		AffineJTransform tr = AffineJTransform.createAffineTransformation(p[0], p[1], p[2], p[3], p[4], p[5], 1);
		return testTransform(shORig, shDest, tr, epsilon);
	}

	/**
	 * Test of one shape can be transformed to another one using a general affine
	 * transform
	 *
	 * @param shORig  Origin Shape
	 * @param shDest  Destinty Shape
	 * @param epsilon Max error
	 * @return True if a general affine transform is supported. False otherwise
	 */
	public static boolean testRotateScaleXYTransform(Shape shORig, Shape shDest, double epsilon) {
		if (!checkMinimalPathRequirements(shORig, 3, shDest, 3)) {
			return false;
		}
		Point[] p = getIdealPoints(6, shORig, shDest);
		AffineJTransform tr = AffineJTransform.createRotateScaleXYTransformation(p[0], p[1], p[2], p[3], p[4], p[5], 1);
		return testTransform(shORig, shDest, tr, epsilon);
	}

	private static Point[] getIdealPoints(int numPoints, Shape shORig, Shape shDest) {
		Point[] points = null;
		if (numPoints == 4) {
			Point A = shORig.getPoint(0);// TODO: Take better points (as far as possible)
			Point B = shORig.getPoint(1);
			Point C = shDest.getPoint(0);
			Point D = shDest.getPoint(1);
			points = new Point[4];
			points[0] = A;
			points[1] = B;
			points[2] = C;
			points[3] = D;
		}
		if (numPoints == 6) {
			Point A = shORig.getPoint(0);// TODO: Take better points (as far as possible)
			Point B = shORig.getPoint(1);
			Point C = shORig.getPoint(2);
			Point D = shDest.getPoint(0);
			Point E = shDest.getPoint(1);
			Point F = shDest.getPoint(2);
			points = new Point[6];
			points[0] = A;
			points[1] = B;
			points[2] = C;
			points[3] = D;
			points[4] = E;
			points[5] = F;
		}

		return points;
	}
}
