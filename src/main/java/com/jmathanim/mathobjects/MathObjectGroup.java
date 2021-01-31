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
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Styling.MODrawPropertiesArray;
import com.jmathanim.Styling.Stylable;
import com.jmathanim.Utils.Rect;
import com.jmathanim.jmathanim.JMathAnimScene;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * A class that manages sets of MathObjects. The objectes are not added to the
 * scene when you add this object to the scene. It acts as a container to
 * perform easily bulk-operations
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class MathObjectGroup extends MathObject implements Iterable<MathObject> {

    public static MathObjectGroup make(MathObject... objects) {
        return new MathObjectGroup(objects);
    }

    MODrawPropertiesArray mpArray;
    private final ArrayList<MathObject> objects;

    public MathObjectGroup() {
        super();
        mpArray = new MODrawPropertiesArray();
        this.objects = new ArrayList<>();
    }

    public MathObjectGroup(MathObject... objects) {
        this(new ArrayList<>(Arrays.asList(objects)));
    }

    public MathObjectGroup(ArrayList<MathObject> objects) {
        super();
        mpArray = new MODrawPropertiesArray();
        this.objects = objects;
        for (MathObject o : objects) {
            mpArray.add(o);
        }
    }

    public MathObjectGroup add(MathObject... objs) {
        for (MathObject obj : objs) {
            objects.add(obj);
        }
        return this;
    }

    public MathObjectGroup add(MathObject e) {
        objects.add(e);
        mpArray.add(e);
        return this;
    }

    public void add(int index, MathObject element) {
        objects.add(index, element);
        mpArray.add(element);
    }

    public void addAll(Collection<? extends MathObject> c) {
        objects.addAll(c);
        mpArray.getObjects().addAll(c);
    }

    public void addAll(int index, Collection<? extends MathObject> c) {
        objects.addAll(index, c);
        mpArray.getObjects().addAll(c);
    }

    @Override
    public <T extends MathObject> T applyAffineTransform(AffineJTransform tr) {
        for (MathObject obj : objects) {
            obj.applyAffineTransform(tr);
        }
        tr.applyTransformsToDrawingProperties(this);
        return (T) this;
    }

    public void clear() {
        objects.clear();
        mpArray.getObjects().clear();
    }

    @Override
    public MathObjectGroup copy() {
        MathObjectGroup copy = new MathObjectGroup();
        for (MathObject obj : this.getObjects()) {
            copy.add(obj.copy());
        }
        return copy;
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r) {
        for (MathObject obj : this.getObjects()) {
            if (!scene.isAlreadyDrawed(obj)) {
                obj.draw(scene, r);
            }
        }
        scene.markAsAlreadyDrawed(this);
    }

    public MathObject get(int index) {
        return objects.get(index);
    }

    @Override
    public Rect getBoundingBox() {
        Rect bbox = objects.get(0).getBoundingBox();
        for (MathObject obj : objects) {
            bbox = Rect.union(bbox, obj.getBoundingBox());
        }
        return bbox;
    }

    public ArrayList<MathObject> getObjects() {
        return objects;
    }

    public int indexOf(Object o) {
        return objects.indexOf(o);
    }

    @Override
    public Iterator<MathObject> iterator() {
        return objects.iterator();
    }

    @Override
    public void registerChildrenToBeUpdated(JMathAnimScene scene) {
        for (MathObject obj : objects) {
            obj.registerChildrenToBeUpdated(scene);
        }
    }

    @Override
    public void restoreState() {
        mpArray.restoreState();
        for (MathObject obj : objects) {
            obj.restoreState();
        }
    }

    @Override
    public void saveState() {
        mpArray.saveState();
        for (MathObject obj : objects) {
            obj.saveState();
        }
    }

    @Override
    public <T extends MathObject> T setAbsoluteSize(Anchor.Type anchorType) {
        for (MathObject obj : objects) {
            obj.setAbsoluteSize(anchorType);
        }
        return (T) this;
    }

    @Override
    public <T extends MathObject> T setAbsoluteSize() {
        return super.setAbsoluteSize(); //To change body of generated methods, choose Tools | Templates.
    }

    public MathObjectGroup setLayout(Anchor.Type anchorType, double gap) {

        for (int n = 1; n < objects.size(); n++) {
            objects.get(n).stackTo(objects.get(n - 1), anchorType, gap);
        }
        return this;

    }

    @Override
    public <T extends MathObject> T setRelativeSize() {
        for (MathObject obj : objects) {
            obj.setRelativeSize();
        }
        return (T) this;
    }

    public int size() {
        return objects.size();
    }

    public <T> T[] toArray(T[] a) {
        return objects.toArray(a);
    }

    @Override
    public void unregisterChildrenToBeUpdated(JMathAnimScene scene) {
        for (MathObject obj : objects) {
            obj.unregisterChildrenToBeUpdated(scene);
        }
    }

    @Override
    public void update(JMathAnimScene scene) {
    }

    @Override
    public <T extends MathObject> T visible(boolean visible) {
        for (MathObject obj : objects) {
            obj.visible(visible);
        }
        return (T) this;
    }

    @Override
    public Stylable getMp() {
        return mpArray;
    }

}
