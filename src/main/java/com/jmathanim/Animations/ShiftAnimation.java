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
package com.jmathanim.Animations;

import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import java.util.HashMap;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class ShiftAnimation extends Animation {

    protected MathObject[] mathObjects;
    HashMap<MathObject, Vec> shiftVectors;
    private HashMap<MathObject, Double> jumpHeights;
    private HashMap<MathObject, Double> rotateAngles;
    private HashMap<MathObject, Double> scaleEffects;

    public ShiftAnimation(double runTime, MathObject[] mathObjects) {
        super(runTime);
        this.mathObjects = mathObjects;
        shiftVectors = new HashMap<>();
        rotateAngles = new HashMap<>();
        jumpHeights = new HashMap<>();
        scaleEffects = new HashMap<>();

    }

    @Override
    public void initialize(JMathAnimScene scene) {
        super.initialize(scene);
        saveStates(mathObjects);
        addObjectsToscene(mathObjects);
    }

    @Override
    public void doAnim(double t) {

        double lt = getLambda().applyAsDouble(t);
        restoreStates(mathObjects);
        for (MathObject obj : mathObjects) {
            Vec v = shiftVectors.get(obj);//Gets the shift vector for this object
            obj.shift(v.mult(lt));
            //Jump
            Double jumpHeight = jumpHeights.get(obj);
            if (jumpHeight != null) {
                if (jumpHeight != 0) {
                    Vec jumpVector = Vec.to(-v.y, v.x).normalize().mult(jumpHeight);
                    double jlt = 4 * lt * (1 - lt);
                    obj.shift(jumpVector.mult(jlt));
                }
            }
             //Rotate
            Double rotateAngle = rotateAngles.get(obj);
            if (rotateAngle != null) {
                if (rotateAngle != 0) {
                    obj.rotate(rotateAngle*lt);
                }
            }
              //Scale effect
            Double scaleEffect = scaleEffects.get(obj);
            if (scaleEffect != null) {
                if (scaleEffect != 0) {
                     double L = 4 * (1 - scaleEffect);
                    double scalelt =  1 - lt * (1 - lt) * L;
                    obj.scale(scalelt);
                }
            }
            
        }
    }

    @Override
    public void finishAnimation() {
        super.finishAnimation();
        doAnim(1);
    }

    public Vec getShiftVector(MathObject obj) {
        return this.shiftVectors.get(obj);
    }

    public <T extends ShiftAnimation> T setShiftVector(MathObject obj, Vec shiftVector) {
        this.shiftVectors.put(obj, shiftVector);
        return (T) this;
    }

    public double getJumpHeight(MathObject obj) {
        return rotateAngles.get(obj);
    }

    public <T extends ShiftAnimation> T setJumpHeight(MathObject obj, double jumpHeight) {
        this.jumpHeights.put(obj, jumpHeight);
        return (T) this;
    }

    public <T extends ShiftAnimation> T setJumpHeight(double jumpHeight) {
        for (MathObject obj : mathObjects) {
            setJumpHeight(obj, jumpHeight);
        }
        return (T) this;
    }
    
     public <T extends ShiftAnimation> T setScaleEffect(MathObject obj, double jumpHeight) {
        this.scaleEffects.put(obj, jumpHeight);
        return (T) this;
    }

    public <T extends ShiftAnimation> T setScaleEffect(double jumpHeight) {
        for (MathObject obj : mathObjects) {
            setScaleEffect(obj, jumpHeight);
        }
        return (T) this;
    }
    
      public <T extends ShiftAnimation> T setRotateEffect(MathObject obj, double angle) {
        this.rotateAngles.put(obj, angle);
        return (T) this;
    }

    public <T extends ShiftAnimation> T setRotateEffect(double angle) {
        for (MathObject obj : mathObjects) {
            setRotateEffect(obj, angle);
        }
        return (T) this;
    }
    
}
