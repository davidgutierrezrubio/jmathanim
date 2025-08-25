package com.jmathanim.Animations.Strategies.Transform;

import com.jmathanim.Utils.UsefulLambdas;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.Shape;

import java.util.function.DoubleUnaryOperator;

import static com.jmathanim.jmathanim.JMathAnimScene.PI;

/**
 * A transformation strategy that transforms one polygonal shape into another by "twisting" it. The transformation is
 * centered around a "pivotal segment", which creates an effect of the shape twisting and stretching into its final
 * form. If origin-destiny segments have the same length, measures are preserved in the intermediate steps.
 * <p>
 * The transformation can be controlled separately for the parts of the shape before and after the pivotal segment using
 * custom timing functions (lambdaForward and lambdaBackward). This animation doesn't work properly with curved shapes.
 */
public class TwistTransform extends TransformShape2ShapeStrategy {

    /**
     * The index of the point that starts the pivotal segment.
     */
    private int numPivotalSegment;
    /**
     * The translation vector required to move the origin's pivotal point to the destiny's pivotal point.
     */
    private Vec vShift;
    /**
     * Array storing the angles at each vertex of the origin shape. The angle at vertex `i` is the angle between segment
     * (i-1, i) and (i, i+1).
     */
    private double[] originAngles;
    /**
     * Array storing the angles at each vertex of the destiny shape. The angle at vertex `i` is the angle between
     * segment (i-1, i) and (i, i+1).
     */
    private double[] destinyAngles;
    /**
     * Array storing the ratio of segment lengths between the destiny and origin shapes. `ratioLengths[i] =
     * (length(destiny_segment_i) / length(origin_segment_i)) - 1`.
     */
    private double[] ratioLengths;
    /**
     * The total rotation angle needed for the pivotal segment to align with the destiny's pivotal segment.
     */
    private double pivotalAngle;
    /**
     * The relative change in length for the pivotal segment.
     */
    private double pivotalLengthRatio;
    /**
     * A timing function to control the animation for the part of the shape "forward" of the pivotal segment (higher
     * indices). If null, the default animation timing is used.
     */
    private DoubleUnaryOperator lambdaForward;


    /**
     * A timing function to control the scaling (stretch/shrink) of the pivotal segment. If null, the default animation
     * timing is used.
     */
    private DoubleUnaryOperator lambdaScalePivotal;
    /**
     * A timing function to control the initial translation and rotation of the pivotal segment. If null, the default
     * animation timing is used.
     */
    private DoubleUnaryOperator lambdaShiftPivotal;
    /**
     * A timing function to control the animation for the part of the shape "backward" of the pivotal segment (lower
     * indices). If null, the default animation timing is used.
     */
    private DoubleUnaryOperator lambdaBackward;

    private DoubleUnaryOperator[] auxiliaryLambdas;

    private boolean isStepByStep;

    /**
     * Protected constructor. Use the static `make` methods for instantiation.
     *
     * @param runTime           The total duration of the animation.
     * @param origin            The starting shape.
     * @param destiny           The target shape.
     * @param numPivotalSegment The index of the point that starts the pivotal segment.
     */
    protected TwistTransform(double runTime, Shape origin, Shape destiny, int numPivotalSegment) {
        super(runTime);
        setOrigin(origin);
        setDestiny(destiny);
        setLambda(t -> t);
        this.numPivotalSegment = numPivotalSegment;
        lambdaBackward = null;
        lambdaForward = null;
        lambdaScalePivotal = null;
        lambdaShiftPivotal = null;
        isStepByStep = false;
        setDebugName("TwistTransform");
    }

    /**
     * Factory method to create a TwistTransform instance.
     * <p>
     * This animation transforms one {@link Shape} into another by interpolating the angles at each vertex. It works
     * best for open polylines with straight segments (no curves). If the corresponding segment lengths of the origin
     * and destiny shapes are the same, the animation preserves these lengths, avoiding distortion.
     *
     * @param runTime           The total duration of the animation.
     * @param origin            The starting shape. It should have the same number of points as the destiny shape.
     * @param destiny           The target shape. It should have the same number of points as the origin shape.
     * @param numPivotalSegment The index of the point that starts the pivotal segment. This segment will be the first
     *                          to be aligned (rotated, scaled, and shifted) to match the destiny shape.
     * @return A new TwistTransform instance.
     */
    public static TwistTransform make(double runTime, Shape origin, Shape destiny, int numPivotalSegment) {
        return new TwistTransform(runTime, origin, destiny, numPivotalSegment);
    }

    /**
     * Factory method to create a TwistTransform instance, with the pivotal segment chosen as the middle segment of the
     * shape.
     * <p>
     * This animation transforms one {@link Shape} into another by interpolating the angles at each vertex. It works
     * best for open polylines with straight segments (no curves). If the corresponding segment lengths of the origin
     * and destiny shapes are the same, the animation preserves these lengths, avoiding distortion.
     *
     * @param runTime The total duration of the animation.
     * @param origin  The starting shape. It should have the same number of points as the destiny shape.
     * @param destiny The target shape. It should have the same number of points as the origin shape.
     * @return A new TwistTransform instance.
     */
    public static TwistTransform make(double runTime, Shape origin, Shape destiny) {
        return new TwistTransform(runTime, origin, destiny, origin.size() / 2);
    }

    /**
     * Returns the step by step flag. If true, segments of the transformed object will be progressively rotated in
     * order. If false, all are rotated at the same time.
     *
     * @return The step flag
     */
    public boolean isStepByStep() {
        return isStepByStep;
    }

    /**
     * Sets the step by step flag. If true, segments of the transformed object will be progressively rotated in order.
     * If false, all are rotated at the same time.
     *
     * @param stepByStep The step flag
     */
    public <T extends TwistTransform> T setStepByStep(boolean stepByStep) {
        isStepByStep = stepByStep;
        return (T) this;
    }

    public DoubleUnaryOperator getLambdaShiftPivotal() {
        return lambdaShiftPivotal;
    }

    /**
     * Sets the timing function for the pivotal segment's translation and rotation.
     *
     * @param lambdaShiftPivotal A DoubleUnaryOperator that maps the animation time [0,1] to a custom progress value.
     */
    public void setLambdaShiftPivotal(DoubleUnaryOperator lambdaShiftPivotal) {
        this.lambdaShiftPivotal = lambdaShiftPivotal;
    }
    /**
     * Returns the timing function for the pivotal segment's scaling.
     * @return The timing function in lambda form
     */
    public DoubleUnaryOperator getLambdaScalePivotal() {
        return lambdaScalePivotal;
    }

    /**
     * Sets the timing function for the pivotal segment's scaling.
     *
     * @param lambdaScalePivotal A DoubleUnaryOperator that maps the animation time [0,1] to a custom progress value.
     */
    public void setLambdaScalePivotal(DoubleUnaryOperator lambdaScalePivotal) {
        this.lambdaScalePivotal = lambdaScalePivotal;
    }
    /**
     * Returns the timing function for the segments before the pivotal segment
     * @return The timing function in lambda form
     */
    public DoubleUnaryOperator getLambdaBackward() {
        return lambdaBackward;
    }

    /**
     * Sets the timing function for the segments before the pivotal segment
     * @param lambdaBackward  The timing function in lambda form
     */
    public void setLambdaBackward(DoubleUnaryOperator lambdaBackward) {
        this.lambdaBackward = lambdaBackward;
    }

    /**
     * Returns the timing function for the segments after the pivotal segment
     * @return The timing function in lambda form
     */
    public DoubleUnaryOperator getLambdaForward() {
        return lambdaForward;
    }

    /**
     * Sets the timing function for the segments after the pivotal segment
     * @param lambdaForward  The timing function in lambda form
     */
    public void setLambdaForward(DoubleUnaryOperator lambdaForward) {
        this.lambdaForward = lambdaForward;
    }

    @Override
    public boolean doInitialization() {
        super.doInitialization();

        // The intermediate object is a copy of the origin that will be modified during the animation.
        setIntermediate(getOriginObject().copy());
        getIntermediateObject().getPath().openPath(); // Open path to allow independent segment manipulation
        getIntermediateObject().setObjectLabel("intermediate");
        saveStates(getIntermediateObject());

        if (getIntermediateObject().size() != getDestinyObject().size()) {
            JMathAnimScene.logger.warn("Origin path size is " + getIntermediateObject().size() + " and destiny path size is " + getDestinyObject().size() + ". Animation will be distorted");
        }


        //Check that the pivotal segment number is appropiate
        int pivotRange = getIntermediateObject().size() - 1;
        if (numPivotalSegment < 0 || numPivotalSegment >= pivotRange) {
            int oldN = numPivotalSegment;
            // The modulo operator can be negative in Java, so we add the size and take the modulo again
            // to ensure the result is always in the range [0, sizeIntermediate - 1].
            this.numPivotalSegment = (numPivotalSegment % pivotRange + pivotRange) % pivotRange;
            JMathAnimScene.logger.warn("Pivotal segment " + oldN + " out of range. Reallocating to " + this.numPivotalSegment);
        }


        // Calculate the vector needed to shift the pivotal point of the origin to the pivotal point of the destiny.
        vShift = getIntermediateObject().get(numPivotalSegment).getV().to(getDestinyObject().get(numPivotalSegment).getV());
        int size = getIntermediateObject().size();
        originAngles = new double[size];
        destinyAngles = new double[size];
        ratioLengths = new double[size - 1];

        // Temporary arrays to store the absolute angle of each segment vector.
        double[] tempOrigAngles = new double[size];
        double[] tempDestinyAngles = new double[size];


        // First pass: Calculate absolute angles of each segment and the ratio of lengths.
        for (int i = 0; i < size - 1; i++) {
            Vec v1 = getIntermediateObject().get(i).getV().to(getIntermediateObject().get(i + 1).getV());
            tempOrigAngles[i] = v1.getAngle();
            Vec v2 = getDestinyObject().get(i).getV().to(getDestinyObject().get(i + 1).getV());
            tempDestinyAngles[i] = v2.getAngle();
            // Store the relative change in length, not the absolute ratio.
            ratioLengths[i] = v2.norm() / v1.norm() - 1;
        }


        // Second pass: Calculate the angle at each vertex (the turn angle).
        for (int i = 1; i < size - 1; i++) {
            // Angle at vertex i is the difference between the angle of segment (i, i+1) and (i-1, i).
            originAngles[i] = normalizeAngle(tempOrigAngles[i] - tempOrigAngles[i - 1] + Math.PI);
            destinyAngles[i] = normalizeAngle(tempDestinyAngles[i] - tempDestinyAngles[i - 1] + Math.PI);
        }

        // Store the specific transformation parameters for the pivotal segment.
        pivotalLengthRatio = ratioLengths[numPivotalSegment];
        pivotalAngle = (tempDestinyAngles[numPivotalSegment] - tempOrigAngles[numPivotalSegment]);
        while (pivotalAngle < -PI) pivotalAngle += 2 * PI;
        while (pivotalAngle > PI) pivotalAngle -= 2 * PI;

        //Compute auxiliary lambdas

        auxiliaryLambdas = new DoubleUnaryOperator[size - 1];
        for (int i = 1; i < size - 1; i++) {
            if (i <= numPivotalSegment) {
                int k = numPivotalSegment - i;
                auxiliaryLambdas[i] = UsefulLambdas.allocateTo(1d * k / numPivotalSegment, 1d * (k + 1) / numPivotalSegment);
            }
            if (i > numPivotalSegment) {
                int k = i - numPivotalSegment - 1;
                int size2 = size - 1 - numPivotalSegment - 1;
                auxiliaryLambdas[i] = UsefulLambdas.allocateTo(1d * k / size2, 1d * (k + 1) / size2);
            }
        }

        return true;
    }

    /**
     * Normalizes an angle to be in the range [0, 2*PI).
     *
     * @param angle The angle in radians.
     * @return The normalized angle.
     */
    private double normalizeAngle(double angle) {
        double twoPi = 2 * Math.PI;
        angle = angle % twoPi;
        if (angle < 0) {
            angle += twoPi;
        }

        return angle;
    }


    @Override
    public void doAnim(double t) {
        super.doAnim(t);
        // Get the interpolated time, respecting the animation's rate function.
        double lt = getLT(t);
        double rt = allocateT(t);// Raw time, clamped to [0,1], allocated with delay effect

        Shape intermediateObject = getIntermediateObject();
        // Restore the intermediate object to its original state (the origin shape) before applying the new frame's transformation.
        restoreStates(intermediateObject);

        // Apply custom timing functions if they exist.
        double ltshiftPivotal = (lambdaShiftPivotal == null ? lt : lambdaShiftPivotal.applyAsDouble(rt));
        double ltscPivotal = (lambdaScalePivotal == null ? lt : lambdaScalePivotal.applyAsDouble(rt));
        // Apply transformations in a specific order, starting from the pivotal segment.
        Vec pivotPoint = intermediateObject.get(numPivotalSegment).getV();
        applyPivotalAlign(pivotPoint, ltshiftPivotal);
        applyPivotalScale(pivotPoint, ltscPivotal);


        applyTransformForwardPoints(numPivotalSegment, rt);

//        double ltb = (lambdaBackward == null ? lt : lambdaBackward.applyAsDouble(rt));
        applyTransformBackwardPoints(numPivotalSegment, rt);

        // Interpolate visual properties like color and stroke width.
        if (isShouldInterpolateStyles()) {
            intermediateObject.getMp().interpolateFrom(getOriginObject().getMp(), getDestinyObject().getMp(), lt);
        }
        // Transform effects, if any are attached to the animation.
        applyAnimationEffects(lt, intermediateObject);

    }

    /**
     * Applies the alignment (translation and rotation) to the entire shape based on the pivotal segment.
     *
     * @param pivotPoint The point around which transformations occur.
     * @param lt         The interpolated time, from 0 to 1.
     */
    private void applyPivotalAlign(Vec pivotPoint, double lt) {
        Shape inter = getIntermediateObject();
        // 1. Translate the whole shape to align the pivotal points.
        inter.shift(vShift.mult(lt));

        // 2. Rotate the whole shape around the pivotal point to align the pivotal segment's angle.
        inter.rotate(pivotPoint, pivotalAngle * lt);
    }

    /**
     * Applies scaling to the pivotal segment by shifting all subsequent points.
     *
     * @param pivotPoint The point that starts the pivotal segment.
     * @param lt         The interpolated time, from 0 to 1.
     */
    private void applyPivotalScale(Vec pivotPoint, double lt) {
        Shape inter = getIntermediateObject();

        // 3. Scale the pivotal segment by shifting all subsequent points.
        // This moves the "end" of the pivotal segment, effectively stretching/shrinking it.
        if (numPivotalSegment + 1 < inter.size()) {
            Vec nextPoint = inter.get(numPivotalSegment + 1).getV();
            Vec v = pivotPoint.to(nextPoint);
            Vec shiftVector = v.mult(pivotalLengthRatio * lt);
            // Apply shift only to points after the pivotal segment.
            for (int i = numPivotalSegment + 1; i < inter.size(); i++) {
                inter.get(i).getV().add(shiftVector);
            }
        }
    }


    /**
     * Iteratively applies transformations to the points *after* the pivotal segment. For each vertex `i`, it applies a
     * rotation and a scaling (as a shift) to all subsequent points `j > i`. This method has a time complexity of O(N^2)
     * per frame, which may be slow for shapes with many points.
     *
     * @param numPivotalSegment The index of the point starting the pivotal segment.
     * @param t                 The interpolated time, from 0 to 1.
     */
    private void applyTransformForwardPoints(int numPivotalSegment, double t) {
        Shape inter = getIntermediateObject();
        int size = inter.size();
        if (numPivotalSegment >= size - 1) {
            return; // No forward points to transform
        }


        for (int i = numPivotalSegment + 1; i < size - 1; i++) {
//            int k=i-numPivotalSegment-1;
//            int size2 = size - 1 - numPivotalSegment - 1;
//            double ltd = UsefulLambdas.allocateTo(1d*k/size2,1d*(k+1)/size2).applyAsDouble(t);
            double ltd;
            if (isStepByStep) {
                double auxt = auxiliaryLambdas[i].applyAsDouble(t);
                ltd = (lambdaForward == null ? lambda.applyAsDouble(auxt) : lambdaForward.applyAsDouble(auxt));
            } else {
                ltd = (lambdaForward == null ? lambda.applyAsDouble(t) : lambdaForward.applyAsDouble(t));
            }

            JMPathPoint currentPivot = inter.get(i);
            JMPathPoint nextPoint = inter.get(i + 1);

            // Calculate the vector for the current segment to determine the shift direction for scaling.
            Vec v = currentPivot.getV().to(nextPoint.getV());
            // The shift amount depends on the length ratio and time.
            // This effectively scales the segment (i, i+1).
            Vec shiftVector = v.mult(ratioLengths[i] * ltd);

            // The rotation angle for the current vertex to match the destiny shape's angle.
            double ang = destinyAngles[i] - originAngles[i];
            double rotationAngle = ang * ltd;

            // Apply transformations (shift for scaling, then rotation) to all subsequent points.
            for (int j = i + 1; j < size; j++) {
                JMPathPoint pointToTransform = inter.get(j);
                pointToTransform.shift(shiftVector);
                pointToTransform.rotate(currentPivot, rotationAngle);
            }
        }
    }

    /**
     * Iteratively applies transformations to the points *before* the pivotal segment. For each vertex `i`, it applies a
     * rotation and a scaling (as a shift) to all preceding points `j < i`. This method has a time complexity of O(N^2)
     * per frame.
     *
     * @param numPivotalSegment The index of the point starting the pivotal segment.
     * @param t                The interpolated time, from 0 to 1.
     */
    private void applyTransformBackwardPoints(int numPivotalSegment, double t) {
        Shape inter = getIntermediateObject();
        if (numPivotalSegment <= 0) {
            return; // No backward points to transform
        }


        for (int i = numPivotalSegment; i > 0; i--) {
//            int k = numPivotalSegment-i;
//            double ltd = UsefulLambdas.allocateTo(1d*k/numPivotalSegment,1d*(k+1)/numPivotalSegment).applyAsDouble(lt);

            double ltd;
            if (isStepByStep) {
                double auxt = auxiliaryLambdas[i].applyAsDouble(t);
                ltd = (lambdaBackward == null ? lambda.applyAsDouble(auxt) : lambdaBackward.applyAsDouble(auxt));
            } else {
                ltd = (lambdaBackward == null ? lambda.applyAsDouble(t) : lambdaBackward.applyAsDouble(t));
            }


            JMPathPoint currentPivot = inter.get(i);
            JMPathPoint prevPoint = inter.get(i - 1);

            // Calculate the vector for the current segment to determine the shift direction for scaling.
            Vec v = currentPivot.getV().to(prevPoint.getV());
            // The shift amount depends on the length ratio of the previous segment and time.
            // This effectively scales the segment (i-1, i).
            Vec shiftVector = v.mult(ratioLengths[i - 1] * ltd);

            // The rotation angle for the current vertex.
            double ang = destinyAngles[i] - originAngles[i];
            // Note the negative sign for backward rotation, to "un-twist" in the opposite direction.
            double rotationAngle = -ang * ltd;

            // Apply transformations (shift for scaling, then rotation) to all preceding points.
            for (int j = i - 1; j >= 0; j--) {
                JMPathPoint pointToTransform = inter.get(j);
                pointToTransform.shift(shiftVector);
                pointToTransform.rotate(currentPivot, rotationAngle);
            }
        }
    }

    @Override
    public void cleanAnimationAt(double t) {
        double lt = Math.max(Math.max(getLT(t),
                (lambdaBackward==null ? -1: lambdaBackward.applyAsDouble(t))),
                (lambdaForward==null ? -1: lambdaForward.applyAsDouble(t)));
        if (lt == 0) {
            cleanAt0();
            return;
        }
         lt = Math.min(Math.min(getLT(t),
                        (lambdaBackward==null ? 2: lambdaBackward.applyAsDouble(t))),
                (lambdaForward==null ? 2: lambdaForward.applyAsDouble(t)));
        if (lt == 1) {
            cleanAt1();
            return;
        }
        cleanAtIntermediate();
    }


}
