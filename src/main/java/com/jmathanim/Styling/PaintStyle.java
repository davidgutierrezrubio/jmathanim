package com.jmathanim.Styling;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Renderers.FXRenderer.JavaFXRenderer;
import javafx.scene.paint.Paint;

/**
 * This interface represents any style that can be applied to a fill or draw, currently a color, a fill or a pattern
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class PaintStyle<T extends PaintStyle<T>> {

    private double alpha;

    public PaintStyle() {
        this.alpha = 1;
    }

    /**
     * Interpolates paint styles and returns proper class. May interpolate JMColor-JMLinearGradient returning
     * JMLinearGradient, JMColor-JMRadialGradient returning JMRadialGradient, etc.
     *
     * @param p1 First PaintStyle to interpolate
     * @param p2 Second PaintStyle to interpolate
     * @param t Interpolation parameter, a number between 0 and 1
     * @return The interpolated paint style
     */
    public static PaintStyle<?> interpolatePaintStyle(PaintStyle<?> p1, PaintStyle<?> p2, double t) {
        if (p1 instanceof JMLinearGradient) {
            JMLinearGradient l1 = (JMLinearGradient) p1;
            return l1.interpolate(p2, t);
        }
        if (p2 instanceof JMLinearGradient) {
            JMLinearGradient l2 = (JMLinearGradient) p2;
            return l2.interpolate(p1, 1 - t);
        }

        if (p1 instanceof JMRadialGradient) {
            JMRadialGradient l1 = (JMRadialGradient) p1;
            return l1.interpolate(p2, t);
        }
        if (p2 instanceof JMRadialGradient) {
            JMRadialGradient l2 = (JMRadialGradient) p2;
            return l2.interpolate(p1, 1 - t);
        }
        return p1.interpolate(p2, t);

    }

    /**
     * Copies the attributes of the specified PaintStyle object into this PaintStyle object.
     *
     * @param A The PaintStyle object from which to copy the attributes. It must not be null.
     */
    abstract public void copyFrom(PaintStyle<?> A);

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
    public PaintStyle<?> setAlpha(double alpha) {
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
     * @param r   JavaFXrenderer instance to use
     * @param cam Camera to compute math coordinates
     * @return The Paint object to use in JavaFX
     */
    //TODO: Separate this in an independent class
    public abstract Paint getFXPaint(JavaFXRenderer r, Camera cam);

    /**
     * Creates a copy of this PaintStyle
     *
     * @return A copy
     */
    public abstract T copy();

    /**
     * Interpolates this PaintStyle with another one, generating a new with interpolated values. Not all implementations
     * of this interface can be interpolated. In this case no interpolation is done and a copy of this object is
     * returned. Currently the following interpolations can be done (in both ways): JMColor-JMColor,
     * JMLinearGradient-JMColor, JMRadialGradient-JMColor, JMLinearGradient-JMLinearGradient (if they have the same
     * relative flag and same cycle method) and JMRadialGradient-JMRadialGradient (same conditions as the previous
     * one).
     *
     * @param p The other PaintStyle to interpolate
     * @param t Interpolation parameter. 0 returns this object, 1 returns the other.
     * @return A new PaintStyle representing the interpolated object. The same class as the first object
     */
    public abstract T interpolate(PaintStyle<?> p, double t);
}
