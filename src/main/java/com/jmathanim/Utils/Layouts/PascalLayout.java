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

import com.jmathanim.Enum.AnchorType;
import com.jmathanim.Enum.LayoutType;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.*;

/**
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class PascalLayout extends GroupLayout {

    private Coordinates<?> referenceCoordinates;// Upper point of the triangle
    private double horizontalGap;
    private double verticalGap;

    protected PascalLayout() {
        this.referenceCoordinates = null;
        this.horizontalGap = 0;
        this.verticalGap = 0;
    }

    /**
     * Creates a new PascalLayout with given parameters
     *
     * @param referenceCoordinates Reference coordinates. Objects will be stacked to this coordinates. If null, the
     *                             first object will be used as reference
     * @param horizontalGap Horizontal gap between elements in the same row
     * @param verticalGap Vertical gap between rows
     * @return The new PascalLayout
     */
    public static PascalLayout make(Coordinates<?> referenceCoordinates, double horizontalGap, double verticalGap) {
        return new PascalLayout().setReferenceCoordinates(referenceCoordinates).setGaps(horizontalGap, verticalGap);
    }

    public static PascalLayout make() {
        return new PascalLayout();
    }

    @Override
    public void executeLayout(AbstractMathGroup<?> group) {
        if (group.isEmpty()) {
            return; // Nothing to do here...
        }
        double w = 0;
        double h = 0;
        for (MathObject g : group) {
            w = Math.max(w, g.getWidth());
            h = Math.max(h, g.getHeight());
        }
        Rect r = new Rect(0, 0, w, h);
        r.addGap(.5 * this.horizontalGap, .5 * this.verticalGap);
        MathObjectGroup workGroup = MathObjectGroup.make();

        // Complete the last row in case it is not complete
        // Find the first triangular number bigger than the group size
        int num = 0;
        int k = 0;
        while (num < group.size()) {
            num = k * (k + 1) / 2;
            k++;
        }
        for (int n = 0; n < num; n++) {

            workGroup.add(Shape.rectangle(r));

        }
        // Add the first element
        int rowSize = 1;
        int counter = 0;
        int rowCounter = 0;
        MathObjectGroup rows = MathObjectGroup.make();
        while (counter < workGroup.size()) {// Main loop
            MathObjectGroup grRow = MathObjectGroup.make();
            while (rowCounter < rowSize) {
                if (counter < workGroup.size()) {
                    grRow.add(workGroup.get(counter));
                }
                counter++;
                rowCounter++;
            }
            grRow.setLayout(LayoutType.RIGHT, 0);
            rows.add(grRow);
            rowSize++;
            rowCounter = 0;
        }
        rows.setLayout(LayoutType.LOWER, 0);

        // Now that the rectangles are properly located, move the original objects so
        // that their centers match those of the workgroup
        for (int n = 0; n < group.size(); n++) {
            group.get(n).stackTo(workGroup.get(n), AnchorType.CENTER);
        }
        // Move now so that the top center point of first element match the Point top.
        Vec A = Anchor.getAnchorPoint(group.get(0), AnchorType.UPPER);
        if (referenceCoordinates != null) {
            group.shift(A.to(referenceCoordinates));
        }
    }

    /**
     * Returns a MathObjectGroup containing other MathObjectGroup objects, one for each row. So for example,
     * getRowGroups(group).get(3) will return a MathObjectGroup with all objects from the fourth row (that will hold 4
     * objects)
     *
     * @param group The group to get the rows from
     * @return A MathObjectGroup with all rows
     */
    public MathObjectGroup getRowGroups(MathObjectGroup group) {
        int rowSize = 1;
        int counter = 0;
        int rowCounter = 0;
        MathObjectGroup rows = MathObjectGroup.make();
        while (counter < group.size()) {// Main loop
            MathObjectGroup grRow = MathObjectGroup.make();
            while (rowCounter < rowSize) {
                if (counter < group.size()) {
                    grRow.add(group.get(counter));
                }
                counter++;
                rowCounter++;
            }
            rows.add(grRow);
            rowSize++;
            rowCounter = 0;
        }
        return rows;
    }

    @Override
    public PascalLayout copy() {
        PascalLayout copy = new PascalLayout();
        copy.setHorizontalGap(horizontalGap)
                .setVerticalGap(verticalGap)
                .setReferenceCoordinates(referenceCoordinates);
        return copy;
    }

    /**
     * Sets the reference coordinates (the top of the Pascal triangle)
     *
     * @return The coordinates
     */
    public Coordinates<?> getReferenceCoordinates() {
        return referenceCoordinates;
    }

    /**
     * Sets the reference coordinates (the top of the Pascal triangle). The default value is null, which means the first
     * object will be used as reference
     *
     * @param referenceCoordinates A Coordinates subclass
     * @return This object
     */
    public PascalLayout setReferenceCoordinates(Coordinates<?> referenceCoordinates) {
        this.referenceCoordinates = referenceCoordinates;
        return this;
    }

    public double getHorizontalGap() {
        return horizontalGap;
    }

    /**
     * Sets the horizontal gap to apply between elements in the same row. Can be a negative number. BY default is 0.
     *
     * @param horizontalGap Horizontal gap to apply
     * @return This object
     */
    public PascalLayout setHorizontalGap(double horizontalGap) {
        this.horizontalGap = horizontalGap;
        return this;
    }

    public double getVerticalGap() {
        return verticalGap;
    }

    /**
     * Sets the vertical gap to apply between elements in the same row. Can be a negative number. BY default is 0.
     *
     * @param verticalGap Vertical gap to apply
     * @return This object
     */
    public PascalLayout setVerticalGap(double verticalGap) {
        this.verticalGap = verticalGap;
        return this;
    }

    /**
     * Sets both horizontal and vertical gap
     *
     * @param horizontalGap
     * @param verticalGap
     * @return This object
     */
    public PascalLayout setGaps(double horizontalGap, double verticalGap) {
        return setHorizontalGap(horizontalGap).setVerticalGap(verticalGap);
    }

}
