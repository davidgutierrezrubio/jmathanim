package com.jmathanim.mathobjects;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Enum.AnchorType;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Styling.MODrawPropertiesArray;
import com.jmathanim.Styling.PaintStyle;
import com.jmathanim.Utils.*;
import com.jmathanim.jmathanim.JMathAnimScene;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractMultiShapeObject<T extends AbstractMultiShapeObject<T>> extends MathObject<T> implements Iterable<Shape> {
    protected final MODrawPropertiesArray mpMultiShape;
    protected final ArrayList<Shape> shapes;
    public boolean isAddedToScene;

    protected AbstractMultiShapeObject() {
        this(new ArrayList<>());
    }

    protected AbstractMultiShapeObject(List<Shape> shapes) {
        super();
        isAddedToScene = false;
        this.shapes = new ArrayList<>();
        this.shapes.addAll(shapes);
        mpMultiShape = new MODrawPropertiesArray();
        for (MathObject sh : shapes) {
            mpMultiShape.add(sh);
        }
    }

    public boolean add(Shape e) {
        mpMultiShape.add(e);
        return shapes.add(e);
    }

    public Shape setShapeAt(int index, Shape element) {
        mpMultiShape.add(element);
        return shapes.set(index, element);

    }

    public void clearShapes() {
        shapes.clear();
        mpMultiShape.getObjects().clear();
    }

    @Override
    public T fillColor(PaintStyle fc) {
        for (Shape jmp : shapes) {
            jmp.fillColor(fc);
        }
        super.fillColor(fc);
        return (T) this;
    }

    @Override
    public T drawColor(PaintStyle dc) {
        for (Shape jmp : shapes) {
            jmp.drawColor(dc);
        }
        super.drawColor(dc);
        return (T) this;
    }

    @Override
    public void copyStateFrom(MathObject<?> obj) {
        super.copyStateFrom(obj);
        if (!(obj instanceof AbstractMultiShapeObject<?>)) {
            return;
        }

        AbstractMultiShapeObject<?> msh = (AbstractMultiShapeObject<?>) obj;
        int n = 0;
        //Assuming this shape and obj has the same number of items
        if (size() == msh.size()) {
            for (Shape s : shapes) {
                s.copyStateFrom(msh.get(n));
                n++;
            }
        } else {//If there is discrepancy, turn it off and on!
            shapes.clear();
            for (Shape sh : msh) {
                add(sh.copy());
            }
        }
    }

    @Override
    public T setAbsoluteSize(AnchorType anchorType) {
        super.setAbsoluteSize(anchorType);
        Vec p = Anchor.getAnchorPoint(this, anchorType);
        for (Shape sh : shapes) {
            sh.setAbsoluteSize(p);
        }
        return (T) this;
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r, Camera cam) {
        if (isVisible()) {
            for (Shape jmp : shapes) {
                //Store camera and temporary use MultiShapeObject camera
                if ((jmp.isVisible()) && (!scene.isAlreadydrawn(jmp))) {
                    if (absoluteSize) {
                        r.drawAbsoluteCopy(jmp, getAbsoluteAnchor());// TODO: This doesnt work for overrided methods
                        // (e.g.: line)
                    } else {
                        jmp.draw(scene, r, cam);
//                    if (isShowDebugText()) {
//                        r.debugText("" + n, jmp.getCenter().v);
//                    }
                    }
                }
            }
        }
        scene.markAsAlreadydrawn(this);
    }

    @Override
    protected Rect computeBoundingBox() {
        Rect resul = null;
        for (Shape jmp : shapes) {
            if (!jmp.isEmpty()) {
                resul = Rect.union(resul, jmp.getBoundingBox());
            }
        }

        if (resul == null) {
            return new EmptyRect();
        } else {
            return resul;
        }
    }

    public Shape get(int n) {
        return shapes.get(n);
    }

    @Override
    public void restoreState() {
        super.restoreState();
        getMp().restoreState();
        for (Shape o : shapes) {
            o.restoreState();
        }
    }

    @Override
    public void saveState() {
        super.saveState();
        getMp().saveState();
        for (Shape o : shapes) {
            o.saveState();
        }
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

    /**
     * Align with another MultiShape so that the center of one of its shapes is aligned with the center of the shape of
     * the other Multishape. This is generally used for LaTeXMathObjects to align two equation by their equal sign
     *
     * @param index            Shape index of the shape to align
     * @param otherObject      The other multishape object
     * @param indexOtherObject Index of the shape of the other multishape to align with
     * @return This object
     */
    public T alignCenter(int index, AbstractMultiShapeObject<?> otherObject, int indexOtherObject) {
        shift(this.get(index).getCenter().to(otherObject.get(indexOtherObject).getCenter()));
        return (T) this;
    }

    /**
     * Returns true if the object contains no shapes, or all the shapes contained are empty.
     *
     * @return if empty, false otherwise
     */
    @Override
    public boolean isEmpty() {
        boolean resul = false;
        for (Shape sh : shapes) {
            resul = resul | sh.isEmpty();
        }
        return resul;
    }

    /**
     * Extracts a part of a MultiShape, given by a set of indices.Indices are unaltered, so shape 5 after slicing is
     * still shape 5.
     *
     * @param delete  If true, sliced shapes will be removed from the original and replaced with empty shapes.
     * @param indices indices to slice (varargs)
     * @return A new multishape instance with the extracted shapes.
     */
    public T slice(boolean delete, int... indices) {
        List<Integer> list = Arrays.stream(indices).boxed().collect(Collectors.toList());//Arrays.asList(indices);
        T resul = (T) this.copy();
        //Populate the new MultiShape with n empty shapes
        for (int n = 0; n < resul.size(); n++) {
            resul.setShapeAt(n, new Shape());
        }
        for (int n = 0; n < this.shapes.size(); n++) {
            if (list.contains(n)) {
                final Shape copy = this.get(n).copy();
                resul.setShapeAt(n, copy);
                if (delete) {// if this index is marked for extraction...
                    this.mpMultiShape.remove(this.get(n));
                    this.setShapeAt(n, new Shape());
                }

            }
        }

        return resul;
    }

    /**
     * Overloaded method, equivalent to slice(true,indices)
     *
     * @param indices indices to slice (varargs)
     * @return A new multishape instance with the extracted shapes. The original multishape object is altered as the
     * extracted shapes becomes null shapes
     */
    public T slice(int... indices) {
        return slice(true, indices);
    }

    /**
     * Gets an array of Shapes with the given indices. This method is used mostly to combine with animations that
     * accepts a varargs of MathObject
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

    /**
     * Gets a MultiShapeObject with the specified shapes of this object. The shapes are referenced.
     *
     * @param indices Indices of the shapes
     * @return A new MultiShapeObject with the specified shapes
     */
    public MultiShapeObject getSubMultiShape(int... indices) {
        MultiShapeObject resul = MultiShapeObject.make();
        resul.getMp().copyFrom(this.getMp());
        for (int n : indices) {
            resul.add(shapes.get(n));
        }
        return resul;
    }

    @Override
    public T applyAffineTransform(AffineJTransform tr) {
        for (Shape sh : shapes) {
            sh.applyAffineTransform(tr);
        }

        tr.applyTransformsToDrawingProperties(this);
        return (T) this;
    }

    @Override
    public MODrawPropertiesArray getMp() {
        return mpMultiShape;
    }

    public boolean containsPoint(Point p) {
        return containsPoint(p.v);
    }

    public boolean containsPoint(Vec v) {
        for (Shape sh : shapes) {
            if (sh.containsPoint(v)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns an array of MathObject with the contents of the group.
     *
     * @return The array
     */
    public Shape[] toArray() {
        return shapes.toArray(new Shape[0]);
    }

    /**
     * Returns a Shape object with all shapes of this MultiShapeObject merged. Drawing style is copied from general
     * drawing style from this object.
     *
     * @param connect If true, connect the ending of a shape with the beginning of the next one by a straight line.
     * @param close   If true, closes the resulting shape.
     * @return The Shape object created.
     */
    public Shape merge(boolean connect, boolean close) {
        Shape resul = new Shape();
        for (Shape sht : this) {
            Shape sh = sht.copy();
            sh.getPath().openPath();
            resul.merge(sh, connect, false);
        }
        if (close) {
            resul.getPath().closePath();
        }
        resul.getMp().copyFrom(this.getMp());
        return resul;
    }

    @Override
    public String toString() {
        return "MultiShape " + objectLabel + "(" + size() + " elements)";
    }

    @Override
    public T setCamera(Camera camera) {
        super.setCamera(camera);
        for (Shape sh : this) {
            sh.setCamera(camera);
        }
        return (T) this;
    }
}
