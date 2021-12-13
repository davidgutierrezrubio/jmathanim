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
package com.jmathanim.mathobjects;

import com.jmathanim.Styling.MODrawProperties;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class CanonicalJMPath {

    public final ArrayList<JMPath> paths;

    public static CanonicalJMPath make(MultiShapeObject msho) {
        CanonicalJMPath resul = new CanonicalJMPath();
        for (Shape sh : msho) {
            CanonicalJMPath aa = sh.getPath().canonicalForm();
            resul.paths.addAll(aa.paths);
        }
        return resul;
    }

    public CanonicalJMPath(ArrayList<JMPath> paths) {
        this.paths = paths;
    }

    public MultiShapeObject createMultiShape(MathObject obj) {
        MODrawProperties mpCopy = null;
        MultiShapeObject msh = new MultiShapeObject();
        if (obj != null) {
            mpCopy = obj.getMp().copy();
            msh.getMp().copyFrom(mpCopy);
        }
        for (JMPath p : paths) {
            final Shape shape = new Shape(p.referencedCopy());
            if (obj != null) {
                shape.getMp().copyFrom(mpCopy);
            }
            msh.add(shape);
        }
        if (obj != null) {
            msh.layer(obj.getLayer());

        }
        return msh;
    }

    public void clear() {
        paths.clear();
    }

    public boolean add(JMPath e) {
        return paths.add(e);
    }

    public boolean addAll(Collection<? extends JMPath> c) {
        return paths.addAll(c);
    }

    public boolean addAll(CanonicalJMPath c) {
        return paths.addAll(c.getPaths());
    }

    public CanonicalJMPath() {
        this(new ArrayList<JMPath>());
    }

    public ArrayList<JMPath> getPaths() {
        return paths;
    }

    public int getNumberOfPaths() {
        return paths.size();
    }

    public JMPath get(int index) {
        return paths.get(index);
    }

    public int getTotalNumberOfPoints() {
        int getTotalNumberOfPoints = 0;
        for (JMPath p : paths) {
            getTotalNumberOfPoints += p.size();
        }
        return getTotalNumberOfPoints;
    }

    public int getTotalNumberOfSegments() {
        int getTotalNumberOfSegments = 0;
        for (JMPath p : paths) {
            // In an open path, n points define n-1 segments
            getTotalNumberOfSegments += p.size() - 1;
        }
        return getTotalNumberOfSegments;
    }

    /**
     * Return path number where segment lies
     *
     * @param k Index of the JMPathPoint
     * @return An int[] array containing the path number and the segment number
     * in that path All zero-based.
     */
    public int[] getSegmentLocation(int k) {
        k = k % getTotalNumberOfSegments(); // If k is too big...
        int n = 0;
        while (paths.get(n).size() - 1 <= k) {
            k -= paths.get(n).size() - 1;
            n++;
        }
        return new int[]{n, k};
    }

    /**
     * Generates a subpath from 0 to t, where t=1 returns the whole path Path
     * should be open and connected (canonical form)
     *
     * @param pathNumber Number of the path to compute the subpath
     * @param t
     * @return A referencedCopy of the path (deep referencedCopy, points are not referenced)
     */
    public JMPath subpath(int pathNumber, double t) {
        JMPath resul = paths.get(pathNumber).copy();
        if (t < 1) {

            double a = t * (resul.size() - 1);

            int k = (int) Math.floor(a);
            double alpha = a - k;
            k++;

            if (alpha > 0 && alpha < 1) {
                // Interpolate
                JMPathPoint interp = resul.interpolateBetweenTwoPoints(k, alpha);
                interp.isThisSegmentVisible = true;
            }

            ArrayList<JMPathPoint> subList = new ArrayList<>();
            if (alpha == 0) {
                k--;
            }
            for (int n = 0; n < k + 1; n++) {
                subList.add(resul.jmPathPoints.get(n));
            }
            resul.jmPathPoints.clear();
            resul.jmPathPoints.addAll(subList);
        }
        return resul;

    }

    public JMPath toJMPath() {
        JMPath resul = new JMPath();
        for (JMPath p : paths) {
            resul.addJMPointsFrom(p);
        }
        return resul;
    }

    public int size() {
        return paths.size();
    }

}
