package com.jmathanim.Styling;

import javafx.scene.paint.Paint;

/**
 * This interface represents any style that can be applied to a fill or draw,
 * currently a color, a fill or a pattern
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 *
 */
public interface PaintStyle {

    public double getAlpha();

    public void setAlpha(double alpha);

    public Paint getFXPaint();

    public PaintStyle copy();

    public PaintStyle interpolate(PaintStyle p, double t);

}
