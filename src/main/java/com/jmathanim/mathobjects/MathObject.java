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

import com.jmathanim.Animations.AffineJTransform;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.JMColor;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.MODrawProperties;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MOProperties.MathObjectAttributes;
import com.jmathanim.mathobjects.updateableObjects.Updateable;
import java.util.HashSet;
import javafx.scene.shape.StrokeLineCap;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class MathObject implements Drawable, Updateable, Stateable {

    public enum MathObjectType {
        OTHER, POINT, RECTANGLE, CIRCLE, ELLIPSE,
        ARC, REGULAR_POLYGON, GENERAL_POLYGON, SEGMENT,
        LINE, MULTISHAPE, LATEX_MULTISHAPE, SVG,
        LATEX_SHAPE, FUNCTION_GRAPH
    }
//    //Implemented types
//    public static final int OTHER = 0;
//    public static final int POINT = 1;
//    public static final int RECTANGLE = 2; //Includes square
//    public static final int CIRCLE = 3;
//    public static final int ELLIPSE = 4;
//    public static final int ARC = 5;
//    public static final int REGULAR_POLYGON = 6;
//    public static final int GENERAL_POLYGON = 7;
//    public static final int SEGMENT = 9;
//    public static final int LINE = 10;
//    public static final int MULTISHAPE = 11;
//    public static final int LATEX_MULTISHAPE = 11;
//    public static final int SVG = 12;
//    public static final int LATEX_SHAPE = 13;
//    public static final int FUNCTION_GRAPH = 14;

    public MODrawProperties mp;
    public String label = "";

    public MathObjectAttributes attrs;

    public boolean absoluteSize = false;

    private MathObjectType objectType;
    /**
     * Scenes where this object belongs.
     *
     */
    private HashSet<JMathAnimScene> scenes;
    public boolean visible = true;
    /**
     * This parameter specifies the amount of object to be drawn 0=none,
     * 1/2=draw half
     */
//    protected double drawParam;

    public int updateLevel;
//    private Point anchorPoint;
    public Point absoluteAnchorPoint;
    private int absoluteAnchorType = Anchor.BY_CENTER;

    public MathObject() {
        this(null);
    }

    public MathObject(MODrawProperties prop) {
        mp = JMathAnimConfig.getConfig().getDefaultMP();//Default MP values
        mp.copyFrom(prop);//Copy all non-null values from prop
        scenes = new HashSet<>();
        updateLevel = 0;
    }

    /**
     * Return center of object. Implementation depends on type of MathObject
     *
     * @return Vec object with center
     */
    public abstract Point getCenter();

    /**
     * Move object so that center is the given coords
     *
     * @param <T> MathObject subclass
     * @param coords Vec with coordinates of new center
     * @return The same object, after moving
     */
    public final <T extends MathObject> T moveTo(Vec coords) {
        return this.moveTo(Point.at(coords.x, coords.y));
    }

    /**
     * Move object so that center is the given point
     *
     * @param <T> MathObject subclass
     * @param p Point with coordinates of new center
     * @return The same object, after moving
     */
    public <T extends MathObject> T moveTo(Point p) {
        putAt(p, Anchor.BY_CENTER);
        return (T) this;
    }

    /**
     * Shift object with the given vector
     *
     * @param <T> MathObject subclass
     * @param shiftVector Amount of shifting
     * @return The same object, after shifting
     */
    public <T extends MathObject> T shift(Vec shiftVector) {
        AffineJTransform tr = AffineJTransform.createTranslationTransform(shiftVector);
        tr.applyTransform(this);
        return (T) this;
    }

    /**
     * Scale from center of object (2D version)
     *
     * @param <T> MathObject subclass
     * @param x x-coordinate of shift vector
     * @param y y-coordinate of shift vector
     * @return The same object, after shifting
     */
    public final <T extends MathObject> T shift(double x, double y) {
        return shift(new Vec(x, y));
    }

    /**
     * Scale from center of object (2D version)
     *
     * @param <T> MathObject subclass
     * @param sx x-scale factor
     * @param sy y-scale factor
     * @return The same object, after scaling
     */
    public final <T extends MathObject> T scale(double sx, double sy) {
        scale(getCenter(), sx, sy);
        return (T) this;
    }

    /**
     * Scale from center of object (2D version) in a uniform scale
     *
     * @param <T> MathObject subclass
     * @param s scale factor
     * @return The same object, after scaling
     */
    public final <T extends MathObject> T scale(double s) {
        return scale(getCenter(), s, s);
    }

    /**
     * Scale from a given center (2D version)
     *
     * @param <T> MathObject subclass
     * @param p Scale center
     * @param sx x-scale factor
     * @param sy y-scale factor
     * @return The same object, after scaling
     */
    public final <T extends MathObject> T scale(Point p, double sx, double sy) {
        return scale(p, sx, sy, 1);
    }

    /**
     * Scale from the center of object (3D version)
     *
     * @param <T> MathObject subclass
     * @param sx x-scale factor
     * @param sy y-scale factor
     * @param sz z-scale factor
     * @return The same object, after scaling
     */
    public final <T extends MathObject> T scale(double sx, double sy, double sz) {
        scale(getBoundingBox().getCenter(), sx, sy, sz);
        return (T) this;
    }

    /**
     * Scale from a given center (3D version)
     *
     * @param <T> MathObject subclass
     * @param scaleCenter Scale center
     * @param sx x-scale factor
     * @param sy y-scale factor
     * @param sz z-scale factor
     * @return The same object, after scaling
     */
    public <T extends MathObject> T scale(Point scaleCenter, double sx, double sy, double sz) {
        AffineJTransform tr = AffineJTransform.createScaleTransform(scaleCenter, sx, sy, sz);
        tr.applyTransform(this);
        return (T) this;
    }

    /**
     * Scale object so that has a specified height. Scaled is done around its
     * center.
     *
     * @param <T> Object type
     * @param h Desired height
     * @return The same object
     */
    public <T extends MathObject> T setHeight(double h) {
        scale(h / this.getBoundingBox().getHeight());
        return (T) this;
    }

    /**
     * Scale object so that has a specified width. Scaled is done around its
     * center.
     *
     * @param <T> Object type
     * @param w Desired width
     * @return The same object
     */
    public <T extends MathObject> T setWidth(double w) {
        scale(w / this.getBoundingBox().getWidth());
        return (T) this;
    }

    public final <T extends MathObject> T center() {
        this.stackToScreen(Anchor.BY_CENTER);
        return (T) this;
    }

    /**
     * Performs a 2D-Rotation of the MathObject around the given rotation center
     *
     * @param <T> MathObject subclass
     * @param center Rotation center
     * @param angle Angle, in radism
     * @return The same object, after rotating
     */
    public <T extends MathObject> T rotate(Point center, double angle) {
        AffineJTransform tr = AffineJTransform.create2DRotationTransform(center, angle);
        tr.applyTransform(this);
        return (T) this;
    }

    /**
     * Performs a 2D-Rotation of the MathObject around the center of the object
     *
     * @param <T> MathObject subclass
     * @param angle Angle, in radism
     * @return The same object, after rotating
     */
    public <T extends MathObject> T rotate(double angle) {
        return rotate(this.getCenter(), angle);
    }

    /**
     * Returns a copy of the object
     *
     * @param <T> MathObject subclass
     * @return copy of object, with identical properties. A deep copy is
     * performed.
     */
    abstract public <T extends MathObject> T copy();


    /**
     * Add the given scene to the collection of scenes which this object has
     * been added to.
     *
     * @param scene Scene
     */
    public void addScene(JMathAnimScene scene) {
        scenes.add(scene);
//        for (MathObject mob:cousins)
//        {
//            scen.add(mob);
//        }
    }

    /**
     * Remove the given scene from the collection of scenes which this object
     * has been added to.
     *
     * @param scene Scene
     */
    public void removeScene(JMathAnimScene scene) {
        scenes.remove(scene);
//         for (MathObject mob:descendent)
//        {
//            mob.removeScene(scen);
//        }
    }

    /**
     * Returns the Bounding box with limits of the MathObject
     *
     * @return A Rect with (xmin,ymin,xmax,ymax)
     */
    public abstract Rect getBoundingBox();

    public void setAlpha(double t) {
        drawAlpha(t);
        fillAlpha(t);
    }

    public abstract void registerChildrenToBeUpdated(JMathAnimScene scene);

    public abstract void unregisterChildrenToBeUpdated(JMathAnimScene scene);

    @Override
    public int getUpdateLevel() {
        return updateLevel;
    }

    @Override
    public void saveState() {
        mp.saveState();
        if (this.attrs != null) {
            this.attrs.saveState();
        }
    }

    @Override
    public void restoreState() {
        mp.restoreState();
        if (this.attrs != null) {
            this.attrs.restoreState();
        }
    }

    public MODrawProperties getMp() {
        return mp;
    }

    public <T extends MathObject> T setMp(MODrawProperties newMp) {
        this.mp.copyFrom(newMp);
        return (T) this;
    }

    public MathObjectType getObjectType() {
        return objectType;
    }

    public final <T extends MathObject> T setObjectType(MathObjectType objectType) {
        this.objectType = objectType;
        return (T) this;
    }

    //Convenience methods to set drawing parameters
    public <T extends MathObject> T drawColor(JMColor dc) {
        mp.getDrawColor().copyFrom(dc);
        return (T) this;
    }

    public <T extends MathObject> T drawColor(String str) {
        drawColor(JMColor.parse(str));
        return (T) this;
    }

    public <T extends MathObject> T fillColor(JMColor fc) {
        mp.getFillColor().copyFrom(fc);
        return (T) this;
    }

    public <T extends MathObject> T fillColor(String str) {
        fillColor(JMColor.parse(str));
        return (T) this;
    }

    public <T extends MathObject> T drawAlpha(double alpha) {
        mp.getDrawColor().alpha = alpha;
        return (T) this;
    }

    public <T extends MathObject> T fillAlpha(double alpha) {
        mp.getFillColor().alpha = alpha;
        return (T) this;
    }

    /**
     * Multiplies alpha Fill color by a scale
     *
     * @param <T> MathObject type
     * @param t Scale to multiply alpha
     * @return This object
     */
    public <T extends MathObject> T multFillAlpha(double t) {
        this.mp.getFillColor().alpha *= t;
        return (T) this;
    }

    /**
     * Multiplies alph Draw color by a scale
     *
     * @param <T> MathObject type
     * @param t Scale to multiply alpha
     * @return This object
     */
    public <T extends MathObject> T multDrawAlpha(double t) {
        this.mp.getDrawColor().alpha *= t;
        return (T) this;
    }

    public <T extends MathObject> T thickness(double th) {
        mp.thickness = th;
        return (T) this;
    }

    public <T extends MathObject> T dashStyle(MODrawProperties.DashStyle dst) {
        mp.dashStyle = dst;
        return (T) this;
    }

    public <T extends MathObject> T visible(boolean visible) {
        this.visible = visible;
        return (T) this;
    }

    public Point getAbsoluteAnchor() {
        return Anchor.getAnchorPoint(this, absoluteAnchorType);
    }

    public Point getAbsoluteAnchorPoint() {
        return absoluteAnchorPoint;
    }

    public void setAbsoluteAnchorPoint(Point p) {
        this.absoluteAnchorPoint = p;
        absoluteAnchorType = Anchor.BY_POINT;

    }

    public final <T extends MathObject> T stackTo(MathObject obj, int anchorType) {
        return stackTo(obj, anchorType, 0);
    }

    public <T extends MathObject> T stackTo(MathObject obj, int anchorType, double gap) {
        Point B = Anchor.getAnchorPoint(obj, anchorType, gap);
        Point A = Anchor.getAnchorPoint(this, Anchor.reverseAnchorPoint(anchorType));
        this.shift(A.to(B));
        return (T) this;
    }

    /**
     * Stack the object to the given anchor, relative to the current camera view
     *
     * @param <T> Mathobject to stack
     * @param anchorType {@link Anchor} type
     * @return The current object
     */
    public final <T extends MathObject> T stackToScreen(int anchorType) {
        return stackToScreen(anchorType, 0, 0);
    }

    /**
     * Stack the object to the given anchor, relative to the current camera
     * view, applying the specified margins.
     *
     * @param <T> Mathobject to stack
     * @param anchorType {@link Anchor} type
     * @param xMargin x margin
     * @param yMargin y margin
     * @return The current object
     */
    public <T extends MathObject> T stackToScreen(int anchorType, double xMargin, double yMargin) {
        Point B = Anchor.getScreenAnchorPoint(anchorType, xMargin, yMargin);
        Point A = Anchor.getAnchorPoint(this, anchorType);
        return this.shift(A.to(B));
    }

    public final <T extends MathObject> T putAt(Point p, int anchorType) {
        return putAt(p, anchorType, 0);
    }

    public <T extends MathObject> T putAt(Point p, int anchorType, double gap) {
        Point anchorPoint = Anchor.getAnchorPoint(this, anchorType, gap);
        return shift(anchorPoint.to(p));
    }

    public <T extends MathObject> T setAbsoluteSize() {
        return setAbsoluteSize(Anchor.BY_CENTER);
    }

    public <T extends MathObject> T setAbsoluteSize(int anchorType) {
        absoluteSize = true;
        absoluteAnchorType = anchorType;
        return (T) this;
    }

    public <T extends MathObject> T setRelativeSize() {
        absoluteSize = false;
        return (T) this;
    }

    /**
     * Sets the layer where this object belongs. Lower layers means that the
     * object will be drawed first and appear under other objects. The number
     * can be any integer
     *
     * @param <T> MathObject subclass
     * @param layer Layer number
     * @return The object
     */
    public <T extends MathObject> T layer(int layer) {
        this.mp.setLayer(layer);
        return (T) this;
    }

    /**
     * Returns the layer where this object belongs to
     *
     * @return The layer number
     */
    public int getLayer() {
        return mp.getLayer();
    }

    /**
     * Sets the given style, as defined in config files. If no such style
     * exists, there is no effect, apart from a warning message
     *
     * @param <T> MathObject subclass
     * @param name Name of the style being applied
     * @return The object
     */
    public <T extends MathObject> T style(String name) {
        mp.loadFromStyle(name);
        return (T) this;
    }

    public <T extends MathObject> T linecap(StrokeLineCap strokeLineCap) {
        this.mp.linecap = strokeLineCap;
        return (T) this;
    }

    public void interpolateMPFrom(MODrawProperties mpDst, double alpha) {
        this.mp.interpolateFrom(this.mp, mpDst, alpha);
    }
      public <T extends MathObject> T fillWithDrawColor(boolean fcd) {
        this.mp.setFillColorIsDrawColor(fcd);
        return (T) this;
    }
}
