package com.jmathanim.MathObjects.Updaters;

import com.jmathanim.Utils.Boxable;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;

public class MoveUpdater extends Updater {
    private final Boxable ref;
    private final Type moveType;
    private double speedt;
    private double speedt2;
    private final double speed;
    private boolean speedsNotComputed;
    private final boolean instant;

    public MoveUpdater(Boxable ref, Type moveType, double speed) {
        this.ref = ref;
        this.speed = speed;
        this.moveType = moveType;
        speedsNotComputed = true;
        instant = (speed <= 0);
    }

//    @Override
//    public void computeUpdateLevel() {
//        if (ref instanceof Updateable) {
//            Updateable obj = (Updateable) ref;
//            setUpdateLevel(Math.max(getMathObject().getUpdateLevel(), obj.getUpdateLevel()) + 1);
//        } else {
//            setUpdateLevel(getMathObject().getUpdateLevel() + 1);
//        }
//    }

    @Override
    public void update(JMathAnimScene scene) {
        if (!instant && speedsNotComputed) {
            speedt = speed * scene.getDt();
            speedt2 = speedt * speedt;
            speedsNotComputed = false;
        }
        Vec vShift = computeProperShiftVector();
        if (vShift == null) return;
        getMathObject().shift(vShift);
    }

    private Vec computeProperShiftVector() {
        Vec v = null;
        double x1, x2, y1, y2;
        switch (moveType) {
            case CENTER_AT:
                v = getMathObject().getCenter().to(ref.getBoundingBox().getCenter());
                break;
            case TO_THE_RIGHT_SIDE:
                x1 = getMathObject().getBoundingBox().xmin;
                x2 = ref.getBoundingBox().xmax;
                if (x1 < x2) v = Vec.to(x2 - x1, 0);
                break;
            case TO_THE_LEFT_SIDE:
                x1 = getMathObject().getBoundingBox().xmax;
                x2 = ref.getBoundingBox().xmin;
                if (x1 > x2) v = Vec.to(x2 - x1, 0);
                break;
            case TO_THE_UPPER_SIDE:
                y1 = getMathObject().getBoundingBox().ymin;
                y2 = ref.getBoundingBox().ymax;
                if (y1 < y2) v = Vec.to(0, y2 - y1);
                break;
            case TO_THE_LOWER_SIDE:
                y1 = getMathObject().getBoundingBox().ymax;
                y2 = ref.getBoundingBox().ymin;
                if (y1 > y2) v = Vec.to(0, y2 - y1);
                break;

        }
        if (v == null) return null;
        if (instant) return v;
        if (v.dot(v) < speedt2) {//If it is "too close"
            return v;
        } else {
            return v.normalize().scale(speedt);
        }
    }


    public enum Type {
        TO_THE_RIGHT_SIDE, TO_THE_LEFT_SIDE, TO_THE_UPPER_SIDE, TO_THE_LOWER_SIDE, CENTER_AT
    }
}
