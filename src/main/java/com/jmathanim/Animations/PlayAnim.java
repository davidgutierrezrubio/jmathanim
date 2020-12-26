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
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.JMColor;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.LaTeXMathObject;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MathObjectGroup;
import com.jmathanim.mathobjects.MultiShapeObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.SVGMathObject;
import com.jmathanim.mathobjects.Shape;
import java.util.ArrayList;

/**
 * Several static methods to easily perform most common animations. This class
 * is automatically instantiated by the JMathAnimScene class for using in the
 * scene.
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class PlayAnim {

    /**
     * Default runtime for ShowCreation methods (in seconds)
     */
    public double defaultRunTimeshowCreation = 2;
    /**
     * Default runtime for FadeIn methods (in seconds)
     */
    public double defaultRunTimefadeIn = 1;
    /**
     * Default runtime for FadeOut methods (in seconds)
     */
    public double defaultRunTimefadeOut = 1;
    /**
     * Default runtime for GrowIn methods (in seconds)
     */
    public double defaultRunTimeGrowIn = 1;
    /**
     * Default runtime for Shrink Out methods (in seconds)
     */
    public double defaultRunTimeShrinkOut = 1;
    /**
     * Default runtime for Highlight methods (in seconds)
     */
    public double defaultRunTimeHighlight = 2;
    /**
     * Default runtime for camera methods (in seconds)
     */
    public double defaultRunTimeCamera = 2;
    /**
     * Default runtime for move out methods (in seconds)
     */
    public double defaultRunTimeMoveOut = 1;
    /**
     * Default runtime for move in methods (in seconds)
     */
    public double defaultRunTimeMoveIn = 1;
    private final JMathAnimScene scene;

    public PlayAnim(JMathAnimScene scene) {
        this.scene = scene;
    }

    /**
     * Animates the given objects, setting their alpha (draw and fill) from 0 to
     * current, using default runtime. Then add the objects to the scene if they
     * aren't.
     *
     * @param objs Mathobjects to animate (varargs)
     */
    public void fadeIn(MathObject... objs) {
        fadeIn(defaultRunTimefadeIn, objs);
    }

    /**
     * Animates the given objects, setting their alpha (draw and fill) from 0 to
     * current, using given runtime.Then add the objects to the scene if they
     * aren't.
     *
     * @param runtime Duration in seconds
     * @param objs Mathobjects to animate (varargs)
     */
    public void fadeIn(double runtime, MathObject... objs) {
        scene.playAnimation(Commands.fadeIn(runtime, objs));
    }

    /**
     * Animates the given objects, setting their alpha (draw and fill) from
     * current to 0, using default runtime. Then remove the objects to the
     * scene.
     *
     * @param objs Mathobjects to animate (varargs)
     */
    public void fadeOut(MathObject... objs) {
        fadeOut(defaultRunTimefadeOut, objs);
    }

    /**
     * Animates the given objects, setting their alpha (draw and fill) from
     * current to 0, using given runtime.Then remove the objects to the scene.
     *
     * @param runtime Duration in seconds
     * @param objs Mathobjects to animate (varargs)
     */
    public void fadeOut(double runtime, MathObject... objs) {
        scene.playAnimation(Commands.fadeOut(runtime, objs));
    }

    /**
     * Animates all the objects in the scene, setting their alpha (draw and
     * fill) from current to 0, using default runtime
     * {@link defaultRunTimefadeOut}. Then remove the objects to the scene. This
     * method is mostly used as a transition between parts of a scene, as it
     * clears the scene completely.
     *
     */
    public void fadeOutAll() {
        fadeOutAll(defaultRunTimefadeOut);
    }

    /**
     * Animates all the objects in the scene, setting their alpha (draw and
     * fill) from current to 0, using specified runtime.Then remove the objects
     * to the scene. This method is mostly used as a transition between parts of
     * a scene, as it clears the scene completely.
     *
     * @param runtime Duration in seconds
     */
    public void fadeOutAll(double runtime) {
        MathObject[] objects = scene.getObjects().toArray(new MathObject[scene.getObjects().size()]);
        scene.playAnimation(Commands.fadeOut(runtime, objects));
    }
    
  public void fadeOutAllBut(double runtime,MathObject...objs) {
      ArrayList<MathObject> toRemove=new ArrayList<>();
      toRemove.addAll(scene.getObjects());
      for (MathObject obj:objs) {
          if (obj instanceof MultiShapeObject) {
              for (Shape sh:(MultiShapeObject)obj){
                  toRemove.remove(sh);
              }
          }
           if (obj instanceof MathObjectGroup) {
              for (MathObject o:(MathObjectGroup)obj){
                  toRemove.remove(o);
              }
          }
          
          
          toRemove.remove(obj);
      }
        MathObject[] objects = toRemove.toArray(new MathObject[toRemove.size()]);
        scene.playAnimation(Commands.fadeOut(runtime, objects));
    }
    /**
     * Shift the specified objects out of the math view and removes them from
     * the scene.
     *
     * @param runtime Duration in seconds
     * @param exitAnchor Exit, given by a Anchor constant. For example
     * Anchor.UPPER will move the objects to the upper side of the math view.
     * @param mathObjects Mathobjects to animate (varargs)
     */
    public void moveOut(double runtime, Anchor.Type exitAnchor, MathObject... mathObjects) {
        scene.playAnimation(Commands.moveOut(runtime, exitAnchor, mathObjects));
    }

    /**
     * Shift the specified objects out of the math view and removes them from
     * the scene.The duration of the animation is the value stored in
     * {@link defaultRunTimeMoveOut}.
     *
     * @param exitAnchor Exit, given by a Anchor constant. For example
     * Anchor.UPPER will move the objects to the upper side of the math view.
     * @param mathObjects Mathobjects to animate (varargs)
     */
    public void moveOut(Anchor.Type exitAnchor, MathObject... mathObjects) {
        scene.playAnimation(Commands.moveOut(defaultRunTimeMoveOut, exitAnchor, mathObjects));
    }

    /**
     * Shift the specified objects from an outer point of the math view to their
     * original position, adding them to the scene.
     *
     * @param runtime Duration in seconds
     * @param enterAnchor Enter point, given by a Anchor constant. For example
     * Anchor.UPPER will move the objects from the upper side of the math view.
     * @param mathObjects Mathobjects to animate (varargs)
     */
    public void moveIn(double runtime, Anchor.Type enterAnchor, MathObject... mathObjects) {
        scene.playAnimation(Commands.moveIn(runtime, enterAnchor, mathObjects));
    }

    /**
     * Shift the specified objects from an outer point of the math view to their
     * original position, adding them to the scene. The duration of the
     * animation is the value stored in {@link defaultRunTimeMoveIn}.
     *
     * @param enterAnchor Enter point, given by a Anchor constant. For example
     * Anchor.UPPER will move the objects from the upper side of the math view.
     * @param mathObjects Mathobjects to animate (varargs)
     */
    public void moveIn(Anchor.Type enterAnchor, MathObject... mathObjects) {
        scene.playAnimation(Commands.moveIn(defaultRunTimeMoveIn, enterAnchor, mathObjects));
    }

    /**
     * Overloaded method. Animation that moves specified objects with a vector
     *
     * @param runtime Duration in seconds
     * @param dx x-coordinate of translation vector
     * @param dy y-coordinate of translation vector
     * @param objs Mathobjects to animate (varargs)
     */
    public void shift(double runtime, double dx, double dy, MathObject... objs) {
        scene.playAnimation(Commands.shift(runtime, dx, dy, objs));
    }

    /**
     * Animation that moves specified objects with a vector
     *
     * @param runtime Duration in seconds
     * @param v Traslation vector
     * @param objs Mathobjects to animate (varargs)
     */
    public void shift(double runtime, Vec v, MathObject... objs) {
        scene.playAnimation(Commands.shift(runtime, v, objs));
    }

    /**
     * Animates a scaling of the given objects uniformly with a given scale,
     * around a specified center
     *
     * @param runTime Duration in seconds
     * @param center Scale center
     * @param scaleFactor Scale factor
     * @param objs Mathobjects to animate (varargs)
     */
    public void scale(double runTime, Point center, double scaleFactor, MathObject... objs) {
        scale(runTime, center, scaleFactor, scaleFactor, scaleFactor, objs);
    }

    /**
     * Animates a scaling of the given objects uniformly with a given scale. The
     * scale center is the center of the combined bounding box of all objects.
     * If you want to scale each object around its own center, use one scale
     * animation for each object.
     *
     * @param runTime Duration in seconds
     * @param scaleFactor Scale factor
     * @param objs Mathobjects to animate (varargs)
     */
    public void scale(double runTime, double scaleFactor, MathObject... objs) {
        scale(runTime, scaleFactor, scaleFactor, objs);
    }

    /**
     * Animates a scaling of the given objects with a x and y scales.The scale
     * center is the center of the combined bounding box of all objects.If you
     * want to scale each object around its own center, use one scale animation
     * for each object.
     *
     * @param runTime Duration in seconds
     * @param scx x scale factor
     * @param scy y scale factor
     * @param objs Mathobjects to animate (varargs)
     */
    public void scale(double runTime, double scx, double scy, MathObject... objs) {
        Rect r = objs[0].getBoundingBox();
        for (MathObject obj : objs) {
            r = Rect.union(r,obj.getBoundingBox());
        }
        scene.playAnimation(Commands.scale(runTime, r.getCenter(), scx, scy, 1, objs));
    }

    /**
     * Animates a scaling of the given objects with given x and y scale, around
     * a specified center
     *
     * @param runTime Duration in seconds
     * @param center Scale center
     * @param scx x-scale
     * @param scy y-scale
     * @param objs Mathobjects to animate (varargs)
     */
    public void scale(double runTime, Point center, double scx, double scy, MathObject... objs) {
        scene.playAnimation(Commands.scale(runTime, center, scx, scy, 1, objs));
    }

    /**
     * Animates a scaling of the given objects with given x, y and z scale,
     * around a specified center.This method is defined for convenience as the
     * z-scale has no effect until 3D is developed.
     *
     * @param runTime Duration in seconds
     * @param center Scale center
     * @param scx x-scale
     * @param scy y-scale
     * @param scz z-scale
     * @param objs Mathobjects to animate (varargs)
     */
    public void scale(double runTime, Point center, double scx, double scy, double scz, MathObject... objs) {
        scene.playAnimation(Commands.scale(runTime, center, scx, scy, scz, objs));
    }

    /**
     * Animates a rotation of the given objects around the center of the
     * combined bounding box of all objects. If you want to rotate each object
     * around its own center, use one rotate animation for each object.
     *
     * @param runTime Duration in seconds
     * @param angle Rotation angle, in radians
     * @param objs Mathobjects to animate (varargs)
     */
    public void rotate(double runTime, double angle, MathObject... objs) {
        Rect r = objs[0].getBoundingBox();
        for (MathObject obj : objs) {
            r = Rect.union(r,obj.getBoundingBox());
        }
        scene.playAnimation(Commands.rotate(runTime, r.getCenter(), angle, objs));
    }

    /**
     * Animates a rotation of the given objects around a specified center.
     *
     * @param runTime Duration in seconds
     * @param center Rotation center
     * @param angle Rotation angle, in radians
     * @param objs Mathobjects to animate (varargs)
     */
    public void rotate(double runTime, Point center, double angle, MathObject... objs) {
        scene.playAnimation(Commands.rotate(runTime, center, angle, objs));
    }

    /**
     * Animates a smooth transform from one MathObject to another. The specific
     * type of transform is chosen depending on the given objects. Currently
     * there are specific implementations FunctionSimpleInterpolateTransform,
     * HomothecyStrategyTransform, PointInterpolationCanonical,
     * PointInterpolationSimpleShapeTransform, and
     * RotateAndScaleXYStrategyTransform. An optimization strategy is also
     * chosen, if available. In general, the transformed object may be unusable
     * after the transform, so it's recommended to remove the transformed object
     * from the scene and using the destiny object instead.
     *
     * @param runTime Duration in seconds
     * @param transformed Object that will be transformed
     * @param destiny Object destiny
     */
    public void transform(double runTime, MathObject transformed, MathObject destiny) {
        scene.playAnimation(new Transform(runTime, transformed, destiny));
    }

    /**
     * Performs a pan and zoom out animation of the current camera to ensure all
     * objects in the scene fit in the math view, using the default run time for
     * camera animations. You can set the gaps between objects and mathview
     * borders, with the camera.setGaps method. This method doesn't zoom in the
     * view.
     */
    public void adjustCameraToAllObjects() {
        adjustCameraToAllObjects(defaultRunTimeCamera);

    }

    /**
     * Performs a pan and zoom out animation of the current camera to ensure all
     * objects in the scene fit in the math view, using the specified run
     * time.You can set the gaps between objects and mathview borders, with the
     * camera.setGaps method. This method doesn't zoom in the view.
     *
     * @param runtime Duration in seconds
     */
    public void adjustCameraToAllObjects(double runtime) {
        final Vec gaps = scene.getCamera().getGaps();
        Rect r = scene.getCamera().getMathView();
        for (MathObject obj : scene.getObjects()) {
            r = Rect.union(r,obj.getBoundingBox().addGap(gaps.x, gaps.y));
        }
        zoomToRect(runtime, r);
    }

    /**
     * Performs a pan and zoom out animation of the current camera to ensure all
     * given objects fit in the math view, using the default run time for camera
     * animations.You can set the gaps between objects and mathview borders,
     * with the camera.setGaps method. This method doesn't zoom in the view.
     *
     * @param objs Mathobjects to include in the view (varargs)
     */
    public void adjustToObjects(MathObject... objs) {
        adjustToObjects(defaultRunTimeCamera, objs);
    }

    /**
     * Performs a pan and zoom out animation of the current camera to ensure all
     * given objects fit in the math view, using the specified run time. You can
     * set the gaps between objects and mathview borders, with the
     * camera.setGaps method. This method doesn't zoom in the view.
     *
     * @param runTime Duration in seconds
     * @param objs Mathobjects to include in the view (varargs)
     */
    public void adjustToObjects(double runTime, MathObject... objs) {
        final Vec gaps = scene.getCamera().getGaps();
        Rect r = scene.getCamera().getMathView();
        for (MathObject obj : objs) {
            r = Rect.union(r,obj.getBoundingBox().addGap(gaps.x, gaps.y));
        }
        zoomToRect(runTime, r);
    }

    /**
     * This method is similar to adjustToObjects, but it performs a zoom in to
     * use all availabla math view to show the specified objects
     *
     * @param runTime Duration in seconds
     * @param objs Mathobjects to include in the view (varargs)
     */
    public void zoomToObjects(double runTime, MathObject... objs) {
        Rect r = objs[0].getBoundingBox();
        for (MathObject obj : objs) {
            r = Rect.union(r,obj.getBoundingBox());
        }
        zoomToRect(runTime, r);
    }

    private void zoomToRect(double runTime, Camera cam, Rect r) {
        scene.playAnimation(Commands.cameraZoomToRect(runTime, cam, r));
    }

    /**
     * Zooms the camera so that it contains the given rect. The resulting
     * mathview is the smallest view containing the Rect specified.
     *
     * @param runTime Duration in seconds
     * @param rect Rect to zoom to
     */
    public void zoomToRect(double runTime, Rect rect) {
        zoomToRect(runTime, scene.getCamera(), rect);
    }

    /**
     * Animate a zoom over the current math view. The rect that represents the
     * math view is multiplied by scale factor, so factors greater than 1 means
     * zoom out.
     *
     * @param scale Scale factor. A scale of value .5 means applying a x2 zoom
     * factor
     * @param runTime Duration in seconds
     */
    public void cameraScale(double runTime, double scale) {
        PlayAnim.this.cameraScale(runTime, scene.getCamera(), scale);
    }

    private void cameraScale(double runTime, Camera cam, double scale) {
        scene.playAnimation(Commands.cameraZoomToRect(runTime, cam, cam.getMathView().scaled(scale, scale)));
    }

    private void cameraShift(double runTime, Vec v, Camera cam) {
        scene.playAnimation(Commands.cameraShift(runTime, cam, v));
    }

    /**
     * Animates a camera pan with the given shift vector
     *
     * @param runTime Duration in seconds
     * @param v Shift vector
     */
    public void cameraShift(double runTime, Vec v) {
        cameraShift(runTime, v, scene.getCamera());
    }

    /**
     * Overloaded method. Animates a camera pan with the given shift vector,
     * specified by x and y coordinates
     *
     * @param runTime Duration in seconds
     * @param x x coordinate of shift vector
     * @param y y coordinate of shift vector
     *
     */
    public void cameraShift(double runTime, double x, double y) {
        cameraShift(runTime, new Vec(x, y), scene.getCamera());
    }

    /**
     * Plays an animation highlighting an object. Scales and unscales this
     * object for 1 second. Objects not scalable ({@link Point} for example) are
     * not affected.
     *
     * @param mobjs Objects to highlight (varargs)
     */
    public void highlight(MathObject... mobjs) {
        scene.playAnimation(Commands.highlight(defaultRunTimeHighlight, mobjs));
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
        scene.playAnimation(Commands.highlight(runTime, mobjs));
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
     * Plays an animation that reduces the size and alpha of the MathObject.A
     * rotation of a given angle is performed meanwhile.After finishing the
     * animation, object is removed from the current scene.
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
     * MathObject.After finishing the animation, object is removed from the
     * current scene.
     *
     * @param runTime Duration in seconds
     * @param mobjs Objects to animate (varargs)
     */
    public void shrinkOut(double runTime, MathObject... mobjs) {
        scene.playAnimation(Commands.shrinkOut(runTime, 0, mobjs));
    }

    /**
     * Convenience method. Plays an animation that reduces the size and alpha of
     * the MathObject, with a duration of 1 second.After finishing the
     * animation, object is removed from the current scene.
     *
     * @param mobj Object to animate
     */
    public void shrinkOut(MathObject mobj) {
        shrinkOut(defaultRunTimeShrinkOut, mobj);

    }

    /**
     * Plays an animation drawing a MathObject.The object drawn is added to the
     * current scene. Several strategies to create the object are automatically
     * chosen: For a simple shape, draws the shape. For a
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
     * Convenience overloaded method. Plays an animation drawing a MathObject
     * with runtime of 2 seconds.
     *
     * @param mobjs Objects to highlight (varargs)
     */
    public void showCreation(MathObject... mobjs) {
        showCreation(defaultRunTimeshowCreation, mobjs);
    }

    /**
     * Changes the draw and fill color of the selected objects, animating the
     * change for the specified time. If any of the colors is null, this color
     * will not be animated. This way you can for example, change only the draw
     * color of the selected objects if fill color is null
     *
     * @param runtime Time of animation (in seconds)
     * @param drawColor Draw color to set (if null, no changes applied during
     * animation)
     * @param fillColor Fill color to set (if null, no changes applied during
     * animation)
     * @param mobjects Objects to apply animation (varargs)
     */
    public void setColor(double runtime, JMColor drawColor, JMColor fillColor, MathObject... mobjects) {
        scene.playAnimation(Commands.setColor(runtime, drawColor, fillColor, mobjects));
    }

    /**
     * Changes the draw parameters of the selected objects to match the given
     * style, animating the change for the specified time.
     *
     * @param runtime Time of animation (in seconds)
     * @param styleName Style to apply
     * @param mobjects bjects to apply animation (varargs)
     */
    public void setStyle(double runtime, String styleName, MathObject... mobjects) {
        scene.playAnimation(Commands.setStyle(runtime, styleName, mobjects));
    }

}
