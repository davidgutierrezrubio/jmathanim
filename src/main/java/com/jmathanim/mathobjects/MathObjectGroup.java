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
package com.jmathanim.mathobjects;

import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.JMColor;
import com.jmathanim.Utils.MODrawProperties;
import com.jmathanim.Utils.Rect;
import com.jmathanim.jmathanim.JMathAnimScene;
import java.util.ArrayList;
import java.util.Collection;
import javafx.scene.shape.StrokeLineCap;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class MathObjectGroup extends MathObject {
    public enum Layout {
        RIGHT,
        LEFT,
        UP,
        DOWN
    }
    private final ArrayList<MathObject> objects;
    public MathObjectGroup() {
        this.objects = new ArrayList<>();
    }
    public MathObjectGroup(ArrayList<MathObject> objects) {
        this.objects = objects;
    }

    @Override
    public Point getCenter() {
        return getBoundingBox().getCenter();
    }

    @Override
    public <T extends MathObject> T moveTo(Point p) {
        for (MathObject obj : objects) {
            obj.moveTo(p);
        }
        return (T) this;
    }

    @Override
    public <T extends MathObject> T copy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void prepareForNonLinearAnimation() {
        for (MathObject obj : objects) {
            obj.prepareForNonLinearAnimation();
        }
    }

    @Override
    public void processAfterNonLinearAnimation() {
        for (MathObject obj : objects) {
            obj.processAfterNonLinearAnimation();
        }
    }

    @Override
    public Rect getBoundingBox() {
        Rect bbox = objects.get(0).getBoundingBox();
        for (MathObject obj : objects) {
            bbox = bbox.union(obj.getBoundingBox());
        }
        return bbox;
    }

    @Override
    public void registerChildrenToBeUpdated(JMathAnimScene scene) {
        for (MathObject obj : objects) {
            obj.registerChildrenToBeUpdated(scene);
        }
    }

    @Override
    public void unregisterChildrenToBeUpdated(JMathAnimScene scene) {
        for (MathObject obj : objects) {
            obj.unregisterChildrenToBeUpdated(scene);
        }
    }

    @Override
    public void draw(Renderer r) {
        for (MathObject obj : objects) {
            obj.draw(r);
        }
    }

    @Override
    public void update(JMathAnimScene scene) {
        for (MathObject obj : objects) {
            obj.update(scene);
        }
    }

    @Override
    public <T extends MathObject> T linecap(StrokeLineCap strokeLineCap) {
        for (MathObject obj : objects) {
            obj.linecap(strokeLineCap);
        }
        return (T) this;
    }

    @Override
    public <T extends MathObject> T style(String name) {
        for (MathObject obj : objects) {
            obj.style(name);
        }
        return (T) this;
    }

    @Override
    public <T extends MathObject> T layer(int layer) {
        for (MathObject obj : objects) {
            obj.layer(layer);
        }
        return (T) this;
    }

    @Override
    public <T extends MathObject> T setRelativeSize() {
        for (MathObject obj : objects) {
            obj.setRelativeSize();
        }
        return (T) this;
    }

    @Override
    public <T extends MathObject> T setAbsoluteSize(int anchorType) {
        return super.setAbsoluteSize(anchorType); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T extends MathObject> T setAbsoluteSize() {
        return super.setAbsoluteSize(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T extends MathObject> T stackToScreen(int anchorType, double xMargin, double yMargin) {
        return super.stackToScreen(anchorType, xMargin, yMargin); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    public <T extends MathObject> T stackTo(MathObject obj, int anchorType, double gap) {
        return super.stackTo(obj, anchorType, gap); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    public void setAbsoluteAnchorPoint(Point p) {
        super.setAbsoluteAnchorPoint(p); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T extends MathObject> T visible(boolean visible) {
        for (MathObject obj : objects) {
            obj.visible(visible);
        }
        return (T) this;
    }

    @Override
    public <T extends MathObject> T dashStyle(MODrawProperties.DashStyle dst) {
        for (MathObject obj : objects) {
            obj.dashStyle(dst);
        }
        return (T) this;
    }

    @Override
    public <T extends MathObject> T thickness(double th) {
        for (MathObject obj : objects) {
            obj.thickness(th);
        }
        return (T) this;
    }

    @Override
    public <T extends MathObject> T fillAlpha(double alpha) {
        for (MathObject obj : objects) {
            obj.fillAlpha(alpha);
        }
        return (T) this;
    }

    @Override
    public <T extends MathObject> T drawAlpha(double alpha) {
        for (MathObject obj : objects) {
            obj.drawAlpha(alpha);
        }
        return (T) this;
    }

    @Override
    public <T extends MathObject> T fillColor(JMColor fc) {
        for (MathObject obj : objects) {
            obj.fillColor(fc);
        }
        return (T) this;
    }

    @Override
    public <T extends MathObject> T drawColor(JMColor dc) {
        for (MathObject obj : objects) {
            obj.drawColor(dc);
        }
        return (T) this;
    }

    @Override
    public void restoreState() {
        for (MathObject obj : objects) {
            obj.restoreState();
        }
    }

    @Override
    public void saveState() {
        for (MathObject obj : objects) {
            obj.saveState();
        }
    }

    public void setLayout(int anchorType, double gap){
        
        for (int n = 1; n < objects.size(); n++) {
            objects.get(n).stackTo(objects.get(n-1), anchorType,gap);
        }
        
    }

    public int size() {
        return objects.size();
    }

    public int indexOf(Object o) {
        return objects.indexOf(o);
    }

    public <T> T[] toArray(T[] a) {
        return objects.toArray(a);
    }

    public MathObject get(int index) {
        return objects.get(index);
    }

    public boolean add(MathObject e) {
        return objects.add(e);
    }

    public void add(int index, MathObject element) {
        objects.add(index, element);
    }

    public void clear() {
        objects.clear();
    }

    public boolean addAll(Collection<? extends MathObject> c) {
        return objects.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends MathObject> c) {
        return objects.addAll(index, c);
    }

    public ArrayList<MathObject> getObjects() {
        return objects;
    }
    
    
    
    
}
