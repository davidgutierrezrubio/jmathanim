/*
 * Copyright (C) 2020 David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
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

package com.jmathanim.Animations.commands;

import com.jmathanim.Animations.AffineJTransform;
import com.jmathanim.Animations.ApplyCommand;
import com.jmathanim.Cameras.Camera;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.MathObjectDrawingProperties;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Commands {

    public static ApplyCommand shift(double runtime, double dx, double dy, MathObject... objects) {
        return shift(runtime, new Vec(dx, dy), objects);
    }

    public static ApplyCommand shift(double runtime, Vec sv, MathObject... objects) {
        return new ApplyCommand(new MathObjectsCommand(objects) {
            Vec shiftVector = sv;

            @Override
            public void initialize() {
                for (MathObject obj : mathObjects) {
                    obj.saveState();
                }
            }

            @Override
            public void execute(double t) {
                for (MathObject obj : mathObjects) {
                    obj.restoreState();
                    obj.shift(shiftVector.mult(t));
                }
            }

            @Override
            public void finish() {
                execute(1);//Ensure that the object is well-located
            }
        }, runtime);
    }//End of shift command

    public static ApplyCommand scale(double runtime, Point c, double sc, MathObject... objects) {
        return scale(runtime, c, sc, sc, sc, objects);
    }

    public static ApplyCommand scale(double runtime, Point c, double scx, double scy, double scz, MathObject... objects) {
        return new ApplyCommand(new MathObjectsCommand(objects) {
            double scalex = scx;
            double scaley = scy;
            double scalez = scz;
            Point scaleCenter = c;

            @Override
            public void initialize() {
                for (MathObject obj : mathObjects) {
                    obj.saveState();
                }
            }

            @Override
            public void execute(double t) {
                for (MathObject obj : mathObjects) {
                    obj.restoreState();
                    double scax = 1 - t + scalex * t;
                    double scay = 1 - t + scaley * t;
                    double scaz = 1 - t + scalez * t;
                    if (scaleCenter != null) {
                        obj.scale(scaleCenter, scax, scay, scaz);
                    } else {
                        obj.scale(obj.getCenter(), scax, scay, scaz);
                    }
                }
            }

            @Override
            public void finish() {
                execute(1);
            }
        }, runtime);
    }//End of scale command

    public static ApplyCommand rotate(double runtime, double ang, MathObject... objects) {
        return rotate(runtime, null, ang, objects);

    }

    public static ApplyCommand rotate(double runtime, Point c, double ang, MathObject... objects) {
        return new ApplyCommand(new MathObjectsCommand(objects) {
            double angle = ang;
            double tPrevious;
            Point rotationCenter = c;
            AffineJTransform tr;

            @Override
            public void initialize() {
                for (MathObject obj : mathObjects) {
                    obj.saveState();//Easy way, but interferes with multiple animations (not easy to solve)
                }
            }

            @Override
            public void execute(double t) {
                for (MathObject obj : mathObjects) {
                    obj.restoreState();
                    if (rotationCenter == null) {
                        tr = AffineJTransform.create2DRotationTransform(obj.getCenter(), angle * t);
                    } else {
                        tr = AffineJTransform.create2DRotationTransform(rotationCenter, angle * t);
                    }
                    tr.applyTransform(obj);
                }
            }

            @Override
            public void finish() {
                execute(1);
            }
        }, runtime);
    }//End of rotate command

    public static ApplyCommand affineTransform(double runtime, Point a, Point b, Point c, Point d, Point e, Point f, MathObject... objects) {
        return new ApplyCommand(new MathObjectsCommand(objects) {
            double tPrevious;
            Point orig1 = a;
            Point orig2 = b;
            Point orig3 = c;
            Point dst1 = d;
            Point dst2 = e;
            Point dst3 = f;
            AffineJTransform tr;

            @Override
            public void initialize() {
                for (MathObject obj : mathObjects) {
                    obj.saveState();//Easy way, but interferes with multiple animations (not easy to solve)
                }
            }

            @Override
            public void execute(double t) {
                for (MathObject obj : mathObjects) {
                    obj.restoreState();
                    tr = AffineJTransform.createAffineTransformation(orig1, orig2, orig3, dst1, dst2, dst3, t);
                    tr.applyTransform(obj);
                }
            }

            @Override
            public void finish() {
                execute(1);
            }
        }, runtime);
    }//End of affineTransform command

    /**
     * Animation command that transforms a MathObject through an
     * Homotopy.Homotopy is specified by 2 pairs of points (origin-destiny)
     *
     * @param runtime Run time (in seconds)
     * @param a First origin point
     * @param b Second origin point
     * @param c First destiny point
     * @param d Second destiny point
     * @param objects Objects to animate (varargs)
     * @return Animation to run with
     * {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...) playAnimation}
     * method
     */
    public static ApplyCommand homotopy(double runtime, Point a, Point b, Point c, Point d, MathObject... objects) {
        return new ApplyCommand(new MathObjectsCommand(objects) {
            double tPrevious;
            Point A = a;
            Point B = b;
            Point C = c;
            Point D = d;
            AffineJTransform tr;

            @Override
            public void initialize() {
                for (MathObject obj : mathObjects) {
                    obj.saveState();//Easy way, but interferes with multiple animations (not easy to solve)
                }
            }

            @Override
            public void execute(double t) {
                for (MathObject obj : mathObjects) {
                    obj.restoreState();
                    tr = AffineJTransform.createDirect2DHomothecy(A, B, C, D, t);
                    tr.applyTransform(obj);
                }
            }

            @Override
            public void finish() {
                execute(1);
            }
        }, runtime);
    }//End of homotopy command

    /**
     * Animation command that perfoms a reflection that maps A into B
     *
     * @param runtime Duration in seconds
     * @param A Origin point
     * @param B Destiny point
     * @param objects Objects to animate (varargs)
     * @return Animation to run with
     * {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...) playAnimation}
     * method
     */
    public static ApplyCommand reflection(double runtime, Point A, Point B, MathObject... objects) {
        return new ApplyCommand(new MathObjectsCommand(objects) {
            double tPrevious;
            Point axis1 = A;
            Point axis2 = B;
            AffineJTransform tr;

            @Override
            public void initialize() {
                for (MathObject obj : mathObjects) {
                    obj.saveState();//Easy way, but interferes with multiple animations (not easy to solve)
                }
            }

            @Override
            public void execute(double t) {
                for (MathObject obj : mathObjects) {
                    obj.restoreState();
                    tr = AffineJTransform.createReflection(axis1, axis2, t);
                    tr.applyTransform(obj);
                }
            }

            @Override
            public void finish() {
                execute(1);
            }
        }, runtime);
    }//End of reflectionByAxis command

    /**
     * Animation command that perfoms a reflection specified by 2 points
     *
     * @param runtime Duration in seconds
     * @param a first axis point
     * @param b second axis point
     *
     * @param objects Objects to animate (varargs)
     * @return Animation to run with
     * {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...) playAnimation}
     * method
     */
    public static ApplyCommand reflectionByAxis(double runtime, Point a, Point b, MathObject... objects) {
        return new ApplyCommand(new MathObjectsCommand(objects) {
            double tPrevious;
            Point axis1 = a;
            Point axis2 = b;
            AffineJTransform tr;

            @Override
            public void initialize() {
                for (MathObject obj : mathObjects) {
                    obj.saveState();//Easy way, but interferes with multiple animations (not easy to solve)
                }
            }

            @Override
            public void execute(double t) {
                for (MathObject obj : mathObjects) {
                    obj.restoreState();
                    tr = AffineJTransform.createReflectionByAxis(axis1, axis2, t);
                    tr.applyTransform(obj);
                }
            }

            @Override
            public void finish() {
                execute(1);
            }
        }, runtime);
    }//End of reflectionByAxis command

    /**
     * Animation command that changes the math drawing properties of given
     * object, interpolating
     *
     * @param runtime Time duration in seconds
     * @param mp Destination {@link MathObjectDrawingProperties}
     * @param objects Objects to animate (varargs)
     * @return Animation to run with
     * {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...) playAnimation}
     * method
     */
    public static ApplyCommand setMP(double runtime, MathObjectDrawingProperties mp, MathObject... objects) {
        return new ApplyCommand(new MathObjectsCommand(objects) {
            MathObjectDrawingProperties mpDst = mp;
            MathObjectDrawingProperties mpBase;

            @Override
            public void initialize() {
                for (MathObject obj : mathObjects) {
                    mpBase = obj.mp.copy();
                }
            }

            @Override
            public void execute(double t) {
                for (MathObject obj : mathObjects) {
                    obj.mp.interpolateFrom(mpBase, mpDst, t);
                }
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
     * @param runtime Time duration in seconds
     * @param styleName Name of destination style
     * @param objects Objects to animate (varargs)
     *
     * @return Animation to run with
     * {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...) playAnimation}
     * method
     */
    public static ApplyCommand setStyle(double runtime, String styleName, MathObject... objects) {
        return setMP(runtime, MathObjectDrawingProperties.createFromStyle(styleName), objects);
    }

    /**
     * Animation command that zooms the camera to a given area specified by a
     * {@link Rect}
     *
     * @param runtime Time duration in seconds
     * @param camera Camera to zoom
     * @param rectToZoom Area to zoom
     * @return Animation to run with
     * {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...) playAnimation}
     * method
     */
    public static ApplyCommand cameraZoomToRect(double runtime, Camera camera, Rect rectToZoom) {
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
     * @param runtime Time duration in seconds
     * @param camera Camera to pan
     * @param shiftVector Shift vector
     *
     * @return Animation to run with
     * {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...) playAnimation}
     * method
     */
    public static ApplyCommand cameraShift(double runtime, Camera camera, Vec shiftVector) {
        Rect r = camera.getMathView().shifted(shiftVector);
        return cameraZoomToRect(runtime, camera, r);

    }
    public static ApplyCommand cameraScale(double runtime,Camera cam,double scale){
         return Commands.cameraZoomToRect(runtime, cam, cam.getMathView().scaled(scale, scale));
    }
    
    /**
     * Animation command that reduces the size and alpha of the
     * {@link MathObject}.After finishing the animation, object is removed from
     * the current scene.
     *
     * @param runtime Run time (in seconds)
     * @param objects Objects to animate (varargs)
     * @return Animation to run with
     * {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...) playAnimation}
     * method
     */
    public static ApplyCommand shrinkOut(double runtime, MathObject... objects) {
        return shrinkOut(runtime, 0, objects);
    }

    /**
     * Animation command that reduces the size and alpha of the
     * {@link MathObject}.A rotation of a given angle is performed
     * meanwhile.After finishing the animation, object is removed from the
     * current scene.
     *
     * @param angle Angle to rotate, in radians
     * @param runtime Duration time in seconds
     * @param objects Objects to animate (varargs)
     * @return Animation to run with
     * {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...) playAnimation}
     * method
     */
    public static ApplyCommand shrinkOut(double runtime, double angle, MathObject... objects) {
        return new ApplyCommand(new MathObjectsCommand(objects) {

            @Override
            public void initialize() {
                for (MathObject obj : mathObjects) {
                    obj.saveState();
                }
            }

            @Override
            public void execute(double t) {
                for (MathObject obj : mathObjects) {
                    obj.restoreState();
                    obj.scale(1 - t);
                    obj.multDrawAlpha(1 - t);
                    obj.multFillAlpha(1 - t);
                    obj.rotate(t * angle);
                }
            }

            @Override
            public void finish() {
                JMathAnimConfig.getConfig().getScene().remove(mathObjects);
            }
        }, runtime);
    }//End of shrinkOut command

    /**
     * Performs the inverse animation than {@link shrinkOut}, that its, scale
     * the size and alpha of the object from zero.
     *
     *
     * @param runtime Duration time in seconds
     * @return Animation to run with
     * @param objects Objects to animate (varargs)
     * {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...) playAnimation}
     * method
     */
    public static ApplyCommand growIn(double runtime, MathObject... objects) {
        return growIn(runtime, 0, objects);
    }

    /**
     * Performs the inverse animation than {@link shrinkOut}, that its, scale
     * the size and alpha of the object from zero. An inverse rotation from
     * given angle to 0 is performed.
     *
     * @param angle Rotation angle
     * @param runtime Duration time in seconds
     * @param objects Objects to animate (varargs)
     * @return Animation to run with
     * {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...) playAnimation}
     * method
     */
    public static ApplyCommand growIn(double runtime, double angle, MathObject... objects) {
        return new ApplyCommand(new MathObjectsCommand(objects) {

            @Override
            public void initialize() {
                for (MathObject obj : mathObjects) {
                    obj.saveState();
                }
            }

            @Override
            public void execute(double t) {
                for (MathObject obj : mathObjects) {
                    obj.restoreState();
                    obj.scale(t);
                    obj.multDrawAlpha(t);
                    obj.multFillAlpha(t);
                    obj.rotate((1 - t) * angle);
                }
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
     * @param runtime Duration time in seconds
     * @param objects Objects to animate (varargs)
     * @return Animation to run with
     * {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...) playAnimation}
     * method
     */
    public static ApplyCommand fadeIn(double runtime, MathObject... objects) {
        return new ApplyCommand(new MathObjectsCommand(objects) {

            @Override
            public void initialize() {
                for (MathObject obj : mathObjects) {
                    obj.saveState();
                }
            }

            @Override
            public void execute(double t) {
                for (MathObject obj : mathObjects) {
                    obj.restoreState();
                    obj.multDrawAlpha(t);
                    obj.multFillAlpha(t);
                }
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
     * @param runtime Duration time in seconds
     * @param objects Object to animate
     * @return Animation to run with
     * {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...) playAnimation}
     * method
     */
    public static ApplyCommand fadeOut(double runtime, MathObject... objects) {
        return new ApplyCommand(new MathObjectsCommand(objects) {

            @Override
            public void initialize() {
                for (MathObject obj : mathObjects) {
                    obj.saveState();
                }
            }

            @Override
            public void execute(double t) {
                for (MathObject obj : mathObjects) {
                    obj.restoreState();
                    obj.multDrawAlpha(1 - t);
                    obj.multFillAlpha(1 - t);
                }
            }

            @Override
            public void finish() {
                execute(0);
                JMathAnimConfig.getConfig().getScene().remove(mathObjects);
            }
        }, runtime);
    }//End of fadeOut command
}
