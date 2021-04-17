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
package com.jmathanim.Utils.Layouts;

import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MathObjectGroup;
import com.jmathanim.mathobjects.Point;
import java.util.ArrayList;
import java.util.function.IntToDoubleFunction;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class FlowLayout extends AbstractBoxLayout {

    public IntToDoubleFunction rowLength;
    BoxLayout.Direction direction;

    public FlowLayout(Point corner, double width, BoxLayout.Direction direction, double inRowGap, double inColGap) {
        super(corner, inRowGap, inColGap);
        rowLength = (int row) -> width;
        this.direction = direction;
        computeDirections(direction);
    }

    private double getAppropiateSize(MathObject obj) {
        double resul = 0;
        switch (direction) {
            case DOWN_LEFT:
            case DOWN_RIGHT:
            case UP_LEFT:
            case UP_RIGHT:
                resul = obj.getHeight();
                break;
            case LEFT_DOWN:
            case LEFT_UP:
            case RIGHT_DOWN:
            case RIGHT_UP:
                resul = obj.getWidth();
                break;
        }
        return resul;
    }

    @Override
    public void applyLayout(MathObjectGroup group) {
        ArrayList<MathObjectGroup> rowGroups = getRowGroups(group);
        
        rowGroups.get(0).get(0).stackTo(corner, firstElementStack);
        for (int n = 1; n < rowGroups.get(0).size(); n++) {
            rowGroups.get(0).get(n).stackTo(rowGroups.get(0).get(n-1), firstElementStack,inRowGap);
        }
    }

    public ArrayList<MathObjectGroup> getRowGroups(MathObjectGroup group) {
        ArrayList<MathObjectGroup> resul = new ArrayList<>();
        MathObject firstOfTheRow = group.get(0);
        MathObjectGroup currentRow = MathObjectGroup.make(firstOfTheRow);
        resul.add(currentRow);
        int rowNumber = 0;
        double totalWidth = getAppropiateSize(firstOfTheRow);//when this variable is greater than size, go to a new line
        //Puts the first element in the corner point
        firstOfTheRow.stackTo(corner, firstElementStack);
        //Now the rest
        for (int n = 1; n < group.size(); n++) {
            totalWidth += getAppropiateSize(group.get(n)) + inRowGap;
            if (totalWidth <= rowLength.applyAsDouble(rowNumber)) {
//                group.get(n).stackTo(group.get(n - 1), inRowStack, inRowGap);
                currentRow.add(group.get(n));
//                System.out.println("row " + rowNumber + ", size=" + totalWidth);
            } else {
//                System.out.println("Salto porque " + totalWidth);
                rowNumber++;
//                group.get(n).stackTo(firstOfTheRow, inColStack, inColGap);
                firstOfTheRow = group.get(n);
                totalWidth = getAppropiateSize(firstOfTheRow);
                currentRow = MathObjectGroup.make(firstOfTheRow);
                resul.add(currentRow);
            }

        }
        return resul;
    }

    private MathObjectGroup.Layout getInRowLayout() {
        MathObjectGroup.Layout resul=null;
        switch(direction) {
            
        }
        return resul;
    }
    
    
    
}
