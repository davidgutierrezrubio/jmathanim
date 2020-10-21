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

package com.jmathanim.Animations;

import com.jmathanim.Animations.commands.Commands;
import com.jmathanim.Cameras.Camera;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.LaTeXMathObject;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MultiShapeObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.SVGMathObject;
import com.jmathanim.mathobjects.Shape;
import java.util.ArrayList;

/**
 * Several static methods to easily perform most common animations
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class PlayAnim {

    JMathAnimScene scene;
    public double defaultRunTimeshowCreation = 2;
    public double defaultRunTimefadeIn = 1;
    public double defaultRunTimefadeOut = 1;
    public double defaultRunTimeGrowIn = 1;
    public double defaultRunTimeShrinkOut = 1;
    public double defaultRunTimeHighlight = 2;

    public PlayAnim(JMathAnimScene scene) {
        this.scene = scene;
    }

    public void fadeIn(MathObject... objs) {
        fadeIn(defaultRunTimefadeIn, objs);
    }

    public void fadeIn(double runtime, MathObject... objs) {
        scene.playAnimation(Commands.fadeIn(runtime, objs));
    }

    public void fadeOut(MathObject... objs) {
        fadeOut(defaultRunTimefadeOut, objs);
    }

    public void fadeOut(double runtime, MathObject... objs) {
        scene.playAnimation(Commands.fadeOut(runtime, objs));
    }

    public void fadeOutAll() {
        fadeOutAll(defaultRunTimefadeOut);
    }

    public void fadeOutAll(double runtime) {
        MathObject[] objects = scene.getObjects().toArray(new MathObject[scene.getObjects().size()]);
        scene.playAnimation(Commands.fadeOut(runtime, objects));
    }

    //Convenience methods
    //This methods allow easy and fast ways to shift, rotate, and scale objects
    public void shift(double runtime, double dx, double dy, MathObject... objs) {
        scene.playAnimation(Commands.shift(runtime, dx, dy, objs));
    }

    public void shift(double runTime, Vec v, MathObject... objs) {
        scene.playAnimation(Commands.shift(runTime, v, objs));
    }

    public void scale(double runTime, Point center, double sc, MathObject... objs) {
        scale(runTime, center, sc, sc, sc, objs);
    }

    public void scale(double runTime, Point center, double scx, double scy, double scz, MathObject... objs) {
        scene.playAnimation(Commands.scale(runTime, center, scx, scy, scz, objs));
    }

    public void rotate(double runTime, double angle, MathObject... objs) {
        scene.playAnimation(Commands.rotate(runTime, angle, objs));
    }

    public void rotate(double runTime, Point center, double angle, MathObject... objs) {
        scene.playAnimation(Commands.rotate(runTime, center, angle, objs));
    }

    public void transform(double runTime, Shape obj1, Shape obj2) {
        scene.playAnimation(new Transform(runTime, obj1, obj2));
    }

    public void adjustToObjects(MathObject... objs) {
        adjustToObjects(2, objs);
    }

    public void adjustToObjects(double runTime, MathObject... objs) {
        final Vec gaps = scene.getCamera().getGaps();
        Rect r = scene.getCamera().getMathView();
        for (MathObject obj : objs) {
            r = r.union(obj.getBoundingBox().addGap(gaps.x, gaps.y));
        }
        zoomToRect(runTime, r);
    }

    public void zoomToObjects(double runTime, MathObject... objs) {
        Rect r = objs[0].getBoundingBox();
        for (MathObject obj : objs) {
            r = r.union(obj.getBoundingBox());
        }
        zoomToRect(runTime, r);
    }

    public void zoomToRect(double runTime, Camera cam, Rect r) {
        scene.playAnimation(Commands.cameraZoomToRect(runTime, cam, r));
    }

    public void zoomToRect(double runTime, Rect r) {
        zoomToRect(runTime, scene.getCamera(), r);
    }

    /**
     * Animate a zoom over the current area. Rect view is multiplied by scale
     * factor
     *
     * @param scale Scale factor. A scale of value .5 means applying a x2 zoom
     * factor
     * @param runTime Duration in seconds
     */
    public void cameraScale(double runTime, double scale) {
        PlayAnim.this.cameraScale(runTime, scene.getCamera(), scale);
    }

    public void cameraScale(double runTime, Camera cam, double scale) {
        scene.playAnimation(Commands.cameraZoomToRect(runTime, cam, cam.getMathView().scaled(scale, scale)));
    }

    public void cameraShift(Camera cam, Vec v, double runTime) {
        scene.playAnimation(Commands.cameraShift(runTime, cam, v));
    }

    /**
     * Animates a camera pan with the given shift vector
     *
     * @param v Shift vector
     * @param runTime Duration in seconds
     */
    public void cameraShift(double runTime,Vec v) {
        scene.playAnimation(Commands.cameraShift(runTime, scene.getCamera(), v));
    }

    /**
     * Convenience method. Animates a camera pan with the given shift vector,
     * specified by x and y coordinates
     *
     * @param x x coordinate of shift vector
     * @param y y coordinate of shift vector
     * @param runTime Duration in seconds
     */
    public void cameraShift(double runTime,double x, double y) {
        scene.playAnimation(Commands.cameraShift(runTime, scene.getCamera(), new Vec(x, y)));
    }

    /**
     * Plays an animation highlighting an object. Scales and unscales this
     * object for 1 second. Objects not scalable ({@link Point} for example) are
     * not affected.
     *
     * @param mobjs Objects to highlight (varargs)
     */
    public void highlight(MathObject... mobjs) {
        scene.playAnimation(new Highlight(defaultRunTimeHighlight, mobjs));
    }

    /**
     * Plays an animation highlighting an object.Scales and unscales this object
     * for given time. Objects not scalable ({@link Point} for example) are not
     * affected.
     *
     * @param runTime Duration in seconds
     * @param mobjs Objects to highlight (varargs)
     */
    public void highlight(double runTime, MathObject... mobjs) {
        scene.playAnimation(new Highlight(runTime, mobjs));
    }

    /**
     * Plays an animation to introduce an object into scene.Scaling, rotating
     * and applying alpha from 0 to current.
     *
     * @param angle Angle in radians
     * @param mobjs Objects to animate (varargs)
     * @param runTime Duration in seconds
     */
    public void growIn(double runTime, double angle, MathObject... mobjs) {
        scene.playAnimation(Commands.growIn(runTime, angle, mobjs));
    }

    /**
     * Plays an animation to introduce an object into scene.Scaling and applying
     * alpha from 0 to current.
     *
     * @param mobjs Objects to animate (varargs)
     * @param runTime Duration in seconds
     */
    public void growIn(double runTime, MathObject... mobjs) {
        scene.playAnimation(Commands.growIn(runTime, mobjs));
    }

    /**
     * Convenience method.Plays an animation to introduce an object into scene.
     * Scaling and applying alpha from 0 to current. Duration of the animation
     * is 2 seconds.
     *
     * @param mobjs Objects to animate (varargs)
     */
    public void growIn(MathObject... mobjs) {
        growIn(defaultRunTimeGrowIn, mobjs);
    }

    /**
     * Plays an animation that reduces the size and alpha of the
     * {@link MathObject}.A rotation of a given angle is performed
     * meanwhile.After finishing the animation, object is removed from the
     * current scene.
     *
     * @param runTime Duration in seconds
     * @param angle Angle in radians
     * @param mobjs Objects to animate (varargs)
     *
     */
    public void shrinkOut(double runTime, double angle, MathObject... mobjs) {
        scene.playAnimation(Commands.shrinkOut(runTime, angle, mobjs));
    }

    /**
     * Plays an animation that reduces the size and alpha of the
     * {@link MathObject}.After finishing the animation, object is removed from
     * the current scene.
     *
     * @param runTime Duration in seconds
     * @param mobjs Objects to animate (varargs)
     */
    public void shrinkOut(double runTime, MathObject... mobjs) {
        scene.playAnimation(Commands.shrinkOut(runTime, 0, mobjs));
    }

    /**
     * Convenience method. Plays an animation that reduces the size and alpha of
     * the {@link MathObject}, with a duration of 1 second.After finishing the
     * animation, object is removed from the current scene.
     *
     * @param mobj Object to animate
     */
    public void shrinkOut(MathObject mobj) {
        shrinkOut(defaultRunTimeShrinkOut, mobj);

    }

    /**
     * Plays an animation drawing a {@link MathObject}.The object drawn is added
     * to the current scene. Several strategies to create the object are
     * automatically chosen: For a simple shape, draws the shape. For a
     * {@link MultiShapeObject} performs a simple shape creation for each shape,
     * with a time gap between one and the next. For a {@link SVGMathObject}
     * (which includes {@link LaTeXMathObject}) a "first draw, then fill"
     * strategy is chosen.
     *
     * @param runtime Run time (in seconds)
     * @param mobjects Objects to highlight (varargs)
     */
    public void showCreation(double runtime, MathObject... mobjects) {
        ArrayList<Animation> anims = new ArrayList<>();
        for (MathObject obj : mobjects) {
            anims.add(new ShowCreation(runtime, obj));
        }
        scene.playAnimation(anims);
    }

    /**
     * Convenience overloaded method. Plays an animation drawing a
     * {@link MathObject} with runtime of 2 seconds.
     *
     * @param mobjs Objects to highlight (varargs)
     */
    public void showCreation(MathObject... mobjs) {
        showCreation(defaultRunTimeshowCreation, mobjs);
    }
}
