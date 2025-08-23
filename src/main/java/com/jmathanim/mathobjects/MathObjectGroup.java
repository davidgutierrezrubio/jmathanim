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

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Enum.AnchorType;
import com.jmathanim.Enum.InnerAnchorType;
import com.jmathanim.Enum.LayoutType;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Styling.MODrawPropertiesArray;
import com.jmathanim.Styling.Stylable;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.EmptyRect;
import com.jmathanim.Utils.Layouts.GroupLayout;
import com.jmathanim.Utils.Rect;
import com.jmathanim.jmathanim.JMathAnimScene;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class that manages sets of MathObjects. The objectes are not added to the scene when you add this object to the
 * scene. It acts as a container to perform easily bulk-operations
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class MathObjectGroup extends MathObject<MathObjectGroup> implements Iterable<MathObject<?>> {

    private final ArrayList<MathObject<?>> objects;
    private final HashMap<String, MathObject<?>> dict;
    MODrawPropertiesArray mpArray;

    public MathObjectGroup() {
        super();
        mpArray = new MODrawPropertiesArray();
        this.objects = new ArrayList<>();
        this.dict = new HashMap<>();
    }

    public MathObjectGroup(MathObject<?>... objects) {
        this(new ArrayList<>(Arrays.asList(objects)));
    }

    public MathObjectGroup(ArrayList<MathObject<?>> objects) {
        super();
        mpArray = new MODrawPropertiesArray();
        this.dict = new HashMap<>();
        this.objects = objects;
        for (MathObject<?> o : objects) {
            mpArray.add(o);
        }
    }

    public static MathObjectGroup make(MathObject<?>... objects) {
        return new MathObjectGroup(objects);
    }

    /**
     * Computes a MathObjectgroup with the same elements, divided en equally sized subgroups. The current group is
     * unaltered.
     *
     * @param size Size of subgroups. Last subgroup created may have less than this number. For example a group with 17
     *             elements with the division parameter 5 will return 3 subgroups of 5 elements and 1 subgroup of 2
     *             elements.
     * @return A new MathObjectGroup contanining other MathObjectGroup instance with the divided objects.
     */
    public MathObjectGroup divide(int size) {
        MathObjectGroup dividedGroup = MathObjectGroup.make();
        MathObjectGroup auxGroup = null;
        for (int n = 0; n < this.size(); n++) {
            if (n % size == 0) {
                auxGroup = MathObjectGroup.make();
                dividedGroup.add(auxGroup);
            }
            auxGroup.add(this.get(n));
        }
        return dividedGroup;
    }

    /**
     * Returns the MathObjectGroup flattened. If the group contains other group in nested levels, it flattens all
     * elements in one single group. The current group is unaltered.
     *
     * @return A new group with the elements flattened
     */
    public MathObjectGroup flatten() {
        return flatten(this);
    }

    private MathObjectGroup flatten(MathObjectGroup group) {
        MathObjectGroup resul = MathObjectGroup.make();
        for (MathObject<?> obj : group) {
            if (obj instanceof MathObjectGroup) {
                MathObjectGroup cc = flatten(((MathObjectGroup) obj));
                resul.addAll(cc.getObjects());
            } else {
                resul.add(obj);
            }
        }
        return resul;
    }

    /**
     * Add the objects to the MathObjectGroup
     *
     * @param objs A vararg of MathObjects
     * @return This MathObjectGroup
     */
    public MathObjectGroup add(MathObject<?>... objs) {
        for (MathObject<?> obj : objs) {
            if (obj != null) {
                objects.add(obj);
                mpArray.add(obj);
            }
        }
        return this;
    }

    /**
     * Add a object to the group and register it as a property with a given key, so it can be retrieved with
     * getProperty
     *
     * @param key Key Name
     * @param obj Object to add
     * @return This MathObjectGroup
     */
    public MathObjectGroup addWithKey(String key, MathObject<?> obj) {
        add(obj);
        dict.put(key, obj);
        return this;
    }

    public void add(int index, MathObject<?> element) {
        objects.add(index, element);
        mpArray.add(element);
    }

    public void addAll(Collection<? extends MathObject<?>> c) {
        objects.addAll(c);
        mpArray.getObjects().addAll(c);
    }

    public void addAll(int index, Collection<? extends MathObject<?>> c) {
        objects.addAll(index, c);
        mpArray.getObjects().addAll(c);
    }

    public void remove(MathObject<?> obj) {
        objects.remove(obj);
    }

    public void remove(String key) {
        if (dict.containsKey(key))
            objects.remove(dict.get(key));
    }

    @Override
    public MathObjectGroup applyAffineTransform(AffineJTransform tr) {
        for (MathObject<?> obj : objects) {
            obj.applyAffineTransform(tr);
        }
        tr.applyTransformsToDrawingProperties(this);
        return this;
    }

    public void clear() {
        objects.clear();
        mpArray.getObjects().clear();
    }

    @Override
    public MathObjectGroup copy() {
        MathObjectGroup copy = new MathObjectGroup();
        copy.getMp().copyFrom(getMp());
        for (MathObject<?> obj : this.getObjects()) {
            MathObject<?> copyObject = obj.copy();
            copy.add(copyObject);
            //If this object is registered with a key, register the copied one with the same key
            if (dict.containsValue(copyObject)) {
                for (String key : dict.keySet()) {
                    if (dict.get(key).equals(obj)) {
                        copy.dict.put(key, copyObject);
                    }
                }
            }
        }
        return copy;
    }

    @Override
    public void copyStateFrom(MathObject<?> obj) {
        super.copyStateFrom(obj);
        if (!(obj instanceof MathObjectGroup)) {
            return;
        }
        MathObjectGroup mg = (MathObjectGroup) obj;

       if (mg.size() == size()) {
            int n = 0;
            for (MathObject<?> o : getObjects()) {
                o.copyStateFrom(mg.get(n));
                n++;
            }
        } else {
            clear();
            for (MathObject<?> o : mg.getObjects()) {
                add(o.copy());
            }
        }
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r, Camera cam) {
        scene.markAsAlreadydrawn(this);
    }

    @Override
    public MathObjectGroup setCamera(Camera camera) {
        for (MathObject<?> obj : this) {
            obj.setCamera(camera);
        }
        return this;
    }

    /**
     * Overloaded method. Distribute evenly spaced objects in the group, horizontally or vertically. x-order or y-order
     * of objects is respected. Order of objects in MathObjectGroup is unaltered.
     *
     * @param orientation A value of enum Orientation (HORIZONTAL or VERTICAL)
     */
    public MathObjectGroup distribute(Orientation orientation) {
        return distribute(orientation, true);
    }

    /**
     * Distribute evenly spaced objects in the group, horizontally or vertically.
     *
     * @param orientation  A value of enum Orientation (HORIZONTAL or VERTICAL)
     * @param respectOrder If true, current x-order or y-order of objects is respected. Position of objects in the
     *                     MathObjectGroup is unaltered. A value of false in a horizontal distribute por example, always
     *                     puts the first element of the group in the left extreme.
     * @return This object
     */
    public MathObjectGroup distribute(Orientation orientation, boolean respectOrder) {
        ArrayList<MathObject<?>> objectsToDistribute = new ArrayList<>(getObjects());
        if (respectOrder) {
            if (orientation == Orientation.HORIZONTAL) {
                objectsToDistribute.sort(Comparator.comparingDouble(obj -> obj.getBoundingBox().xmin));
            } else {
                objectsToDistribute.sort(Comparator.comparingDouble(obj -> obj.getBoundingBox().ymin));
            }
        }
        if (size() < 2) {
            return this;//Nothing to do here
        }
        double left, right, dstCoord;
        double size = 0;
        double gap;
        Rect bb = getBoundingBox();
//Compute left and right (bottom and top) margins
//and sum of widths (heights)
        if (orientation == Orientation.HORIZONTAL) {
            left = bb.xmin;
            right = bb.xmax;
            for (MathObject<?> thi : this) {
                size += thi.getWidth();
            }
        } else {
            left = bb.ymin;
            right = bb.ymax;
            for (MathObject<?> thi : this) {
                size += thi.getHeight();
            }
        }
        dstCoord = left;//Starting coordinate (horizontal or vertical)
        //Compute gap
        gap = ((right - left) - size) / (size() - 1);

        //Move every object
        for (int i = 0; i < size(); i++) {
            MathObject<?> obj = objectsToDistribute.get(i);

            if (orientation == Orientation.HORIZONTAL) {
                obj.shift(dstCoord - obj.getBoundingBox().xmin, 0);
                dstCoord = obj.getBoundingBox().xmax + gap;
            } else {
                obj.shift(0, dstCoord - obj.getBoundingBox().ymin);
                dstCoord = obj.getBoundingBox().ymax + gap;
            }
        }
        return this;
    }

    /**
     * Gets the MathObject<?> stored at a given position
     *
     * @param index Index of the object
     * @return The MathObject
     */
    public MathObject<?> get(int index) {
        return objects.get(index);
    }

    public MathObject<?> get(String key) {
        if (dict.containsKey(key)) {
            return dict.get(key);
        } else {
            try {
                throw new Exception("Key " + key + " does not exists in MathObjectGroup " + this.objectLabel);
            } catch (Exception ex) {
                Logger.getLogger(MathObjectGroup.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    @Override
    protected Rect computeBoundingBox() {
        //If group is empty, returns an empty rect
        if (objects.isEmpty()) {
            return new EmptyRect();
        }

        Rect bbox = objects.get(0).getBoundingBox();
        for (MathObject<?> obj : objects) {
            bbox = Rect.union(bbox, obj.getBoundingBox());
        }
        return bbox;
    }

    public ArrayList<MathObject<?>> getObjects() {
        return objects;
    }

    public int indexOf(Object o) {
        return objects.indexOf(o);
    }

    @Override
    public Iterator<MathObject<?>> iterator() {
        return objects.iterator();
    }

    @Override
    public void restoreState() {
        mpArray.restoreState();
        for (MathObject<?> obj : objects) {
            obj.restoreState();
        }
    }

    @Override
    public void saveState() {
        mpArray.saveState();
        for (MathObject<?> obj : objects) {
            obj.saveState();
        }
    }

    @Override
    public MathObjectGroup setAbsoluteSize(AnchorType anchorType) {
        for (MathObject<?> obj : objects) {
            obj.setAbsoluteSize(anchorType);
        }
        return this;
    }

    public MathObjectGroup setLayout(GroupLayout layout) {
        layout.applyLayout(this);
        return this;
    }

    public MathObjectGroup setLayout(LayoutType layoutType, double gap) {
        return setLayout(null, layoutType, gap);
    }

    public MathObjectGroup setLayout(MathObject<?> corner, LayoutType layoutType, double gap) {
        AnchorType anchor = AnchorType.CENTER;

        switch (layoutType) {
            case CENTER:
                anchor = AnchorType.CENTER;
                gap = 0;
                break;
            case RIGHT:
                anchor = AnchorType.RIGHT;
                break;
            case LEFT:
                anchor = AnchorType.LEFT;
                break;
            case UPPER:
                anchor = AnchorType.UPPER;
                break;
            case LOWER:
                anchor = AnchorType.LOWER;
                break;
            case URIGHT:
                anchor = AnchorType.URIGHT;
                break;
            case ULEFT:
                anchor = AnchorType.ULEFT;
                break;
            case DRIGHT:
                anchor = AnchorType.DRIGHT;
                break;
            case DLEFT:
                anchor = AnchorType.DLEFT;
                break;
            case RUPPER:
                anchor = AnchorType.RUPPER;
                break;
            case LUPPER:
                anchor = AnchorType.LUPPER;
                break;
            case RLOWER:
                anchor = AnchorType.RLOWER;
                break;
            case LLOWER:
                anchor = AnchorType.LLOWER;
                break;
            case DIAG1:
                anchor = AnchorType.DIAG1;
                break;
            case DIAG2:
                anchor = AnchorType.DIAG2;
                break;
            case DIAG3:
                anchor = AnchorType.DIAG3;
                break;
            case DIAG4:
                anchor = AnchorType.DIAG4;
                break;
            default:
                JMathAnimScene.logger.error("Layout not recognized, reverting to CENTER");
                break;
        }
        if (corner != null) {
            objects.get(0).stackTo(corner, anchor, gap);
        }
        for (int n = 1; n < objects.size(); n++) {
            objects.get(n).stackTo(objects.get(n - 1), anchor, gap);
        }
        return this;

    }

    @Override
    public MathObjectGroup setRelativeSize() {
        for (MathObject<?> obj : objects) {
            obj.setRelativeSize();
        }
        return this;
    }

    public int size() {
        return objects.size();
    }

    /**
     * Returns an array of MathObject<?> with the contents of the group.
     *
     * @return The array
     */
    public MathObject<?>[] toArray() {
        return objects.toArray(new MathObject<?>[0]);
    }

    @Override
    public MathObjectGroup visible(boolean visible) {
        for (MathObject<?> obj : objects) {
            obj.visible(visible);
        }
        return this;
    }

    @Override
    public Stylable getMp() {
        return mpArray;
    }

    /**
     * Reverse the order of the elements in this group
     */
    public void reverse() {
        Collections.reverse(objects);
    }

    @Override
    public boolean isEmpty() {
        boolean resul = true;
        for (MathObject<?> thi : this) {
            resul = resul && thi.isEmpty();
        }
        return resul;

    }

    /**
     * Adjust gaps of all object so that bounding boxes are equal. Additional gaps are passed as parameters. Size of the
     * bounding box (before adding gaps) is computed as the maximum of the bounding boxes of group elements.
     *
     * @param anchorType How to align previous bounding box into the new one (CENTER, UPPER...)
     * @param rightGap   Right Gap to add.
     * @param upperGap   Upper Gap to add.
     * @param leftGap    Left Gap to add.
     * @param lowerGap   Lower Gap to add.
     * @return This group
     */
    public MathObjectGroup homogeneizeBoundingBoxes(InnerAnchorType anchorType, double upperGap, double rightGap, double lowerGap, double leftGap) {
        double hmax = 0;
        double wmax = 0;
        for (MathObject<?> ob : this) {//Compute max of widths and heights
            double w = ob.getWidth();
            double h = ob.getHeight();
            hmax = Math.max(hmax, h);
            wmax = Math.max(wmax, w);
        }

        homogeneizeBoundingBoxesTo(anchorType, wmax, hmax, upperGap, rightGap, lowerGap, leftGap);

        return this;
    }

    /**
     * Add gaps (negative if necessary) to every object so that all have the same width and height
     *
     * @param anchorType How to align previous bounding box into the new one (CENTER, UPPER...)
     * @param width      Desired width
     * @param height     Desired height
     * @param upperGap   Upper Gap to add.
     * @param rightGap   Right Gap to add.
     * @param lowerGap   Lower Gap to add.
     * @param leftGap    Left Gap to add.
     * @return This object
     */
    public MathObjectGroup homogeneizeBoundingBoxesTo(InnerAnchorType anchorType, double width, double height, double upperGap, double rightGap, double lowerGap, double leftGap) {
        for (MathObject<?> ob : this) {//Now add proper gaps
            double rGap = 0, lGap = 0, uGap = 0, loGap = 0;
            double w = ob.getWidth();
            double h = ob.getHeight();
            switch (anchorType) {
                case CENTER:
                    rGap = (width - w) / 2;
                    lGap = (width - w) / 2;
                    uGap = (height - h) / 2;
                    loGap = (height - h) / 2;
                    break;
                case RIGHT:
                    lGap = (width - w);
                    uGap = (height - h) / 2;
                    loGap = (height - h) / 2;
                    break;
                case LEFT:
                    rGap = (width - w);
                    uGap = (height - h) / 2;
                    loGap = (height - h) / 2;
                    break;
                case UPPER:
                    rGap = (width - w) / 2;
                    lGap = (width - w) / 2;
                    loGap = (height - h);
                    break;
                case LOWER:
                    rGap = (width - w) / 2;
                    lGap = (width - w) / 2;
                    uGap = (height - h);
                    break;
                case RUPPER:
                    lGap = (width - w);
                    loGap = (height - h);
                    break;
                case RLOWER:
                    lGap = (width - w);
                    uGap = (height - h);
                    break;
                case LLOWER:
                    rGap = (width - w);
                    uGap = (height - h);
                    break;
                case LUPPER:
                    rGap = (width - w);
                    loGap = (height - h);
                    break;
            }
            rGap += rightGap;
            lGap += leftGap;
            uGap += upperGap;
            loGap += lowerGap;

            ob.setGaps(uGap, rGap, loGap, lGap);
        }
        return this;
    }

    @Override
    public MathObjectGroup layer(int layer) {
        for (MathObject<?> thi : this) {
            thi.layer(layer);
        }
        return this;
    }

    @Override
    public String toString() {
        String toString = "MathObjectGroup[" + size() + "] ";
        if (size() < 5) {
            for (MathObject<?> obj : objects) {
                toString += "[" + obj.toString() + "] ";
            }
        }
        return toString;
    }

    /**
     * Orientation (for distribution method)
     */
    public enum Orientation {
        VERTICAL,
        HORIZONTAL
    }
}
