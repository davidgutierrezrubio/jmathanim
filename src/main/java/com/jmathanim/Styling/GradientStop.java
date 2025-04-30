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
package com.jmathanim.Styling;

import javafx.scene.paint.Stop;

import java.util.TreeMap;

/**
 * Holds a bunch of colors to define a linear or radial gradient
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class GradientStop {

    private final TreeMap<Double, JMColor> colors;
    

    public GradientStop() {
        this(new TreeMap<>());
    }

    public GradientStop(TreeMap<Double, JMColor> colors) {
        this.colors = colors;
    }

    public <T extends GradientStop> T add(double t, JMColor col) {
        colors.put(t, col);
        return (T) this;
    }

    public <T extends GradientStop> T remove(double t) {
        colors.remove(t);
        return (T) this;
    }

    /**
     * Converts the current color marks for appropriate use with the JavaFX
     * library. If there are no marks, it generates a basic white-to-black
     * gradient
     *
     * @return An array of JavaFX Stop objects
     */
    public Stop[] toFXStop(double alpha) {
        if (colors.isEmpty()) {//Generate a basic white-black gradient
            add(0, JMColor.WHITE);
            add(1, JMColor.BLACK);
        }
        Stop[] resul = new Stop[colors.size()];
        int k = 0;
        for (Double t : colors.keySet()) {
            resul[k] = new Stop(t, colors.get(t).getFXColor(alpha));
            k++;
        }
        return resul;
    }

    /**
     * Add the properly interpolated color at given time.
     * Mostly used when interpolating 2 gradients that need to align stop marks before.
     * @param t
     */
    protected void addInterpolatedColor(double t) {
        if (colors.containsKey(t)) return;
        //Get the lower and upper value
        double upper=1;
        double lower=0;
        for (double tt:colors.keySet()) {
            if ((tt<t)&&(lower<tt)) {//Find a greater lower level
                lower=tt;
            }
             if ((t<tt)&&(tt<upper)) {//Find a greater lower level
                upper=tt;
            }
        }
        JMColor colA=colors.get(lower);
        JMColor colB=colors.get(upper);
        JMColor newColor = (JMColor)colA.interpolate(colB, (t-lower)/(upper-lower));
        colors.put(t, newColor);
    }
    
    
    
    public TreeMap<Double, JMColor> getColorTreeMap() {
        return colors;
    }
    
    public GradientStop copy() {
        GradientStop copy=new GradientStop();
        for (double t: colors.keySet()) {
            copy.add(t,colors.get(t).copy());
        }
        
        return copy;
    }

    public int size() {
        return colors.size();
    }

}
