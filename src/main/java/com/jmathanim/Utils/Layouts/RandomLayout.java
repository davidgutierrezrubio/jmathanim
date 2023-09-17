/*
 * Copyright (C) 2023 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jmathanim.Utils.Layouts;

import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MathObjectGroup;
import com.jmathanim.mathobjects.Point;
import java.util.Random;

/**
 * A Layout that puts the elements of the group in random positions around a
 * point
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class RandomLayout extends GroupLayout {

    private double deviation;
    private Distribution distribution;
    private Point center;
    Random random;

    public enum Distribution {
        /**
         * Uniform distribution with 0 mean
         */
        UNIFORM,
        /**
         * Normal, Gaussian distribution with 0 mean
         */
        NORMAL
    };

    /**
     * Returns a new RandomLayout object with no center, Normal distribution and
     * given deviation
     *
     * @param deviation Deviation (Standard Deviation) to generate random
     * positions
     * @return The object created
     */
    public static RandomLayout make(double deviation) {
        return new RandomLayout(null, Distribution.NORMAL, deviation);
    }

    /**
     * Returns a new RandomLayout object with no center, and given distribution
     * and deviation
     *
     * @param distribution Distribution to use. Currently, UNIFORM and GAUSSIAN
     * are implemented.
     * @param deviation Deviation (Standard Deviation) to generate random
     * positions
     * @return The object created
     */
    public static RandomLayout make(Distribution distribution, double deviation) {
        return new RandomLayout(null, distribution, deviation);
    }

    /**
     * Default constructor
     *
     * @param center Center of layout. If null, group objects are located around
     * the current center of the MathObjectGroup
     * @param distribution Distribution to use. Currently, UNIFORM and GAUSSIAN
     * are implemented.
     * @param deviation Deviation (Standard Deviation) to generate random
     * positions
     */
    public RandomLayout(Point center, Distribution distribution, double deviation) {
        this.deviation = deviation;
        this.distribution = distribution;
        random = new Random();
        this.center = center;
    }

    /**
     * Generates a random vector with the parameters (distribution and
     * deviation) given at the class. This method is used to generate the random
     * locations.
     *
     * @return A Vec object with the coordinates of the random vector
     */
    public Vec getRandomVector() {
        switch (distribution) {
            case UNIFORM:
                return Vec.to((random.nextDouble() - .5) * 2 * deviation, (random.nextDouble() - .5) * 2 * deviation);
            case NORMAL:
                return Vec.to(random.nextGaussian(0, deviation), random.nextGaussian(0, deviation));
            default:
                return Vec.to(0, 0);
        }

    }

    @Override
    public void applyLayout(MathObjectGroup group) {
        Vec v;
        if (center != null) {
            v = center.v;
        } else {
            v = group.getCenter().v;
        }

        for (MathObject obj : group) {
            obj.moveTo(Point.at(v.add(getRandomVector())));
        }

    }

    @Override
    public RandomLayout copy() {
        return new RandomLayout(center.copy(), distribution, deviation);
    }

    /**
     * Returns the standard deviation
     *
     * @return The deviation
     */
    public double getDeviation() {
        return deviation;
    }

    /**
     * Sets the standard deviation
     *
     * @param deviation The deviation
     * @return This object
     */
    public RandomLayout setDeviation(double deviation) {
        this.deviation = deviation;
        return this;
    }

    /**
     * Returns the used distribution
     *
     * @return The distribution. A value of enum Distribution
     */
    public Distribution getDistribution() {
        return distribution;
    }

    /**
     * Sets the distribution to use to compute the random vectors
     *
     * @param distribution The distribution. A value of enum Distribution
     * @return This object
     */
    public RandomLayout setDistribution(Distribution distribution) {
        this.distribution = distribution;
        return this;
    }

    /**
     * Returns the center of the layout. Objects will be located at this point
     * plus a random vector. If this object is null, center of the
     * MathObjectGroup will be used.
     *
     * @return The center of the layout or null
     */
    public Point getLayoutCenter() {
        return center;
    }

    /**
     * Sets the center of the layout. Objects will be located at this point plus
     * a random vector. If this object is null, center of the MathObjectGroup
     * will be used.
     *
     * @param center The center of the layout or null
     * @return This object
     */
    public RandomLayout setLayoutCenter(Point center) {
        this.center = center;
        return this;
    }

}
