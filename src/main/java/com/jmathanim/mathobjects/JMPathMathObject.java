/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.MathObjectDrawingProperties;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import java.awt.Color;
import java.util.ArrayList;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class JMPathMathObject extends MathObject {

    public final JMPath jmpath;
    protected boolean needsRecalcControlPoints;
    protected int numInterpolationPoints = 1;//TODO: Adaptative interpolation
    protected boolean isClosed = false;
    protected final ArrayList<JMPathPoint> vertices;

    /**
     * Type of path, JMPath.STRAIGHT or JMPath.CURVED
     */
    

    private ArrayList<Boolean> visibilityTemp;
    private double fillAlphaTemp;

    public JMPathMathObject() {
        this(null);
    }

    public JMPathMathObject(JMPath jmpath, MathObjectDrawingProperties mp) {
        super(mp);
        vertices = new ArrayList<JMPathPoint>();
        this.jmpath = jmpath;
        needsRecalcControlPoints = false;
    }

    public JMPathMathObject(MathObjectDrawingProperties mp) {//TODO: Fix this
        super(mp);
        vertices = new ArrayList<JMPathPoint>();
        jmpath = new JMPath();
        needsRecalcControlPoints = false;
            }

    public JMPathPoint getPoint(int n) {
        return jmpath.getPoint(n);
    }

    /**
     * This method computes all necessary points to the path (interpolation and
     * control)
     */
    protected final void computeJMPathFromVertices() {
        //TODO: ¿Compute intermediate points?
        jmpath.clear();//clear points
        for (JMPathPoint p : vertices) {
            jmpath.addPoint(p);
        }
        if (isClosed) {
            jmpath.close();
        } else {
            jmpath.open();
        }
        //This should'nt be done unless necessary (an animation for example)
        if (numInterpolationPoints > 1) {
            jmpath.interpolate(numInterpolationPoints);//Interpolate points
        }
        

        jmpath.generateControlPoints();
        needsRecalcControlPoints = false;
    }

   

    public JMPath getPath() {
        return jmpath;
    }

    protected final void computeVerticesFromPath() {
        vertices.clear();
        for (JMPathPoint p : jmpath.jmPathPoints) {
            if (p.type == JMPathPoint.TYPE_VERTEX) {
                vertices.add(p);
            }
        }
    }

    @Override
    public Point getCenter() {
        return getBoundingBox().getCenter();

    }

    @Override
    public void shift(Vec shiftVector) {
        jmpath.shift(shiftVector);
    }

    @Override
    public void setDrawParam(double drawParam, int numSlices) {

        //If this is the first call, be sure to store visibility status
        if (drawParam == 0) {
            fillAlphaTemp = mp.fillColor.getAlpha() / 255.;
            visibilityTemp = new ArrayList<Boolean>();
            for (int n = 0; n < jmpath.jmPathPoints.size(); n++) {
                visibilityTemp.add(jmpath.jmPathPoints.get(n).isVisible);
            }
        }

        mp.setFillAlpha((float) (fillAlphaTemp * drawParam));

//        jmpath.isFilled = (drawParam >= 1);//Fill path if is completely drawn
        double sliceSize = jmpath.jmPathPoints.size() * drawParam / numSlices;

        for (int n = 0; n < jmpath.jmPathPoints.size() / numSlices; n++) {
            for (int k = 0; k < numSlices; k++) {//TODO: Store initial visible in array
                int h = k * jmpath.jmPathPoints.size() / numSlices + n;
                if (n < sliceSize) {
                    jmpath.getPoint(h).isVisible = visibilityTemp.get(h);
                } else {
                    jmpath.getPoint(h).isVisible = false;
                }
            }
        }

    }

    public void removeInterpolationPoints() {
        jmpath.removeInterpolationPoints();
    }

//    public void removeInterpolationPoints() {
//        ArrayList<JMPathPoint> toRemove = new ArrayList<>();
//        for (JMPathPoint p : jmpath.points) {
//            if (p.type == JMPathPoint.TYPE_INTERPOLATION_POINT) {
//                toRemove.add(p);
//            }
//        }
//        jmpath.points.removeAll(toRemove);
////        jmpath.generateControlPoints();
//    }
    @Override
    public void moveTo(Vec coords) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void scale(Point scaleCenter, double sx, double sy, double sz) {
        jmpath.scale(scaleCenter, sx, sy, sz);
    }

    @Override
    public JMPathMathObject copy() {
        JMPathMathObject resul = new JMPathMathObject(jmpath.rawCopy(), mp.copy());
        resul.isClosed = this.isClosed;
        return resul;
    }

    @Override
    public void prepareForNonLinearAnimation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void processAfterNonLinearAnimation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void draw(Renderer r) {
        r.setBorderColor(mp.drawColor);
        r.setFillColor(mp.fillColor);
        r.setStroke(this);
        r.drawPath(this);
    }

    @Override
    public Rect getBoundingBox() {
        return jmpath.getBoundingBox();
    }

    void setColor(Color color) {
        this.mp.drawColor = color;
    }

    void setFillColor(Color color) {
        this.mp.fillColor = color;
    }

    @Override
    public void setDrawAlpha(double t) {
        this.mp.setDrawAlpha((float) t);
    }

    @Override
    public void setFillAlpha(double t) {
        this.mp.setFillAlpha((float) t);
    }

    @Override
    public String toString() {
        return jmpath.toString();
    }

    @Override
    public void registerChildrenToBeUpdated(JMathAnimScene scene) {
        for (JMPathPoint p : jmpath.jmPathPoints) {
            scene.registerObjectToBeUpdated(p.p);
            scene.registerObjectToBeUpdated(p.cp1);
            scene.registerObjectToBeUpdated(p.cp2);
        }
    }

    @Override
    public void unregisterChildrenToBeUpdated(JMathAnimScene scene) {
        for (JMPathPoint p : jmpath.jmPathPoints) {
            scene.unregisterObjectToBeUpdated(p.p);
            scene.unregisterObjectToBeUpdated(p.cp1);
            scene.unregisterObjectToBeUpdated(p.cp2);
        }
    }

    @Override
    public void update() {
        //Nothing to do here
    }
}
