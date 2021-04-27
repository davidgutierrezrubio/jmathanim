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
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class UsefulLambdas {

    /**
     * A lambda function simulating a single bounce, with predefinied parameters
     *
     * @return The lambda function, ready to use in any animation with the
     * setLambda method
     */
    public static DoubleUnaryOperator bounce1() {
        return bounce1(2.2, 4);
    }

    private static DoubleUnaryOperator bounce1(double a, double b) {
        final double aa = a;
        final double aaRoot = 1 / Math.sqrt(a);
        final double bb = b;
        return new DoubleUnaryOperator() {
            @Override
            public double applyAsDouble(double t) {
                if (t < aaRoot) {
                    return aa * t * t;
                } else {
                    return 1 + bb * (t - aaRoot) * (t - 1);
                }
            }
        };
    }

    /**
     * A lambda function simulating a double bounce, with predefinied parameters
     *
     * @return The lambda function, ready to use in any animation with the
     * setLambda method
     */
    public static DoubleUnaryOperator bounce2() {
        return bounce2(2.5, 8);
    }

    private static DoubleUnaryOperator bounce2(double a, double b) {
        final double aa = a;
        final double aaRoot1 = 1 / Math.sqrt(a);
        final double aaRoot2 = (2 - aaRoot1) * aaRoot1;
        final double bb = b;
        return new DoubleUnaryOperator() {
            @Override
            public double applyAsDouble(double t) {
                if (t < aaRoot1) {
                    return aa * t * t;
                } else if (t < aaRoot2) {
                    return 1 + bb * (t - aaRoot1) * (t - aaRoot2);
                } else {
                    return 1 + bb * (t - 1) * (t - aaRoot2);
                }
            }
        };
    }

    public static DoubleUnaryOperator backAndForthBounce1() {
        return backAndForthBounce1(.7, 1d);
    }

    private static DoubleUnaryOperator backAndForthBounce1(double a, double b) {
        final double aa = a;
        final double bb = b * 4 / (a * a);
        return new DoubleUnaryOperator() {
            @Override
            public double applyAsDouble(double t) {
                if (t < a) {
                    return bb * t * (a - t);
                } else {
                    return (a - t) * (t - 1) * bb;
                }
            }
        };
    }

    public static DoubleUnaryOperator backAndForthBounce2() {
        return backAndForthBounce2(.7, 1d);
    }

    private static DoubleUnaryOperator backAndForthBounce2(double a, double b) {
        final double norm = b * 4 / (a * a);
        double c = a + a * (1 - a);
        return new DoubleUnaryOperator() {
            @Override
            public double applyAsDouble(double t) {
                if (t < a) {
                    return norm * t * (a - t);
                }
                if (t < c) {
                    return (a - t) * (t - c) * norm;
                } else {
                    return (c - t) * (t -1) * norm;
                }
            }
        };
    }
// public static DoubleUnaryOperator endingSmooth() {
//     double smooth=.75;
//        return new DoubleUnaryOperator() {
//            @Override
//            public double applyAsDouble(double t) {
//                return (1-smooth)*t+smooth*(1+(t-1)*(1-t));
//            }
//        };
//    }
}
