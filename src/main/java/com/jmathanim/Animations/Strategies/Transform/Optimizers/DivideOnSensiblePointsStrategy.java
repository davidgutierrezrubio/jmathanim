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
package com.jmathanim.Animations.Strategies.Transform.Optimizers;

import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.Shape;

/**
 * Optimizes the paths when the transformed path has a number of connected
 * componentes is greter than 1 and the destiny is 0. In this case, try to
 * divide the destiny path equally
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class DivideOnSensiblePointsStrategy implements OptimizePathsStrategy {

    @Override
    public void optimizePaths(Shape sh1, Shape sh2) {
        JMPath pa1 = sh1.getPath();
        JMPath pa2 = sh2.getPath();
        int n1 = pa1.getNumberOfConnectedComponents();
        int n2 = pa2.getNumberOfConnectedComponents();
        if ((n1 < 2) | (n2 > 1)) {
            return; //Do nothing
        }

        int numSegments = pa2.size();

        int step = numSegments / n1;
        //If there are more connected componentes than segments in destiny shape, do nothing
        if (step == 0) {
            return;
        }

        for (int n = n2; n < Math.min(n2+2, n1); n++) {
            for (int k = 0; k < pa2.size(); k++) {
                JMPathPoint jmp = pa2.jmPathPoints.get(k);
                if (jmp.type != JMPathPoint.JMPathPointType.INTERPOLATION_POINT) {
                    JMPathPoint jmpPrev = pa2.jmPathPoints.get(k - 1);
                    JMPathPoint jmpNext = pa2.jmPathPoints.get(k + 1);
                    if ((jmp.isThisSegmentVisible) && (!jmp.isCurved)) {
                        if ((jmpNext.isThisSegmentVisible)) {
//                        if ((jmpPrev.isThisSegmentVisible)&&(jmpNext.isThisSegmentVisible)) {
                            pa2.separate(k);
                            break;
                        }
                    }
                }
            }

            System.out.println("Done" + pa1.getNumberOfConnectedComponents() + "   " + pa2.getNumberOfConnectedComponents());

        }
    }

}
