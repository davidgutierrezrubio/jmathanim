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

import com.jmathanim.jmathanim.JMathAnimScene;

import javafx.scene.paint.Paint;

import java.lang.reflect.Field;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class JMColor implements PaintStyle {

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
//
//    //https://stackoverflow.com/questions/4129666/how-to-convert-hex-to-rgb-using-java
//    private static JMColor hex(String hex) {
//        int rr, gg, bb, aa;
//        hex = hex.replace("#", "");
//        switch (hex.length()) {
//            case 6:
//                rr = Integer.valueOf(hex.substring(0, 2), 16);
//                gg = Integer.valueOf(hex.substring(2, 4), 16);
//                bb = Integer.valueOf(hex.substring(4, 6), 16);
//                return JMColor.rgbInt(rr, gg, bb, 255);
//            case 8:
//                rr = Integer.valueOf(hex.substring(0, 2), 16);
//                gg = Integer.valueOf(hex.substring(2, 4), 16);
//                bb = Integer.valueOf(hex.substring(4, 6), 16);
//                aa = Integer.valueOf(hex.substring(6, 8), 16);
//                return JMColor.rgbInt(rr, gg, bb, aa);
//            case 3: //Case https://www.quackit.com/css/color/values/css_hex_color_notation_3_digits.cfm
//                rr = Integer.valueOf(hex.substring(0, 1) + hex.substring(0, 1), 16);
//                gg = Integer.valueOf(hex.substring(1, 2) + hex.substring(1, 2), 16);
//                bb = Integer.valueOf(hex.substring(2, 3) + hex.substring(2, 3), 16);
//                return JMColor.rgbInt(rr, gg, bb, 255);
//            default:
//                JMathAnimScene.logger.warn("Color {} not recognized ", hex);
//                return null;
//        }
//    }

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
    public JMColor copy() {
        return new JMColor(r, g, b, alpha);
    }

    /**
     * Set the RGBA values of those given by the parameter. If the given color
     * is null, nothing is done
     *
     * @param jmcolor The JMColor to copy values from
     */
    public final void copyFrom(JMColor jmcolor) {
        if (jmcolor != null) {
            r = jmcolor.r;
            g = jmcolor.g;
            b = jmcolor.b;
            alpha = jmcolor.alpha;
        }
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
    public JMColor interpolate(PaintStyle p, double t) {
        if (p instanceof JMColor) {
            JMColor B = (JMColor) p;

            double rr = (1 - t) * r + t * B.r;
            double gg = (1 - t) * g + t * B.g;
            double bb = (1 - t) * b + t * B.b;
            double aa = (1 - t) * alpha + t * B.alpha;
            return new JMColor(rr, gg, bb, aa);
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
     * digits. If the string equals one of the defined names ("white", "blue",
     * etc.), returns this color. The names are case-insensitive.
     *
     * @param str The string with the hex digits or the color name
     * @return A new JMColor with given parameters.
     */
    public static JMColor parse(String str) {
        javafx.scene.paint.Color col = javafx.scene.paint.Color.WHITE;// Default color
        str = str.toUpperCase().trim();
        if ("NONE".equals(str)) {
            return new JMColor(0, 0, 0, 0);
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
    public Paint getFXPaint() {
        return getFXColor();
    }

    @Override
    public double getAlpha() {
        return alpha;
    }

    @Override
    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

}
