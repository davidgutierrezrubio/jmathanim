/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations.commands;

import com.jmathanim.Animations.AffineTransform;
import com.jmathanim.Animations.ApplyCommand;
import com.jmathanim.Cameras.Camera;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.MathObjectDrawingProperties;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Commands {

    public static ApplyCommand shift(MathObject object, double dx, double dy, double runtime) {
        return shift(object, new Vec(dx, dy), runtime);
    }

    public static ApplyCommand shift(MathObject object, Vec sv, double runtime) {
        return new ApplyCommand(new SingleMathObjectCommand(object) {
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
                execute(1);//Ensure that the object is well-located
            }
        }, runtime);
    }//End of shift command

    public static ApplyCommand scale(MathObject object, Point c, double sc, double runtime) {
        return scale(object, c, sc, sc, sc, runtime);
    }

    public static ApplyCommand scale(MathObject object, Point c, double scx, double scy, double scz, double runtime) {
        return new ApplyCommand(new SingleMathObjectCommand(object) {
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
                execute(1);
            }
        }, runtime);
    }//End of scale command

    public static ApplyCommand rotate(MathObject object, Point c, double ang, double runtime) {
        return new ApplyCommand(new SingleMathObjectCommand(object) {
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
                execute(1);
            }
        }, runtime);
    }//End of scale command

    public static ApplyCommand homotopy(MathObject object, Point a, Point b, Point c, Point d, double runtime) {
        return new ApplyCommand(new SingleMathObjectCommand(object) {
            double tPrevious;
            Point A = a;
            Point B = b;
            Point C = c;
            Point D = d;
            AffineTransform tr;

            @Override
            public void initialize() {
                mathObject.saveState();//Easy way, but interferes with multiple animations (not easy to solve)
            }

            @Override
            public void execute(double t) {
                mathObject.restoreState();
                tr = AffineTransform.createDirect2DHomotopy(A, B, C, D, t);
                tr.applyTransform(mathObject);
            }

            @Override
            public void finish() {
                execute(1);
            }
        }, runtime);
    }//End of homotopy command

    public static ApplyCommand reflectionByAxis(MathObject object, Shape s, double runtime) {
        return new ApplyCommand(new SingleMathObjectCommand(object) {
            double tPrevious;
            Shape S = s;
            AffineTransform tr;

            @Override
            public void initialize() {
                mathObject.saveState();//Easy way, but interferes with multiple animations (not easy to solve)
            }

            @Override
            public void execute(double t) {
                mathObject.restoreState();
                tr = AffineTransform.createReflectionByAxis(S, t);
                tr.applyTransform(mathObject);
            }

            @Override
            public void finish() {
                execute(1);
            }
        }, runtime);
    }//End of homotopy command

    public static ApplyCommand setMP(MathObject object, MathObjectDrawingProperties mp, double runtime) {
        return new ApplyCommand(new SingleMathObjectCommand(object) {
            MathObjectDrawingProperties mpDst = mp;
            MathObjectDrawingProperties mpBase;

            @Override
            public void initialize() {
                mpBase = mathObject.mp.copy();
            }

            @Override
            public void execute(double t) {
                mathObject.mp.interpolateFrom(mpBase, mpDst, t);
            }

            @Override
            public void finish() {
                execute(1);
            }
        }, runtime);
    }//End of setMP command

    public static ApplyCommand setTemplate(MathObject object, String templateName, double runtime) {
        return setMP(object, MathObjectDrawingProperties.createFromTemplate(templateName), runtime);
    }

    public static ApplyCommand cameraFocusToRect(Camera camera, Rect rd, double runtime) {
        return new ApplyCommand(new AbstractCommand() {
            Camera cam = camera;
            Rect rDst = cam.getRectThatContains(rd);
            Rect rSource;

            @Override
            public void initialize() {
                rSource = cam.getMathView();
            }

            @Override
            public void execute(double t) {
                Rect r = rSource.interpolate(rDst, t);
                cam.setMathView(r);
            }

            @Override
            public void finish() {
                execute(1);
            }

            @Override
            public void addObjectsToScene(JMathAnimScene scene) {
            }
        }, runtime);
    }

    public static ApplyCommand cameraShift(Camera camera, Vec v, double runtime) {
        Rect r = camera.getMathView().shifted(v);
        return cameraFocusToRect(camera, r, runtime);

    }

    public static ApplyCommand shrinkOut(MathObject object, double runtime) {
        return shrinkOut(object, 0, runtime);
    }

    public static ApplyCommand shrinkOut(MathObject object, double angle, double runtime) {
        return new ApplyCommand(new SingleMathObjectCommand(object) {

            @Override
            public void initialize() {
                mathObject.saveState();
            }

            @Override
            public void execute(double t) {
                mathObject.restoreState();
                mathObject.scale(1 - t);
                mathObject.drawAlpha(mathObject.mp.drawColor.alpha * (1 - t));
                mathObject.fillAlpha(mathObject.mp.fillColor.alpha * (1 - t));
                mathObject.rotate(t * angle);
            }

            @Override
            public void finish() {
                JMathAnimConfig.getConfig().getScene().remove(mathObject);
            }
        }, runtime);
    }//End of shrinkOut command

    public static ApplyCommand growIn(MathObject object, double runtime) {
        return growIn(object, 0, runtime);
    }

    public static ApplyCommand growIn(MathObject object, double angle, double runtime) {
        return new ApplyCommand(new SingleMathObjectCommand(object) {

            @Override
            public void initialize() {
                mathObject.saveState();
            }

            @Override
            public void execute(double t) {
                mathObject.restoreState();
                mathObject.scale(t);
                mathObject.drawAlpha(mathObject.mp.drawColor.alpha * t);
                mathObject.fillAlpha(mathObject.mp.fillColor.alpha * t);
                mathObject.rotate((1 - t) * angle);
            }

            @Override
            public void finish() {
                execute(1);
            }
        }, runtime);
    }//End of growIn command
}
