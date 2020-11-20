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

import com.jmathanim.jmathanim.JMathAnimScene;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Manages the parameters to transform one math formula to another
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class TransformFormulaParameters {

    private int sizeOrig, sizeDst;
    private final HashMap<String, int[]> origGroups;
    private final HashMap<String, int[]> dstGroups;
    private final HashMap<String, String> maps;

    /**
     * Creates a new matrix representing the transformation from one latex
     * expression to another
     *
     * @param sizeOrig Number of elements of origin latex expression
     * @param sizeDst Number of elements of destination latex expression
     */
    public TransformFormulaParameters(int sizeOrig, int sizeDst) {
        this.sizeOrig = sizeOrig;
        this.sizeDst = sizeDst;
        origGroups = new HashMap<>();
        dstGroups = new HashMap<>();
        maps = new HashMap<>();
    }

    public void map(int i, int j) {
        map(defineOrigGroup("_" + i, i), defineDstGroup("_" + j, j));

    }

    public void map(String name, int j) {
        map(name, defineDstGroup("_" + j, j));
    }

    public void mapRange(int i1, int i2,int j) {
        for (int n = 0; n <=i2-i1; n++) {
            map(i1+n,j+n);
            
        }
    }
    
    public void map(int i, String name) {
        map(defineOrigGroup("_" + i, i), name);
    }

    public String defineOrigGroup(String name, int... indices) {

        for (int i : indices) {
            final String belongsToAnOrigGroup = belongsToAnOrigGroup(i);
            if (!"".equals(belongsToAnOrigGroup)) {
                JMathAnimScene.logger.error("Index " + i + " already belongs to a created group "+belongsToAnOrigGroup+". Weird results may occur.");
            }
        }
        origGroups.put(name, indices);
        return name;
    }

    public String defineDstGroup(String name, int... indices) {

        for (int i : indices) {
            final String belongsToADstGroup = belongsToADstGroup(i);
            if (!"".equals(belongsToADstGroup)) {
                JMathAnimScene.logger.error("Index " + i + " already belongs to a created group "+belongsToADstGroup+". Weird results may occur.");
            }
        }
        dstGroups.put(name, indices);
        return name;
    }

    public void map(String gr1, String gr2) {
        maps.put(gr1, gr2);

    }

    /**
     * Check if the given index belongs to a created group
     *
     * @param index The index to check
     * @return True if belongs to a group, false otherwise
     */
    public String belongsToAnOrigGroup(int index) {
        for (String p : origGroups.keySet()) {
            int[] li = origGroups.get(p);
            for (int n = 0; n < li.length; n++) {
                if (li[n]==index) {
                    return p;
                }
            }
        }
        return "";
    }

    public String belongsToADstGroup(int index) {
        for (String p : dstGroups.keySet()) {
            List<int[]> ar = Arrays.asList(dstGroups.get(p));
            if (ar.contains(index)) {
                return p;
            }
        }
        return "";
    }

    public int[][] createMatrix() {
        int[][] resul = new int[sizeDst][sizeOrig];

        for (String name1 : maps.keySet()) {
            String name2 = maps.get(name1);
            int[] gr1 = origGroups.get(name1);
            int[] gr2 = dstGroups.get(name2);

            for (int i : gr1) {
                for (int j : gr2) {
                    resul[j][i] = 1;
                }
            }

        }

        return resul;
    }

    @Override
    public String toString() {
        int[][] mat = createMatrix();
        String resul = "";
        for (int[] col : mat) {
            String rowStr = "";
            for (int cell : col) {
                rowStr += "" + cell;
            }
            resul += rowStr + "\n";
        }
        return resul;
    }

    public HashMap<String, int[]> getOrigGroups() {
        return origGroups;
    }

    public HashMap<String, int[]> getDstGroups() {
        return dstGroups;
    }

    public HashMap<String, String> getMaps() {
        return maps;
    }

}
