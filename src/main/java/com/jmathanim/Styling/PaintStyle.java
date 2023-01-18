package com.jmathanim.Styling;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Renderers.FXRenderer.JavaFXRenderer;
import javafx.scene.paint.Paint;

/**
 * This interface represents any style that can be applied to a fill or draw,
 * currently a color, a fill or a pattern
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 *
 */
public abstract class PaintStyle {

    private double alpha;

    public PaintStyle() {
        this.alpha = 1;
    }

    abstract public void copyFrom(PaintStyle A);

    /**
     * Returns the alpha parameter used for this paint style
     *
     * @return Alpha value. 0 means invisible, 1 fully opaque
     */
    public double getAlpha() {
        return alpha;
    }

    /**
     * Sets the alpha parameter used for this paint style
     *
     * @param alpha Alpha value. 0 means invisible, 1 fully opaque
     */
    public PaintStyle setAlpha(double alpha) {
        this.alpha = alpha;
        return this;
    }

    @Override
    public abstract boolean equals(Object p);

    @Override
    public abstract int hashCode();

    /**
     * returns a valid Paint object to be used in JavaFX methods
     *
     * @param r JavaFXrenderer instance to use
     * @param cam Camera to compute math coordinates
     * @return The Paint object to use in JavaFX
     */
    public abstract Paint getFXPaint(JavaFXRenderer r, Camera cam);

    /**
     * Creates a copy of this PaintStyle
     *
     * @return A copy
     */
    public abstract PaintStyle copy();

    /**
     * Interpolates this PaintStyle with another one, generating a new with
     * interpolated values. Not all implementations of this interface can be
     * interpolated. In this case no interpolation is done and a copy of this
     * object is returned. Currently the following interpolations can be done
     * (in both ways): JMColor-JMColor, JMLinearGradient-JMColor,
     * JMRadialGradient-JMColor, JMLinearGradient-JMLinearGradient (if they have
     * the same relative flag and same cycle method) and
     * JMRadialGradient-JMRadialGradient (same conditions as the previous one).
     *
     * @param p The other PaintStyle to interpolate
     * @param t Interpolation parameter. 0 returns this object, 1 returns the
     * other.
     * @return A new PaintStyle representing the interpolated object.
     */
    public abstract PaintStyle interpolate(PaintStyle p, double t);

}
