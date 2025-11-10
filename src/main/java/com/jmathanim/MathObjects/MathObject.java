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
package com.jmathanim.MathObjects;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Cameras.DummyCamera;
import com.jmathanim.Enum.*;
import com.jmathanim.MathObjects.Updaters.Updater;
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.Styling.PaintStyle;
import com.jmathanim.Styling.RendererEffects;
import com.jmathanim.Styling.Stylable;
import com.jmathanim.Utils.*;
import com.jmathanim.jmathanim.JMathAnimConfig;
import com.jmathanim.jmathanim.JMathAnimScene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This class represents a mathematical object that can be drawn on screen, transformed or animated. All math objects
 * subclass from here.
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
@SuppressWarnings("ALL")
public abstract class MathObject<T extends MathObject<T>> extends AbstractVersioned implements
        Drawable,
        Boxable,
        Linkable,
        Stylable<T>,
        AffineTransformable<T> {
    protected final AffineJTransform modelMatrix;
    private final HashSet<MathObject<?>> dependents;
    private final RendererEffects rendererEffects;
    private final HashMap<String, Object> properties;
    private final ArrayList<Updater> updaters;
    public String objectLabel = "";
    public boolean absoluteSize = false;
    public Vec absoluteAnchorVec;
    protected JMathAnimScene scene;
    protected boolean isRigid = false;
    protected Camera camera;
    private boolean hasBeenUpdated = false;
    private int updateLevel;
    private String debugText = "";
    private AnchorType absoluteAnchorAnchorType = AnchorType.CENTER;
    private double leftGap, upperGap, rightGap, lowerGap;
    private Rect boundingBox;

    public MathObject() {
        this.updateLevel = 0;
        JMathAnimConfig config = JMathAnimConfig.getConfig();
        if (config.getRenderer() != null) {
            rendererEffects = config.getRenderer().buildRendererEffects();
        } else {
            rendererEffects = new RendererEffects();
        }
        scene = config.getScene();
        if (scene != null) {
            camera = scene.getCamera();//Default camera
        } else {
            camera = new DummyCamera();
        }
        //Default values for an object that always updates
        dependents = new HashSet<>();

        //Default gaps
        leftGap = 0;
        upperGap = 0;
        rightGap = 0;
        lowerGap = 0;
        this.properties = new HashMap<>();
        updaters = new ArrayList<>();
        modelMatrix = new AffineJTransform();
    }

//    public boolean isRigid() {
//        return isRigid;
//    }
//
//    public void setRigid(boolean rigid) {
//        isRigid = rigid;
//    }

    public AffineJTransform getModelMatrix() {
        return modelMatrix;
    }

    public Camera getCamera() {
        return camera;
    }

    /**
     * Set the associated camera to this object. The camera is used to compute the screen coordinates where it will be
     * drawn. If not camera is set, default camera is associated when added to the scene.
     *
     * @param camera Camera
     * @return This object
     */
    public T setCamera(Camera camera) {
        if ((this instanceof shouldUdpateWithCamera) && (getCamera() != null)) {
            getCamera().unregisterUpdateable((shouldUdpateWithCamera) this);
            camera.registerUpdateable((shouldUdpateWithCamera) this);
        }
        this.camera = camera;
        changeVersion();
        return (T) this;
    }

    /**
     * Center the object in the math view. This command is equivalent this.stackToScreen(Type.BY_CENTER);
     *
     * @return The same object
     */
    public final T center() {
        this.stack().toScreen(ScreenAnchor.CENTER);
        return (T) this;
    }

    /**
     * Center the object vertically in the math view.
     *
     * @return The same object
     */
    public final T vCenter() {
        Vec v = getCenter();
        center();
        shift(v.x - getCenter().x, 0);
        return (T) this;
    }

    /**
     * Center the object horizontally in the math view.
     *
     * @return The same object
     */
    public final T hCenter() {
        Vec vCenter = getCenter();
        center();
        shift(0, vCenter.y - getCenter().y);
        return (T) this;
    }


    /**
     * Returns a copy of the object
     *
     * @return copy of object, with identical properties. A deep copy is performed.
     */
    abstract public T copy();

    /**
     * Copy state from another object of the same type. After this method is executed, the two objects should be
     * identical.
     *
     * @param obj The object to copy state from.
     */
    @Override
    public void copyStateFrom(Stateable obj) {
        if (!(obj instanceof MathObject<?>)) return;
        MathObject mathObject = (MathObject) obj;
        this.setCamera(mathObject.getCamera());
        getMp().copyFrom(mathObject.getMp());
//        this.getMp().copyFrom(obj.getMp());
        if (this.getRendererEffects() != null) {
            this.getRendererEffects().copyFrom(rendererEffects);
        }
        this.modelMatrix.copyFrom(mathObject.modelMatrix);
        changeVersion();
    }


    /**
     * Returns the Bounding box with limits of the MathObject
     *
     * @return A Rect with (xmin,ymin,xmax,ymax)
     */
    @Override
    public final Rect getBoundingBox() {
//        return computeBoundingBox().addGap(rightGap, upperGap, leftGap, lowerGap);
        if (needsUpdate()) {
            update(scene);//Updates and recomputes bounding box
        }
        return boundingBox.addGap(rightGap, upperGap, leftGap, lowerGap);
    }

    protected void performUpdateBoundingBox(JMathAnimScene scene) {
        boundingBox = computeBoundingBox();
    }

    protected abstract Rect computeBoundingBox();

    public void setAlpha(double t) {
        drawAlpha(t);
        fillAlpha(t);
        changeVersion();
    }


    /**
     * Copy draw attributes from another one.
     *
     * @param newMp Drawing properties to be copied
     * @return This object
     */
    public T setMp(MODrawProperties newMp) {
        this.getMp().copyFrom(newMp);
        changeVersion();
        return (T) this;
    }


    public Vec getAbsoluteAnchor() {
        return Anchor.getAnchorPoint(this, absoluteAnchorAnchorType);
    }

    public Vec getAbsoluteAnchorVec() {
        return absoluteAnchorVec;
    }

    /**
     * Marks this object with the absolute size flag.In this case, it will be drawn using a fixed camera, so that it
     * will appear with the same size regardless of the zoom applied to the camera. The given point will be used as
     * reference point to position the object.
     *
     * @param anchorVec Reference vector to position the object.
     * @return The current object
     */
    public T setAbsoluteSize(Vec anchorVec) {
        this.absoluteAnchorVec = anchorVec;
        absoluteAnchorAnchorType = AnchorType.BY_POINT;
        absoluteSize = true;
        changeVersion();
        return (T) this;
    }

    /**
     * Marks this object with the absolute size flag.In this case, it will be drawn using a fixed camera, so that it
     * will appear with the same size regardless of the zoom applied to the camera. The specified anchor will be used as
     * reference point to position the object.
     *
     * @param anchorType {@link Anchor} type
     * @return The current object
     */
    public T setAbsoluteSize(AnchorType anchorType) {
        absoluteSize = true;
        absoluteAnchorAnchorType = anchorType;
        absoluteSize = true;
        changeVersion();
        return (T) this;
    }

    public T setRelativeSize() {
        absoluteSize = false;
        changeVersion();
        return (T) this;
    }

    /**
     * Convenience class to apply stack methods
     *
     * @return
     */
    public StackUtils<T> stack() {
        return new StackUtils<>((T) this);
    }


    /**
     * Sets the layer where this object belongs. Lower layers means that the object will be drawn first and appear under
     * other objects. The number can be any integer
     *
     * @param layer Layer number
     * @return The object
     */
    public T layer(int layer) {
        this.getMp().setLayer(layer);
        changeVersion();
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
     * Sets the given style, as defined in config files. If no such style exists, there is no effect, apart from a
     * warning message
     *
     * @param name Name of the style being applied
     * @return The object
     */
    public T style(String name) {
        getMp().loadFromStyle(name);
        changeVersion();
        return (T) this;
    }

    /**
     * Sets the linecap style, using one of the styles of StrokeLineCap
     *
     * @param strokeLineCap Style of linecap
     * @return The object
     */
    public T linecap(StrokeLineCap strokeLineCap) {
        this.getMp().setLinecap(strokeLineCap);
        changeVersion();
        return (T) this;
    }

    /**
     * Registers an updater for the current MathObject instance.
     * <p>
     * This method associates the provided updater with the current MathObject and refreshes its update level. It is
     * important to note that this may introduce infinite recursion if not handled correctly.
     *
     * @param updater the updater to register
     * @return the current MathObject instance
     */
    public T registerUpdater(Updater updater) {
        updater.setMathObject(this);
        updaters.add(updater);
//        setUpdateLevel(getUpdateLevel());
        return (T) this;
    }

    @Override
    protected boolean applyUpdaters(JMathAnimScene scene) {
        boolean resultFlag = false;
        for (Updater u : updaters) {
            u.update(scene);
            resultFlag = true;//If at least one updater, the object is changed
        }
        return resultFlag;
    }


    protected String getDebugText() {
        return debugText;
    }

    /**
     * Set debug text that the renderer will draw over the object for debugging purposes. This method cannot be accesed
     * directly, it has to be accessed through the MediatorMathObject.setDebugText static method
     *
     * @param debugText The debug text
     */
    protected void setDebugText(String debugText) {
        this.debugText = debugText;
    }

    /**
     * Returns the visibility status of the object. If false, object will not be drawn, even if it is added to the
     * scene. It will be updated, though.
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
     * Scale object so that has a specified width. Scaled is done around its center.
     *
     * @param w Desired width
     * @return The same object
     */
    public T setWidth(double w) {
        scale(w / this.getBoundingBox().getWidth());
        return (T) this;
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
     * Scale object so that has a specified height. Scaled is done around its center.
     *
     * @param h Desired height
     * @return The same object
     */
    public T setHeight(double h) {
        scale(h / this.getBoundingBox().getHeight());
        return (T) this;
    }

    /**
     * Check if the current object is empty (for example: a MultiShape with no objects). An empty object case should be
     * considered as they return null bounding boxes.
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
     * @param obj  Object to align with. This object remains unaltered.
     * @param type Align type, a value from the enum Align
     * @return This object
     */
    public T align(Boxable obj, AlignType type) {
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
                shiftVector.y = .5d * ((objectBoundingBox.ymin + objectBoundingBox.ymax) - (thisBoundingBox.ymin + thisBoundingBox.ymax));
                break;
            case HCENTER:
                shiftVector.x = .5d * ((objectBoundingBox.xmin + objectBoundingBox.xmax) - (thisBoundingBox.xmin + thisBoundingBox.xmax));
                break;
        }
        shift(shiftVector);
        return (T) this;
    }

    /**
     * Apply an affine transform to the object.
     *
     * @param affineJTransform Affine transform to apply
     * @return This object
     */
    public T applyAffineTransform(AffineJTransform affineJTransform) {
        if (isRigid) {
            AffineJTransform compose = modelMatrix.compose(affineJTransform);
            modelMatrix.copyFrom(compose);
        }
        changeVersion();
        return (T) this;// By default does nothing
    }

    /**
     * This hook is invoked when this object is added to the scene. You can override this method if you are defining
     * your own MathObject subclass.
     *
     * @param scene Scene where the object is added
     */
    protected void addToSceneHook(JMathAnimScene scene) {
        this.scene = scene;
        if (camera == null) {
            camera = scene.getCamera();
        }
        setProperty("scene", scene);
    }

    /**
     * This hook is invoked when this object is removed from the scene. You can override this method if you are defining
     * your own MathObject subclass.
     *
     * @param scene Scene from where the object is removed
     */
    protected void removedFromSceneHook(JMathAnimScene scene) {
        this.scene = null;
        setProperty("scene", null);
    }

    //    /**
//     * Returns the set of objects that depend on this to be properly updated
//     *
//     * @return A HashSet of the dependent MathObjects
//     */
//    public HashSet<MathObject<?>> getDependentObjects() {
//        return dependents;
//    }


    /**
     * Sets the gaps for this object. These gaps will be added to the bounding box of the object.
     *
     * @param upperGap Upper gap
     * @param rightGap Right gap
     * @param lowerGap Lower gap
     * @param leftGap  Left gap
     * @return This object
     */
    public T setGaps(double upperGap, double rightGap, double lowerGap, double leftGap) {
        this.rightGap = rightGap;
        this.upperGap = upperGap;
        this.leftGap = leftGap;
        this.lowerGap = lowerGap;
        changeVersion();
        return (T) this;
    }

    /**
     * Returns a double array with current bounding box gaps
     *
     * @return An array with values upperGap, rightGap, lowerGap, leftGap
     */
    public double[] getGaps() {
        return new double[]{upperGap, rightGap, lowerGap, leftGap};
    }

    /**
     * Retrieves the property with given key
     *
     * @param key A String with the key name
     * @return The proporty
     */
    public Object getProperty(String key) {
        return properties.get(key);
    }

    /**
     * Stores an object into an internal dictionary of the MathObject. This can be useful if additional information to
     * this object needs to be saved
     *
     * @param key A String denoting the key
     * @param obj Any Java Object
     * @return This object
     */
    public T setProperty(String key, Object obj) {
        properties.put(key, obj);
        return (T) this;
    }

    /**
     * Sets the label object. This is used mostly for debugging purposes
     *
     * @param objectLabel The label
     * @return This object
     */
    public T setObjectLabel(String objectLabel) {
        this.objectLabel = objectLabel;
        return (T) this;
    }


    @Override
    public RendererEffects getRendererEffects() {
        return rendererEffects;
    }

    //Overriden methods to ensure proper return value


    @Override
    public T drawColor(PaintStyle<?> dc) {
        changeVersion();
        return Stylable.super.drawColor(dc);
    }

    @Override
    public T drawColor(String str) {
        changeVersion();
        return Stylable.super.drawColor(str);
    }

    @Override
    public T drawAlpha(double alpha) {
        changeVersion();
        return Stylable.super.drawAlpha(alpha);
    }

    @Override
    public T fillAlpha(double alpha) {
        changeVersion();
        return Stylable.super.fillAlpha(alpha);
    }

    @Override
    public T thickness(double newThickness) {
        changeVersion();
        return Stylable.super.thickness(newThickness);
    }

    @Override
    public T dashStyle(DashStyle dashStyle) {
        changeVersion();
        return Stylable.super.dashStyle(dashStyle);
    }

    @Override
    public T visible(boolean visible) {
        changeVersion();
        return Stylable.super.visible(visible);
    }

    @Override
    public T color(PaintStyle dc) {
        changeVersion();
        return Stylable.super.color(dc);
    }

    @Override
    public T color(String str) {
        changeVersion();
        return Stylable.super.color(str);
    }

    @Override
    public T fillColor(PaintStyle fc) {
        changeVersion();
        return Stylable.super.fillColor(fc);

    }

    @Override
    public T fillColor(String str) {
        changeVersion();
        return Stylable.super.fillColor(str);
    }

    @Override
    public T shift(Coordinates<?> shiftVector) {
        changeVersion();
        return AffineTransformable.super.shift(shiftVector);
    }

    @Override
    public T shift(double x, double y) {
        changeVersion();
        return AffineTransformable.super.shift(x, y);
    }

    @Override
    public T shift(double x, double y, double z) {
        changeVersion();
        return AffineTransformable.super.shift(x, y, z);
    }

    @Override
    public T scale(double sx, double sy) {
        changeVersion();
        return AffineTransformable.super.scale(sx, sy);
    }

    @Override
    public T scale(double s) {
        changeVersion();
        return AffineTransformable.super.scale(s);
    }

    @Override
    public T scale(Coordinates<?> scaleCenter, double scale) {
        changeVersion();
        return AffineTransformable.super.scale(scaleCenter, scale);
    }

    @Override
    public T scale(Coordinates<?> scaleCenter, double sx, double sy) {
        changeVersion();
        return AffineTransformable.super.scale(scaleCenter, sx, sy);
    }

    @Override
    public T scale(double sx, double sy, double sz) {
        return AffineTransformable.super.scale(sx, sy, sz);
    }

    @Override
    public T scale(Coordinates<?> scaleCenter, double sx, double sy, double sz) {
        changeVersion();
        return AffineTransformable.super.scale(scaleCenter, sx, sy, sz);
    }

    @Override
    public T rotate(double angle) {
        changeVersion();
        return AffineTransformable.super.rotate(angle);
    }

    @Override
    public T rotate(Coordinates<?> center, double angle) {
        changeVersion();
        return AffineTransformable.super.rotate(center, angle);
    }

    @Override
    public T rotate3d(double anglex, double angley, double anglez) {
        changeVersion();
        return AffineTransformable.super.rotate3d(anglex, angley, anglez);
    }

    @Override
    public T rotate3d(Coordinates<?> center, double anglex, double angley, double anglez) {
        changeVersion();
        return AffineTransformable.super.rotate3d(center, anglex, angley, anglez);
    }

    @Override
    public T moveTo(Coordinates<?> p) {
        changeVersion();
        return AffineTransformable.super.moveTo(p);
    }

    @Override
    public T moveTo(double x, double y) {
        changeVersion();
        return AffineTransformable.super.moveTo(x, y);
    }

    @Override
    public T smash(Boxable containerBox, double horizontalGap, double verticalGap) {
        changeVersion();
        return AffineTransformable.super.smash(containerBox, horizontalGap, verticalGap);
    }

    @Override
    public T smash(Boxable containerBox) {
        changeVersion();
        return AffineTransformable.super.smash(containerBox);
    }
}
