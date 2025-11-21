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

import com.jmathanim.Utils.ColorParser;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.jmathanim.LogUtils;

import java.util.Iterator;
import java.util.TreeMap;

/**
 * JMColor is a class representing a color with red (r), green (g), blue (b),
 * and alpha (transparency) components. Components are expressed as values
 * between 0 and 1. It provides methods for managing colors, including creation,
 * conversion, interpolation, inversion, and parsing from string formats.
 */
public class JMColor extends PaintStyle<JMColor> {

    private double red;
    private double green;
    private double blue;
    private double alpha;


    protected JMColor(double red, double green, double blue, double alpha) {
        this.red =Math.max(Math.min(red, 1), 0);
        this.green =Math.max(Math.min(green, 1), 0);
        this.blue =Math.max(Math.min(blue, 1), 0);
        this.alpha = alpha;
    }

    /**
     * Creates a new JMColor with the specified red, green, blue, and alpha
     * components, from 0 to 1.
     *
     * @param red Red component 0-1
     * @param green Green component 0-1
     * @param blue Blue component 0-1
     * @param alpha Alpha component 0-1
     */
    public static JMColor rgba(double red, double green, double blue, double alpha) {
        return new JMColor(red, green, blue, alpha);
    }


    /**
     * Creates a new JMColor with the specified red, green, blue, and alpha
     * components, represented by integers from 0 to 255.
     *
     * @param r Red component 0-255
     * @param g Green component 0-255
     * @param b Blue component 0-255
     * @param alpha Alpha component 0-255
     * @return The new JMcolor
     */
    public static JMColor rgbaInt(int r, int g, int b, int alpha) {
        return new JMColor(r * 1.f / 255, g * 1.f / 255, b * 1.f / 255, alpha * 1.f / 255);
    }

    /**
     * Computes the inverse color
     *
     * @return The inverse color
     */
    public JMColor getInverse() {
        return new JMColor(1 - getRed(), 1 - getGreen(), 1 - getBlue(), alpha);
    }

    /**
     * Returns a copy of this object
     *
     * @return A raw copy of the JMColor, with identical attributes.
     */
    @Override
    public JMColor copy() {
        return new JMColor(getRed(), getGreen(), getBlue(), alpha);
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
            setRed(jmColor.getRed());
            setGreen(jmColor.getGreen());
            setBlue(jmColor.getBlue());
            alpha = jmColor.alpha;
        }
        //If PaintStyle is a linear gradient, take the first color

        if (ps instanceof JMLinearGradient) {
            JMLinearGradient jMLinearGradient = (JMLinearGradient) ps;
            TreeMap<Double, JMColor> stops = jMLinearGradient.getStops().getColorTreeMap();
            if (stops.isEmpty()) {
                return;
            }
            Iterator<JMColor> iterator = stops.values().iterator();
            this.copyFrom(iterator.next());
        }
        //The same for radial gradients
        if (ps instanceof JMRadialGradient) {
            JMRadialGradient jMRadialGradient = (JMRadialGradient) ps;
            TreeMap<Double, JMColor> stops = jMRadialGradient.getStops().getColorTreeMap();
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
    public JMColor interpolate(PaintStyle<?> p, double t) {
        t = (t < 0 ? 0 : t);
        t = (t > 1 ? 1 : t);
        if (p instanceof JMColor) {
            JMColor B = (JMColor) p;

            double rr = (1 - t) * getRed() + t * B.getRed();
            double gg = (1 - t) * getGreen() + t * B.getGreen();
            double bb = (1 - t) * getBlue() + t * B.getBlue();
            double aa = (1 - t) * alpha + t * B.alpha;
            return new JMColor(rr, gg, bb, aa);
        }
//        if (p instanceof JMLinearGradient) {
//            return p.interpolate(this, 1 - t);
//        }
//        if (p instanceof JMRadialGradient) {
//            return p.interpolate(this, 1 - t);
//        }
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
        str = str.toLowerCase().trim();
        JMColor colrgb = ColorParser.parseRGBorRGBA(str);
        if (colrgb != null) {//String is format "rgb(r,g,b) decimals or RGB(R,G,B) integers"
            return colrgb;
        }
        JMColor colHsl = ColorParser.parseHSL(str);
        if (colHsl != null) {//String is format "rgb(r,g,b) decimals or RGB(R,G,B) integers"
            return colHsl;
        }

        if (("none".equals(str))||("transparent".equals(str))) {
            return new JMColor(0, 0, 0, 0);
        }
        if ("random".equals(str)) {
            return JMColor.random();
        }
        if (str.startsWith("#"))// Hex
        {
//            col = javafx.scene.paint.Color.valueOf(str);
            return ColorParser.parseHexColor(str);
        } else {
//            try {
//                Field field = javafx.scene.paint.Color.class.getField(str.toUpperCase());
//                col = (javafx.scene.paint.Color) field.get(JMColor.class);
//            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
//                JMathAnimScene.logger.warn("Color "+ LogUtils.method(str)+" is not recognized ");
//            }
            if (ColorParser.COLOR_NAMES.containsKey(str)) {
                Integer[] rgb = ColorParser.COLOR_NAMES.get(str);
                return JMColor.rgbaInt(rgb[0], rgb[1], rgb[2],255);
            }

        }
        JMathAnimScene.logger.warn("Color " + LogUtils.method(str) + " is not recognized. Returning "+LogUtils.method("WHITE")+" instead.");
        return JMColor.rgba(1,1,1,1);
    }

//    /**
//     * Parse SVG strings rgb(R,G,B) and returns the generated color
//     *
//     * @param input A String with format rgb(R,G,B)
//     * @return The color. If the String has no valid format, returns null
//     */
//    private static JMColor extractRGBValues(String input) {
////        // Verifica si la cadena comienza con "rgb(" y termina con ")"
//        if (input.startsWith("RGB(") && input.endsWith(")")) {
//            // Elimina los caracteres "rgb(" al principio y ")" al final
//            String valuesString = input.substring(4, input.length() - 1);
//
//            // Divide la cadena en partes utilizando la coma como separador
//            String[] valuesArray = valuesString.split(",");
//
//            try {
//                // Convierte las partes a números enteros
//                int red = Integer.parseInt(valuesArray[0].trim());
//                int green = Integer.parseInt(valuesArray[1].trim());
//                int blue = Integer.parseInt(valuesArray[2].trim());
//                int alpha;
//                if (valuesArray.length == 4) {
//                    alpha = Integer.parseInt(valuesArray[3].trim());
//                } else {
//                    alpha = 255;
//                }
//
//                return JMColor.rgbaInt(red, green, blue, alpha);
//            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
//                //Try to parse double values
//                try {
//                    // Convierte las partes a números enteros
//                    float red = Float.parseFloat(valuesArray[0].trim());
//                    float green = Float.parseFloat(valuesArray[1].trim());
//                    float blue = Float.parseFloat(valuesArray[2].trim());
//                    float alpha;
//                    if (valuesArray.length == 4) {
//                        alpha = Float.parseFloat(valuesArray[3].trim());
//                    } else {
//                        alpha = 1;
//                    }
//                    return new JMColor(red, green, blue, alpha);
//                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e2) {
//                    return null;
//                }
//            }
//        }
//        return null;
//    }

    public static JMColor fromFXColor(javafx.scene.paint.Color col) {
        JMColor resul = new JMColor(1, 1, 1, 1);
        resul.setRed(col.getRed());
        resul.setGreen(col.getGreen());
        resul.setBlue(col.getBlue());
        resul.alpha = col.getOpacity();
        return resul;
    }

    @Override
    public String toString() {
        return "JMcolor(" + getRed() + ", " + getGreen() + "," + getBlue() + ", " + alpha + ')';
    }

    @Override
    public double getAlpha() {
        return alpha;
    }

    @Override
    public JMColor setAlpha(double alpha) {
        this.alpha = Math.max(Math.min(alpha, 1), 0);
        return this;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Long.hashCode(Double.doubleToLongBits(this.getRed()));
        hash = 53 * hash + Long.hashCode(Double.doubleToLongBits(this.getGreen()));
        hash = 53 * hash + Long.hashCode(Double.doubleToLongBits(this.getBlue()));
        hash = 53 * hash + Long.hashCode(Double.doubleToLongBits(this.alpha));
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
        if (Double.doubleToLongBits(this.getRed()) != Double.doubleToLongBits(other.getRed())) {
            return false;
        }
        if (Double.doubleToLongBits(this.getGreen()) != Double.doubleToLongBits(other.getGreen())) {
            return false;
        }
        if (Double.doubleToLongBits(this.getBlue()) != Double.doubleToLongBits(other.getBlue())) {
            return false;
        }
        return Double.doubleToLongBits(this.alpha) == Double.doubleToLongBits(other.alpha);
    }

    public double getRed() {
        return red;
    }

    public void setRed(double red) {
        this.red = Math.max(Math.min(red, 1), 0);
    }

    public double getGreen() {
        return green;
    }

    public void setGreen(double green) {
        this.green = Math.max(Math.min(green, 1), 0);
    }

    public double getBlue() {
        return blue;
    }

    public void setBlue(double blue) {
        this.blue =Math.max(Math.min(blue, 1), 0);
        changeVersionAndMarkDirty();
    }

    @Override
    public void performMathObjectUpdateActions() {

    }

    @Override
    public void performUpdateBoundingBox() {

    }

    @Override
    protected boolean applyUpdaters(boolean previousToObjectUpdate){
        return false;
    }
}
