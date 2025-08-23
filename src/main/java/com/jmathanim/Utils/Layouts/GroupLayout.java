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
package com.jmathanim.Utils.Layouts;

import com.jmathanim.Enum.InnerAnchorType;
import com.jmathanim.Utils.Rect;
import com.jmathanim.mathobjects.AbstractMathGroup;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MathObjectGroup;
import com.jmathanim.mathobjects.Shape;

import java.util.ArrayList;

/**
 * A basic abstract class to implement any layout that can be applied to a
 * MathObjectGroup
 *
 * @author David Gutiérrez Rubio
 */
public abstract class GroupLayout {

    private final ArrayList<double[]> backupGaps;
    private InnerAnchorType innerAnchor;
    private double ugap, rgap, logap, lgap;

    public GroupLayout() {
        backupGaps = new ArrayList<>();
        innerAnchor = null;
        ugap = 0;
        rgap = 0;
        logap = 0;
        lgap = 0;
    }


    /**
     * Executes the layout logic specific to the implementing class
     * for the provided MathObjectGroup. This method is responsible
     * for arranging the elements within the group as per the layout's
     * definition. This method should not be called directly, but it is
     * automatically called from the applyLayout method.
     *
     * @param group The MathObjectGroup to which the layout logic will
     *              be applied.
     */
    protected abstract void executeLayout(AbstractMathGroup<?> group);

    /**
     * Applies the layout configuration to the specified MathObjectGroup.
     * This method ensures proper arrangement of elements within the group
     * by optionally homogenizing bounding boxes and executing the defined layout logic.
     *
     * @param group The MathObjectGroup to which the layout is applied.
     */
    public final void applyLayout(AbstractMathGroup<?> group) {
        if (innerAnchor != null) {//Should homogeneize bounding boxes first
            saveGaps(group);
            group.homogeneizeBoundingBoxes(innerAnchor, rgap, ugap, logap, logap);
        }
        executeLayout(group);
        if (innerAnchor != null) {
            restoreGaps(group);
        }
    }

    private void saveGaps(AbstractMathGroup<?> group) {
        backupGaps.clear();
        for (MathObject ob : group) {
            backupGaps.add(ob.getGaps());
        }
    }

    private void restoreGaps(AbstractMathGroup<?> group) {
        for (int i = 0; i < group.size(); i++) {
            double[] gaps = backupGaps.get(i);
            group.get(i).setGaps(
                    gaps[0],
                    gaps[1],
                    gaps[2],
                    gaps[3]);

        }
    }

    /**
     * Specify that bounding boxes of elements should be homogenized when
     * applying the layout. All elements will have bounding boxes with the
     * maximum height and width of group elements and the gaps specified as
     * parameters. The original bounding box is located according to the anchor
     * specified (CENTER, UPPER, etc.)
     *
     * @param <T>      Calling class
     * @param anchor   How to locate the original bounding box inside the new one
     * @param upperGap Upper gap to add
     * @param rightGap Right gap to add
     * @param lowerGap Lower gap to add
     * @param leftGap  Left gap to add
     * @return This object
     */
    public <T extends GroupLayout> T homogenize(InnerAnchorType anchor, double upperGap, double rightGap, double lowerGap, double leftGap) {
        innerAnchor = anchor;
        this.ugap = upperGap;
        this.rgap = rightGap;
        this.logap = lowerGap;
        this.lgap = leftGap;
        return (T) this;
    }

    /**
     * Returns the bounding box that will have the specified group if layout is
     * applied. The group is unaltered.
     *
     * @param group MathObjectGroup to apply layout
     * @return The bounding box
     */
    public Rect getBoundingBox(MathObjectGroup group) {
        if (group.isEmpty()) {// Nothing to show
            return null;
        }
        MathObjectGroup boxedGroup = createBoxedGroup(group, 0, 0);
        applyLayout(boxedGroup);
        Rect bbox = boxedGroup.getBoundingBox();
        return bbox;
    }

    /**
     * Creates a simpler group with rectangles representing the bounding boxes
     *
     * @param group The MathObjectGroup to compute bounding boxes
     * @param hgap  Horizontal gap. The height of the rectangles will be
     *              increased by this gap.
     * @param vgap  Vertical gap. The width of the rectangles will be increased
     *              by this gap.
     * @return A new MathObjectGroup, with rectangles representing the bounding
     * boxes
     */
    protected MathObjectGroup createBoxedGroup(MathObjectGroup group, double hgap, double vgap) {
        MathObjectGroup resul = MathObjectGroup.make();
        for (MathObject ob : group) {
            resul.add(Shape.rectangle(ob.getBoundingBox().addGap(hgap, vgap)));
        }
        return resul;
    }

    public abstract <T extends GroupLayout> T copy();
}
