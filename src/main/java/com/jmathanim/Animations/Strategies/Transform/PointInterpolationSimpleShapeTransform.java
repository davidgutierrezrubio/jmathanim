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
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

/**
 * Point interpolation when both paths are simple, closed curves
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class PointInterpolationSimpleShapeTransform extends TransformStrategy {

    private final Shape origin;
    private Shape originBase;
    private final Shape destiny;
    private final Shape intermediate;
    Point origCenter, dstCenter;

    public PointInterpolationSimpleShapeTransform(double runtime, Shape origin, Shape destiny) {
        super(runtime);
        this.origin = origin;
        this.intermediate = new Shape();
        this.destiny = destiny;

        origCenter = this.origin.getCenter();
        dstCenter = this.destiny.getCenter();

    }

    @Override
    public void initialize(JMathAnimScene scene) {
        super.initialize(scene);
        intermediate.copyStateFrom(origin);
        intermediate.getPath().distille();//Clean paths before transform
        destiny.getPath().distille();
        if (optimizeStrategy == null) {
            optimizeStrategy = new SimpleConnectedPathsOptimizationStrategy(intermediate, destiny);
        }

        alignNumberOfElements(intermediate.getPath(), destiny.getPath());
        optimizeStrategy.optimizePaths(intermediate, destiny);
        // Mark all points as curved during transformation
        for (JMPathPoint jmp : intermediate.getPath().jmPathPoints) {
            jmp.isCurved = true;
        }
        originBase = intermediate.copy();

        prepareJumpPath(origCenter, dstCenter, intermediate);
    }

    @Override
    public void prepareForAnim(double t) {
        addObjectsToscene(intermediate);
        removeObjectsFromScene(origin, destiny);
    }

    @Override
    public void doAnim(double t) {
        super.doAnim(t);
        double lt = getLT(t);
        JMPathPoint interPoint, basePoint, dstPoint;
        for (int n = 0; n < intermediate.getPath().size(); n++) {
            interPoint = intermediate.get(n);
            basePoint = originBase.get(n);
            dstPoint = destiny.get(n);

            // Interpolate point
            interPoint.p.v.x = (1 - lt) * basePoint.p.v.x + lt * dstPoint.p.v.x;
            interPoint.p.v.y = (1 - lt) * basePoint.p.v.y + lt * dstPoint.p.v.y;
            interPoint.p.v.z = (1 - lt) * basePoint.p.v.z + lt * dstPoint.p.v.z;

            // Interpolate control point 1
            interPoint.cpExit.v.x = (1 - lt) * basePoint.cpExit.v.x + lt * dstPoint.cpExit.v.x;
            interPoint.cpExit.v.y = (1 - lt) * basePoint.cpExit.v.y + lt * dstPoint.cpExit.v.y;
            interPoint.cpExit.v.z = (1 - lt) * basePoint.cpExit.v.z + lt * dstPoint.cpExit.v.z;

            // Interpolate control point 2
            interPoint.cpEnter.v.x = (1 - lt) * basePoint.cpEnter.v.x + lt * dstPoint.cpEnter.v.x;
            interPoint.cpEnter.v.y = (1 - lt) * basePoint.cpEnter.v.y + lt * dstPoint.cpEnter.v.y;
            interPoint.cpEnter.v.z = (1 - lt) * basePoint.cpEnter.v.z + lt * dstPoint.cpEnter.v.z;
        }
        if (isShouldInterpolateStyles()) {
            // Style interpolation
            intermediate.getMp().interpolateFrom(origin.getMp(), destiny.getMp(), lt);
        }
        // Transform effects
        applyAnimationEffects(lt, intermediate);

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

    @Override
    public MathObject getIntermediateObject() {
        return intermediate;
    }

    @Override
    public MathObject getOriginObject() {
        return origin;
    }

    @Override
    public MathObject getDestinyObject() {
        return destiny;
    }

    @Override
    public void finishAnimation() {
        cleanAnimationAt(t);
    }

    @Override
    public void cleanAnimationAt(double t) {
        double lt = getLT(t);
        if (lt == 0) {
            removeObjectsFromScene(intermediate, destiny);
            addObjectsToscene(origin);
            return;
        }
        if (lt == 1) {
            removeObjectsFromScene(intermediate, origin);
            addObjectsToscene(destiny);
            return;
        }
        removeObjectsFromScene(destiny, origin);
        addObjectsToscene(intermediate);

    }

}
