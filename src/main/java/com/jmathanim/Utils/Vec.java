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
import static com.jmathanim.jmathanim.JMathAnimScene.PI;
import com.jmathanim.mathobjects.Stateable;
import static java.lang.Math.sqrt;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

/**
 * A vector in 3D
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class Vec implements Stateable, HasDirection {

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

    /**
     * Multiplies the vector by a scalar and stores the resul. The original
     * vector is altered and the method returns this object.
     *
     * @param lambda The scalar to multiply
     * @return This vector
     */
    public Vec multInSite(double lambda) {
        x *= lambda;
        y *= lambda;
        z *= lambda;
        return this;
    }

    /**
     * Returns a new vector representing this vector scaled by a factor. The
     * current vector is unaltered.
     *
     * @param lambda The factor
     * @return The new vector
     */
    public Vec mult(double lambda) {
        return this.copy().multInSite(lambda);
    }

    /**
     * Adds the given vector to this and stores the resul. The original vector
     * is altered and the method returns this object.
     *
     * @param b The vector to add
     * @return This vector
     */
    public Vec addInSite(Vec b) {
        x += b.x;
        y += b.y;
        z += b.z;
        return this;
    }

    /**
     * Substracts the given vector to this and stores the resul. The original
     * vector is altered and the method returns this object.
     *
     * @param b The vector to substract
     * @return This vector
     */
    public Vec minusInSite(Vec b) {
        x -= b.x;
        y -= b.y;
        z -= b.z;
        return this;
    }

    /**
     * Substracts the given vector to this and return the result. The original
     * vector is unaltered.
     *
     * @param b The vector to substract
     * @return The substraction result
     */
    public Vec minus(Vec b) {
        return this.copy().minusInSite(b);
    }

    /**
     * Add the given vector to this and return the result. The original vector
     * is unaltered.
     *
     * @param b The vector to add
     * @return The sum result
     */
    public Vec add(Vec b) {
        return this.copy().addInSite(b);
    }

    public double norm() {
        return (double) sqrt(x * x + y * y + z * z);
    }

    /**
     * Returns a new point between this and v2, given by the parameter
     *
     * @param v2 The other point to interpolate
     * @param alpha Parameter of interpolation. 0 gives this point. 1 gives v2.
     * 0.5 returns the middle point. Values less than 0 and greater than 1 are
     * allowed.
     * @return The interpolated point
     */
    public Vec interpolate(Vec v2, double alpha) {
        return new Vec((1 - alpha) * x + alpha * v2.x, (1 - alpha) * y + alpha * v2.y, (1 - alpha) * z + alpha * v2.z);

    }

    /**
     * Returns a copy of the vector
     *
     * @return The copy
     */
    public Vec copy() {
        Vec resul = new Vec(x, y, z);
        return resul;
    }

    /**
     * Copy coordinates from given vector
     *
     * @param v Vector to copy from
     */
    public void copyFrom(Vec v) {
        if (v != null) {
            this.x = v.x;
            this.y = v.y;
            this.z = v.z;
        }
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
        while (angle < 0) {
            angle += 2 * PI;
        }
        while (angle > 2 * PI) {
            angle -= 2 * PI;
        }
        return angle;
    }

    /**
     * Return the angle of the vector, between -PI and PI (2d version)
     *
     * @return The angle
     */
    public double getAngleRightQuad() {
        double angle = Math.atan(this.y / this.x);
        return angle;
    }

    /**
     * Rotates the vector the specified angle, storing the result in the
     * original vector (2d version)
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
     * Rotates the vector the specified angle, and returns the result.The
     * original vector is unaltered (2d version).
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
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.z) ^ (Double.doubleToLongBits(this.z) >>> 32));
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
        return "Vec(" + x + ", " + y + ", "+z+')';
    }

    public static Vec to(double x, double y, double z) {
        return new Vec(x, y, z);
    }

    public static Vec to(double x, double y) {
        return new Vec(x, y);
    }

    /**
     * Return the normalized vector, with modulus 1. If the vector is the null
     * vector, does nothing. The original vector is unaltered.
     *
     * @return The normalized vector if the modulus is positive. The original
     * otherwise.
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
     * Applies an affine transform to the vector. the transformed vector. The
     * original vector is altered.
     *
     * @param tr Affine transform
     * @return This object, with the transform applied
     */
    public Vec applyAffineTransform(AffineJTransform tr) {
        RealMatrix pRow = new Array2DRowRealMatrix(new double[][]{{1d, x, y, z}});
        RealMatrix pNew = pRow.multiply(tr.getMatrix());

        x = pNew.getEntry(0, 1);
        y = pNew.getEntry(0, 2);
        z = pNew.getEntry(0, 3);
        return this;
    }

    /**
     * Scales the vector according to given parameters. The vector is modified.
     *
     * @param scx X scale
     * @param scy Y scale
     * @return This vector
     */
    public Vec scaleInSite(double scx, double scy) {
        this.x *= scx;
        this.y *= scy;
        return this;
    }

    /**
     * Returns a scaled version of the vector. The original vector is not
     * modified
     *
     * @param scx X scale
     * @param scy Y scale
     * @return A copy of the vector, scaled.
     */
    public Vec scale(double scx, double scy) {
        return this.copy().scaleInSite(scx, scy);
    }

}
