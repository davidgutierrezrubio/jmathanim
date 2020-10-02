/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Animations.AffineTransform;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.JMColor;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.MathObjectDrawingProperties;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.updateableObjects.Updateable;
import java.util.HashSet;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class MathObject implements Drawable, Updateable, Stateable {

    //Implemented types
    public static final int OTHER = 0;
    public static final int POINT = 1;
    public static final int RECTANGLE = 2; //Includes square
    public static final int CIRCLE = 3;
    public static final int ELLIPSE = 4;
    public static final int ARC = 5;
    public static final int REGULAR_POLYGON = 6;
    public static final int GENERAL_POLYGON = 7;
    public static final int SEGMENT = 9;

    public MathObjectDrawingProperties mp;
    protected MathObjectDrawingProperties mpBackup;
    public String label = "";

    public boolean absoluteSize = false;

    private int objectType;
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
    private int layer = 0;

    public MathObject() {
        this(null);
    }

    public MathObject(MathObjectDrawingProperties prop) {
        mp = JMathAnimConfig.getConfig().getDefaultMP();//Default MP values
        mp.digestFrom(prop);//Copy all non-null values from prop
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
    public abstract <T extends MathObject> T moveTo(Vec coords);

    /**
     * Move object so that center is the given point
     *
     * @param <T> MathObject subclass
     * @param p Point with coordinates of new center
     * @return The same object, after moving
     */
    public <T extends MathObject> T moveTo(Point p) {
        return moveTo(p.v);
    }

    /**
     * Shift object with the given vector
     *
     * @param <T> MathObject subclass
     * @param shiftVector Amount of shifting
     * @return The same object, after shifting
     */
    public abstract <T extends MathObject> T shift(Vec shiftVector);

    /**
     * Scale from center of object (2D version)
     *
     * @param <T> MathObject subclass
     * @param x x-coordinate of shift vector
     * @param y y-coordinate of shift vector
     * @return The same object, after shifting
     */
    public <T extends MathObject> T shift(double x, double y) {
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
    public <T extends MathObject> T scale(double sx, double sy) {
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
    public <T extends MathObject> T scale(double s) {
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
    public <T extends MathObject> T scale(Point p, double sx, double sy) {
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
    public <T extends MathObject> T scale(double sx, double sy, double sz) {
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
        AffineTransform tr = AffineTransform.create2DScaleTransform(scaleCenter, sx, sy, sz);
        tr.applyTransform(this);
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
        AffineTransform tr = AffineTransform.create2DRotationTransform(center, angle);
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
     * Update all necessary componentes of this object to display properly This
     * should be called when any of its subobjects (sides, vertices...) changes
     */
    abstract public void prepareForNonLinearAnimation();

    abstract public void processAfterNonLinearAnimation();

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
        this.mpBackup = this.mp.copy();
    }

    @Override
    public void restoreState() {
        mp.copyFrom(mpBackup);
    }

    public MathObjectDrawingProperties getMp() {
        return mp;
    }

    public void setMp(MathObjectDrawingProperties _mp) {
        this.mp.copyFrom(mp);
    }

    public int getObjectType() {
        return objectType;
    }

    public void setObjectType(int objectType) {
        this.objectType = objectType;
    }

    //Convenience methods to set drawing parameters
    public <T extends MathObject> T drawColor(JMColor dc) {
        mp.drawColor.set(dc);
        return (T) this;
    }

    public <T extends MathObject> T fillColor(JMColor fc) {
        mp.fillColor.set(fc);
        return (T) this;
    }

    public <T extends MathObject> T drawAlpha(double alpha) {
        mp.drawColor.alpha = alpha;
        return (T) this;
    }

    public <T extends MathObject> T fillAlpha(double alpha) {
        mp.fillColor.alpha = alpha;
        return (T) this;
    }

    /**
     * Multiplies alpha Fill color by a scale
     *
     * @param t Scale to multiply alpha
     */
    public <T extends MathObject> T multFillAlpha(double t) {
        this.mp.fillColor.alpha *= t;
        return (T) this;
    }

    /**
     * Multiplies alph Draw color by a scale
     *
     * @param t Scale to multiply alpha
     */
    public <T extends MathObject> T multDrawAlpha(double t) {
        this.mp.drawColor.alpha *= t;
        return (T) this;
    }

    public <T extends MathObject> T thickness(double th) {
        mp.thickness = th;
        return (T) this;
    }

    public Point getAbsoluteAnchorPoint() {
        return Anchor.getAnchorPoint(this, absoluteAnchorType);
    }

    public void setAbsolutAnchorPoint(int anchor) {
        absoluteAnchorType = anchor;

    }

    public void setAbsoluteAnchorPoint(Point p) {
        this.absoluteAnchorPoint = p;
        absoluteAnchorType = Anchor.BY_POINT;

    }

    public void stackTo(MathObject obj, int anchorType) {
        Point B = Anchor.getAnchorPoint(obj, anchorType);
        Point A = Anchor.getAnchorPoint(this, Anchor.reverseAnchorPoint(anchorType));
        this.shift(A.to(B));
    }

    public <T extends MathObject> T stackTo(int anchorType) {
        return stackTo(anchorType, 0, 0);
    }

    public <T extends MathObject> T stackTo(int anchorType, double xMargin, double yMargin) {
        Point B = Anchor.getScreenAnchorPoint(anchorType, xMargin, yMargin);
        Point A = Anchor.getAnchorPoint(this, anchorType);
        return this.shift(A.to(B));
    }

    public void putAt(Point p, int anchorType) {
        putAt(p, anchorType, 0);
    }

    public <T extends MathObject> T putAt(Point p, int anchorType, double gap) {
        Point anchorPoint = Anchor.getAnchorPoint(this, anchorType, gap);
        return shift(anchorPoint.to(p));
    }

    public <T extends MathObject> T setAbsoluteSize() {
        absoluteSize = true;
        absoluteAnchorType = Anchor.BY_CENTER;//Default anchor
        return (T) this;
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
    public <T extends MathObject> T setLayer(int layer) {
        this.layer = layer;
        return (T) this;
    }

    /**
     * Returns the layer where this object belongs to
     *
     * @return The layer number
     */
    public int getLayer() {
        return layer;
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
}
