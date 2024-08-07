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

import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Stateable;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class MODrawPropertiesArray implements Stylable, Stateable {

    private ArrayList<MathObject> objects;
    private final MODrawProperties mpRef;

    public MODrawPropertiesArray(MODrawProperties mp) {
        mpRef = new MODrawProperties();
        mpRef.copyFrom(mp);
        objects = new ArrayList<>();
    }

    public MODrawPropertiesArray() {
        mpRef = new MODrawProperties();
        mpRef.copyFrom(JMathAnimConfig.getConfig().getDefaultMP());
        objects = new ArrayList<>();
    }

    public MODrawPropertiesArray(ArrayList<MathObject> objects) {
        this.objects = objects;
        mpRef = new MODrawProperties();
    }

    public ArrayList<MathObject> getObjects() {
        return objects;
    }

    public void add(MathObject... objs) {
        objects.addAll(Arrays.asList(objs));
    }

    public void setObjects(ArrayList<MathObject> objects) {
        this.objects = objects;
    }

    @Override
    public void setVisible(Boolean visible) {
        for (MathObject obj : objects) {
            obj.getMp().setVisible(visible);
        }
        mpRef.setVisible(visible);
    }

    @Override
    public Boolean isVisible() {
        return mpRef.isVisible();
    }

    public boolean remove(MathObject o) {
        return objects.remove(o);
    }

    @Override
    public MODrawProperties copy() {
        return this.mpRef.copy();
    }

    @Override
    public void copyFrom(Stylable prop) {
        for (MathObject obj : objects) {
            obj.getMp().copyFrom(prop);
        }
        mpRef.copyFrom(prop);
    }

    @Override
    public void interpolateFrom(Stylable dst, double alpha) {
        for (MathObject obj : objects) {
            obj.getMp().interpolateFrom(dst, alpha);
        }
        mpRef.interpolateFrom(dst, alpha);
    }

    @Override
    public void interpolateFrom(Stylable a, Stylable b, double alpha) {
        for (MathObject obj : objects) {
            obj.getMp().interpolateFrom(a, b, alpha);
        }
        mpRef.interpolateFrom(a, b, alpha);
    }

    @Override
    public void loadFromStyle(String name) {
        for (MathObject obj : objects) {
            obj.getMp().loadFromStyle(name);

        }
        mpRef.loadFromStyle(name);
    }

    @Override
    public void rawCopyFrom(MODrawProperties mp) {
        for (MathObject obj : objects) {
            obj.getMp().rawCopyFrom(mp);
        }
        mpRef.rawCopyFrom(mp);
    }

    @Override
    public void restoreState() {
        for (MathObject obj : objects) {
            obj.getMp().restoreState();
        }
        mpRef.restoreState();

    }

    @Override
    public void saveState() {
        for (MathObject obj : objects) {
            obj.getMp().saveState();
        }
        mpRef.saveState();
    }

    @Override
    public void setDrawAlpha(double alpha) {
        for (MathObject obj : objects) {
            obj.getMp().setDrawAlpha(alpha);
        }
        mpRef.setDrawAlpha(alpha);

    }

    @Override
    public void setDrawColor(PaintStyle drawColor) {
        for (MathObject obj : objects) {
            obj.getMp().setDrawColor(drawColor);
        }
        mpRef.setDrawColor(drawColor);
    }

    @Override
    public void setFillAlpha(double alpha) {
        for (MathObject obj : objects) {
            obj.getMp().setFillAlpha(alpha);
        }
        mpRef.setFillAlpha(alpha);
    }

    @Override
    public void multDrawAlpha(double mult) {
        for (MathObject obj : objects) {
            obj.getMp().multDrawAlpha(mult);
        }
        mpRef.multDrawAlpha(mult);
    }

    @Override
    public void multFillAlpha(double mult) {
        for (MathObject obj : objects) {
            obj.getMp().multFillAlpha(mult);
        }
        mpRef.multFillAlpha(mult);
    }

    @Override
    public void setFillColor(PaintStyle fillColor) {
        for (MathObject obj : objects) {
            obj.getMp().setFillColor(fillColor);
        }
        mpRef.setFillColor(fillColor);
    }

    @Override
    public void setLayer(int layer) {
        for (MathObject obj : objects) {
            obj.getMp().setLayer(layer);
        }
        mpRef.setLayer(layer);
    }

    @Override
    public Integer getLayer() {
        return mpRef.getLayer();
    }

    public PaintStyle getDrawColor() {
        return mpRef.getDrawColor();
    }

    @Override
    public PaintStyle getFillColor() {
        return mpRef.getFillColor();
    }

    @Override
    public Stylable getSubMP(int n) {
        return objects.get(n).getMp();
    }

    @Override
    public MODrawProperties getFirstMP() {
        return mpRef;
    }

    @Override
    public StrokeLineCap getLineCap() {
        return mpRef.getLineCap();
    }

    @Override
    public StrokeLineJoin getLineJoin() {
        return mpRef.getLineJoin();
    }

    @Override
    public void setLinecap(StrokeLineCap linecap) {
        for (MathObject obj : objects) {
            obj.getMp().setLinecap(linecap);
        }
        mpRef.setLinecap(linecap);
    }

    @Override
    public void setLineJoin(StrokeLineJoin linejoin) {
        for (MathObject obj : objects) {
            obj.getMp().setLineJoin(linejoin);
        }
        mpRef.setLineJoin(linejoin);
    }

    @Override
    public Double getThickness() {
        return mpRef.getThickness();
    }

    @Override
    public void setThickness(Double thickness) {
        for (MathObject obj : objects) {
            obj.getMp().setThickness(thickness);
        }
        mpRef.setThickness(thickness);
    }

    @Override
    public void setDotStyle(Point.DotSyle dotStyle) {
        for (MathObject obj : objects) {
            obj.getMp().setDotStyle(dotStyle);
        }
        mpRef.setDotStyle(dotStyle);
    }

    @Override
    public Point.DotSyle getDotStyle() {
        return mpRef.getDotStyle();
    }

    @Override
    public void setDashStyle(MODrawProperties.DashStyle dashStyle) {
        for (MathObject obj : objects) {
            obj.getMp().setDashStyle(dashStyle);
        }
        mpRef.setDashStyle(dashStyle);
    }

    @Override
    public MODrawProperties.DashStyle getDashStyle() {
        return mpRef.getDashStyle();
    }

    @Override
    public Boolean isAbsoluteThickness() {
        return mpRef.isAbsoluteThickness();
    }

    @Override
    public void setAbsoluteThickness(Boolean absThickness) {
        for (MathObject obj : objects) {
            obj.getMp().setAbsoluteThickness(absThickness);
        }
        mpRef.setAbsoluteThickness(absThickness);
    }

    @Override
    public void multThickness(double multT) {
        for (MathObject obj : objects) {
            obj.getMp().multThickness(multT);
        }
        mpRef.multThickness(multT);
    }

    @Override
    public Boolean isFaceToCamera() {
        return mpRef.isFaceToCamera();
    }

    @Override
    public void setFaceToCamera(Boolean faceToCamera) {
        for (MathObject obj : objects) {
            obj.getMp().setFaceToCamera(faceToCamera);
        }
        mpRef.setFaceToCamera(faceToCamera);
    }

    @Override
    public Vec getFaceToCameraPivot() {
        return mpRef.getFaceToCameraPivot();
    }

    @Override
    public void setFaceToCameraPivot(Vec pivot) {
        for (MathObject obj : objects) {
            obj.getMp().setFaceToCameraPivot(pivot);
        }
        mpRef.setFaceToCameraPivot(pivot);
    }

    @Override
    public void setScaleArrowHead1(Double scale) {
        for (MathObject obj : objects) {
            obj.getMp().setScaleArrowHead1(scale);
        }
        mpRef.setScaleArrowHead1(scale);
    }

    @Override
    public void setScaleArrowHead2(Double scale) {
        for (MathObject obj : objects) {
            obj.getMp().setScaleArrowHead2(scale);
        }
        mpRef.setScaleArrowHead2(scale);
    }

    @Override
    public Double getScaleArrowHead1() {
        return mpRef.getScaleArrowHead1();
    }

    @Override
    public Double getScaleArrowHead2() {
        return mpRef.getScaleArrowHead2();
    }

}
