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
import com.jmathanim.Animations.Strategies.Transform.Optimizers.SimpleConnectedPathsOptimizationStrategy;
import com.jmathanim.MathObjects.AbstractShape;
import com.jmathanim.MathObjects.CanonicalJMPath;
import com.jmathanim.MathObjects.Shape;
import com.jmathanim.MathObjects.Shapes.JMPath;
import com.jmathanim.MathObjects.Shapes.JMPathPoint;
import com.jmathanim.Styling.JMColor;
import com.jmathanim.Utils.Vec;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Separates paths into canonical forms to interpolate point by point
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class PointInterpolationCanonical extends TransformStrategy<AbstractShape<?>> {

    private final AbstractShape<?> destinyCopy;
//    private final AbstractShape<?> shOrigin;
//    private final AbstractShape<?> shDestiny;
//    private final AbstractShape<?> shIntermediate;
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
    public PointInterpolationCanonical(double runtime, AbstractShape<?> origin, AbstractShape<?> destiny) {
        super(runtime);
        this.setOrigin(origin);
        this.setIntermediate(new Shape());
        this.setDestiny(destiny);
        this.destinyCopy = new Shape();
//        this.shOrigin = origin;
//        this.shDestiny = destiny;
//        this.shIntermediate = (Shape) getIntermediateObject();
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

        if ((getIntermediateObject().isEmpty()) || (destinyCopy.isEmpty())) {
            return false;
        }

        // I ensure they have the same number of points
        // and be in connected components form.
        // Remove consecutive hidden vertices, in case.
        this.getIntermediateObject().getPath().distille();
        this.destinyCopy.getPath().distille();


        if (optimizeStrategy == null) {
            optimizeStrategy = new DivideOnSensiblePointsStrategy();
//            optimizeStrategy = new DivideEquallyStrategy();
        }
        optimizeStrategy.optimizePaths(getIntermediateObject(), destinyCopy);
        optimizeStrategy.optimizePaths(destinyCopy, getIntermediateObject());

//        originalShapeBaseCopy = intermediate.copy();
        preparePaths();
        if (DEBUG_COLORS) {
            for (int n = 0; n < connectedOrigin.getNumberOfPaths(); n++) {
                Shape sh = new Shape(connectedOrigin.get(n));
                sh.drawColor(JMColor.random()).thickness(10);
                addObjectsToscene(sh);
                addedAuxiliaryObjectsToScene.add(sh);
            }

        }
        getIntermediateObject().getPath().clear();
        getIntermediateObject().getPath().addJMPointsFrom(connectedOrigin.toJMPath());

        // Jump paths
        Vec origCenter = this.getOriginObject().getCenter();
        Vec dstCenter = this.getDestinyObject().getCenter();
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
                interPoint = convertedPath.getJmPathPoints().get(n);
                basePoint = fromPath.getJmPathPoints().get(n);
                dstPoint = toPath.getJmPathPoints().get(n);

                // Interpolate point
                interPoint.getV().x = (1 - lt) * basePoint.getV().x + lt * dstPoint.getV().x;
                interPoint.getV().y = (1 - lt) * basePoint.getV().y + lt * dstPoint.getV().y;
                interPoint.getV().z = (1 - lt) * basePoint.getV().z + lt * dstPoint.getV().z;

                // Interpolate control point 1
                interPoint.getVExit().x = (1 - lt) * basePoint.getVExit().x + lt * dstPoint.getVExit().x;
                interPoint.getVExit().y = (1 - lt) * basePoint.getVExit().y + lt * dstPoint.getVExit().y;
                interPoint.getVExit().z = (1 - lt) * basePoint.getVExit().z + lt * dstPoint.getVExit().z;

                // Interpolate control point 2
                interPoint.getVEnter().x = (1 - lt) * basePoint.getVEnter().x + lt * dstPoint.getVEnter().x;
                interPoint.getVEnter().y = (1 - lt) * basePoint.getVEnter().y + lt * dstPoint.getVEnter().y;
                interPoint.getVEnter().z = (1 - lt) * basePoint.getVEnter().z + lt * dstPoint.getVEnter().z;
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
     */
    private void preparePaths() {


        //If shapes are simple (closed, 1 component) we can do a previous extra optimization, cycling points
        int n1 = getIntermediateObject().getPath().getNumberOfConnectedComponents();
        int n2 = destinyCopy.getPath().getNumberOfConnectedComponents();
        if ((n1==0)&&(n2==0)) {
            SimpleConnectedPathsOptimizationStrategy strategy=new SimpleConnectedPathsOptimizationStrategy(getIntermediateObject(),destinyCopy);
            strategy.optimizePaths(getIntermediateObject(),destinyCopy);
        }
        JMPath pathTransformed=getIntermediateObject().getPath();
        JMPath pathDestiny=destinyCopy.getPath();

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
        //Ensure that both paths have the same number of elements, interpolating if necessary

        alignNumberOfComponents(connectedOrigin, connectedDst);





        connectedOriginaRawCopy = new CanonicalJMPath();
        for (JMPath p : connectedOrigin.getPaths()) {
            connectedOriginaRawCopy.add(p.copy());
        }
        // Mark all points as curved during the transform
        for (int numConnected = 0; numConnected < this.connectedDst.getNumberOfPaths(); numConnected++) {
            JMPath convertedPath = connectedOrigin.get(numConnected);
            for (JMPathPoint p : convertedPath.getJmPathPoints()) {
                p.setSegmentToThisPointCurved(true);
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
            Vec v = conSmall.get(n - 1).getJMPointAt(-1).getV();

            // Create a dummy path with sizePathToAdd points, all equal
            JMPath pa = new JMPath();
            for (int k = 0; k < sizePathToAdd; k++) {
                JMPathPoint jmp = JMPathPoint.curveTo(v.copy());
                pa.addJMPoint(jmp);
            }
            pa.getJmPathPoints().get(0).setSegmentToThisPointVisible(false);
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
