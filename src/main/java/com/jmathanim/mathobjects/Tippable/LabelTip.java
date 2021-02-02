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

    /**
     * Attach a LaTeX expression to a specific point of a Shape. The LaTeX is
     * attached outside the point
     *
     * @param shape Shape to attach the tip
     * @param location Point of the shape to locate the tip. A parameter between
     * 0 and 1. Values outside this range are normalized.
     * @param text LaTeX string
     * @return The tippable object
     */
    public static LabelTip makeLabelTip(Shape shape, double location, String text) {
        LabelTip resul = new LabelTip();
        resul.shape = shape;
        resul.setLocation(location);
        resul.group = MathObjectGroup.make();
        resul.latexLabel = LaTeXMathObject.make(text);
        resul.group.add(resul.latexLabel);
        resul.markPoint = Point.at(0, 0).visible(false);
        resul.group.add(resul.markPoint);
        resul.setTextOffset(.5 * resul.latexLabel.getHeight());
        resul.setTip(resul.group);
        resul.setAnchor(Anchor.Type.LOWER);
        resul.setOffsetAngle(-PI / 2);
        return resul;
    }
    private boolean fixedAngle;
    private double textOffset;
    protected MathObjectGroup group;

    public double getTextOffset() {
        return textOffset;
    }

    /**
     * Sets the distance between the marker point and the bottom line of the
     * LaTeX expression.By default this value is half height of the LaTeX
     * expression.
     *
     * @param <T> Calling subclass
     * @param textOffset A positive value. Negative values are normalized.
     * @return This object
     */
    public <T extends LabelTip> T setTextOffset(double textOffset) {
        if (textOffset < 0) {
            textOffset = -textOffset;
        }
        this.textOffset = textOffset;
        group.setLayout(Anchor.Type.LOWER, getTextOffset());
        return (T) this;
    }

    /**
     * Sets the distance between the marker point and the bottom line of the
     * LaTeX expression. The difference with other similar methods is that in
     * this case the distance is given relative to the current width of the
     * LaTeX equation. By default this value is half height of the LaTeX
     * expression.
     *
     * @param <T> Calling subclass
     * @param textOffset A positive value, relative to the current width of the
     * LaTeX expression.
     * @return This object
     */
    public <T extends LabelTip> T setTextOffsetRW(double textOffset) {
        return setTextOffset(textOffset * latexLabel.getWidth());
    }

    /**
     * Sets the distance between the marker point and the bottom line of the
     * LaTeX expression. The difference with other similar methods is that in
     * this case the distance is given relative to the current height of the
     * LaTeX equation. By default this value is half height of the LaTeX
     * expression.
     *
     * @param <T> Calling subclass
     * @param textOffset A positive value, relative to the current width of the
     * LaTeX expression.
     * @return This object
     */
    public <T extends LabelTip> T setTextOffsetRH(double textOffset) {
        return setTextOffset(textOffset * latexLabel.getHeight());
    }

    private LabelTip() {
        super();

    }

    @Override
    protected void updateLocations() {
        super.updateLocations(); //To change body of generated methods, choose Tools | Templates.
        MathObjectGroup msh = (MathObjectGroup) getTipCopy();
        if (fixedAngle) {
            msh.get(0).rotate(-totalRotationAngle);
        }
    }

    /**
     * A flag whether the LaTeX expression should rotate according to the slope
     * of the shape or not.
     *
     * @param <T> Calling subclass
     * @param fixedAngle True if LaTeX expression should rotate, false
     * otherwise.
     * @return This object
     */
    public <T extends LabelTip> T fixedAngle(boolean fixedAngle) {
        this.fixedAngle = fixedAngle;
        return (T) this;
    }

    /**
     * Mark point where the label locates
     *
     * @return A Point object
     */
    public Point getMarkPoint() {
        return markPoint;
    }

    /**
     * Sets the visibility of the mark point. This method is equivalent to
     * this.getMarkPoint().visible(flag)
     *
     * @param <T> Calling subclass
     * @param visible Visible flag
     * @return This object
     */
    public <T extends LabelTip> T visibleMarkPoint(boolean visible) {
        markPoint.visible(visible);
        return (T) this;
    }

}
