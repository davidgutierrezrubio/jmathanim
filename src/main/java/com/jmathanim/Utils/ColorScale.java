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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * A color scale to be used in density plots
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class ColorScale {

    public static ColorScale createDefault(double minValue, double maxValue) {
        ColorScale cs = new ColorScale();
        cs.addMarker(minValue, JMColor.BLUE);
        cs.addMarker(maxValue, JMColor.RED);
        return cs;
    }

    private ArrayList<Double> markers;
    private HashMap<Double, JMColor> colors;

    public ColorScale() {
        markers = new ArrayList<>();
        colors = new HashMap<>();

    }

    public void addMarker(double marker, JMColor color) {
        markers.add(marker);
        colors.put(marker, color);
        Collections.sort(markers);
    }

    public JMColor getColorValue(double x) {
        int n = 0;
        while (markers.get(n) <= x) {
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

        double alpha = (x - a) / (b - a);
        JMColor colB = colors.get(b);
        return colA.getInterpolatedColor(colB, alpha);
    }

    public ArrayList<Double> getMarkers() {
        return markers;
    }

}
