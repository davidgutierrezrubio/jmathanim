package com.jmathanim.Animations.Strategies.Transform;

import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.MathObjectGroup;
import com.jmathanim.mathobjects.Shape;
import com.jmathanim.mathobjects.updateableObjects.Trail;

import java.util.ArrayList;

public class TwistTransform extends TransformShape2ShapeStrategy {

    private int numPoint;
    private Vec vShift;
    private double[] originAngles;
    private double[] destinyAngles;
    private double[] originLengths;
    private double[] destinyLengths;

    public TwistTransform(double runTime, Shape origin, Shape destiny, int numPoint) {
        super(runTime);
        setOrigin(origin);
        setDestiny(destiny);
        numPoint = numPoint;
    }

    @Override
    public boolean doInitialization() {
        super.doInitialization();
        vShift = getOriginObject().get(numPoint).p.to(getDestinyObject().get(numPoint).p);
        setIntermediate(getOriginObject().copy());
        getIntermediateObject().drawColor("red");
        getIntermediateObject().setObjectLabel("intermediate");
        getOriginObject().setObjectLabel("origin");
        getDestinyObject().setObjectLabel("destiny");
        saveStates(getIntermediateObject());

        for (int i = 0; i < getIntermediateObject().size(); i++) {
//            scene.add(Trail.make(getIntermediateObject().get(i).p).drawColor("blue"));
        }

        //Calcular angulos
        int size = getOriginObject().size();
        originAngles=new double[size-1];
        destinyAngles=new double[size-1];
        originLengths=new double[size-1];
        destinyLengths=new double[size-1];

        double[] tempOrigAngles=new double[size-1];
        double[] tempDestinyAngles=new double[size-1];


        for (int i = 0; i < size - 1; i++) {
            Vec v1 = getOriginObject().get(i).p.to(getOriginObject().get(i + 1).p);
            tempOrigAngles[i]= v1.getAngle();
            originLengths[i]=v1.norm();
            Vec v2 = getDestinyObject().get(i).p.to(getDestinyObject().get(i + 1).p);
            tempDestinyAngles[i]= v2.getAngle();
            destinyLengths[i] = v2.norm();
        }
        originAngles[0]=tempOrigAngles[0];
        destinyAngles[0]=tempDestinyAngles[0];

        for (int i=1; i<size-1;i++) {
            originAngles[i]=normalizeAngle(tempOrigAngles[i]-tempOrigAngles[i-1]+Math.PI);
            destinyAngles[i]=normalizeAngle(tempDestinyAngles[i]-tempDestinyAngles[i-1]+Math.PI);
        }



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
        Shape intermediateObject = getIntermediateObject();
        restoreStates(intermediateObject);
        intermediateObject.shift(vShift.mult(lt));

        int size = getOriginObject().size();
        for (int i = 0; i < size-1; i++) {

            MathObjectGroup mg = MathObjectGroup.make();
            for (int j = i+1; j < size; j++) {
                mg.add(getIntermediateObject().get(j));
            }

            double ang=destinyAngles[i]-originAngles[i];
            double lambda1 = destinyLengths[i]/originLengths[i] - 1;
            Vec v=getIntermediateObject().get(i).p.to(getIntermediateObject().get(i+1).p);
            mg.shift(v.mult(lambda1));
            mg.rotate(getIntermediateObject().get(i).p,ang*lt);
        }

        if (isShouldInterpolateStyles()) {
            intermediateObject.getMp().interpolateFrom(getOriginObject().getMp(), getDestinyObject().getMp(), lt);
        }
        // Transform effects
        applyAnimationEffects(lt, intermediateObject);

    }
}
