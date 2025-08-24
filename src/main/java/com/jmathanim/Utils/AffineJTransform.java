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

import com.jmathanim.mathobjects.Coordinates;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Stateable;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;
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
public class AffineJTransform implements Stateable {

    /**
     * Matrix that stores the transform, with the following form: {{1, x, y, z},
     * {0, vx, vy, vz},{0, wx, wy, wz},{0 tx ty tz}} Where x,y,z is the image of
     * (0,0,0) and v,w,t are the images of canonical vectors.
     */
    public RealMatrix matrix;
    public RealMatrix matrixBackup;

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

    public RealMatrix getMatrix() {
        return matrix;
    }

    public void setMatrix(RealMatrix matrix) {
        this.matrix = matrix;
    }

    public AffineJTransform copy() {
        RealMatrix copyMatrix = this.matrix.copy();
        return new AffineJTransform(copyMatrix);
    }

    /**
     * Sets the imagen of the origin by this transform In practice, it changes
     * the first row of the matrix transform
     *
     * @param coords Destiny point of origin (0,0,0), given by a vector
     */
    public void setOriginImg(Coordinates coords) {
        Vec v=coords.getVec();
        setOriginImg(v.x, v.y, v.z);
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
     * @param coords Destiny vector of (1,0,0)
     */
    public void setV1Img(Coordinates coords) {
        Vec v=coords.getVec();
        setV1Img(v.x, v.y, v.z);
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
     * @param coords Destiny vector of (0,1,0)
     */
    public void setV2Img(Coordinates coords) {
        Vec v = coords.getVec();
        setV2Img(v.x, v.y, v.z);
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
     * @param coords Destiny vector of (0,0,1)
     */
    public void setV3Img(Coordinates coords) {
        Vec v = coords.getVec();
        setV3Img(v.x, v.y, v.z);
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
        mObject.applyAffineTransform(this);
    }

    public void applyTransformsToDrawingProperties(MathObject mObject) {
        // Determinant of the A_xy=2D-submatrix, to compute change in thickness
        // As Area changes in det(A_xy), we change thickness in the root square of
        // det(A_xy)
        if (!mObject.getMp().isAbsoluteThickness()) {
            double det = matrix.getEntry(1, 1) * matrix.getEntry(2, 2) - matrix.getEntry(2, 1) * matrix.getEntry(1, 2);
            final double sqrtDet = Math.sqrt(Math.abs(det));
            mObject.getMp().multThickness(Math.sqrt(sqrtDet));
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

        T resul = (T)obj.copy();
        applyTransform(resul);
        return resul;
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
    public static AffineJTransform createTranslationTransform(Coordinates a, Coordinates b) {
        return createTranslationTransform(a.to(b));
    }

    /**
     * Returns an AffineTransform that representes a traslation with vector v
     *
     * @param v The traslation vector
     * @return A newAffineTransform with traslation
     */
    public static AffineJTransform createTranslationTransform(Coordinates v) {
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
    public static AffineJTransform create2DRotationTransform(Coordinates center, double angle) {
        AffineJTransform resul = new AffineJTransform();
        final double sin = Math.sin(angle);
        final double cos = Math.cos(angle);
        resul.setV1Img(cos, sin);
        resul.setV2Img(-sin, cos);

        AffineJTransform tr1 = AffineJTransform.createTranslationTransform(center.getVec().mult(-1));
        AffineJTransform tr2 = AffineJTransform.createTranslationTransform(center.getVec());

        return tr1.compose(resul.compose(tr2));
    }

    /**
     * Creates the 3d rotation given the center and angles.
     *
     * @param center Center of the rotation
     * @param anglex Rotation angle around the x axis
     * @param angley Rotation angle around the y axis
     * @param anglez Rotation angle around the z axis
     * @param alpha Alpha interpolation parameter from 0 to 1 (0=no rotation,
     * 1=full rotation)
     * @return A new AffineTransform with the rotation
     */
    public static AffineJTransform create3DRotationTransform(Coordinates center, double anglex, double angley, double anglez, double alpha) {
        AffineJTransform resul = new AffineJTransform();
        final double sinz = Math.sin(alpha * anglez);
        final double cosz = Math.cos(alpha * anglez);

        final double siny = Math.sin(alpha * angley);
        final double cosy = Math.cos(alpha * angley);

        final double sinx = Math.sin(alpha * anglex);
        final double cosx = Math.cos(alpha * anglex);
        resul.setV1Img(cosz * cosy, cosz * siny * sinx - sinz * cosx, cosz * siny * cosx + sinz * sinx);
        resul.setV2Img(sinz * cosy, sinz * siny * sinx + cosz * cosx, sinz * siny * cosx - cosz * sinx);
        resul.setV3Img(-siny, cosy * sinx, cosy * cosx);
        if (center != null) {
            AffineJTransform tr1 = AffineJTransform.createTranslationTransform(center.getVec().mult(-1));
            AffineJTransform tr2 = AffineJTransform.createTranslationTransform(center.getVec());
            return tr1.compose(resul.compose(tr2));
        } else {
            return resul;
        }
    }

    /**
     * Creates the 3d rotation that maps the vectors AB1,AB2 into CD1,CD2. This
     * transformation does not map A into C, only performs the rotation. If you
     * want to perform the full transformation, use isometric3D instead.
     *
     * @param A First point of the origin axes
     * @param B1 Second point of the origin axes
     * @param B2 Third point of the origin axes
     * @param C First point of the destiny axes
     * @param D1 Second point of the destiny axes
     * @param D2 Third point of the destiny axes
     * @param alpha Alpha interpolation parameter from 0 to 1 (0=no rotation,
     * 1=full rotation)
     * @return A new AffineTransform with the rotation
     */
    public static AffineJTransform create3DRotationTransform(
            Coordinates A, Coordinates B1, Coordinates B2, Coordinates C, Coordinates D1, Coordinates D2, double alpha) {
        Vec v1 = A.to(B1).normalize();
        Vec v2 = v1.cross(A.to(B2)).normalize();
        Vec v3 = v1.cross(v2).normalize();

        Vec w1 = C.to(D1).normalize();
        Vec w2 = w1.cross(C.to(D2)).normalize();
        Vec w3 = w1.cross(w2).normalize();
        double[][] ma = EulerAnglesCalculator.calculateRotationMatrix(v1, v2, v3, w1, w2, w3);
        Rotation ro = new Rotation(ma, .000001);
        double[] angles = ro.getAngles(RotationOrder.XYZ, RotationConvention.VECTOR_OPERATOR);
        Rotation ro2 = new Rotation(RotationOrder.XYZ, RotationConvention.VECTOR_OPERATOR, alpha * angles[0], alpha * angles[1], alpha * angles[2]);
        double[][] ma2 = ro2.getMatrix();
        AffineJTransform tr = new AffineJTransform();
        tr.setV1Img(ma2[0][0], ma2[1][0], ma2[2][0]);
        tr.setV2Img(ma2[0][1], ma2[1][1], ma2[2][1]);
        tr.setV3Img(ma2[0][2], ma2[1][2], ma2[2][2]);
        return tr;
    }

    /**
     * Creates a transform which scales from a given point by a factor
     * uniformly.
     *
     * @param center Scale center
     * @param scale Factor of scale. A value of 1 means no change.
     * @return The transform
     */
    public static AffineJTransform createScaleTransform(Coordinates center, double scale) {
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
    public static AffineJTransform createScaleTransform(Coordinates center, double scalex, double scaley) {
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
    public static AffineJTransform createScaleTransform(Coordinates center, double scalex, double scaley, double scalez) {
        AffineJTransform resul = new AffineJTransform();
        resul.setV1Img(scalex, 0, 0);
        resul.setV2Img(0, scaley, 0);
        resul.setV3Img(0, 0, scalez);
        AffineJTransform tr1 = AffineJTransform.createTranslationTransform(center.getVec().mult(-1));
        AffineJTransform tr2 = AffineJTransform.createTranslationTransform(center.getVec());
        return tr1.compose(resul.compose(tr2));
    }

    /**
     * Creates a 2D isomorphic transform in the plane (a
     * rotation+traslation+uniform scale) which transforms the point originA into
     * point destinyA and originB into destinyB. There are 2 such transforms that
     * accomplish this, inverse and direct. This method returns the direct.
     *
     * @param originA First origin point
     * @param originB Second origin point
     * @param destinyA Image of the first origin point
     * @param destinyB Image of the second origin point
     * @param alpha Alpha interpolation parameter from 0 to 1 (0=no transform,
     * 1=full transform)
     * @return The transform
     */
    public static AffineJTransform createDirect2DIsomorphic(Coordinates originA, Coordinates originB, Coordinates destinyA, Coordinates destinyB, double alpha) {
        double angle;// Angle between AB and CD
        Vec v1 = originA.getVec().to(originB.getVec());// Vector AB
        Vec v2 = destinyA.getVec().to(destinyB.getVec());// Vector CD
        Vec v3 = originA.getVec().to(destinyA.getVec());// Vector AC
        double d1 = v1.norm();
        double d2 = v2.norm();
        double dotProd = v1.dot(v2) / d1 / d2;
        //In some cases, a smaaaaaaall round error can give numbers greater than 1
        //making angle=NaN, so we have to be sure dotProd stays between -1 and 1
        dotProd = (dotProd > 1 ? 1 : dotProd);
        dotProd = (dotProd < -1 ? -1 : dotProd);
        angle = Math.acos(dotProd);

        // Need to compute also cross-product in order to stablish if clockwise or
        // counterclockwise
        if (v1.x * v2.y - v1.y * v2.x < 0) {
            angle = -angle;
        }
        // The rotation part
        AffineJTransform rotation = AffineJTransform.create2DRotationTransform(originA.getVec(), angle * alpha);

        // The scale part
        AffineJTransform scale = AffineJTransform.createScaleTransform(originA, (1 - alpha) + d2 / d1 * alpha);

        // The traslation part
        AffineJTransform traslation = AffineJTransform.createTranslationTransform(v3.mult(alpha));
        return rotation.compose(scale).compose(traslation);
    }

    /**
     * Creates the only direct 3d isomorphic transform that maps A into C, B1
     * into D1 and plane AB1B2 into CD1D2. Objects are scaled uniformly with relation
     * AD1/AB1.
     *
     * @param A First origin point
     * @param B1 Second origin point
     * @param B2 Third point to determine origin plane
     * @param C First destiny point
     * @param D1 Second destiny point
     * @param D2 Third point to determine destiny plane
     * @param alpha Alpha interpolation parameter from 0 to 1 (0=no transform,
     * 1=full transform)
     * @return The created transform
     */
    public static AffineJTransform createDirect3DIsomorphic(Coordinates A, Coordinates B1,
                                                            Coordinates B2, Coordinates C,
                                                            Coordinates D1, Coordinates D2,
                                                            double alpha) {
        Vec vShift = A.to(C);
        double d = C.to(D1).norm() / A.to(B1).norm();
        // The rotation part
        //Compute the alpha, beta, gamma angles

        //Traslation origin axes to (0,0,0)...
        AffineJTransform shift1=AffineJTransform.createTranslationTransform(A.getVec().mult(-1));
        //And the inverse one
        AffineJTransform shift2=AffineJTransform.createTranslationTransform(A);
        AffineJTransform rotation = AffineJTransform.create3DRotationTransform(A, B1, B2, C, D1, D2, alpha);

        // The scale part
        AffineJTransform scale = AffineJTransform.createScaleTransform(A, (1 - alpha) + d * alpha);

        // The traslation part
        AffineJTransform traslation = AffineJTransform.createTranslationTransform(vShift.mult(alpha));
        //So, move to (0,0,0), perform the rotation and scale, move back to A and the traslation to C
        return shift1.compose(rotation).compose(scale).compose(shift2).compose(traslation);
    }

    /**
     * Creates a inverse 2D isomorphic transform in the plane (a
     * rotation+traslation+uniform scale) which transforms the point originA into
     * destinyC and originB into destinyD. There are 2 such transforms that
     * accomplish this, inverse and direct. This method returns the inverse.
     *
     * @param originA First origin point
     * @param originB Second origin point
     * @param destinyA Image of the first origin point
     * @param destinyB Image of the second origin point
     * @param alpha Alpha parameter to animate the transform. 0 means unaltered.
     * 1 means the full transform done.
     * @return The transform
     */
    public static AffineJTransform createInverse2DIsomorphic(Coordinates originA, Coordinates originB,
                                                             Coordinates destinyA, Coordinates destinyB,
                                                             double alpha) {
        double angle;// Angle between AB and CD
        Vec v1 = originA.to(originB);// Vector AB
        Vec v2 = destinyB.to(destinyA);// Vector CD
        Vec v3 = originA.to(destinyB);// Vector AC
        double d1 = v1.norm();
        double d2 = v2.norm();
        double dotProd = v1.dot(v2) / d1 / d2;
        //In some cases, a smaaaaaaall round error can give numbers greater than 1
        //making angle=NaN, so we have to be sure dotProd stays between -1 and 1
        dotProd = (dotProd > 1 ? 1 : dotProd);
        dotProd = (dotProd < -1 ? -1 : dotProd);
        angle = Math.acos(dotProd);

        // Need to compute also cross-product in order to establish if it is clockwise or
        // counterclockwise
        if (v1.x * v2.y - v1.y * v2.x < 0) {
            angle = -angle;
        }
        // The rotation part
        AffineJTransform rotation = AffineJTransform.create2DRotationTransform(originA, angle * alpha);
        double scaleFactorY = (1 - alpha) + d2 / d1 * alpha;
        double scaleFactorX = (1 - alpha) + d2 / d1 * alpha;

        // The scale part
        AffineJTransform scale = AffineJTransform.createScaleTransform(originA, scaleFactorX, scaleFactorY);
        AffineJTransform reflection = AffineJTransform.createReflection(originA, originB, alpha);

        // The translation part
        AffineJTransform traslation = AffineJTransform.createTranslationTransform(v3.mult(alpha));
        return reflection.compose(rotation).compose(scale).compose(traslation);
    }

    /**
     * Create a Reflection that transforms A into B
     *
     * @param A Origin point
     * @param B Destiny point
     * @param alpha Alpha parameter. 0 means unaltered, 1 fully reflection done
     * @return The reflection
     */
    public static AffineJTransform createReflection(Coordinates A, Coordinates B, double alpha) {
        Vec E1 = Vec.to(1, 0);
        Vec E2 = Vec.to(-1, 0);
        AffineJTransform canonize = AffineJTransform.createDirect2DIsomorphic(A, B, E1, E2, 1);
        AffineJTransform invCanonize = canonize.getInverse();
        // A reflection from (1,0) to (-1,0) has a very simple form
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
    public static AffineJTransform createReflectionByAxis(Coordinates E1, Coordinates E2, double alpha) {
        AffineJTransform canonize = AffineJTransform.createDirect2DIsomorphic(E1, E2, Vec.to(0, 0),
                Vec.to(0, E2.getVec().norm()), 1);
        AffineJTransform invCanonize = canonize.getInverse();
        // A reflection from (1,0) to (-1,0) has a very simple form
        AffineJTransform canonizedReflection = new AffineJTransform();
        canonizedReflection.setV1Img((1 - alpha) - 1 * alpha, 0, 0);

        AffineJTransform resul = canonize.compose(canonizedReflection).compose(invCanonize);

        return resul;

    }
//
//    public static AffineJTransform create3DRotationTransform(Vec v1, Vec v2, double alpha) {
//        Vec a = v1.normalize();
//        Vec b = v2.normalize();
//        Vec v = a.cross(b);
//
//        if (v.norm() < .00001) {
//            return new AffineJTransform();//Identity
//        } else {
//            v = v.normalize();
//        }
//
//        double dot = a.dot(b);
//        double theta = alpha * Math.acos(dot);
//        double cosTheta = Math.cos(theta);
//        double sinTheta = Math.sin(theta);
//
//        AffineJTransform resul = new AffineJTransform();
//        resul.setV1Img(
//                cosTheta + v.x * v.x * (1 - cosTheta),
//                v.y * v.x * (1 - cosTheta) + v.z * sinTheta,
//                v.z * v.x * (1 - cosTheta) - v.y * sinTheta
//        );
//
//        resul.setV2Img(
//                v.x * v.y * (1 - cosTheta) - v.z * sinTheta,
//                cosTheta + v.y * v.y * (1 - cosTheta),
//                v.z * v.y * (1 - cosTheta) + v.x * sinTheta
//        );
//
//        resul.setV3Img(
//                v.x * v.z * (1 - cosTheta) + v.y * sinTheta,
//                v.y * v.z * (1 - cosTheta) - v.x * sinTheta,
//                cosTheta + v.z * v.z * (1 - cosTheta)
//        );
//        return resul;
//    }

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
    public static AffineJTransform createAffineTransformation(Coordinates A, Coordinates B, Coordinates C, Coordinates D, Coordinates E, Coordinates F,
            double lambda) {

        Vec a=A.getVec();
        Vec b=B.getVec();
        Vec c=C.getVec();
        Vec d=D.getVec();
        Vec e=E.getVec();
        Vec f=F.getVec();

        // First I create a transformation that map O,e1,e2 into A,B,C
        AffineJTransform tr1 = new AffineJTransform();
        tr1.setOriginImg(a);
        tr1.setV1Img(a.to(b));
        tr1.setV2Img(a.to(c));
        tr1 = tr1.getInverse();

        // Now I create a transformation that map O,e1,e2 into D,E,F
        AffineJTransform tr2 = new AffineJTransform();
        tr2.setOriginImg(d);
        tr2.setV1Img(d.to(e));
        tr2.setV2Img(d.to(f));

        // The transformation I am looking for is X-> tr2(tr^-1(X))
        AffineJTransform tr = tr1.compose(tr2);
        AffineJTransform id = new AffineJTransform();
        return id.interpolate(tr, lambda);

    }

    /**
     * Overloaded method. Returns the affine transform that maps the rectangle
     * r1 onto r2
     *
     * @param r1 Origin rectangle
     * @param r2 Destiny rectangle
     * @param lambda Lambda parameter. 0 means unaltered, 1 fully transform done
     * @return The transform
     */
    public static AffineJTransform createAffineTransformation(Rect r1, Rect r2, double lambda) {
        Vec A1 = r1.getDL();
        Vec A2 = r2.getDL();
        Vec B1 = r1.getDR();
        Vec B2 = r2.getDR();
        Vec C1 = r1.getUL();
        Vec C2 = r2.getUL();
        return createAffineTransformation(A1, B1, C1, A2, B2, C2, lambda);
    }

    public static AffineJTransform createRotateScaleXYTransformation(Coordinates A, Coordinates B, Coordinates C, Coordinates D, Coordinates E,
            Coordinates F, double lambda) {
        // First map A,B into (0,0) and (1,0)
        AffineJTransform tr1 = AffineJTransform.createDirect2DIsomorphic(A, B, Vec.to(0, 0), Vec.to(1, 0), 1);

        // Now I create a transformation that adjust the y-scale, proportionally
        // This transform will be applied inversely too
        AffineJTransform tr2 = new AffineJTransform();
        final double proportionalHeight = (F.to(E).norm() / D.to(E).norm()) / (B.to(C).norm() / B.to(A).norm());
        tr2.setV2Img(0, proportionalHeight * lambda + (1 - lambda) * 1); // Interpolated here

        // Finally, and isomorphism to carry A,B into D,E
        AffineJTransform tr3 = AffineJTransform.createDirect2DIsomorphic(A, B, D, E, lambda);// Interpolated here
        // The final transformation
        return tr1.compose(tr2).compose(tr1.getInverse()).compose(tr3);
    }

    public void copyFrom(AffineJTransform resul) {
        this.setMatrix(resul.getMatrix().copy());
    }

    @Override
    public void saveState() {
        matrixBackup = matrix.copy();

    }

    @Override
    public void restoreState() {
        matrix = matrixBackup;
    }

}

class EulerAnglesCalculator {

    public static double[][] calculateRotationMatrix(Vec A, Vec B1, Vec B2,
            Vec C, Vec D1, Vec D2) {
        // Matriz M1 con vectores A, B1, B2
        double[][] M1 = {
            {A.x, B1.x, B2.x},
            {A.y, B1.y, B2.y},
            {A.z, B1.z, B2.z}
        };

        // Matriz M2 con vectores C, D1, D2
        double[][] M2 = {
            {C.x, D1.x, D2.x},
            {C.y, D1.y, D2.y},
            {C.z, D1.z, D2.z}
        };

        // Calcular la matriz de rotación R = M2 * M1^T
        return multiplyMatrices(M2, transposeMatrix(M1));
    }

    public static double[][] transposeMatrix(double[][] matrix) {
        double[][] transpose = new double[matrix[0].length][matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                transpose[j][i] = matrix[i][j];
            }
        }
        return transpose;
    }

    public static double[][] multiplyMatrices(double[][] matrix1, double[][] matrix2) {
        double[][] result = new double[matrix1.length][matrix2[0].length];
        for (int i = 0; i < matrix1.length; i++) {
            for (int j = 0; j < matrix2[0].length; j++) {
                for (int k = 0; k < matrix1[0].length; k++) {
                    result[i][j] += matrix1[i][k] * matrix2[k][j];
                }
            }
        }
        return result;
    }

    public static double[] calculateEulerAngles(double[][] R) {
        double alpha, beta, gamma;

        // Calcular el ángulo β (rotación alrededor del eje Y)
        beta = Math.asin(-R[2][0]);

        // Comprobación de gimbal lock
        if (Math.abs(Math.cos(beta)) > 1e-6) {
            // Si no hay gimbal lock, calcular α y γ
            alpha = Math.atan2(R[2][1], R[2][2]);
            gamma = Math.atan2(R[1][0], R[0][0]);
        } else {
            // En caso de gimbal lock
            alpha = 0.0;
            if (R[2][0] == -1) {
                beta = Math.PI / 2;
                gamma = Math.atan2(R[0][1], R[0][2]);
            } else {
                beta = -Math.PI / 2;
                gamma = -Math.atan2(R[0][1], R[0][2]);
            }
        }

        return new double[]{alpha, beta, gamma};
    }

    public static double[] calculateEulerAnglesOld(double[][] R) {
        double alpha, beta, gamma;

        // Calcular el ángulo β (rotación alrededor del eje Y)
        beta = Math.asin(-R[2][0]);

        // Comprobación de gimbal lock
        if (Math.abs(Math.cos(beta)) > 1e-6) {
            // Si no hay gimbal lock, calcular α y γ
            alpha = Math.atan2(R[2][1], R[2][2]);
            gamma = Math.atan2(R[1][0], R[0][0]);
        } else {
            // En caso de gimbal lock
            alpha = 0.0;
            if (R[2][0] == -1) {
                beta = Math.PI / 2;
                gamma = Math.atan2(R[0][1], R[0][2]);
            } else {
                beta = -Math.PI / 2;
                gamma = -Math.atan2(R[0][1], R[0][2]);
            }
        }

        return new double[]{alpha, beta, gamma};
    }
}
