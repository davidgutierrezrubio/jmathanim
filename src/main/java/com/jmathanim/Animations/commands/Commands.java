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

    /**
     * Animation command that transforms a MathObject through an Homotopy.
     * Homotopy is specified by 2 pairs of points (origin-destiny)
     *
     * @param object Object to transform
     * @param a First origin point
     * @param b Second origin point
     * @param c First destiny point
     * @param d Second destiny point
     * @param runtime Duration in seconds
     * @return Animation to run with
     * {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...) playAnimation}
     * method
     */
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

    /**
     * Animation command that perfoms a reflection specified by a {@link Shape}
     *
     * @param object {@link MathObject} to reflect
     * @param axis Axis. Only the first 2 points of the {@link Shape} are used
     * to determine axis
     * @param runtime Duration in seconds
     * @return Animation to run with
     * {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...) playAnimation}
     * method
     */
    public static ApplyCommand reflectionByAxis(MathObject object, Shape axis, double runtime) {
        return new ApplyCommand(new SingleMathObjectCommand(object) {
            double tPrevious;
            Shape S = axis;
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

    /**
     * Animation command that changes the math drawing properties of given
     * object, interpolating
     *
     * @param object Object to animate
     * @param mp Destination {@link MathObjectDrawingProperties}
     * @param runtime Time duration in seconds
     * @return Animation to run with
     * {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...) playAnimation}
     * method
     */
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

    /**
     * Animation command that changes the style of given object, interpolating
     *
     * @param object Object to animate
     * @param styleName Name of destination style
     * @param runtime Time duration in seconds
     * @return Animation to run with
     * {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...) playAnimation}
     * method
     */
    public static ApplyCommand setStyle(MathObject object, String styleName, double runtime) {
        return setMP(object, MathObjectDrawingProperties.createFromStyle(styleName), runtime);
    }

    /**
     * Animation command that zooms the camera to a given area specified by a
     * {@link Rect}
     *
     * @param camera Camera to zoom
     * @param rectToZoom Area to zoom
     * @param runtime Time duration in seconds
     * @return Animation to run with
     * {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...) playAnimation}
     * method
     */
    public static ApplyCommand cameraZoomToRect(Camera camera, Rect rectToZoom, double runtime) {
        return new ApplyCommand(new AbstractCommand() {
            Camera cam = camera;
            Rect rDst = cam.getRectThatContains(rectToZoom);
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

    /**
     * Animation that pans the {@link Camera} by a given vector
     *
     * @param camera Camera to pan
     * @param shiftVector Shift vector
     * @param runtime Time duration in seconds
     * @return Animation to run with
     * {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...) playAnimation}
     * method
     */
    public static ApplyCommand cameraShift(Camera camera, Vec shiftVector, double runtime) {
        Rect r = camera.getMathView().shifted(shiftVector);
        return cameraZoomToRect(camera, r, runtime);

    }

    /**
     * Animation command that reduces the size and alpha of the
     * {@link MathObject}. After finishing the animation, object is removed from
     * the current scene.
     *
     * @param object Object to animate
     * @return Animation to run with
     * {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...) playAnimation}
     * method
     */
    public static ApplyCommand shrinkOut(MathObject object, double runtime) {
        return shrinkOut(object, 0, runtime);
    }

    /**
     * Animation command that reduces the size and alpha of the
     * {@link MathObject}.A rotation of a given angle is performed meanwhile.
     * After finishing the animation, object is removed from the current scene.
     *
     * @param object Object to animate
     * @param angle Angle to rotate, in radians
     * @param runtime Duration time in seconds
     * @return Animation to run with
     * {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...) playAnimation}
     * method
     */
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
                mathObject.multDrawAlpha(1 - t);
                mathObject.multFillAlpha(1 - t);
                mathObject.rotate(t * angle);
            }

            @Override
            public void finish() {
                JMathAnimConfig.getConfig().getScene().remove(mathObject);
            }
        }, runtime);
    }//End of shrinkOut command

    /**
     * Performs the inverse animation than {@link shrinkOut}, that its, scale
     * the size and alpha of the object from zero.
     *
     * @param object Object to animate
     * @param runtime Duration time in seconds
     * @return Animation to run with
     * {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...) playAnimation}
     * method
     */
    public static ApplyCommand growIn(MathObject object, double runtime) {
        return growIn(object, 0, runtime);
    }

    /**
     * Performs the inverse animation than {@link shrinkOut}, that its, scale
     * the size and alpha of the object from zero. An inverse rotation from
     * given angle to 0 is performed.
     *
     * @param object Object to animate
     * @param angle Rotation angle
     * @param runtime Duration time in seconds
     * @return Animation to run with
     * {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...) playAnimation}
     * method
     */
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
                mathObject.multDrawAlpha(t);
                mathObject.multFillAlpha(t);
                mathObject.rotate((1 - t) * angle);
            }

            @Override
            public void finish() {
                execute(1);
            }
        }, runtime);
    }//End of growIn command

    /**
     * Performs an animation modifying the alpha of the object from 0 to the
     * original alpha of object. Both drawAlpha and fillAlpha are animated.
     *
     * @param object Object to animate
     * @param runtime Duration time in seconds
     * @return Animation to run with
     * {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...) playAnimation}
     * method
     */
    public static ApplyCommand fadeIn(MathObject object, double runtime) {
        return new ApplyCommand(new SingleMathObjectCommand(object) {

            @Override
            public void initialize() {
                mathObject.saveState();
            }

            @Override
            public void execute(double t) {
                mathObject.restoreState();
                mathObject.multDrawAlpha(t);
                mathObject.multFillAlpha(t);
            }

            @Override
            public void finish() {
                execute(1);
            }
        }, runtime);
    }//End of fadeIn command

    /**
     * Performs an animation modifying the alpha of the object to 0. Both
     * drawAlpha and fillAlpha are animated. Object is removed from current
     * scene after finishing animation.
     *
     * @param object Object to animate
     * @param runtime Duration time in seconds
     * @return Animation to run with
     * {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...) playAnimation}
     * method
     */
    public static ApplyCommand fadeOut(MathObject object, double runtime) {
        return new ApplyCommand(new SingleMathObjectCommand(object) {

            @Override
            public void initialize() {
                mathObject.saveState();
            }

            @Override
            public void execute(double t) {
                mathObject.restoreState();
                mathObject.multDrawAlpha(1 - t);
                mathObject.multFillAlpha(1 - t);
            }

            @Override
            public void finish() {
                execute(0);
                JMathAnimConfig.getConfig().getScene().remove(mathObject);
            }
        }, runtime);
    }//End of fadeOut command
}
