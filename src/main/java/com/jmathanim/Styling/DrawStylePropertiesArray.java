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

import com.jmathanim.Enum.DashStyle;
import com.jmathanim.Enum.DotStyle;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.LatexStyle;
import com.jmathanim.Utils.Vec;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents an array of mathematical object drawing properties.
 * <p>
 * This class encapsulates a collection of Stylable instances, along with reference properties used for styling
 * and drawing. It provides functionality for managing the styling, visibility, state preservation, interpolations, and
 * visual attributes of associated Stylable. The core functionality includes copying, restoring, saving states,
 * and managing layers and colors for the drawing and filling processes.
 * <p>
 * Fields: - `objects`: A list containing the associated Stylable instances. - `mpRef`: A reference to the main
 * Stylable used for styling and rendering.
 * <p>
 * This class is intended as a utility for handling collections of Stylable within a visual or computational
 * context. Instances can be initialized with default properties or copied from existing objects, with modifications
 * propagating across associated entities.
 */
public class DrawStylePropertiesArray implements DrawStyleProperties, Stylable {

    private final MODrawPropertiesLaTeX mpRef;
    private ArrayList<DrawStyleProperties> mpArray;
    private LatexStyle latexStyle = null;
    private boolean hasBeenChanged;

    /**
     * Constructs a new MODrawPropertiesArray instance by copying the properties from the provided Stylable
     * object. Initializes an empty list of objects.
     *
     * @param mp The Stylable object from which the properties are copied to initialize the new instance.
     */
    public DrawStylePropertiesArray(DrawStyleProperties mp) {
        mpRef = new MODrawPropertiesLaTeX();
        mpRef.copyFrom(mp);
        mpArray = new ArrayList<>();
    }

    /**
     * Default constructor for the MODrawPropertiesArray class.
     * <p>
     * This constructor initializes a new instance of MODrawPropertiesArray with the following defaults: - Creates a new
     * instance of Stylable and assigns it to mpRef. - Copies the default Stylable settings from
     * JMathAnimConfig's configuration into mpRef. - Initializes the objects field as an empty ArrayList of
     * Stylable.
     */
    public DrawStylePropertiesArray() {
        mpRef = new MODrawPropertiesLaTeX();
        mpRef.copyFrom(JMathAnimConfig.getConfig().getDefaultMP());
        mpArray = new ArrayList<>();
    }

    /**
     * Constructs a new instance of MODrawPropertiesArray with a given list of Stylable instances.
     *
     * @param mpArray The list of Stylable instances to initialize the MODrawPropertiesArray.
     */
    public DrawStylePropertiesArray(ArrayList<DrawStyleProperties> mpArray) {
        this.mpArray = mpArray;
        mpRef = new MODrawPropertiesLaTeX();
    }

    public DrawStylePropertiesArray(DrawStyleProperties...mpArray) {
        this.mpArray = new ArrayList<>();
        for (DrawStyleProperties mp : mpArray) {
            this.mpArray.add(mp);
        }
        mpRef = new MODrawPropertiesLaTeX();
    }

    public int size() {
        return mpArray.size();
    }


    public DrawStyleProperties get(int index) {
        return mpArray.get(index);
    }

    public DrawStyleProperties remove(int index) {
        return mpArray.remove(index);
    }

    /**
     * Returns the LaTeXStyle of the associated AbstractLaTeXMODrawProperties
     *
     * @return A LaTeXStyle instance, or null if there is no style defined
     */
    public LatexStyle getLatexStyle() {
        return latexStyle;
    }

    /**
     * Sets the currente LaTeXStyle for the associated AbstractLaTeXMODrawProperties The LaTeXStyle class manages
     * automatic coloring of LaTeX tokens
     *
     * @param latexStyle LaTeXStyle to set
     */
    public void setLatexStyle(LatexStyle latexStyle) {
        this.latexStyle = latexStyle;
        setHasBeenChanged(true);
    }


    /**
     * Retrieves the list of Stylable instances contained within this object.
     *
     * @return An ArrayList containing Stylable instances.
     */
    public ArrayList<DrawStyleProperties> getMpArray() {
        return mpArray;
    }

    /**
     * Sets the list of Stylable instances for this object.
     *
     * @param mpArray The ArrayList of Stylable instances to be set.
     */
    public void setMpArray(ArrayList<DrawStyleProperties> mpArray) {
        this.mpArray = mpArray;
        setHasBeenChanged(true);
    }

    /**
     * Adds one or more Stylable instances to the collection of objects.
     *
     * @param objs The Stylable instances to be added.
     */
    public void add(DrawStyleProperties... objs) {
        mpArray.addAll(Arrays.asList(objs));
        setHasBeenChanged(true);
    }

    /**
     * Sets the visibility of the current object and all associated mathematical objects.
     *
     * @param visible Indicates whether the objects should be visible (true) or invisible (false).
     * @return
     */
    @Override
    public DrawStyleProperties setVisible(Boolean visible) {
        for (DrawStyleProperties obj : mpArray) {
            obj.setVisible(visible);
        }
        mpRef.setVisible(visible);
        setHasBeenChanged(true);
        return this;
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
     * Removes the specified Stylable from the underlying collection.
     *
     * @param o The Stylable to be removed from the collection.
     * @return true if the Stylable was successfully removed; false otherwise.
     */
    public boolean remove(DrawStyleProperties o) {
        setHasBeenChanged(true);
        return mpArray.remove(o);
    }

    /**
     * Creates and returns a copy of the current Stylable object.
     *
     * @return A new Stylable instance that is a copy of the current object.
     */
    @Override
    public MODrawProperties copy() {
        return this.mpRef.copy();
    }

    /**
     * Copies the properties from the given {@link DrawStyleProperties} object to the current instance. This method iterates
     * through all the objects in the current instance, copying the properties from the provided {@link DrawStyleProperties} object
     * for each object's `mp` attribute and the `mpRef` attribute of the current instance.
     *
     * @param prop The {@link DrawStyleProperties} object whose properties will be copied.
     */
    @Override
    public void copyFrom(DrawStyleProperties prop) {
        if (prop instanceof DrawStylePropertiesArray) {
            DrawStylePropertiesArray moDrawPropertiesArray = (DrawStylePropertiesArray) prop;
            copyFrom(moDrawPropertiesArray);
            return;
        }
        for (DrawStyleProperties obj : mpArray) {
            obj.copyFrom(prop);
        }
        mpRef.copyFrom(prop);
        if (prop instanceof MODrawPropertiesLaTeX) {
            MODrawPropertiesLaTeX moDrawPropertiesLaTeX = (MODrawPropertiesLaTeX) prop;
            if (moDrawPropertiesLaTeX.latexStyle != null)
                mpRef.latexStyle = moDrawPropertiesLaTeX.latexStyle;

        }
        setHasBeenChanged(true);
    }

    public void copyFrom(DrawStylePropertiesArray prop) {
        if (prop.mpArray.size() == mpArray.size()) {//Copy all styles separately
            for (int i = 0; i < prop.mpArray.size(); i++) {
                mpArray.get(i).copyFrom(prop.mpArray.get(i));
            }
            mpRef.copyFrom(prop.mpRef);
        } else {
            copyFrom(prop.mpRef);
        }
        setHasBeenChanged(true);
    }

    /**
     * Interpolates the properties of the current object from the specified destination object using the given
     * interpolation parameter.
     *
     * @param dst   The destination Stylable object from which to interpolate properties.
     * @param alpha The interpolation parameter, where 0 represents the current object and 1 represents the
     *              destination.
     */
    @Override
    public void interpolateFrom(DrawStyleProperties dst, double alpha) {
        for (DrawStyleProperties obj : mpArray) {
            obj.interpolateFrom(dst, alpha);
        }
        mpRef.interpolateFrom(dst, alpha);
        setHasBeenChanged(true);
    }

    /**
     * Interpolates the properties of the current object from two given Stylable objects using the specified
     * interpolation parameter. The interpolation is applied to all child objects and an internal reference.
     *
     * @param a     The first Stylable object representing the starting state of the interpolation.
     * @param b     The second Stylable object representing the ending state of the interpolation.
     * @param alpha The interpolation parameter. Values range from 0.0 (fully state "a") to 1.0 (fully state "b").
     */
    @Override
    public void interpolateFrom(DrawStyleProperties a, DrawStyleProperties b, double alpha) {
        for (DrawStyleProperties obj : mpArray) {
            obj.interpolateFrom(a, b, alpha);
        }
        mpRef.interpolateFrom(a, b, alpha);
        setHasBeenChanged(true);
    }

    /**
     * Applies a style to all associated MODrawPropertiess and the reference properties of this object.
     *
     * @param name The name of the style to be loaded and applied.
     */
    @Override
    public void loadFromStyle(String name) {
        for (DrawStyleProperties obj : mpArray) {
            obj.loadFromStyle(name);

        }
        mpRef.loadFromStyle(name);
        setHasBeenChanged(true);
    }

    /**
     * Copies the raw properties from another {@code Stylable} instance to this instance. The method performs a
     * deep copy by iterating over associated math objects and updating their properties, as well as updating a
     * reference to the provided properties.
     *
     * @param mp The {@code Stylable} instance from which properties will be copied.
     */
    @Override
    public void rawCopyFrom(MODrawProperties mp) {
        for (DrawStyleProperties obj : mpArray) {
            obj.rawCopyFrom((MODrawProperties) mp);
        }
        mpRef.rawCopyFrom((MODrawProperties) mp);
        setHasBeenChanged(true);
    }

    /**
     * Sets the drawing alpha value for all associated mathematical objects and the main drawing properties reference.
     *
     * @param alpha The alpha value to set for drawing, which controls the transparency level.
     * @return
     */
    @Override
    public DrawStyleProperties setDrawAlpha(double alpha) {
        for (DrawStyleProperties obj : mpArray) {
            obj.setDrawAlpha(alpha);
        }
        mpRef.setDrawAlpha(alpha);
        setHasBeenChanged(true);
        return this;
    }

    /**
     * Sets the fill alpha value for the drawing properties of all associated objects and the reference instance.
     *
     * @param alpha The alpha value to set for the fill property. This represents the opacity level, where 0 is fully
     *              transparent and 1 is fully opaque.
     * @return
     */
    @Override
    public DrawStyleProperties setFillAlpha(double alpha) {
        for (DrawStyleProperties obj : mpArray) {
            obj.setFillAlpha(alpha);
        }
        mpRef.setFillAlpha(alpha);
        setHasBeenChanged(true);
        return this;
    }

    /**
     * Multiplies the draw alpha value for all contained MODrawPropertiess and a reference Stylable instance.
     *
     * @param mult The multiplier to apply to the draw alpha value.
     * @return
     */
    @Override
    public DrawStyleProperties multDrawAlpha(double mult) {
        for (DrawStyleProperties obj : mpArray) {
            obj.multDrawAlpha(mult);
        }
        mpRef.multDrawAlpha(mult);
        setHasBeenChanged(true);
        return this;
    }

    /**
     * Multiplies the fill alpha value of each contained object and a reference to the Stylable by the specified
     * multiplier.
     *
     * @param mult The multiplier to apply to the fill alpha values.
     * @return
     */
    @Override
    public DrawStyleProperties multFillAlpha(double mult) {
        for (DrawStyleProperties obj : mpArray) {
            obj.multFillAlpha(mult);
        }
        mpRef.multFillAlpha(mult);
        setHasBeenChanged(true);
        return this;
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
     * Sets the layer for all Stylable instances in the list and updates the reference object's layer.
     *
     * @param layer The layer value to be set for each Stylable and the reference object.
     * @return
     */
    @Override
    public DrawStyleProperties setLayer(int layer) {
        for (DrawStyleProperties obj : mpArray) {
            obj.setLayer(layer);
        }
        mpRef.setLayer(layer);
        setHasBeenChanged(true);
        return this;
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
     * @return
     */
    @Override
    public DrawStyleProperties setDrawColor(PaintStyle drawColor) {
        for (DrawStyleProperties obj : mpArray) {
            obj.setDrawColor(drawColor);
        }
        mpRef.setDrawColor(drawColor);
        setHasBeenChanged(true);
        return this;
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
     * Sets the fill color for all the {@link DrawStyleProperties} instances in the collection and updates the reference
     * {@link DrawStyleProperties}.
     *
     * @param fillColor The {@link PaintStyle} instance representing the fill color to be applied.
     * @return
     */
    @Override
    public DrawStyleProperties setFillColor(PaintStyle fillColor) {
        for (DrawStyleProperties obj : mpArray) {
            obj.setFillColor(fillColor);
        }
        mpRef.setFillColor(fillColor);
        setHasBeenChanged(true);
        return this;
    }

    /**
     * Retrieves the first instance of the `Stylable` associated with this object.
     *
     * @return The first `Stylable` instance, referenced by `mpRef`.
     */
    @Override
    public MODrawPropertiesLaTeX getFirstMP() {
        return mpRef;
    }

    @Override
    public boolean hasBeenChanged() {
       return this.hasBeenChanged;
    }

    @Override
    public void setHasBeenChanged(boolean hasBeenChanged) {
        this.hasBeenChanged=hasBeenChanged;
    }

    /**
     * Retrieves the currently defined line cap style for the stroke.
     *
     * @return The {@code StrokeLineCap} value representing the style of the line cap. This determines how the end of a
     * line or path is rendered.
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
     * Sets the style of the line join for all contained Stylable instances and the reference drawing
     * properties.
     *
     * @param linejoin the {@link StrokeLineJoin} style to be applied to configure the way lines in shapes are joined.
     * @return
     */
    @Override
    public DrawStyleProperties setLineJoin(StrokeLineJoin linejoin) {
        for (DrawStyleProperties obj : mpArray) {
            obj.setLineJoin(linejoin);
        }
        mpRef.setLineJoin(linejoin);
        setHasBeenChanged(true);
        return this;
    }

    /**
     * Sets the line cap style for the stroke in all objects within the array and the reference object.
     *
     * @param linecap The {@link StrokeLineCap} style to be applied. Determines the shape used at the ends of open paths
     *                when stroked.
     * @return
     */
    @Override
    public DrawStyleProperties setLinecap(StrokeLineCap linecap) {
        for (DrawStyleProperties obj : mpArray) {
            obj.setLinecap(linecap);
        }
        mpRef.setLinecap(linecap);
        setHasBeenChanged(true);
        return this;
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
     * Sets the thickness for all associated MODrawPropertiess and the reference Stylable properties.
     *
     * @param thickness The thickness value to be applied to the MODrawPropertiess and the reference property.
     * @return
     */
    @Override
    public DrawStyleProperties setThickness(Double thickness) {
        for (DrawStyleProperties obj : mpArray) {
            obj.setThickness(thickness);
        }
        mpRef.setThickness(thickness);
        setHasBeenChanged(true);
        return this;
    }

    /**
     * Retrieves the dot style associated with this object.
     *
     * @return The current DotStyle as defined in the referenced object.
     */
    @Override
    public DotStyle getDotStyle() {
        return mpRef.getDotStyle();
    }

    /**
     * Sets the dot style of all Stylable instances in the collection as well as the referenced Stylable
     * object.
     *
     * @param dotStyle The dot style to apply. This parameter is of type Point.DotStyle, which determines the style of
     *                 dots to be set.
     * @return
     */
    @Override
    public DrawStyleProperties setDotStyle(DotStyle dotStyle) {
        for (DrawStyleProperties obj : mpArray) {
            obj.setDotStyle(dotStyle);
        }
        mpRef.setDotStyle(dotStyle);
        setHasBeenChanged(true);
        return this;
    }

    @Override
    public DrawStyleProperties getMp() {
        return this;
    }
    /**
     * Retrieves the dash style property from the referenced Stylable.
     *
     * @return The dash style of type {@link DashStyle}.
     */
    @Override
    public DashStyle getDashStyle() {
        return mpRef.getDashStyle();
    }

    /**
     * Sets the dash style for all associated objects and the main properties reference.
     *
     * @param dashStyle The dash style to apply. This defines the pattern of dashes for drawing lines.
     * @return
     */
    @Override
    public DrawStyleProperties setDashStyle(DashStyle dashStyle) {
        for (DrawStyleProperties obj : mpArray) {
            obj.setDashStyle(dashStyle);
        }
        mpRef.setDashStyle(dashStyle);
        setHasBeenChanged(true);
        return this;
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
     * Sets the absolute thickness property for all objects in the array, as well as the reference object. The absolute
     * thickness determines whether the thickness of the objects is absolute or relative.
     *
     * @param absThickness A Boolean value indicating whether the thickness should be treated as absolute (true) or not
     *                     (false).
     * @return
     */
    @Override
    public DrawStyleProperties setAbsoluteThickness(Boolean absThickness) {
        for (DrawStyleProperties obj : mpArray) {
            obj.setAbsoluteThickness(absThickness);
        }
        mpRef.setAbsoluteThickness(absThickness);
        setHasBeenChanged(true);
        return this;
    }

    /**
     * Multiplies the thickness of all MODrawPropertiess in this MODrawPropertiesArray as well as the referenced
     * Stylable by the specified factor.
     *
     * @param multT The factor by which to multiply the thickness of the objects.
     * @return
     */
    @Override
    public DrawStyleProperties multThickness(double multT) {
        for (DrawStyleProperties obj : mpArray) {
            obj.multThickness(multT);
        }
        mpRef.multThickness(multT);
        setHasBeenChanged(true);
        return this;
    }

    /**
     * Determines whether the associated object is oriented to face the camera.
     *
     * @return Boolean value indicating if the object is facing the camera. Returns true if it is, otherwise false.
     */
    @Override
    public Boolean isFaceToCamera() {
        return mpRef.isFaceToCamera();
    }

    /**
     * Sets the face-to-camera property for each {@code Stylable} in the collection and updates the reference
     * object. This property determines whether the object should always face the camera or not.
     *
     * @param faceToCamera A {@code Boolean} value indicating whether the objects should face the camera. If
     *                     {@code true}, the objects will face the camera; otherwise, they won't.
     * @return
     */
    @Override
    public DrawStyleProperties setFaceToCamera(Boolean faceToCamera) {
        for (DrawStyleProperties obj : mpArray) {
            obj.setFaceToCamera(faceToCamera);
        }
        mpRef.setFaceToCamera(faceToCamera);
        setHasBeenChanged(true);
        return this;
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
     * Sets the pivot point for the "face to camera" functionality for this object and all its sub-objects. The pivot
     * point determines the reference point around which the object will face the camera.
     *
     * @param pivot The {@code Vec} instance representing the pivot point.
     * @return
     */
    @Override
    public DrawStyleProperties setFaceToCameraPivot(Vec pivot) {
        for (DrawStyleProperties obj : mpArray) {
            obj.setFaceToCameraPivot(pivot);
        }
        mpRef.setFaceToCameraPivot(pivot);
        setHasBeenChanged(true);
        return this;
    }

}
