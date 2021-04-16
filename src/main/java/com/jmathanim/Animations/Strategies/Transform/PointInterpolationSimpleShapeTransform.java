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
package com.jmathanim.Animations.Strategies.Transform;

import com.jmathanim.Animations.AnimationWithEffects;
import com.jmathanim.Animations.Strategies.Transform.Optimizers.SimpleConnectedPathsOptimizationStrategy;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

/**
 * Point interpolation when both paths are simple, closed curves
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class PointInterpolationSimpleShapeTransform extends AnimationWithEffects {

    private final Shape mobjTransformed;
    private final Shape mobjDestiny;
    private Shape originalShapeBaseCopy;
    Point origCenter,dstCenter;

    public PointInterpolationSimpleShapeTransform(double runtime, Shape mobjTransformed, Shape mobjDestiny) {
        super(runtime);

        this.mobjTransformed = mobjTransformed;
        this.mobjDestiny = mobjDestiny;
        origCenter=this.mobjTransformed.getCenter();
        dstCenter=this.mobjDestiny.getCenter();

    }

    @Override
    public void initialize(JMathAnimScene scene) {
        super.initialize(scene);
        if (optimizeStrategy == null) {
            optimizeStrategy = new SimpleConnectedPathsOptimizationStrategy(mobjTransformed, mobjDestiny);
        }

        alignNumberOfElements(mobjTransformed.getPath(), mobjDestiny.getPath());
        optimizeStrategy.optimizePaths(mobjTransformed, mobjDestiny);
        originalShapeBaseCopy = mobjTransformed.copy();
        //Mark all points as curved during transformation
        for (JMPathPoint jmp : mobjTransformed.getPath().jmPathPoints) {
            jmp.isCurved = true;
        }
        addObjectsToscene(mobjTransformed);
        
        prepareJumpPath(origCenter, dstCenter, mobjTransformed);
    }

    @Override
    public void doAnim(double t) {
        double lt = lambda.applyAsDouble(t);
        JMPathPoint interPoint, basePoint, dstPoint;
        for (int n = 0; n < mobjTransformed.getPath().size(); n++) {
            interPoint = mobjTransformed.getPath().getJMPoint(n);
            basePoint = originalShapeBaseCopy.getPath().getJMPoint(n);
            dstPoint = mobjDestiny.getPath().getJMPoint(n);

            //Interpolate point
            interPoint.p.v.x = (1 - lt) * basePoint.p.v.x + lt * dstPoint.p.v.x;
            interPoint.p.v.y = (1 - lt) * basePoint.p.v.y + lt * dstPoint.p.v.y;
            interPoint.p.v.z = (1 - lt) * basePoint.p.v.z + lt * dstPoint.p.v.z;

            //Interpolate control point 1
            interPoint.cpExit.v.x = (1 - lt) * basePoint.cpExit.v.x + lt * dstPoint.cpExit.v.x;
            interPoint.cpExit.v.y = (1 - lt) * basePoint.cpExit.v.y + lt * dstPoint.cpExit.v.y;
            interPoint.cpExit.v.z = (1 - lt) * basePoint.cpExit.v.z + lt * dstPoint.cpExit.v.z;

            //Interpolate control point 2
            interPoint.cpEnter.v.x = (1 - lt) * basePoint.cpEnter.v.x + lt * dstPoint.cpEnter.v.x;
            interPoint.cpEnter.v.y = (1 - lt) * basePoint.cpEnter.v.y + lt * dstPoint.cpEnter.v.y;
            interPoint.cpEnter.v.z = (1 - lt) * basePoint.cpEnter.v.z + lt * dstPoint.cpEnter.v.z;
        }
        //Style interpolation
        mobjTransformed.getMp().interpolateFrom(originalShapeBaseCopy.getMp(), mobjDestiny.getMp(), lt);

        //Transform effects
        applyAnimationEffects(lt, mobjTransformed);

    }

    @Override
    public void finishAnimation() {
        super.finishAnimation();

        for (int n = 0; n < mobjTransformed.getPath().size(); n++) {
            JMPathPoint p1 = mobjTransformed.getPath().getJMPoint(n);
            JMPathPoint p2 = mobjDestiny.getPath().getJMPoint(n);
            p1.type = p2.type;
            p1.isCurved = p2.isCurved;
            p1.isThisSegmentVisible = p2.isThisSegmentVisible;
            p1.cpExitvBackup = p2.cpExitvBackup;
            p1.cpEntervBackup = p2.cpEntervBackup;
        }

        mobjTransformed.getPath().removeInterpolationPoints();
        mobjTransformed.getMp().copyFrom(mobjDestiny.getMp());
        mobjTransformed.absoluteSize = mobjDestiny.absoluteSize;
    }

    private void alignNumberOfElements(JMPath path1, JMPath path2) {
        JMPath pathSmall, pathBig;
        if (path1.size() < path2.size()) {
            pathSmall = path1;
            pathBig = path2;
        } else {
            pathBig = path1;
            pathSmall = path2;
        }
        pathSmall.alignPathsToGivenNumberOfElements(pathBig.size());
    }

}
