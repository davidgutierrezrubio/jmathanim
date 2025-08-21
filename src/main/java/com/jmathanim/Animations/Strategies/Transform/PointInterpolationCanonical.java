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

import com.jmathanim.Animations.Strategies.Transform.Optimizers.DivideOnSensiblePointsStrategy;
import com.jmathanim.Styling.JMColor;
import com.jmathanim.mathobjects.*;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Separates paths into canonical forms to interpolate point by point
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class PointInterpolationCanonical extends TransformStrategy {

    private final Shape destinyCopy;
    private final Shape shOrigin;
    private final Shape shDestiny;
    private final Shape shIntermediate;
    public CanonicalJMPath connectedOrigin, connectedDst, connectedOriginaRawCopy;
    private final ArrayList<Shape> addedAuxiliaryObjectsToScene;
//    private final Shape mobjDestinyOrig;
//    private Shape originalShapeBaseCopy;
    private static final boolean DEBUG_COLORS = false;
//    private final Shape mobjTransformedOrig;
    private boolean originWasAddedAtFirst, destinyWasAddedAtFirst;

    /**
     * Constructor
     *
     * @param runtime Duration in seconds
     * @param origin Origin shape
     * @param destiny Destinty Shape
     */
    public PointInterpolationCanonical(double runtime, Shape origin, Shape destiny) {
        super(runtime);
        this.setOrigin(origin);
        this.setIntermediate(new Shape());
        this.setDestiny(destiny);
        this.destinyCopy = new Shape();
        this.shOrigin = origin;
        this.shDestiny = destiny;
        this.shIntermediate = (Shape) getIntermediateObject();
        this.addedAuxiliaryObjectsToScene = new ArrayList<>();
    }

    @Override
    public boolean doInitialization() {
        super.doInitialization();
        originWasAddedAtFirst = scene.isInScene(getOriginObject());
        destinyWasAddedAtFirst = scene.isInScene(getDestinyObject());
        getIntermediateObject().copyStateFrom(getOriginObject());
        destinyCopy.copyStateFrom(getDestinyObject());
        // This is the initialization for the point-to-point interpolation
        // Prepare paths.
        // First, if any of the shapes is empty, do nothing

        if ((shIntermediate.size() == 0) || (shDestiny.size() == 0)) {
            return false;
        }

        // I ensure they have the same number of points
        // and be in connected components form.
        // Remove consecutive hidden vertices, in case.
        this.shIntermediate.getPath().distille();
        this.destinyCopy.getPath().distille();
        if (optimizeStrategy == null) {
            optimizeStrategy = new DivideOnSensiblePointsStrategy();
//            optimizeStrategy = new DivideEquallyStrategy();
        }
        optimizeStrategy.optimizePaths(shIntermediate, destinyCopy);
        optimizeStrategy.optimizePaths(destinyCopy, shIntermediate);

//        originalShapeBaseCopy = intermediate.copy();
        preparePaths(shIntermediate.getPath(), destinyCopy.getPath());
        if (DEBUG_COLORS) {
            for (int n = 0; n < connectedOrigin.getNumberOfPaths(); n++) {
                Shape sh = new Shape(connectedOrigin.get(n));
                sh.drawColor(JMColor.random()).thickness(10);
                addObjectsToscene(sh);
                addedAuxiliaryObjectsToScene.add(sh);
            }

        }
        shIntermediate.getPath().clear();
        shIntermediate.getPath().addJMPointsFrom(connectedOrigin.toJMPath());

        // Jump paths
        Point origCenter = this.getOriginObject().getCenter();
        Point dstCenter = this.getDestinyObject().getCenter();
        prepareJumpPath(origCenter, dstCenter, getIntermediateObject());
        return true;
    }

    @Override
    public void doAnim(double t) {
        super.doAnim(t);
        double lt = getLT(t);
        JMPathPoint interPoint, basePoint, dstPoint;

        if ((connectedOrigin.getNumberOfPaths() == 0) || (connectedDst.getNumberOfPaths() == 0)) {
            return;
        }

        for (int numConnected = 0; numConnected < this.connectedDst.getNumberOfPaths(); numConnected++) {
            JMPath convertedPath = connectedOrigin.get(numConnected);
            JMPath fromPath = connectedOriginaRawCopy.get(numConnected);
            JMPath toPath = connectedDst.get(numConnected);
            for (int n = 0; n < convertedPath.size(); n++) {
                interPoint = convertedPath.jmPathPoints.get(n);
                basePoint = fromPath.jmPathPoints.get(n);
                dstPoint = toPath.jmPathPoints.get(n);

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

        }
        if (isShouldInterpolateStyles()) {
         getIntermediateObject().getMp().interpolateFrom(getOriginObject().getMp(), getDestinyObject().getMp(), lt);
        }

        // Transform effects
        applyAnimationEffects(lt, getIntermediateObject());

    }

    /**
     * Creates connectedOrigin and connectedDst, two paths in their
     * canonicalforms (and array of simple connected open paths)
     *
     * @param pathTransformed F
     * @param pathDestiny
     */
    private void preparePaths(JMPath pathTransformed, JMPath pathDestiny) {

        connectedOrigin = pathTransformed.canonicalForm();
        connectedDst = pathDestiny.canonicalForm();

        // Sort canonical forms from biggest chunk to smallest.
        // The size is computed using the semiperimeter of the bounding box
        Comparator<JMPath> comparator = (JMPath o1, JMPath o2) -> {
            if ((o1.getWidth() + o1.getHeight()) < (o2.getWidth() + o2.getHeight())) {
                return 1;
            } else {
                return -1;
            }
        };
        connectedOrigin.paths.sort(comparator);
        connectedDst.paths.sort(comparator);

        if ((connectedOrigin.getNumberOfPaths() == 0) || (connectedDst.getNumberOfPaths() == 0)) {
            return;
        }

        alignNumberOfComponents(connectedOrigin, connectedDst);
        connectedOriginaRawCopy = new CanonicalJMPath();
        for (JMPath p : connectedOrigin.getPaths()) {
            connectedOriginaRawCopy.add(p.copy());
        }
        // Mark all points as curved during the transform
        for (int numConnected = 0; numConnected < this.connectedDst.getNumberOfPaths(); numConnected++) {
            JMPath convertedPath = connectedOrigin.get(numConnected);
            for (JMPathPoint p : convertedPath.jmPathPoints) {
                p.isCurved = true;
            }

        }

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

    private void alignNumberOfComponents(CanonicalJMPath con1, CanonicalJMPath con2) {
        if ((con1.getNumberOfPaths() == 0) || (con2.getNumberOfPaths() == 0)) {
            return;
        }
        CanonicalJMPath conBig, conSmall;
        if (con1.getNumberOfPaths() < con2.getNumberOfPaths()) {
            conSmall = con1;
            conBig = con2;
        } else {
            conBig = con1;
            conSmall = con2;
        }

        for (int n = conSmall.getNumberOfPaths(); n < conBig.getNumberOfPaths(); n++) {
            int sizePathToAdd = conBig.get(n).size();
//            if (sizePathToAdd <= 2) {
//                throw new Exception("Paths should have at least 2 points!");
//            }
            // Last point of conSmall
            Point p = conSmall.get(n - 1).getJMPointAt(-1).p;

            // Create a dummy path with sizePathToAdd points, all equal
            JMPath pa = new JMPath();
            for (int k = 0; k < sizePathToAdd; k++) {
                JMPathPoint jmp = JMPathPoint.curveTo(p.copy());
                pa.addJMPoint(jmp);
            }
            pa.jmPathPoints.get(0).isThisSegmentVisible = false;
            // Add the new path created
            conSmall.add(pa);
        }

        // Now that I am sure we have the same number of connected components, let's
        // align the number in each one
        for (int n = 0; n < conSmall.getNumberOfPaths(); n++) {
            alignNumberOfElements(conSmall.get(n), conBig.get(n));
        }

    }

}
