/*
 * Copyright (C) 2021 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
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
package com.jmathanim.Styling;

import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.LatexStyle;
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Stateable;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents an array of mathematical object drawing properties.
 * <p>
 * This class encapsulates a collection of MathObject instances, along with
 * reference properties used for styling and drawing. It provides functionality
 * for managing the styling, visibility, state preservation, interpolations,
 * and visual attributes of associated MathObjects. The core functionality
 * includes copying, restoring, saving states, and managing layers and colors
 * for the drawing and filling processes.
 * <p>
 * Fields:
 * - `objects`: A list containing the associated MathObject instances.
 * - `mpRef`: A reference to the main MODrawProperties used for styling and rendering.
 * <p>
 * This class is intended as a utility for handling collections of styled and
 * drawable mathematical objects within a visual or computational context. Instances
 * can be initialized with default properties or copied from existing objects, with
 * modifications propagating across associated entities.
 */
public class MODrawPropertiesArray implements Stylable, Stateable {

    private final MODrawPropertiesLaTeX mpRef;
    private ArrayList<MathObject> objects;
    private LatexStyle latexStyle = null;

    /**
     * Constructs a new MODrawPropertiesArray instance by copying the properties from the provided MODrawProperties object.
     * Initializes an empty list of objects.
     *
     * @param mp The MODrawProperties object from which the properties are copied to initialize the new instance.
     */
    public MODrawPropertiesArray(MODrawProperties mp) {
        mpRef = new MODrawPropertiesLaTeX();
        mpRef.copyFrom(mp);
        objects = new ArrayList<>();
    }

    /**
     * Default constructor for the MODrawPropertiesArray class.
     * <p>
     * This constructor initializes a new instance of MODrawPropertiesArray with the following defaults:
     * - Creates a new instance of MODrawProperties and assigns it to mpRef.
     * - Copies the default MODrawProperties settings from JMathAnimConfig's configuration into mpRef.
     * - Initializes the objects field as an empty ArrayList of MathObject.
     */
    public MODrawPropertiesArray() {
        mpRef = new MODrawPropertiesLaTeX();
        mpRef.copyFrom(JMathAnimConfig.getConfig().getDefaultMP());
        objects = new ArrayList<>();
    }

    /**
     * Constructs a new instance of MODrawPropertiesArray with a given list of MathObject instances.
     *
     * @param objects The list of MathObject instances to initialize the MODrawPropertiesArray.
     */
    public MODrawPropertiesArray(ArrayList<MathObject> objects) {
        this.objects = objects;
        mpRef = new MODrawPropertiesLaTeX();
    }


    /**
     * Returns the LaTeXStyle of the associated AbstractLaTeXMathObject
     *
     * @return A LaTeXStyle instance, or null if there is no style defined
     */
    public LatexStyle getLatexStyle() {
        return latexStyle;
    }

    /**
     * Sets the currente LaTeXStyle for the associated AbstractLaTeXMathObject
     * The LaTeXStyle class manages automatic coloring of LaTeX tokens
     *
     * @param latexStyle LaTeXStyle to set
     */
    public void setLatexStyle(LatexStyle latexStyle) {
        this.latexStyle = latexStyle;
    }


    /**
     * Retrieves the list of MathObject instances contained within this object.
     *
     * @return An ArrayList containing MathObject instances.
     */
    public ArrayList<MathObject> getObjects() {
        return objects;
    }

    /**
     * Sets the list of MathObject instances for this object.
     *
     * @param objects The ArrayList of MathObject instances to be set.
     */
    public void setObjects(ArrayList<MathObject> objects) {
        this.objects = objects;
    }

    /**
     * Adds one or more MathObject instances to the collection of objects.
     *
     * @param objs The MathObject instances to be added.
     */
    public void add(MathObject... objs) {
        objects.addAll(Arrays.asList(objs));
    }

    /**
     * Sets the visibility of the current object and all associated mathematical objects.
     *
     * @param visible Indicates whether the objects should be visible (true) or invisible (false).
     */
    @Override
    public void setVisible(Boolean visible) {
        for (MathObject obj : objects) {
            obj.getMp().setVisible(visible);
        }
        mpRef.setVisible(visible);
    }

    /**
     * Determines whether the current object is visible.
     *
     * @return True if the object is visible, false otherwise.
     */
    @Override
    public Boolean isVisible() {
        return mpRef.isVisible();
    }

    /**
     * Removes the specified MathObject from the underlying collection.
     *
     * @param o The MathObject to be removed from the collection.
     * @return true if the MathObject was successfully removed; false otherwise.
     */
    public boolean remove(MathObject o) {
        return objects.remove(o);
    }

    /**
     * Creates and returns a copy of the current MODrawProperties object.
     *
     * @return A new MODrawProperties instance that is a copy of the current object.
     */
    @Override
    public MODrawProperties copy() {
        return this.mpRef.copy();
    }

    /**
     * Copies the properties from the given {@link Stylable} object to the current instance.
     * This method iterates through all the objects in the current instance, copying
     * the properties from the provided {@link Stylable} object for each object's `mp`
     * attribute and the `mpRef` attribute of the current instance.
     *
     * @param prop The {@link Stylable} object whose properties will be copied.
     */
    @Override
    public void copyFrom(Stylable prop) {
        for (MathObject obj : objects) {
            obj.getMp().copyFrom(prop);
        }
        mpRef.copyFrom(prop);
        if (prop instanceof MODrawPropertiesLaTeX) {
            MODrawPropertiesLaTeX moDrawPropertiesLaTeX = (MODrawPropertiesLaTeX) prop;
            if (moDrawPropertiesLaTeX.latexStyle != null)
                mpRef.latexStyle = moDrawPropertiesLaTeX.latexStyle;

        }
    }

    public void copyFrom(MODrawPropertiesArray prop) {
        if (prop.objects.size() == objects.size()) {//Copy all styles separately
            for (int i = 0; i < prop.objects.size(); i++) {
                objects.get(i).getMp().copyFrom(prop.objects.get(i).getMp());
            }
            mpRef.copyFrom(prop.mpRef);
        } else {
            copyFrom(prop.mpRef);
        }
    }

    /**
     * Interpolates the properties of the current object from the specified destination object
     * using the given interpolation parameter.
     *
     * @param dst   The destination Stylable object from which to interpolate properties.
     * @param alpha The interpolation parameter, where 0 represents the current object and 1 represents the destination.
     */
    @Override
    public void interpolateFrom(Stylable dst, double alpha) {
        for (MathObject obj : objects) {
            obj.getMp().interpolateFrom(dst, alpha);
        }
        mpRef.interpolateFrom(dst, alpha);
    }

    /**
     * Interpolates the properties of the current object from two given Stylable objects
     * using the specified interpolation parameter. The interpolation is applied to all
     * child objects and an internal reference.
     *
     * @param a     The first Stylable object representing the starting state of the interpolation.
     * @param b     The second Stylable object representing the ending state of the interpolation.
     * @param alpha The interpolation parameter. Values range from 0.0 (fully state "a") to 1.0 (fully state "b").
     */
    @Override
    public void interpolateFrom(Stylable a, Stylable b, double alpha) {
        for (MathObject obj : objects) {
            obj.getMp().interpolateFrom(a, b, alpha);
        }
        mpRef.interpolateFrom(a, b, alpha);
    }

    /**
     * Applies a style to all associated MathObjects and the reference properties of this object.
     *
     * @param name The name of the style to be loaded and applied.
     */
    @Override
    public void loadFromStyle(String name) {
        for (MathObject obj : objects) {
            obj.getMp().loadFromStyle(name);

        }
        mpRef.loadFromStyle(name);
    }

    /**
     * Copies the raw properties from another {@code MODrawProperties} instance to this instance.
     * The method performs a deep copy by iterating over associated math objects and updating
     * their properties, as well as updating a reference to the provided properties.
     *
     * @param mp The {@code MODrawProperties} instance from which properties will be copied.
     */
    @Override
    public void rawCopyFrom(MODrawProperties mp) {
        for (MathObject obj : objects) {
            obj.getMp().rawCopyFrom(mp);
        }
        mpRef.rawCopyFrom(mp);
    }

    /**
     * Restores the state of this MODrawPropertiesArray object and its associated MathObjects and properties.
     * This method iterates through the list of MathObjects contained in the `objects` field
     * and calls the `restoreState` method on each object's `MathProperties` instance. It
     * ensures that all associated MathObject properties are reverted to their previously
     * saved state.
     * <p>
     * Additionally, it calls the `restoreState` method on the `mpRef` field (a reference
     * to this object's MathProperties), restoring the preserved state of its main properties.
     */
    @Override
    public void restoreState() {
        for (MathObject obj : objects) {
            obj.getMp().restoreState();
        }
        mpRef.restoreState();

    }

    /**
     * Saves the current state of the object and its referenced components.
     * This method iterates over all MathObject instances in the collection and
     * invokes their saveState method through their corresponding MODrawProperties (mp).
     * Additionally, the saveState of the referenced MODrawProperties object (mpRef)
     * is also called to ensure all relevant state data is preserved.
     */
    @Override
    public void saveState() {
        for (MathObject obj : objects) {
            obj.getMp().saveState();
        }
        mpRef.saveState();
    }

    /**
     * Sets the drawing alpha value for all associated mathematical objects and the main drawing properties reference.
     *
     * @param alpha The alpha value to set for drawing, which controls the transparency level.
     */
    @Override
    public void setDrawAlpha(double alpha) {
        for (MathObject obj : objects) {
            obj.getMp().setDrawAlpha(alpha);
        }
        mpRef.setDrawAlpha(alpha);

    }

    /**
     * Sets the fill alpha value for the drawing properties of all associated objects
     * and the reference instance.
     *
     * @param alpha The alpha value to set for the fill property. This represents the
     *              opacity level, where 0 is fully transparent and 1 is fully opaque.
     */
    @Override
    public void setFillAlpha(double alpha) {
        for (MathObject obj : objects) {
            obj.getMp().setFillAlpha(alpha);
        }
        mpRef.setFillAlpha(alpha);
    }

    /**
     * Multiplies the draw alpha value for all contained MathObjects and a reference MODrawProperties instance.
     *
     * @param mult The multiplier to apply to the draw alpha value.
     */
    @Override
    public void multDrawAlpha(double mult) {
        for (MathObject obj : objects) {
            obj.getMp().multDrawAlpha(mult);
        }
        mpRef.multDrawAlpha(mult);
    }

    /**
     * Multiplies the fill alpha value of each contained object and a reference
     * to the MODrawProperties by the specified multiplier.
     *
     * @param mult The multiplier to apply to the fill alpha values.
     */
    @Override
    public void multFillAlpha(double mult) {
        for (MathObject obj : objects) {
            obj.getMp().multFillAlpha(mult);
        }
        mpRef.multFillAlpha(mult);
    }

    /**
     * Retrieves the layer value associated with the object.
     *
     * @return the layer value as an Integer, or the default value if it is not set.
     */
    @Override
    public Integer getLayer() {
        return mpRef.getLayer();
    }

    /**
     * Sets the layer for all MathObject instances in the list and updates the reference object's layer.
     *
     * @param layer The layer value to be set for each MathObject and the reference object.
     */
    @Override
    public void setLayer(int layer) {
        for (MathObject obj : objects) {
            obj.getMp().setLayer(layer);
        }
        mpRef.setLayer(layer);
    }

    /**
     * Retrieves the current drawing color associated with this object.
     *
     * @return The drawing color represented as a PaintStyle object.
     */
    public PaintStyle getDrawColor() {
        return mpRef.getDrawColor();
    }

    /**
     * Sets the draw color of the current object and all its sub-objects.
     *
     * @param drawColor The {@link PaintStyle} instance representing the desired draw color.
     */
    @Override
    public void setDrawColor(PaintStyle drawColor) {
        for (MathObject obj : objects) {
            obj.getMp().setDrawColor(drawColor);
        }
        mpRef.setDrawColor(drawColor);
    }

    /**
     * Retrieves the fill color associated with the current object.
     *
     * @return The current {@link PaintStyle} representing the fill color.
     */
    @Override
    public PaintStyle getFillColor() {
        return mpRef.getFillColor();
    }

    /**
     * Sets the fill color for all the {@link MathObject} instances in the collection
     * and updates the reference {@link MODrawProperties}.
     *
     * @param fillColor The {@link PaintStyle} instance representing the fill color
     *                  to be applied.
     */
    @Override
    public void setFillColor(PaintStyle fillColor) {
        for (MathObject obj : objects) {
            obj.getMp().setFillColor(fillColor);
        }
        mpRef.setFillColor(fillColor);
    }

    /**
     * Retrieves a specific sub drawing property from the list of objects based on
     * the given index.
     *
     * @param n The index of the object in the list from which to retrieve the sub
     *          drawing property.
     * @return The Stylable associated with the specified index.
     */
    @Override
    public Stylable getSubMP(int n) {
        return objects.get(n).getMp();
    }

    /**
     * Retrieves the first instance of the `MODrawProperties` associated with this object.
     *
     * @return The first `MODrawProperties` instance, referenced by `mpRef`.
     */
    @Override
    public MODrawPropertiesLaTeX getFirstMP() {
        return mpRef;
    }

    /**
     * Retrieves the currently defined line cap style for the stroke.
     *
     * @return The {@code StrokeLineCap} value representing the style of the line cap.
     * This determines how the end of a line or path is rendered.
     */
    @Override
    public StrokeLineCap getLineCap() {
        return mpRef.getLineCap();
    }

    /**
     * Retrieves the line join style used for stroking shapes.
     *
     * @return the StrokeLineJoin value representing the line join style (e.g., MITER, BEVEL, ROUND).
     */
    @Override
    public StrokeLineJoin getLineJoin() {
        return mpRef.getLineJoin();
    }

    /**
     * Sets the style of the line join for all contained MathObject instances and the reference drawing properties.
     *
     * @param linejoin the {@link StrokeLineJoin} style to be applied to configure the way lines in shapes are joined.
     */
    @Override
    public void setLineJoin(StrokeLineJoin linejoin) {
        for (MathObject obj : objects) {
            obj.getMp().setLineJoin(linejoin);
        }
        mpRef.setLineJoin(linejoin);
    }

    /**
     * Sets the line cap style for the stroke in all objects within the array and the reference object.
     *
     * @param linecap The {@link StrokeLineCap} style to be applied. Determines the shape used at the ends of open paths when stroked.
     */
    @Override
    public void setLinecap(StrokeLineCap linecap) {
        for (MathObject obj : objects) {
            obj.getMp().setLinecap(linecap);
        }
        mpRef.setLinecap(linecap);
    }

    /**
     * Returns the thickness value associated with the object.
     *
     * @return the thickness as a Double, or null if not set.
     */
    @Override
    public Double getThickness() {
        return mpRef.getThickness();
    }

    /**
     * Sets the thickness for all associated MathObjects and the reference MathObject properties.
     *
     * @param thickness The thickness value to be applied to the MathObjects and the reference property.
     */
    @Override
    public void setThickness(Double thickness) {
        for (MathObject obj : objects) {
            obj.getMp().setThickness(thickness);
        }
        mpRef.setThickness(thickness);
    }

    /**
     * Retrieves the dot style associated with this object.
     *
     * @return The current DotStyle as defined in the referenced object.
     */
    @Override
    public Point.DotSyle getDotStyle() {
        return mpRef.getDotStyle();
    }

    /**
     * Sets the dot style of all MathObject instances in the collection as well as the referenced MODrawProperties object.
     *
     * @param dotStyle The dot style to apply. This parameter is of type Point.DotStyle, which determines the style of dots to be set.
     */
    @Override
    public void setDotStyle(Point.DotSyle dotStyle) {
        for (MathObject obj : objects) {
            obj.getMp().setDotStyle(dotStyle);
        }
        mpRef.setDotStyle(dotStyle);
    }

    /**
     * Retrieves the dash style property from the referenced MODrawProperties.
     *
     * @return The dash style of type {@link MODrawProperties.DashStyle}.
     */
    @Override
    public MODrawProperties.DashStyle getDashStyle() {
        return mpRef.getDashStyle();
    }

    /**
     * Sets the dash style for all associated objects and the main properties reference.
     *
     * @param dashStyle The dash style to apply. This defines the pattern of dashes for drawing lines.
     */
    @Override
    public void setDashStyle(MODrawProperties.DashStyle dashStyle) {
        for (MathObject obj : objects) {
            obj.getMp().setDashStyle(dashStyle);
        }
        mpRef.setDashStyle(dashStyle);
    }

    /**
     * Checks if the thickness of the current object is defined as absolute.
     *
     * @return A Boolean indicating whether the thickness is absolute (true) or relative (false).
     */
    @Override
    public Boolean isAbsoluteThickness() {
        return mpRef.isAbsoluteThickness();
    }

    /**
     * Sets the absolute thickness property for all objects in the array, as well as the reference object.
     * The absolute thickness determines whether the thickness of the objects is absolute or relative.
     *
     * @param absThickness A Boolean value indicating whether the thickness should be treated as absolute (true) or not (false).
     */
    @Override
    public void setAbsoluteThickness(Boolean absThickness) {
        for (MathObject obj : objects) {
            obj.getMp().setAbsoluteThickness(absThickness);
        }
        mpRef.setAbsoluteThickness(absThickness);
    }

    /**
     * Multiplies the thickness of all MathObjects in this MODrawPropertiesArray
     * as well as the referenced MODrawProperties by the specified factor.
     *
     * @param multT The factor by which to multiply the thickness of the objects.
     */
    @Override
    public void multThickness(double multT) {
        for (MathObject obj : objects) {
            obj.getMp().multThickness(multT);
        }
        mpRef.multThickness(multT);
    }

    /**
     * Determines whether the associated object is oriented to face the camera.
     *
     * @return Boolean value indicating if the object is facing the camera. Returns true if it is,
     * otherwise false.
     */
    @Override
    public Boolean isFaceToCamera() {
        return mpRef.isFaceToCamera();
    }

    /**
     * Sets the face-to-camera property for each {@code MathObject} in the collection and updates the reference object.
     * This property determines whether the object should always face the camera or not.
     *
     * @param faceToCamera A {@code Boolean} value indicating whether the objects should face the camera.
     *                     If {@code true}, the objects will face the camera; otherwise, they won't.
     */
    @Override
    public void setFaceToCamera(Boolean faceToCamera) {
        for (MathObject obj : objects) {
            obj.getMp().setFaceToCamera(faceToCamera);
        }
        mpRef.setFaceToCamera(faceToCamera);
    }

    /**
     * Retrieves the pivot point used for aligning the object to face the camera.
     *
     * @return A Vec object representing the pivot point for the face-to-camera alignment.
     */
    @Override
    public Vec getFaceToCameraPivot() {
        return mpRef.getFaceToCameraPivot();
    }

    /**
     * Sets the pivot point for the "face to camera" functionality for this object and all its sub-objects.
     * The pivot point determines the reference point around which the object will face the camera.
     *
     * @param pivot The {@code Vec} instance representing the pivot point.
     */
    @Override
    public void setFaceToCameraPivot(Vec pivot) {
        for (MathObject obj : objects) {
            obj.getMp().setFaceToCameraPivot(pivot);
        }
        mpRef.setFaceToCameraPivot(pivot);
    }

    /**
     * Retrieves the scaling factor for the first arrowhead associated with the current object.
     *
     * @return A {@code Double} representing the scaling factor of the first arrowhead, or {@code null} if not set.
     */
    @Override
    public Double getScaleArrowHead1() {
        return mpRef.getScaleArrowHead1();
    }

    /**
     * Sets the scale of the first arrowhead for all associated MathObjects and the reference draw properties.
     *
     * @param scale The new scale value for the first arrowhead, represented as a Double.
     */
    @Override
    public void setScaleArrowHead1(Double scale) {
        for (MathObject obj : objects) {
            obj.getMp().setScaleArrowHead1(scale);
        }
        mpRef.setScaleArrowHead1(scale);
    }

    /**
     * Retrieves the scaling factor for the second arrowhead.
     *
     * @return The scaling factor applied to the second arrowhead as a Double.
     */
    @Override
    public Double getScaleArrowHead2() {
        return mpRef.getScaleArrowHead2();
    }

    /**
     * Sets the scaling factor for the second arrowhead of the objects and the reference properties.
     *
     * @param scale The scaling factor to be applied to the second arrowhead.
     */
    @Override
    public void setScaleArrowHead2(Double scale) {
        for (MathObject obj : objects) {
            obj.getMp().setScaleArrowHead2(scale);
        }
        mpRef.setScaleArrowHead2(scale);
    }

}
