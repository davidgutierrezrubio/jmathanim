/*
 * Copyright (C) 2021 David Gutierrez Rubio davidgutierrezrubio@gmail.com
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

import com.jmathanim.Utils.ResourceLoader;
import com.jmathanim.jmathanim.JMathAnimScene;
import java.net.URL;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class ArrowTip extends TippableObject {

   
    public static ArrowTip make(Shape shape, double location, slopeDirection dir, MathObject arrowtip) {
        ArrowTip resul = new ArrowTip(shape, location, dir);
        resul.setTip(arrowtip);
        return resul;
    }

    public static ArrowTip make(Shape shape, double location, slopeDirection dir, Arrow2D.ArrowType type) {
        ArrowTip resul = new ArrowTip(shape, location, dir);
        MultiShapeObject at = buildArrowHead(type);
        at.fillColor(shape.getMp().getDrawColor());
        at.drawColor(shape.getMp().getDrawColor());
        resul.setTip(at);
        return resul;
    }

    private ArrowTip(Shape shape, double location, slopeDirection dir) {
        setDirection(dir);
        setLocation(location);
        this.shape = shape;
    }

    public Shape getShape() {
        return shape;
    }

    private static MultiShapeObject buildArrowHead(Arrow2D.ArrowType type) {
        SVGMathObject head = null;
        String name = "#arrow";

        double scaleDefaultValue;
        if (type != Arrow2D.ArrowType.NONE) {//If type=NONE, head=null
            switch (type) {//TODO: Improve this
                case TYPE_1:
                    name += "1";
                    scaleDefaultValue = 1.5;
                    break;
                case TYPE_2:
                    name += "2";
                    scaleDefaultValue = 1.5;
                    break;
                case TYPE_3:
                    name += "3";
                    scaleDefaultValue = 1;
                    break;
                default:
                    name += "1";
                    scaleDefaultValue = 1.5;
            }
            name += ".svg";
            try {
//            baseFileName = outputDir.getCanonicalPath() + File.separator + "arrows" + File.separator + name;
                ResourceLoader rl = new ResourceLoader();
                URL arrowUrl = rl.getResource(name, "arrows");
                head = new SVGMathObject(arrowUrl);

            } catch (NullPointerException ex) {
                JMathAnimScene.logger.error("Arrow head " + name + " not found");
            }
        } else {
            head = new SVGMathObject();
        }
        return head;
    }

   
}
