package com.jmathanim.Styling;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Renderers.JavaFXRenderer;
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

    public Paint getFXPaint(JavaFXRenderer r,Camera cam);

    public PaintStyle copy();

    public PaintStyle interpolate(PaintStyle p, double t);

}
