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

import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.ColorScale;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Rect;
import com.jmathanim.jmathanim.JMathAnimScene;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/**
 * This class represents a density plot in a specified area given by a Rect
 * object and a Bifunction object
 *
 * @author David Gutiérrez davidgutierrezrubio@gmail.com
 */
public class DensityPlot extends AbstractJMImage {

    BiFunction<Double, Double, Double> densityLambdaFunction;
    WritableImage raster;
    double widthView, heightView;
    private int wRaster;
    private int hRaster;
    private ColorScale colorScale;

    /**
     * Creates a new Densityplot with the given domain
     *
     * @param area The domain to represent, in a Rect object
     * @param densityMap A lambda function, for example (x,y)-&gt;x*y
     * @return The density plot created
     */
    public static DensityPlot make(Rect area, BiFunction<Double, Double, Double> densityMap) {
        return new DensityPlot(area, densityMap);
    }

    public DensityPlot(Rect area, BiFunction<Double, Double, Double> densityMap) {
        this.bbox = area.copy();
        this.densityLambdaFunction = densityMap;
        widthView = -1;//Ensures that will create the raster image in the first update of the object
        heightView = -1;
        colorScale = new ColorScale();
        scene = JMathAnimConfig.getConfig().getScene();
    }

    public <T extends MathObject> T copy() {
        return (T) new DensityPlot(bbox, densityLambdaFunction);
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
        double w = scene.getCamera().getMathView().getWidth();
        double h = scene.getCamera().getMathView().getHeight();
        if ((w != widthView) || (h != heightView)) {//Only recreate it if necessary
            widthView = w;
            heightView = h;
            double[] xy1 = scene.getCamera().mathToScreen(bbox.xmin, bbox.ymax);
            double[] xy2 = scene.getCamera().mathToScreen(bbox.xmax, bbox.ymin);
            wRaster = (int) (xy2[0] - xy1[0]);
            hRaster = (int) (xy2[1] - xy1[1]);
            raster = new WritableImage(wRaster, hRaster);
            updatePixels();
        }
    }

    private void updatePixels() {

        FutureTask<Integer> task = new FutureTask<>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                createColorScale();
                PixelWriter pixelWriter = raster.getPixelWriter();
                for (int i = 0; i < wRaster; i++) {
                    for (int j = 0; j < hRaster; j++) {
                        Point p = bbox.getRelPoint(i * 1d / wRaster, 1 - j * 1d / hRaster);
                        double z = densityLambdaFunction.apply(p.v.x, p.v.y);
                        pixelWriter.setColor(i, j, colorScale.getColorValue(z).getFXColor());
                    }
                }

                return 0;
            }
        });
        Platform.runLater(task);
        try {
            task.get();
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(DensityPlot.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void createColorScale() {
        if (colorScale.getMarkers().isEmpty()) {
            double a = Double.MAX_VALUE;
            double b = Double.MIN_VALUE;
            for (int i = 0; i < wRaster; i++) {
                for (int j = 0; j < hRaster; j++) {
                    Point p = bbox.getRelPoint(i * 1d / wRaster, 1 - j * 1d / hRaster);
                    double z = densityLambdaFunction.apply(p.v.x, p.v.y);
                    if (z < a) {
                        a = z;
                    }
                    if (z > b) {
                        b = z;
                    }
                }
            }
            colorScale = ColorScale.createDefault(-1, 1);
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

    /**
     * Gets the current function represented in the density
     *
     * @return
     */
    public BiFunction<Double, Double, Double> getFunction() {
        return densityLambdaFunction;
    }

    public void setFunction(BiFunction<Double, Double, Double> densityMap) {
        this.densityLambdaFunction = densityMap;
        updatePixels();
    }

    public ColorScale getColorScale() {
        return colorScale;
    }

    public void setColorScale(ColorScale colorScale) {
        this.colorScale = colorScale;
    }

    @Override
    public <T extends MathObject> T applyAffineTransform(AffineJTransform tr) {
        //Nothing to do (for now...)
        return (T) this;
    }

}
