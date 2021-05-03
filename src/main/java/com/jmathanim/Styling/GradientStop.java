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

import java.util.HashMap;
import javafx.scene.paint.Stop;

/**
 * Holds a bunch of colors to define a linear or radial gradient
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class GradientStop {

    private final HashMap<Double, JMColor> colors;

    public GradientStop() {
        this(new HashMap<>());
    }

    public GradientStop(HashMap<Double, JMColor> colors) {
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
     * Converts the current color marks for appropiate use with the JavaFX
     * library. If there are no marks, it generates a basic white-to-black
     * gradient
     *
     * @return An array of JavaFX Stop objects
     */
    public Stop[] toFXStop() {
        if (colors.isEmpty()) {//Generate a basic white-black gradient
            add(0, JMColor.WHITE);
            add(1, JMColor.BLACK);
        }
        Stop[] resul = new Stop[colors.size()];
        int k = 0;
        for (Double t : colors.keySet()) {
            resul[k] = new Stop(t, colors.get(t).getFXColor());
            k++;
        }
        return resul;
    }

    public HashMap<Double, JMColor> getColorHashMap() {
        return colors;
    }
    
}
