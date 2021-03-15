/*
 * Copyright (C) 2021 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
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
package com.jmathanim.Utils.Layouts;

import com.jmathanim.Utils.Anchor;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObjectGroup;
import com.jmathanim.mathobjects.Point;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class SimpleLayout extends GroupLayout {

    private final double horizontalGap;
    private final MathObjectGroup.Layout layout;
    private final double verticalGap;
    private Point refPoint;

    public SimpleLayout(MathObjectGroup.Layout layout, double hgap, double vgap) {
        this(null, layout, hgap, vgap);
    }

    public SimpleLayout(Point refPoint, MathObjectGroup.Layout layout, double hgap, double vgap) {
        this.layout = layout;
        this.horizontalGap = hgap;
        this.verticalGap = vgap;
        this.refPoint = refPoint;
    }

    @Override
    public void applyLayout() {
        Anchor.Type anchor1 = Anchor.Type.CENTER;
        Anchor.Type anchor2 = Anchor.Type.CENTER;

        double hgap = 0;
        double vgap = 0;
        switch (layout) {
            case CENTER:
                anchor1 = Anchor.Type.CENTER;
                anchor2 = Anchor.Type.CENTER;
                break;
            case RIGHT:
                anchor1 = Anchor.Type.LEFT;
                anchor2 = Anchor.Type.RIGHT;
                hgap = this.horizontalGap;
                break;
            case LEFT:
                anchor1 = Anchor.Type.RIGHT;
                anchor2 = Anchor.Type.LEFT;
                hgap = this.horizontalGap;
                break;
            case UPPER:
                anchor1 = Anchor.Type.LOWER;
                anchor2 = Anchor.Type.UPPER;
                vgap = this.verticalGap;
                break;
            case LOWER:
                anchor1 = Anchor.Type.UPPER;
                anchor2 = Anchor.Type.LOWER;
                vgap = this.verticalGap;
                break;
            case URIGHT:
                anchor1 = Anchor.Type.UL;
                anchor2 = Anchor.Type.UR;
                hgap = this.horizontalGap;
                break;
            case ULEFT:
                anchor1 = Anchor.Type.UR;
                anchor2 = Anchor.Type.UL;
                hgap = this.horizontalGap;
                break;
            case DRIGHT:
                anchor1 = Anchor.Type.DL;
                anchor2 = Anchor.Type.DR;
                hgap = this.horizontalGap;
                break;
            case DLEFT:
                anchor1 = Anchor.Type.DR;
                anchor2 = Anchor.Type.DL;
                hgap = this.horizontalGap;
                break;
            case RUPPER:
                anchor1 = Anchor.Type.DR;
                anchor2 = Anchor.Type.UR;
                vgap = this.verticalGap;
                break;
            case LUPPER:
                anchor1 = Anchor.Type.DL;
                anchor2 = Anchor.Type.UL;
                vgap = this.verticalGap;
                break;
            case RLOWER:
                anchor1 = Anchor.Type.UR;
                anchor2 = Anchor.Type.DR;
                vgap = this.verticalGap;
                break;
            case LLOWER:
                anchor1 = Anchor.Type.UL;
                anchor2 = Anchor.Type.DL;
                vgap = this.verticalGap;
                break;
            case DIAG1:
                anchor1 = Anchor.Type.DL;
                anchor2 = Anchor.Type.UR;
                vgap = this.verticalGap;
                hgap = this.horizontalGap;
                break;
            case DIAG2:
                anchor1 = Anchor.Type.DR;
                anchor2 = Anchor.Type.UL;
                vgap = this.verticalGap;
                hgap = this.horizontalGap;
                break;
            case DIAG3:
                anchor1 = Anchor.Type.UR;
                anchor2 = Anchor.Type.DL;
                vgap = this.verticalGap;
                hgap = this.horizontalGap;
                break;
            case DIAG4:
                anchor1 = Anchor.Type.UL;
                anchor2 = Anchor.Type.DR;
                vgap = this.verticalGap;
                hgap = this.horizontalGap;
                break;
            default:
                JMathAnimScene.logger.error("Layout not recognized, reverting to CENTER");
                break;
        }
        if (this.refPoint!=null) {
            getGroup().get(0).stackTo(anchor1, this.refPoint, Anchor.Type.CENTER, hgap, vgap);
        }
        for (int n = 1; n < getGroup().size(); n++) {
            getGroup().get(n).stackTo(anchor1, getGroup().get(n - 1), anchor2, hgap, vgap);
        }
    }

}
