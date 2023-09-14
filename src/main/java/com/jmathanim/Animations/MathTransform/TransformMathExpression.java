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
package com.jmathanim.Animations.MathTransform;

import com.jmathanim.Animations.Animation;
import com.jmathanim.Animations.AnimationGroup;
import com.jmathanim.Animations.AnimationWithEffects;
import com.jmathanim.Animations.Commands;
import com.jmathanim.Animations.FlipTransform;
import com.jmathanim.Animations.Transform;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.OrientationType;
import com.jmathanim.jmathanim.JMathAnimScene;
import static com.jmathanim.jmathanim.JMathAnimScene.PI;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MathObjectGroup;
import com.jmathanim.mathobjects.MultiShapeObject;
import com.jmathanim.mathobjects.Shape;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class TransformMathExpression extends Animation {

    private RemoveType defaultRemovingStyle;
    private AddType defaultAddingStyle;
    private final MultiShapeObject latexTransformedOrig;
    private final MultiShapeObject latexIntermediate;

    /**
     * Sets the default removing style. Any item that has not defined its
     * removing style will use this. Style by default is SHRINK_OUT.
     *
     * @param defaultRemovingStyle
     */
    public void setDefaultRemovingStyle(RemoveType defaultRemovingStyle) {
        this.defaultRemovingStyle = defaultRemovingStyle;
    }

    /**
     * Sets the default adding style. Any item that has not defined its removing
     * style will use this. Style by default is GROW_IN.
     *
     * @param defaultAddingStyle
     */
    public void setDefaultAddingStyle(AddType defaultAddingStyle) {
        this.defaultAddingStyle = defaultAddingStyle;
    }

    public enum AddType {
        FADE_IN, GROW_IN, GROWH_IN, GROWV_IN, MOVE_IN_UP, MOVE_IN_LEFT, MOVE_IN_RIGHT, MOVE_IN_DOWN
    }

    public enum RemoveType {
        FADE_OUT, SHRINK_OUT, SHRINKH_OUT, SHRINKV_OUT, MOVE_OUT_UP, MOVE_OUT_LEFT, MOVE_OUT_RIGHT, MOVE_OUT_DOWN
    }

    public enum TransformType {
        INTERPOLATION, FLIP_HORIZONTALLY, FLIP_VERTICALLY, FLIP_BOTH
    }

    private final MultiShapeObject latexDestiny;
    private final MultiShapeObject latexTransformedBase;
    private final AnimationGroup animations;

    private final ArrayList<Shape> toDelete;

    // Transformation parameters
    private final HashMap<String, int[]> trParOrigGroups;
    private final HashMap<String, int[]> trParDstGroups;
    private final HashMap<String, String> trParMaps;
    private final HashMap<String, TransformMathExpressionParameters> trParTransformParameters;
    private final HashMap<Integer, TransformMathExpressionParameters> removeInOrigParameters;
    private final HashMap<Integer, TransformMathExpressionParameters> addInDstParameters;

    /**
     * Static builder. Creates a new animation that transforms a math expression
     * into another. Fine-tuning of the animation can be donde with the map,
     * mapRange, defineDstGroup and defineOrigGroup commands.
     *
     * @param runTime Time in seconds
     * @param latexTransformed Original math expression
     * @param latexDestiny Destiny math expression
     */
    public static TransformMathExpression make(double runTime, MultiShapeObject latexTransformed, MultiShapeObject latexDestiny) {
        return new TransformMathExpression(runTime, latexTransformed, latexDestiny);
    }

    /**
     * Creates a new animation that transforms a math expression into another.
     * Fine-tuning of the animation can be donde with the map, mapRange,
     * defineDstGroup and defineOrigGroup commands.
     *
     * @param runTime Time in seconds
     * @param latexTransformedOrig Original math expression
     * @param latexDestiny Destiny math expression
     */
    public TransformMathExpression(double runTime, MultiShapeObject latexTransformedOrig, MultiShapeObject latexDestiny) {
        super(runTime);
        this.latexTransformedBase = MultiShapeObject.make();
        this.latexTransformedOrig = latexTransformedOrig;
        this.latexIntermediate = MultiShapeObject.make();
        this.latexDestiny = latexDestiny;
        animations = new AnimationGroup();
        toDelete = new ArrayList<>();
        trParOrigGroups = new HashMap<>();
        trParDstGroups = new HashMap<>();
        trParMaps = new HashMap<>();
        trParTransformParameters = new HashMap<>();
        removeInOrigParameters = new HashMap<>();
        addInDstParameters = new HashMap<>();
    }

    @Override
    public boolean doInitialization() {
        super.doInitialization();
        this.latexTransformedBase.copyStateFrom(latexTransformedOrig);

        defaultAddingStyle = AddType.GROW_IN;
        defaultRemovingStyle = RemoveType.SHRINK_OUT;

        for (int n = 0; n < this.latexTransformedBase.size(); n++) {
            removeInOrigParameters.put(n, new TransformMathExpressionParameters());
        }
        for (int n = 0; n < this.latexDestiny.size(); n++) {
            addInDstParameters.put(n, new TransformMathExpressionParameters());
        }

        HashMap<String, int[]> or = trParOrigGroups;
        HashMap<String, int[]> dst = trParDstGroups;
        HashMap<String, String> maps = trParMaps;
        for (String name1 : maps.keySet()) {
            String name2 = maps.get(name1);

//            if (false) {
//                Shape sh1 = getShapeForGroup(or, name1, latexTransformed, removeInOrigParameters);
//                Shape sh2 = getShapeForGroup(dst, name2, latexDestiny, addInDstParameters);
//                if ((sh1.size() > 0) && (sh2.size() > 0)) {// If any of the shapes is empty...abort!
//                    createTransformSubAnimation(sh1, sh2, trParTransformParameters.get(name1));
//                }
//            } else {
            // For each of the shapes of a origin group, makes a transform animation
            // The destiny will be one merged shape of all shapes of destiny group
            ArrayList<Shape> listDst = getShapeListForGroup(dst, name2, latexDestiny, addInDstParameters);
            ArrayList<Shape> listOrig = getShapeListForGroup(or, name1, latexTransformedBase, removeInOrigParameters);

            int nDst = listDst.size();
            int nOrig = listOrig.size();
            if (nDst < nOrig) {
                for (Shape sh : listOrig) {
                    Shape sh2 = getShapeForGroup(dst, name2, latexDestiny, addInDstParameters);
                    createTransformSubAnimation(sh, sh2, trParTransformParameters.get(name1));
                }
            } else {
                for (int k = 0; k < nOrig; k++) {
                    Shape sh1 = listOrig.get(k);
                    Shape sh2 = listDst.get(k);
                    createTransformSubAnimation(sh1, sh2, trParTransformParameters.get(name1));
                }
                //If destiny has more shapes than origin, make copies of the last one of orig and map

                for (int k = 0; k < nDst - nOrig; k++) {
                    Shape sh1 = listOrig.get(nOrig - 1);
                    Shape sh2 = listDst.get(nOrig + k);
                    createTransformSubAnimation(sh1, sh2, trParTransformParameters.get(name1));
                }
            }

//            }
        }
        for (int n : removeInOrigParameters.keySet()) {
            createRemovingSubAnimation(n, removeInOrigParameters.get(n));
        }
        for (int n : addInDstParameters.keySet()) {
            Shape sh = latexDestiny.get(n);
            createAddingSubAnimation(sh, addInDstParameters.get(n));
        }

        return animations.initialize(scene);
    }

    private void createRemovingSubAnimation(int n, TransformMathExpressionParameters par) {
        Shape sh = latexTransformedBase.get(n);
        addObjectsToscene(sh);
        AnimationGroup group = new AnimationGroup();
        if (par.getRemovingStyle() == null) {
            par.setRemovingStyle(defaultRemovingStyle);
        }

        switch (par.getRemovingStyle()) {
            case FADE_OUT:
                group.add(Commands.fadeOut(runTime, sh).setLambda(x -> Math.sqrt(x)));
                break;
            case SHRINK_OUT:
                group.add(Commands.shrinkOut(runTime, sh));
                break;
            case SHRINKH_OUT:
                group.add(Commands.shrinkOut(runTime, 0, OrientationType.HORIZONTAL, sh));
                break;
            case SHRINKV_OUT:
                group.add(Commands.shrinkOut(runTime, 0, OrientationType.VERTICAL, sh));
                break;
            case MOVE_OUT_UP:
                group.add(Commands.moveOut(runTime, Anchor.Type.UPPER, sh).setLambda(getTotalLambda()));
                break;
            case MOVE_OUT_LEFT:
                group.add(Commands.moveOut(runTime, Anchor.Type.LEFT, sh).setLambda(getTotalLambda()));
                break;
            case MOVE_OUT_RIGHT:
                group.add(Commands.moveOut(runTime, Anchor.Type.RIGHT, sh).setLambda(getTotalLambda()));
                break;
            case MOVE_OUT_DOWN:
                group.add(Commands.moveOut(runTime, Anchor.Type.LOWER, sh).setLambda(getTotalLambda()));
                break;
        }
        if (par.getNumTurns() != 0) {
            Animation rotation = Commands.rotate(runTime, 2 * PI * par.getNumTurns(), sh);
            rotation.setUseObjectState(false);
            group.add(rotation);
        }

        animations.add(group);
    }

    private void createAddingSubAnimation(Shape sh, TransformMathExpressionParameters par) {
        AnimationGroup group = new AnimationGroup();

        if (par.getAddingStyle() == null) {
            par.setAddingStyle(defaultAddingStyle);
        }

        switch (par.getAddingStyle()) {
            case FADE_IN:
                animations.add(Commands.fadeIn(runTime, sh).setLambda(getTotalLambda()));
                break;
            case GROW_IN:
                animations.add(Commands.growIn(runTime, sh).setLambda(getTotalLambda()));
                break;
            case GROWH_IN:
                animations.add(Commands.growIn(runTime, 0, OrientationType.HORIZONTAL, sh).setLambda(getTotalLambda()));
                break;
            case GROWV_IN:
                animations.add(Commands.growIn(runTime, 0, OrientationType.VERTICAL, sh).setLambda(getTotalLambda()));
                break;

            case MOVE_IN_UP:
                animations.add(Commands.moveIn(runTime, Anchor.Type.UPPER, sh).setLambda(getTotalLambda()));
                break;
            case MOVE_IN_LEFT:
                animations.add(Commands.moveIn(runTime, Anchor.Type.LEFT, sh).setLambda(getTotalLambda()));
                break;
            case MOVE_IN_RIGHT:
                animations.add(Commands.moveIn(runTime, Anchor.Type.RIGHT, sh).setLambda(getTotalLambda()));
                break;
            case MOVE_IN_DOWN:
                animations.add(Commands.moveIn(runTime, Anchor.Type.LOWER, sh).setLambda(getTotalLambda()));
                break;
        }
        if (par.getNumTurns() != 0) {
            Animation rotation = Commands.rotate(runTime, 2 * PI * par.getNumTurns(), sh);
            rotation.setUseObjectState(false);
            group.add(rotation);
        }
        animations.add(group);
        toDelete.add(sh);
    }

    private void createTransformSubAnimation(Shape sh, Shape sh2, TransformMathExpressionParameters par) {
        AnimationWithEffects transform = null;
        switch (par.getTransformStyle()) {
            case INTERPOLATION:
                transform = new Transform(runTime, sh, sh2);
                break;
            case FLIP_HORIZONTALLY:
                transform = new FlipTransform(runTime, OrientationType.HORIZONTAL, sh, sh2);
                break;
            case FLIP_VERTICALLY:
                transform = new FlipTransform(runTime, OrientationType.VERTICAL, sh, sh2);
                break;
            case FLIP_BOTH:
                transform = new FlipTransform(runTime, OrientationType.BOTH, sh, sh2);
                break;
        }
        transform.setLambda(getTotalLambda());
//        AnimationGroup group = new AnimationGroup(transform);

        if (par.getJumpHeightFromJumpEffect() != 0) {
//            Vec v = sh.getCenter().to(sh2.getCenter());
//            group.add(par.createJumpAnimation(runTime, v, sh));
            transform.addJumpEffect(par.getJumpHeightFromJumpEffect(), par.getJumptype());
        }
        if (par.getNumTurns() != 0) {
//            group.add(par.createRotateAnimation(runTime, sh));
            transform.addRotationEffect(par.getNumTurns());
            transform.setLambda(t -> t);
        }
        if (par.getAlphaMultFromAlphaEffect() != 1) {
//            Animation changeAlpha = par.createAlphaMultAnimation(runTime, sh);
//            group.add(changeAlpha);
            transform.addAlphaEffect(par.getAlphaMultFromAlphaEffect());
        }
        if (par.getScaleFromScaleEffect() != 1) {
//            group.add(par.createScaleAnimation(runTime, sh));
            transform.addScaleEffect(par.getScaleFromScaleEffect());
        }

//        anim.add(group);//, radius, rota));
        animations.add(transform);
        toDelete.add(sh);
        toDelete.add(sh2);
    }

    private ArrayList<Shape> getShapeListForGroup(HashMap<String, int[]> or, String names, MultiShapeObject lat,
            HashMap<Integer, TransformMathExpressionParameters> listRemainders) {
        ArrayList<Shape> resul = new ArrayList<>();
        int[] gr = or.get(names);
        for (int n = 0; n < gr.length; n++) {
            resul.add(lat.get(gr[n]));
//            resul.add(lat.get(gr[n]).copy());
            listRemainders.remove(gr[n]);
        }
        return resul;
    }

    private Shape getShapeForGroup(HashMap<String, int[]> or, String names, MultiShapeObject lat,
            HashMap<Integer, TransformMathExpressionParameters> listRemainders) {
        int[] gr = or.get(names);
        Shape sh = lat.get(gr[0]);
//        Shape sh = lat.get(gr[0]).copy();
        listRemainders.remove(gr[0]);
        for (int n = 1; n < gr.length; n++) {
            sh.merge(lat.get(gr[n]), false, false);
//            sh.merge(lat.get(gr[n]).copy(), false, false);
            listRemainders.remove(gr[n]);
        }
        return sh;
    }

    @Override
    public void doAnim(double t) {
        super.doAnim(t);
        animations.doAnim(t);
    }

    @Override
    public void finishAnimation() {
        animations.finishAnimation();
        super.finishAnimation();

        for (Shape sh : toDelete) {
            removeObjectsFromScene(sh);
        }
//        addObjectsToscene(latexDestiny);
    }

    /**
     * Maps ith-element of original formula to the jth-element destiny formula.
     * Current transform will be done via the Transform method, interpolating
     * point by point. It will return an associated
     * TransformMathExpressionParameters instance so that effects can be added
     * to the transform.
     *
     * @param i ith-element of original formula
     * @param j jth-element of the destiny formula.
     * @return Associated transform parameter, to add effects.
     */
    public TransformMathExpressionParameters map(int i, int j) {
        return map(defineOrigGroup("_" + i, i), defineDstGroup("_" + j, j));

    }

    /**
     * Overloaded method. Maps the origin group defined with the given name to
     * the jth-element of destiny formula.
     *
     * @param name Name of the origin group previously defined
     * @param j jth-element of the destiny formula.
     * @return Associated transform parameter, to add effects.
     */
    public TransformMathExpressionParameters map(String name, int j) {
        return map(name, defineDstGroup("_" + j, j));
    }

    /**
     * Maps i1,i1+1,i1+2,...,i2 to j,j+1,j+2,...j+(i2-i1). Returns a
     * TransformMathExpressionParametersArray to add uniformly the same effects
     * to all maps created
     *
     * @param i1 First origin index to map (included)
     * @param i2 Last origin index to map (included)
     * @param j First destiny index to map
     * @return Associated array transform parameter, to add effects.
     */
    public TransformMathExpressionParametersArray mapRange(int i1, int i2, int j) {
        TransformMathExpressionParametersArray ar = new TransformMathExpressionParametersArray();
        for (int n = 0; n <= i2 - i1; n++) {
            final int ind = i1 + n;
            map(ind, j + n);
            ar.add(trParTransformParameters.get("_" + ind));
        }
        return ar;
    }

    /**
     * Map all index from origin to destiny. For this to work, destiny size has
     * to be greater or equal than origin size.
     *
     * @return Associated array transform parameter, to add effects.
     */
    public TransformMathExpressionParametersArray mapAll() {
        int n = Math.min(latexDestiny.size() - 1, latexTransformedBase.size() - 1);
        return mapRange(0, n, 0);
    }

    /**
     * Overloaded method. Maps the origin group defined with the given name to
     * the jth-element of destiny formula.
     *
     * @param i ith-element of original formula
     * @param name Name of the destiny group previously defined
     * @return Associated transform parameter, to add effects.
     */
    public TransformMathExpressionParameters map(int i, String name) {
        return map(defineOrigGroup("_" + i, i), name);
    }

    /**
     * Overloaded method.Maps the origin group defined with the given name to
     * the specified destiny group.
     *
     * @param origName Name of the origin group previously defined
     * @param dstName Name of the destiny group previously defined
     * @return Associated transform parameter, to add effects.
     */
    public TransformMathExpressionParameters map(String origName, String dstName) {
        trParMaps.put(origName, dstName);
        TransformMathExpressionParameters par = new TransformMathExpressionParameters();
        trParTransformParameters.put(origName, par);
        return par;
    }

    /**
     * Sets the removing style for the specified origin indices marked for
     * removal
     *
     * @param type One of the enum RemoveType: FADE_OUT, SHRINK_OUT,
     * MOVE_OUT_UP, MOVE_OUT_LEFT, MOVE_OUT_RIGHT, MOVE_OUT_DOWN
     * @param indices varargs origin indices
     * @return Array of transform parameters
     */
    public TransformMathExpressionParametersArray setRemovingStyle(RemoveType type, int... indices) {
        TransformMathExpressionParametersArray ar = new TransformMathExpressionParametersArray();
        for (int i : indices) {
            if (removeInOrigParameters.containsKey(i)) {
                removeInOrigParameters.get(i).setRemovingStyle(type);
                ar.add(removeInOrigParameters.get(i));
            }
        }
        return ar;
    }

    /**
     * Sets the adding style for the specified destiny indices marked for adding
     *
     * @param type One of the enum AddTyp: FADE_IN, GROW_IN, MOVE_IN_UP,
     * MOVE_IN_LEFT, MOVE_IN_RIGHT, MOVE_IN_DOWN
     * @param indices varargs origin indices
     * @return Array of transform parameters
     */
    public TransformMathExpressionParametersArray setAddingStyle(AddType type, int... indices) {
        TransformMathExpressionParametersArray ar = new TransformMathExpressionParametersArray();
        for (int i : indices) {
            if (addInDstParameters.containsKey(i)) {
                addInDstParameters.get(i).setAddingStyle(type);
                ar.add(addInDstParameters.get(i));
            }
        }
        return ar;
    }

    /**
     * Define a group of origin indices
     *
     * @param name Name of the group
     * @param indices varargs of origin indices
     * @return The name of the group created
     */
    public String defineOrigGroup(String name, int... indices) {

        for (int i : indices) {
            final String belongsToAnOrigGroup = belongsToAnOrigGroup(i);
            if (!"".equals(belongsToAnOrigGroup)) {
                JMathAnimScene.logger.error("Index " + i + " already belongs to a created group " + belongsToAnOrigGroup
                        + ". Weird results may occur.");
            }
        }
        trParOrigGroups.put(name, indices);
        return name;
    }

    /**
     * Define a group of destiny indices
     *
     * @param name Name of the group
     * @param indices varargs of destiny indices
     * @return The name of the group created
     */
    public String defineDstGroup(String name, int... indices) {

        for (int i : indices) {
            final String belongsToADstGroup = belongsToADstGroup(i);
            if (!"".equals(belongsToADstGroup)) {
                JMathAnimScene.logger.error("Index " + i + " already belongs to a created group " + belongsToADstGroup
                        + ". Weird results may occur.");
            }
        }
        trParDstGroups.put(name, indices);
        return name;
    }

    private String belongsToAnOrigGroup(int index) {
        for (String p : trParOrigGroups.keySet()) {
            int[] li = trParOrigGroups.get(p);
            for (int n = 0; n < li.length; n++) {
                if (li[n] == index) {
                    return p;
                }
            }
        }
        return "";
    }

    private String belongsToADstGroup(int index) {
        for (String p : trParDstGroups.keySet()) {
            List<int[]> ar = Arrays.asList(trParDstGroups.get(p));
            if (ar.contains(index)) {
                return p;
            }
        }
        return "";
    }

    @Override
    public void cleanAnimationAt(double t) {
        double lt = getLT(t);
        animations.cleanAnimationAt(t);
        if (lt == 0) {
            removeObjectsFromScene(latexDestiny, latexTransformedBase);
            removeObjectsFromScene(animations.getIntermediateObject());
            addObjectsToscene(latexTransformedOrig);
            return;
        }
        if (lt == 1) {
            MathObjectGroup a = animations.getIntermediateObject();
            removeObjectsFromScene(latexTransformedOrig, latexTransformedBase);
            removeObjectsFromScene(animations.getIntermediateObject());
            addObjectsToscene(latexDestiny);
            return;
        }
        removeObjectsFromScene(latexTransformedOrig, latexTransformedBase, latexDestiny);
        addObjectsToscene(animations.getIntermediateObject());
    }

    @Override
    public void prepareForAnim(double t) {
        animations.prepareForAnim(t);
        removeObjectsFromScene(latexDestiny, latexTransformedBase, latexTransformedOrig);
        addObjectsToscene(animations.getIntermediateObject());
    }

    @Override
    public MathObject getIntermediateObject() {
        return null;
    }

    @Override
    public void reset() {
        super.reset();
        animations.reset();
    }
}
