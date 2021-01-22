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
package com.jmathanim.Animations;

import com.jmathanim.Utils.Anchor;
import com.jmathanim.jmathanim.Scene2D;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MathObjectGroup;
import com.jmathanim.mathobjects.SVGMathObject;
import com.jmathanim.mathobjects.Shape;
import com.jmathanim.mathobjects.updateableObjects.AnchoredMathObject;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class Guarini extends Scene2D {

    private SVGMathObject horseWhiteA;
    private SVGMathObject horseWhiteB;
    private SVGMathObject horseBlackA;
    private SVGMathObject horseBlackB;
    private MathObjectGroup chess;

    @Override
    public void setupSketch() {
        config.parseFile("#preview.xml");
        config.parseFile("#light.xml");
    }

    @Override
    public void runSketch() throws Exception {
//        disableAnimations();
        int rows = 3;
        int cols = 3;
        chess = createChessBoard(rows, cols);
        add(chess.center());
        camera.adjustToAllObjects();

        createHorses(chess);
        Shape lines[][] = new Shape[rows * cols][rows * cols];
        for (int i = 0; i < rows * cols; i++) {
            for (int j = 0; j < rows * cols; j++) {
                lines[i][j] = lineCell(i, j);
            }
        }
        play.showCreation(lines[6][5], lines[6][1]);
        play.fadeOut(lines[6][5], lines[6][1]);
        play.showCreation(lines[8][3], lines[8][1]);
        play.fadeOut(lines[8][3], lines[8][1]);
//Path: 6, 5, 0, 7, 2, 3, 8, 1,  
        play.fadeOut(horseBlackA, horseBlackB, horseWhiteA, horseWhiteB);
        Shape[] cellsPath = new Shape[8];
        Shape wholePath = lines[6][5].copy();
        cellsPath[0] = chess.get(6).copy();
        wholePath.getPath().addPoint(chess.get(0).getCenter());
        cellsPath[1] = chess.get(5).copy();
        wholePath.getPath().addPoint(chess.get(7).getCenter());
        cellsPath[2] = chess.get(0).copy();
        wholePath.getPath().addPoint(chess.get(2).getCenter());
        cellsPath[3] = chess.get(7).copy();
        wholePath.getPath().addPoint(chess.get(3).getCenter());
        cellsPath[4] = chess.get(2).copy();
        wholePath.getPath().addPoint(chess.get(8).getCenter());
        cellsPath[5] = chess.get(3).copy();
        wholePath.getPath().addPoint(chess.get(1).getCenter());
        cellsPath[6] = chess.get(8).copy();
        wholePath.getPath().addPoint(chess.get(6).getCenter());
        cellsPath[7] = chess.get(1).copy();
        wholePath.getPath().distille();
        for (int n = 0; n < 8; n++) {
            registerUpdateable(new AnchoredMathObject(cellsPath[n], wholePath.getPoint(n), Anchor.Type.BY_CENTER));
           add(cellsPath[n]);
        }
        
        
//        add(wholePath.getPoint(0).thickness(8));
        play.showCreation(6, wholePath);
        Shape expandedPath = Shape.regularPolygon(8).scale(2).center();
        expandedPath.mp.copyFrom(wholePath.mp);
        play.adjustToObjects(expandedPath);
        play.transform(6, wholePath, expandedPath);
        waitSeconds(3);
    }

    private Shape lineCell(int i, int j) {
        return Shape.segment(chess.get(i).getCenter(), chess.get(j).getCenter()).thickness(8).drawColor("darkred");
    }

    private void moveToCell(int cell, SVGMathObject horse) {
        //        waitSeconds(3);
        play.stackTo(3, chess.get(cell), Anchor.Type.BY_CENTER, 0, horse);
    }

    private void createHorses(MathObjectGroup chess) {
        horseWhiteA = SVGMathObject.make("chessHorse.svg").setHeight(.8);
        horseWhiteA.thickness(3).fillColor("white").drawColor("black");
        horseWhiteB = horseWhiteA.copy();
        horseBlackA = horseWhiteA.copy().fillColor("black").drawColor("white");
        horseBlackB = horseBlackA.copy();
        add(horseWhiteA.stackTo(chess.get(0), Anchor.Type.BY_CENTER));
        add(horseWhiteB.stackTo(chess.get(2), Anchor.Type.BY_CENTER));

        add(horseBlackA.stackTo(chess.get(6), Anchor.Type.BY_CENTER));
        add(horseBlackB.stackTo(chess.get(8), Anchor.Type.BY_CENTER));
    }

    private MathObjectGroup createChessBoard(int rows, int cols) {
        Shape cellWhite = Shape.square().fillColor("white").drawColor("black").thickness(3);
        Shape cellBlack = cellWhite.copy().fillColor("gray");
        double w = cellWhite.getWidth();
        double h = cellWhite.getHeight();
        MathObjectGroup resul = new MathObjectGroup();
        boolean whiteRow = true;
        for (int i = 0; i < rows; i++) {
            boolean whiteCol = whiteRow;
            for (int j = 0; j < cols; j++) {
                Shape cell = (whiteCol ? cellWhite.copy() : cellBlack.copy());
                resul.add(cell.shift(j * w, i * h));
                whiteCol = !whiteCol;
            }
            whiteRow = !whiteRow;
        }
        return resul;
    }

}
