package com.jmathanim.MathObjects;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Enum.AnchorType;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Styling.DrawStylePropertiesObjectsArray;
import com.jmathanim.Styling.PaintStyle;
import com.jmathanim.Utils.*;
import com.jmathanim.jmathanim.JMathAnimScene;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

//public abstract class
//AbstractMultiShapeObject<T extends AbstractShape<T>>
//        extends MathObject<T>
//        implements Iterable<AbstractShape<T>> {

public abstract class AbstractMultiShapeObject<
        S extends AbstractMultiShapeObject<S, T>,
        T extends AbstractShape<T>>
        extends MathObject<S> implements Iterable<T>,hasShapes {


    protected final DrawStylePropertiesObjectsArray mpMultiShape;
    protected final ArrayList<T> shapes;
    private final Class<T> clazz;
    public boolean isAddedToScene;


    protected AbstractMultiShapeObject(Class<T> clazz) {
        this(clazz, new ArrayList<>());
    }

    protected AbstractMultiShapeObject(Class<T> clazz, List<T> shapes) {
        super();
        this.clazz = clazz;
        isAddedToScene = false;
        this.shapes = new ArrayList<>();
        mpMultiShape = new DrawStylePropertiesObjectsArray();
//        addDependency(mpMultiShape);

        for (T sh : shapes) {
            add(sh);
        }
    }

    public boolean add(T element) {
        mpMultiShape.add(element);
//        addDependency(element);
        return shapes.add(element);
    }


    abstract protected T createEmptyShapeAt(int index);

    public T setShapeAt(int index, T element) {
        mpMultiShape.add(element);
        addDependency(element);
        return shapes.set(index, element);

    }

    public void clearShapes() {
        for (AbstractShape<?> sh : shapes) {
            removeDependency(sh);
        }
        shapes.clear();
        mpMultiShape.getObjects().clear();
    }

    @Override
    public S fillColor(PaintStyle fc) {
        changeVersionAndMarkDirty();
        for (T jmp : shapes) {
            jmp.fillColor(fc);
        }
        super.fillColor(fc);
        return (S) this;
    }

    @Override
    public S drawColor(PaintStyle<?> dc) {
        changeVersionAndMarkDirty();
        for (AbstractShape<?> jmp : shapes) {
            jmp.drawColor(dc);
        }
        super.drawColor(dc);
        return (S) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void copyStateFrom(Stateable obj) {
        if (!(obj instanceof AbstractMultiShapeObject<?, ?>)) return;
        AbstractMultiShapeObject<?, ?> msh = (AbstractMultiShapeObject<S, T>) obj;
        super.copyStateFrom(msh);
        int n = 0;
        //Assuming this shape and obj has the same number of items
        if (size() == msh.size()) {
            for (T s : shapes) {
                s.copyStateFrom(msh.get(n));
                n++;
            }
        } else {//If there is discrepancy, turn it off and on!
            shapes.clear();
            for (AbstractShape<?> sh : msh.shapes) {
                T sh2 = (T) sh;
                T copy = sh2.copy();
                add(copy);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public S setAbsoluteSize(AnchorType anchorType) {
        super.setAbsoluteSize(anchorType);
        Vec p = Anchor.getAnchorPoint(this, anchorType);
        for (T sh : shapes) {
            sh.setAbsoluteSize(p);
        }
        return (S) this;
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r, Camera cam) {
        if (isVisible()) {
            for (T jmp : shapes) {
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
    public Rect computeBoundingBox() {
        Rect resul = EmptyRect.make();
        for (T jmp : shapes) {
            if (!jmp.isEmpty()) {
                resul = Rect.union(resul, jmp.getBoundingBox());
            }
        }
        return resul;
    }

    public T get(int n) {
        return shapes.get(n);
    }


    public ArrayList<T> getShapes() {
        return shapes;
    }

    @Override
    public Iterator<T> iterator() {
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
    @SuppressWarnings("unchecked")
    public S alignCenter(int index, AbstractMultiShapeObject<?, ?> otherObject, int indexOtherObject) {
        shift(this.get(index).getCenter().to(otherObject.get(indexOtherObject).getCenter()));
        return (S) this;
    }

    /**
     * Returns true if the object contains no shapes, or all the shapes contained are empty.
     *
     * @return if empty, false otherwise
     */
    @Override
    public boolean isEmpty() {
        if (size() == 0) return true;
        boolean resul = false;
        for (AbstractShape<?> sh : shapes) {
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
    public S slice(boolean delete, int... indices) {
        List<Integer> list = Arrays.stream(indices).boxed().collect(Collectors.toList());//Arrays.asList(indices);
        S resul = (S) this.copy();
        resul.clearShapes();
        int size = size();
        //Populate the new MultiShape with n empty shapes
//        for (int n = 0; n < size; n++) {
//            resul.createEmptyShapeAt(n);
//        }
        for (int n = 0; n < size; n++) {
            if (list.contains(n)) {
                final T copy = this.get(n).copy();
                resul.add(copy);
                if (delete) {// if this index is marked for extraction...
                    this.mpMultiShape.remove(this.get(n));
                    this.createEmptyShapeAt(n);
                }
            } else {
                resul.createEmptyShapeAt(n);
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
    public S slice(int... indices) {
        return slice(true, indices);
    }

    /**
     * Gets an array of Shapes with the given indices. This method is used mostly to combine with animations that
     * accepts a varargs of MathObject
     *
     * @param indices Indices to extract
     * @return An array of shapes
     */
    public T[] getSubArray(int... indices) {
        @SuppressWarnings("unchecked")
        T[] resul = (T[]) java.lang.reflect.Array.newInstance(clazz, indices.length);
        int k = 0;
        for (int n : indices) {
            resul[k] = (T) shapes.get(n);
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
    @SuppressWarnings("unchecked")
    public S getSelectedIndices(int... indices) {
        S resul = makeNewEmptyInstance();
        resul.getMp().copyFrom(this.getMp());
        for (int n : indices) {
            resul.add(shapes.get(n));
        }
        return (S) resul;
    }

    @SuppressWarnings("unchecked")
    @Override
    public S applyAffineTransform(AffineJTransform affineJTransform) {
        for (T sh : shapes) {
            sh.getPath().applyAffineTransform(affineJTransform);
        }
        affineJTransform.applyTransformsToDrawingProperties(this);
        return (S) this;
    }

    @Override
    public DrawStylePropertiesObjectsArray getMp() {
        return mpMultiShape;
    }

    public boolean containsPoint(Point p) {
        return containsPoint(p.v);
    }

    public boolean containsPoint(Vec v) {
        for (T sh : shapes) {
            if (sh.containsPoint(v)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void performMathObjectUpdateActions() {

    }

    /**
     * Returns an array of MathObject with the contents of the group.
     *
     * @return The array
     */
    @Override
    public T[] toShapesArray() {
        @SuppressWarnings("unchecked")
        T[] arr = (T[]) java.lang.reflect.Array.newInstance(clazz, shapes.size());
        return shapes.toArray(arr);
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
        for (T sht : this) {
            T sh = sht.copy();
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

    @SuppressWarnings("unchecked")
    @Override
    public S setCamera(Camera camera) {
        super.setCamera(camera);
        for (T sh : this) {
            sh.setCamera(camera);
        }
        return (S) this;
    }

    @Override
    public boolean needsUpdate() {
        newLastMaxDependencyVersion = lastCleanedDepsVersionSum;
//        if (dirty) return true;
//        return newLastMaxDependencyVersion != lastCleanedDepsVersionSum;
        return dirty;
    }

    @Override
    public void markClean() {
        long v=0;
        for (int i = 0; i < shapes.size(); i++) {
            T t = shapes.get(i);
            t.markClean();
            v = t.getVersion();
            if (v>newLastMaxDependencyVersion) newLastMaxDependencyVersion=v;
        }
        super.markClean();
    }

    public abstract S makeNewEmptyInstance();

}
