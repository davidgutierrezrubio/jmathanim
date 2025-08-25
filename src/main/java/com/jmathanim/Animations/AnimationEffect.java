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

import com.jmathanim.Enum.JumpType;
import com.jmathanim.Styling.Stylable;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.ResourceLoader;
import com.jmathanim.Utils.UsefulLambdas;
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.*;

import java.util.HashMap;

import static com.jmathanim.jmathanim.JMathAnimScene.PI;

/**
 * This class holds and manages information about animation effects
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class AnimationEffect {

    private final HashMap<AffineTransformable<?>, JMPath> jumpPaths;
    /**
     * Height of the jump, a negative one can be specified
     */
    protected Double jumpHeight;
    /**
     * Number of turns of the rotation effect. A negative number means clockwise
     */
    protected Integer numTurns;
    /**
     * Alpha scale effect parameter. 1 means no effect.
     */
    protected Double alphaScaleEffect;
    /**
     * Scale parameter. 1 means no effect
     */
    protected Double scaleEffect;
    /**
     * Type of jump
     */


    JumpType jumpType;

    /**
     * Creates a new AnimationEffect. This object stores effect parameters for those animation who support effects.
     */
    public AnimationEffect() {
        this.jumpHeight = null;
        this.numTurns = null;
        this.scaleEffect = null;
        this.alphaScaleEffect = null;
        jumpType = null;
        this.jumpPaths = new HashMap<>();
    }

    /**
     * Adds a jump effect with a parabolical path and given height. Note that the direction of the jump is the vector
     * start-end rotated 90 degrees counterclockwise.
     *
     * @param height The height of the jump, in math coordinates. A negative height can be passed as parameter.
     */
    public void addJumpEffect(double height) {
        this.jumpHeight = height;
        jumpType = JumpType.PARABOLICAL;
    }

    /**
     * Adds a jump effect with a given path and given height. Note that the direction of the jump is the vector
     * start-end rotated 90 degrees counterclockwise.
     *
     * @param height The height of the jump, in math coordinates. A negative height can be passed as parameter.
     * @param type   Type of jump path. A value of enum JumpType
     */
    public void addJumpEffect(double height, JumpType type) {
        this.jumpHeight = height;
        jumpType = type;
    }

    /**
     * Adds a rotation effect to the animation, rotating the animated objects a specified number of turns.
     *
     * @param numTurns Number of turns. If positive, the turns are counterclockwise. If negative, clockwise.
     */
    public void addRotationEffect(int numTurns) {
        this.numTurns = numTurns;
    }

    /**
     * Adds an alpha effect to the animated objects.
     *
     * @param alphaScale The alpha scale to apply. For example a value of 0.5 will set the alpha of animated objects to
     *                   50% at t=0.5 and return to the previous values at the end of the animation.
     */
    public void addAlphaEffect(double alphaScale) {
        this.alphaScaleEffect = alphaScale;
    }

    /**
     * Adds a scale effect to the animated objects
     *
     * @param scale The scale to apply. For example a value of 2 will scale by 2 all objects at t=0.5 and return to the
     *              previous values at the end of the animation.
     */
    public void addScaleEffect(double scale) {
        this.scaleEffect = scale;
    }

    protected void applyScaleEffect(double t, AffineTransformable<?> obj) {
        if ((scaleEffect != null) && (scaleEffect != 1)) {
            double L = 4 * (1 - scaleEffect);
            double scalelt = 1 - t * (1 - t) * L;
            obj.scale(scalelt);
        }
    }

    protected void applyRotationEffect(double t, AffineTransformable<?> obj) {
        if ((numTurns != null) && (numTurns != 0)) {
            double rotateAngle = 2 * PI * numTurns;
            obj.rotate(rotateAngle * t);
        }
    }

    protected void applyAlphaScaleEffect(double t, Stylable<?> obj) {
        if ((alphaScaleEffect != null) && (alphaScaleEffect != 1)) {
            double L = 4 * (1 - alphaScaleEffect);
            double alphaScalelt = 1 - t * (1 - t) * L;
            obj.drawAlpha(alphaScalelt);
            obj.fillAlpha(alphaScalelt);
        }
    }

    protected void prepareJumpPath(Coordinates<?> A, Coordinates<?> B, AffineTransformable<?> obj) {
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
                jumpPath = Shape.arc(PI).scale(.5);// .scale(1, 2 * jumpHeight / dist);
                jumpPath.getPath().reverse();
                break;
            case TRIANGULAR:
                jumpPath = Shape.polyLine(Point.origin(), Vec.to(.7, .7), Vec.to(1, 1), Vec.to(1.3, .7),
                        Vec.to(2, 0));
                break;
            case FOLIUM:
                ResourceLoader rl = new ResourceLoader();
                jumpPath = SVGMathObject.make("#foliumJumpPath.svg").get(0).scale(1, -1);
                break;
            case PARABOLICAL:
                jumpPath = new Shape(FunctionGraph.make(t -> 4 * t * (1 - t), 0, 1, 2).getPath());
                break;
            case SINUSOIDAL:
                jumpPath = new Shape(FunctionGraph.make(t -> Math.sin(PI * t), 0, 1, 2).getPath());
                break;
            case SINUSOIDAL2:
//                jumpPath = new Shape(FunctionGraph.make(t -> 10.39230484541326*t*(1-t)*(1-2*t), 0, 1).getPath());
                jumpPath = new Shape(FunctionGraph.make(t -> Math.sin(2 * PI * t), 0, 1, 3).getPath());
                break;
            case CRANE:
                jumpPath = Shape.polyLine(Point.origin(), Vec.to(0, .7), Vec.to(0, 1), Vec.to(.3, 1), Vec.to(.7, 1),
                        Vec.to(1, 1), Vec.to(1, .7), Vec.to(1, 0));
                break;
            case BOUNCE1:
                jumpPath = new Shape(FunctionGraph.make(UsefulLambdas.backAndForthBounce1(), 0, 1).getPath());
                break;
            case BOUNCE2:
                jumpPath = new Shape(FunctionGraph.make(UsefulLambdas.backAndForthBounce2(), 0, 1).getPath());
                break;
        }

        if (jumpPath != null) {
            if (jumpType != JumpType.SEMICIRCLE) {
                jumpPath.scale(1, jumpPath.getWidth() * jumpHeight / (jumpPath.getHeight() * dist));
            }
            if (jumpType == JumpType.ELLIPTICAL) {
                jumpPath.scale(1, 1.25);
            }
            AffineJTransform.createDirect2DIsomorphic(jumpPath.getPoint(0), jumpPath.getPoint(-1), A, B, 1)
                    .applyTransform(jumpPath);
            jumpPaths.put(obj, jumpPath.getPath());
        }
    }

    protected void applyJumpEffect(double t, AffineTransformable<?> obj) {
        if (jumpPaths.containsKey(obj)) {
            obj.moveTo(jumpPaths.get(obj).getParametrizedPointAt(t));
//            obj.moveTo(jumpPaths.get(obj).getJMPointAt(t).p);
        }

    }

    protected void applyAnimationEffects(double lt, AffineTransformable<?> obj) {
        applyJumpEffect(lt, obj);
        applyScaleEffect(lt, obj);
        applyRotationEffect(lt, obj);
        if (obj instanceof Stylable)
            applyAlphaScaleEffect(lt, (Stylable<?>) obj);
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
