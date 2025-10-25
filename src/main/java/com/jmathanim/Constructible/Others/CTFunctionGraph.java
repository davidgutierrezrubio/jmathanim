/*
 * Copyright (C) 2023 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jmathanim.Constructible.Others;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Constructible.Constructible;
import com.jmathanim.MathObjects.FunctionGraph;
import com.jmathanim.MathObjects.MathObject;
import com.jmathanim.MathObjects.shouldUdpateWithCamera;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTFunctionGraph extends Constructible implements shouldUdpateWithCamera {

    FunctionGraph fg;
//
//    public static CTFunctionGraph make(String function) {
//        Rect r = JMathAnimConfig.getConfig().getCamera().getMathView();
//        CTFunctionGraph resul = new CTFunctionGraph(function, r.xmin, r.xmax);
//        resul.setDynamicRange(true);
//        resul.fg.updatePoints();
//        resul.rebuildShape();
//        return resul;
//    }
//
//    public static CTFunctionGraph make(String function, double xmin, double xmax) {
//        CTFunctionGraph resul = new CTFunctionGraph(function, xmin, xmax);
//        resul.setDynamicRange(false);
//        resul.fg.updatePoints();
//        resul.rebuildShape();
//        return resul;
//    }

//    private CTFunctionGraph(String function, double xmin, double xmax) {
//        fg = FunctionGraph.make(function, xmin, xmax);
//    }

    @Override
    public Constructible copy() {
        //TODO: Implement this
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public MathObject getMathObject() {
        return fg;
    }

    @Override
    public void rebuildShape() {
//        fg.updatePoints();
    }

    @Override
    public void updateWithCamera(Camera camera) {
        fg.updateWithCamera(camera);
    }

    public boolean isDynamicRange() {
        return fg.isDynamicRange();
    }

    public void setDynamicRange(boolean dynamicRange) {
        fg.setDynamicRange(dynamicRange);
    }

}
