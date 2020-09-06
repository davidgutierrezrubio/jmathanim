/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations;

import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.JMPathMathObject;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.Point;
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
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class AffineTransform {

    public RealMatrix A;

    public AffineTransform() {
        this(MatrixUtils.createRealIdentityMatrix(4));
    }

    public AffineTransform(RealMatrix A) {
        this.A = A;
    }

    public void setOriginImg(Point p) {
        setOriginImg(p.v);
    }

    public void setOriginImg(Vec v) {
        setOriginImg(v.x, v.y, v.z);
    }

    public void setOriginImg(int x, int y, int z) {
        setOriginImg((double) x, (double) y, (double) z);
    }

    public void setOriginImg(double x, double y) {
        A.setRow(0, new double[]{1, x, y, 0});
    }

    public void setOriginImg(double x, double y, double z) {
        A.setRow(0, new double[]{1, x, y, z});
    }

    public void setV1Img(Point p) {
        setV1Img(p.v);
    }

    public void setV1Img(Vec v) {
        setV1Img(v.x, v.y, v.z);
    }

    public void setV1Img(double x, double y, double z) {
        A.setRow(1, new double[]{0, x, y, z});
    }

    public void setV1Img(double x, double y) {
        setV1Img(x, y, 0);
    }

    public void setV2Img(Point p) {
        setV2Img(p.v);
    }

    public void setV2Img(Vec v) {
        setV2Img(v.x, v.y, v.z);
    }

    public void setV2Img(double x, double y, double z) {
        A.setRow(2, new double[]{0, x, y, z});
    }

    public void setV2Img(double x, double y) {
        setV2Img(x, y, 0);
    }

    public void setV3Img(Point p) {
        setV3Img(p.v);
    }

    public void setV3Img(Vec v) {
        setV3Img(v.x, v.y, v.z);
    }

    public void setV3Img(double x, double y, double z) {
        A.setRow(3, new double[]{0, x, y, z});
    }

    public void setV3Img(double x, double y) {
        setV3Img(x, y, 0);
    }
 public JMPathMathObject applyTransform(JMPathMathObject obj) {
     JMPathMathObject resul=obj.copy();
     int size=obj.jmpath.size();
     for (int n=0;n<size;n++)
     {
         JMPathPoint jmPDst = resul.getPoint(n);
         JMPathPoint pSrc = obj.getPoint(n);
         Point pDst = applyTransform(pSrc.p);
         Point cp1Dst = applyTransform(pSrc.cp1);
         Point cp2Dst = applyTransform(pSrc.cp2);
         
         jmPDst.p.v.x=pDst.v.x;
         jmPDst.p.v.y=pDst.v.y;
         jmPDst.p.v.z=pDst.v.z;
         
         jmPDst.cp1.v.x=cp1Dst.v.x;
         jmPDst.cp1.v.y=cp1Dst.v.y;
         jmPDst.cp1.v.z=cp1Dst.v.z;
         
         jmPDst.cp2.v.x=cp2Dst.v.x;
         jmPDst.cp2.v.y=cp2Dst.v.y;
         jmPDst.cp2.v.z=cp2Dst.v.z;
     }
     return resul;
     
 }
    
    public Point applyTransform(Point p) {
        RealMatrix pRow = new Array2DRowRealMatrix(new double[][]{{1d, p.v.x, p.v.y, p.v.z}});
        RealMatrix pNew = pRow.multiply(A);

        double xx = pNew.getEntry(0, 1);
        double yy = pNew.getEntry(0, 2);
        return new Point(xx, yy);

    }

    /**
     * Compose another Affine Transform and returns the new AffineTransform if
     * C=A.compose(B) The resulting transform C,applied to a point, will result
     * in applying first A and then B
     *
     * @param tr The AffintTransform to compose with
     * @return The composed AffineTransform
     */
    public AffineTransform compose(AffineTransform tr) {
        return new AffineTransform(A.multiply(tr.A));
    }

    /**
     * Gets the inverse transform
     *
     * @return The inverse transform
     */
    public AffineTransform getInverse() {
        RealMatrix B = new LUDecomposition(A).getSolver().getInverse();
        return new AffineTransform(B);
    }

    /**
     * Overloaded method. Creates an AffineTransform that moves a into b
     *
     * @param a Origin
     * @param b Destiny
     * @return A newAffineTransform with traslation
     */
    public static AffineTransform createTranslationTransform(Point a, Point b) {
        return createTranslationTransform(new Vec(b.v.x - a.v.x, b.v.y - a.v.y, b.v.z - a.v.z));
    }

    /**
     * Returns an AffineTransform that representes a traslation with vector v
     *
     * @param v The traslation vector
     * @return A newAffineTransform with traslation
     */
    public static AffineTransform createTranslationTransform(Vec v) {
        AffineTransform resul = new AffineTransform();
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
    public static AffineTransform create2DRotationTransform(Point center, double angle) {
        AffineTransform resul = new AffineTransform();
        final double sin = Math.sin(angle);
        final double cos = Math.cos(angle);
        resul.setV1Img(cos, sin);
        resul.setV2Img(-sin, cos);

        AffineTransform tr1 = AffineTransform.createTranslationTransform(center.v.mult(-1));
        AffineTransform tr2 = AffineTransform.createTranslationTransform(center.v);

        return tr1.compose(resul.compose(tr2));
    }

    public static AffineTransform create2DScaleTransform(Point center, double scale) {
        return create2DScaleTransform(center, scale, scale, scale);
    }

    public static AffineTransform create2DScaleTransform(Point center, double scalex, double scaley) {
        return create2DScaleTransform(center, scalex, scaley, 1);
    }

    public static AffineTransform create2DScaleTransform(Point center, double scalex, double scaley, double scalez) {
        AffineTransform resul = new AffineTransform();
        resul.setV1Img(scalex, 0, 0);
        resul.setV2Img(0, scaley, 0);
        resul.setV3Img(0, 0, scalez);
        AffineTransform tr1 = AffineTransform.createTranslationTransform(center.v.mult(-1));
        AffineTransform tr2 = AffineTransform.createTranslationTransform(center.v);

        return tr1.compose(resul.compose(tr2));
    }

}
