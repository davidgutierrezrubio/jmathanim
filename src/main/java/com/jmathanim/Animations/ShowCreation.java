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

import com.jmathanim.Animations.Strategies.ShowCreation.*;
import com.jmathanim.Constructible.Constructible;
import com.jmathanim.Constructible.NullMathObject;
import com.jmathanim.Enum.ShowCreationStrategy;
import com.jmathanim.MathObjects.*;
import com.jmathanim.MathObjects.Axes.Axes;
import com.jmathanim.MathObjects.Delimiters.Arrow;
import com.jmathanim.MathObjects.Delimiters.Delimiter;
import com.jmathanim.MathObjects.Shapes.Line;
import com.jmathanim.MathObjects.Shapes.MultiShapeObject;
import com.jmathanim.MathObjects.Shapes.Ray;
import com.jmathanim.MathObjects.Text.LatexMathObject;
import com.jmathanim.MathObjects.Text.LatexShape;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.jmathanim.LogUtils;

import java.util.function.DoubleUnaryOperator;

/**
 * Animation that shows the creation of a MathObject. The precise strategy for creating depends on the type of
 * MathObject
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class ShowCreation extends Animation {

    protected final Vec[] pencilPosition;
    MathObject<?> mobj;//Mathobject that will be created
    MathObject<?> origObj;//Original constructible object, in case
    private Animation creationStrategy;
    private ShowCreationStrategy strategyType = ShowCreationStrategy.NONE;

    /**
     * Creates an animation that shows the creation of the specified MathObject.
     *
     * @param runtime Run time in seconds
     * @param mobj    Mathobject to animate
     */
    public ShowCreation(double runtime, MathObject<?> mobj) {
        super(runtime);
        setDebugName("showCreation");
        pencilPosition = new Vec[]{Vec.to(0,0), Vec.to(0,0)};
        this.mobj=mobj;
        this.origObj=mobj;
    }

    /**
     * Static constructor. Creates an animation that shows the creation of the specified MathObject.
     *
     * @param runtime Run time in seconds
     * @param mobj    Mathobject to animate
     * @return The animation ready to play with playAnim method
     */
    public static ShowCreation make(double runtime, MathObject<?> mobj) {
        if (mobj == null) {
            return null;
        }
        return new ShowCreation(runtime, mobj);
    }

    private boolean extractMathObjectToCreate(MathObject<?> mobj) {
        if (mobj instanceof NullMathObject) {
            this.mobj = mobj;
            addThisAtTheEnd.add(origObj);
            return true;
        }
        if (mobj instanceof Delimiter) {
            this.mobj = mobj;
            addThisAtTheEnd.add(origObj);
            return true;
        }
        if (mobj instanceof Arrow) {
            this.mobj = mobj;
            addThisAtTheEnd.add(origObj);
            return true;
        }
        if (mobj instanceof Constructible<?>) {
            this.mobj = ((Constructible<?>) mobj).getMathObject();
            removeThisAtTheEnd.add(this.mobj);
            addThisAtTheEnd.add(origObj);
            return false;//Needs further inspecting
        }

        if (mobj instanceof RigidBox) {
            this.mobj = ((RigidBox) mobj).getMathObjectCopyToDraw();
            removeObjectsFromScene(origObj);
            removeThisAtTheEnd.add(this.mobj);
            addThisAtTheEnd.add(origObj);
            return false;//Needs further inspecting
        }
        this.mobj = mobj;
        addThisAtTheEnd.add(origObj);
        return true;
    }

    private AbstractMultiShapeObject<?,?> convertToMultiShapeObject(MathObject<?> mobj){
        if (mobj instanceof Shape) {
            Shape shape = (Shape) mobj;
            return MultiShapeObject.make(shape);
        }
        if (mobj instanceof LatexShape) {
            LatexShape shape = (LatexShape) mobj;
            return LatexMathObject.make(shape);
        }
        return null;
    }


    @Override
    public boolean doInitialization() {
        super.doInitialization();
        //First, extract MathObjects if passed object is a container (RigidBox, Constructible, etc.)
        while (!extractMathObjectToCreate(this.mobj));
        try {
            if (strategyType == ShowCreationStrategy.NONE) {
                determineCreationStrategy(this.mobj);
            }
            createStrategy();
            creationStrategy.setLambda(getTotalLambda());
            creationStrategy.setAddObjectsToScene(this.isShouldAddObjectsToScene());
            creationStrategy.setShouldInterpolateStyles(this.isShouldInterpolateStyles());
            creationStrategy.setUseObjectState(this.isUseObjectState());
            creationStrategy.initialize(scene);
        } catch (NullPointerException | ClassCastException e) {
            JMathAnimScene.logger.error("Couldn't create ShowCreation strategy for "
                    + LogUtils.method(this.mobj.getClass().getSimpleName()) + ". No animation will be done.");
        }
        return true;
    }

    @Override
    public void doAnim(double t) {
        super.doAnim(t);
        if (creationStrategy == null) return;
        creationStrategy.doAnim(t);
        try {
            if (creationStrategy instanceof CreationStrategy) {
                CreationStrategy cs = (CreationStrategy) creationStrategy;
                pencilPosition[0].copyCoordinatesFrom(cs.getPencilPosition()[0]);
                pencilPosition[1].copyCoordinatesFrom(cs.getPencilPosition()[1]);
            }
        } catch (java.lang.NullPointerException e) {
            //do nothing
        }
    }

    //    @Override
//    public boolean processAnimation() {
//        if ((creationStrategy != null)) {
//            boolean ret = creationStrategy.processAnimation();
//
//            return ret;
//        } else {
//            return true;
//        }
//    }
    @Override
    public void finishAnimation() {
        if (creationStrategy != null) {
            creationStrategy.finishAnimation();
        }
        super.finishAnimation();

    }

    /**
     * Determines the strategy to animate the creation of the object
     *
     * @param mobj MathObject which will be animated. Its type determines the type of animation to perform.
     */
    private void determineCreationStrategy(MathObject<?> mobj) {

        if (mobj instanceof Point) {
            this.strategyType = ShowCreationStrategy.POINT_CREATION;
            return;
        }
        if (mobj instanceof Axes) {
            this.strategyType = ShowCreationStrategy.AXES_CREATION;
            return;
        }
        if (mobj instanceof MathObjectGroup) {
            this.strategyType = ShowCreationStrategy.GROUP_CREATION;
            return;
        }
        if (mobj instanceof LatexMathObject) {
            this.strategyType = ShowCreationStrategy.LATEX_CREATION;
            return;
        }
        if (mobj instanceof SVGMathObject) {
            this.strategyType = ShowCreationStrategy.FIRST_DRAW_AND_THEN_FILL;
            return;
        }
        if (origObj instanceof Arrow) {
            this.strategyType = ShowCreationStrategy.ARROW_CREATION;
            return;
        }
        if (mobj instanceof Delimiter) {
            this.strategyType = ShowCreationStrategy.DELIMITER_CREATION;
            return;
        }
        if (mobj instanceof MultiShapeObject) {
            this.strategyType = ShowCreationStrategy.MULTISHAPE_CREATION;
            return;
        }
        if (mobj instanceof Line) {
            this.strategyType = ShowCreationStrategy.LINE_CREATION;
            return;
        }
        if (mobj instanceof Ray) {
            this.strategyType = ShowCreationStrategy.RAY_CREATION;
            return;
        }
        if (mobj instanceof FunctionGraph) {
            this.strategyType = ShowCreationStrategy.SIMPLE_SHAPE_CREATION;
            return;
        }
        if (mobj instanceof AbstractShape<?>) {
//            this.strategyType = ShowCreationStrategy.SIMPLE_SHAPE_CREATION;
            this.strategyType = ShowCreationStrategy.FIRST_DRAW_AND_THEN_FILL;
            return;
        }

    }

    /**
     * Sets the animation strategy
     *
     * @param <T>          This class
     * @param strategyType Strategy, chosen from enum ShowCreationStrategy
     * @return This object
     */
    public <T extends ShowCreation> T setStrategy(ShowCreationStrategy strategyType) {
        this.strategyType = strategyType;
        return (T) this;
    }

    /**
     * Creates the strategy object
     *
     * @throws ClassCastException If the current object cannot be cast to the required class.
     */
    private void createStrategy() throws ClassCastException {
        switch (this.strategyType) {
            case POINT_CREATION:
                creationStrategy = Commands.fadeIn(this.runTime, origObj);
                removeThisAtTheEnd.remove(this.mobj);
                break;
            case GROUP_CREATION:
                creationStrategy = new GroupCreationAnimation(this.runTime, (MathObjectGroup) mobj);
                JMathAnimScene.logger.debug("ShowCreation method: "+ LogUtils.method("GroupCreationStrategy"));
                break;
            case LINE_CREATION:
//                final Shape lineToCreate = ((Line) mobj).toSegment(mobj.getCamera());
//                removeThisAtTheEnd.add(lineToCreate);
                creationStrategy = new LineCreationAnimation(this.runTime, (Line) mobj);
                JMathAnimScene.logger.debug("ShowCreation method: "+ LogUtils.method("LineCreationStrategy"));
                break;
            case RAY_CREATION:
//                final Shape rayToCreate = ((Ray) mobj).toSegment(mobj.getCamera());
//                removeThisAtTheEnd.add(rayToCreate);
                creationStrategy = new SimpleShapeCreationAnimation(this.runTime, (Ray) mobj);
                JMathAnimScene.logger.debug("ShowCreation method: "+ LogUtils.method("RayCreationStrategy"));
                break;
            case ARROW_CREATION:
                creationStrategy = new ArrowCreationAnimation(this.runTime, (Arrow) origObj);
                JMathAnimScene.logger.debug("ShowCreation method: "+ LogUtils.method("ArrowCreationStrategy"));
                break;
            case DELIMITER_CREATION:
                Delimiter del = (Delimiter) mobj;
                creationStrategy = new AbstractCreationStrategy(runTime) {
                    @Override
                    public MathObject<?> getIntermediateObject() {
                        return del;
                    }

                    @Override
                    public boolean doInitialization() {
                        return super.doInitialization();
                    }

                    @Override
                    public void doAnim(double t) {
                        super.doAnim(t);
                        del.setAmplitudeScale(getTotalLambda().applyAsDouble(t));
                    }

                    @Override
                    public void cleanAnimationAt(double t) {
                        double lt = getLT(t);
                        if (lt == 0) {//Ended at t=0, nothing remains...
                            removeObjectsFromScene(del);
                            return;
                        }
                    }

                    @Override
                    public void prepareForAnim(double t) {
                        addObjectsToscene(del);
                    }
                };
                JMathAnimScene.logger.debug("ShowCreation method: "+ LogUtils.method("Delimiter (growIn)"));

                break;

            case SIMPLE_SHAPE_CREATION:
                creationStrategy = new SimpleShapeCreationAnimation(runTime, (AbstractShape<?>) mobj);
                JMathAnimScene.logger.debug("ShowCreation method: "+ LogUtils.method("SimpleShapeCreationStrategy"));

                break;
            case MULTISHAPE_CREATION:
                MultiShapeObject msh = (MultiShapeObject) mobj;
                removeThisAtTheEnd.addAll(msh.getShapes());
                addThisAtTheEnd.add(mobj);
                creationStrategy = new FirstDrawThenFillAnimation(runTime, msh);
                JMathAnimScene.logger.debug("ShowCreation method: "+ LogUtils.method("MultiShapeCreationStrategy"));
                break;
            case FIRST_DRAW_AND_THEN_FILL:
                creationStrategy = new FirstDrawThenFillAnimation(runTime, (hasShapes) mobj);
                JMathAnimScene.logger.debug("ShowCreation method: "+ LogUtils.method("FirstDrawThenFillStrategy"));
                break;
            case LATEX_CREATION:
                LatexMathObject lat = (LatexMathObject) mobj;
                removeThisAtTheEnd.addAll(lat.getShapes());
                addThisAtTheEnd.add(mobj);
                creationStrategy = new FirstDrawThenFillAnimation(runTime, lat);
                JMathAnimScene.logger.debug("ShowCreation method: "+ LogUtils.method("FirstDrawThenFillStrategy (LaTeXMathObject)"));
                break;
            case AXES_CREATION:
                creationStrategy = new AxesCreationAnimation(runTime, (Axes) mobj);
//            case NONE:
//                creationStrategy = Commands.fadeIn(runTime, mobj);
//                JMathAnimScene.logger.warn("Couldn't create strategy for ShowCreation method: Will use FadeIn instead");
            default:
                break;
        }
    }

    /**
     * Sets the strategy used to create the object
     *
     * @param <T>          Calling subclass
     * @param strategyType Strategy type. A value from the enum ShowCreationStrategy
     * @return This object
     */
    public <T extends ShowCreation> T setStrategyType(ShowCreationStrategy strategyType) {
        this.strategyType = strategyType;
        return (T) this;
    }

    /**
     * Returns a reference to the "pencil" position.
     *
     * @return An array with 2 point objects. The 0 index stores the previous position of the pencil and 1 stores the
     * current direction
     */
    public Vec[] getPencilPosition() {
        return pencilPosition;

    }

    @Override
    public <T extends Animation> T setLambda(DoubleUnaryOperator lambda) {
        super.setLambda(lambda);
        if (creationStrategy == null) return (T) this;
        creationStrategy.setLambda(lambda);
        return (T) this;
    }

    @Override
    public void cleanAnimationAt(double t) {
        if (creationStrategy == null) return;
        creationStrategy.cleanAnimationAt(t);
        removeObjectsFromScene(removeThisAtTheEnd);
    }

    @Override
    public void prepareForAnim(double t) {
        if (creationStrategy == null) return;
        creationStrategy.prepareForAnim(t);
    }

    @Override
    public MathObject<?> getIntermediateObject() {
        if (creationStrategy == null) return NullMathObject.make();
        return creationStrategy.getIntermediateObject();
    }

    @Override
    public void reset() {
        super.reset();
        if (getStatus() != Status.NOT_INITIALIZED) {
            //This is to prevent calling the next line when the strategy is null
            if (creationStrategy == null) return;
            creationStrategy.reset();
        }
    }



}
