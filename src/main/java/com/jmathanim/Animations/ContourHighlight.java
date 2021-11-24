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

import com.jmathanim.Styling.JMColor;
import com.jmathanim.Utils.UsefulLambdas;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Arrow2D;
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MathObjectGroup;
import com.jmathanim.mathobjects.MultiShapeObject;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David
 */
public class ContourHighlight extends Animation {

    MathObject[] objs;
    JMColor highlightColor;
    double thickness;
    double amplitude;

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

    /**
     * Creates an animation that hightlights the contour of a Shape object.
     *
     * @param runTime Runtime in seconds
     * @param objs Shapes to run the animation (varargs).
     */
    public ContourHighlight(double runTime, MathObject... objs) {
        super(runTime);
        this.objs = objs;
        highlightColor = JMColor.parse("red");
        this.thickness = 10;
        this.amplitude = .1;
//        setLambda(t -> t);
    }

    @Override
    public void initialize(JMathAnimScene scene) {
        super.initialize(scene);
    }

    @Override
    public void doAnim(double t) {
        if (t >= 1) {
            return;
        }
        double lt = getLambda().applyAsDouble(t);
        double b = UsefulLambdas.allocateTo(0, 1 - amplitude).applyAsDouble(lt);
        double a = UsefulLambdas.allocateTo(amplitude, 1).applyAsDouble(lt);
        System.out.println("a=" + a + ", b" + b);
        for (MathObject obj : objs) {
            process(obj, a, b);

        }
    }

    private void process(MathObject obj, double a, double b) {
        if (obj instanceof Line) {
            Line line = ((Line) obj);
            addSubShapeToScene(line.toSegment(scene.getCamera()), a, b);
            return;
        }
        if (obj instanceof Arrow2D) {
            Arrow2D ar = ((Arrow2D) obj);
            addSubShapeToScene(ar.getBody(), a, b);
            return;
        }
        if (obj instanceof MultiShapeObject) {
            MultiShapeObject msh = ((MultiShapeObject) obj);
            for (Shape sh : msh) {
                addSubShapeToScene(sh, a, b);
            }
        }
        if (obj instanceof MathObjectGroup) {
            MathObjectGroup mg = ((MathObjectGroup) obj);
            for (MathObject subObject : mg) {
                process(subObject, a, b);
            }
        }
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
        scene.addOnce(sub);
    }

    /**
     * Sets the amplitude of the highlight, from 0 to 1. A value of .1 will draw
     * the 10% of the Shape.
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

}
