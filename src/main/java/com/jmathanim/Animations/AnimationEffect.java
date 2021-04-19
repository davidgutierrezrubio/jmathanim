/*
 * Copyright (C) 2021 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
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

import com.jmathanim.Utils.AffineJTransform;
import static com.jmathanim.jmathanim.JMathAnimScene.PI;
import com.jmathanim.mathobjects.FunctionGraph;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.SVGMathObject;
import com.jmathanim.mathobjects.Shape;
import java.util.HashMap;

/**
 * This class holds and manages information about animation effects
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class AnimationEffect {

    protected Double jumpHeight;
    protected Integer numTurns;
    protected Double alphaScaleEffect;
    protected Double scaleEffect;
    private final HashMap<MathObject, JMPath> jumpPaths;

    public AnimationEffect() {
        this.jumpHeight = null;
        this.numTurns = null;
        this.scaleEffect = null;
        this.alphaScaleEffect = null;
        jumpType = null;
        this.jumpPaths = new HashMap<>();
    }

    public enum JumpType {
        SEMICIRCLE, PARABOLICAL, ELLIPTICAL, TRIANGULAR, FOLIUM, SINUSOIDAL2, SINUSOIDAL, CRANE
    }
    JumpType jumpType;

    public void addJumpEffect(double height) {
        this.jumpHeight = height;
        jumpType = JumpType.PARABOLICAL;
    }

    public void addJumpEffect(double height, JumpType type) {
        this.jumpHeight = height;
        jumpType = type;
    }

    public void addRotationEffect(int numTurns) {
        this.numTurns = numTurns;
    }

    public void addAlphaEffect(double alphaScale) {
        this.alphaScaleEffect = alphaScale;
    }

    public void addScaleEffect(double scale) {
        this.scaleEffect = scale;
    }

    protected void applyScaleEffect(double t, MathObject obj) {
        if ((scaleEffect != null) && (scaleEffect != 1)) {
            double L = 4 * (1 - scaleEffect);
            double scalelt = 1 - t * (1 - t) * L;
            obj.scale(scalelt);
        }
    }

    protected void applyRotationEffect(double t, MathObject obj) {
        if ((numTurns != null) && (numTurns != 0)) {
            double rotateAngle = 2 * PI * numTurns;
            obj.rotate(rotateAngle * t);
        }
    }

    protected void applyAlphaScaleEffect(double t, MathObject obj) {
        if ((alphaScaleEffect != null) && (alphaScaleEffect != 1)) {
            double L = 4 * (1 - alphaScaleEffect);
            double alphaScalelt = 1 - t * (1 - t) * L;
            obj.drawAlpha(alphaScalelt);
            obj.fillAlpha(alphaScalelt);
        }
    }

    protected void prepareJumpPath(Point A, Point B, MathObject obj) {
        if ((jumpHeight == null) || (jumpHeight == 0) || A.to(B).norm() == 0) {
            return;
        }
        double dist = A.to(B).norm();
        Shape jumpPath = null;
        switch (jumpType) {
            case SEMICIRCLE:
                jumpPath = Shape.arc(PI).scale(1, Math.signum(jumpHeight));
                jumpPath.getPath().reverse();
                break;
            case ELLIPTICAL:
                jumpPath = Shape.arc(PI).scale(.5);//.scale(1, 2 * jumpHeight / dist);
                jumpPath.getPath().reverse();
                break;
            case TRIANGULAR:
                jumpPath = Shape.polyLine(
                        Point.origin(),
                        Point.at(.7, .7),
                        Point.at(1, 1),
                        Point.at(1.3, .7),
                        Point.at(2, 0));
                break;
            case FOLIUM:
                jumpPath = SVGMathObject.make("#foliumJumpPath.svg").get(0).scale(1, -1);
                break;
            case PARABOLICAL:
                jumpPath = new Shape(FunctionGraph.make(t -> 4 * t * (1 - t), 0, 1).getPath());
                break;
            case SINUSOIDAL:
                jumpPath = new Shape(FunctionGraph.make(t -> Math.sin(PI * t), 0, 1).getPath());
                break;
            case SINUSOIDAL2:
//                jumpPath = new Shape(FunctionGraph.make(t -> 10.39230484541326*t*(1-t)*(1-2*t), 0, 1).getPath());
                jumpPath = new Shape(FunctionGraph.make(t -> Math.sin(2 * PI * t), 0, 1).getPath());
                break;
            case CRANE:
                jumpPath = Shape.polyLine(
                        Point.origin(),
                        Point.at(0, .7),
                        Point.at(0, 1),
                        Point.at(.3, 1),
                        Point.at(.7, 1),
                        Point.at(1, 1),
                        Point.at(1, .7),
                        Point.at(1, 0));
                break;
        }

        if (jumpPath != null) {
            if (jumpType != JumpType.SEMICIRCLE) {
                jumpPath.scale(1, jumpPath.getWidth() * jumpHeight / (jumpPath.getHeight() * dist));
            }
            if (jumpType==JumpType.ELLIPTICAL) {
                jumpPath.scale(1,1.25);
            }
            AffineJTransform.createDirect2DHomothecy(jumpPath.getPoint(0), jumpPath.getPoint(-1), A, B, 1).applyTransform(jumpPath);
            jumpPaths.put(obj, jumpPath.getPath());
        }
    }

    protected void applyJumpEffect(double t, MathObject obj) {
        if (jumpPaths.containsKey(obj)) {
            obj.moveTo(jumpPaths.get(obj).getPointAt(t).p);
        }

    }

    protected void applyAnimationEffects(double lt, MathObject obj) {
        applyJumpEffect(lt, obj);
        applyScaleEffect(lt, obj);
        applyRotationEffect(lt, obj);
        applyAlphaScaleEffect(lt, obj);
    }

    public void copyEffectParametersFrom(AnimationEffect obj) {
        if (obj.jumpHeight != null) {
            this.addJumpEffect(obj.jumpHeight, obj.jumpType);
        }
        if (obj.alphaScaleEffect != null) {
            this.addAlphaEffect(obj.alphaScaleEffect);
        }
        if (obj.numTurns != null) {
            this.addRotationEffect(obj.numTurns);
        }
        if (obj.scaleEffect != null) {
            this.addScaleEffect(obj.scaleEffect);
        }
    }
}