/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations.commands;

import com.jmathanim.Animations.AffineTransform;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class Commands {

    public static SingleMathObjectCommand shift(MathObject object, double dx, double dy) {
        return shift(object, new Vec(dx, dy));
    }

    public static SingleMathObjectCommand shift(MathObject object, Vec sv) {
        return new SingleMathObjectCommand(object) {
            Vec shiftVector = sv;

            @Override
            public void initialize() {
                mathObject.saveState();
            }

            @Override
            public void execute(double t) {
                mathObject.restoreState();
                mathObject.shift(shiftVector.mult(t));
            }

            @Override
            public void finish() {
            }
        };
    }//End of shift command

    public static SingleMathObjectCommand scale(MathObject object, Point c, double sc) {
        return scale(object, c, sc, sc, sc);
    }

    public static SingleMathObjectCommand scale(MathObject object, Point c, double scx, double scy, double scz) {
        return new SingleMathObjectCommand(object) {
            double scalex = scx;
            double scaley = scy;
            double scalez = scz;
            Point scaleCenter = c;

            @Override
            public void initialize() {
                mathObject.saveState();
            }

            @Override
            public void execute(double t) {
                mathObject.restoreState();
                double scax = 1 - t + scalex * t;
                double scay = 1 - t + scaley * t;
                double scaz = 1 - t + scalez * t;
                mathObject.scale(scaleCenter, scax, scay, scaz);
            }

            @Override
            public void finish() {
            }
        };
    }//End of scale command

    public static SingleMathObjectCommand rotate(MathObject object, Point c, double ang) {
        return new SingleMathObjectCommand(object) {
            double angle = ang;
            double tPrevious;
            Point rotationCenter = c;
            Point rotationCenterPrevious;
            AffineTransform tr;

            @Override
            public void initialize() {
                mathObject.saveState();//Easy way, but interferes with multiple animations (not easy to solve)
                tPrevious = 0;
                rotationCenterPrevious = rotationCenter.copy();
            }

            @Override
            public void execute(double t) {
                mathObject.restoreState();
                tr = AffineTransform.create2DRotationTransform(rotationCenter, angle * t);
                tr.applyTransform(mathObject);
            }

            @Override
            public void finish() {
            }
        };
    }//End of scale command
}
