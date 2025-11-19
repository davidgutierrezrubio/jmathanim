package com.jmathanim.MathObjects;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Enum.DotStyle;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Styling.DrawStyleProperties;
import com.jmathanim.Styling.JMColor;
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.DependableUtils;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimConfig;
import com.jmathanim.jmathanim.JMathAnimScene;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public abstract class AbstractPoint<T extends AbstractPoint<T>> extends MathObject<T> implements
        Coordinates<T>, AffineTransformable<T>, Interpolable<T> {
    //Position of the point to be drawn in screen
    public final Vec v;
    //Current position of the Shape representing the point
    protected final Vec previousVecPosition;
    protected final Shape dotShape;
    private final MODrawProperties mpPoint;
    private long mpVersion;
    private long dotShapeVersion;

    protected AbstractPoint() {
        this(Vec.to(0, 0));
    }

    protected AbstractPoint(Vec v) {
        super();
        this.v = v;
        previousVecPosition = this.getVec().copy();
        this.dotShape = new Shape();
        dotShapeVersion=-1L;
        mpVersion=-1L;
        mpPoint = JMathAnimConfig.getConfig().getDefaultMP();
        mpPoint.copyFrom(JMathAnimConfig.getConfig().getStyles().get("dotdefault"));
        mpPoint.setAbsoluteThickness(true);
        addDependency(this.v);
        addDependency(mpPoint);
        generateDotShape();
    }

    @Override
    public boolean needsUpdate() {
        newLastMaxDependencyVersion= DependableUtils.maxVersion(
               this.v,
                getMp()
        );
        return dirty || (newLastMaxDependencyVersion != lastCleanedDepsVersionSum);
    }

    @Override
    public T applyAffineTransform(AffineJTransform affineJTransform) {
        RealMatrix pRow = new Array2DRowRealMatrix(new double[][]{{1d, v.x, v.y, v.z}});
        RealMatrix pNew = pRow.multiply(affineJTransform.getMatrix());

        v.x = pNew.getEntry(0, 1);
        v.y = pNew.getEntry(0, 2);
        v.z = pNew.getEntry(0, 3);
        affineJTransform.applyTransformsToDrawingProperties(this);
        return (T) this;
    }

    @Override
    public Vec getCenter() {
        return Vec.to(v.x, v.y, v.z);
    }

    @Override
    public DrawStyleProperties getMp() {
        return mpPoint;
    }

    public Shape getDotShape() {
        if (dotShape.isEmpty()) {
            generateDotShape();
            generateStyleForDot();
            dotShape.setAbsoluteSize(v);
        }
        if (getMp().getVersion()>mpVersion) {
            generateDotShape();
            generateStyleForDot();
            dotShape.setAbsoluteSize(v);
            mpVersion=getMp().getVersion();
        }
        if (!previousVecPosition.equals(v)) {
            dotShape.shift(previousVecPosition.to(v));//TODO Improve this
            previousVecPosition.copyCoordinatesFrom(v);
        }
        return dotShape;
    }
    @Override
    public void draw(JMathAnimScene scene, Renderer r, Camera cam) {
        if (v.isNaN()) {
//        if ((v.isNaN()) || (this.scene == null))  {
            return;
        }
        if (isVisible()) {
            getDotShape();
            dotShape.draw(scene, r, cam);
        }
        scene.markAsAlreadydrawn(this);

    }

    /**
     * Stablishes dot style.
     *
     * @param dotStyle Style dot. DOT_STYLE_CIRCLE, DOT_STYLE_CROSS, DOT_STYLE_PLUS
     * @return The object
     */
    public T dotStyle(DotStyle dotStyle) {
        this.getMp().setDotStyle(dotStyle);
        return (T) this;
    }


    @Override
    public void performMathObjectUpdateActions() {
        generateDotShape();
    }

    private void generateDotShape() {
        double st = scene.getRenderer().ThicknessToMathWidth(this);
        double th = scene.getRenderer().MathWidthToThickness(st);
        double sc = .5 * st;
        dotShape.getPath().clear();
        switch (getMp().getDotStyle()) {
            case CROSS:
                dotShape.getPath().addPoint(Vec.to(-sc, sc), Vec.to(sc, -sc), Vec.to(sc, sc), Vec.to(-sc, -sc));
                dotShape.get(0).setSegmentToThisPointVisible(false);
                dotShape.get(2).setSegmentToThisPointVisible(false);
                dotShape.shift(previousVecPosition);
                break;
            case PLUS:
                dotShape.getPath().addPoint(Vec.to(0, sc), Vec.to(0, -sc), Vec.to(sc, 0), Vec.to(-sc, 0));
                dotShape.get(0).setSegmentToThisPointVisible(false);
                dotShape.get(2).setSegmentToThisPointVisible(false);
                dotShape.shift(previousVecPosition);
                break;
            case TRIANGLE_DOWN_HOLLOW:
                dotShape.getPath().addPoint(Vec.to(-sc, 0.5773502691893 * sc), Vec.to(sc, 0.5773502691893 * sc), Vec.to(0, -1.15470053838 * sc));
                dotShape
                        .shift(previousVecPosition);
                break;
            case TRIANGLE_UP_HOLLOW:
                dotShape.getPath().addPoint(Vec.to(-sc, -0.5773502691893 * sc), Vec.to(sc, -0.5773502691893 * sc), Vec.to(0, 1.15470053838 * sc));
                dotShape.shift(v);
                break;
            case TRIANGLE_DOWN_FILLED:
                dotShape.getPath().addPoint(Vec.to(-sc, 0.5773502691893 * sc), Vec.to(sc, 0.5773502691893 * sc), Vec.to(0, -1.15470053838 * sc));
                dotShape.shift(previousVecPosition);
                break;
            case TRIANGLE_UP_FILLED:
                dotShape.getPath().addPoint(Vec.to(-sc, -0.5773502691893 * sc), Vec.to(sc, -0.5773502691893 * sc), Vec.to(0, 1.15470053838 * sc));
                dotShape.shift(previousVecPosition);
                break;

            case RING:
                dotShape.getPath().copyStateFrom(Shape.circle().getPath());
                dotShape.shift(previousVecPosition).scale(.5 * st);
                break;
            default:// Default case, includes CIRCLE
                dotShape.getPath().copyStateFrom(Shape.circle().getPath());
                dotShape.shift(previousVecPosition).scale(.5 * st);
                break;
        }
        dotShape.getMp().setFaceToCamera(true);
        dotShape.getMp().setFaceToCameraPivot(this.v);
    }



    private void generateStyleForDot() {
        double st = scene.getRenderer().ThicknessToMathWidth(this);
        double th = scene.getRenderer().MathWidthToThickness(st);
        double sc = .5 * st;
        switch (getMp().getDotStyle()) {
            case CROSS:
                dotShape.drawColor(getMp().getDrawColor()) .fillColor(JMColor.parse("none")).thickness(.25 * th);
                break;
            case PLUS:
                dotShape.drawColor(getMp().getDrawColor()) .fillColor(JMColor.parse("none")).thickness(.25 * th);
                break;
            case TRIANGLE_DOWN_HOLLOW:
                dotShape.drawColor(getMp().getDrawColor()) .fillColor(JMColor.parse("none")).thickness(.25 * th);
                break;
            case TRIANGLE_UP_HOLLOW:
                dotShape.drawColor(getMp().getDrawColor()).thickness(.25 * th);
                break;
            case TRIANGLE_DOWN_FILLED:
                dotShape.drawColor(getMp().getDrawColor()).fillColor(getMp().getDrawColor()).thickness(0);
                break;
            case TRIANGLE_UP_FILLED:
                dotShape.drawColor(getMp().getDrawColor()).fillColor(getMp().getDrawColor()).thickness(0);
                break;
            case RING:
                dotShape.drawColor(getMp().getDrawColor()).fillColor(JMColor.parse("none")).thickness(.25 * th);
                break;
            default:// Default case, includes CIRCLE
                dotShape.drawColor(getMp().getDrawColor())
                        .fillColor(getMp().getDrawColor()).thickness(0);
                break;
        }
        dotShape.getMp().setFaceToCamera(true);
        dotShape.getMp().setFaceToCameraPivot(this.v);
    }




    /**
     * Returns the current dot style
     *
     * @return A value of enum DotStyle: CIRCLE, CROSS, PLUS
     */
    public DotStyle getDotStyle() {
        return getMp().getDotStyle();
    }

    /**
     * Creates a copy of the object
     *
     * @return The copy
     */
    public abstract T copy();

    /**
     * Return a new Point object which represents the original point plus a given vector. The original point is
     * unaltered.
     *
     * @param addVector Vector to add
     * @return Original point+addVector
     */
    public T add(Vec addVector) {
        T resul = this.copy();
        resul.v.shift(addVector);
        return resul;
    }

    /**
     * Returns a new Point, linearly interpolated between this and p2 with alpha parameter
     *
     * @param coords2 Second Point to interpolate. Any object that implements the Coordinates interface can be used.
     * @param alpha   Interpolation parameter where 0 returns a copy of this object and 1 a copy of another object
     * @return The new Point
     */
    public T interpolate(Coordinates<?> coords2, double alpha) {
        Vec w = v.interpolate(coords2, alpha);
        T resul = this.copy();
        resul.copyCoordinatesFrom(w);
        return resul;
    }

    @Override
    public Rect computeBoundingBox() {
        return new Rect(v.x, v.y, v.z, v.x, v.y, v.z);
    }


    /**
     * Copy full state form another point p
     *
     * @param obj
     */
    @Override
    public void copyStateFrom(Stateable obj) {
        if (!(obj instanceof AbstractPoint)) return;
        AbstractPoint<?> p2 = (AbstractPoint<?>) obj;
        super.copyStateFrom(obj);
        this.v.copyCoordinatesFrom(p2.v);//Copy coordinates
        this.previousVecPosition.copyCoordinatesFrom(p2.previousVecPosition);//Copy coordinates
        generateDotShape();
        generateStyleForDot();
        this.scene = p2.scene;

    }

    public boolean isEquivalentTo(Point p2, double epsilon) {
        return v.isEquivalentTo(p2.v, epsilon);
    }

    @Override
    public Vec getVec() {
        return v;
    }

    @Override
    public T add(Coordinates<?> coords) {
        T copy = copy();
        return copy.shift(coords);
    }

    @Override
    protected void setDebugText(String debugText) {
        dotShape.setDebugText(debugText);
    }

    @Override
    protected String getDebugText() {
        return dotShape.getDebugText();
    }


}
