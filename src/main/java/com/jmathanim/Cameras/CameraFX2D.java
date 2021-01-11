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
package com.jmathanim.Cameras;

import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;

/**
 * A Camera subclass designed to work with the JavaFX library. This class
 * converts math coordinates to screen cordinates. Screen coordinates are always
 * (0,0)-(w,h) where (0,0) is upper left corner
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class CameraFX2D extends Camera {

    private double xminB, xmaxB, yminB, ymaxB;//Backup values for saveState()

    public CameraFX2D(JMathAnimScene scene, int screenWidth, int screenHeight) {
        super(scene,screenWidth,screenHeight);

    }


    /**
     * Center camera in math-coordinates x,y
     *
     * @param x
     * @param y
     */
    @Override
    public final void setCenter(double x, double y) {
        double mWidth = xmax - xmin;
        setMathXY(x - .5 * mWidth, x + .5 * mWidth, y);

    }

    public void setCenter(MathObject obj) {
        double xx = obj.getCenter().v.x;
        double yy = obj.getCenter().v.y;
        double zz = obj.getCenter().v.z;
        setCenter(xx, yy);
    }

    @Override
    public <T extends Camera> T  setMathXY(double xmin, double xmax, double ycenter) {

        if (xmax <= xmin) {
            return (T) this;
        }
        this.xmin = xmin;
        this.xmax = xmax;
        //Compute y so that proportion is the same as the screen
        double ratioScreen = ((double) screenWidth) / ((double) screenHeight);
        //(xmax-xmin)/(ymax-ymin)=ratioScreen, so...
        this.ymax = ycenter + .5 * (xmax - xmin) / ratioScreen;
        this.ymin = ycenter - .5 * (xmax - xmin) / ratioScreen;
        return (T) this;
    }

    @Override
    public int mathToScreen(double mathScalar) {
        //xmin,ymin->(0,0)
        //xmax, ymax->(screenWidth,screenHeight)
//        resul = (int) ((mathScalar - xmin) + mathScalar * screenWidth / xmax);
        return (int) Math.round(mathScalar * screenWidth / (xmax - xmin));
    }

    public double mathToScreenFX(double mathScalar) {
        //xmin,ymin->(0,0)
        //xmax, ymax->(screenWidth,screenHeight)
//        resul = (int) ((mathScalar - xmin) + mathScalar * screenWidth / xmax);
        return mathScalar * screenHeight / (ymax - ymin);
    }

    public double[] mathToScreenFX(double mathX, double mathY) {
        //xmin,ymin->(0,0)
        //xmax, ymax->(screenWidth,screenHeight)
        double x, y;
        x = (mathX - xmin) * screenWidth / (xmax - xmin);
        y = (ymax - mathY) * screenHeight / (ymax - ymin);
        return new double[]{x, y};
    }

    public double[] screenToMath(double x, double y) {
        double mx = (x * (xmax - xmin) / screenWidth + xmin);
        double my = -(y * (ymax - ymin) / screenHeight - ymax);
        return new double[]{mx, my};
    }

    @Override
    public double screenToMath(double screenScalar) {
        //resul = (int) ((mathScalar - xmin) + mathScalar * screenWidth / xmax);
        return screenScalar * (xmax - xmin) / screenWidth;
    }

    public double[] mathToScreenFX(Vec p) {
        return mathToScreenFX(p.x, p.y);
    }

    @Override
    public void saveState() {
        xminB = xmin;
        xmaxB = xmax;
        yminB = ymin;
        ymaxB = ymax;
    }

    @Override
    public void restoreState() {
        xmin = xminB;
        xmax = xmaxB;
        ymin = yminB;
        ymax = ymaxB;
    }

    @Override
    public int[] mathToScreen(double mathX, double mathY) {
        throw new UnsupportedOperationException("Not supported on FX."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setWidth(double d) {
        scale(d / getMathView().getWidth());
    }

}
