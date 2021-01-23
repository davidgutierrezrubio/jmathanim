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

    private Point pointLoc;
    private Vec pointTo;
    private MathObject arrowtip, arrowtipDrawableCopy;
    int anchorValue;
    private slopeDirection direction;
    private Shape shape;
    private double location;

    public enum slopeDirection {
        NEGATIVE, POSITIVE
    };

//    public static ArrowTip make(Shape shape, double location, slopeDirection dir, MathObject arrowTip) {
//        return make(shape, location, dir, MultiShapeObject.make(arrowTip));
//    }

    public static ArrowTip make(Shape shape, double location, slopeDirection dir, MathObject arrowtip) {
        ArrowTip resul = new ArrowTip(shape, location, dir);
        resul.setTipShape(arrowtip);
        return resul;
    }

    public static ArrowTip make(Shape shape, double location, slopeDirection dir, Arrow2D.ArrowType type) {
        ArrowTip resul = new ArrowTip(shape, location, dir);
        MultiShapeObject at = buildArrowHead(type);
        at.fillColor(shape.mp.getDrawColor());
        at.drawColor(shape.mp.getDrawColor());
        resul.setTipShape(at);
        return resul;
    }

    private ArrowTip(Shape shape, double location, slopeDirection dir) {
        this.direction = dir;
        this.location = location;
        this.shape = shape;
    }

    public Shape getShape() {
        return shape;
    }

    public void setTipShape(MathObject tipShape) {
        arrowtip = tipShape;
    }

    public MathObject getTipShape() {
        return arrowtip;
    }

    public MathObject getTipShapeToDraw() {
        return arrowtipDrawableCopy;
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

    @Override
    public void updateLocations(JMPathPoint jmp) {
        this.pointLoc = jmp.p;

        arrowtipDrawableCopy = arrowtip.copy();
        arrowtipDrawableCopy.setHeight(.1);
        //Shifting
        Point headPoint = this.arrowtipDrawableCopy.getBoundingBox().getUpper();
        this.arrowtipDrawableCopy.shift(headPoint.to(pointLoc));

        //Rotating
        if (direction == slopeDirection.POSITIVE) {
            this.pointTo = jmp.p.to(jmp.cpExit);
        } else {
            this.pointTo = jmp.cpEnter.to(jmp.p);
        }
        Vec v = pointTo;
        double angle = v.getAngle();
        AffineJTransform tr = AffineJTransform.create2DRotationTransform(pointLoc, -Math.PI / 2 + angle);
        tr.applyTransform(arrowtipDrawableCopy);
    }

    @Override
    public void draw(Renderer r) {
        arrowtipDrawableCopy.draw(r);
    }

    @Override
    public int getUpdateLevel() {
        return shape.getUpdateLevel() + 1;
    }

    @Override
    public void update(JMathAnimScene scene) {
        if (!scene.getObjects().contains(shape)) {
            return;
        }
        updateLocations(shape.getPath().getPointAt(location));
    }

    @Override
    public Rect getBoundingBox() {
        updateLocations(shape.getPath().getPointAt(location));
        return arrowtipDrawableCopy.getBoundingBox();
    }

    @Override
    public <T extends MathObject> T copy() {
        return (T) make(shape, location, direction, arrowtip.copy());
    }
}
