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

import com.jmathanim.Animations.Strategies.Transform.FlipTransform;
import com.jmathanim.Cameras.Camera;
import com.jmathanim.Cameras.Camera3D;
import com.jmathanim.Constructible.Constructible;
import com.jmathanim.Enum.AnchorType;
import com.jmathanim.Enum.LayoutType;
import com.jmathanim.Enum.ScreenAnchor;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Styling.DrawStyleProperties;
import com.jmathanim.Styling.JMColor;
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.Styling.PaintStyle;
import com.jmathanim.Utils.*;
import com.jmathanim.Utils.Layouts.GroupLayout;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.*;
import javafx.scene.shape.StrokeLineCap;

import java.util.*;

import static com.jmathanim.jmathanim.JMathAnimScene.DEGREES;
import static com.jmathanim.jmathanim.JMathAnimScene.PI;

/**
 * A convenience class that stores most common animations in static methods
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Commands {

    /**
     * A shift animation. Animates the objects moving them with the given vector.
     *
     * @param runtime
     * @param dx
     * @param dy
     * @param objects
     * @return
     */
    public static ShiftAnimation shift(double runtime, double dx, double dy, AffineTransformable<?>... objects) {
        return shift(runtime, new Vec(dx, dy), objects);
    }

    public static ShiftAnimation shift(double runtime, Vec sv, AffineTransformable<?>... objects) {
        ShiftAnimation resul = new ShiftAnimation(runtime, objects) {
            @Override
            public boolean doInitialization() {
                super.doInitialization();
                for (AffineTransformable obj : objects) {
                    setShiftVector(obj, sv);
                }
                return true;
            }

        };
        resul.setDebugName("Shift");
        return resul;
    }

    public static ShiftAnimation moveTo(double runtime, Coordinates<?> destiny, AffineTransformable<?>... objects) {
        ShiftAnimation resul = new ShiftAnimation(runtime, objects) {
            @Override
            public boolean doInitialization() {
                super.doInitialization();
                for (AffineTransformable<?> obj : objects) {
                    setShiftVector(obj, obj.getCenter().to(destiny));
                }
                return true;
            }
        };
        resul.setDebugName("MoveTo");
        return resul;
    }

    /**
     * Creates an animation that scales back and forth the specified objets up to 50%. The center of the scale will be
     * the same for all objects, and equals to the center of combined bounding box.
     *
     * @param runtime Duration in seconds
     * @param objects Objects to highlight
     * @return The animation, ready to play with the playAnim method
     */
    public static Animation highlight(double runtime, MathObject<?>... objects) {
        return highlight(runtime, 1.5, objects);
    }

    /**
     * Creates an animation that scales back and forth the specified objets up to given scale.
     *
     * @param runtime     Duration in seconds
     * @param scale       The factor to scale. A value of 1.5 will scale the objecs up to 50% more.
     * @param mathObjects Objects to highlight
     * @return The animation, ready to play with the playAnim method
     */
    public static Animation highlight(double runtime, double scale, MathObject<?>... mathObjects) {

        Runnable initHook = () -> {
            for (MathObject obj : mathObjects) {
                if (obj instanceof Constructible) {
                    Constructible cnstr = (Constructible) obj;
                    cnstr.setFreeMathObject(true);
                }
            }
        };

        Runnable finishHook = () -> {
            for (MathObject obj : mathObjects) {
                if (obj instanceof Constructible) {
                    Constructible cnstr = (Constructible) obj;
                    cnstr.setFreeMathObject(false);
                }
            }
        };

        AnimationGroup anim = AnimationGroup.make();
        for (MathObject mathObject : mathObjects) {
            anim.add(Commands.scale(runtime, scale, mathObject));
        }
//        Animation anim = Commands.scale(runtime, center, scale, group);
        anim.initRunnable = initHook;
        anim.finishRunnable = finishHook;
        anim.setLambda(UsefulLambdas.backAndForth());

        anim.setDebugName("Highlight");
        return anim;
    }

    /**
     * Similar to the highlight animation, but adds a little twist of 15 degrees to the objects. The factor scale
     * applied is 1.5.
     *
     * @param runtime Duration in seconds positive and negative direction
     * @param objects Objects to animate
     * @return The animation, ready to play with the playAnim method
     */
    public static AnimationGroup twistAndScale(double runtime, MathObject<?>... objects) {
        return twistAndScale(runtime, 1.5, 15 * DEGREES, objects);
    }

    /**
     * Similar to the highlight animation, but adds a little twist to the objects
     *
     * @param runtime     Duration in seconds
     * @param scale       The factor to scale. A value of 1.5 will scale the objecs up to 50% more.
     * @param twistAngle  Max angle of rotation. The twist will perform to positive and negative direction
     * @param mathObjects Objects to animate
     * @return The animation, ready to play with the playAnim method
     */
    public static AnimationGroup twistAndScale(double runtime, double scale, double twistAngle, MathObject<?>... mathObjects) {
        HashMap<Constructible, Boolean> constructiblesFreeStatus = new HashMap<Constructible, Boolean>();
        Runnable initHook = () -> {
            for (MathObject obj : mathObjects) {
                if (obj instanceof Constructible) {
                    Constructible cnstr = (Constructible) obj;
                    constructiblesFreeStatus.put(cnstr, cnstr.isFreeMathObject());
                    cnstr.setFreeMathObject(true);
                }
            }
        };

        Runnable finishHook = () -> {
            for (MathObject obj : mathObjects) {
                if (obj instanceof Constructible) {
                    Constructible cnstr = (Constructible) obj;
                    cnstr.setFreeMathObject(constructiblesFreeStatus.get(cnstr));
                }
            }
        };
        AnimationGroup ag = new AnimationGroup();

        for (MathObject<?> obj : mathObjects) {
            Vec center = obj.getCenter();
            Animation rotateAnim = Commands.rotate(runtime, center, twistAngle, obj).setLambda(t -> Math.sin(4 * PI * t));
            ag.add(rotateAnim);
            ag.add(Commands.scale(runtime, center, scale, obj).setLambda(UsefulLambdas.backAndForth()).setUseObjectState(false));
        }
        ag.setDebugName("TwistAndScale");
        ag.initRunnable = initHook;
        ag.finishRunnable = finishHook;
        return ag;
    }

    /**
     * Animates a uniform scale change from centers of objects
     *
     * @param runtime     Duration in seconds
     * @param sc          Scale to apply
     * @param mathObjects Objects to animate
     * @return The animation, ready to play with the playAnim method
     */
    public static Animation scale(double runtime, double sc, MathObject<?>... mathObjects) {
        AnimationGroup ag = new AnimationGroup();
        Vec center = MathObjectGroup.make(mathObjects).getCenter();
        for (MathObject obj : mathObjects) {
            ag.add(Commands.scale(runtime, center, sc, obj));
        }
        return ag;
    }

    /**
     * Animates a scale change from centers of objects
     *
     * @param runtime        Duration in seconds
     * @param scx            X scale to apply
     * @param scy            Y scale to apply
     * @param objectsToScale Objects to animate
     * @return The animation, ready to play with the playAnim method
     */
    public static Animation scale(double runtime, double scx, double scy, AffineTransformable<?>... objectsToScale) {
        AnimationGroup ag = new AnimationGroup();
        //Compute combined center
        Rect r = objectsToScale[0].getBoundingBox();
        for (AffineTransformable<?> obj : objectsToScale) {
            r = Rect.union(r, obj.getBoundingBox());
        }
        Vec center = r.getCenter();
        for (AffineTransformable<?> obj : objectsToScale) {
            ag.add(Commands.scale(runtime, center, scx, scy, 1, obj));
        }
        return ag;
    }

    public static Animation scale(double runtime, Coordinates c, double sc, AffineTransformable<?>... objects) {
        return scale(runtime, c, sc, sc, sc, objects);
    }

    public static Animation scale(double runtime, Coordinates c, double scx, double scy, double scz, AffineTransformable<?>... objects) {
        Animation resul = new Animation(runtime) {
            final double scalex = scx;
            final double scaley = scy;
            final double scalez = scz;
            final Coordinates scaleCenter = c;
            final AffineTransformable<?>[] mathObjects = objects;

            @Override
            public boolean doInitialization() {
                super.doInitialization();
                saveStates(mathObjects);
                return true;
            }

            @Override
            public MathObjectGroup getIntermediateObject() {
                return MathObjectGroup.make(filterMathObjects(mathObjects));
            }

            @Override
            public void doAnim(double t) {
                super.doAnim(t);
                double lt = getLT(t);
                restoreStates(mathObjects);
                for (AffineTransformable<?> obj : mathObjects) {
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
//                doAnim(1);
                super.finishAnimation();
            }

            @Override
            public void cleanAnimationAt(double t) {
            }

            @Override
            public void prepareForAnim(double t) {
                addObjectsToscene(filterMathObjects(mathObjects));
            }
        };
        resul.setDebugName("Scale");
        return resul;
    }

    public static AnimationWithEffects rotate(double runtime, double rotationAngle, AffineTransformable<?>... objects) {
        return rotate(runtime, null, rotationAngle, objects);
    }

    public static AnimationWithEffects rotate(double runtime, double rotationAngle, List<? extends AffineTransformable<?>> objects) {
        AffineTransformable<?>[] arObjects = objects.toArray(new AffineTransformable[0]);
        return rotate(runtime, null, rotationAngle, arObjects);
    }

    public static AnimationWithEffects rotate(double runtime, Coordinates<?> rotationCenter, double rotationAngle, List<? extends AffineTransformable<?>> objects) {
        AffineTransformable<?>[] arObjects = objects.toArray(new AffineTransformable[0]);
        return rotate(runtime, rotationCenter, rotationAngle, arObjects);
    }


    public static AnimationWithEffects rotate(double runtime, Coordinates<?> rotationCenter, double rotationAngle, AffineTransformable<?>... objects) {
        AnimationWithEffects resul = new AnimationWithEffects(runtime) {
            final double angle = rotationAngle;
            final AffineTransformable[] objectsToRotate = objects;
            private boolean[] shouldBeAdded;

            @Override
            public boolean doInitialization() {
                super.doInitialization();
                saveStates(objectsToRotate);
                shouldBeAdded = new boolean[objectsToRotate.length];
                for (int i = 0; i < objectsToRotate.length; i++) {
                    //True if object is NOT added to the scene
                    if (objectsToRotate[i] instanceof MathObject) {
                        MathObject<?> mathObject = (MathObject<?>) objectsToRotate[i];
                        shouldBeAdded[i] = !scene.isInScene(mathObject);

                    }

                }
                return true;
            }

            @Override
            public MathObjectGroup getIntermediateObject() {
                return MathObjectGroup.make(filterMathObjects(objectsToRotate));
            }

            @Override
            public void doAnim(double t) {
                super.doAnim(t);
                double lt = getLT(t);
                restoreStates(objectsToRotate);
                for (AffineTransformable<?> obj : objectsToRotate) {
                    if (rotationCenter == null) {
                        obj.rotate(obj.getCenter(), angle * lt);
                    } else {
                        obj.rotate(rotationCenter, angle * lt);
                    }
                    applyAnimationEffects(lt, obj);
                }
            }

            @Override
            public void finishAnimation() {
//                doAnim(1);
                super.finishAnimation();
            }

            @Override
            public void cleanAnimationAt(double t) {
                double lt = getLT(t);
                if (lt == 0) {
                    for (int i = 0; i < objectsToRotate.length; i++) {
                        //If object initially wasn't in the scene, remove it
                        if (shouldBeAdded[i]) {
                            if (objectsToRotate[i] instanceof MathObject) {
                                MathObject<?> mathObject = (MathObject<?>) objectsToRotate[i];
                                scene.remove(mathObject);
                            }

                        }
                    }
                }
            }

            @Override
            public void prepareForAnim(double t) {
                addObjectsToscene(filterMathObjects(objectsToRotate));
            }
        };
        resul.setDebugName("Rotate");

        return resul;
    }// End of rotate command

    public static Animation rotate3d(double runtime, double angx, double angy, double angz, MathObject<?>... objects) {
        return rotate3d(runtime, null, angx, angy, angz, objects);

    }

    public static Animation rotate3d(double runtime, Coordinates c, double angx, double angy, double angz, MathObject<?>... objects) {
        return new Animation(runtime) {
            final double anglex = angx;
            final double angley = angy;
            final double anglez = angz;
            final MathObject[] mathObjects = objects;
            Vec rotationCenter = null;

            @Override
            public MathObjectGroup getIntermediateObject() {
                return MathObjectGroup.make(mathObjects);
            }

            @Override
            public boolean doInitialization() {
                super.doInitialization();
                saveStates(mathObjects);
                if (c != null) {
                    rotationCenter = c.getVec();
                }
                return true;
            }

            @Override
            public void doAnim(double t) {
                super.doAnim(t);
                double lt = getLT(t);
                restoreStates(mathObjects);
                for (MathObject obj : mathObjects) {
                    if (rotationCenter == null) {
                        obj.rotate3d(obj.getCenter(), anglex * lt, angley * lt, anglez * lt);
                    } else {
                        obj.rotate3d(rotationCenter, anglex * lt, angley * lt, anglez * lt);
                    }
                }
            }

            @Override
            public void finishAnimation() {
                doAnim(t);
                super.finishAnimation();
            }

            @Override
            public void cleanAnimationAt(double t) {
            }

            @Override
            public void prepareForAnim(double t) {
                addObjectsToscene(mathObjects);
            }
        };
    }// End of rotate command

    /**
     * Animates an affine transformation that maps A,B,C into D,E,F
     *
     * @param runtime
     * @param A       First origin point
     * @param B       Second origin point
     * @param C       Third origin point
     * @param D       Image of first origin point
     * @param E       Image of second origin point
     * @param F       Image of third origin point
     * @param objects
     * @return The transform
     */
    public static AnimationWithEffects affineTransform(double runtime, Coordinates A, Coordinates B, Coordinates C, Coordinates D, Coordinates E,
                                                       Coordinates F, MathObject<?>... objects) {
        AnimationWithEffects resul = new AnimationWithEffects(runtime) {
            final MathObject[] mathObjects = objects;
            Vec orig1, orig2, orig3, dst1, dst2, dst3;
            AffineJTransform tr;

            @Override
            public boolean doInitialization() {
                orig1 = A.getVec().copy();
                orig2 = B.getVec().copy();
                orig3 = C.getVec().copy();
                dst1 = D.getVec().copy();
                dst2 = E.getVec().copy();
                dst3 = F.getVec().copy();
                super.doInitialization();
                saveStates(mathObjects);
                for (MathObject obj : mathObjects) {
                    tr = AffineJTransform.createAffineTransformation(orig1, orig2, orig3, dst1, dst2, dst3, 1);
                    Vec center = obj.getCenter();
                    prepareJumpPath(center, center.applyAffineTransform(tr), obj);
                }
                return true;
            }

            @Override
            public MathObjectGroup getIntermediateObject() {
                return MathObjectGroup.make(mathObjects);
            }

            @Override
            public void doAnim(double t) {
                super.doAnim(t);
                restoreStates(mathObjects);
                double lt = getLT(t);
                for (MathObject obj : mathObjects) {
                    tr = AffineJTransform.createAffineTransformation(orig1, orig2, orig3, dst1, dst2, dst3, lt);
                    tr.applyTransform(obj);
                    applyAnimationEffects(lt, obj);
                }
            }

            @Override
            public void finishAnimation() {
                doAnim(t);
                super.finishAnimation();
            }

            @Override
            public void cleanAnimationAt(double t) {
            }

            @Override
            public void prepareForAnim(double t) {
                addObjectsToscene(mathObjects);
            }
        };
        resul.setDebugName("Affine Transform");
        return resul;
    }// End of affineTransform command


    public static AnimationWithEffects isomorphism(double runtime, Rect rOrig, Rect rDst, MathObject<?>... objects) {
        return isomorphism(runtime, rOrig.getUpperLeft(), rOrig.getLowerRight(), rDst.getUpperLeft(), rDst.getLowerRight(), objects);
    }

    /**
     * Animation command that transforms a MathObject through a direct isomorphism. Isomorphism is specified by 2 pairs
     * of points (origin-destiny)
     *
     * @param runtime Run time (in seconds)
     * @param a       First origin point
     * @param b       Second origin point
     * @param c       First destiny point
     * @param d       Second destiny point
     * @param objects Objects to animate (varargs)
     * @return Animation to run playAnimation method method
     */
    public static AnimationWithEffects isomorphism(double runtime, Coordinates<?> a, Coordinates<?> b, Coordinates<?> c, Coordinates<?> d,
                                                   MathObject<?>... objects) {
        AnimationWithEffects resul = new AnimationWithEffects(runtime) {
            final MathObject<?>[] mathObjects = objects;
            AffineJTransform tr;
            Vec A, B, C, D;

            @Override
            public boolean doInitialization() {
                A = a.getVec().copy();
                B = b.getVec().copy();
                C = c.getVec().copy();
                D = d.getVec().copy();
                super.doInitialization();
                saveStates(mathObjects);
                tr = AffineJTransform.createDirect2DIsomorphic(A, B, C, D, 1);
                for (MathObject<?> obj : mathObjects) {
                    Vec center = obj.getCenter();
                    prepareJumpPath(center, center.applyAffineTransform(tr), obj);
                }
                return true;
            }

            @Override
            public MathObjectGroup getIntermediateObject() {
                return MathObjectGroup.make(mathObjects);
            }

            @Override
            public void doAnim(double t) {
                super.doAnim(t);
                double lt = getLT(t);
                restoreStates(mathObjects);
                tr = AffineJTransform.createDirect2DIsomorphic(A, B, C, D, lt);
                for (MathObject obj : mathObjects) {
                    tr.applyTransform(obj);
                    applyAnimationEffects(lt, obj);
                }
            }

            @Override
            public void finishAnimation() {
                doAnim(t);
                super.finishAnimation();
            }

            @Override
            public void cleanAnimationAt(double t) {
            }

            @Override
            public void prepareForAnim(double t) {
                addObjectsToscene(mathObjects);
            }
        };
        resul.setDebugName("Isomorphism Transform");
        return resul;
    }// End of Isomorphism command

    public static AnimationWithEffects isomorphism3d(double runtime, Coordinates a, Coordinates b1,
                                                     Coordinates b2, Coordinates c, Coordinates d1,
                                                     Coordinates d2,
                                                     MathObject<?>... objects) {
        AnimationWithEffects resul = new AnimationWithEffects(runtime) {
            final MathObject<?>[] mathObjects = objects;
            AffineJTransform tr;
            Vec A, B1, B2, C, D1, D2;


            @Override
            public boolean doInitialization() {
                A = a.getVec().copy();
                B1 = b1.getVec().copy();
                B2 = b2.getVec().copy();
                C = c.getVec().copy();
                D1 = d1.getVec().copy();
                D2 = d2.getVec().copy();
                super.doInitialization();
                saveStates(mathObjects);
                tr = AffineJTransform.createDirect3DIsomorphic(A, B1, B2, C, D1, D2, 1);
                for (MathObject<?> obj : mathObjects) {
                    Vec center = obj.getCenter();
                    prepareJumpPath(center, center.applyAffineTransform(tr), obj);
                }
                return true;
            }

            @Override
            public MathObjectGroup getIntermediateObject() {
                return MathObjectGroup.make(mathObjects);
            }

            @Override
            public void doAnim(double t) {
                super.doAnim(t);
                double lt = getLT(t);
                restoreStates(mathObjects);
                tr = AffineJTransform.createDirect3DIsomorphic(A, B1, B2, C, D1, D2, lt);
                for (MathObject obj : mathObjects) {
                    tr.applyTransform(obj);
                    applyAnimationEffects(lt, obj);
                }
            }

            @Override
            public void finishAnimation() {
                doAnim(t);
                super.finishAnimation();
            }

            @Override
            public void cleanAnimationAt(double t) {
            }

            @Override
            public void prepareForAnim(double t) {
                addObjectsToscene(mathObjects);
            }
        };
        resul.setDebugName("Isomorphism3d Transform");
        return resul;
    }// End of Isomorphism command

    /**
     * Animation command that transforms a MathObject through a inverse isomorphism. Isomorphism is specified by 2 pairs
     * of points (origin-destiny)
     *
     * @param runtime Run time (in seconds)
     * @param a       First origin point
     * @param b       Second origin point
     * @param c       First destiny point
     * @param d       Second destiny point
     * @param objects Objects to animate (varargs)
     * @return Animation to run playAnimation method method
     */
    public static AnimationWithEffects inverseIsomorphism(double runtime, Coordinates a, Coordinates b, Coordinates c, Coordinates d,
                                                          MathObject<?>... objects) {
        AnimationWithEffects resul = new AnimationWithEffects(runtime) {
            final MathObject[] mathObjects = objects;
            AffineJTransform tr;
            Vec A = a.getVec().copy();
            Vec B = b.getVec().copy();
            Vec C = c.getVec().copy();
            Vec D = d.getVec().copy();

            @Override
            public boolean doInitialization() {
                super.doInitialization();
                saveStates(mathObjects);
                tr = AffineJTransform.createInverse2DIsomorphic(A, B, C, D, 1);
                for (MathObject obj : mathObjects) {
                    Vec center = obj.getCenter();
                    prepareJumpPath(center, center.applyAffineTransform(tr), obj);
                }
                return true;
            }

            @Override
            public MathObjectGroup getIntermediateObject() {
                return MathObjectGroup.make(mathObjects);
            }

            @Override
            public void doAnim(double t) {
                super.doAnim(t);
                double lt = getLT(t);
                restoreStates(mathObjects);
                tr = AffineJTransform.createInverse2DIsomorphic(A, B, C, D, lt);
                for (MathObject obj : mathObjects) {
                    tr.applyTransform(obj);
                    applyAnimationEffects(lt, obj);
                }
            }

            @Override
            public void finishAnimation() {
//                doAnim(1);
                super.finishAnimation();
            }

            @Override
            public void cleanAnimationAt(double t) {
            }

            @Override
            public void prepareForAnim(double t) {
                addObjectsToscene(mathObjects);
            }
        };
        resul.setDebugName("Inverse isomorphism Transform");
        return resul;
    }// End of homothecy command

    /**
     * Animation command that perfoms a reflection that maps A into B
     *
     * @param runtime Duration in seconds
     * @param A       Origin point
     * @param B       Destiny point
     * @param objects Objects to animate (varargs)
     * @return Animation to run with playAnim method
     */
    public static AnimationWithEffects reflection(double runtime, Coordinates A, Coordinates B, MathObject<?>... objects) {
        AnimationWithEffects resul = new AnimationWithEffects(runtime) {
            final MathObject<?>[] mathObjects = objects;
            final Vec axis1 = A.getVec().copy();
            final Vec axis2 = B.getVec().copy();
            AffineJTransform tr;

            @Override
            public boolean doInitialization() {
                super.doInitialization();
                saveStates(mathObjects);
                for (MathObject obj : mathObjects) {
                    tr = AffineJTransform.createReflection(axis1, axis2, 1);
                    Vec center = obj.getCenter();
                    prepareJumpPath(center, center.applyAffineTransform(tr), obj);
                }
                return true;
            }

            @Override
            public MathObjectGroup getIntermediateObject() {
                return MathObjectGroup.make(mathObjects);
            }

            @Override
            public void doAnim(double t) {
                super.doAnim(t);
                double lt = getLT(t);
                restoreStates(mathObjects);
                for (MathObject obj : mathObjects) {
                    tr = AffineJTransform.createReflection(axis1, axis2, lt);
                    tr.applyTransform(obj);
                    applyAnimationEffects(lt, obj);
                }
            }

            @Override
            public void finishAnimation() {
                doAnim(t);
                super.finishAnimation();
            }

            @Override
            public void cleanAnimationAt(double t) {
            }

            @Override
            public void prepareForAnim(double t) {
                addObjectsToscene(mathObjects);
            }
        };
        resul.setDebugName("Reflection Transform");
        return resul;
    }// End of reflectionByAxis command

    /**
     * Animation command that perfoms a reflection specified by 2 points
     *
     * @param runtime Duration in seconds
     * @param A       first axis point
     * @param B       second axis point
     * @param objects Objects to animate (varargs)
     * @return Animation to run with playAnim method
     */
    public static AnimationWithEffects reflectionByAxis(double runtime, Coordinates A, Coordinates B, MathObject<?>... objects) {
        AnimationWithEffects resul = new AnimationWithEffects(runtime) {
            final MathObject[] mathObjects = objects;
            final Vec axisPoint1 = A.getVec().copy();
            final Vec axisPoint2 = B.getVec().copy();
            AffineJTransform tr;

            @Override
            public boolean doInitialization() {
                super.doInitialization();
                saveStates(mathObjects);
                for (MathObject obj : mathObjects) {
                    tr = AffineJTransform.createReflectionByAxis(axisPoint1, axisPoint2, 1);
                    Vec center = obj.getCenter();
                    prepareJumpPath(center, center.applyAffineTransform(tr), obj);
                }
                return true;
            }

            @Override
            public MathObjectGroup getIntermediateObject() {
                return MathObjectGroup.make(mathObjects);
            }

            @Override
            public void doAnim(double t) {
                super.doAnim(t);
                double lt = getLT(t);
                restoreStates(mathObjects);
                for (MathObject obj : mathObjects) {
                    tr = AffineJTransform.createReflectionByAxis(axisPoint1, axisPoint2, lt);
                    tr.applyTransform(obj);
                    applyAnimationEffects(lt, obj);
                }
            }

            @Override
            public void finishAnimation() {
                doAnim(t);
                super.finishAnimation();
            }

            @Override
            public void cleanAnimationAt(double t) {
            }

            @Override
            public void prepareForAnim(double t) {
                addObjectsToscene(mathObjects);
            }
        };
        resul.setDebugName("Reflexion by Axis Transform");
        return resul;
    }// End of reflectionByAxis command

    /**
     * Changes the draw color and fill color of the objects to the given one. If one of the colors is null, the colors
     * are not changed Thus, if you want to change only drawColor, you should set fillColor to null.
     *
     * @param runtime   Duration in seconds
     * @param drawColor Color to be the drawColor
     * @param fillColor Color to be the fillColor
     * @param objects   MathObjects to animate (varargs)
     * @return The animation to be played with the playAnimation method
     */
    public static Animation setColor(double runtime, PaintStyle drawColor, PaintStyle fillColor, MathObject<?>... objects) {
        MODrawProperties mpDst = MODrawProperties.makeNullValues();
        if (drawColor != null) {
            mpDst.setDrawColor(drawColor);
        }
        if (fillColor != null) {
            mpDst.setFillColor(fillColor);
        }
//            Animation cmd = setMP(runtime, mpDst, ob);
        Animation cmd = setMP(runtime, mpDst, objects);

        cmd.setDebugName("setColor");
        return cmd;
    }

    /**
     * Animation command that changes the math drawing properties of given object, interpolating
     *
     * @param runtime Time duration in seconds
     * @param mp      Destination {@link MODrawProperties}
     * @param objects Objects to animate (varargs)
     * @return Animation to run with playAnim method
     */
    public static Animation setMP(double runtime, DrawStyleProperties mp, MathObject<?>... objects) {
        Animation resul = new Animation(runtime) {
            final MathObject[] mathObjects = objects;
            final DrawStyleProperties mpDst = mp;

            @Override
            public boolean doInitialization() {
                super.doInitialization();
                saveStates(mathObjects);
                return true;
            }

            @Override
            public MathObjectGroup getIntermediateObject() {
                return MathObjectGroup.make(mathObjects);
            }

            @Override
            public void doAnim(double t) {
                super.doAnim(t);
                double lt = getLT(t);
                restoreStates(mathObjects);
                for (MathObject obj : mathObjects) {
                    obj.getMp().interpolateFrom(mpDst, lt);
                }
            }

            @Override
            public void finishAnimation() {
                doAnim(t);
                super.finishAnimation();
            }

            @Override
            public void cleanAnimationAt(double t) {
            }

            @Override
            public void prepareForAnim(double t) {
                addObjectsToscene(mathObjects);
            }
        };
        resul.setDebugName("setMP");
        return resul;
    }// End of setMP command

    /**
     * Animation command that changes the style of given object, interpolating
     *
     * @param runtime   Time duration in seconds
     * @param styleName Name of destination style
     * @param objects   Objects to animate (varargs)
     * @return Animation to run with playAnim method
     */
    public static Animation setStyle(double runtime, String styleName, MathObject<?>... objects) {
        Animation resul = setMP(runtime, MODrawProperties.createFromStyle(styleName), objects);
        resul.setDebugName("setStyle");
        return resul;
    }

    /**
     * Animation command that pans and zooms the camera to the selected objects. Gaps are automatically added to (via
     * the camera.setGaps method).
     *
     * @param runtime Duration (in seconds)
     * @param camera  Camera to animate
     * @param objs    Mathobjects to zoom to (varargs)
     * @return Animation to run with playAnim method
     */
    public static Animation cameraZoomToObjects(double runtime, Camera camera, MathObject<?>... objs) {
        Rect r = objs[0].getBoundingBox();
        for (MathObject obj : objs) {
            r = Rect.union(r, obj.getBoundingBox());
        }
        r.addGap(camera.getGaps().x, camera.getGaps().y);
        return cameraZoomToRect(runtime, camera, r);
    }

    /**
     * Animation command that zooms the camera to a given area specified by a {@link Rect}
     *
     * @param runtime    Time duration in seconds
     * @param camera     Camera to zoom
     * @param rectToZoom Area to zoom
     * @return Animation to run with playAnim method
     */
    public static Animation cameraZoomToRect(double runtime, Camera camera, Rect rectToZoom) {
        Animation resul;
        if (camera instanceof Camera3D) {
            Camera3D camera3d = (Camera3D) camera;
            Vec vecLook = camera3d.look.to(rectToZoom.getCenter());
            Vec vecEye = camera3d.eye.to(rectToZoom.getCenter());
            vecEye.addInSite(Vec.to(0, 0, camera3d.getProperEyeHeight(rectToZoom)));
            resul = AnimationGroup.make(
                    shift(runtime, vecEye, camera3d.eye),
                    shift(runtime, vecLook, camera3d.look)
            );
        } else {
            resul = new Animation(runtime) {
                final Camera cam = camera;
                final Rect rDst = cam.getMathViewThatContains(rectToZoom);
                Rect rSource;

                @Override
                public boolean doInitialization() {
                    super.doInitialization();
                    rSource = cam.getMathView();
                    return true;
                }

                @Override
                public MathObject getIntermediateObject() {
                    return null;
                }

                @Override
                public void doAnim(double t) {
                    super.doAnim(t);
                    double lt = getLT(t);
                    Rect r = rSource.interpolate(rDst, lt);
                    cam.setMathView(r);
                }

                @Override
                public void finishAnimation() {
                    doAnim(t);
                    super.finishAnimation();
                }

                @Override
                public void cleanAnimationAt(double t) {
                }

                @Override
                public void prepareForAnim(double t) {
                }
            };
        }
        resul.setDebugName("cameraZoomToRect");
        return resul;
    }

    /**
     * Animation that pans the {@link Camera} by a given vector
     *
     * @param runtime     Time duration in seconds
     * @param camera      Camera to pan
     * @param shiftVector Shift vector
     * @return Animation to run with playAnim method
     */
    public static Animation cameraShift(double runtime, Camera camera, Vec shiftVector) {
        Animation resul = null;
        if (camera != null) {
            if (camera instanceof Camera3D) {
                Camera3D camera3d = (Camera3D) camera;
                resul = shift(runtime, shiftVector, camera3d.eye, camera3d.look);
            } else {

                Rect r = camera.getMathView().shift(shiftVector);
                resul = cameraZoomToRect(runtime, camera, r);
            }
            resul.setDebugName("cameraShift");
        }
        return resul;
    }

    public static Animation cameraScale(double runtime, Camera cam, double scale) {
        Animation resul;
        if (cam instanceof Camera3D) {
            Camera3D camera3d = (Camera3D) cam;
            Vec shiftVector = camera3d.look.to(camera3d.eye).multInSite(scale - 1);
            resul = shift(runtime, shiftVector, camera3d.eye);
        } else {
            resul = Commands.cameraZoomToRect(runtime, cam, cam.getMathView().scale(scale, scale));
        }
        resul.setDebugName("cameraScale");
        return resul;
    }

    public static Animation camera3DRotate(double runtime, Camera3D cam, double angle) {
        return camera3DRotate(runtime, cam, angle, Axis.Z);
    }

    public static Animation camera3DRotate(double runtime, Camera3D cam, double angle, Axis axis) {
        double anglex = (axis == Axis.X ? angle : 0);
        double angley = (axis == Axis.Y ? angle : 0);
        double anglez = (axis == Axis.Z ? angle : 0);
        Animation resul = Commands.rotate3d(runtime, cam.look, anglex, angley, anglez, cam.eye);
        resul.setDebugName("camera3DRotate");
        return resul;
    }

    /**
     * Animation command that reduces the size and alpha of the MathObject.After finishing the animation, object is
     * removed from the current scene.
     *
     * @param runtime Run time (in seconds)
     * @param objects Objects to animate (varargs)
     * @return Animation to run with playAnim method
     */
    public static Animation shrinkOut(double runtime, MathObject<?>... objects) {
        return shrinkOut(runtime, 0, objects);
    }

    /**
     * Animation command that reduces the size and alpha of the MathObject.A rotation of a given angle is performed
     * meanwhile.After finishing the animation, object is removed from the current scene.
     *
     * @param angle   Angle to rotate, in radians
     * @param runtime Duration time in seconds
     * @param objects Objects to animate (varargs)
     * @return Animation to run with playAnim method
     */
    public static Animation shrinkOut(double runtime, double angle, MathObject<?>... objects) {
        return shrinkOut(runtime, angle, OrientationType.BOTH, objects);
    }

    /**
     * Animation command that reduces the size and alpha of the MathObject.A rotation of a given angle is performed
     * meanwhile.After finishing the animation, object is removed from the current scene.
     *
     * @param angle      Angle to rotate, in radians
     * @param runtime    Duration time in seconds
     * @param shrinkType How to shrink, HORIZONTAL, VERTICAL or BOTH
     * @param objects    Objects to animate (varargs)
     * @return Animation to run with playAnim method
     */
    public static Animation shrinkOut(double runtime, double angle, OrientationType shrinkType, MathObject<?>... objects) {
        Animation anim = new Animation(runtime) {
            final MathObject[] mathObjects = objects;

            @Override
            public boolean doInitialization() {

                for (MathObject obj : mathObjects) {
                    if (obj instanceof Constructible) {
                        Constructible cnstr = (Constructible) obj;
                        cnstr.setFreeMathObject(true);
                    }
                }
                super.doInitialization();
                saveStates(mathObjects);
                return true;
            }

            @Override
            public MathObjectGroup getIntermediateObject() {
                return MathObjectGroup.make(mathObjects);
            }

            @Override
            public void doAnim(double t) {
                super.doAnim(t);
                double lt = getLT(t);
                double sx = (shrinkType == OrientationType.VERTICAL ? 1 : 1 - lt);
                double sy = (shrinkType == OrientationType.HORIZONTAL ? 1 : 1 - lt);
                restoreStates(mathObjects);
                for (MathObject obj : mathObjects) {
                    obj.scale(sx, sy);
                    obj.drawAlpha(obj.getMp().getDrawColor().getAlpha() * (1 - lt));
                    obj.fillAlpha(obj.getMp().getFillColor().getAlpha() * (1 - lt));
                    obj.thickness(obj.getMp().getThickness() * (1 - lt));
                    obj.rotate(lt * angle);
                }
            }

            @Override
            public void finishAnimation() {
                doAnim(t);
                super.finishAnimation();
                removeObjectsFromScene(mathObjects);
                for (MathObject obj : mathObjects) {
                    if (obj instanceof Constructible) {
                        Constructible cnstr = (Constructible) obj;
                        cnstr.setFreeMathObject(false);
                    }
                }
            }

            @Override
            public void cleanAnimationAt(double t) {
                double lt = getLT(t);
                if (lt == 1) {
                    removeObjectsFromScene(mathObjects);
                }
            }

            @Override
            public void prepareForAnim(double t) {
                addObjectsToscene(mathObjects);
            }
        };
        anim.setLambda(t -> t);// Default
        anim.setDebugName("shrinkOut");
        return anim;
    }// End of shrinkOut command

    /**
     * Performs the inverse animation than shrinkOut, that its, scale the size and alpha of the object from zero.
     *
     * @param runtime Duration time in seconds
     * @param objects Objects to animate
     * @return Animation to run with playAnim method
     */
    public static Animation growIn(double runtime, MathObject<?>... objects) {
        return growIn(runtime, 0, OrientationType.BOTH, objects);
    }

    /**
     * Performs the inverse animation than shrinkOut, that its, scale the size and alpha of the object from zero. An
     * inverse rotation from given angle to 0 is performed.
     *
     * @param angle   Rotation angle
     * @param runtime Duration time in seconds
     * @param objects Objects to animate (varargs)
     * @return Animation to run with playAnim method
     */
    public static Animation growIn(double runtime, double angle, MathObject<?>... objects) {
        return growIn(runtime, angle, OrientationType.BOTH, objects);
    }

    /**
     * Performs the inverse animation than shrinkOut, that its, scale the size and alpha of the object from zero. An
     * inverse rotation from given angle to 0 is performed.
     *
     * @param angle    Rotation angle
     * @param runtime  Duration time in seconds
     * @param growType Scale type: HORIZONTAL, VERTICAL or BOTH
     * @param objects  Objects to animate (varargs)
     * @return Animation to run with playAnim method
     */
    public static Animation growIn(double runtime, double angle, OrientationType growType, MathObject<?>... objects) {
        Animation anim = new Animation(runtime) {
            MathObject[] mathObjects = objects;

            @Override
            public boolean doInitialization() {

                this.mathObjects = Arrays.stream(objects)
                        .filter(Objects::nonNull)
                        .toArray(size -> Arrays.copyOf(objects, size));

                for (MathObject obj : mathObjects) {
//                    obj.visible(false);
                    if (obj instanceof Constructible) {
                        Constructible cnstr = (Constructible) obj;
                        cnstr.setFreeMathObject(true);
                    }
                }
                super.doInitialization();
                saveStates(mathObjects);
                return true;
            }

            @Override
            public MathObjectGroup getIntermediateObject() {
                return MathObjectGroup.make(mathObjects);
            }

            @Override
            public void doAnim(double t) {
                super.doAnim(t);
                double lt = getLT(t);
                double sx = (growType == OrientationType.VERTICAL ? 1 : lt);
                double sy = (growType == OrientationType.HORIZONTAL ? 1 : lt);
                restoreStates(mathObjects);
                for (MathObject obj : mathObjects) {
                    obj.scale(sx, sy);
                    obj.drawAlpha(obj.getMp().getDrawColor().getAlpha() * lt);
                    obj.fillAlpha(obj.getMp().getFillColor().getAlpha() * lt);
                    obj.rotate((1 - lt) * angle);
                }
            }

            @Override
            public void finishAnimation() {
                doAnim(t);
                super.finishAnimation();
                for (MathObject obj : mathObjects) {
                    if (obj instanceof Constructible) {
                        Constructible cnstr = (Constructible) obj;
                        cnstr.setFreeMathObject(false);
                    }
                }
            }

            @Override
            public void cleanAnimationAt(double t) {
                double lt = getLT(t);
                if (lt == 0) {//if ended at the beginning, remove objects
                    removeObjectsFromScene(mathObjects);
                }
            }

            @Override
            public void prepareForAnim(double t) {

                addObjectsToscene(mathObjects);
            }
        };
        anim.setLambda(t -> t);// Default value
        anim.setDebugName("growIn");
        return anim;
    }// End of growIn command

    /**
     * Performs an animation modifying the alpha of the object from 0 to the original alpha of object. Both drawAlpha
     * and fillAlpha are animated.
     *
     * @param runtime Duration time in seconds
     * @param objects Objects to animate (varargs)
     * @return Animation to run with the playAnim method
     */
    public static AnimationWithEffects fadeIn(double runtime, MathObject<?>... objects) {
        AnimationWithEffects anim = new AnimationWithEffects(runtime) {
            MathObject[] mathObjects = objects;

            @Override
            public boolean doInitialization() {
                super.doInitialization();
                this.mathObjects = Arrays.stream(objects)
                        .filter(Objects::nonNull)
                        .toArray(size -> Arrays.copyOf(objects, size));
                saveStates(mathObjects);
                for (MathObject obj : mathObjects) {
                    obj.visible(false);
                }
                return true;
            }

            @Override
            public MathObjectGroup getIntermediateObject() {
                return MathObjectGroup.make(mathObjects);
            }

            @Override
            public void doAnim(double t) {
                super.doAnim(t);
                double lt = getLT(t);
                restoreStates(mathObjects);
                for (MathObject obj : mathObjects) {
                    obj.getMp().multDrawAlpha(lt);
                    obj.getMp().multFillAlpha(lt);
                    applyAnimationEffects(lt, obj);
                }
            }

            @Override
            public void finishAnimation() {
                doAnim(t);
                super.finishAnimation();

            }

            @Override
            public void cleanAnimationAt(double t) {
                double lt = getLT(t);
                if (lt == 0) {//If ends at t=0, should remove objects from scene, as played in reverse
                    removeObjectsFromScene(mathObjects);
                } else {
                    addObjectsToscene(mathObjects);
                }
            }

            @Override
            public void prepareForAnim(double t) {
                addObjectsToscene(mathObjects);
            }

        };
        anim.setLambda(t -> t);// Default value
        anim.setDebugName("fadeIn");
        return anim;
    }// End of fadeIn command

    /**
     * Performs an animation modifying the alpha of the object to 0. Both drawAlpha and fillAlpha are animated. Object
     * is removed from current scene after finishing animation.
     *
     * @param runtime Duration time in seconds
     * @param objects Object to animate
     * @return Animation to run with playAnim method
     */
    public static AnimationWithEffects fadeOut(double runtime, MathObject<?>... objects) {
        AnimationWithEffects anim = new AnimationWithEffects(runtime) {
            MathObject[] mathObjects = objects;

            @Override
            public boolean doInitialization() {
                super.doInitialization();
                this.mathObjects = Arrays.stream(objects)
                        .filter(Objects::nonNull)
                        .toArray(size -> Arrays.copyOf(objects, size));
                saveStates(mathObjects);
                return true;
            }

            @Override
            public MathObjectGroup getIntermediateObject() {
                return MathObjectGroup.make(mathObjects);
            }

            @Override
            public void doAnim(double t) {
                super.doAnim(t);
                double lt = getLT(t);
                restoreStates(mathObjects);
                for (MathObject obj : mathObjects) {
                    obj.getMp().multDrawAlpha(1 - lt);
                    obj.getMp().multFillAlpha(1 - lt);
                    applyAnimationEffects(lt, obj);
                }
            }

            @Override
            public void finishAnimation() {
                doAnim(t);
                super.finishAnimation();

            }

            @Override
            public void cleanAnimationAt(double t) {
                double lt = getLT(t);
                if (lt == 1) {
                    restoreStates(mathObjects);// Restore original alphas in case of reutilization
                    removeObjectsFromScene(mathObjects);
                }
            }

            @Override
            public void prepareForAnim(double t) {
                addObjectsToscene(mathObjects);
            }
        };
        anim.setLambda(t -> t);// Default value
        anim.setDebugName("fadeOut");
        return anim;
    }// End of fadeOut command

    /**
     * Animated version of method setLayout for MathObjectGroup instances
     *
     * @param runtime    Duration in seconds
     * @param corner     Corner to layout from. If null, first object of the group will be used
     * @param layoutType Type of anchor to apply layout as defined in the enum Type
     * @param gap        Gap to apply between elements, in math units
     * @param group      MathObjectGroup instance to apply the layout
     * @return Animation to run with playAnim method
     */
    public static ShiftAnimation setLayout(double runtime, Coordinates<?> corner, LayoutType layoutType, double gap,
                                           MathObjectGroup group) {
        //Create a simple group with rectangles
        Shape[] shapes = group.getObjects().stream().map(t -> Shape.rectangle(t.getBoundingBox())).toArray(Shape[]::new);
        MathObjectGroup groupCopy = MathObjectGroup.make(shapes);
        groupCopy.setLayout(corner, layoutType, gap);
        HashMap<MathObject<?>, Vec> centers = new HashMap<>();
        int n = 0;
        for (MathObject<?> ob : groupCopy) {
            centers.put(ob, ob.getCenter());// The destination centers of the objects of the group
            n++;
        }
        MathObject<?>[] mathobjects = group.getObjects().toArray(new MathObject[group.size()]);

        return getShiftAnimation(runtime, mathobjects, centers);
    }

    private static ShiftAnimation getShiftAnimation(double runtime, MathObject<?>[] mathobjects, HashMap<MathObject<?>, Vec> centers) {
        ShiftAnimation resul = new ShiftAnimation(runtime, mathobjects) {
            @Override
            public boolean doInitialization() {
                super.doInitialization();
                JMathAnimScene.logger.debug("Initialized setLayout animation");
                for (MathObject obj : mathobjects) {
                    Vec dst = centers.get(obj);
                    setShiftVector(obj, obj.getCenter().to(dst));
                }
                return true;
            }

        };
        resul.setDebugName("setLayout");
        return resul;
    }

    /**
     * Animated version of method setLayout for MathObjectGroup instances
     *
     * @param runtime Duration in seconds
     * @param layout  A GroupLayout subclass
     * @param group   MathObjectGroup instance to apply the layout
     * @return Animation to run with playAnim method
     */
    public static ShiftAnimation setLayout(double runtime, GroupLayout layout, MathObjectGroup group) {

        MathObject[] mathobjects = group.getObjects().toArray(new MathObject[group.size()]);

        ShiftAnimation resul = new ShiftAnimation(runtime, mathobjects) {
            final HashMap<MathObject<?>, Vec> centers = new HashMap<>();

            @Override
            public boolean doInitialization() {
                super.doInitialization();
                MathObjectGroup groupCopy = group.copy();
                groupCopy.setLayout(layout);
                for (int n = 0; n < groupCopy.size(); n++) {
                    centers.put(group.get(n), groupCopy.get(n).getCenter());// The destination centers of the objects of the group
                }

                JMathAnimScene.logger.debug("Initialized setLayout animation");
                for (MathObject obj : mathobjects) {
                    Vec dst = centers.get(obj);
                    setShiftVector(obj, obj.getCenter().to(dst));
                }
                return true;
            }
        };
        resul.setDebugName("setLayout");
        return resul;
    }

    /**
     * Animates a change in the alpha fill of the objects. The precise change is given by the lambda function used in
     * the animation
     *
     * @param runTime Duration in seconds
     * @param objects MathObjects to apply the animation (varargs)
     * @return Animation to run with playAnim method
     */
    public static AnimationWithEffects changeFillAlpha(double runTime, MathObject<?>... objects) {
        AnimationWithEffects resul = new AnimationWithEffects(runTime) {
            final MathObject[] mathObjects = objects;
            final ArrayList<Double> alphaOrig = new ArrayList<>();

            @Override
            public boolean doInitialization() {
                super.doInitialization();
                JMathAnimScene.logger.debug("Initialized changeFillAlpha animation");
                saveStates(mathObjects);
                return true;
            }

            @Override
            public MathObjectGroup getIntermediateObject() {
                return MathObjectGroup.make(mathObjects);
            }

            @Override
            public void doAnim(double t) {
                super.doAnim(t);
                restoreStates(mathObjects);
                double lt = getLT(t);
                for (MathObject obj : objects) {
                    obj.getMp().setFillAlpha(obj.getMp().getFillColor().getAlpha() * lt);
                    applyAnimationEffects(lt, obj);
                }
            }

            @Override
            public void finishAnimation() {
//                doAnim(1);
                super.finishAnimation();
            }

            @Override
            public void cleanAnimationAt(double t) {
            }

            @Override
            public void prepareForAnim(double t) {
                addObjectsToscene(mathObjects);
            }
        };
        resul.setDebugName("changeFillAlpha");
        return resul;
    }

    /**
     * Performs an exit animation of an object(s). When finished, removes the objects from the scene.
     *
     * @param runtime     Duration in seconds
     * @param exitAnchor  Exit direction. A vaue of Type
     * @param mathObjects Objects to exit (varargs)
     * @return this animation, ready to play with the playAnimation method
     */
    public static ShiftAnimation moveOut(double runtime, ScreenAnchor exitAnchor, MathObject<?>... mathObjects) {
        ShiftAnimation resul = new ShiftAnimation(runtime, mathObjects) {
            @Override
            public boolean doInitialization() {
                Camera camera;
                Renderer rend = JMathAnimConfig.getConfig().getRenderer();
                JMathAnimScene.logger.debug("Initialized moveOut animation");
                for (MathObject<?> obj : mathObjects) {//Free constructible objects before saving states
                    if (obj instanceof Constructible) {
                        Constructible<?> cnstr = (Constructible<?>) obj;
                        cnstr.setFreeMathObject(true);
                    }
                }
                super.doInitialization();
                // Compute appropiate shift vectors
                Rect r = JMathAnimConfig.getConfig().getCamera().getMathView();
                for (MathObject<?> obj : mathObjects) {

                    if (obj.getCamera() == null)
                        camera = JMathAnimConfig.getConfig().getCamera();
                    else
                        camera = obj.getCamera();

                    double gap = -rend.ThicknessToMathWidth(obj) * 2;
                    Vec objAnchor;
                    Vec screenAnchor = Anchor.getScreenAnchorPoint(camera, exitAnchor, -gap,-gap);
                    switch (exitAnchor) {
                        case RIGHT:
                            objAnchor = Anchor.getAnchorPoint(obj, AnchorType.LEFT);
                            screenAnchor.y = objAnchor.y;
                            break;
                        case UPPER:
                            objAnchor = Anchor.getAnchorPoint(obj, AnchorType.LOWER);
                            screenAnchor.x = objAnchor.x;
                        case LOWER:
                            objAnchor = Anchor.getAnchorPoint(obj, AnchorType.UPPER);
                            screenAnchor.x = objAnchor.x;
                            break;
                        case UPPER_LEFT:
                            objAnchor = Anchor.getAnchorPoint(obj, AnchorType.DIAG4);
                            break;
                        case UPPER_RIGHT:
                            objAnchor = Anchor.getAnchorPoint(obj, AnchorType.DIAG3);
                            break;
                        case LOWER_LEFT:
                            objAnchor = Anchor.getAnchorPoint(obj, AnchorType.DIAG1);
                            break;
                        case LOWER_RIGHT:
                            objAnchor = Anchor.getAnchorPoint(obj, AnchorType.DIAG2);
                            break;
                        default:
//                        case LEFT:
                            objAnchor = Anchor.getAnchorPoint(obj, AnchorType.RIGHT);
                            screenAnchor.y = objAnchor.y;
                            break;
                    }
                    this.setShiftVector(obj, objAnchor.to(screenAnchor));
                }
                return true;
            }

            @Override
            public void finishAnimation() {
                super.finishAnimation();
                double lt = getTotalLambda().applyAsDouble(1);

                for (MathObject obj : mathObjects) {
                    if (lt == 1) {//Remove objects if completely moved out
                        removeObjectsFromScene(obj);
                    }
                    if (obj instanceof Constructible) {
                        Constructible cnstr = (Constructible) obj;
                        cnstr.setFreeMathObject(false);
                    }
                }
            }
        };
        resul.setDebugName("moveOut for " + mathObjects.length + " object(s)");
        return resul;
    }

    /**
     * Performs an enter animation of an object(s).The object(s) are added automatically to the scene.
     *
     * @param runtime     Duration in seconds
     * @param enterAnchor Enter direction. A vaue of Type
     * @param mathObjects Objects to enter (varargs)
     * @return this animation, ready to play with the playAnimation method
     */
    public static ShiftAnimation moveIn(double runtime, ScreenAnchor enterAnchor, MathObject<?>... mathObjects) {


        Renderer rend = JMathAnimConfig.getConfig().getRenderer();
        ArrayList<MathObject<?>> toRemove = new ArrayList<>();
        ArrayList<MathObject<?>> toAnimateArrayList = new ArrayList<>(Arrays.asList(mathObjects));
        final MathObject[] toAnimateArray = toAnimateArrayList.toArray(new MathObject[0]);
        ShiftAnimation resul = new ShiftAnimation(runtime, toAnimateArray) {
            @Override
            public boolean doInitialization() {
                Camera camera;

                for (MathObject obj : toAnimateArray) {
                    if (obj instanceof Constructible) {
                        Constructible cnstr = (Constructible) obj;
                        cnstr.setFreeMathObject(true);
                    }
                }


                super.doInitialization();
                addThisAtTheEnd.addAll(Arrays.asList(mathObjects));
                removeThisAtTheEnd.addAll(toRemove);
                JMathAnimScene.logger.debug("Initialized moveIn animation");
                for (MathObject<?> obj : toAnimateArray) {
                    if (obj.getCamera() == null)
                        camera = JMathAnimConfig.getConfig().getCamera();
                    else
                        camera = obj.getCamera();


                    double gap = -rend.ThicknessToMathWidth(obj) * 2;

                    Vec q = Anchor.getScreenAnchorPoint(camera, enterAnchor, 0, 0);
                    Vec p;
                    switch (enterAnchor) {
                        case RIGHT:
                            p = Anchor.getAnchorPoint(obj, AnchorType.LEFT,gap,gap,gap);
                            q.y = p.y;
                            break;
                        case UPPER:
                            p = Anchor.getAnchorPoint(obj, AnchorType.LOWER,gap,gap,gap);
                            q.x = p.x;
                            break;
                        case LOWER:
                            p = Anchor.getAnchorPoint(obj, AnchorType.UPPER,gap,gap,gap);
                            q.x = p.x;
                            break;
                        case UPPER_LEFT:
                            p = Anchor.getAnchorPoint(obj, AnchorType.DIAG4,gap,gap,gap);
                            break;
                        case UPPER_RIGHT:
                            p = Anchor.getAnchorPoint(obj, AnchorType.DIAG3,gap,gap,gap);
                            break;
                        case LOWER_LEFT:
                            p = Anchor.getAnchorPoint(obj, AnchorType.DIAG1,gap,gap,gap);
                            break;
                        case LOWER_RIGHT:
                            p = Anchor.getAnchorPoint(obj, AnchorType.DIAG2,gap,gap,gap);
                            break;
                        default:
                            //case LEFT:
                            p = Anchor.getAnchorPoint(obj, AnchorType.RIGHT,gap,gap,gap);
                            q.y = p.y;
                    }
                    obj.shift(p.to(q));
                    this.setShiftVector(obj, q.to(p));
                }
                saveStates(toAnimateArray);
                return true;
            }

            @Override
            public void finishAnimation() {
                super.finishAnimation();
                double lt = getTotalLambda().applyAsDouble(t);
                if (lt == 0) {
                    removeObjectsFromScene(mathObjects);
                    restoreStates(mathObjects);
                }
                for (MathObject obj : mathObjects) {
                    if (obj instanceof Constructible) {
                        Constructible cnstr = (Constructible) obj;
                        cnstr.setFreeMathObject(false);
                    }
                }
            }
        };
        resul.setDebugName(
                "moveIn for " + mathObjects.length + " object(s)");
        return resul;
    }

    /**
     * Transforms one object to another, animating a flip effect
     *
     * @param runtime    Duration in seconds
     * @param ob1        Origin object
     * @param ob2        Destiny object
     * @param horizontal If true, an horizontal flipping is done, vertical otherwise
     * @return The animation
     */
    public static FlipTransform flipTransform(double runtime, boolean horizontal, MathObject ob1, MathObject ob2) {
        return new FlipTransform(runtime,
                (horizontal ? OrientationType.HORIZONTAL : OrientationType.VERTICAL), ob1, ob2);
    }

    /**
     * Animated version of the align method
     *
     * @param runtime     time in seconds
     * @param dst         Destiny object to align with
     * @param type        Type of align, a value of MathObject.Align enum
     * @param mathobjects Mathobjects to animate
     * @return The created animation
     */
    public static ShiftAnimation align(double runtime, MathObject dst, MathObject.Align type,
                                       MathObject<?>... mathobjects) {
        ShiftAnimation resul = new ShiftAnimation(runtime, mathobjects) {
            @Override
            public boolean doInitialization() {
                super.doInitialization();
                JMathAnimScene.logger.debug("Initialized align animation");
                for (MathObject obj : mathobjects) {
                    Vec dstCenter = Shape.rectangle(obj.getBoundingBox()).align(dst, type).getCenter();
                    setShiftVector(obj, obj.getCenter().to(dstCenter));
                }
                return true;
            }

        };
        resul.setDebugName("align for " + mathobjects.length + " object(s)");
        return resul;
    }

    /**
     * Animated version of the stackTo method.The destination point is computed at the initialize() method so it cab ne
     * safely concatenated. If several objects are animated, the second will be stacked to the first, and so on
     *
     * @param runtime     time in seconds
     * @param dst         Destiny coordinates to stack
     * @param anchorType  Type of stack, a value of Type enum
     * @param gap         Gap between the stacked objects
     * @param mathobjects Mathobjects to animate
     * @return The created animation
     */
    public static ShiftAnimation stackTo(double runtime, Coordinates<?> dst, AnchorType anchorType, double gap,
                                         MathObject<?>... mathobjects) {
        ShiftAnimation resul = new ShiftAnimation(runtime, mathobjects) {
            @Override
            public boolean doInitialization() {
                super.doInitialization();
                AnchorType reverse = Anchor.reverseAnchorPoint(anchorType);
                Coordinates<?> previous = dst;

                for (MathObject<?> obj : mathobjects) {
                    Vec objAnchor = Anchor.getAnchorPoint(obj, reverse);
                    setShiftVector(obj, objAnchor.to(previous));
                    previous = Anchor.getAnchorPoint(obj, anchorType);
                }
                return true;
            }
        };
        resul.setDebugName("stackTo for " + mathobjects.length + " object(s)");
        return resul;
    }

    /**
     * Animates a crossout. The size of the crossout is computed from the bounding box of the crossed object
     *
     * @param runtime Duration in seconds
     * @param obj     Object to cross out
     * @return The created animation
     */
    public static Animation crossOut(double runtime, MathObject<?> obj) {
        Rect bbox = obj.getBoundingBox();
        Shape s1 = Shape.segment(bbox.getUpperLeft(), bbox.getLowerRight()).scale(.75).linecap(StrokeLineCap.BUTT)
                .drawColor(JMColor.RED).layer(Integer.MAX_VALUE);
        Shape s2 = Shape.segment(bbox.getUpperRight(), bbox.getLowerLeft()).scale(.75).linecap(StrokeLineCap.BUTT)
                .drawColor(JMColor.RED).layer(Integer.MAX_VALUE);
        double longi = .25 * s1.getPoint(0).to(s1.getPoint(1)).norm();
        double width = JMathAnimConfig.getConfig().getRenderer().MathWidthToThickness(longi);
        s1.thickness(width).getMp().setAbsoluteThickness(false);
        s2.thickness(width).getMp().setAbsoluteThickness(false);
        Concatenate resul = new Concatenate(
                new ShowCreation(.5 * runtime, s1).setLambda(t -> t),
                new ShowCreation(.5 * runtime, s2).setLambda(t -> t)
        );
        resul.setDebugName("crossOut");
        return resul;
    }

    public static JoinAnimation crossAndFadeOut(double runtime, MathObject obj) {
        JoinAnimation resul = JoinAnimation.make(runtime);
        Rect bb = obj.getBoundingBox();
        final Vec a = bb.getUpperRight();
        final Vec b = bb.getLowerLeft();
        double width = JMathAnimConfig.getConfig().getRenderer().MathWidthToThickness(a.to(b).norm());
        Shape cross = Shape.segment(a, b).thickness(width * .25).drawColor("red");
        cross.getMp().setLinecap(StrokeLineCap.SQUARE);
        //This tricky lambda is necessary due to the squared linecap
        resul.add(ShowCreation.make(1, cross).setLambda(UsefulLambdas.restrictTo(.1, 1)));
        resul.add(Commands.fadeOut(1, obj, cross));
        return resul;
    }

    public static JoinAnimation crossAndShrink(double runtime, MathObject obj) {
        JoinAnimation resul = JoinAnimation.make(runtime);
        Rect bb = obj.getBoundingBox();
        final Vec a = bb.getUpperRight();
        final Vec b = bb.getLowerLeft();
        double width = JMathAnimConfig.getConfig().getRenderer().MathWidthToThickness(a.to(b).norm());
        Shape cross = Shape.segment(a, b).thickness(width * .25).drawColor("red");
        cross.getMp().setLinecap(StrokeLineCap.SQUARE);
        //This tricky lambda is necessary due to the squared linecap
        resul.add(ShowCreation.make(2, cross).setLambda(UsefulLambdas.restrictTo(.1, 1)));
        resul.add(Commands.shrinkOut(1, obj, cross));
        return resul;
    }

    public enum Axis {
        X, Y, Z
    }

}
