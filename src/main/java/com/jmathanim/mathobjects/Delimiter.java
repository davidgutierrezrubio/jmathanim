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

import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.ResourceLoader;

/**
 * A extensible delimiter like braces or parenthesis
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Delimiter extends Shape {

    private final Point A, B;
    private final SVGMathObject body;

    /**
     * Type of delimiter
     */
    public enum Type {
        /**
         * Brace {
         */
        BRACE,
        /**
         * Parenthesis (
         */
        PARENTHESIS
    }
    private Type type;

    /**
     * Constructs a new delimiter. The points mark the beginning and end of the
     * delimiter. The delimiter lies at the "left" of vector AB.
     *
     * @param A Beginning point
     * @param B Ending point
     * @param type Type of delimiter, one enum {@link Type}
     */
    public Delimiter(Point A, Point B, Type type) {
        this.A = A;
        this.B = B;
        this.type = type;
        this.getPath().addPoint(A);
        this.getPath().addPoint(B);
        ResourceLoader rl = new ResourceLoader();
        String name = "";
        switch (type) {
            case BRACE:
                name = "#braces.svg";
                break;
            case PARENTHESIS:
                name = "#parenthesis.svg";
                break;
        }
        body = new SVGMathObject(rl.getResource(name, "delimiters"));
        this.style("latexdefault");
        this.drawAlpha(0);//This is necessary so that "stitches" are not seen when fadeIn or fadeOut
    }

    private MultiShapeObject generateDelimiter() {
        double width = A.to(B).norm();

        MultiShapeObject resul = body.copy();
        for (Shape sh : resul) {
            sh.mp.copyFrom(this.mp);
        }

        if (type == Type.BRACE) {
            double minimumWidthToShrink = .5;
            double wr = (width < minimumWidthToShrink ? 1 - (width - minimumWidthToShrink) * (width - minimumWidthToShrink) / minimumWidthToShrink / minimumWidthToShrink : 1);
            resul.setWidth(wr);
            double hasToGrow = width - resul.getBoundingBox().getWidth();
            //0,1,2,3,4,5 shapes  Shapes 1 and 4 are extensible
            double w = resul.get(1).getBoundingBox().getWidth();
            double scale = 1 + .5 * hasToGrow / w;
            resul.get(1).scale(resul.get(1).getBoundingBox().getRight(), scale, 1);
            resul.get(4).scale(resul.get(4).getBoundingBox().getLeft(), scale, 1);
            resul.get(0).shift(-.5 * hasToGrow, 0);
            resul.get(5).shift(.5 * hasToGrow, 0);
        }

        if (type == Type.PARENTHESIS) {
            double minimumWidthToShrink = 1.3;
            double wr = (width < minimumWidthToShrink ? 1 - (width - minimumWidthToShrink) * (width - minimumWidthToShrink) / minimumWidthToShrink / minimumWidthToShrink : 1);
            resul.setWidth(wr);
            double hasToGrow = width - resul.getBoundingBox().getWidth();
            //0,1,2,3 shapes where shapes 1 and 2 are extensible
            double w = resul.get(1).getBoundingBox().getWidth();
            double scale = 1 + .5 * hasToGrow / w;
            resul.get(1).scale(resul.get(1).getBoundingBox().getLeft(), scale, 1);
            resul.get(2).scale(resul.get(2).getBoundingBox().getRight(), scale, 1);
            resul.get(0).shift(.5 * hasToGrow, 0);
            resul.get(3).shift(-.5 * hasToGrow, 0);
        }

        Rect bb = resul.getBoundingBox();
        AffineJTransform tr = AffineJTransform.createDirect2DHomothecy(bb.getDL(), bb.getDR(), A, B, 1);
        tr.applyTransform(resul);
        return resul;
    }

    @Override
    public void draw(Renderer r) {
        MultiShapeObject del = generateDelimiter();
        del.draw(r);
    }

    @Override
    public Rect getBoundingBox() {
        return generateDelimiter().getBoundingBox();
    }

}
