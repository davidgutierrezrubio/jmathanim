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
package com.jmathanim.Utils;

import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Arrow2D;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MathObjectGroup;
import com.jmathanim.mathobjects.MultiShapeObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

/**
 * This class handles affine transform, both in 2D and 3D To restrict transforms
 * to 2D, is enough to leave last row and col of transform Matrix as Identity
 * Affine transform Matrix has the following form: 1 x y z 0 vx vy vz 0 wx wy wz
 * 0 tx ty tz Where x,y,z is the image of (0,0,0) and v,w,z are the images of
 * canonical vectors
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class AffineJTransform {

    /**
     * Matrix that stores the transform, with the following form: {{1, x, y, z},
     * {0, vx, vy, vz},{0, wx, wy, wz},{0 tx ty tz}} Where x,y,z is the image of
     * (0,0,0) and v,w,z are the images of canonical vectors.
     */
    public RealMatrix matrix;

    /**
     * Creates a new Identity transform
     */
    public AffineJTransform() {
        this(MatrixUtils.createRealIdentityMatrix(4));
    }

    /**
     * Creates a new transform, with the specified matrix
     *
     * @param rm Matrix with the following form: {{1, x, y, z}, {0, vx, vy,
     * vz},{0, wx, wy, wz},{0 tx ty tz}} Where x,y,z is the image of (0,0,0) and
     * v,w,z are the images of canonical vectors.
     */
    public AffineJTransform(RealMatrix rm) {
        this.matrix = rm;
    }

    /**
     * Sets the imagen of the origin by this transform In practice, it changes
     * the first row of the matrix transform
     *
     * @param v Destiny point of origin (0,0,0), given by a vector
     */
    public void setOriginImg(Vec v) {
        setOriginImg(v.x, v.y, v.z);
    }

    /**
     * Overloaded method
     *
     * @param p Destiny point of origin (0,0,0)
     */
    public void setOriginImg(Point p) {
        setOriginImg(p.v);
    }

    /**
     * Overloaded method for 2D transformations. z coordinate is assumed 0.
     *
     * @param x x-coordinate of image of origin (0,0,0)
     * @param y y-coordinate of image of origin (0,0,0)
     */
    public void setOriginImg(double x, double y) {
        setOriginImg(x, y, 0);
    }

    /**
     * Overloaded method.
     *
     * @param x x-coordinate of image of origin (0,0,0)
     * @param y y-coordinate of image of origin (0,0,0)
     * @param z z-coordinate of image of origin (0,0,0)
     */
    public void setOriginImg(double x, double y, double z) {
        matrix.setRow(0, new double[]{1, x, y, z});
    }

    /**
     * Sets the imagen of the first canonical vector (1,0,0) by this transform
     * In practice, it changes the second row of the matrix transform
     *
     * @param v Destiny vector of (1,0,0)
     */
    public void setV1Img(Vec v) {
        setV1Img(v.x, v.y, v.z);
    }

    /**
     * Overloaded method
     *
     * @param p Destiny vector of (1,0,0) given by a vector
     */
    public void setV1Img(Point p) {
        setV1Img(p.v);
    }

    /**
     * Overloaded method
     *
     * @param x x-coordinate of image of the first canonical vector (1,0,0)
     * @param y y-coordinate of image of the first canonical vector (1,0,0)
     * @param z z-coordinate of image of the first canonical vector (1,0,0)
     */
    public void setV1Img(double x, double y, double z) {
        matrix.setRow(1, new double[]{0, x, y, z});
    }

    /**
     * Overloaded method for 2D transformations. z coordinate is assumed 0.
     *
     * @param x x-coordinate of image of the first canonical vector (1,0,0)
     * @param y y-coordinate of image of the first canonical vector (1,0,0)
     */
    public void setV1Img(double x, double y) {
        setV1Img(x, y, 0);
    }

    /**
     * Sets the imagen of the second canonical vector (0,1,0) by this transform
     * In practice, it changes the third row of the matrix transform
     *
     * @param v Destiny vector of (0,1,0)
     */
    public void setV2Img(Vec v) {
        setV2Img(v.x, v.y, v.z);
    }

    /**
     * Overloaded method
     *
     * @param p Destiny vector of (0,1,0) given by a point
     */
    public void setV2Img(Point p) {
        setV2Img(p.v);
    }

    /**
     * Overloaded method
     *
     * @param x x-coordinate of image of the second canonical vector (0,1,0)
     * @param y y-coordinate of image of the second canonical vector (0,1,0)
     * @param z z-coordinate of image of the second canonical vector (0,1,0)
     */
    public void setV2Img(double x, double y, double z) {
        matrix.setRow(2, new double[]{0, x, y, z});
    }

    /**
     * Overloaded method for 2D transformations. z coordinate is assumed 0.
     *
     * @param x x-coordinate of image of the second canonical vector (0,1,0)
     * @param y y-coordinate of image of the second canonical vector (0,1,0)
     */
    public void setV2Img(double x, double y) {
        setV2Img(x, y, 0);
    }

    /**
     * Sets the imagen of the third canonical vector (0,0,1) by this transform
     * In practice, it changes the fourth row of the matrix transform
     *
     * @param v Destiny vector of (0,0,1)
     */
    public void setV3Img(Vec v) {
        setV3Img(v.x, v.y, v.z);
    }

    /**
     * Overloaded method
     *
     * @param p Destiny vector of (0,0,1) given by a point
     */
    public void setV3Img(Point p) {
        setV3Img(p.v);
    }

    /**
     * Overloaded method
     *
     * @param x x-coordinate of image of the third canonical vector (0,0,1)
     * @param y y-coordinate of image of the third canonical vector (0,0,1)
     * @param z z-coordinate of image of the third canonical vector (0,0,1)
     */
    public void setV3Img(double x, double y, double z) {
        matrix.setRow(3, new double[]{0, x, y, z});
    }

    /**
     * Overloaded method for 2D transformations. z coordinate is assumed 0.
     *
     * @param x x-coordinate of image of the third canonical vector (0,0,1)
     * @param y y-coordinate of image of the third canonical vector (0,0,1)
     */
    public void setV3Img(double x, double y) {
        setV3Img(x, y, 0);
    }

    /**
     * Apply the current transform to the given MathObject. Strategy
     * transformation depends on the type of MathObject. The object is modified.
     *
     * For MultiShapeObject objects iterates the transformation over all its
     * shapes
     *
     * For Lineobjects transform its defining points.
     *
     * For Shape objects iterates the transformation over all the JMPathPoint
     * objects.
     *
     * For JMPathPoint objects, transform is applied to its point and control
     * points.
     *
     * @param mObject Object to apply transform
     */
    public void applyTransform(MathObject mObject) {

        if (mObject instanceof MathObjectGroup) {
            MathObjectGroup mobj = (MathObjectGroup) mObject;
            for (MathObject obj : mobj.getObjects()) {
                applyTransform(obj);
            }
            return;
        }

        if (mObject instanceof MultiShapeObject) {
            MultiShapeObject mobj = (MultiShapeObject) mObject;
            for (Shape obj : mobj.shapes) {
                applyTransform(obj);
            }
            return;
        }
        if (mObject instanceof Line) {
            Line mobj = (Line) mObject;
            applyTransform(mobj.getP1());
            applyTransform(mobj.getP2());
            applyTransformsToDrawingProperties(mObject);
            return;
        }
        if (mObject instanceof Shape) {
            Shape mobj = (Shape) mObject;
            int size = mobj.jmpath.size();
            for (int n = 0; n < size; n++) {
                applyTransform(mobj.getJMPoint(n));
            }
            applyTransformsToDrawingProperties(mObject);
            return;
        }

        if (mObject instanceof Arrow2D) {
            applyTransform(((Arrow2D) mObject).getBody());
            applyTransformsToDrawingProperties(mObject);
            return;
        }

        if (mObject instanceof JMPathPoint) {
            JMPathPoint jmPDst = (JMPathPoint) mObject;
            JMPathPoint pSrc = jmPDst.copy();
            Point pDst = getTransformedObject(pSrc.p);
            Point cp1Dst = getTransformedObject(pSrc.cp1);
            Point cp2Dst = getTransformedObject(pSrc.cp2);

            jmPDst.p.v.copyFrom(pDst.v);
            jmPDst.cp1.v.copyFrom(cp1Dst.v);
            jmPDst.cp2.v.copyFrom(cp2Dst.v);

            applyTransformsToDrawingProperties(mObject);
            return;
        }

        if (mObject instanceof Point) {
            Point p = (Point) mObject;
            RealMatrix pRow = new Array2DRowRealMatrix(new double[][]{{1d, p.v.x, p.v.y, p.v.z}});
            RealMatrix pNew = pRow.multiply(matrix);

            p.v.x = pNew.getEntry(0, 1);
            p.v.y = pNew.getEntry(0, 2);
            p.v.z = pNew.getEntry(0, 3);
            applyTransformsToDrawingProperties(mObject);
            return;
        }
        JMathAnimScene.logger.warn("Don't know how to perform an Affine Transform on object " + mObject.getClass().getName());
    }

    private void applyTransformsToDrawingProperties(MathObject mObject) {
        //Determinant of the A_xy=2D-submatrix, to compute change in thickness
        //As Area changes in det(A_xy), we change thickness in the root square of det(A_xy)
        double det = matrix.getEntry(1, 1) * matrix.getEntry(2, 2) - matrix.getEntry(2, 1) * matrix.getEntry(1, 2);
        if (!mObject.mp.absoluteThickness) {
            mObject.mp.thickness *= Math.sqrt(det);
        }

    }

    /**
     * Returns a copy of the transformed object. Original object is not
     * modified.
     *
     * @param <T> Object type
     * @param obj Mathobject to transform
     * @return The transformed object
     */
    public <T extends MathObject> T getTransformedObject(MathObject obj) {

        T resul = obj.copy();
        applyTransform(resul);
        return (T) resul;
    }

    /**
     * Compose another Affine Transform and returns the new AffineTransform if
     * C=A.compose(B) The resulting transform C,applied to a point, will result
     * in applying first A and then B. Mathematically C(x)=B(A(x))
     *
     * @param tr The AffintTransform to compose with
     * @return The composed AffineTransform
     */
    public AffineJTransform compose(AffineJTransform tr) {
        return new AffineJTransform(matrix.multiply(tr.matrix));
    }

    /**
     * Gets the inverse transform
     *
     * @return The inverse transform
     */
    public AffineJTransform getInverse() {
        RealMatrix B = new LUDecomposition(matrix).getSolver().getInverse();
        return new AffineJTransform(B);
    }

    /**
     * Overloaded method. Creates an AffineTransform that moves a into b
     *
     * @param a Origin
     * @param b Destiny
     * @return A newAffineTransform with traslation
     */
    public static AffineJTransform createTranslationTransform(Point a, Point b) {
        return createTranslationTransform(new Vec(b.v.x - a.v.x, b.v.y - a.v.y, b.v.z - a.v.z));
    }

    /**
     * Returns an AffineTransform that representes a traslation with vector v
     *
     * @param v The traslation vector
     * @return A newAffineTransform with traslation
     */
    public static AffineJTransform createTranslationTransform(Vec v) {
        AffineJTransform resul = new AffineJTransform();
        resul.setOriginImg(v);
        return resul;
    }

    /**
     * Returns a 2D rotation transform
     *
     * @param center Center of the rotation
     * @param angle Angle (in radians)
     * @return A new AffineTransform with the rotation
     */
    public static AffineJTransform create2DRotationTransform(Point center, double angle) {
        AffineJTransform resul = new AffineJTransform();
        final double sin = Math.sin(angle);
        final double cos = Math.cos(angle);
        resul.setV1Img(cos, sin);
        resul.setV2Img(-sin, cos);

        AffineJTransform tr1 = AffineJTransform.createTranslationTransform(center.v.mult(-1));
        AffineJTransform tr2 = AffineJTransform.createTranslationTransform(center.v);

        return tr1.compose(resul.compose(tr2));
    }

    /**
     * Creates a transform which scales from a given point by a factor
     * uniformly.
     *
     * @param center Scale center
     * @param scale Factor of scale. A value of 1 means no change.
     * @return The transform
     */
    public static AffineJTransform createScaleTransform(Point center, double scale) {
        return createScaleTransform(center, scale, scale, scale);
    }

    /**
     * Overloaded method. Creates a transform,which scales from a given point by
     * a x and y factor uniformly. The z-factor is 1.
     *
     * @param center Scale center
     * @param scalex x-scale factor
     * @param scaley y-scale factor
     * @return The transform
     */
    public static AffineJTransform createScaleTransform(Point center, double scalex, double scaley) {
        return createScaleTransform(center, scalex, scaley, 1);
    }

    /**
     * Overloaded method. Creates a transform,which scales from a given point by
     * a x, y and z factor uniformly.
     *
     * @param center Scale center
     * @param scalex x-scale factor
     * @param scaley y-scale factor
     * @param scalez z-scale factor
     * @return The transform
     */
    public static AffineJTransform createScaleTransform(Point center, double scalex, double scaley, double scalez) {
        AffineJTransform resul = new AffineJTransform();
        resul.setV1Img(scalex, 0, 0);
        resul.setV2Img(0, scaley, 0);
        resul.setV3Img(0, 0, scalez);
        AffineJTransform tr1 = AffineJTransform.createTranslationTransform(center.v.mult(-1));
        AffineJTransform tr2 = AffineJTransform.createTranslationTransform(center.v);
        return tr1.compose(resul.compose(tr2));
    }

    /**
     * Creates a 2D homothecy transform in the plane (a
     * rotation+traslation+uniform scale) which transforms the point A into
     * point C and point B into point D. There are 2 such transforms that
     * accomplish this, inverse and direct. This method returns the direct. To
     * obtain the inverse method, a composition with a reflection of axis B-D
     * should be done.
     *
     * @param A First origin point
     * @param B Second origin point
     * @param C Image of the first origin point
     * @param D Image of the second origin point
     * @param alpha Alpha parameter to animate the transform. 0 means unaltered.
     * 1 means the full transform done.
     * @return The transform
     */
    public static AffineJTransform createDirect2DHomothecy(Point A, Point B, Point C, Point D, double alpha) {
        double angle;//Angle between AB and CD
        Vec v1 = A.to(B);//Vector AB
        Vec v2 = C.to(D);//Vector CD
        Vec v3 = A.to(C);//Vector AC
        double d1 = v1.norm();
        double d2 = v2.norm();
        angle = Math.acos(v1.dot(v2) / d1 / d2);

        //Need to compute also cross-product in order to stablish if clockwise or counterclockwise
        if (v1.x * v2.y - v1.y * v2.x < 0) {
            angle = -angle;
        }
        //The rotation part
        AffineJTransform rotation = AffineJTransform.create2DRotationTransform(A, angle * alpha);

        //The scale part
        AffineJTransform scale = AffineJTransform.createScaleTransform(A, (1 - alpha) + d2 / d1 * alpha);

        //The traslation part
        AffineJTransform traslation = AffineJTransform.createTranslationTransform(v3.mult(alpha));
        return rotation.compose(scale).compose(traslation);
    }

    /**
     * Create a Reflection that transforms A into B
     *
     * @param A Origin point
     * @param B Destiny point
     * @param alpha Alpha parameter. 0 means unaltered, 1 fully reflection done
     * @return The reflection
     */
    public static AffineJTransform createReflection(Point A, Point B, double alpha) {
        Point E1 = new Point(1, 0);
        Point E2 = new Point(-1, 0);
        AffineJTransform canonize = AffineJTransform.createDirect2DHomothecy(A, B, E1, E2, 1);
        AffineJTransform invCanonize = canonize.getInverse();
        //A reflection from (1,0) to (-1,0) has a very simple form
        AffineJTransform canonizedReflection = new AffineJTransform();
        canonizedReflection.setV1Img(E1.interpolate(E2, alpha));
        AffineJTransform resul = canonize.compose(canonizedReflection).compose(invCanonize);
        return resul;
    }

    /**
     * Create a reflexion with axis specified by 2 points.
     *
     * @param E1 First point of simmetry axis
     * @param E2 Second point of simmetry axis
     * @param alpha parameter. 0 means unaltered, 1 fully reflection done
     * @return The reflection
     */
    public static AffineJTransform createReflectionByAxis(Point E1, Point E2, double alpha) {
        AffineJTransform canonize = AffineJTransform.createDirect2DHomothecy(E1, E2, new Point(0, 0), new Point(0, E2.v.norm()), 1);
        AffineJTransform invCanonize = canonize.getInverse();
        //A reflection from (1,0) to (-1,0) has a very simple form
        AffineJTransform canonizedReflection = new AffineJTransform();
        canonizedReflection.setV1Img((1 - alpha) - 1 * alpha, 0, 0);

        AffineJTransform resul = canonize.compose(canonizedReflection).compose(invCanonize);

        return resul;

    }

    /**
     * Interpolate current transform with a given one. The transform is not
     * modified.
     *
     * @param transform Transform to interpolate with
     * @param lambda Interpolation parameter. 0 means this transform and 1 the
     * given one.
     * @return The interpolated transform
     */
    public AffineJTransform interpolate(AffineJTransform transform, double lambda) {
//        AffineTransform resul = new AffineTransform();
        double[] row1_1 = this.matrix.getRow(0);
        double[] row1_2 = transform.matrix.getRow(0);
        double interp11 = (1 - lambda) * row1_1[1] + lambda * row1_2[1];
        double interp12 = (1 - lambda) * row1_1[2] + lambda * row1_2[2];
        double interp13 = (1 - lambda) * row1_1[3] + lambda * row1_2[3];

        transform.setOriginImg(interp11, interp12, interp13);

        double[] row2_1 = this.matrix.getRow(1);
        double[] row2_2 = transform.matrix.getRow(1);
        double interp21 = (1 - lambda) * row2_1[1] + lambda * row2_2[1];
        double interp22 = (1 - lambda) * row2_1[2] + lambda * row2_2[2];
        double interp23 = (1 - lambda) * row2_1[3] + lambda * row2_2[3];

        transform.setV1Img(interp21, interp22, interp23);

        double[] row3_1 = this.matrix.getRow(2);
        double[] row3_2 = transform.matrix.getRow(2);
        double interp31 = (1 - lambda) * row3_1[1] + lambda * row3_2[1];
        double interp32 = (1 - lambda) * row3_1[2] + lambda * row3_2[2];
        double interp33 = (1 - lambda) * row3_1[3] + lambda * row3_2[3];

        transform.setV2Img(interp31, interp32, interp33);

        return transform;

    }

    /**
     * Creates an affine transformation that maps A,B,C into D,E,F
     *
     * @param A First origin point
     * @param B Second origin point
     * @param C Third origin point
     * @param D Image of first origin point
     * @param E Image of second origin point
     * @param F Image of third origin point
     * @param lambda Lambda parameter. 0 means unaltered, 1 fully transform done
     * @return The transform
     */
    public static AffineJTransform createAffineTransformation(Point A, Point B, Point C, Point D, Point E, Point F, double lambda) {
        //First I create a transformation that map O,e1,e2 into A,B,C
        AffineJTransform tr1 = new AffineJTransform();
        tr1.setOriginImg(A.copy());
        tr1.setV1Img(A.to(B));
        tr1.setV2Img(A.to(C));
        tr1 = tr1.getInverse();

        //Now I create a transformation that map O,e1,e2 into D,E,F
        AffineJTransform tr2 = new AffineJTransform();
        tr2.setOriginImg(D.copy());
        tr2.setV1Img(D.to(E));
        tr2.setV2Img(D.to(F));

        //The transformation I am looking for is X-> tr2(tr^-1(X))
        AffineJTransform tr = tr1.compose(tr2);
        AffineJTransform id = new AffineJTransform();
        return id.interpolate(tr, lambda);

    }

    public static AffineJTransform createRotateScaleXYTransformation(Point A, Point B, Point C, Point D, Point E, Point F, double lambda) {
        //First map A,B into (0,0) and (1,0)
        AffineJTransform tr1 = AffineJTransform.createDirect2DHomothecy(A, B, new Point(0, 0), new Point(1, 0), 1);

        //Now I create a transformation that adjust the y-scale, proportionally
        //This transform will be applied inversely too
        AffineJTransform tr2 = new AffineJTransform();
        final double proportionalHeight = (F.to(E).norm() / D.to(E).norm()) / (B.to(C).norm() / B.to(A).norm());
        tr2.setV2Img(0, proportionalHeight * lambda + (1 - lambda) * 1); //Interpolated here

        //Finally, and homothecy to carry A,B into D,E
        AffineJTransform tr3 = AffineJTransform.createDirect2DHomothecy(A, B, D, E, lambda);//Interpolated here
        //The final transformation
        return tr1.compose(tr2).compose(tr1.getInverse()).compose(tr3);
    }

}
