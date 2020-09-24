/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations;

import com.jmathanim.Animations.commands.Commands;
import com.jmathanim.Cameras.Camera;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

/**
 * Several static methods to easily perform most common animations
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class PlayAnim {

    JMathAnimScene scene;

    public PlayAnim(JMathAnimScene scene) {
        this.scene = scene;
    }

    public void fadein(MathObject obj, double runTime) {
        scene.play(new FadeIn(obj, runTime));
    }

    //Convenience methods
    //This methods allow easy and fast ways to shift, rotate, and scale objects
    public void shift(MathObject obj, double dx, double dy, double runTime) {
        scene.play(Commands.shift(obj, dx, dy, runTime));
    }

    public void shift(MathObject obj, Vec v, double runTime) {
        scene.play(Commands.shift(obj, v, runTime));
    }

    public void scale(MathObject obj, Point center, double sc, double runTime) {
        scale(obj, center, sc, sc, sc, runTime);
    }

    public void scale(MathObject obj, Point center, double scx, double scy, double scz, double runTime) {
        scene.play(Commands.scale(obj, center, scx, scy, scz, runTime));
    }

    public void rotate(MathObject obj, double angle, double runTime) {
        scene.play(Commands.rotate(obj, obj.getCenter(), angle, runTime));
    }

    public void rotate(MathObject obj, Point center, double angle, double runTime) {
        scene.play(Commands.rotate(obj, center, angle, runTime));
    }

    public void transform(Shape obj1, Shape obj2, double runTime) {
        scene.play(new Transform(obj1, obj2, runTime));
    }

    public void zoomToRect(Camera cam, Rect r, double runTime) {
        scene.play(Commands.cameraFocusToRect(cam, r, runTime));
    }

    public void zoomToRect(Rect r, double runTime) {
        zoomToRect(scene.getCamera(), r, runTime);
    }

    public void scaleCamera(double scale, double runTime) {
        scaleCamera(scene.getCamera(), scale, runTime);
    }

    public void scaleCamera(Camera cam, double scale, double runTime) {
        scene.play(Commands.cameraFocusToRect(cam, cam.getMathView().scaled(scale, scale), runTime));
    }

    public void shiftCamera(Camera cam, Vec v, double runTime) {
        scene.play(Commands.cameraShift(cam, v, runTime));
    }

    public void shiftCamera(Vec v, double runTime) {
        scene.play(Commands.cameraShift(scene.getCamera(), v, runTime));
    }

    public void shiftCamera(double x, double y, double runTime) {
        scene.play(Commands.cameraShift(scene.getCamera(), new Vec(x, y), runTime));
    }
}
