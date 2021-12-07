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
package com.jmathanim.mathobjects;

import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.ResourceLoader;
import com.jmathanim.Utils.SVGUtils;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class manages import from SVG files and converting them into multipath
 * objects
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class SVGMathObject extends MultiShapeObject {

    protected String filename;

    Double anchorX = null;
    Double anchorY = null;

    JMPath importJMPathTemp;// Path to temporary import SVG Path commmands
//    private JMColor currentFillColor;
//    private JMColor currentDrawColor;
//    private double currentStrokeSize = .5d;

    public static SVGMathObject make(String fname) {
        return new SVGMathObject(fname);
    }

    // This empty constructor is needed
    public SVGMathObject() {
        super();
    }

    public SVGMathObject(String fname) {
        super();
        //This is the default style for SVG objects
        fillColor("black");
        drawColor("black");
        double defaultThickness = scene.getRenderer().MathWidthToThickness(1);//Default thickness
        getMp().setThickness(defaultThickness);
        
        ResourceLoader rl = new ResourceLoader();
        URL urlImage = rl.getResource(fname, "images");
        try {
            SVGUtils svgu = new SVGUtils(scene);
            svgu.importSVG(urlImage, this);
        } catch (Exception ex) {
            Logger.getLogger(SVGMathObject.class.getName()).log(Level.SEVERE, null, ex);
        }
        getMp().setAbsoluteThickness(false);// Default behaviour

        stackTo(new Point(0, 0), Anchor.Type.UL);
    }

    public SVGMathObject(URL url) {
        super();
        try {
            SVGUtils svgu = new SVGUtils(scene);
            svgu.importSVG(url, this);
        } catch (Exception ex) {
            Logger.getLogger(SVGMathObject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public SVGMathObject copy() {
        SVGMathObject resul = new SVGMathObject();
        for (Shape sh : shapes) {
            final Shape copy = sh.copy();
            resul.add(copy);
        }
        resul.getMp().copyFrom(getMp());
        resul.absoluteSize = this.absoluteSize;
        return resul;
    }
}
