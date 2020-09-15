/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Renderers.Renderer;


/**
 * Anything that can be drawed in the canvas
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public interface Drawable {
    public void draw(Renderer r);
}
