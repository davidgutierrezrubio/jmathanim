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
import com.jmathanim.mathobjects.Shape;
import com.jmathanim.mathobjects.Stateable;
import java.util.ArrayList;
import java.util.Collection;
import javafx.scene.shape.StrokeLineCap;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class MODrawPropertiesArray implements Stylable, Stateable {

    private ArrayList<MathObject> objects;
    private int layer = 0;

    public MODrawPropertiesArray() {
    }

    public MODrawPropertiesArray(ArrayList<MathObject> objects) {
        this.objects = objects;
    }

    public ArrayList<MathObject> getObjects() {
        return objects;
    }

    public boolean add(MathObject e) {
        return objects.add(e);
    }

    public boolean addAll(Collection<? extends MathObject> c) {
        return objects.addAll(c);
    }

    public void setObjects(ArrayList<MathObject> objects) {
        this.objects = objects;
    }

    public boolean remove(Object o) {
        return objects.remove(o);
    }

    @Override
    public MODrawProperties copy() {
        return this.getFirstMP().copy();
    }

    @Override
    public void copyFrom(Stylable prop) {
        for (MathObject obj : objects) {
            obj.getMp().copyFrom(prop);
        }
    }

    @Override
    public void interpolateFrom(Stylable a, Stylable b, double alpha) {
        for (MathObject obj : objects) {
            obj.getMp().interpolateFrom(a, b, alpha);
        }
    }

    @Override
    public void loadFromStyle(String name) {
        for (MathObject obj : objects) {
            obj.getMp().loadFromStyle(name);
        }
    }

    @Override
    public void rawCopyFrom(MODrawProperties mp) {
        for (MathObject obj : objects) {
            obj.getMp().rawCopyFrom(mp);
        }
    }

    @Override
    public void restoreState() {
        for (MathObject obj : objects) {
            obj.getMp().restoreState();
        }
    }

    @Override
    public void saveState() {
        for (MathObject obj : objects) {
            obj.getMp().saveState();
        }
    }

    @Override
    public void setDrawAlpha(double alpha) {
        for (MathObject obj : objects) {
            obj.getMp().setDrawAlpha(alpha);
        }
    }

    @Override
    public void setDrawColor(JMColor drawColor) {
        for (MathObject obj : objects) {
            obj.getMp().setDrawColor(drawColor);
        }
    }

    @Override
    public void setFillAlpha(double alpha) {
        for (MathObject obj : objects) {
            obj.getMp().setFillAlpha(alpha);
        }
    }

    @Override
    public void setFillColor(JMColor fillColor) {
        for (MathObject obj : objects) {
            obj.getMp().setFillColor(fillColor);
        }
    }

    @Override
    public void setFillColorIsDrawColor(Boolean fillColorIsDrawColor) {
        for (MathObject obj : objects) {
            obj.getMp().setFillColorIsDrawColor(fillColorIsDrawColor);
        }
    }

    @Override
    public void setFilled(boolean fill) {
        for (MathObject obj : objects) {
            obj.getMp().setFilled(fill);
        }
    }

    @Override
    public void setLayer(int layer) {
        for (MathObject obj : objects) {
            obj.getMp().setLayer(layer);
        }
    }

    @Override
    public Integer getLayer() {
        return objects.get(0).getMp().getLayer();
    }

    @Override
    public void setMultFillAlpha(double alpha) {
        for (MathObject obj : objects) {
            obj.getMp().setMultFillAlpha(alpha);
        }
    }

    @Override
    public void setMultDrawAlpha(double alpha) {
        for (MathObject obj : objects) {
            obj.getMp().setMultDrawAlpha(alpha);
        }
    }

    @Override
    public JMColor getDrawColor() {
        return objects.get(0).getMp().getDrawColor();
    }

    @Override
    public JMColor getFillColor() {
        return objects.get(0).getMp().getFillColor();
    }

    public Stylable getSubMP(int n) {
        return objects.get(n).getMp();
    }

    @Override
    public MODrawProperties getFirstMP() {
        Stylable mpRec = this;
        while (!(mpRec instanceof MODrawProperties)) {
            mpRec = mpRec.getSubMP(0);
        }
        MODrawProperties mpO = (MODrawProperties) mpRec;
        return mpO;
    }

    @Override
    public StrokeLineCap getLinecap() {
        return objects.get(0).getMp().getLinecap();
    }

    @Override
    public void setLinecap(StrokeLineCap linecap) {
        for (MathObject obj : objects) {
            obj.getMp().setLinecap(linecap);
        }
    }

    @Override
    public Double getThickness() {
        return objects.get(0).getMp().getThickness();
    }

    @Override
    public void setThickness(Double thickness) {
        for (MathObject obj : objects) {
            obj.getMp().setThickness(thickness);
        }
    }

    @Override
    public void setDotStyle(Point.DotSyle dotStyle) {
        for (MathObject obj : objects) {
            obj.getMp().setDotStyle(dotStyle);
        }
    }

    @Override
    public Point.DotSyle getDotStyle() {
        return objects.get(0).getMp().getDotStyle();
    }

    @Override
    public void setDashStyle(MODrawProperties.DashStyle dashStyle) {
        for (MathObject obj : objects) {
            obj.getMp().setDashStyle(dashStyle);
        }
    }

    @Override
    public MODrawProperties.DashStyle getDashStyle() {
        return objects.get(0).getMp().getDashStyle();
    }

    @Override
    public Boolean isAbsoluteThickness() {
        return objects.get(0).getMp().isAbsoluteThickness();
    }

    @Override
    public void setAbsoluteThickness(Boolean absThickness) {
        for (MathObject obj : objects) {
            obj.getMp().setAbsoluteThickness(absThickness);
        }
    }

    @Override
    public Boolean isFillColorIsDrawColor() {
        return objects.get(0).getMp().isFillColorIsDrawColor();
    }

    @Override
    public void multThickness(double multT) {
        for (MathObject obj : objects) {
            obj.getMp().multThickness(multT);
        }
    }
}
