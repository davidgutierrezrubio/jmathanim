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

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Styling.JMColor;
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.Styling.PaintStyle;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Layouts.GroupLayout;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MathObjectGroup;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.scene.shape.StrokeLineCap;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Commands {

    /**
     * A shift animation. Animates the objects moving them with the given
     * vector.
     *
     * @param runtime
     * @param dx
     * @param dy
     * @param objects
     * @return
     */
    public static ShiftAnimation shift(double runtime, double dx, double dy, MathObject... objects) {
        return shift(runtime, new Vec(dx, dy), objects);
    }

    public static ShiftAnimation shift(double runtime, Vec sv, MathObject... objects) {
        return new ShiftAnimation(runtime, objects) {
            @Override
            public void initialize(JMathAnimScene scene) {
                super.initialize(scene);
                for (MathObject obj : objects) {
                    setShiftVector(obj, sv);
                }
            }

        };
//
//        return new Animation(runtime) {
//            MathObject[] mathObjects = objects;
//            Vec shiftVector = sv;
//
//            @Override
//            public void initialize(JMathAnimScene scene) {
//                super.initialize(scene);
//                saveStates(mathObjects);
//                addObjectsToscene(mathObjects);
//            }
//
//            @Override
//            public void doAnim(double t) {
//                double lt = getLambda().applyAsDouble(t);
//                restoreStates(mathObjects);
//                for (MathObject obj : mathObjects) {
//                    obj.shift(shiftVector.mult(lt));
//                }
//            }
//
//            @Override
//            public void finishAnimation() {
//                super.finishAnimation();
//                doAnim(1);
//            }
//        };
    }

    public static Animation highlight(double runtime, MathObject... objects) {
        return highlight(runtime, 1.5, objects);
    }

    public static Animation highlight(double runtime, double scale, MathObject... objects) {
        AnimationGroup ag = new AnimationGroup();
        Point center = MathObjectGroup.make(objects).getCenter();
        for (MathObject obj : objects) {
            ag.add(Commands.scale(runtime, center, scale, obj).setLambda((x) -> 4 * x * (1 - x)));
        }
        return ag;
    }

    public static Animation scale(double runtime, double sc, MathObject... objects) {
        AnimationGroup ag = new AnimationGroup();
        Point center = MathObjectGroup.make(objects).getCenter();
        for (MathObject obj : objects) {
            ag.add(Commands.scale(runtime, center, sc, obj));
        }
        return ag;
    }

    public static Animation scale(double runtime, Point c, double sc, MathObject... objects) {
        return scale(runtime, c, sc, sc, sc, objects);
    }

    public static Animation scale(double runtime, Point c, double scx, double scy, double scz, MathObject... objects) {
        return new Animation(runtime) {
            double scalex = scx;
            double scaley = scy;
            double scalez = scz;
            Point scaleCenter = c;
            MathObject[] mathObjects = objects;

            @Override
            public void initialize(JMathAnimScene scene) {
                super.initialize(scene);
                saveStates(mathObjects);
                addObjectsToscene(mathObjects);
            }

            @Override
            public void doAnim(double t) {
                double lt = lambda.applyAsDouble(t);
                restoreStates(mathObjects);
                for (MathObject obj : mathObjects) {
                    double scax = 1 - lt + scalex * lt;
                    double scay = 1 - lt + scaley * lt;
                    double scaz = 1 - lt + scalez * lt;
                    if (scaleCenter != null) {
                        obj.scale(scaleCenter, scax, scay, scaz);
                    } else {
                        obj.scale(obj.getCenter(), scax, scay, scaz);
                    }
                }
            }

            @Override
            public void finishAnimation() {
                super.finishAnimation();
                doAnim(1);
            }
        };
    }

    public static Animation rotate(double runtime, double ang, MathObject... objects) {
        return rotate(runtime, null, ang, objects);

    }

    public static Animation rotate(double runtime, Point c, double ang, MathObject... objects) {
        return new Animation(runtime) {
            double angle = ang;
            Point rotationCenter = null;
            MathObject[] mathObjects = objects;

            @Override
            public void initialize(JMathAnimScene scene) {
                super.initialize(scene);
                saveStates(mathObjects);
                if (c != null) {
                    rotationCenter = c;
                }
                addObjectsToscene(mathObjects);
            }

            @Override
            public void doAnim(double t) {
                double lt = lambda.applyAsDouble(t);
                restoreStates(mathObjects);
                for (MathObject obj : mathObjects) {
                    if (rotationCenter == null) {
                        obj.rotate(obj.getCenter(), angle * lt);
                    } else {
                        obj.rotate(rotationCenter, angle * lt);
                    }
                }
            }

            @Override
            public void finishAnimation() {
                super.finishAnimation();
                doAnim(1);
            }
        };
    }// End of rotate command

    public static AnimationWithEffects affineTransform(double runtime, Point a, Point b, Point c, Point d, Point e,
            Point f, MathObject... objects) {
        return new AnimationWithEffects(runtime) {
            Point orig1 = a.copy();
            Point orig2 = b.copy();
            Point orig3 = c.copy();
            Point dst1 = d.copy();
            Point dst2 = e.copy();
            Point dst3 = f.copy();
            MathObject[] mathObjects = objects;
            AffineJTransform tr;

            @Override
            public void initialize(JMathAnimScene scene) {
                super.initialize(scene);
                saveStates(mathObjects);
                addObjectsToscene(mathObjects);
                for (MathObject obj : mathObjects) {
                    tr = AffineJTransform.createAffineTransformation(orig1, orig2, orig3, dst1, dst2, dst3, 1);
                    Point center = obj.getCenter();
                    prepareJumpPath(center, tr.getTransformedObject(center), obj);
                }
            }

            @Override
            public void doAnim(double t) {
                restoreStates(mathObjects);
                double lt = lambda.applyAsDouble(t);
                for (MathObject obj : mathObjects) {
                    tr = AffineJTransform.createAffineTransformation(orig1, orig2, orig3, dst1, dst2, dst3, lt);
                    tr.applyTransform(obj);
                    applyAnimationEffects(lt, obj);
                }
            }

            @Override
            public void finishAnimation() {
                super.finishAnimation();
                doAnim(1);
            }
        };
    }// End of affineTransform command

    /**
     * Animation command that transforms a MathObject through an homothecy.
     * Homothecy is specified by 2 pairs of points (origin-destiny)
     *
     * @param runtime Run time (in seconds)
     * @param a First origin point
     * @param b Second origin point
     * @param c First destiny point
     * @param d Second destiny point
     * @param objects Objects to animate (varargs)
     * @return Animation to run playAnimation method method
     */
    public static AnimationWithEffects homothecy(double runtime, Point a, Point b, Point c, Point d,
            MathObject... objects) {
        return new AnimationWithEffects(runtime) {
            Point A = a.copy();
            Point B = b.copy();
            Point C = c.copy();
            Point D = d.copy();
            AffineJTransform tr;
            MathObject[] mathObjects = objects;

            @Override
            public void initialize(JMathAnimScene scene) {
                super.initialize(scene);
                saveStates(mathObjects);
                addObjectsToscene(mathObjects);
                tr = AffineJTransform.createDirect2DHomothecy(A, B, C, D, 1);
                for (MathObject obj : mathObjects) {
                    Point center = obj.getCenter();
                    prepareJumpPath(center, tr.getTransformedObject(center), obj);
                }
            }

            @Override
            public void doAnim(double t) {
                double lt = getLambda().applyAsDouble(t);
                restoreStates(mathObjects);
                tr = AffineJTransform.createDirect2DHomothecy(A, B, C, D, lt);
                for (MathObject obj : mathObjects) {
                    tr.applyTransform(obj);
                    applyAnimationEffects(lt, obj);
                }
            }

            @Override
            public void finishAnimation() {
                super.finishAnimation();
                doAnim(1);
            }
        };
    }// End of homothecy command

    /**
     * Animation command that perfoms a reflection that maps A into B
     *
     * @param runtime Duration in seconds
     * @param A Origin point
     * @param B Destiny point
     * @param objects Objects to animate (varargs)
     * @return Animation to run with      {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...)
	 *         playAnimation} method
     */
    public static AnimationWithEffects reflection(double runtime, Point A, Point B, MathObject... objects) {
        return new AnimationWithEffects(runtime) {
            MathObject[] mathObjects = objects;
            Point axis1 = A.copy();
            Point axis2 = B.copy();
            AffineJTransform tr;

            @Override
            public void initialize(JMathAnimScene scene) {
                super.initialize(scene);
                saveStates(mathObjects);
                addObjectsToscene(mathObjects);
                for (MathObject obj : mathObjects) {
                    tr = AffineJTransform.createReflection(axis1, axis2, 1);
                    Point center = obj.getCenter();
                    prepareJumpPath(center, tr.getTransformedObject(center), obj);
                }
            }

            @Override
            public void doAnim(double t) {
                double lt = getLambda().applyAsDouble(t);
                restoreStates(mathObjects);
                for (MathObject obj : mathObjects) {
                    tr = AffineJTransform.createReflection(axis1, axis2, lt);
                    tr.applyTransform(obj);
                    applyAnimationEffects(lt, obj);
                }
            }

            @Override
            public void finishAnimation() {
                super.finishAnimation();
                doAnim(1);
            }
        };
    }// End of reflectionByAxis command

    /**
     * Animation command that perfoms a reflection specified by 2 points
     *
     * @param runtime Duration in seconds
     * @param a first axis point
     * @param b second axis point
     *
     * @param objects Objects to animate (varargs)
     * @return Animation to run with      {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...)
	 *         playAnimation} method
     */
    public static AnimationWithEffects reflectionByAxis(double runtime, Point a, Point b, MathObject... objects) {
        return new AnimationWithEffects(runtime) {
            MathObject[] mathObjects = objects;
            Point axisPoint1 = a.copy();
            Point axisPoint2 = b.copy();
            AffineJTransform tr;

            @Override
            public void initialize(JMathAnimScene scene) {
                super.initialize(scene);
                saveStates(mathObjects);
                addObjectsToscene(mathObjects);
                for (MathObject obj : mathObjects) {
                    tr = AffineJTransform.createReflectionByAxis(axisPoint1, axisPoint2, 1);
                    Point center = obj.getCenter();
                    prepareJumpPath(center, tr.getTransformedObject(center), obj);
                }
            }

            @Override
            public void doAnim(double t) {
                double lt = getLambda().applyAsDouble(t);
                restoreStates(mathObjects);
                for (MathObject obj : mathObjects) {
                    tr = AffineJTransform.createReflectionByAxis(axisPoint1, axisPoint2, lt);
                    tr.applyTransform(obj);
                    applyAnimationEffects(lt, obj);
                }
            }

            @Override
            public void finishAnimation() {
                super.finishAnimation();
                doAnim(1);
            }
        };
    }// End of reflectionByAxis command

    /**
     * Changes the draw color and fill color of the objects to the given one. If
     * one of the colors is null, the colors are not changed Thus, if you want
     * to change only drawColor, you should set fillColor to null.
     *
     * @param runtime Duration in seconds
     * @param drawColor Color to be the drawColor
     * @param fillColor Color to be the fillColor
     * @param objects MathObjects to animate (varargs)
     * @return The animation to be played with the playAnimation method
     */
    public static AnimationGroup setColor(double runtime, PaintStyle drawColor, PaintStyle fillColor, MathObject... objects) {
        AnimationGroup ag = new AnimationGroup();
        for (MathObject ob : objects) {
            MODrawProperties mpDst = MODrawProperties.makeNullValues();
            if (drawColor != null) {
                mpDst.setDrawColor(drawColor);
            }
            if (fillColor != null) {
                mpDst.setFillColor(fillColor);
            }
            Animation cmd = setMP(runtime, mpDst, ob);
            ag.add(cmd);
        }
        return ag;
    }

    /**
     * Animation command that changes the math drawing properties of given
     * object, interpolating
     *
     * @param runtime Time duration in seconds
     * @param mp Destination {@link MODrawProperties}
     * @param objects Objects to animate (varargs)
     * @return Animation to run with      {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...)
	 *         playAnimation} method
     */
    public static Animation setMP(double runtime, MODrawProperties mp, MathObject... objects) {
        return new Animation(runtime) {
            MathObject[] mathObjects = objects;
            MODrawProperties mpDst = mp;

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
                    obj.getMp().interpolateFrom(mpDst, lt);
                }
            }

            @Override
            public void finishAnimation() {
                super.finishAnimation();
                doAnim(1);
            }
        };
    }// End of setMP command

    /**
     * Animation command that changes the style of given object, interpolating
     *
     * @param runtime Time duration in seconds
     * @param styleName Name of destination style
     * @param objects Objects to animate (varargs)
     *
     * @return Animation to run with      {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...)
	 *         playAnimation} method
     */
    public static Animation setStyle(double runtime, String styleName, MathObject... objects) {
        return setMP(runtime, MODrawProperties.createFromStyle(styleName), objects);
    }

    /**
     * Animation command that zooms the camera to a given area specified by a
     * {@link Rect}
     *
     * @param runtime Time duration in seconds
     * @param camera Camera to zoom
     * @param rectToZoom Area to zoom
     * @return Animation to run with      {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...)
	 *         playAnimation} method
     */
    public static Animation cameraZoomToRect(double runtime, Camera camera, Rect rectToZoom) {
        return new Animation(runtime) {
            Camera cam = camera;
            Rect rDst = cam.getRectThatContains(rectToZoom);
            Rect rSource;

            @Override
            public void initialize(JMathAnimScene scene) {
                super.initialize(scene);
                rSource = cam.getMathView();
            }

            @Override
            public void doAnim(double t) {
                double lt = getLambda().applyAsDouble(t);
                Rect r = rSource.interpolate(rDst, lt);
                cam.setMathView(r);
            }

            @Override
            public void finishAnimation() {
                super.finishAnimation();
                doAnim(1);
            }
        };
    }

    /**
     * Animation that pans the {@link Camera} by a given vector
     *
     * @param runtime Time duration in seconds
     * @param camera Camera to pan
     * @param shiftVector Shift vector
     *
     * @return Animation to run with      {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...)
	 *         playAnimation} method
     */
    public static Animation cameraShift(double runtime, Camera camera, Vec shiftVector) {
        Rect r = camera.getMathView().shifted(shiftVector);
        return cameraZoomToRect(runtime, camera, r);

    }

    public static Animation cameraScale(double runtime, Camera cam, double scale) {
        return Commands.cameraZoomToRect(runtime, cam, cam.getMathView().scale(scale, scale));
    }

    /**
     * Animation command that reduces the size and alpha of the MathObject.After
     * finishing the animation, object is removed from the current scene.
     *
     * @param runtime Run time (in seconds)
     * @param objects Objects to animate (varargs)
     * @return Animation to run with      {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...)
	 *         playAnimation} method
     */
    public static Animation shrinkOut(double runtime, MathObject... objects) {
        return shrinkOut(runtime, 0, objects);
    }

    /**
     * Animation command that reduces the size and alpha of the MathObject.A
     * rotation of a given angle is performed meanwhile.After finishing the
     * animation, object is removed from the current scene.
     *
     * @param angle Angle to rotate, in radians
     * @param runtime Duration time in seconds
     * @param objects Objects to animate (varargs)
     * @return Animation to run with      {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...)
	 *         playAnimation} method
     */
    public static Animation shrinkOut(double runtime, double angle, MathObject... objects) {
        Animation anim = new Animation(runtime) {
            MathObject[] mathObjects = objects;

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
                    obj.scale(1 - lt);
                    obj.multDrawAlpha(1 - lt);
                    obj.multFillAlpha(1 - lt);
                    obj.rotate(lt * angle);
                }
            }

            @Override
            public void finishAnimation() {
                super.finishAnimation();
                doAnim(1);
            }
        };
        anim.setLambda(t -> t);// Default
        return anim;
    }// End of shrinkOut command

    /**
     * Performs the inverse animation than {@link shrinkOut}, that its, scale
     * the size and alpha of the object from zero.
     *
     *
     * @param runtime Duration time in seconds
     * @return Animation to run with
     * @param objects Objects to animate (varargs)      {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...)
	 *                playAnimation} method
     */
    public static Animation growIn(double runtime, MathObject... objects) {
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
     * @return Animation to run with      {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...)
	 *         playAnimation} method
     */
    public static Animation growIn(double runtime, double angle, MathObject... objects) {
        Animation anim = new Animation(runtime) {
            MathObject[] mathObjects = objects;

            @Override
            public void initialize(JMathAnimScene scene) {
                super.initialize(scene);
                saveStates(mathObjects);
                addObjectsToscene(mathObjects);
                for (MathObject obj : mathObjects) {
                    obj.visible(false);
                }
            }

            @Override
            public void doAnim(double t) {
                double lt=getLambda().applyAsDouble(t);
                restoreStates(mathObjects);
                for (MathObject obj : mathObjects) {
                    obj.scale(lt);
                    obj.multDrawAlpha(lt);
                    obj.multFillAlpha(lt);
                    obj.rotate((1 - lt) * angle);
                }
            }

            @Override
            public void finishAnimation() {
                super.finishAnimation();
                doAnim(1);
            }
        };
        anim.setLambda(t -> t);// Default value
        return anim;
    }// End of growIn command

    /**
     * Performs an animation modifying the alpha of the object from 0 to the
     * original alpha of object. Both drawAlpha and fillAlpha are animated.
     *
     * @param runtime Duration time in seconds
     * @param objects Objects to animate (varargs)
     * @return Animation to run with      {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...)
	 *         playAnimation} method
     */
    public static Animation fadeIn(double runtime, MathObject... objects) {
        Animation anim = new Animation(runtime) {
            MathObject[] mathObjects = objects;

            @Override
            public void initialize(JMathAnimScene scene) {
                super.initialize(scene);
                this.mathObjects = objects;
                saveStates(mathObjects);
                addObjectsToscene(mathObjects);
                for (MathObject obj : mathObjects) {
                    obj.visible(false);
                }
            }

            @Override
            public void doAnim(double t) {
                double lt=getLambda().applyAsDouble(t);
                restoreStates(mathObjects);
                for (MathObject obj : mathObjects) {
                    obj.multDrawAlpha(lt);
                    obj.multFillAlpha(lt);
                }
            }

            @Override
            public void finishAnimation() {
                super.finishAnimation();
                doAnim(1);
            }
        };
        anim.setLambda(t -> t);// Default value
        return anim;
    }// End of fadeIn command

    /**
     * Performs an animation modifying the alpha of the object to 0. Both
     * drawAlpha and fillAlpha are animated. Object is removed from current
     * scene after finishing animation.
     *
     * @param runtime Duration time in seconds
     * @param objects Object to animate
     * @return Animation to run with      {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...)
	 *         playAnimation} method
     */
    public static Animation fadeOut(double runtime, MathObject... objects) {
        Animation anim = new Animation(runtime) {
            MathObject[] mathObjects = objects;

            @Override
            public void initialize(JMathAnimScene sc) {
                super.initialize(sc);
                saveStates(mathObjects);
                sc.add(mathObjects);
            }

            @Override
            public void doAnim(double t) {
                double lt=getLambda().applyAsDouble(t);
                restoreStates(mathObjects);
                for (MathObject obj : mathObjects) {
                    obj.multDrawAlpha(1 - lt);
                    obj.multFillAlpha(1 - lt);
                }
            }

            @Override
            public void finishAnimation() {
                super.finishAnimation();
                restoreStates(mathObjects);// Restore original alphas in case of reutilization
                removeObjectsToscene(mathObjects);
            }
        };
        anim.setLambda(t -> t);// Default value
        return anim;
    }// End of fadeOut command

    /**
     * Animated version of method setLayout for MathObjectGroup instances
     *
     * @param runtime Duration in seconds
     * @param corner Corner to layout from. If null, first object of the group
     * will be used
     * @param layout Type of anchor to apply layout as defined in the enum
     * Anchor.Type
     * @param gap Gap to apply between elements, in math units
     * @param group MathObjectGroup instance to apply the layout
     * @return Animation to run with      {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...)
	 *         playAnimation} method
     */
    public static ShiftAnimation setLayout(double runtime, MathObject corner, MathObjectGroup.Layout layout, double gap,
            MathObjectGroup group) {
        group.saveState();
        group.setLayout(corner, layout, gap);
        HashMap<MathObject, Point> centers = new HashMap<>();
        int n = 0;
        for (MathObject ob : group) {
            centers.put(ob, ob.getCenter());// The destination centers of the objects of the group
            n++;
        }
        group.restoreState();
        MathObject[] mathobjects = group.getObjects().toArray(new MathObject[group.size()]);

        return new ShiftAnimation(runtime, mathobjects) {
            @Override
            public void initialize(JMathAnimScene scene) {
                super.initialize(scene);
                JMathAnimScene.logger.debug("Initialized setLayout animation");
                for (MathObject obj : mathobjects) {
                    Point dst = centers.get(obj);
                    setShiftVector(obj, obj.getCenter().to(dst));
                }
            }

        };
    }

    /**
     * Animated version of method setLayout for MathObjectGroup instances
     *
     * @param runtime Duration in seconds
     * @param layout A GroupLayout subclass
     * @param group MathObjectGroup instance to apply the layout
     * @return Animation to run with      {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...)
	 *         playAnimation} method
     */
    public static ShiftAnimation setLayout(double runtime, GroupLayout layout, MathObjectGroup group) {
        group.saveState();// TODO: Jump effect doesn't work yet
        group.setLayout(layout);
        HashMap<MathObject, Point> centers = new HashMap<>();
        int n = 0;
        for (MathObject ob : group) {
            centers.put(ob, ob.getCenter());// The destination centers of the objects of the group
            n++;
        }
        group.restoreState();
        MathObject[] mathobjects = group.getObjects().toArray(new MathObject[group.size()]);

        return new ShiftAnimation(runtime, mathobjects) {
            @Override
            public void initialize(JMathAnimScene scene) {
                super.initialize(scene);
                JMathAnimScene.logger.debug("Initialized setLayout animation");
                for (MathObject obj : mathobjects) {
                    Point dst = centers.get(obj);
                    setShiftVector(obj, obj.getCenter().to(dst));
                }
            }
        };
    }

    /**
     * Animates a change in the alpha fill of the objects. The precise change is
     * given by the lambda function used in the animation
     *
     * @param runTime Duration in seconds
     * @param objects MathObjects to apply the animation (varargs)
     * @return Animation to run with      {@link JMathAnimScene#playAnimation(com.jmathanim.Animations.Animation...)
	 *         playAnimation} method
     */
    public static Animation changeFillAlpha(double runTime, MathObject... objects) {
        return new Animation(runTime) {
            MathObject[] mathObjects = objects;
            ArrayList<Double> alphaOrig = new ArrayList<>();

            @Override
            public void initialize(JMathAnimScene scene) {
                super.initialize(scene);
                JMathAnimScene.logger.debug("Initialized changeFillAlpha animation");
                for (MathObject obj : objects) {
                    alphaOrig.add(obj.getMp().getFillColor().getAlpha());
                }
                addObjectsToscene(mathObjects);
            }

            @Override
            public void doAnim(double t) {
                int n = 0;
                for (MathObject obj : objects) {
                    double a = alphaOrig.get(n) * lambda.applyAsDouble(t);
                    obj.getMp().setFillAlpha((float) a);
                }
            }

            @Override
            public void finishAnimation() {
                super.finishAnimation();
                doAnim(1);
            }
        };
    }

    /**
     * Performs an exit animation of an object(s). When finished, removes the
     * objects from the scene.
     *
     * @param runtime Duration in seconds
     * @param exitAnchor Exit direction. A vaue of Anchor.Type
     * @param mathObjects Objects to exit (varargs)
     * @return this animation, ready to play with the playAnimation method
     */
    public static ShiftAnimation moveOut(double runtime, Anchor.Type exitAnchor, MathObject... mathObjects) {
        ShiftAnimation resul = new ShiftAnimation(runtime, mathObjects) {
            @Override
            public void initialize(JMathAnimScene scene) {
                super.initialize(scene);
                JMathAnimScene.logger.debug("Initialized moveOut animation");
                // Compute appropiate shift vectors
                Rect r = JMathAnimConfig.getConfig().getCamera().getMathView();
                for (int n = 0; n < mathObjects.length; n++) {
                    MathObject obj = mathObjects[n];
                    Point p = Anchor.getAnchorPoint(obj, Anchor.reverseAnchorPoint(exitAnchor));
                    Point q = Anchor.getAnchorPoint(Shape.rectangle(r), exitAnchor, 1);
                    switch (exitAnchor) {
                        case LEFT:
                            q.v.y = p.v.y;
                        case RIGHT:
                            q.v.y = p.v.y;
                            break;
                        case UPPER:
                        case LOWER:
                            q.v.x = p.v.x;
                            break;
                    }
                    this.setShiftVector(obj, p.to(q));
                }
            }

            public void finishAnimation() {
                super.finishAnimation();
                for (MathObject obj : mathObjects) {
                    removeObjectsToscene(obj);
                }
            }

        };

        return resul;
    }

    /**
     * Performs an enter animation of an object(s).The object(s) are added
     * automatically to the scene.
     *
     * @param runtime Duration in seconds
     * @param enterAnchor Enter direction. A vaue of Anchor.Type
     * @param mathObjects Objects to enter (varargs)
     * @return this animation, ready to play with the playAnimation method
     */
    public static ShiftAnimation moveIn(double runtime, Anchor.Type enterAnchor, MathObject... mathObjects) {

        // Compute appropiate shift vectors
        Rect r = JMathAnimConfig.getConfig().getCamera().getMathView();

        ShiftAnimation resul = new ShiftAnimation(runtime, mathObjects) {
            @Override
            public void initialize(JMathAnimScene scene) {
                super.initialize(scene);
                JMathAnimScene.logger.debug("Initialized moveIn animation");
                for (int n = 0; n < mathObjects.length; n++) {
                    MathObject obj = mathObjects[n];
                    final Anchor.Type reverseAnchor = Anchor.reverseAnchorPoint(enterAnchor);
                    Point p = Anchor.getAnchorPoint(obj, reverseAnchor);
                    Point q = Anchor.getAnchorPoint(Shape.rectangle(r), enterAnchor);
                    switch (enterAnchor) {
                        case LEFT:
                            q.v.y = p.v.y;
                        case RIGHT:
                            q.v.y = p.v.y;
                            break;
                        case UPPER:
                        case LOWER:
                            q.v.x = p.v.x;
                            break;
                    }
                    obj.shift(p.to(q));
                    this.setShiftVector(obj, q.to(p));
                }
                saveStates(mathObjects);

            }

        };
        return resul;
    }

    /**
     * Transforms one object to another, animating a flip effect
     *
     * @param runtime Duration in seconds
     * @param ob1 Origin object
     * @param ob2 Destiny object
     * @param horizontal If true, an horizontal flipping is done, vertical
     * otherwise
     * @return The animation
     */
    public static FlipTransform flipTransform(double runtime, boolean horizontal, MathObject ob1, MathObject ob2) {
        return new FlipTransform(runtime,
                (horizontal ? FlipTransform.FlipType.HORIZONTAL : FlipTransform.FlipType.VERTICAL), ob1, ob2);
    }

    /**
     * Animated version of the align method
     *
     * @param runtime time in seconds
     * @param dst Destiny object to align with
     * @param type Type of align, a value of MathObject.Align enum
     * @param mathobjects Mathobjects to animate
     * @return The created animation
     */
    public static ShiftAnimation align(double runtime, MathObject dst, MathObject.Align type,
            MathObject... mathobjects) {
        ShiftAnimation resul = new ShiftAnimation(runtime, mathobjects) {
            @Override
            public void initialize(JMathAnimScene scene) {
                super.initialize(scene);
                JMathAnimScene.logger.debug("Initialized align animation");
                for (MathObject obj : mathobjects) {
                    Point dstCenter = Shape.rectangle(obj.getBoundingBox()).align(dst, type).getCenter();
                    setShiftVector(obj, obj.getCenter().to(dstCenter));
                }
            }

        };
        return resul;
    }

    /**
     * Animated version of the stackTo method.The destination point is computed
     * at the initialize() method so it cab ne safely concatenated. If several
     * objects are animated, the second will be stacked to the first, and so on
     *
     * @param runtime time in seconds
     * @param dst Destiny object to align with
     * @param type Type of stack, a value of Anchor.Type enum
     * @param gap Gap between the stacked objects
     * @param mathobjects Mathobjects to animate
     * @return The created animation
     */
    public static ShiftAnimation stackTo(double runtime, MathObject dst, Anchor.Type type, double gap,
            MathObject... mathobjects) {
        return new ShiftAnimation(runtime, mathobjects) {
            @Override
            public void initialize(JMathAnimScene scene) {
                MathObject previous = dst;

                super.initialize(scene);
                for (MathObject obj : mathobjects) {
                    MathObject objc = Shape.rectangle(obj.getBoundingBox()).stackTo(previous, type, gap);
                    setShiftVector(obj, obj.getCenter().to(objc.getCenter()));
                    previous = objc;
                }
            }
        };
    }

    /**
     * Animates a crossout. The size of the crossout is computed from the
     * bounding box of the crossed object
     *
     * @param runtime Duration in seconds
     * @param obj Object to cross out
     * @return The created animation
     */
    public static Animation crossOut(double runtime, MathObject obj) {
        Rect bbox = obj.getBoundingBox();
        Shape s1 = Shape.segment(bbox.getUL(), bbox.getDR()).scale(.75).linecap(StrokeLineCap.BUTT)
                .drawColor(JMColor.RED).layer(Integer.MAX_VALUE);
        Shape s2 = Shape.segment(bbox.getUR(), bbox.getDL()).scale(.75).linecap(StrokeLineCap.BUTT)
                .drawColor(JMColor.RED).layer(Integer.MAX_VALUE);
        double longi = .25 * s1.getPoint(0).to(s1.getPoint(1)).norm();
        double width = JMathAnimConfig.getConfig().getRenderer().getThicknessForMathWidth(longi);
        s1.thickness(width).getMp().setAbsoluteThickness(false);
        s2.thickness(width).getMp().setAbsoluteThickness(false);
        return new Concatenate(new ShowCreation(.5 * runtime, s1), new ShowCreation(.5 * runtime, s2));
    }
}
