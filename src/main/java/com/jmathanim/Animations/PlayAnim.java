/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations;

import com.jmathanim.Animations.commands.Commands;
import com.jmathanim.Cameras.Camera;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;
import java.util.ArrayList;

/**
 * Several static methods to easily perform most common animations
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class PlayAnim {

    JMathAnimScene scene;

    public PlayAnim(JMathAnimScene scene) {
        this.scene = scene;
    }

    public void fadeIn(MathObject obj) {
        scene.playAnimation(Commands.fadeIn(obj, 1));
    }

    public void fadeIn(MathObject obj, double runTime) {
        scene.playAnimation(Commands.fadeIn(obj, runTime));
    }

    public void fadeOut(MathObject obj) {
        scene.playAnimation(Commands.fadeOut(obj, 1));
    }

    public void fadeOut(MathObject obj, double runTime) {
        scene.playAnimation(Commands.fadeOut(obj, runTime));
    }

    //Convenience methods
    //This methods allow easy and fast ways to shift, rotate, and scale objects
    public void shift(MathObject obj, double dx, double dy, double runTime) {
        scene.playAnimation(Commands.shift(obj, dx, dy, runTime));
    }

    public void shift(MathObject obj, Vec v, double runTime) {
        scene.playAnimation(Commands.shift(obj, v, runTime));
    }

    public void scale(MathObject obj, Point center, double sc, double runTime) {
        scale(obj, center, sc, sc, sc, runTime);
    }

    public void scale(MathObject obj, Point center, double scx, double scy, double scz, double runTime) {
        scene.playAnimation(Commands.scale(obj, center, scx, scy, scz, runTime));
    }

    public void rotate(MathObject obj, double angle, double runTime) {
        scene.playAnimation(Commands.rotate(obj, obj.getCenter(), angle, runTime));
    }

    public void rotate(MathObject obj, Point center, double angle, double runTime) {
        scene.playAnimation(Commands.rotate(obj, center, angle, runTime));
    }

    public void transform(Shape obj1, Shape obj2, double runTime) {
        scene.playAnimation(new Transform(obj1, obj2, runTime));
    }

    public void adjustToObjects(ArrayList<MathObject> objs, double runTime) {
        Rect r = scene.getCamera().getMathView();
        for (MathObject obj : objs) {
            r = r.union(obj.getBoundingBox().addGap(.1, .1));
        }
        zoomToRect(r, runTime);
    }

    public void zoomToObjects(ArrayList<MathObject> objs, double runTime) {
        Rect r = objs.get(0).getBoundingBox();
        for (MathObject obj : objs) {
            r = r.union(obj.getBoundingBox());
        }
        zoomToRect(r, runTime);
    }

    public void zoomToRect(Camera cam, Rect r, double runTime) {
        scene.playAnimation(Commands.cameraZoomToRect(cam, r, runTime));
    }

    public void zoomToRect(Rect r, double runTime) {
        zoomToRect(scene.getCamera(), r, runTime);
    }

    /**
     * Animate a zoom over the current area. Rect view is multiplied by scale
     * factor
     *
     * @param scale Scale factor. A scale of value .5 means applying a x2 zoom
     * factor
     * @param runTime Duration in seconds
     */
    public void scaleCamera(double scale, double runTime) {
        scaleCamera(scene.getCamera(), scale, runTime);
    }

    public void scaleCamera(Camera cam, double scale, double runTime) {
        scene.playAnimation(Commands.cameraZoomToRect(cam, cam.getMathView().scaled(scale, scale), runTime));
    }

    public void shiftCamera(Camera cam, Vec v, double runTime) {
        scene.playAnimation(Commands.cameraShift(cam, v, runTime));
    }

    /**
     * Animates a camera pan with the given shift vector
     *
     * @param v Shift vector
     * @param runTime Duration in seconds
     */
    public void shiftCamera(Vec v, double runTime) {
        scene.playAnimation(Commands.cameraShift(scene.getCamera(), v, runTime));
    }

    /**
     * Convenience method. Animates a camera pan with the given shift vector,
     * specified by x and y coordinates
     *
     * @param x x coordinate of shift vector
     * @param y y coordinate of shift vector
     * @param runTime Duration in seconds
     */
    public void shiftCamera(double x, double y, double runTime) {
        scene.playAnimation(Commands.cameraShift(scene.getCamera(), new Vec(x, y), runTime));
    }

    /**
     * Plays an animation highlighting an object. Scales and unscales this
     * object for a second. Objects not scalable ({@link Point} for example) are
     * not affected.
     *
     * @param mobj Object to highlight
     */
    public void highlight(MathObject mobj) {
        scene.playAnimation(new Highlight(mobj));
    }

    /**
     * Plays an animation highlighting an object. Scales and unscales this
     * object for given time. Objects not scalable ({@link Point} for example)
     * are not affected.
     *
     * @param mobj Object to highlight
     * @param runTime Duration in seconds
     */
    public void highlight(MathObject mobj, double runTime) {
        scene.playAnimation(new Highlight(mobj, runTime));
    }

    /**
     * Plays an animation to introduce an object into scene.Scaling, rotating
     * and applying alpha from 0 to current.
     *
     * @param mobj Object to animate
     * @param angle Angle in radians
     * @param runTime Duration in seconds
     */
    public void growIn(MathObject mobj, double angle, double runTime) {
        scene.playAnimation(Commands.growIn(mobj, angle, runTime));
    }

    /**
     * Plays an animation to introduce an object into scene. Scaling and
     * applying alpha from 0 to current.
     *
     * @param mobj Object to animate
     * @param runTime Duration in seconds
     */
    public void growIn(MathObject mobj, double runTime) {
        scene.playAnimation(Commands.growIn(mobj, runTime));
    }

    /**
     * Convenience method. Plays an animation to introduce an object into scene.
     * Scaling and applying alpha from 0 to current. Duration of the animation
     * is 2 seconds.
     *
     * @param mobj Object to animate
     */
    public void growIn(MathObject mobj) {
        growIn(mobj, 2);
    }

    /**
     * Plays an animation to remove an object from a scene. Scaling and applying
     * alpha from current to 0. Object is removed from scene after finishing
     * animation.
     *
     * @param mobj Object to animate
     * @param angle Angle in radians
     * @param runTime Duration in seconds
     */
    public void shrinkOut(MathObject mobj, double angle, double runTime) {
        scene.playAnimation(Commands.shrinkOut(mobj, angle, runTime));
    }

    /**
     * Plays an animation to remove an object from a scene. Scaling and applying
     * alpha from current to 0. Object is removed from scene after finishing
     * animation.
     *
     * @param mobj Object to animate
     * @param runTime Duration in seconds
     */
    public void shrinkOut(MathObject mobj, double runTime) {
        scene.playAnimation(Commands.shrinkOut(mobj, runTime));
    }

    /**
     * Convenience method. Plays an animation to remove an object from a scene.
     * Scaling and applying alpha from current to 0, with 1 second duration.
     * Object is removed from scene after finishing animation.
     *
     * @param mobj Object to animate
     */
    public void shrinkOut(MathObject mobj) {
        shrinkOut(mobj, 1);
    }
}
