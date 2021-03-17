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

import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.Rect;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MathObjectGroup;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;
import java.util.ArrayList;
import jdk.nashorn.internal.runtime.arrays.ArrayLikeIterator;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class PascalLayout extends GroupLayout {

    private Point top;//Upper point of the triangle
    private double horizontalGap, verticalGap;

    public PascalLayout(Point top, double horizontalGap, double verticalGap) {
        this.top = top;
        this.horizontalGap = horizontalGap;
        this.verticalGap = verticalGap;
    }

    @Override
    public void applyLayout(MathObjectGroup group) {
        if (group.isEmpty()) {
            return; //Nothing to do here...
        }
        double w = 0;
        double h = 0;
        for (MathObject g : group) {
            w = Math.max(w, g.getWidth());
            h = Math.max(h, g.getHeight());
        }
        Rect r = new Rect(0, 0, w, h);
        System.out.println("r=" + r);
        r = r.addGap(.5*this.horizontalGap, .5*this.verticalGap);
        MathObjectGroup workGroup = MathObjectGroup.make();

        //Complete the last row in case it is not complete
        //Find the first triangular number bigger than the group size
        int num = 0;
        int k = 0;
        while (num < group.size()) {
            num = k * (k + 1) / 2;
            k++;
        }
        for (int n = 0; n < num; n++) {

            workGroup.add(Shape.rectangle(r));

        }
        //Add the first element
        int rowSize = 1;
        int counter = 0;
        int rowCounter = 0;
        MathObjectGroup rows = MathObjectGroup.make();
        while (counter < workGroup.size()) {//Main loop
            MathObjectGroup grRow = MathObjectGroup.make();
            while (rowCounter < rowSize) {
                if (counter < workGroup.size()) {
                    grRow.add(workGroup.get(counter));
                }
                counter++;
                rowCounter++;
                System.out.println("row " + rowSize + " counter " + counter);
            }
            grRow.setLayout(MathObjectGroup.Layout.RIGHT, 0);
            rows.add(grRow);
            rowSize++;
            rowCounter = 0;
        }
        rows.setLayout(MathObjectGroup.Layout.LOWER, 0);

        //Move now so that the top center point of first element match the Point top.
        Point A = Anchor.getAnchorPoint(workGroup.get(0), Anchor.Type.UPPER);
        rows.shift(A.to(top));

        //Now that the rectangles are properly located, move the original objects so that their centers match those of the workgroup
        for (int n = 0; n < group.size(); n++) {
            group.get(n).stackTo(workGroup.get(n), Anchor.Type.CENTER);
        }

    }

}
