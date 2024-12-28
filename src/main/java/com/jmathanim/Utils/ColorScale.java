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

import com.jmathanim.Styling.JMColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * A color scale to be used in density plots
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class ColorScale {

    /**
     * Creates a default color scale from blue to red
     *
     * @param minValue Minimum value (blue)
     * @param maxValue Maximum value (red)
     * @return The created ColorScale object
     */
    public static ColorScale createDefaultBR(double minValue, double maxValue) {
        ColorScale cs = new ColorScale();
        cs.addMarker(minValue, JMColor.BLUE);
        cs.addMarker(maxValue, JMColor.RED);
        return cs;
    }

    /**
     * Creates a default color scale from black to white
     *
     * @param minValue Minimum value (black)
     * @param maxValue Maximum value (white)
     * @return The created ColorScale object
     */
    public static ColorScale createDefaultBW(double minValue, double maxValue) {
        ColorScale cs = new ColorScale();
        cs.addMarker(minValue, JMColor.BLACK);
        cs.addMarker(maxValue, JMColor.WHITE);
        return cs;
    }

    private final ArrayList<Double> markers;
    private final HashMap<Double, JMColor> colors;

    /**
     * Creates a new, empty ColorScale object
     */
    public ColorScale() {
        markers = new ArrayList<>();
        colors = new HashMap<>();

    }

    /**
     * Add a new marker. The color scale should return the given color at the
     * given parameter
     *
     * @param marker Color
     * @param color Parameter
     */
    public void addMarker(double marker, JMColor color) {
        markers.add(marker);
        colors.put(marker, color);
        Collections.sort(markers);
    }

    /**
     * Returns the computed color at the given parameter
     *
     * @param t Parameter
     * @return The computed color, according to the scale
     */
    public JMColor getColorValue(double t) {
        int n = 0;
        while (markers.get(n) <= t) {
            n++;
            if (n == markers.size()) {
                return colors.get(markers.get(markers.size() - 1));
            }
        }
        if (n == 0) {
            return colors.get(markers.get(0));
        }
        double a = markers.get(n - 1);
        double b = markers.get(n);
        JMColor colA = colors.get(a).copy();

        double alpha = (t - a) / (b - a);
        JMColor colB = colors.get(b);
        return (JMColor) colA.interpolate(colB, alpha);
    }

    public ArrayList<Double> getMarkers() {
        return markers;
    }

    public void copyFrom(ColorScale colorScale) {
        markers.clear();
        for (Double mark : colorScale.markers) {
            double m = mark;
            markers.add(m);
        }
        colors.clear();
        for (Double c : colors.keySet()) {
            double m = c;
            colors.put(m, colors.get(m).copy());
        }
    }

}
