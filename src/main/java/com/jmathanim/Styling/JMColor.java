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
package com.jmathanim.Styling;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Renderers.FXRenderer.JavaFXRenderer;
import com.jmathanim.jmathanim.JMathAnimScene;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import javafx.scene.paint.Paint;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class JMColor extends PaintStyle {

    public static JMColor NONE = new JMColor(0, 0, 0, 0);
    public static JMColor WHITE = new JMColor(1, 1, 1, 1);
    public static JMColor BLACK = new JMColor(0, 0, 0, 1);
    public static JMColor RED = new JMColor(1, 0, 0, 1);
    public static JMColor GREEN = new JMColor(0, 1, 0, 1);
    public static JMColor BLUE = new JMColor(0, 0, 1, 1);
    public static JMColor GRAY = new JMColor(.5, .5, .5, 1);

    public double r, g, b;
    private double alpha;

    /**
     * Creates a new JMColor with the specified red, green, blue, and alha
     * componentes, from 0 to 1.
     *
     * @param r Red component 0-1
     * @param g Green component 0-1
     * @param b Blue component 0-1
     * @param alpha Alpha component 0-1
     */
    public JMColor(double r, double g, double b, double alpha) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.alpha = alpha;
    }

    /**
     * Creates a new JMColor with the specified red, green, blue, and alha
     * componentes, from 0 to 256.
     *
     * @param r Red component 0-255
     * @param g Green component 0-255
     * @param b Blue component 0-255
     * @param alpha Alpha component 0-255
     * @return The new JMcolor
     */
    public static JMColor rgbInt(int r, int g, int b, int alpha) {
        return new JMColor(r * 1.f / 255, g * 1.f / 255, b * 1.f / 255, alpha * 1.f / 255);
    }

    /**
     * Return a {@link java.awt.Color} object representing the color.
     *
     * @return Color
     */
    public java.awt.Color getAwtColor() {
        return new java.awt.Color((float) r, (float) g, (float) b, (float) alpha);
    }

    /**
     * Return a {@link java.awt.Color} object representing the color.
     *
     * @return Color
     */
    public javafx.scene.paint.Color getFXColor() {
        r = (r < 0 ? 0 : r);
        g = (g < 0 ? 0 : g);
        b = (b < 0 ? 0 : b);

        r = (r > 1 ? 1 : r);
        g = (r > 1 ? 1 : g);
        b = (r > 1 ? 1 : b);
        return new javafx.scene.paint.Color((float) r, (float) g, (float) b, (float) alpha);
    }

    /**
     * Return a {@link java.awt.Color} object representing the color. Overloaded
     * method with given alpha.
     *
     * @param alpha Alpha to apply to color
     * @return Color
     */
    public javafx.scene.paint.Color getFXColor(double alpha) {
        return new javafx.scene.paint.Color((float) r, (float) g, (float) b, (float) alpha);
    }

    /**
     * Computes the inverse color
     *
     * @return The inverse color
     */
    public JMColor getInverse() {
        return new JMColor(1 - r, 1 - g, 1 - b, alpha);
    }

    /**
     * Returns a copy of this object
     *
     * @return A raw copy of the JMColor, with identical attributes.
     */
    @Override
    public JMColor copy() {
        return new JMColor(r, g, b, alpha);
    }

    /**
     * Set the RGBA values of those given by the parameter.If the given color is
     * null, nothing is done
     *
     * @param ps PaintStyly to copy from
     */
    @Override
    public final void copyFrom(PaintStyle ps) {
        if (ps == null) {
            return;
        }
        if (ps instanceof JMColor) {
            JMColor jmColor = (JMColor) ps;
            r = jmColor.r;
            g = jmColor.g;
            b = jmColor.b;
            alpha = jmColor.alpha;
        }
        //If PaintStyle is a linear gradient, take the first color

        if (ps instanceof JMLinearGradient) {
            JMLinearGradient jMLinearGradient = (JMLinearGradient) ps;
            HashMap<Double, JMColor> stops = jMLinearGradient.getStops().getColorHashMap();
            if (stops.isEmpty()) {
                return;
            }
            Iterator<JMColor> iterator = stops.values().iterator();
            this.copyFrom(iterator.next());
        }
        //The same for radial gradients
        if (ps instanceof JMRadialGradient) {
            JMRadialGradient jMRadialGradient = (JMRadialGradient) ps;
            HashMap<Double, JMColor> stops = jMRadialGradient.getStops().getColorHashMap();
            if (stops.isEmpty()) {
                return;
            }
            Iterator<JMColor> iterator = stops.values().iterator();
            this.copyFrom(iterator.next());
        }
        //TODO: For a image pattern, should compute the average color...
    }

    /**
     * Interpolates this JMColor with another PaintStyle. The original color is
     * unaltered
     *
     * @param p PaintStyle to interpolate
     * @param t Interpolation parameter. 0 means this object, 1 means the given
     * color
     * @return A JMcolor with components interpolated
     */
    @Override
    public PaintStyle interpolate(PaintStyle p, double t) {
        t = (t < 0 ? 0 : t);
        t = (t > 1 ? 1 : t);
        if (p instanceof JMColor) {
            JMColor B = (JMColor) p;

            double rr = (1 - t) * r + t * B.r;
            double gg = (1 - t) * g + t * B.g;
            double bb = (1 - t) * b + t * B.b;
            double aa = (1 - t) * alpha + t * B.alpha;
            return new JMColor(rr, gg, bb, aa);
        }
        if (p instanceof JMLinearGradient) {
            return p.interpolate(this, 1 - t);
        }
        if (p instanceof JMRadialGradient) {
            return p.interpolate(this, 1 - t);
        }
        return this.copy();//I don't know what to do here, so I return the same.
    }

    // Static methods
    /**
     * Returns a new JMColor with random r,g,b components. Alpha component is 1.
     *
     * @return The new JMColor
     */
    public static JMColor random() {
        return new JMColor(Math.random(), Math.random(), Math.random(), 1);
    }

    /**
     * Parse a string with color information and returns the JMColor associated.
     * If the string begins with "#" parses hexadecimal numbers in 3, 6 or 8
     * digits. If the string equals one of the defined JavaFX color names
     * ("white", "blue", etc.), returns this color. The names are
     * case-insensitive.
     *
     * @param str The string with the hex digits or the color name
     * @return A new JMColor with given parameters.
     */
    public static JMColor parse(String str) {
        javafx.scene.paint.Color col = javafx.scene.paint.Color.WHITE;// Default color
        str = str.toUpperCase().trim();
        JMColor colrgb = extractRGBValues(str);
        if (colrgb != null) {//String is format "rgb(r,g,b) decimals or RGB(R,G,B) integers"
            return colrgb;
        }

        if ("NONE".equals(str)) {
            return new JMColor(0, 0, 0, 0);
        }
        if ("RANDOM".equals(str)) {
            return JMColor.random();
        }
        if (str.startsWith("#"))// Hex
        {
            col = javafx.scene.paint.Color.valueOf(str);
        } else {
            try {
                Field field = javafx.scene.paint.Color.class.getField(str.toUpperCase());
                col = (javafx.scene.paint.Color) field.get(JMColor.class);
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                JMathAnimScene.logger.warn("Color {} not recognized ", str);
            }
        }
        return JMColor.fromFXColor(col);
    }

    /**
     * Parse SVG strings rgb(R,G,B) and returns the generated color
     *
     * @param input A String with format rgb(R,G,B)
     * @return The color. If the String has no valid format, returns null
     */
    private static JMColor extractRGBValues(String input) {
//        // Verifica si la cadena comienza con "rgb(" y termina con ")"
        if (input.startsWith("RGB(") && input.endsWith(")")) {
            // Elimina los caracteres "rgb(" al principio y ")" al final
            String valuesString = input.substring(4, input.length() - 1);

            // Divide la cadena en partes utilizando la coma como separador
            String[] valuesArray = valuesString.split(",");

            try {
                // Convierte las partes a números enteros
                int red = Integer.parseInt(valuesArray[0].trim());
                int green = Integer.parseInt(valuesArray[1].trim());
                int blue = Integer.parseInt(valuesArray[2].trim());
                int alpha;
                if (valuesArray.length == 4) {
                    alpha = Integer.parseInt(valuesArray[3].trim());
                } else {
                    alpha = 255;
                }

                return JMColor.rgbInt(red, green, blue, alpha);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                //Try to parse double values
                try {
                    // Convierte las partes a números enteros
                    float red = Float.parseFloat(valuesArray[0].trim());
                    float green = Float.parseFloat(valuesArray[1].trim());
                    float blue = Float.parseFloat(valuesArray[2].trim());
                    float alpha;
                    if (valuesArray.length == 4) {
                        alpha = Float.parseFloat(valuesArray[3].trim());
                    } else {
                        alpha = 1;
                    }
                    return new JMColor(red, green, blue, alpha);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e2) {
                    return null;
                }
            }
        }
        return null;
    }

    public static JMColor fromFXColor(javafx.scene.paint.Color col) {
        JMColor resul = new JMColor(1, 1, 1, 1);
        resul.r = col.getRed();
        resul.g = col.getGreen();
        resul.b = col.getBlue();
        resul.alpha = col.getOpacity();
        return resul;
    }

    @Override
    public String toString() {
        return "JMcolor(" + r + ", " + g + "," + b + ", " + alpha + ')';
    }

    @Override
    public Paint getFXPaint(JavaFXRenderer r, Camera cam) {
        return getFXColor();
    }

    @Override
    public double getAlpha() {
        return alpha;
    }

    @Override
    public JMColor setAlpha(double alpha) {
        this.alpha = alpha;
        return this;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.r) ^ (Double.doubleToLongBits(this.r) >>> 32));
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.g) ^ (Double.doubleToLongBits(this.g) >>> 32));
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.b) ^ (Double.doubleToLongBits(this.b) >>> 32));
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.alpha) ^ (Double.doubleToLongBits(this.alpha) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final JMColor other = (JMColor) obj;
        if (Double.doubleToLongBits(this.r) != Double.doubleToLongBits(other.r)) {
            return false;
        }
        if (Double.doubleToLongBits(this.g) != Double.doubleToLongBits(other.g)) {
            return false;
        }
        if (Double.doubleToLongBits(this.b) != Double.doubleToLongBits(other.b)) {
            return false;
        }
        return Double.doubleToLongBits(this.alpha) == Double.doubleToLongBits(other.alpha);
    }

}
