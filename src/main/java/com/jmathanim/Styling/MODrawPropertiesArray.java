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

import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Stateable;
import java.util.ArrayList;
import javafx.scene.shape.StrokeLineCap;

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
        for (MathObject obj : objs) {
            objects.add(obj);
        }
    }

    public void setObjects(ArrayList<MathObject> objects) {
        this.objects = objects;
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
    public void setDrawColor(JMColor drawColor) {
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
    public void setFillColor(JMColor fillColor) {
        for (MathObject obj : objects) {
            obj.getMp().setFillColor(fillColor);
        }
        mpRef.setFillColor(fillColor);
    }

    @Override
    public void setFillColorIsDrawColor(Boolean fillColorIsDrawColor) {
        for (MathObject obj : objects) {
            obj.getMp().setFillColorIsDrawColor(fillColorIsDrawColor);
        }
        mpRef.setFillColorIsDrawColor(fillColorIsDrawColor);
    }

    @Override
    public void setFilled(boolean fill) {
        for (MathObject obj : objects) {
            obj.getMp().setFilled(fill);
        }
        mpRef.setFilled(fill);
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

    @Override
    public void setMultFillAlpha(double alpha) {
        for (MathObject obj : objects) {
            obj.getMp().setMultFillAlpha(alpha);
        }
        mpRef.setMultFillAlpha(alpha);
    }

    @Override
    public void setMultDrawAlpha(double alpha) {
        for (MathObject obj : objects) {
            obj.getMp().setMultDrawAlpha(alpha);
        }
        mpRef.setMultDrawAlpha(alpha);
    }

    @Override
    public JMColor getDrawColor() {
        return mpRef.getDrawColor();
    }

    @Override
    public JMColor getFillColor() {
        return mpRef.getFillColor();
    }

    public Stylable getSubMP(int n) {
        return objects.get(n).getMp();
    }

    @Override
    public MODrawProperties getFirstMP() {
        return mpRef;
    }

    @Override
    public StrokeLineCap getLinecap() {
        return mpRef.getLinecap();
    }

    @Override
    public void setLinecap(StrokeLineCap linecap) {
        for (MathObject obj : objects) {
            obj.getMp().setLinecap(linecap);
        }
        mpRef.setLinecap(linecap);
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
    public Boolean isFillColorIsDrawColor() {
        return mpRef.isFillColorIsDrawColor();
    }

    @Override
    public void multThickness(double multT) {
        for (MathObject obj : objects) {
            obj.getMp().multThickness(multT);
        }
        mpRef.multThickness(multT);
    }
}
