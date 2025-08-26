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
import com.jmathanim.mathobjects.AbstractShape;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.Shape;

/**
 * Point interpolation when both paths are simple, closed curves
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class PointInterpolationSimpleShapeTransform extends TransformStrategy<AbstractShape<?>> {

    private AbstractShape<?> originBase;
    Vec origCenter, dstCenter;
    private final AbstractShape<?> shDestiny;
    private final AbstractShape<?> shIntermediate;

    public PointInterpolationSimpleShapeTransform(double runtime, AbstractShape<?> origin, AbstractShape<?> destiny) {
        super(runtime);
        this.setOrigin(origin);

        this.setDestiny(destiny);
        //Cast variables
        this.shDestiny = destiny;
        this.shIntermediate = new Shape();
//        this.setIntermediate(new Shape());

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
        for (JMPathPoint jmp : shIntermediate.getPath().getJmPathPoints()) {
            jmp.setCurved(true);
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
            interPoint.getV().x = (1 - lt) * basePoint.getV().x + lt * dstPoint.getV().x;
            interPoint.getV().y = (1 - lt) * basePoint.getV().y + lt * dstPoint.getV().y;
            interPoint.getV().z = (1 - lt) * basePoint.getV().z + lt * dstPoint.getV().z;

            // Interpolate control point 1
            interPoint.getvExit().x = (1 - lt) * basePoint.getvExit().x + lt * dstPoint.getvExit().x;
            interPoint.getvExit().y = (1 - lt) * basePoint.getvExit().y + lt * dstPoint.getvExit().y;
            interPoint.getvExit().z = (1 - lt) * basePoint.getvExit().z + lt * dstPoint.getvExit().z;

            // Interpolate control point 2
            interPoint.getvEnter().x = (1 - lt) * basePoint.getvEnter().x + lt * dstPoint.getvEnter().x;
            interPoint.getvEnter().y = (1 - lt) * basePoint.getvEnter().y + lt * dstPoint.getvEnter().y;
            interPoint.getvEnter().z = (1 - lt) * basePoint.getvEnter().z + lt * dstPoint.getvEnter().z;
        }
        if (isShouldInterpolateStyles()) {
            // Style interpolation
            getIntermediateObject().getMp().interpolateFrom(getOriginObject().getMp(), getDestinyObject().getMp(), lt);
        }
        // Transform effects
        applyAnimationEffects(lt, getIntermediateObject());

    }

    @Override
    public AbstractShape<?> getIntermediateObject() {
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
