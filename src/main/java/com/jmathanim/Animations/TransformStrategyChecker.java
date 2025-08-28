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
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.AbstractShape;
import com.jmathanim.mathobjects.JMPath;
import org.apache.commons.math3.linear.SingularMatrixException;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class TransformStrategyChecker {

    private static boolean testTransform(AbstractShape<?> shORig, AbstractShape<?> shDest, AffineJTransform tr, double epsilon) {
        JMPath pathOrigTransformed = shORig.getPath().copy().applyAffineTransform(tr);
        return shDest.getPath().isEquivalentTo(pathOrigTransformed, epsilon);
    }

    /**
     * Test of one shape can be transformed to another one using an isomorphism
     *
     * @param shORig Origin Shape
     * @param shDest Destinty Shape
     * @param epsilon Max error
     * @return True if an isomorphism is supported. False otherwise
     */
    public static boolean testDirectIsomorphismTransform(AbstractShape<?> shORig, AbstractShape<?> shDest, double epsilon) {
        if (!checkMinimalPathRequirements(shORig, 2, shDest, 2)) {
            return false;
        }
        Vec[] points = getIdealPoints(4, shORig, shDest);
        try {
            AffineJTransform tr = AffineJTransform.createDirect2DIsomorphic(points[0], points[1], points[2], points[3], 1);
            return testTransform(shORig, shDest, tr, epsilon);
        } catch (SingularMatrixException e) {
            return false;
        }
    }

    private static boolean checkMinimalPathRequirements(AbstractShape<?> shORig, int n1, AbstractShape<?> shDest, int n2) {
        boolean mininumPoints = ((shORig.getPath().size() >= n1) && (shDest.getPath().size() >= n2));
        boolean sameSize = (shORig.getPath().size() == shDest.getPath().size());
        return mininumPoints & sameSize;
    }

    /**
     * Test of one shape can be transformed to another one using a general
     * affine transform
     *
     * @param shORig Origin Shape
     * @param shDest Destinty Shape
     * @param epsilon Max error
     * @return True if a general affine transform is supported. False otherwise
     */
    public static boolean testGeneralAffineTransform(AbstractShape<?> shORig, AbstractShape<?> shDest, double epsilon) {
        if (!checkMinimalPathRequirements(shORig, 3, shDest, 3)) {
            return false;
        }
        Vec[] p = getIdealPoints(6, shORig, shDest);
        try {
            AffineJTransform tr = AffineJTransform.createAffineTransformation(p[0], p[1], p[2], p[3], p[4], p[5], 1);
            return testTransform(shORig, shDest, tr, epsilon);
        } catch (SingularMatrixException e) {
            return false;
        }
    }

    /**
     * Test of one shape can be transformed to another one using a general
     * affine transform
     *
     * @param shORig Origin Shape
     * @param shDest Destinty Shape
     * @param epsilon Max error
     * @return True if a general affine transform is supported. False otherwise
     */
    public static boolean testRotateScaleXYTransform(AbstractShape<?> shORig, AbstractShape<?> shDest, double epsilon) {
        if (!checkMinimalPathRequirements(shORig, 3, shDest, 3)) {
            return false;
        }
        Vec[] p = getIdealPoints(6, shORig, shDest);
        try {
            AffineJTransform tr = AffineJTransform.createRotateScaleXYTransformation(p[0], p[1], p[2], p[3], p[4], p[5], 1);
            return testTransform(shORig, shDest, tr, epsilon);
        } catch (SingularMatrixException e) {
            return false;
        }
    }

    private static Vec[] getIdealPoints(int numPoints, AbstractShape<?> shORig, AbstractShape<?> shDest) {
        Vec[] points = null;
        if (numPoints == 4) {
            Vec A = shORig.get(0).getV();// TODO: Take better points (as far as possible)
            Vec B = shORig.get(1).getV();
            Vec C = shDest.get(0).getV();
            Vec D = shDest.get(1).getV();
            points = new Vec[4];
            points[0] = A;
            points[1] = B;
            points[2] = C;
            points[3] = D;
        }
        if (numPoints == 6) {
            Vec A = shORig.get(0).getV();// TODO: Take better points (as far as possible)
            Vec B = shORig.get(1).getV();
            Vec C = shORig.get(2).getV();
            Vec D = shDest.get(0).getV();
            Vec E = shDest.get(1).getV();
            Vec F = shDest.get(2).getV();
            points = new Vec[6];
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
