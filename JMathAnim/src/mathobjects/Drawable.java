/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mathobjects;

import java.awt.Graphics2D;

/**
 * Anything that can be drawin in the canvas
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
interface Drawable {
    public void draw(Graphics2D g2d);
}
