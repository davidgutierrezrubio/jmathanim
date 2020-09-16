/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.MathObjectDrawingProperties;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.updateableObjects.Updateable;
import java.awt.Color;
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

    public static final int SLICE_SIMPLE = 1;
    public static final int SLICE_DOUBLE = 2;
    public static final int SLICE_FOUR = 3;

    //Anchor types
    public static final int ANCHOR_BY_POINT = 1;
    public static final int ANCHOR_BY_CENTER = 2;

    public static final int ANCHOR_LEFT = 3;
    public static final int ANCHOR_RIGHT = 4;
    public static final int ANCHOR_UPPER = 5;
    public static final int ANCHOR_LOWER = 6;

    public static final int ANCHOR_UL = 7;
    public static final int ANCHOR_UR = 8;
    public static final int ANCHOR_DL = 9;
    public static final int ANCHOR_DR = 10;

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
    public boolean visible;
    /**
     * This parameter specifies the amount of object to be drawn 0=none,
     * 1/2=draw half
     */
//    protected double drawParam;

    /**
     * Mathobjects dependent of this. These should be updated with its own
     * method when this object changes. This is designed for Objects with its
     * specific dependency function (MiddlePoint for example)
     */
    public final HashSet<Updateable> dependent;

    /**
     * MathObjects children of this (for example: Polygon has Point as vertices)
     */
    public final HashSet<MathObject> children;

    public int updateLevel;
//    private Point anchorPoint;
    public Point absoluteAnchorPoint;
    private int absoluteAnchorType=ANCHOR_BY_CENTER;

//    /**
//     * Mathobjects which this is dependent from. This object should be updated4
//     * when any of this list changes.
//     */
//    public final HashSet<MathObject> ascendent;
//    public final HashSet<MathObject> cousins;
    public MathObject() {
        this(null);
    }

    public MathObject(MathObjectDrawingProperties prop) {
        mp = JMathAnimConfig.getDefaultMP();//Default MP values
        mp.digestFrom(prop);//Copy all non-null values from prop
//        ascendent=new HashSet<>();
        dependent = new HashSet<>();
        children = new HashSet<>();
//        cousins=new HashSet<>();
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
     * @param coords Vec with coordinates of new center
     */
    public abstract void moveTo(Vec coords);

    public void moveTo(Point p) {
        moveTo(p.v);
    }

    /**
     * Shift object with the given vector
     *
     * @param shiftVector
     */
    public abstract void shift(Vec shiftVector);

    public void shift(double x, double y) {
        shift(new Vec(x, y));
    }

    /**
     * Scale from center of object (2D version)
     *
     * @param <T>
     * @param sx
     * @param sy
     */
    public <T extends MathObject> T scale(double sx, double sy) {
        scale(getCenter(), sx, sy);
        return (T) this;
    }

    /**
     * Scale from center of object (2D version)
     *
     * @param <T>
     * @param p
     * @param sx
     * @param sy
     * @return
     */
    public <T extends MathObject> T scale(Point p, double sx, double sy) {
        scale(p, sx, sy, 1);
        return (T) this;
    }

    /**
     * Scale from center of object (3D version)
     *
     * @param sx
     * @param sy
     * @param sz
     */
    public void scale(double sx, double sy, double sz) {
        scale(getBoundingBox().getCenter(), sx, sy, sz);
    }

    public abstract void scale(Point scaleCenter, double sx, double sy, double sz);

    /**
     * Returns a copy of the object
     *
     * @param <T>
     * @return copy of object, with identical properties
     */
    abstract public <T extends MathObject> T copy();

    /**
     * Update all necessary componentes of this object to display properly This
     * should be called when any of its subobjects (sides, vertices...) changes
     */
    abstract public void prepareForNonLinearAnimation();

    abstract public void processAfterNonLinearAnimation();

    public void updateDependents() {
        HashSet<Updateable> desC = (HashSet<Updateable>) dependent.clone();
        for (Updateable mob : desC) {
            mob.update();
        }
    }

    public void addScene(JMathAnimScene scen) {
        scenes.add(scen);
//        for (MathObject mob:cousins)
//        {
//            scen.add(mob);
//        }
    }

    public void removeScene(JMathAnimScene scen) {
        scenes.remove(scen);
//         for (MathObject mob:descendent)
//        {
//            mob.removeScene(scen);
//        }
    }

    public void addObjectToScene(MathObject mob) {
        for (JMathAnimScene sce : scenes) {
            mob.addScene(sce);
        }
    }

    /**
     * Sets the drawing parameter. This method alters the drawing parameters of
     * the MathObject so that it displays only partially. It is used for
     * animation ShowCreation, for example
     *
     * @param t Parameter to draw (0=nothing, 1=draw the entire object)
     */
    public abstract void setDrawParam(double t, int sliceType);

    /**
     * Returns the Bounding box with limits of the MathObject
     *
     * @return A Rect with (xmin,ymin,xmax,ymax)
     */
    public abstract Rect getBoundingBox();

    public void setAlpha(double t) {
        setDrawAlpha(t);
        setFillAlpha(t);
    }

    public abstract void setDrawAlpha(double t);

    public abstract void setFillAlpha(double t);

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
    public <T extends MathObject> T drawColor(Color dc) {
        mp.drawColor = dc;
        return (T) this;
    }

    public <T extends MathObject> T fillColor(Color fc) {
        mp.fillColor = fc;
        mp.fill = true;
        return (T) this;
    }

    public Point getAbsoluteAnchorPoint() {
        return getAnchorPoint(absoluteAnchorType);
    }

    public void setAbsolutAnchorPoint(int anchor) {
        absoluteAnchorType=anchor;

    }
    public void setAbsolutAnchorPoint(Point p) {
        this.absoluteAnchorPoint=p;
        absoluteAnchorType=ANCHOR_BY_POINT;

    }

    public Point getAnchorPoint(int anchor) {
        Point resul = new Point();
        switch (anchor) {
            case ANCHOR_BY_POINT:
                resul = absoluteAnchorPoint;
                break;
            case ANCHOR_BY_CENTER:
                resul = getCenter();
                break;

            case ANCHOR_LEFT:
                resul = getBoundingBox().getLeft();
                break;
            case ANCHOR_RIGHT:
                resul = getBoundingBox().getRight();
                break;
            case ANCHOR_LOWER:
                resul = getBoundingBox().getLower();
                break;
            case ANCHOR_UPPER:
                resul = getBoundingBox().getUpper();
                break;

            case ANCHOR_UL:
                resul = getBoundingBox().getUL();
                break;
            case ANCHOR_UR:
                resul = getBoundingBox().getUR();
                break;
            case ANCHOR_DL:
                resul = getBoundingBox().getDL();
                break;
            case ANCHOR_DR:
                resul = getBoundingBox().getDR();
                break;

        }
        return resul;
    }
    
    public <T extends MathObject> T setAbsoluteSize()
    {
        absoluteSize=true;
        absoluteAnchorType=ANCHOR_BY_CENTER;
        return (T) this;
    }
    public <T extends MathObject> T setAbsoluteSize(int anchorType)
    {
        absoluteSize=true;
        absoluteAnchorType=anchorType;
        return (T) this;
    }
     public <T extends MathObject> T setRelativeSize()
    {
        absoluteSize=false;
        return (T) this;
    }
    
    
}
