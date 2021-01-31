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
package com.jmathanim.mathobjects.Tippable;

import com.jmathanim.Styling.JMColor;
import com.jmathanim.Utils.Anchor;
import static com.jmathanim.jmathanim.JMathAnimScene.PI;
import com.jmathanim.mathobjects.LaTeXMathObject;
import com.jmathanim.mathobjects.MathObjectGroup;
import com.jmathanim.mathobjects.MultiShapeObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class LabelTip extends TippableObject {

    private LaTeXMathObject latexLabel;
    private Point markPoint;

    public static LabelTip make(Shape shape, double location, String text) {
        LabelTip resul = new LabelTip();
        resul.shape = shape;
        resul.setLocation(location);
        MathObjectGroup tip = MathObjectGroup.make();
        resul.latexLabel = LaTeXMathObject.make(text);
        tip.add(resul.latexLabel);
        resul.markPoint=Point.at(0,0).drawColor(JMColor.BLUE);
        tip.add(resul.markPoint);
        tip.setLayout(Anchor.Type.LOWER, .5*resul.latexLabel.getHeight());
        resul.setTip(tip);
        resul.setAnchor(Anchor.Type.LOWER);
        resul.setOffsetAngle(-PI / 2);
        return resul;
    }
    private boolean fixedAngle;

    private LabelTip() {
        super();

    }

    @Override
    public void updateLocations() {
        super.updateLocations(); //To change body of generated methods, choose Tools | Templates.
        MathObjectGroup msh = (MathObjectGroup) getTipCopy();
        if (fixedAngle) {
            msh.get(0).rotate(-totalRotationAngle);
        }
    }

    public <T extends LabelTip> T fixedAngle(boolean fixedAngle) {
        this.fixedAngle = fixedAngle;
        return (T) this;
    }
    
   public <T extends LabelTip> T markPointVisible(boolean visible) {
        this.markPoint.visible(visible);
        return (T) this;
    }

    public Point getMarkPoint() {
        return markPoint;
    }
    
}
