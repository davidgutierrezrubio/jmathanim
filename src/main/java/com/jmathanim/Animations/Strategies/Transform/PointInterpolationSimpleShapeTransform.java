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

import com.jmathanim.Animations.Strategies.Transform.Optimizers.SimpleConnectedPathsOptimizationStrategy;
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.Shape;

/**
 * Point interpolation when both paths are simple, closed curves
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class PointInterpolationSimpleShapeTransform extends TransformStrategy {

    private Shape originBase;
    Vec origCenter, dstCenter;
    private final Shape shDestiny;
    private final Shape shIntermediate;

    public PointInterpolationSimpleShapeTransform(double runtime, Shape origin, Shape destiny) {
        super(runtime);
        this.setOrigin(origin);
        this.setIntermediate(new Shape());
        this.setDestiny(destiny);
        //Cast variables
        this.shDestiny = destiny;
        this.shIntermediate = (Shape) getIntermediateObject();

        origCenter = this.getOriginObject().getCenter();
        dstCenter = this.getDestinyObject().getCenter();

    }

    @Override
    public boolean doInitialization() {
        super.doInitialization();
        shIntermediate.copyStateFrom(getOriginObject());
        shIntermediate.getPath().distille();//Clean paths before transform
        shDestiny.getPath().distille();
        if (optimizeStrategy == null) {
            optimizeStrategy = new SimpleConnectedPathsOptimizationStrategy(shIntermediate, shDestiny);
        }

        alignNumberOfElements(shIntermediate.getPath(), shDestiny.getPath());
        optimizeStrategy.optimizePaths(shIntermediate, shDestiny);
        // Mark all points as curved during transformation
        for (JMPathPoint jmp : shIntermediate.getPath().jmPathPoints) {
            jmp.isCurved = true;
        }
        originBase = getIntermediateObject().copy();

        prepareJumpPath(origCenter, dstCenter, getIntermediateObject());
        return true;
    }

    @Override
    public void doAnim(double t) {
        super.doAnim(t);
        double lt = getLT(t);
        JMPathPoint interPoint, basePoint, dstPoint;
        for (int n = 0; n < shIntermediate.getPath().size(); n++) {
            interPoint = shIntermediate.get(n);
            basePoint = originBase.get(n);
            dstPoint = shDestiny.get(n);

            // Interpolate point
            interPoint.v.x = (1 - lt) * basePoint.v.x + lt * dstPoint.v.x;
            interPoint.v.y = (1 - lt) * basePoint.v.y + lt * dstPoint.v.y;
            interPoint.v.z = (1 - lt) * basePoint.v.z + lt * dstPoint.v.z;

            // Interpolate control point 1
            interPoint.vExit.x = (1 - lt) * basePoint.vExit.x + lt * dstPoint.vExit.x;
            interPoint.vExit.y = (1 - lt) * basePoint.vExit.y + lt * dstPoint.vExit.y;
            interPoint.vExit.z = (1 - lt) * basePoint.vExit.z + lt * dstPoint.vExit.z;

            // Interpolate control point 2
            interPoint.vEnter.x = (1 - lt) * basePoint.vEnter.x + lt * dstPoint.vEnter.x;
            interPoint.vEnter.y = (1 - lt) * basePoint.vEnter.y + lt * dstPoint.vEnter.y;
            interPoint.vEnter.z = (1 - lt) * basePoint.vEnter.z + lt * dstPoint.vEnter.z;
        }
        if (isShouldInterpolateStyles()) {
            // Style interpolation
            getIntermediateObject().getMp().interpolateFrom(getOriginObject().getMp(), getDestinyObject().getMp(), lt);
        }
        // Transform effects
        applyAnimationEffects(lt, getIntermediateObject());

    }

    @Override
    public Shape getIntermediateObject() {
        return shIntermediate;
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
