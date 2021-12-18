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

import com.jmathanim.Styling.JMColor;
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.Styling.PaintStyle;
import com.jmathanim.Styling.Stylable;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.Anchor.Type;
import com.jmathanim.Utils.Boxable;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.updateableObjects.Updateable;
import javafx.scene.shape.StrokeLineCap;

/**
 * This class represents a mathematical object that can be drawed on screen,
 * transformed or animated. All math objects subclass from here.
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class MathObject implements Drawable, Updateable, Stateable, Boxable {

    public enum Align {
        LEFT, RIGHT, UPPER, LOWER, HCENTER, VCENTER
    }

    protected JMathAnimScene scene;
    private String debugText = "";

    private MODrawProperties mp;
    public String objectLabel = "";

    public boolean absoluteSize = false;

    public Point absoluteAnchorPoint;
    private Type absoluteAnchorType = Type.CENTER;

    public MathObject() {
        this(null);
    }

    public MathObject(MODrawProperties prop) {

        scene = JMathAnimConfig.getConfig().getScene();
        mp = JMathAnimConfig.getConfig().getDefaultMP();// Default MP values
        mp.copyFrom(prop);// Copy all non-null values from prop
        //Default values for an object that always updates
    }

    /**
     * Return center of object, intented by default as the center of bounding
     * box.
     *
     * @return Vec object with center
     */
    public Point getCenter() {
        return this.getBoundingBox().getCenter();//TODO: Adapt this to 3D!
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
     * Shift object. Overloaded method
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
     * Shift object.Overloaded method.
     *
     * @param <T> MathObject subclass
     * @param x x-coordinate of shift vector
     * @param y y-coordinate of shift vector
     * @param z z-coordinate of shift vector
     * @return The same object, after shifting
     */
    public final <T extends MathObject> T shift(double x, double y, double z) {
        return shift(new Vec(x, y, z));
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

    /**
     * Center the object in the math view. This command is equivalent
     * this.stackToScreen(Type.BY_CENTER);
     *
     * @param <T> Object type
     * @return The same object
     */
    public final <T extends MathObject> T center() {
        this.stackToScreen(Type.CENTER);
        return (T) this;
    }

    /**
     * Center the object vertically in the math view.
     *
     * @param <T> Object type
     * @return The same object
     */
    public final <T extends MathObject> T vCenter() {
        Vec v = getCenter().v;
        center();
        shift(v.x - getCenter().v.x, 0);
        return (T) this;
    }

    /**
     * Center the object horizontally in the math view.
     *
     * @param <T> Object type
     * @return The same object
     */
    public final <T extends MathObject> T hCenter() {
        Vec v = getCenter().v;
        center();
        shift(0, v.y - getCenter().v.y);
        return (T) this;
    }

    /**
     * Performs a 2D-Rotation of the MathObject around the object center
     *
     * @param <T> MathObject subclass
     * @param angle Angle, in radians
     * @return The same object, after rotating
     */
    public <T extends MathObject> T rotate(double angle) {
        return rotate(getCenter(), angle);
    }

    /**
     * Performs a 2D-Rotation of the MathObject around the given rotation center
     *
     * @param <T> MathObject subclass
     * @param center Rotation center
     * @param angle Angle, in radians
     * @return The same object, after rotating
     */
    public <T extends MathObject> T rotate(Point center, double angle) {
        AffineJTransform tr = AffineJTransform.create2DRotationTransform(center, angle);
        tr.applyTransform(this);
        return (T) this;
    }

    /**
     * Performs a 3D-Rotation of the MathObject around the center of the object
     *
     * @param <T> MathObject subclass
     * @param anglex Rotation angle in x axis, in radians
     * @param angley Rotation angle in y axis, in radians
     * @param anglez Rotation angle in z axis, in radians
     * @return The same object, after rotating
     */
    public <T extends MathObject> T rotate3d(double anglex, double angley, double anglez) {
        return rotate3d(this.getCenter(), anglex, angley, anglez);
    }

    /**
     * Performs a 2D-Rotation of the MathObject around the given rotation center
     *
     * @param <T> MathObject subclass
     * @param center Rotation center
     * @param anglex Rotation angle in x axis, in radians
     * @param angley Rotation angle in y axis, in radians
     * @param anglez Rotation angle in z axis, in radians
     * @return The same object, after rotating
     */
    public <T extends MathObject> T rotate3d(Point center, double anglex, double angley, double anglez) {
        AffineJTransform tr = AffineJTransform.create3DRotationTransform(center, anglex, angley, anglez);
        tr.applyTransform(this);
        return (T) this;
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
     * Copy state from another object of the same type. After this method is
     * executed, the two objects should be identical.
     *
     * @param obj The object to copy state from.
     */
//    public void copyStateFrom(MathObject obj) {
//        JMathAnimScene.logger.error("copyStateFrom method not implemented for this object, sorry");
//    }
    abstract public void copyStateFrom(MathObject obj);

    /**
     * Returns the Bounding box with limits of the MathObject
     *
     * @return A Rect with (xmin,ymin,xmax,ymax)
     */
    @Override
    public abstract Rect getBoundingBox();

    public void setAlpha(double t) {
        drawAlpha(t);
        fillAlpha(t);
    }

//    public void registerChildrenToBeUpdated(JMathAnimScene scene) {
//    }
//
//    public void unregisterChildrenToBeUpdated(JMathAnimScene scene) {
//    }
    @Override
    public void saveState() {
        getMp().saveState();
    }

    @Override
    public void restoreState() {
        getMp().restoreState();
    }

    /**
     * Return the current drawing attributes object
     *
     * @return The drawing attributes object
     */
    public Stylable getMp() {
        return mp;
    }

    /**
     * Copy draw attributes from another one.
     *
     * @param <T> Subclass of MathObject that calls the method
     * @param newMp Drawing properties to be copied
     * @return This object
     */
    public <T extends MathObject> T setMp(MODrawProperties newMp) {
        this.getMp().copyFrom(newMp);
        return (T) this;
    }

    /**
     * Changes both draw and fill color
     *
     * @param <T> Subclass
     * @param dc A PaintStyle object. Can be a JMColor, a gradient or image
     * pattern
     * @return This object
     */
    public <T extends MathObject> T color(PaintStyle dc) {
        drawColor(dc);
        fillColor(dc);
        return (T) this;
    }

    /**
     * Overloaded method. Sets both draw and fill color
     *
     * @param <T> Calling subclass
     * @param str A string representing the draw color, as in the JMcolor.parse
     * method
     * @return This object
     */
    public <T extends MathObject> T color(String str) {
        drawColor(str);
        fillColor(str);
        return (T) this;
    }

    /**
     * Sets the draw color of the object
     *
     * @param <T> Subclass of MathObject that calls the method
     * @param dc A JMcolor object with the draw color
     * @return The MathObject subclass
     */
    public <T extends MathObject> T drawColor(PaintStyle dc) {
        getMp().setDrawColor(dc);
        return (T) this;
    }

    /**
     * Sets the draw color of the object. Overloaded method.
     *
     * @param <T> Subclass of MathObject that calls the method
     * @param str A string representing the draw color, as in the JMcolor.parse
     * method
     * @return The MathObject subclass
     */
    public final <T extends MathObject> T drawColor(String str) {
        drawColor(JMColor.parse(str));
        return (T) this;
    }

    /**
     * Sets the fill color of the object
     *
     * @param <T> Subclass of MathObject that calls the method
     * @param fc A JMcolor object with the fill color
     * @return The MathObject subclass
     */
    public <T extends MathObject> T fillColor(PaintStyle fc) {
        getMp().setFillColor(fc);
        return (T) this;
    }

    /**
     * Sets the fill color of the object. Overloaded method.
     *
     * @param <T> Subclass of MathObject that calls the method
     * @param str A string representing the fill color, as in the JMcolor.parse
     * method
     * @return The MathObject subclass
     */
    public final <T extends MathObject> T fillColor(String str) {
        fillColor(JMColor.parse(str));
        return (T) this;
    }

    /**
     * Sets the alpha component of the draw color
     *
     * @param <T> Subclass of MathObject that calls the method
     * @param alpha Alpha value, between 0 (transparent) and 1 (opaque)
     * @return The MathObject subclass
     */
    public <T extends MathObject> T drawAlpha(double alpha) {
        getMp().setDrawAlpha(alpha);
        return (T) this;
    }

    /**
     * Sets the alpha component of the fill color
     *
     * @param <T> Subclass of MathObject that calls the method
     * @param alpha Alpha value, between 0 (transparent) and 1 (opaque)
     * @return This MathObject subclass
     */
    public <T extends MathObject> T fillAlpha(double alpha) {
        getMp().setFillAlpha(alpha);
        return (T) this;
    }

    /**
     * Multiplies alpha fill color by a scale
     *
     * @param <T> Subclass of MathObject that calls the method
     * @param alphaScale Scale to multiply alpha
     * @return This MathObject subclass
     */
    public <T extends MathObject> T multFillAlpha(double alphaScale) {
        getMp().setMultFillAlpha(alphaScale);
        return (T) this;
    }

    /**
     * Multiplies alpha draw color by a scale
     *
     * @param <T> Subclass of MathObject that calls the method
     * @param alphaScale Scale to multiply alpha
     * @return This MathObject subclass
     */
    public <T extends MathObject> T multDrawAlpha(double alphaScale) {
        getMp().setMultDrawAlpha(alphaScale);
        return (T) this;
    }

    /**
     * Sets the thickness to draw the contour of the object
     *
     * @param <T> Subclass of MathObject that calls the method
     * @param newThickness Thickness
     * @return This MathObject subclass
     */
    public <T extends MathObject> T thickness(double newThickness) {
        getMp().setThickness(newThickness);
        return (T) this;
    }

    /**
     * Sets the dashStyle, from one of the types defined in the enum
     * MODrawProperties.DashStyle
     *
     * @param <T> Subclass of MathObject that calls the method
     * @param dashStyle A value from enum MODrawProperties.DashStyle
     * @return This MathObject subclass
     */
    public <T extends MathObject> T dashStyle(MODrawProperties.DashStyle dashStyle) {
        getMp().setDashStyle(dashStyle);
        return (T) this;
    }

    /**
     * Sets the flag visible. If false, the object won't be draw using the
     * renderer, although it still will be in the scene.
     *
     * @param <T> Subclass of MathObject that calls the method
     * @param visible
     * @return This MathObject subclass
     */
    public <T extends MathObject> T visible(boolean visible) {
        getMp().setVisible(visible);
        return (T) this;
    }

    public Point getAbsoluteAnchor() {
        return Anchor.getAnchorPoint(this, absoluteAnchorType);
    }

    public Point getAbsoluteAnchorPoint() {
        return absoluteAnchorPoint;
    }

    /**
     * Marks this object with the absolute size flag.In this case, it will be
     * drawn using a fixed camera, so that it will appear with the same size
     * regardless of the zoom applied to the camera. The given point will be
     * used as reference point to position the object.
     *
     * @param <T> Mathobject subclass
     * @param p Reference point to position the object.
     * @return The current object
     */
    public <T extends MathObject> T setAbsoluteSize(Point p) {
        this.absoluteAnchorPoint = p;
        absoluteAnchorType = Type.BY_POINT;
        absoluteSize = true;
        return (T) this;

    }

    /**
     * Stack the object to another using a specified anchor. The anchor for the
     * stacked object is automatically selected as the reverse of the destiny
     * anchor. For example stackTo(obj, RIGHT) will move this object so that its
     * LEFT anchor matchs the RIGHT anchor of the destiny. This method is
     * equivalent to stackTo(obj,type,0)
     *
     * @param <T> Mathobject subclass
     * @param obj The destiny object. Anyting that implements the Boxable
     * interface, like MathObject or Rect
     * @param anchorType {@link Anchor} type
     * @return The current object
     */
    public final <T extends MathObject> T stackTo(Boxable obj, Type anchorType) {
        return stackTo(obj, anchorType, 0);
    }

    /**
     * Stack the object to another using a specified anchor. For example
     * stackTo(UPPER, obj, RIGHT) will move this object so that its UPPER anchor
     * matchs the RIGHT anchor of the destiny.
     *
     * @param <T> Mathobject subclass
     * @param originAnchor Anchor of this object to use
     * @param destinyObject Destiny object to stack with
     * @param destinyAnchor Anchor of destiny object to use
     * @param originGap Amount of gap to leave between the anchors, in math
     * units. The direction of the gap will be computed using origin anchor as
     * reference.
     * @return This object
     */
    public <T extends MathObject> T stackTo(Type originAnchor, Boxable destinyObject, Type destinyAnchor, double originGap) {
        return stackTo(originAnchor, destinyObject, destinyAnchor, originGap, 0);
    }

    /**
     * Stack the object to another using a specified anchor.For example
     * stackTo(UPPER, obj, RIGHT) will move this object so that its UPPER anchor
     * matchs the RIGHT anchor of the destiny.
     *
     * @param <T> Mathobject subclass
     * @param originAnchor Anchor of this object to use
     * @param destinyObject Destiny object to stack with
     * @param destinyAnchor Anchor of destiny object to use
     * @param originGap Amount of gap to leave in origin anchor, in math units
     * @param destinyGap Amount of gap to leave in destiny anchor, in math units
     * @return This object
     */
    public <T extends MathObject> T stackTo(Type originAnchor, Boxable destinyObject, Type destinyAnchor, double originGap, double destinyGap) {
        if (!destinyObject.isEmpty()) {
            Point B = Anchor.getAnchorPoint(destinyObject, destinyAnchor, destinyGap);
            Point A = Anchor.getAnchorPoint(this, originAnchor, originGap);
            this.shift(A.to(B));
        }
        return (T) this;
    }

    /**
     * Stack the object to another using a specified anchor. For example
     * stackTo(UPPER, obj, RIGHT) will move this object so that its UPPER anchor
     * matchs the RIGHT anchor of the destiny. The difference with similar
     * methods is that the gap is given relative to this object width.
     *
     * @param <T> Mathobject subclass
     * @param anchorObj Anchor of this object to use
     * @param dstObj Destiny object to stack with
     * @param anchorType Anchor of destiny object to use
     * @param gap Amount of gap, relative to this object width, to leave between
     * the anchors, in math units.
     * @return This object
     */
    public <T extends MathObject> T stackToRW(Type anchorObj, Boxable dstObj, Type anchorType, double gap) {
        return stackTo(anchorObj, dstObj, anchorType, gap * this.getWidth());
    }

    /**
     * Stack the object to another using a specified anchor. For example
     * stackTo(UPPER, obj, RIGHT) will move this object so that its UPPER anchor
     * matchs the RIGHT anchor of the destiny. The difference with similar
     * methods is that the gap is given relative to this object height.
     *
     * @param <T> Mathobject subclass
     * @param anchorObj Anchor of this object to use
     * @param dstObj Destiny object to stack with
     * @param anchorType Anchor of destiny object to use
     * @param gap Amount of gap, relative to this object height, to leave
     * between the anchors, in math units.
     * @return This object
     */
    public <T extends MathObject> T stackToRH(Type anchorObj, Boxable dstObj, Type anchorType, double gap) {
        return stackTo(anchorObj, dstObj, anchorType, gap * this.getHeight());
    }

    /**
     * Stack the object to another using a specified anchor. For example
     * stackTo(obj, RIGHT) will move this object so that its LEFT anchor matchs
     * the RIGHT anchor of the destiny. The difference with similar methods is
     * that the gap is given relative to this object height.
     *
     * @param <T> Mathobject subclass
     * @param dstObj Destiny object to stack with
     * @param anchorType Anchor of destiny object to use
     * @param gap Amount of gap, relative to this object height, to leave
     * between the anchors, in math units.
     * @return This object
     */
    public <T extends MathObject> T stackToRH(Boxable dstObj, Type anchorType, double gap) {
        return stackToRH(Anchor.reverseAnchorPoint(anchorType), dstObj, anchorType, gap);
    }

    /**
     * Stack the object to another using a specified anchor. For example
     * stackTo(obj, RIGHT) will move this object so that its LEFT anchor matchs
     * the RIGHT anchor of the destiny. The difference with similar methods is
     * that the gap is given relative to this object width.
     *
     * @param <T> Mathobject subclass
     * @param dstObj Destiny object to stack with
     * @param anchorType Anchor of destiny object to use
     * @param gap Amount of gap, relative to this object width, to leave between
     * the anchors, in math units.
     * @return This object
     */
    public <T extends MathObject> T stackToRW(Boxable dstObj, Type anchorType, double gap) {
        return stackToRW(Anchor.reverseAnchorPoint(anchorType), dstObj, anchorType, gap);
    }

    /**
     * Stack the object to another using a specified anchor.The anchor for the
     * stacked object is automatically selected as the reverse of the destiny
     * anchor. For example stackTo(obj, RIGHT) will move this object so that its
     * LEFT anchor matchs the RIGHT anchor of the destiny.
     *
     * @param <T> Mathobject subclass
     * @param obj The destiny object. Anyting that implements the Boxable
     * interface, like MathObject or Rect
     * @param anchorType {@link Anchor} type
     * @param gap Amount of gap to leave between the anchors, in math units
     * @return The current object
     */
    public <T extends MathObject> T stackTo(Boxable obj, Type anchorType, double gap) {
        return stackTo(Anchor.reverseAnchorPoint(anchorType), obj, anchorType, gap);
    }

    /**
     * Stack the object to the given anchor, relative to the current camera view
     *
     * @param <T> Mathobject subclass
     * @param anchorType {@link Anchor} type
     * @return The current object
     */
    public final <T extends MathObject> T stackToScreen(Type anchorType) {
        return stackToScreen(anchorType, 0, 0);
    }

    /**
     * Stack the object to the given anchor, relative to the current camera
     * view, applying the specified margins.
     *
     * @param <T> Mathobject subclass
     * @param anchorType {@link Anchor} type
     * @param xMargin x margin
     * @param yMargin y margin
     * @return The current object
     */
    public <T extends MathObject> T stackToScreen(Type anchorType, double xMargin, double yMargin) {
        Point B = Anchor.getScreenAnchorPoint(anchorType, xMargin, yMargin);
        Point A = Anchor.getAnchorPoint(this, anchorType);
        return this.shift(A.to(B));
    }

    /**
     * Shifts the object so that its center lies at the specified location
     *
     * @param <T> Mathobject subclass
     * @param p Destination point
     * @return The current object
     */
    public final <T extends MathObject> T moveTo(Point p) {
        return stackTo(p, Anchor.Type.CENTER);
    }

    /**
     * Overloaded method. Shifts the object so that its center lies at the
     * specified location
     *
     * @param <T> Mathobject subclass
     * @param x x destiny coordinate
     * @param y y destiny coordinate
     * @return The current object
     */
    public <T extends MathObject> T moveTo(double x, double y) {
        return moveTo(Point.at(x, y));
    }

    /**
     * Marks this object with the absolute size flag. In this case, it will be
     * drawn using a fixed camera, so that it will appear with the same size
     * regardless of the zoom applied to the camera. The center of the object
     * will be used as reference point to position the object.
     *
     * @param <T> Mathobject subclass
     * @return The current object
     */
    public <T extends MathObject> T setAbsoluteSize() {
        return setAbsoluteSize(Type.CENTER);
    }

    /**
     * Marks this object with the absolute size flag.In this case, it will be
     * drawn using a fixed camera, so that it will appear with the same size
     * regardless of the zoom applied to the camera. The specified anchor will
     * be used as reference point to position the object.
     *
     * @param <T> Mathobject subclass
     * @param anchorType {@link Anchor} type
     * @return The current object
     */
    public <T extends MathObject> T setAbsoluteSize(Type anchorType) {
        absoluteSize = true;
        absoluteAnchorType = anchorType;
        absoluteSize = true;
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
        this.getMp().setLayer(layer);
        return (T) this;
    }

    /**
     * Returns the layer where this object belongs to
     *
     * @return The layer number
     */
    public Integer getLayer() {
        return getMp().getLayer();
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
        getMp().loadFromStyle(name);
        return (T) this;
    }

    /**
     * Sets the linecap style, using one of the styles of StrokeLineCap
     *
     * @param <T> MathObject subclass
     * @param strokeLineCap Style of linecap
     * @return The object
     */
    public <T extends MathObject> T linecap(StrokeLineCap strokeLineCap) {
        this.getMp().setLinecap(strokeLineCap);
        return (T) this;
    }

    // Updateable methods
    @Override
    public void update(JMathAnimScene scene) {
        //Nothing to do by default
    }

    @Override
    public int getUpdateLevel() {
        return 0;// Default value, objects that need to be updated should override this
    }

    public String getDebugText() {
        return debugText;
    }

    public <T extends MathObject> T debugText(String debugText) {
        this.debugText = debugText;
        return (T) this;
    }

    /**
     * Returns the visibility status of the object. If false, object will not be
     * drawed, even if it is added to the scene. It will be updated, though.
     *
     * @return True if visible, false otherwise
     */
    public boolean isVisible() {
        return getMp().isVisible();
    }

    /**
     * Returns the current width of the object, in math coordinates
     *
     * @return The width
     */
    public double getWidth() {
        Rect b = getBoundingBox();
        if (b == null) {
            return 0;
        } else {
            return b.getWidth();
        }
    }

    /**
     * Returns the current height of the object, in math coordinates
     *
     * @return The height
     */
    public double getHeight() {
        Rect b = getBoundingBox();
        if (b == null) {
            return 0;
        } else {
            return b.getHeight();
        }
    }

    /**
     * Check if the current object is empty (for example: a MultiShape with no
     * objects). A empty object case should be considered as they return null
     * bounding boxes.
     *
     * @return True if object is empty, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return false;
    }

    /**
     * Align this object with another one
     *
     * @param <T> MathObject subclass
     * @param obj Object to align with. This object remains unaltered.
     * @param type Align type, a value from the enum Align
     * @return This object
     */
    public <T extends MathObject> T align(Boxable obj, Align type) {
        Vec shiftVector = Vec.to(0, 0);
        Rect thisBoundingBox = this.getBoundingBox();
        Rect objectBoundingBox = obj.getBoundingBox();
        switch (type) {
            case LOWER:
                shiftVector.y = objectBoundingBox.ymin - thisBoundingBox.ymin;
                break;
            case UPPER:
                shiftVector.y = objectBoundingBox.ymax - thisBoundingBox.ymax;
                break;
            case LEFT:
                shiftVector.x = objectBoundingBox.xmin - thisBoundingBox.xmin;
                break;
            case RIGHT:
                shiftVector.x = objectBoundingBox.xmax - thisBoundingBox.xmax;
                break;
            case VCENTER:
                shiftVector.y = .5d * ((objectBoundingBox.ymin + objectBoundingBox.ymax)
                        - (thisBoundingBox.ymin + thisBoundingBox.ymax));
                break;
            case HCENTER:
                shiftVector.x = .5d * ((objectBoundingBox.xmin + objectBoundingBox.xmax)
                        - (thisBoundingBox.xmin + thisBoundingBox.xmax));
                break;
        }
        shift(shiftVector);
        return (T) this;
    }

    /**
     * Apply an affine transform to the object.
     *
     * @param <T> Calling subclass
     * @param transform Affine transform to apply
     * @return This object
     */
    public <T extends MathObject> T applyAffineTransform(AffineJTransform transform) {
        return (T) this;// By default does nothing
    }

    public void addToSceneHook(JMathAnimScene scene) {
    }

}
