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
import com.jmathanim.jmathanim.JMathAnimScene;
import java.util.function.BiFunction;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 *
 * @author David
 */
public class DensityPlot extends AbstractJMImage {

    BiFunction<Double, Double, Double> densityMap;
    WritableImage raster;
    double widthView,heightView;
    private int wRaster;
    private int hRaster;

    public DensityPlot(Rect area, BiFunction<Double, Double, Double> densityMap) {
        this.bbox=area.copy();
        this.densityMap = densityMap;
        widthView=-1;
        heightView=-1;
        scene=JMathAnimConfig.getConfig().getScene();
    }

    @Override
    public <T extends MathObject> T copy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }



    @Override
    public int getUpdateLevel() {
        return 0;
    }

    @Override
    public void update(JMathAnimScene scene) {
        super.update(scene);
        createRasterImage();
    }

    
    private void createRasterImage() {
        double w=scene.getCamera().getMathView().getWidth();
        double h=scene.getCamera().getMathView().getHeight();
        if ((w!=widthView)||(h!=heightView)) {
            widthView=w;
            heightView=h;
            double[] xy1=scene.getCamera().mathToScreen(bbox.xmin, bbox.ymax);
            double[] xy2=scene.getCamera().mathToScreen(bbox.xmax, bbox.ymin);
            wRaster = (int)(xy2[0]-xy1[0]);
            hRaster = (int)(xy2[1]-xy1[1]);
            System.out.println("Creating new density raster "+wRaster+" "+hRaster);
            raster=new WritableImage(wRaster, hRaster);
            updatePixels();
        }
    }
    private void updatePixels() {
        PixelWriter pixelWriter = raster.getPixelWriter();
        for (int i = 0; i < wRaster; i++) {
            for (int j = 0; j < hRaster; j++) {
                Point p=bbox.getRelPoint(i*1d/wRaster, j*1d/hRaster);
                double z=densityMap.apply(p.v.x, p.v.y);
                int zt=(int)(z*100);
                pixelWriter.setColor(i, j, Color.rgb(zt, zt, zt));
            }
            
        }
    }

    @Override
    public String getId() {
        return "";
    }

    @Override
    public Image getImage() {
        return raster;
    }

}
