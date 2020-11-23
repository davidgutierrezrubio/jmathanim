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
import com.jmathanim.Animations.Commands;
import com.jmathanim.Animations.Transform;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import static com.jmathanim.jmathanim.JMathAnimScene.PI;
import com.jmathanim.mathobjects.LaTeXMathObject;
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

    public enum AddType {
        FADE_IN, GROW_IN,
        MOVE_IN_UP, MOVE_IN_LEFT,
        MOVE_IN_RIGHT, MOVE_IN_DOWN
    }

    public enum RemoveType {
        FADE_OUT, SHRINK_OUT,
        MOVE_OUT_UP, MOVE_OUT_LEFT,
        MOVE_OUT_RIGHT, MOVE_OUT_DOWN
    }

    private final LaTeXMathObject latexDestiny;
    private final LaTeXMathObject latexTransformed;
    private final AnimationGroup anim;

    private final ArrayList<Shape> toDelete;

    //Transformation parameters
    private final int trParSizeOrig;
    private final int trParSizeDst;
    private final HashMap<String, int[]> trParOrigGroups;
    private final HashMap<String, int[]> trParDstGroups;
    private final HashMap<String, String> trParMaps;
    private final HashMap<String, TransformMathExpressionParameters> trParTransformParameters;
    private final HashMap<Integer, TransformMathExpressionParameters> removeInOrig;
    private final HashMap<Integer, TransformMathExpressionParameters> addInDst;

    public TransformMathExpression(double runTime, LaTeXMathObject latexTransformed, LaTeXMathObject latexDestiny) {
        super(runTime);
        this.latexTransformed = latexTransformed;
        this.latexDestiny = latexDestiny;
        anim = new AnimationGroup();
//        transformParameters = new TransformFormulaParameters(this.latexTransformed.size(), this.latexDestiny.size());
        toDelete = new ArrayList<>();

        this.trParSizeOrig = this.latexTransformed.size();
        this.trParSizeDst = this.latexDestiny.size();
        trParOrigGroups = new HashMap<>();
        trParDstGroups = new HashMap<>();
        trParMaps = new HashMap<>();
        trParTransformParameters = new HashMap<>();
        removeInOrig = new HashMap<>();
        addInDst = new HashMap<>();
        for (int n = 0; n < this.latexTransformed.size(); n++) {
            removeInOrig.put(n, new TransformMathExpressionParameters());
        }
        for (int n = 0; n < this.latexDestiny.size(); n++) {
            addInDst.put(n, new TransformMathExpressionParameters());
        }
    }

    @Override
    public void initialize() {
        scene.remove(latexTransformed);
        HashMap<String, int[]> or = trParOrigGroups;
        HashMap<String, int[]> dst = trParDstGroups;
        HashMap<String, String> maps = trParMaps;
        for (String name1 : maps.keySet()) {
            String name2 = maps.get(name1);

            if (true) {
                Shape sh1 = getShapeForGroup(or, name1, latexTransformed, removeInOrig);
                Shape sh2 = getShapeForGroup(dst, name2, latexDestiny, addInDst);
                createTransformSubAnimation(sh1, sh2, trParTransformParameters.get(name1));
            } else {
                //For each of the shapes of a origin group, makes a transform animation 
                //The destiny will be one merged shape of all shapes of destiny group
                for (Shape sh : getShapeListForGroup(or, name1, latexTransformed, removeInOrig)) {
                    Shape sh2 = getShapeForGroup(dst, name2, latexDestiny, addInDst);
                    createTransformSubAnimation(sh, sh2, trParTransformParameters.get(name1));
                }
            }
        }
        for (int n : removeInOrig.keySet()) {
            createRemovingSubAnimation(n, removeInOrig.get(n));
        }
        for (int n : addInDst.keySet()) {
            Shape sh = latexDestiny.get(n);
            createAddingSubAnimation(sh, addInDst.get(n));
        }

        anim.initialize();
//        scene.add(mshOrig);
    }

    private void createRemovingSubAnimation(int n, TransformMathExpressionParameters par) {
        Shape sh = latexTransformed.get(n);
        scene.add(sh);
        switch (par.getRemovingStyle()) {
            case FADE_OUT:
                anim.add(Commands.fadeOut(runTime, sh).setLambda(t -> Math.sqrt(t)));
                break;
            case SHRINK_OUT:
                anim.add(Commands.shrinkOut(runTime, sh));
                break;
            case MOVE_OUT_UP:
                anim.add(Commands.moveOut(runTime, Anchor.Type.UPPER, sh));
                break;
            case MOVE_OUT_LEFT:
                anim.add(Commands.moveOut(runTime, Anchor.Type.LEFT, sh));
                break;
            case MOVE_OUT_RIGHT:
                anim.add(Commands.moveOut(runTime, Anchor.Type.RIGHT, sh));
                break;
            case MOVE_OUT_DOWN:
                anim.add(Commands.moveOut(runTime, Anchor.Type.LOWER, sh));
                break;
        }
    }

    private void createAddingSubAnimation(Shape sh, TransformMathExpressionParameters par) {
        switch (par.getAddingStyle()) {
            case FADE_IN:
                anim.add(Commands.fadeIn(runTime, sh));
                break;
            case GROW_IN:
                anim.add(Commands.growIn(runTime, sh));
                break;
            case MOVE_IN_UP:
                anim.add(Commands.moveIn(runTime, Anchor.Type.UPPER, sh));
                break;
            case MOVE_IN_LEFT:
                anim.add(Commands.moveIn(runTime, Anchor.Type.LEFT, sh));
                break;
            case MOVE_IN_RIGHT:
                anim.add(Commands.moveIn(runTime, Anchor.Type.RIGHT, sh));
                break;
            case MOVE_IN_DOWN:
                anim.add(Commands.moveIn(runTime, Anchor.Type.LOWER, sh));
                break;
        }
        toDelete.add(sh);
    }

    private void createTransformSubAnimation(Shape sh, Shape sh2, TransformMathExpressionParameters par) {
        final Transform transform = new Transform(runTime, sh, sh2);

        AnimationGroup group = new AnimationGroup(transform);

        if (par.getJumpHeight() != 0) {
            Vec v = sh.getCenter().to(sh2.getCenter());
            Vec shiftVector = Vec.to(-v.y, v.x).normalize().mult(par.getJumpHeight());

            final Animation radiusShift = Commands.shift(runTime, shiftVector, sh);
            radiusShift.setLambda(t -> Math.sin(PI * t));
            radiusShift.setUseObjectState(false);
            group.add(radiusShift);
        }
        if (par.getNumTurns() != 0) {
            Animation rotation = Commands.rotate(runTime, 2 * PI * par.getNumTurns(), sh);
            rotation.setUseObjectState(false);
            group.add(rotation);
        }
        if (par.getAlphaMult() != 1) {
            double L = 4 * (1 - par.getAlphaMult());
            Animation changeAlpha = new Animation(runTime) {
                @Override
                public void initialize() {
                }

                @Override
                public void doAnim(double t) {
                    double lt = 1 - t * (1 - t) * L;
                    sh.fillAlpha(lt * sh.mp.getFillColor().alpha);
                    sh.drawAlpha(lt * sh.mp.getDrawColor().alpha);
                }

                @Override
                public void finishAnimation() {
                    doAnim(1);
                }
            };
            group.add(changeAlpha);
        }
        if (par.getScale() != 1) {
            double L = 4 * (1 - par.getScale());
            Animation changeScale = new Animation(runTime) {
                @Override
                public void initialize() {
                }

                @Override
                public void doAnim(double t) {
                    double lt = 1 - t * (1 - t) * L;
                    sh.scale(lt);
                    System.out.println(lt);
                }

                @Override
                public void finishAnimation() {
                    doAnim(1);
                }
            };
            group.add(changeScale);
        }

        anim.add(group);//, radius, rota));
        toDelete.add(sh);
        toDelete.add(sh2);
    }

    private ArrayList<Shape> getShapeListForGroup(HashMap<String, int[]> or, String names, LaTeXMathObject lat, HashMap<Integer, TransformMathExpressionParameters> listRemainders) {
        ArrayList<Shape> resul = new ArrayList<>();
        int[] gr = or.get(names);
        for (int n = 0; n < gr.length; n++) {
            resul.add(lat.get(gr[n]).copy());
            listRemainders.remove(gr[n]);
        }
        return resul;
    }

    private Shape getShapeForGroup(HashMap<String, int[]> or, String names, LaTeXMathObject lat, HashMap<Integer, TransformMathExpressionParameters> listRemainders) {
        int[] gr = or.get(names);
        Shape sh = lat.get(gr[0]).copy();
        listRemainders.remove(gr[0]);
        for (int n = 1; n < gr.length; n++) {
            sh.merge(lat.get(gr[n]).copy());
            listRemainders.remove(gr[n]);
        }
        return sh;
    }

    @Override
    public boolean processAnimation() {
        return anim.processAnimation(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doAnim(double t) {
    }

    @Override
    public void finishAnimation() {
        anim.finishAnimation();
//        scene.remove(mshDst);
        scene.add(latexDestiny);
        for (Shape sh : toDelete) {
            scene.remove(sh);
        }
    }

    public TransformMathExpressionParameters map(int i, int j) {
        return map(defineOrigGroup("_" + i, i), defineDstGroup("_" + j, j));

    }

    public TransformMathExpressionParameters map(String name, int j) {
        return map(name, defineDstGroup("_" + j, j));
    }

    public TransformMathExpressionParametersArray mapRange(int i1, int i2, int j) {
        TransformMathExpressionParametersArray ar = new TransformMathExpressionParametersArray();
        for (int n = 0; n <= i2 - i1; n++) {
            final int ind = i1 + n;
            map(ind, j + n);
            ar.add(trParTransformParameters.get("_" + ind));

        }
        return ar;
    }

//    public TransformMathExpressionParameters getOrigTransformParameters(int n) {
//        return removeInOrig.get(n);
//    }
//
//    public TransformMathExpressionParameters getDstTransformParameters(int n) {
//        return addInDst.get(n);
//    }
    public TransformMathExpressionParameters map(int i, String name) {
        return map(defineOrigGroup("_" + i, i), name);
    }

    public TransformMathExpressionParameters map(String gr1, String gr2) {
        trParMaps.put(gr1, gr2);
        TransformMathExpressionParameters par = new TransformMathExpressionParameters();
        trParTransformParameters.put(gr1, par);
        return par;

    }

    public void setRemovingStyle(RemoveType type, int... indices) {
        for (int i : indices) {
            if (removeInOrig.containsKey(i)) {
                removeInOrig.get(i).setRemovingStyle(type);
            }
        }
    }

    public void setAddingStyle(AddType type, int... indices) {
        for (int i : indices) {
            if (addInDst.containsKey(i)) {
                addInDst.get(i).setAddingStyle(type);
            }
        }
    }

    public String defineOrigGroup(String name, int... indices) {

        for (int i : indices) {
            final String belongsToAnOrigGroup = belongsToAnOrigGroup(i);
            if (!"".equals(belongsToAnOrigGroup)) {
                JMathAnimScene.logger.error("Index " + i + " already belongs to a created group " + belongsToAnOrigGroup + ". Weird results may occur.");
            }
        }
        trParOrigGroups.put(name, indices);
        return name;
    }

    public String defineDstGroup(String name, int... indices) {

        for (int i : indices) {
            final String belongsToADstGroup = belongsToADstGroup(i);
            if (!"".equals(belongsToADstGroup)) {
                JMathAnimScene.logger.error("Index " + i + " already belongs to a created group " + belongsToADstGroup + ". Weird results may occur.");
            }
        }
        trParDstGroups.put(name, indices);
        return name;
    }

    public String belongsToAnOrigGroup(int index) {
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

    public String belongsToADstGroup(int index) {
        for (String p : trParDstGroups.keySet()) {
            List<int[]> ar = Arrays.asList(trParDstGroups.get(p));
            if (ar.contains(index)) {
                return p;
            }
        }
        return "";
    }

    public int[][] createMatrix() {
        int[][] resul = new int[trParSizeDst][trParSizeOrig];

        for (String name1 : trParMaps.keySet()) {
            String name2 = trParMaps.get(name1);
            int[] gr1 = trParOrigGroups.get(name1);
            int[] gr2 = trParDstGroups.get(name2);

            for (int i : gr1) {
                for (int j : gr2) {
                    resul[j][i] = 1;
                }
            }

        }

        return resul;
    }
}
