/*
 * Copyright (C) 2021 David
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

import com.jmathanim.Constructible.Constructible;
import com.jmathanim.Constructible.Points.CTPoint;
import com.jmathanim.Styling.JMColor;
import com.jmathanim.Utils.Boxable;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.UsefulLambdas;
import com.jmathanim.mathobjects.*;

import java.util.ArrayList;

/**
 *
 * @author David
 */
public class ContourHighlight extends Animation {

    MathObject[] objs;
    JMColor highlightColor;
    double thickness;
    double amplitude;
    private final MultiShapeObject subshapes;

    /**
     * Static constuctor. Creates an animation that hightlights the contour of a
     * Shape object.
     *
     * @param runTime Runtime in seconds
     * @param objs Shapes to run the animation (varargs).
     * @return The animation ready to play with playAnim method.
     */
    public static ContourHighlight make(double runTime, MathObject... objs) {
        return new ContourHighlight(runTime, objs);
    }

    public static ContourHighlight make(double runTime, double gap, Rect... objs) {
//        MathObject[] toArray = Arrays.stream(objs).map(t -> Shape.rectangle(t.copy().addGap(gap, gap))).toArray(new MathObject[0]);
        MathObject[] toArray = new MathObject[objs.length];
        for (int i = 0; i < objs.length; i++) {
            toArray[i]=Shape.rectangle(objs[i].copy().addGap(gap, gap));
        }

        return new ContourHighlight(runTime, toArray);
    }

    public static ContourHighlight makeBBox(double runTime, double gap, Boxable... objs) {
//        MathObject[] toArray = Arrays.stream(objs).map(t -> Shape.rectangle(t.getBoundingBox().addGap(gap, gap))).toArray(new MathObject[0]);
         MathObject[] toArray = new MathObject[objs.length];
        for (int i = 0; i < objs.length; i++) {
            toArray[i]=Shape.rectangle(objs[i].getBoundingBox().addGap(gap, gap));
        }
        return new ContourHighlight(runTime, toArray);
    }

    /**
     * Creates an animation that hightlights the contour of a Shape object.
     *
     * @param runTime Runtime in seconds
     * @param objs Shapes to run the animation (varargs).
     */
    public ContourHighlight(double runTime, MathObject... objs) {
        super(runTime);
        subshapes = MultiShapeObject.make();
        this.objs = objs;
        highlightColor = JMColor.parse("red");
        this.thickness = 10;
        this.amplitude = .4;
        this.setDebugName("Contour highlight");
        setLambda(t -> t);
    }

    @Override
    public boolean doInitialization() {
        super.doInitialization();
        ArrayList<MathObject> toAnimateArrayList = new ArrayList<>();
        for (MathObject obj : objs) {
            if (obj instanceof MathObjectGroup) {
                toAnimateArrayList.add(Shape.rectangle(obj.getBoundingBox()));
            }

            if (obj instanceof Constructible) {
                if (obj instanceof CTPoint) {
                    Point p = ((CTPoint) obj).getMathObject();
                    double radius = scene.getRenderer().ThicknessToMathWidth(p.getMp().getThickness()) * 1;
                    toAnimateArrayList.add(Shape.circle().scale(radius).moveTo(p));
                } else {
                    toAnimateArrayList.add(((Constructible) obj).getMathObject());
                }
            } else {
                if (obj instanceof Point) {
                    Point p = (Point) obj;
                    double radius = scene.getRenderer().ThicknessToMathWidth(p.getMp().getThickness()) * 1;
                    toAnimateArrayList.add(Shape.circle().scale(radius).moveTo(p));
                } else {
                    toAnimateArrayList.add(obj);
                }
            }

        }

        this.objs = toAnimateArrayList.toArray(new MathObject[0]);
        return true;
    }

    @Override
    public void doAnim(double t) {
        super.doAnim(t);
        subshapes.getShapes().clear();

        if (t >= 1) {
            return;
        }
        double lt = getLT(t);
        if (lt == 0) {//Don't draw a single point
            return;
        }
        double b = UsefulLambdas.allocateTo(0, 1 - .5 * amplitude).applyAsDouble(lt);
        double a = UsefulLambdas.allocateTo(.5 * amplitude, 1).applyAsDouble(lt);
        for (MathObject obj : objs) {
            process(obj, a, b);

        }
    }

    private void process(MathObject obj, double a, double b) {
        if (obj instanceof Line) {
            Line line = ((Line) obj);
            addSubShapeToScene(line.toSegment(obj.getCamera()), a, b);
            return;
        }
        if (obj instanceof MultiShapeObject) {
            MultiShapeObject msh = ((MultiShapeObject) obj);
            for (Shape sh : msh) {
                addSubShapeToScene(sh, a, b);
            }
        }
//        if (obj instanceof MathObjectGroup) {
//            MathObjectGroup mg = ((MathObjectGroup) obj);
//            for (MathObject subObject : mg) {
//                process(subObject, a, b);
//            }
//        }
        if (obj instanceof Shape) {
            Shape sh = ((Shape) obj);
            addSubShapeToScene(sh, a, b);
        }

    }

    private void addSubShapeToScene(Shape sh, double a, double b) {
        Shape sub = sh.getSubShape(Math.min(a, b), Math.max(a, b));
        sub
                .style("default")
                .thickness(thickness)
                .fillAlpha(0)
                .drawColor(highlightColor);
        subshapes.add(sub);
    }

    /**
     * Sets the amplitude of the highlight, from 0 to 1. A value of .1 will draw
     * the 10% of the Shape. This is not the real 10%-length of shape, as this
     * dependes of the total of JMPathPoints in the path and the length of the
     * Bezier curves they determine
     *
     * @param amplitude Amplitude parameter, from 0 to 1.
     * @return This object.
     */
    public ContourHighlight setAmplitude(double amplitude) {
        this.amplitude = amplitude;
        return this;
    }

    /**
     * Sets the color of the highlight.
     *
     * @param color The color of the highlight.
     * @return
     */
    public ContourHighlight setColor(JMColor color) {
        this.highlightColor = color;
        return this;
    }

    /**
     * Overloaded method. Sets the color of the highlight.
     *
     * @param strColor A String object defining a color to be parsed
     * @return This object.
     */
    public ContourHighlight setColor(String strColor) {
        this.highlightColor = JMColor.parse(strColor);
        return this;
    }

    /**
     * Returns the thickness of the higlight
     *
     * @return The thickness
     */
    public double getThickness() {
        return thickness;
    }

    /**
     * Sets the thickness of the highlight
     *
     * @param thickness The desired thickness
     * @return This object.
     */
    public ContourHighlight setThickness(double thickness) {
        this.thickness = thickness;
        return this;
    }

    @Override
    public void cleanAnimationAt(double t) {
        removeObjectsFromScene(subshapes);
    }

    @Override
    public void prepareForAnim(double t) {
        addObjectsToscene(subshapes);
    }

    @Override
    public MultiShapeObject getIntermediateObject() {
        return subshapes;
    }
}
