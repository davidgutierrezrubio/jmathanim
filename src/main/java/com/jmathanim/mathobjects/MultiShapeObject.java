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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * This class stores multiple JMPathObjects, and properly apply transforms and
 * animations to them
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class MultiShapeObject extends MathObject implements Iterable<Shape> {

    public boolean isAddedToScene;
    public final ArrayList<Shape> shapes;

    public static MultiShapeObject make(Shape...shapes) {
        return new MultiShapeObject(shapes);
    }
    
    public MultiShapeObject() {
        this(new ArrayList<Shape>());
    }

    public MultiShapeObject(Shape... shapes) {
        this(Arrays.asList(shapes));
    }

    public MultiShapeObject(List<Shape> jmps) {
        super();
        isAddedToScene = false;
        this.shapes = new ArrayList<>();
        this.shapes.addAll(jmps);
    }

    public boolean add(Shape e) {
        return shapes.add(e);
    }

    public boolean addJMPathObject(JMPath p) {
        return shapes.add(new Shape(p, null));
    }

    public boolean addJMPathObject(JMPath p, MODrawProperties mp) {
        return shapes.add(new Shape(p, mp));
    }

    @Override
    public Point getCenter() {
        return getBoundingBox().getCenter();
    }

    @Override
    public <T extends MathObject> T fillColor(JMColor fc) {
        for (Shape jmp : shapes) {
            jmp.fillColor(fc);
        }
        return super.fillColor(fc);
    }

    @Override
    public <T extends MathObject> T drawColor(JMColor dc) {
        for (Shape jmp : shapes) {
            jmp.drawColor(dc);
        }
        return super.drawColor(dc);
    }

//    @Override
//    public <T extends MathObject> T shift(Vec shiftVector) {
//        for (Shape jmp : shapes) {
//            jmp.shift(shiftVector);
//        }
//        return (T) this;
//    }
    @Override
    public MultiShapeObject copy() {
        MultiShapeObject resul = new MultiShapeObject();
        for (Shape sh : shapes) {
            final Shape copy = sh.copy();
            resul.add(copy);
        }
        resul.mp.copyFrom(mp);
        resul.absoluteSize = this.absoluteSize;
        return resul;
    }

    @Override
    public void draw(Renderer r) {

        int n = 0;
        for (Shape jmp : shapes) {
            if (jmp.isVisible()) {
                if (absoluteSize) {
                    r.drawAbsoluteCopy(jmp, getAbsoluteAnchor().v);//TODO: This doesnt work for overrided methods (e.g.: line)
                } else {
                    jmp.draw(r);
//                    if (isShowDebugText()) {
//                        r.debugText("" + n, jmp.getCenter().v);
//                    }
                }
            }
            n++;
        }
    }

    public <T extends MultiShapeObject> T showDebugIndices(boolean value) {
        if (value) {
            int k = 0;
            for (Shape sh : shapes) {
                sh.debugText("" + k);
                k++;
            }
        } else {
            for (Shape sh : shapes) {
                sh.debugText("");
            }
        }
        return (T) this;
    }

    @Override
    public Rect getBoundingBox() {
        if (shapes.size() > 0) {
            Rect resul = null;
            for (Shape jmp : shapes) {
                resul = Rect.union(resul, jmp.getBoundingBox());
            }
            return resul;
        } else {
            return null;
        }
    }

    @Override
    public <T extends MathObject> T style(String name) {
        for (Shape jmp : shapes) {
            jmp.style(name);
        }
        return (T) this;
    }

    @Override
    public <T extends MathObject> T drawAlpha(double t) {
        for (Shape jmp : shapes) {
            jmp.drawAlpha(t);
        }
        return (T) this;
    }

    @Override
    public <T extends MathObject> T fillAlpha(double t) {
        for (Shape jmp : shapes) {
            jmp.fillAlpha(t);
        }
        return (T) this;
    }

    @Override
    public <T extends MathObject> T multDrawAlpha(double t) {
        for (Shape jmp : shapes) {
            jmp.multDrawAlpha(t);
        }
        return (T) this;
    }

    @Override
    public <T extends MathObject> T multFillAlpha(double t) {
        for (Shape jmp : shapes) {
            jmp.multFillAlpha(t);
        }
        return (T) this;
    }

    public Shape get(int n) {
        return shapes.get(n);
    }

    @Override
    public <T extends MathObject> T layer(int layer) {
        for (Shape sh : shapes) {
            sh.layer(layer);
        }
        return (T) this;
    }

    @Override
    public <T extends MathObject> T visible(boolean visible) {
        for (Shape sh : shapes) {
            sh.visible(visible);
        }
        return (T) this;
    }

    @Override
    public void registerChildrenToBeUpdated(JMathAnimScene scene) {
        for (Shape o : shapes) {
            o.registerChildrenToBeUpdated(scene);
        }
    }

    @Override
    public void update(JMathAnimScene scene) {
    }

    @Override
    public void restoreState() {
        super.restoreState();
        for (Shape o : shapes) {
            o.restoreState();
        }
    }

    @Override
    public void saveState() {
        super.saveState();
        for (Shape o : shapes) {
            o.saveState();
        }
    }

    @Override
    public void unregisterChildrenToBeUpdated(JMathAnimScene scene) {
        for (Shape o : shapes) {
            o.unregisterChildrenToBeUpdated(scene);
        }
    }

    @Override
    public void interpolateMPFrom(MODrawProperties mpDst, double alpha) {
        for (int n = 0; n < shapes.size(); n++) {
            shapes.get(n).interpolateMPFrom(mpDst, alpha);
        }
    }

    @Override
    public <T extends MathObject> T fillWithDrawColor(boolean fcd) {
        for (int n = 0; n < shapes.size(); n++) {
            shapes.get(n).fillWithDrawColor(fcd);
        }
        return (T) this;
    }

    public ArrayList<Shape> getShapes() {
        return shapes;
    }

    @Override
    public Iterator<Shape> iterator() {
        return shapes.iterator();
    }

    public int size() {
        return shapes.size();
    }

    public <T extends MultiShapeObject> T alignCenter(int n, MultiShapeObject lat, int m) {
        shift(this.get(n).getCenter().to(lat.get(m).getCenter()));
        return (T) this;
    }

    @Override
    public boolean isEmpty() {
        boolean resul = false;
        for (Shape sh : shapes) {
            resul = resul | sh.isEmpty();
        }
        return resul;
    }

    /**
     * Extracts a part of a MultiShape, given by a set of indices.Indices are
     * unaltered, so shape 5 after slicing is still shape 5.
     *
     * @param <T> Multishape subclass
     * @param delete If true, sliced shapes will be removed from the original
     * and replaced with empty shapes.
     * @param indices indices to slice (varargs)
     * @return A new multishape instance with the extracted shapes.
     */
    public <T extends MultiShapeObject> T slice(boolean delete, Integer... indices) {
        List<Integer> list = Arrays.asList(indices);
        T resul = (T) this.copy();
        for (int n = 0; n < resul.shapes.size(); n++) {
            resul.shapes.set(n, new Shape());
        }
        for (int n = 0; n < this.shapes.size(); n++) {
            if (list.contains(n)) {//if this index is marked for extraction...
                
                if (delete) {
                    resul.shapes.set(n, this.get(n));
                    this.shapes.set(n, new Shape());
                }else
                    resul.shapes.set(n, this.get(n).copy());
            }
        }

        return resul;
    }

    /**
     * Overloaded method, equivalent to slice(true,indices)
     *
     * @param <T> Multishape subclass object
     * @param indices indices to slice (varargs)
     * @return A new multishape instance with the extracted shapes. The original
     * multishape object is altered as the extracted shapes becomes null shapes
     */
    public <T extends MultiShapeObject> T slice(Integer... indices) {
        return slice(true, indices);
    }

    /**
     * Gets an array of Shapes with the given indices. This method is used
     * mostly to combine with animations that accepts a varargs of MathObject
     *
     * @param indices
     * @return
     */
    public Shape[] getSubArray(int... indices) {
        Shape[] resul = new Shape[indices.length];
        int k = 0;
        for (int n : indices) {
            resul[k] = shapes.get(n);
            k++;
        }

        return resul;
    }

}
