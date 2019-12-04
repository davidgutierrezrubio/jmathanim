/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Renderers;

import java.awt.Color;
import java.io.File;

/**
 * Coordinates x,y,z are always given in (0,0) to (w,h), where (0,0) is upper
 * left corner.
 *
 * @author David
 */
public abstract class Renderer {

    Color color;

    public Renderer() {
        color = Color.WHITE;//Default color

    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    abstract public void drawArc();

    abstract public void drawLine(int x1, int y1, int x2, int y2);

    abstract public void drawPolygon();

    abstract public void drawCircle(int x, int y, int radius);

    abstract public void saveFrame(int frameCount);
}
