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
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MathObjectGroup;
import com.jmathanim.mathobjects.Point;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class SpiralLayout extends GroupLayout {

    private final Point center;
    private double horizontalGap, verticalGap;

    public enum Orientation {
        UPPER_CLOCKWISE, UPPER_COUNTERCLOCKWISE, RIGHT_CLOCKWISE, RIGHT_COUNTERCLOCKWISE,
        LOWER_CLOCKWISE, LOWER_COUNTERCLOCKWISE, LEFT_CLOCKWISE, LEFT_COUNTERCLOCKWISE
    }
    Orientation orientation;
    Anchor.Type[] stacks;

    public SpiralLayout() {
        this(null, Orientation.RIGHT_CLOCKWISE,0,0);
    }

    public SpiralLayout(Point center, Orientation orientation,double hgap,double vgap) {
        this.center = center;
        this.orientation = orientation;
        this.horizontalGap=hgap;
        this.verticalGap=vgap;
        switch (orientation) {
            case LEFT_CLOCKWISE:
                stacks = new Anchor.Type[]{Anchor.Type.LEFT, Anchor.Type.UPPER, Anchor.Type.RIGHT, Anchor.Type.LOWER};
                break;
            case RIGHT_CLOCKWISE:
                stacks = new Anchor.Type[]{Anchor.Type.RIGHT, Anchor.Type.LOWER, Anchor.Type.LEFT, Anchor.Type.UPPER};
                break;
            case UPPER_CLOCKWISE:
                stacks = new Anchor.Type[]{Anchor.Type.UPPER, Anchor.Type.RIGHT, Anchor.Type.LOWER, Anchor.Type.LEFT};
                break;
            case LOWER_CLOCKWISE:
                stacks = new Anchor.Type[]{Anchor.Type.LOWER, Anchor.Type.LEFT, Anchor.Type.UPPER, Anchor.Type.RIGHT};
                break;
            case LEFT_COUNTERCLOCKWISE:
                stacks = new Anchor.Type[]{Anchor.Type.LEFT, Anchor.Type.LOWER, Anchor.Type.RIGHT, Anchor.Type.UPPER};
                break;
            case RIGHT_COUNTERCLOCKWISE:
                stacks = new Anchor.Type[]{Anchor.Type.RIGHT, Anchor.Type.UPPER, Anchor.Type.LEFT, Anchor.Type.LOWER};
                break;
            case UPPER_COUNTERCLOCKWISE:
                stacks = new Anchor.Type[]{Anchor.Type.UPPER, Anchor.Type.LEFT, Anchor.Type.LOWER, Anchor.Type.RIGHT};
                break;
            case LOWER_COUNTERCLOCKWISE:
                stacks = new Anchor.Type[]{Anchor.Type.LOWER, Anchor.Type.RIGHT, Anchor.Type.UPPER, Anchor.Type.LEFT};
                break;
        }

    }

    @Override
    public void applyLayout(MathObjectGroup group) {
        if (this.center != null) {//Stack first element to the center
           group.get(0).stackTo(this.center, Anchor.Type.CENTER);
        }
        int stackType = 0;//Index to the array of used stacks
        int numberOfStacks = 1;//This variable holds how many objects should I stack before doing a "turn"
        int turnNumber=1;
        for (int n = 1; n < group.size(); n++) {
            Anchor.Type stack = stacks[stackType];
            group.get(n).stackTo(Anchor.reverseAnchorPoint(stack),group.get(n - 1), stack,this.horizontalGap,this.verticalGap);
            numberOfStacks--;
            if (numberOfStacks==0) {//Ok, time to turn...
                turnNumber++;
                numberOfStacks=(turnNumber+1)/2;//integer division: 1,1,2,2,3,3,4,4,....
                stackType=(stackType+1) % 4;
            }
        }

    }

}
