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

/**
 * This class manages import from SVG files and converting them into multipath
 * objects
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class SVGMathObject extends AbstractMultiShapeObject<SVGMathObject,Shape> {

    protected String filename;

    Double anchorX = null;
    Double anchorY = null;

    JMPath importJMPathTemp;// Path to temporary import SVG Path commmands
//    private JMColor currentFillColor;
//    private JMColor currentDrawColor;
//    private double currentStrokeSize = .5d;
//
//  public static SVGMathObject make(String filename) {
//      ResourceLoader rl=new ResourceLoader();
//        URL url = rl.getResource(filename, "images");
//        return make(url);
//  }
  public static SVGMathObject make() {
      return new SVGMathObject();
  }


    protected SVGMathObject() {
        super(Shape.class);
    }
//
//    /**
//     * Creates a new SVGMathObject from the specified URL
//     * @param url A URL object pointing to a SVG file.
//     */
//    public static SVGMathObject make(URL url) {
//        SVGMathObject resul = new SVGMathObject();
//        try {
//            SVGUtils svgu = new SVGUtils(JMathAnimConfig.getConfig().getScene());
//            svgu.importSVG(url, resul);
//            resul.getMp().setAbsoluteThickness(false);
//        } catch (Exception ex) {
//            Logger.getLogger(SVGMathObject.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return resul;
//    }

    @Override
    public SVGMathObject copy() {
        SVGMathObject copy = SVGMathObject.make();
        copy.copyStateFrom(this);
        return copy;
    }

    @Override
    protected Shape createEmptyShapeAt(int index) {
        Shape sh = new Shape();
        shapes.add(index, sh);
        return sh;
    }

    @Override
    protected SVGMathObject makeNewEmptyInstance() {
        return SVGMathObject.make();
    }
}
