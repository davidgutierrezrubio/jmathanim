/*
 * Copyright (C) 2023 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jmathanim.Styling;

/**
 * Represents a set of graphical effects that can be applied during the rendering process.
 * This class provides properties and methods to control effects such as Gaussian blur and drop shadow.
 */
public class RendererEffects {

    //Gaussian blur
    private double gaussianBlurRadius;

    //Drop shadow
    private double shadowKernelSize;
    private double shadowOffsetX;
    private double shadowOffsetY;
    private JMColor shadowColor;

    public void copyFrom(RendererEffects r) {
        this.setGaussianBlurRadius(r.getGaussianBlurRadius());
        this.setShadowKernelSize(r.getShadowKernelSize());
        this.setShadowColor(r.getShadowColor());
        this.setShadowOffset(r.getShadowOffsetX(), r.getShadowOffsetY());
    }
    
    
    
    
    public RendererEffects() {
        gaussianBlurRadius = 0;
        shadowKernelSize = 0;
        shadowOffsetX = 0;
        shadowOffsetY = 0;
        shadowColor = JMColor.BLACK;
    }

    public double getShadowKernelSize() {
        return shadowKernelSize;
    }

    public RendererEffects setShadowKernelSize(double shadowKernelSize) {
        this.shadowKernelSize = shadowKernelSize;
        return this;
    }

    public double getShadowOffsetX() {
        return shadowOffsetX;
    }

    public RendererEffects setShadowOffset(double shadowOffsetX, double shadowOffsetY) {
        this.shadowOffsetX = shadowOffsetX;
        this.shadowOffsetY = shadowOffsetY;
        return this;
    }

    public double getShadowOffsetY() {
        return shadowOffsetY;
    }

    public JMColor getShadowColor() {
        return shadowColor;
    }

    public RendererEffects setShadowColor(JMColor shadowColor) {
        this.shadowColor = shadowColor;
        return this;
    }

    public RendererEffects setShadowColor(String color) {
        this.shadowColor = JMColor.parse(color);
           return this;
    }

    public double getGaussianBlurRadius() {
        return gaussianBlurRadius;
    }

    public RendererEffects setGaussianBlurRadius(double gaussianBlurRadius) {
        this.gaussianBlurRadius = gaussianBlurRadius;
        return this;
    }

}
