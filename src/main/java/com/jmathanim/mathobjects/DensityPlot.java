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

import com.jmathanim.Utils.*;
import com.jmathanim.jmathanim.JMathAnimScene;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents a density plot in a specified area given by a Rect
 * object and a Bifunction object
 *
 * @author David Gutiérrez davidgutierrezrubio@gmail.com
 */
public class DensityPlot extends AbstractJMImage implements hasScalarParameter {

    TriFunction<Double, Double, Double, Double> densityLambdaFunction;
    WritableImage raster;
    double widthView, heightView;
    private int wRaster;
    private int hRaster;
    private ColorScale colorScale;
    private double scalar;

    /**
     * Creates a new Densityplot with the given domain
     *
     * @param area The domain to represent, in a Rect object
     * @param densityMap A lambda function, for example (x,y)-&gt;x*y
     * @return The density plot created
     */
    public static DensityPlot make(Rect area, BiFunction<Double, Double, Double> densityMap) {
        TriFunction<Double, Double, Double, Double> triMap = (x, y, t) -> densityMap.apply(x, y);
        return new DensityPlot(area, triMap);
    }

    /**
     * Creates a new Densityplot with the given domain
     *
     * @param area The domain to represent, in a Rect object
     * @param densityMap A lambda function, for example (x,y)-&gt;x*y
     * @return The density plot created
     */
    public static DensityPlot make(Rect area, TriFunction<Double, Double, Double, Double> densityMap) {
        return new DensityPlot(area, densityMap);
    }

    private DensityPlot(Rect area, TriFunction<Double, Double, Double, Double> densityMap) {
        this.bbox = area.copy();
        this.densityLambdaFunction = densityMap;
        widthView = -1;// Ensures that will create the raster image in the first update of the object
        heightView = -1;
        colorScale = new ColorScale();
        this.scalar = 0;
        scene = JMathAnimConfig.getConfig().getScene();
    }

    @Override
    public AbstractJMImage copy() {
        DensityPlot copy = new DensityPlot(bbox, densityLambdaFunction);
        copy.colorScale.copyFrom(colorScale);
        return this;
    }

    @Override
    public void copyStateFrom(MathObject obj) {
         super.copyStateFrom(obj);
        if (!(obj instanceof DensityPlot)) {
            return;
        }
        DensityPlot dp = (DensityPlot) obj;
        this.bbox.copyFrom(dp.bbox);
        this.densityLambdaFunction = dp.densityLambdaFunction;
        widthView = dp.widthView;
        heightView = dp.heightView;
        colorScale.copyFrom(dp.colorScale);
    }


    @Override
    public void update(JMathAnimScene scene) {
        super.update(scene);
        createRasterImage();
    }

    private void createRasterImage() {
        double w = getCamera().getMathView().getWidth();
        double h = getCamera().getMathView().getHeight();
        if ((w != widthView) || (h != heightView)) {// Only recreate it if necessary
            widthView = w;
            heightView = h;
            double[] xy1 = getCamera().mathToScreen(bbox.xmin, bbox.ymax);
            double[] xy2 = getCamera().mathToScreen(bbox.xmax, bbox.ymin);
            wRaster = (int) (xy2[0] - xy1[0]);
            hRaster = (int) (xy2[1] - xy1[1]);
            raster = new WritableImage(wRaster, hRaster);
            updatePixels();
        }
    }

    private void updatePixels() {
        if (raster == null) {
            return;
        }
        FutureTask<Integer> task = new FutureTask<>(() -> {
            createColorScale();
            PixelWriter pixelWriter = raster.getPixelWriter();
            for (int i = 0; i < wRaster; i++) {
                for (int j = 0; j < hRaster; j++) {
                    Point p = bbox.getRelPoint(i * 1d / wRaster, 1 - j * 1d / hRaster);
                    double z = densityLambdaFunction.apply(p.v.x, p.v.y, getScalar());
                    pixelWriter.setColor(i, j, colorScale.getColorValue(z).getFXColor());
                }
            }
            
            return 0;
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
            double b = -Double.MAX_VALUE;
            for (int i = 0; i < wRaster; i++) {
                for (int j = 0; j < hRaster; j++) {
                    Point p = bbox.getRelPoint(i * 1d / wRaster, 1 - j * 1d / hRaster);
                    double z = densityLambdaFunction.apply(p.v.x, p.v.y, getScalar());
                    if (z < a) {
                        a = z;
                    }
                    if (z > b) {
                        b = z;
                    }
                }
            }
            colorScale = ColorScale.createDefaultBR(-1, 1);
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
    public TriFunction<Double, Double, Double, Double> getFunction() {
        return densityLambdaFunction;
    }

    public void setFunction(BiFunction<Double, Double, Double> densityMap) {
        this.densityLambdaFunction = (x, y, t) -> densityMap.apply(x, y);
        updatePixels();
    }

    public void setFunction(TriFunction<Double, Double, Double, Double> densityMap) {
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
    public DensityPlot applyAffineTransform(AffineJTransform tr) {
        // TODO: Apply model matrix
        return this;
    }

    @Override
    public double getScalar() {
        return scalar;
    }

    @Override
    public void setScalar(double scalar) {
        this.scalar = scalar;
        updatePixels();
    }

}
