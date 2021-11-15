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

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Renderers.FXRenderer.JavaFXRenderer;
import com.jmathanim.mathobjects.JMImage;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;

/**
 * Creates a new image pattern
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class JMImagePattern extends PaintStyle {

    JMImage img;

    /**
     * Creates a new image pattern to be used as draw or fill style.
     *
     * @param str File name of image. By default, files will be searched at
     * local resources/images folder unless used the modifier "!" to specify an
     * absolute path.
     */
    public JMImagePattern(String str) {
        super();
        img = new JMImage(str);
    }
  /**
     * Creates a new image pattern to be used as draw or fill style.
     *
     * @param img a JMImage to be used as pattern
     */
    public JMImagePattern(JMImage img) {
        super();
        this.img = img.copy();
    }

    @Override
    public PaintStyle copy() {
        return new JMImagePattern(img);
    }

    @Override
    public Paint getFXPaint(JavaFXRenderer r, Camera cam) {
        return new ImagePattern(img.getImage(), 0, 0, img.getWidth(), img.getHeight(), true);
//        return new ImagePattern(img);
    }

    @Override
    public PaintStyle interpolate(PaintStyle p, double t) {
        return this;//Can't interpolate an image yet
    }

    public JMImagePattern setPatternHeight(double h) {
        img.setHeight(h);
        return this;
    }

    public JMImagePattern setPatternWidth(double w) {
        img.setWidth(w);
        return this;
    }

}
