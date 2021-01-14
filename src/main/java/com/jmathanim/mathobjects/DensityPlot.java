/*
 * Copyright (C) 2021 David
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
package com.jmathanim.mathobjects;

import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Rect;
import java.util.function.BiFunction;
import javafx.scene.image.WritableImage;

/**
 *
 * @author David
 */
public class DensityPlot extends MathObject {

    Rect area;
    BiFunction<Double, Double, Double> densityMap;
    WritableImage raster;
    double widthView,heightView;

    public DensityPlot(Rect area, BiFunction<Double, Double, Double> densityMap) {
        this.area = area;
        this.densityMap = densityMap;
        widthView=-1;
        heightView=-1;
        scene=JMathAnimConfig.getConfig().getScene();
    }

    @Override
    public Point getCenter() {
        return area.getCenter();

    }

    @Override
    public <T extends MathObject> T copy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Rect getBoundingBox() {
        return area.copy();
    }

    @Override
    public void draw(Renderer r) {
        createRasterImage();
    }

    private void createRasterImage() {
        double w=scene.getCamera().getMathView().getWidth();
        double h=scene.getCamera().getMathView().getHeight();
        if ((w!=widthView)||(h!=heightView)) {
            widthView=w;
            heightView=h;
            double[] xy1=scene.getCamera().mathToScreen(area.xmin, area.ymax);
            double[] xy2=scene.getCamera().mathToScreen(area.xmax, area.ymin);
            final int wRaster = (int)(xy2[0]-xy1[0]);
            final int hRaster = (int)(xy2[1]-xy1[1]);
            System.out.println("Creating new density raster "+wRaster+" "+hRaster);
            raster=new WritableImage(wRaster, hRaster);
        }
    }

}
