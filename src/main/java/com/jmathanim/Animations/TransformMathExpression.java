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

import com.jmathanim.Animations.Strategies.Transform.MultiShapeTransform;
import com.jmathanim.Animations.commands.Commands;
import com.jmathanim.Utils.MODrawProperties;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.LaTeXMathObject;
import com.jmathanim.mathobjects.MultiShapeObject;
import com.jmathanim.mathobjects.Shape;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import sun.security.util.ArrayUtil;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class TransformMathExpression extends Animation {

    private final LaTeXMathObject latexDestiny;
    private final LaTeXMathObject latexTransformed;
    private final MultiShapeObject mshDst;
    private final MultiShapeObject mshOrig;
    private AnimationGroup anim;
    private TransformFormulaParameters tr;

    public TransformMathExpression(double runTime, LaTeXMathObject latexTransformed, LaTeXMathObject latexDestiny) {
        super(runTime);
        this.latexTransformed = latexTransformed;
        this.latexDestiny = latexDestiny;
        this.mshOrig = new MultiShapeObject();
        this.mshDst = new MultiShapeObject();
    }

    public TransformFormulaParameters getTransformParameters() {
        return tr;
    }

    public void setTransformParameters(TransformFormulaParameters tr) {
        this.tr = tr;
    }

    @Override
    public void initialize() {
        HashMap<String, int[]> or = tr.getOrigGroups();
        HashMap<String, int[]> dst = tr.getDstGroups();
        HashMap<String, String> maps = tr.getMaps();
        for (String name1:maps.keySet())
        {
            String name2 = maps.get(name1);
            Shape sh1=getShapeForGroup(or, name1, latexTransformed);
            Shape sh2=getShapeForGroup(dst, name2, latexDestiny);
            scene.add(sh1);//needs to be removed too
            
        }

        anim.initialize();
        scene.add(mshOrig);
    }

    public Shape getShapeForGroup(HashMap<String, int[]> or, String names,LaTeXMathObject lat) {
        int[] gr = or.get(names);
        Shape sh = lat.get(gr[0]).copy();
        for (int n = 1; n < gr.length; n++) {
            sh.merge(lat.get(gr[n]).copy());
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
        scene.remove(mshDst);
        scene.add(latexDestiny);
    }

}
