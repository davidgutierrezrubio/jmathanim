/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Utils.MathObjectDrawingProperties;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class CanonicalJMPath {

    public final ArrayList<JMPath> paths;

    public CanonicalJMPath(ArrayList<JMPath> paths) {
        this.paths = paths;
    }

    public MultiShapeObject createMultiShape(MathObject obj) {
        MathObjectDrawingProperties mpCopy = obj.mp.copy();
        MultiShapeObject msh = new MultiShapeObject();
        for (JMPath p : paths) {
            final Shape shape = new Shape(p.copy(), mpCopy);
            msh.addShape(shape);
        }
        msh.layer(obj.getLayer());
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
            //In an open path, n points define n-1 segments
            getTotalNumberOfSegments += p.size() - 1;
        }
        return getTotalNumberOfSegments;
    }

    /**
     * Return path number where segment lies
     *
     * @param k Index of the JMPathPoint
     * @return An int[] array containing the path number and the segment number in that path
     * All zero-based.
     */
    public int[] getSegmentLocation(int k) {
        k = k % getTotalNumberOfSegments(); //If k is too big...
        int n = 0;
        while (paths.get(n).size()-1 <= k) {
            k -= paths.get(n).size()-1;
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
     * @return A copy of the path (deep copy, points are not referenced)
     */
    public JMPath subpath(int pathNumber, double t) {
        JMPath resul = paths.get(pathNumber).rawCopy();
        if (t < 1) {

            double a = t * (resul.size() - 1);

            int k = (int) Math.floor(a);
            double alpha = a - k;
            k++;

            if (alpha > 0 && alpha < 1) {
                //Interpolate
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
 public JMPath toJMPath()
 {
        JMPath resul = new JMPath();
        for (JMPath p : paths) {
            resul.addPointsFrom(p);
        }
        return resul;
 }
}
