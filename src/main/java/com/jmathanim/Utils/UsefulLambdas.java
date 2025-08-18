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
package com.jmathanim.Utils;

import java.util.function.DoubleUnaryOperator;

/**
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class UsefulLambdas {

    /**
     * A lambda function simulating a single bounce, with predefinied parameters
     *
     * @return The lambda function, ready to use in any animation with the setLambda method
     */
    public static DoubleUnaryOperator bounce1() {
        return bounce1(2.2, 4);
    }

    private static DoubleUnaryOperator bounce1(double a, double b) {
        final double aa = a;
        final double aaRoot = 1 / Math.sqrt(a);
        final double bb = b;
        return (double t) -> {
            if (t < aaRoot) {
                return aa * t * t;
            } else {
                return 1 + bb * (t - aaRoot) * (t - 1);
            }
        };
    }

    /**
     * A lambda function simulating a double bounce, with predefinied parameters
     *
     * @return The lambda function, ready to use in any animation with the setLambda method
     */
    public static DoubleUnaryOperator bounce2() {
        return bounce2(2.5, 8);
    }

    private static DoubleUnaryOperator bounce2(double a, double b) {
        final double aa = a;
        final double aaRoot1 = 1 / Math.sqrt(a);
        final double aaRoot2 = (2 - aaRoot1) * aaRoot1;
        final double bb = b;
        return (double t) -> {
            if (t < aaRoot1) {
                return aa * t * t;
            } else if (t < aaRoot2) {
                return 1 + bb * (t - aaRoot1) * (t - aaRoot2);
            } else {
                return 1 + bb * (t - 1) * (t - aaRoot2);
            }
        };
    }

    /**
     * Back and forth (50%-50%), in a linear way.
     *
     * @return The lambda function
     */
    public static DoubleUnaryOperator backAndForthLinear() {
        return backAndForthLinear(.5d);
    }

    /**
     * Back and forth, in a linear way.
     *
     * @param percentage Percentage of runtime to divide the go and go back paths. A value 0f 0.75 gives 75% of time to
     *                   "go" and 25% to "back".
     * @return The lambda function
     */
    public static DoubleUnaryOperator backAndForthLinear(double percentage) {
        return (double t) -> {
            if (t < percentage) {
                return t / percentage;
            } else {
                return 1 - (t - percentage) / (1 - percentage);
            }
        };
    }

    /**
     * Back and forth, describing a parabola, with maximum at 0.5
     *
     * @return The lambda function
     */
    public static DoubleUnaryOperator backAndForth() {
        return (double t) -> 4 * t * (1 - t);
    }

    /**
     * Parabollical back and forth, with a small bounce at the end
     *
     * @return The lambda function
     */
    public static DoubleUnaryOperator backAndForthBounce1() {
        return backAndForthBounce1(.7, 1d);
    }

    private static DoubleUnaryOperator backAndForthBounce1(double a, double b) {
        final double aa = a;
        final double bb = b * 4 / (a * a);
        return (double t) -> {
            if (t < a) {
                return bb * t * (a - t);
            } else {
                return (a - t) * (t - 1) * bb;
            }
        };
    }

    /**
     * Parabollical back and forth, with 2 smalls bounces at the end
     *
     * @return The lambda function
     */
    public static DoubleUnaryOperator backAndForthBounce2() {
        return backAndForthBounce2(.7, 1d);
    }

    private static DoubleUnaryOperator backAndForthBounce2(double a, double b) {
        final double norm = b * 4 / (a * a);
        double c = a + a * (1 - a);
        return (double t) -> {
            if (t < a) {
                return Math.min(1, norm * t * (a - t));
            }
            if (t < c) {
                return Math.min(1, (a - t) * (t - c) * norm);
            } else {
                return Math.min(1, (c - t) * (t - 1) * norm);
            }
        };
    }

    /**
     * Default smooth operator. This is the default lambda used in animations
     *
     * @return The lambda function
     */
    public static DoubleUnaryOperator smooth() {
        return smooth(.7d);
    }

    /**
     * Lambda smooth operator. The parameter determines the smoothness.
     *
     * @param smoothness Smoothness level. 0 means straigt line, 1 fully smoothed.
     * @return The lambda function
     */
    public static DoubleUnaryOperator smooth(double smoothness) {
        return (double t) -> {
            double h = (t == 0 ? 0 : Math.exp(-1 / t));
            double h2 = (t == 1 ? 0 : Math.exp(-1 / (1 - t)));
            return (1 - smoothness) * t + smoothness * h / (h + h2);

//        return t * t * (3 - 2 * t);
//        return t;
        };
    }

    /**
     * Returns a linear lambda function from a to b. An animation with this lambda will start at a*runtime and end at
     * b*runtime. Mostly used for composing with other lambdas.
     *
     * @param a Percentage of runtime to start
     * @param b Percentage of runtime to end
     * @return The lambda function
     */
    public static DoubleUnaryOperator allocateTo(double a, double b) {
        return (double t) -> {
            if (t < a) {
                return 0;
            }
            if (t > b) {
                return 1;
            }
            return (t - a) / (b - a);
        };
    }

    /**
     * Restrict the animations to start and end at determinate points. For example values of .25 and .5 will play the
     * animation with the same runtime, starting at 25% of animation and ending at 50%.
     *
     * @param a Start time of animation (from 0 to 1)
     * @param b End time of animation (from 0 to 1)
     * @return The lambda function
     */
    public static DoubleUnaryOperator restrictTo(double a, double b) {
        return (double t) -> t * (b - a) + a;
    }

    /**
     * Reverse function. An animation with this lambda will play backwards in time. Mostly used for composing with other
     * lambdas.
     *
     * @return The lambda function
     */
    public static DoubleUnaryOperator reverse() {
        return (double t) -> 1 - t;
    }

    /**
     * Clone function. Repeat this function N times and allocate them consecutively. Each animation will last por
     * runTime/N seconds.
     *
     * @param cloneNumber Number of clones
     * @return The lambda function
     */
    public static DoubleUnaryOperator clone(int cloneNumber) {
        return (double t) -> t * cloneNumber - Math.floor(t * cloneNumber);
    }

    /**
     * Threshold function. Returns 0 for values less than the threshold parameter and 1 for equal or bigger ones. This
     * function may be handy when you want a certain animation to perform immediately at a given time.
     *
     * @param threshold Threshold value
     * @return The lambda function
     */
    public static DoubleUnaryOperator thresHold(double threshold) {
        return (double t) -> t < threshold ? 0 : 1;
    }
}
