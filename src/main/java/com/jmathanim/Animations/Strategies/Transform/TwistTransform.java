package com.jmathanim.Animations.Strategies.Transform;

import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.MathObjectGroup;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

import java.util.function.DoubleUnaryOperator;

public class TwistTransform extends TransformShape2ShapeStrategy {

    private int numPivotalSegment;
    private Vec vShift;
    private double[] originAngles;
    private double[] destinyAngles;
    private double[] ratioLengths;
    private double pivotalAngle;
    private double pivotalLengthRatio;
    private DoubleUnaryOperator lambdaForward;

    public DoubleUnaryOperator getLambdaBackward() {
        return lambdaBackward;
    }

    public void setLambdaBackward(DoubleUnaryOperator lambdaBackward) {
        this.lambdaBackward = lambdaBackward;
    }

    public DoubleUnaryOperator getLambdaForward() {
        return lambdaForward;
    }

    public void setLambdaForward(DoubleUnaryOperator lambdaForward) {
        this.lambdaForward = lambdaForward;
    }

    private DoubleUnaryOperator lambdaBackward;

    public TwistTransform(double runTime, Shape origin, Shape destiny, int numPoint) {
        super(runTime);
        setOrigin(origin);
        setDestiny(destiny);
        numPivotalSegment = numPoint;
        lambdaBackward=null;
        lambdaForward=null;
    }

    @Override
    public boolean doInitialization() {
        super.doInitialization();

        setIntermediate(getOriginObject().copy());
        getIntermediateObject().getPath().openPath();
        getIntermediateObject().setObjectLabel("intermediate");
        saveStates(getIntermediateObject());

        vShift = getIntermediateObject().get(numPivotalSegment).p.to(getDestinyObject().get(numPivotalSegment).p);
        int size = getIntermediateObject().size();
        originAngles = new double[size];
        destinyAngles = new double[size];
        ratioLengths = new double[size - 1];

        double[] tempOrigAngles = new double[size];
        double[] tempDestinyAngles = new double[size];


        for (int i = 0; i < size - 1; i++) {
            Vec v1 = getIntermediateObject().get(i).p.to(getIntermediateObject().get(i + 1).p);
            tempOrigAngles[i] = v1.getAngle();
            Vec v2 = getDestinyObject().get(i).p.to(getDestinyObject().get(i + 1).p);
            tempDestinyAngles[i] = v2.getAngle();
            ratioLengths[i] = v2.norm() / v1.norm() - 1;
        }


        for (int i = 1; i < size - 1; i++) {
            originAngles[i] = normalizeAngle(tempOrigAngles[i] - tempOrigAngles[i - 1] + Math.PI);
            destinyAngles[i] = normalizeAngle(tempDestinyAngles[i] - tempDestinyAngles[i - 1] + Math.PI);
        }

        pivotalLengthRatio = ratioLengths[numPivotalSegment];
        pivotalAngle = tempDestinyAngles[numPivotalSegment] - tempOrigAngles[numPivotalSegment];


        return true;
    }

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
        double lt = getLT(t);
        double rt;
        rt = (t < 0 ? 0 : t);
        rt = (rt > 1 ? 1 : rt);

        double ltf = (lambdaForward == null ? lt : lambdaForward.applyAsDouble(rt));
        double ltb = (lambdaBackward == null ? lt : lambdaBackward.applyAsDouble(rt));

        Shape intermediateObject = getIntermediateObject();
        restoreStates(intermediateObject);
        int numPoint = 1;
        applyTransformToPivotalSegment(numPivotalSegment, lt);
        applyTransformForwardPoints(numPivotalSegment, ltf);
        applyTransformBackwardPoints(numPivotalSegment, ltb);

        if (isShouldInterpolateStyles()) {
            intermediateObject.getMp().interpolateFrom(getOriginObject().getMp(), getDestinyObject().getMp(), lt);
        }
        // Transform effects
        applyAnimationEffects(lt, intermediateObject);

    }

    private MathObjectGroup subPath(int pivotal, boolean forward) {
        int size = getIntermediateObject().size();
        MathObjectGroup mg = MathObjectGroup.make();
        if (forward)
            for (int j = pivotal + 1; j < size; j++) {
                mg.add(getIntermediateObject().get(j));
            }
        else {
            for (int j = pivotal - 1; j >=0; j--) {
                mg.add(getIntermediateObject().get(j));
            }
        }
        return mg;
    }

    private void applyTransformToPivotalSegment(int numPivotalSegment, double lt) {
        getIntermediateObject().shift(vShift.mult(lt));
        Point pivotPoint = getIntermediateObject().get(numPivotalSegment).p;
        getIntermediateObject().rotate(pivotPoint, pivotalAngle * lt);
        MathObjectGroup mg=subPath(numPivotalSegment,true);
        Vec v = pivotPoint.to(getIntermediateObject().get(numPivotalSegment+1).p);
        mg.shift(v.mult(pivotalLengthRatio*lt));
    }

    private void applyTransformForwardPoints(int numPivotalSegment, double lt) {
        int size = getIntermediateObject().size();
        if (numPivotalSegment==size-1) return; //No forward points
        for (int i = numPivotalSegment+1; i < size - 1; i++) {
            MathObjectGroup mg=subPath(i,true);
            double ang = destinyAngles[i] - originAngles[i];
            Vec v = getIntermediateObject().get(i).p.to(getIntermediateObject().get(i + 1).p);
            mg.shift(v.mult(ratioLengths[i]));
            mg.rotate(getIntermediateObject().get(i).p, ang * lt);
        }
    }

    private void applyTransformBackwardPoints(int numPivotalSegment, double lt) {
        if (numPivotalSegment==0) return;//No backward points
        for (int i = numPivotalSegment; i > 0; i--) {
            MathObjectGroup mg=subPath(i,false);
            double ang = destinyAngles[i] - originAngles[i];
            Vec v = getIntermediateObject().get(i).p.to(getIntermediateObject().get(i-1).p);
            mg.shift(v.mult(ratioLengths[i-1]*lt));
            mg.rotate(getIntermediateObject().get(i).p, -ang * lt);
        }
    }


}
