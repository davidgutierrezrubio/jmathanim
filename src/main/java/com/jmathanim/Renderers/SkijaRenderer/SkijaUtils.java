package com.jmathanim.Renderers.SkijaRenderer;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.MathObjects.MathObject;
import com.jmathanim.MathObjects.Shapes.JMPath;
import com.jmathanim.MathObjects.Shapes.JMPathPoint;
import com.jmathanim.Styling.*;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import io.github.humbleui.skija.*;

import java.util.HashMap;
import java.util.Map;


class SkijaUtils {
    private final JMathAnimConfig config;
    private final HashMap<JMPath, Path> paths;
    private final Canvas canvas;
    private final SkijaHandler handler;

    public SkijaUtils(SkijaHandler handler) {
        this.config = handler.config;
        this.canvas = handler.canvas;
        this.handler = handler;
        paths = new HashMap<>();
    }

    public void clearFrame() {
        canvas.clear(0xFFFFFFFF);//TODO: set color to background config color
    }


    /**
     * Computes matrix transform for given JMathAnim camera. This matrix will transform from math coordinates to screen
     * coordinates.
     *
     * @param camera Camera
     * @return The transform matrix
     */

    protected Matrix33 createCameraView(Camera camera) {
        float width_math = 10f;
        com.jmathanim.Utils.Rect mathView = camera.getMathView();
        float scale = (float) (config.mediaW / mathView.getWidth());

        float centerX = (float) (.5 * (mathView.xmin + mathView.xmax));
        float centerY = (float) (.5 * (mathView.ymin + mathView.ymax));
        // Coordenadas del centro en píxeles después de escalar
        float dx = config.mediaW / 2f - centerX * scale;
        float dy = config.mediaH / 2f + centerY * scale; // + porque Y se invierte
        Matrix33 transform = Matrix33.makeTranslate(dx, dy)
                .makeConcat(Matrix33.makeScale(scale, -scale));
        return transform;
    }

    /**
     * Converts JMPath to format suitable to be drawn by Skija
     *
     * @param jmpath JMPath to transform
     * @return Skija Path object
     */
    protected Path convertJMPathToSkijaPath(JMPath jmpath) {
//        if (paths.containsKey(jmpath)) {
//            return paths.get(jmpath);
//        }

        Path path = new Path();
        Vec prev = jmpath.getJmPathPoints().get(0).getV().copy();
        path.moveTo((float) prev.x, (float) prev.y);
        for (int n = 1; n < jmpath.size() + 1; n++) {
            Vec point = jmpath.getJmPathPoints().get(n).getV();
            Vec cpoint1 = jmpath.getJmPathPoints().get(n - 1).getVExit();
            Vec cpoint2 = jmpath.getJmPathPoints().get(n).getVEnter();

            if (jmpath.getJmPathPoints().get(n).isSegmentToThisPointVisible()) {
                JMPathPoint jp = jmpath.getJmPathPoints().get(n);
                //Should remove this in Skija?
                if ((!jp.isSegmentToThisPointCurved()) || ((isAbsEquiv(prev, cpoint1, .1)) && (isAbsEquiv(point, cpoint2, .0001)))) {
                    path.lineTo((float) point.x, (float) point.y);
                } else {
                    path.cubicTo((float) cpoint1.x, (float) cpoint1.y, (float) cpoint2.x, (float) cpoint2.y, (float) point.x, (float) point.y);
                }
            } else {
                path.moveTo((float) point.x, (float) point.y);
            }
            prev.copyCoordinatesFrom(point);
        }
//        paths.put(jmpath, path);
        return path;
    }

    protected boolean isAbsEquiv(Vec a, Vec b, double epsilon) {
        final double nn = Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
        return nn < epsilon;
    }

    /**
     * Create skija paint parameters from JMathAnim style properties object
     *
     * @param style Shape object to get styles
     * @return
     */
    protected Paint createDrawPaint(MathObject obj, DrawStyleProperties style) {
        Paint paint = new Paint();
        paint.setMode(PaintMode.STROKE);
        setColor(paint, obj, style.getDrawColor());
        float th = (float) handler.ThicknessToMathWidth(style);
        applyThickness(th, paint);
        return paint;
    }

    protected Paint createFillPaint(MathObject obj, DrawStyleProperties style) {
        Paint paint = new Paint();
        paint.setMode(PaintMode.FILL);
        setColor(paint, obj, style.getFillColor());
        return paint;
    }

    protected Paint createFillAndDrawPaint(MathObject obj, DrawStyleProperties style) {
        Paint paint = new Paint();
        paint.setMode(PaintMode.STROKE_AND_FILL);
        setColor(paint, obj, style.getDrawColor());
        //Stroke width 4=height of media???
        float th = (float) handler.ThicknessToMathWidth(style);
        applyThickness(th, paint);
        return paint;
    }

    private void applyThickness(float th, Paint paint) {
        paint.setStrokeWidth(th);
    }


    private void setColor(Paint paint, MathObject obj, PaintStyle color) {
        if (color instanceof JMColor) {
            JMColor jmColor = (JMColor) color;
            paint.setColor4f(new Color4f((float) jmColor.getRed(), (float) jmColor.getGreen(), (float) jmColor.getBlue(), (float) jmColor.getAlpha()));
        }
        if (color instanceof JMLinearGradient) {
            paint.setShader(buildLinearGradient(obj, (JMLinearGradient) color));
        }
        if (color instanceof JMRadialGradient) {
            paint.setShader(buildRadialGradient(obj, (JMRadialGradient) color));
        }
    }

    private Shader buildRadialGradient(MathObject obj, JMRadialGradient jmRadialGradient) {
        Vec vCenter;
        float radius;

        if (jmRadialGradient.isRelativeToShape()) {
            Rect bb=obj.getBoundingBox();
             radius = (float) Math.max(bb.getHeight(), bb.getWidth());

            vCenter=bb.getCenter();

        } else {
            vCenter=jmRadialGradient.getCenter();
            radius= (float) jmRadialGradient.getRadius();
        }





        GradientStop stops = jmRadialGradient.getStops();
        int[] colors = new int[stops.size()];
        float[] stopsf = new float[stops.size()];

        int i = 0;
        for (Map.Entry<Double, JMColor> entry : stops.getColorTreeMap().entrySet()) {
            colors[i] = jmColorToInt(entry.getValue());
            stopsf[i] = entry.getKey().floatValue();
            i++;
        }

        return Shader.makeRadialGradient((float) vCenter.x, (float) vCenter.y, radius, colors, stopsf);

    }

    private Shader buildLinearGradient(MathObject obj, JMLinearGradient jmLinearGradient) {
        Vec vStart, vEnd;
        if (jmLinearGradient.isRelativeToShape()) {
            Rect bb=obj.getBoundingBox();
            vStart = bb.getRelVec(jmLinearGradient.getStart());
            vEnd = bb.getRelVec(jmLinearGradient.getEnd());

        } else {
            vStart = jmLinearGradient.getStart();
            vEnd = jmLinearGradient.getEnd();
        }
        GradientStop stops = jmLinearGradient.getStops();
        int[] colors = new int[stops.size()];
        float[] stopsf = new float[stops.size()];

        int i = 0;
        for (Map.Entry<Double, JMColor> entry : stops.getColorTreeMap().entrySet()) {
            colors[i] = jmColorToInt(entry.getValue());
            stopsf[i] = entry.getKey().floatValue();
            i++;
        }
        return Shader.makeLinearGradient((float) vStart.x, (float) vStart.y, (float) vEnd.x, (float) vEnd.y, colors, stopsf);
    }


    /**
     * Convert JMatnAnim color object to Skija color object
     *
     * @param jmColor JMathAnim color object
     * @return The equivalente Skija Color4f object
     */
    public Color4f JMColorToColor4f(JMColor jmColor) {
        return new Color4f((float) jmColor.getRed(), (float) jmColor.getGreen(), (float) jmColor.getBlue(), (float) jmColor.getAlpha());
    }

    public int jmColorToInt(JMColor color) {
        return doubleToSkijaColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    private int doubleToSkijaColor(double r, double g, double b, double a) {
        r = Math.max(0.0f, Math.min(1.0f, r));
        g = Math.max(0.0f, Math.min(1.0f, g));
        b = Math.max(0.0f, Math.min(1.0f, b));
        a = Math.max(0.0f, Math.min(1.0f, a));

        int aInt = (int) (a * 255) << 24;
        int rInt = (int) (r * 255) << 16;
        int gInt = (int) (g * 255) << 8;
        int bInt = (int) (b * 255);
        return aInt | rInt | gInt | bInt;
    }


    /**
     * Computes inverse Skija matrix
     *
     * @param m Matrix
     * @return inverse matrix
     */
    public Matrix33 getInverseMatrix(Matrix33 m) {
        float[] mat = m.getMat();
        float a = mat[0], b = mat[1], c = mat[2];
        float d = mat[3], e = mat[4], f = mat[5];
        float g = mat[6], h = mat[7], i = mat[8];

        float det = a * (e * i - f * h) -
                b * (d * i - f * g) +
                c * (d * h - e * g);

        if (Math.abs(det) < 1e-6) return null;

        float invDet = 1.0f / det;

        float[] inv = new float[9];
        inv[0] = (e * i - f * h) * invDet;
        inv[1] = (c * h - b * i) * invDet;
        inv[2] = (b * f - c * e) * invDet;
        inv[3] = (f * g - d * i) * invDet;
        inv[4] = (a * i - c * g) * invDet;
        inv[5] = (c * d - a * f) * invDet;
        inv[6] = (d * h - e * g) * invDet;
        inv[7] = (b * g - a * h) * invDet;
        inv[8] = (a * e - b * d) * invDet;

        return new Matrix33(inv);
    }

    /**
     * Apply transform matrix to a 2d point
     *
     * @param m Matrix
     * @param x x coordinate
     * @param y y coordinate
     * @return a float[] with transformed coordinates
     */
    public float[] applyMatrix(Matrix33 m, float x, float y) {
        float[] mat = m.getMat();
        float xNew = mat[0] * x + mat[1] * y + mat[2];
        float yNew = mat[3] * x + mat[4] * y + mat[5];
        float w = mat[6] * x + mat[7] * y + mat[8];

        if (Math.abs(w) < 1e-6) w = 1; // evitar división por cero

        return new float[]{xNew / w, yNew / w};
    }

    /**
     * Computes the appropriate projection camera for an object to be drawn with absolute size
     *
     * @param cameraMatrixObject Camera to project
     * @param anchor             Anchor point.
     * @param fixedCameraMatrix  Fixed camera, that determines the absolute object size
     * @return The projection camera to be used to compute screen coordinates.
     */
    public Matrix33 projectToCamera(Matrix33 cameraMatrixObject, Vec anchor, Matrix33 fixedCameraMatrix) {
        Matrix33 inv = getInverseMatrix(fixedCameraMatrix);
        float[] coordOrig = new float[]{(float) anchor.x, (float) anchor.y};
        float[] screenCoordinates = applyMatrix(cameraMatrixObject, coordOrig[0], coordOrig[1]);
        float[] coo = applyMatrix(inv, screenCoordinates[0], screenCoordinates[1]);
        Matrix33 resul = Matrix33.makeTranslate(coo[0] - coordOrig[0], coo[1] - coordOrig[1]);
        resul = fixedCameraMatrix.makeConcat(resul);//First translate, second change of coordinates
        return resul;
    }
}
