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

import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.ResourceLoader;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import java.net.URL;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class ArrowTip extends TippableObject {

    MultiShapeObject arrowtip;
    private Point location;
    private Vec pointTo;
    private MultiShapeObject arrowtipDrawableCopy;
    int anchorValue;

    public ArrowTip(MultiShapeObject arrowtip) {
        this.arrowtip = arrowtip;
    }

    public ArrowTip(Arrow2D.ArrowType type) {
        this.arrowtip = buildArrowHead(type);
    }

    public MultiShapeObject getArrowtip() {
        return arrowtip;
    }

    public MultiShapeObject getArrowtipDrawableCopy() {
        return arrowtipDrawableCopy;
    }

    public final MultiShapeObject buildArrowHead(Arrow2D.ArrowType type) {
        SVGMathObject head = null;
        String name = "#arrow";

        double scaleDefaultValue;
        if (type != Arrow2D.ArrowType.NONE) {//If type=NONE, head=null
            switch (type) {//TODO: Improve this
                case TYPE_1:
                    name += "1";
                    anchorValue = 2;
                    scaleDefaultValue = 1.5;
                    break;
                case TYPE_2:
                    name += "2";
                    anchorValue = 7;
                    scaleDefaultValue = 1.5;
                    break;
                case TYPE_3:
                    name += "3";
                    anchorValue = 7;
                    scaleDefaultValue = 1;
                    break;
                default:
                    name += "1";
                    anchorValue = 2;
                    scaleDefaultValue = 1.5;
            }
//            if (side == 1) {
//                anchorPoint1 = anchorValue;
//                defaultArrowHead1Size1 *= scaleDefaultValue;
//            } else {
//                anchorPoint2 = anchorValue;
//                defaultArrowHead1Size2 *= scaleDefaultValue;
//            }

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

    public <T extends ArrowTip> T copy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateLocations(JMPathPoint jmp) {
        this.location = jmp.p;
        this.pointTo = jmp.p.to(jmp.cpExit);
        arrowtipDrawableCopy = arrowtip.copy();
        arrowtipDrawableCopy.setHeight(.1);
        //Shifting
        Point headPoint = this.arrowtipDrawableCopy.getBoundingBox().getUpper();
        this.arrowtipDrawableCopy.shift(headPoint.to(location));

        //Rotating
        Vec v = pointTo;
        double angle = v.getAngle();
        AffineJTransform tr = AffineJTransform.create2DRotationTransform(location, -Math.PI / 2 + angle);
        tr.applyTransform(arrowtipDrawableCopy);
    }

    @Override
    public void draw(Renderer r) {
        arrowtipDrawableCopy.draw(r);
    }

}
