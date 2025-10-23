package com.jmathanim.mathobjects;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Enum.AlignType;
import com.jmathanim.Renderers.FXRenderer.JavaFXRendererUtils;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Styling.DrawStyleProperties;
import com.jmathanim.Styling.JMColor;
import com.jmathanim.Utils.*;
import com.jmathanim.jmathanim.JMathAnimScene;
import javafx.scene.shape.Path;

import java.util.ArrayList;
import java.util.Objects;
import java.util.OptionalInt;

public abstract class
AbstractShape<T extends AbstractShape<T>>
        extends MathObject<T> implements hasPath {
    protected final JMPath jmpath;
    private final DrawStyleProperties mpShape;
    protected boolean showDebugPoints = false;
    protected boolean isConvex = false;


    protected AbstractShape() {
        this(new JMPath());
    }


    protected AbstractShape(JMPath jmpath) {
        super();
        this.jmpath = jmpath;
        this.mpShape = JMathAnimConfig.getConfig().getDefaultMP();
    }

    public abstract Shape toShape();
    /**
     * Returns a new Point object lying in the Shape, at the given position
     *
     * @param t Position parameter, from 0 (beginning) to 1 (end)
     * @return a new Point object at the specified position of the shape.
     */

    public Vec getVecAt(double t) {
        return getPath().getJMPointAt(t).getV();
    }

    /**
     * Returns a new Point object lying in the Shape, at the given parametrized position, considering the arclentgh of
     * the curve.
     *
     * @param t Position parameter, from 0 (beginning) to 1 (end)
     * @return a new Point object at the specified position of the shape.
     */
    public Vec getParametrizedVecAt(double t) {
        return getPath().getParametrizedVecAt(t);
    }

    @Override
    public DrawStyleProperties getMp() {
        return mpShape;
    }

    /**
     * Align this object with another one
     *
     * @param obj  Object to align with. This object remains unaltered.
     * @param type Align type, a value from the enum Align
     * @return This object
     */
    @Override
    public T align(Boxable obj, AlignType type) {
        Vec shiftVector = Vec.to(0, 0);
        Rect thisBoundingBox = this.getBoundingBox();
        Rect objectBoundingBox = obj.getBoundingBox();
        switch (type) {
            case LOWER:
                shiftVector.y = objectBoundingBox.ymin - thisBoundingBox.ymin;
                break;
            case UPPER:
                shiftVector.y = objectBoundingBox.ymax - thisBoundingBox.ymax;
                break;
            case LEFT:
                shiftVector.x = objectBoundingBox.xmin - thisBoundingBox.xmin;
                break;
            case RIGHT:
                shiftVector.x = objectBoundingBox.xmax - thisBoundingBox.xmax;
                break;
            case VCENTER:
                shiftVector.y = 0.5 * ((objectBoundingBox.ymin + objectBoundingBox.ymax) - (thisBoundingBox.ymin + thisBoundingBox.ymax));
                break;
            case HCENTER:
                shiftVector.x = 0.5 * ((objectBoundingBox.xmin + objectBoundingBox.xmax) - (thisBoundingBox.xmin + thisBoundingBox.xmax));
                break;
        }
        shift(shiftVector);
        return (T) this;
    }

    @Override
    public T applyAffineTransform(AffineJTransform affineJTransform) {
        super.applyAffineTransform(affineJTransform);
        if (!isRigid) {
            jmpath.applyAffineTransform(affineJTransform);
            affineJTransform.applyTransformsToDrawingProperties(this);
        }
        return (T) this;
    }

    @Override
    protected Rect computeBoundingBox() {
        return jmpath.getBoundingBox();
    }

    /**
     * Overloaded method. Check if a given point is inside the shape
     *
     * @param p Point to check
     * @return True if p lies inside of the shape (regardless of being filled or not). False otherwise.
     */
    public boolean containsPoint(Point p) {
        return containsPoint(p.v);
    }

    /**
     * Check if a given vector is inside the shape
     *
     * @param v Vector to check
     * @return True if v lies inside of the shape (regardless of being filled or not). False otherwise.
     */
    public boolean containsPoint(Vec v) {
        Camera dummyCamera = JMathAnimConfig.getConfig().getFixedCamera();
        Path path = JavaFXRendererUtils.createFXPathFromJMPath(jmpath, Vec.to(0, 0), dummyCamera);
        path.setFill(JMColor.parse("black").getFXColor()); // It's necessary that the javafx path is filled to work
        double[] xy = dummyCamera.mathToScreenFX(v);
        return path.contains(xy[0], xy[1]);
    }

    /**
     * Returns the n-th JMPathPoint of path.
     *
     * @param n index. A cyclic index, so that 0 means the first point and -1 the last one
     * @return The JMPathPoint
     */
    public JMPathPoint get(int n) {
        return jmpath.getJmPathPoints().get(n);
    }

    /**
     * Returns a reference to the point at position n This is equivalent to get(n).p
     *
     * @param n Point number. A cyclic index, so that 0 means the first point and -1 the last one
     * @return The point
     */
    public Point getPoint(int n) {
        return jmpath.get(n).getPoint();
    }

    /**
     * Returns the path associated to this Shape
     *
     * @return A JMPath object
     */
    @Override
    public JMPath getPath() {
        return jmpath;
    }

    /**
     * Gets a Shape object with the subshape given by start and end parameters
     *
     * @param a Start parameter
     * @param b End parameter
     * @return A Shape object
     */
    public Shape getSubShape(double a, double b) {
        Shape subShape = new Shape();
        subShape.getMp().copyFrom(this.getMp());
        if (!jmpath.isEmpty()) {
            final JMPath subPath = jmpath.getSubPath(a, b);
            subShape.getPath().getJmPathPoints().addAll(subPath.getJmPathPoints());
        }
        return subShape;
    }

    /**
     * Return the value of boolean flag showDebugPoints
     *
     * @return If true, the point number will be superimposed on screen when drawing this shape
     */
    public boolean isShowDebugPoints() {
        return showDebugPoints;
    }

    /**
     * Sets the vaue of boolean flag showDebugPoints. If true, the point number will be superimposed on screen when
     * drawing this shape. Accessed through the MediatorMathObject class
     *
     * @param showDebugPoints
     * @return This object
     */
    protected void setShowDebugPoints(boolean showDebugPoints) {
        this.showDebugPoints = showDebugPoints;
    }

    /**
     * Returns the size, that is, the number of JMPathPoints of the Shape
     *
     * @return Number of JMPathPoints
     */
    public int size() {
        return jmpath.size();
    }

    /**
     * Gets the normal vector of the shape, asumming the shape is planar.
     *
     * @return The normal vector
     */
    public Vec getNormalVector() {
        if (size() < 3) {
            return Vec.to(0, 0, 0);
        }
        Vec v1 = get(0).getV().minus(get(size() / 3).getV());
        Vec v2 = get(size() / 2).getV().minus(get(size() / 3).getV());
        return v1.cross(v2);
    }

    /**
     * Reverse the points of the path. First point becomes last. The object is altered
     *
     * @return This object
     */
    public T reverse() {
        getPath().reverse();
        return (T) this;
    }

    @Override
    public String toString() {
        return "Shape " + objectLabel + ": " + jmpath.toString();
    }


    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        OptionalInt m = getPath().getJmPathPoints().stream().mapToInt(t -> t.getUpdateLevel()).max();
        if (m.isPresent()) {
            setUpdateLevel(m.getAsInt() + 1);
        } else {
            setUpdateLevel(0);
        }
    }

    /**
     * Merges with the given Shape, adding all their jmpathpoints.If the shapes were disconnected they will remain so
     * unless the connect parameter is set to true. In such case, the shapes will be connected by a straight line from
     * the last point of the calling object to the first point of the given one.
     *
     * @param sh          Shape to merge
     * @param connectAtoB If true, the end of path A will be connected to the beginning of path B by a straight line
     * @param connectBtoA If true, the end of path B will be connected to the beginning of path A by a straight line
     * @return This object
     */
    public T merge(hasPath sh, boolean connectAtoB, boolean connectBtoA) {
        jmpath.merge(sh.getPath().copy(), connectAtoB, connectBtoA);
        return (T) this;
    }

    @Override
    public void copyStateFrom(Stateable obj) {
        super.copyStateFrom(obj);

        if (!(obj instanceof AbstractShape)) return;
        AbstractShape<?> sh2 = (AbstractShape<?>) obj;

        if (!isRigid) {
            getPath().copyStateFrom(sh2.getPath());
        }
        absoluteSize = sh2.absoluteSize;
        isConvex = sh2.isConvex;
        showDebugPoints = sh2.showDebugPoints;
        if (!Objects.equals(getDebugText(), "")) {
            setDebugText(getDebugText() + "_copy");

        }
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r, Camera cam) {
        if (isVisible()) {
            if (absoluteSize) {
                r.drawAbsoluteCopy(this, getAbsoluteAnchor());
            } else {
                r.drawPath(this, cam);
                if (isShowDebugPoints()) {
                    for (int n = 0; n < size(); n++) {
                        r.debugText("" + n, getPoint(n).v);
                    }

                }
            }
        }
    }

    public ArrayList<ArrayList<float[]>> computePolygonalPieces() {
        return jmpath.computePolygonalPieces(scene.getCamera());
    }

    public boolean isOpen() {
        return getPath().isOpen();
    }


}
