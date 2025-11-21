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

import com.jmathanim.MathObjects.JMImage;
import com.jmathanim.jmathanim.JMathAnimScene;

import java.util.Objects;

/**
 * Creates a new image pattern
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class JMImagePattern extends PaintStyle<JMImagePattern> {

    private JMImage img;

    public JMImagePattern(String str) {
        super();
            img=JMImage.make(str);
    }

    
    public JMImagePattern(JMImage img) {
        super();
        this.img = img.copy();
    }

    @Override
    public JMImagePattern copy() {
        return new JMImagePattern(img);
    }

    @Override
    public void copyFrom(PaintStyle A) {
        if (A instanceof JMImagePattern) {
            JMImagePattern jmip = (JMImagePattern) A;
            this.img=jmip.img.copy();
        }
    }


    @Override
    public JMImagePattern interpolate(PaintStyle<?> p, double t) {
        JMathAnimScene.logger.warn("Image interpolation still not implemented, returning original object");
        return this.copy();//Can't interpolate an image yet
    }

    public JMImagePattern  setPatternHeight(double h) {
        img.setHeight(h);
        return this;
    }

    public JMImagePattern setPatternWidth(double w) {
        img.setWidth(w);
        return this;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.img);
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
        final JMImagePattern other = (JMImagePattern) obj;
        return Objects.equals(this.img, other.img);
    }

    public JMImage getImage() {
        return img;
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
