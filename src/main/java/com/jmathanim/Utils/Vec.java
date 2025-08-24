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

import com.jmathanim.Constructible.Lines.HasDirection;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.AffineTransformable;
import com.jmathanim.mathobjects.Coordinates;
import com.jmathanim.mathobjects.Interpolable;
import com.jmathanim.mathobjects.Stateable;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import static com.jmathanim.jmathanim.JMathAnimScene.PI2;

/**
 * A vector in 3D
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class Vec implements Stateable, HasDirection, Coordinates<Vec>, AffineTransformable<Vec>, Interpolable<Vec> {

    public double x, y, z;
    public double xState, yState, zState;

    /**
     * Returns a new Vec with the given coordinates
     *
     * @param x x coordinate
     * @param y y coordinate
     */
    public Vec(double x, double y) {
        this(x, y, 0);
    }

    /**
     * Returns a new Vec with the given coordinates, 3D version
     *
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     */
    public Vec(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;

    }

    public static Vec to(double x, double y, double z) {
        return new Vec(x, y, z);
    }

    public static Vec to(double x, double y) {
        return new Vec(x, y);
    }


    /**
     * Static builder.Creates and returns a new point at random coordinates, inside the math view.
     *
     * @return The created point
     */
    public static Vec random() {
        Rect r = JMathAnimConfig.getConfig().getCamera().getMathView();
        double x = r.xmin + (r.xmax - r.xmin) * Math.random();
        double y = r.ymin + (r.ymax - r.ymin) * Math.random();
        return new Vec(x, y);
    }

    /**
     * Computes the dot product of this vector and a given one
     *
     * @param b The other vector to compute the dot product
     * @return The result
     */
    public double dot(Vec b) {
        return x * b.x + y * b.y + z * b.z;
    }

    /**
     * Computes the cross product of this vector and a given one
     *
     * @param b The other vector to compute the cross product
     * @return A new vector with the result.
     */
    public Vec cross(Vec b) {
        return new Vec(this.y * b.z - this.z * b.y, this.z * b.x - this.x * b.z, this.x * b.y - this.y * b.x);
    }


    @Override
    public Vec mult(double lambda) {
        return this.copy().multInSite(lambda);
    }


    public Vec minus(Coordinates<?> v2) {

        return (Vec) this.copy().minusInSite(v2);
    }


    @Override
    public Vec add(Coordinates<?> v2) {
        return (Vec) this.copy().addInSite(v2);
    }

    /**
     * Returns a new point between this and v2, given by the parameter
     *
     * @param coords2 The other point to interpolate
     * @param alpha   Parameter of interpolation. 0 gives this point. 1 gives v2. 0.5 returns the middle point. Values
     *                less than 0 and greater than 1 are allowed.
     * @return The interpolated point
     */
    @Override
    public Vec interpolate(Coordinates<?> coords2, double alpha) {
        Vec v2 = coords2.getVec();
        return new Vec((1 - alpha) * x + alpha * v2.x, (1 - alpha) * y + alpha * v2.y, (1 - alpha) * z + alpha * v2.z);
    }

    /**
     * Returns a copy of the vector
     *
     * @return The copy
     */
    public Vec copy() {
        return new Vec(x, y, z);
    }


    @Override
    public void saveState() {
        xState = x;
        yState = y;
        zState = z;
    }

    @Override
    public void restoreState() {
        x = xState;
        y = yState;
        z = zState;

    }

    /**
     * Return the angle of the vector, between 0 and 2*PI (2d version)
     *
     * @return The angle
     */
    public double getAngle() {
        double angle = Math.atan2(this.y, this.x);
        angle %= PI2;
        if (angle < 0) angle += PI2;
        return angle;
    }

    /**
     * Return the angle of the vector, between -PI and PI (2d version)
     *
     * @return The angle
     */
    public double getAngleRightQuad() {
        return Math.atan(this.y / this.x);
    }

    /**
     * Rotates the vector the specified angle, storing the result in the original vector (2d version)
     *
     * @param angle Rotation angle
     * @return This vector
     */
    public Vec rotateInSite(double angle) {
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        double a = this.x;
        double b = this.y;
        this.x = c * a - s * b;
        this.y = s * a + c * b;
        return this;
    }

    /**
     * Rotates the coordinates given by the vector the specified angle around the given center, storing the result in
     * the original vector (2d version)
     *
     * @param center Rotation center
     * @param angle  Rotation angle
     * @return This vector
     */
    public Vec rotate(Coordinates<?> center, double angle) {
        Vec vCenter = center.getVec();
        Vec rotatedVector = Vec.to(x - vCenter.x, y - vCenter.y);
        rotatedVector.rotateInSite(angle);
        return rotatedVector.addInSite(vCenter);
    }


    /**
     * Rotates the vector the specified angle, and returns the result.The original vector is unaltered (2d version).
     *
     * @param angle Rotation angle
     * @return A new vector with the resul
     */
    public Vec rotate(double angle) {
        return this.copy().rotateInSite(angle);
    }


    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Long.hashCode(Double.doubleToLongBits(this.x));
        hash = 29 * hash + Long.hashCode(Double.doubleToLongBits(this.y));
        hash = 29 * hash + Long.hashCode(Double.doubleToLongBits(this.z));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Vec other = (Vec) obj;
        if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
            return false;
        }
        if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y)) {
            return false;
        }
        return Double.doubleToLongBits(this.z) == Double.doubleToLongBits(other.z);
    }

    @Override
    public String toString() {
        return String.format("Vec(%.2f, %.2f, %.2f)", x, y, z);
    }

    /**
     * Return the normalized vector, with modulus 1. If the vector is the null vector, does nothing. The original vector
     * is unaltered.
     *
     * @return The normalized vector if the modulus is positive. The original otherwise.
     */
    public Vec normalize() {
        double norm = this.norm();
        if (norm > 0) {
            return this.mult(1d / norm);
        } else {
            return this;
        }
    }

    /**
     * Checkif any of its components is Nan
     *
     * @return True if x, y or z is NaN. False otherwise
     */
    public boolean isNaN() {
        return (Double.isNaN(x)) || (Double.isNaN(y)) || Double.isNaN(z);
    }

    @Override
    public Vec getDirection() {
        return this;
    }

    /**
     * Applies an affine transform to the vector. the transformed vector. The original vector is altered.
     *
     * @param tr Affine transform
     * @return This object, with the transform applied
     */
    @Override
    public Vec applyAffineTransform(AffineJTransform tr) {
        RealMatrix pRow = new Array2DRowRealMatrix(new double[][]{{1d, x, y, z}});
        RealMatrix pNew = pRow.multiply(tr.getMatrix());

        x = pNew.getEntry(0, 1);
        y = pNew.getEntry(0, 2);
        z = pNew.getEntry(0, 3);
        return this;
    }




    public boolean isEquivalentTo(Vec v2, double epsilon) {
        boolean resul = (Math.abs(x - v2.x) <= epsilon) & (Math.abs(y - v2.y) <= epsilon) & (Math.abs(z - v2.z) <= epsilon);
        return resul;
    }

    @Override
    public Vec getVec() {
        return this;
    }


    @Override
    public Rect getBoundingBox() {
        return new Rect(x, y, x, y);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int getUpdateLevel() {
        return 0;
    }

    @Override
    public void setUpdateLevel(int level) {

    }

    @Override
    public void update(JMathAnimScene scene) {

    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {

    }

    @Override
    public void unregisterUpdateableHook(JMathAnimScene scene) {
    }


}
