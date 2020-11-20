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

import com.jmathanim.Animations.commands.Commands;
import com.jmathanim.mathobjects.LaTeXMathObject;
import com.jmathanim.mathobjects.Shape;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class TransformMathExpression extends Animation {

    public enum AddType {
        FADEIN, GROWIN
    }

    public enum RemoveType {
        FADEOUT, SHRINKOUT
    }

    private final LaTeXMathObject latexDestiny;
    private final LaTeXMathObject latexTransformed;
    private final AnimationGroup anim;
    private TransformFormulaParameters transformParameters;
    private final ArrayList<Integer> removeInOrig;
    private final ArrayList<Integer> addInDst;
    private final ArrayList<Shape> toDelete;
    private AddType addType = AddType.FADEIN;
    private RemoveType removeType = RemoveType.FADEOUT;

    public TransformMathExpression(double runTime, LaTeXMathObject latexTransformed, LaTeXMathObject latexDestiny) {
        super(runTime);
        this.latexTransformed = latexTransformed;
        this.latexDestiny = latexDestiny;
        anim = new AnimationGroup();
        transformParameters = new TransformFormulaParameters(this.latexTransformed.size(), this.latexDestiny.size());
        removeInOrig = new ArrayList<>();
        addInDst = new ArrayList<>();
        toDelete = new ArrayList<>();

        for (int n = 0; n < this.latexTransformed.size(); n++) {
            removeInOrig.add(n);
        }
        for (int n = 0; n < this.latexDestiny.size(); n++) {
            addInDst.add(n);
        }
    }

    public TransformFormulaParameters getTransformParameters() {
        return transformParameters;
    }

    public void setTransformParameters(TransformFormulaParameters tr) {
        this.transformParameters = tr;
    }

    @Override
    public void initialize() {
        scene.remove(latexTransformed);
        HashMap<String, int[]> or = transformParameters.getOrigGroups();
        HashMap<String, int[]> dst = transformParameters.getDstGroups();
        HashMap<String, String> maps = transformParameters.getMaps();
        for (String name1 : maps.keySet()) {
            String name2 = maps.get(name1);

            if (true) {
                Shape sh1 = getShapeForGroup(or, name1, latexTransformed, removeInOrig);
                Shape sh2 = getShapeForGroup(dst, name2, latexDestiny, addInDst);
                anim.add(new Transform(runTime, sh1, sh2));
                toDelete.add(sh1);
                toDelete.add(sh2);
            } else {
                //For each of the shapes of a origin group, makes a transform animation 
                //The destiny will be one merged shape of all shapes of destiny group
                for (Shape sh : getShapeListForGroup(or, name1, latexTransformed, removeInOrig)) {
                    Shape sh2 = getShapeForGroup(dst, name2, latexDestiny, addInDst);
                    anim.add(new Transform(runTime, sh, sh2));
                    toDelete.add(sh);
                    toDelete.add(sh2);
                }
            }
        }
        for (int n : removeInOrig) {
            Shape sh = latexTransformed.get(n);
            scene.add(sh);
            switch (removeType) {
                case FADEOUT:
                    anim.add(Commands.fadeOut(runTime, sh));
                    break;
                case SHRINKOUT:
                    anim.add(Commands.shrinkOut(runTime, sh));
                    break;
            }
        }
        for (int n : addInDst) {
            Shape sh = latexDestiny.get(n);
            switch (addType) {
                case FADEIN:
                    anim.add(Commands.fadeIn(runTime, sh));
                    break;
                case GROWIN:
                    anim.add(Commands.growIn(runTime, sh));
                    break;
            }
            toDelete.add(sh);
        }

        anim.initialize();
//        scene.add(mshOrig);
    }

    public ArrayList<Shape> getShapeListForGroup(HashMap<String, int[]> or, String names, LaTeXMathObject lat, ArrayList<Integer> listRemainders) {
        ArrayList<Shape> resul = new ArrayList<>();
        int[] gr = or.get(names);
        for (int n = 0; n < gr.length; n++) {
            resul.add(lat.get(gr[n]).copy());
            listRemainders.removeAll(Arrays.asList(gr[n]));
        }
        return resul;
    }

    public Shape getShapeForGroup(HashMap<String, int[]> or, String names, LaTeXMathObject lat, ArrayList<Integer> listRemainders) {
        int[] gr = or.get(names);
        Shape sh = lat.get(gr[0]).copy();
        listRemainders.removeAll(Arrays.asList(gr[0]));
        for (int n = 1; n < gr.length; n++) {
            sh.merge(lat.get(gr[n]).copy());
            listRemainders.removeAll(Arrays.asList(gr[n]));
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

    public AddType getAddType() {
        return addType;
    }

    public void setAddType(AddType addType) {
        this.addType = addType;
    }

    public RemoveType getRemoveType() {
        return removeType;
    }

    public void setRemoveType(RemoveType removeType) {
        this.removeType = removeType;
    }

    public void map(int i, int j) {
        transformParameters.map(i, j);
    }

    public void map(String name, int j) {
        transformParameters.map(name, j);
    }

    public void map(int i, String name) {
        transformParameters.map(i, name);
    }

    public String defineOrigGroup(String name, int... indices) {
        return transformParameters.defineOrigGroup(name, indices);
    }

    public String defineDstGroup(String name, int... indices) {
        return transformParameters.defineDstGroup(name, indices);
    }

    public void map(String gr1, String gr2) {
        transformParameters.map(gr1, gr2);
    }

    public void mapRange(int i1, int i2, int j) {
        transformParameters.mapRange(i1, i2, j);
    }

}
