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
package com.jmathanim.Animations.Strategies.Transform.Optimizers;

import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.Shape;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class SimpleConnectedPathsOptimizationStrategy implements OptimizePathsStrategy {

    Shape sh1, sh2;

    public SimpleConnectedPathsOptimizationStrategy(Shape sh1, Shape sh2) {
        this.sh1 = sh1;
        this.sh2 = sh2;
    }

    @Override
    public void optimizePaths(Shape shape1, Shape shape2) {
        JMPath pa1c = shape1.getPath().rawCopy();
        JMPath pa2 = shape2.getPath();
        int direction = pa1c.getOrientation() * pa2.getOrientation();

        ArrayList<Double> dists = new ArrayList<>();
        for (int n = 1; n < pa1c.size(); n++) {
            pa1c.cyclePoints(1, direction);
            double d = SumDistancesBetweenPaths(pa1c, pa2);
//            double d = varAnglesBetweenPaths(pa1c, pa2);
//            double d = varAnglesBetweenPaths(pa1c, pa2)+varDistancesBetweenPaths(pa1c, pa2);
//            double d = varDistancesBetweenPaths(pa1c, pa2);
            dists.add(d);
        }
        int cycleMin = dists.indexOf(Collections.min(dists)) + 1;

        shape1.getPath().cyclePoints(cycleMin, direction);
    }

    private double SumDistancesBetweenPaths(JMPath pa1, JMPath pa2) {
        double dist = 0;
        for (int n = 0; n < pa1.size(); n++) {
            dist += pa1.getJMPoint(n).p.to(pa2.getJMPoint(n).p).norm();
        }
        return dist;
    }

    private double varDistancesBetweenPaths(JMPath pa1, JMPath pa2) {
        ArrayList<Double> distances = new ArrayList<>();
        for (int n = 0; n < pa1.size(); n++) {
            double dist = pa1.getJMPoint(n).p.to(pa2.getJMPoint(n).p).norm();
            distances.add(dist);
        }
        return stdDev(distances);
    }

    private double varAnglesBetweenPaths(JMPath pa1, JMPath pa2) {
        ArrayList<Double> angles = new ArrayList<>();
        for (int n = 0; n < pa1.size(); n++) {
            double angle = pa1.getJMPoint(n).p.to(pa2.getJMPoint(n).p).getAngle();
            angles.add(angle);
        }
        return stdDev(angles);
    }

    private double stdDev(ArrayList<Double> values) {
        double var = 0;
        double sum = 0;
        double sumSq = 0;
        for (Double x : values) {
            sum += x;
            sumSq += x * x;
        }
        var = sumSq / values.size() - sum * sum / values.size() / values.size();
        return Math.sqrt(var);
    }

}
