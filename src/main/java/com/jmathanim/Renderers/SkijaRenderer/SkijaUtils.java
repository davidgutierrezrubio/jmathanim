package com.jmathanim.Renderers.SkijaRenderer;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Styling.JMColor;
import com.jmathanim.Styling.PaintStyle;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.Shape;
import io.github.humbleui.skija.*;

import java.util.HashMap;


class SkijaUtils {
    private final JMathAnimConfig config;
    private final HashMap<JMPath, Path> paths;
    private final SkijaRenderer renderer;

    public SkijaUtils(JMathAnimConfig config, SkijaRenderer skijaRenderer) {
        this.config = config;
        renderer = skijaRenderer;
        paths = new HashMap<>();
    }

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

    protected Path convertJMPathToSkijaPath(JMPath jmpath) {
//        if (paths.containsKey(jmpath)) {
//            return paths.get(jmpath);
//        }

        Path path = new Path();
        Vec prev = jmpath.jmPathPoints.get(0).p.v.copy();
        path.moveTo((float) prev.x, (float) prev.y);
        for (int n = 1; n < jmpath.size() + 1; n++) {
            Vec point = jmpath.jmPathPoints.get(n).p.v;
            Vec cpoint1 = jmpath.jmPathPoints.get(n - 1).cpExit.v;
            Vec cpoint2 = jmpath.jmPathPoints.get(n).cpEnter.v;

            if (jmpath.jmPathPoints.get(n).isThisSegmentVisible) {
                JMPathPoint jp = jmpath.jmPathPoints.get(n);
                //JavaFX has problems drawin CubicCurves when control points are equal than points
                if ((!jp.isCurved) || ((isAbsEquiv(prev, cpoint1, .1)) && (isAbsEquiv(point, cpoint2, .0001)))) {
                    path.lineTo((float) point.x, (float) point.y);
                } else {
                    path.cubicTo((float) cpoint1.x, (float) cpoint1.y, (float) cpoint2.x, (float) cpoint2.y, (float) point.x, (float) point.y);
                }
            } else {
                path.moveTo((float) point.x, (float) point.y);
            }
            prev.copyFrom(point);
        }
//        paths.put(jmpath, path);
        return path;
    }

    protected boolean isAbsEquiv(Vec a, Vec b, double epsilon) {
        final double nn = Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
        return nn < epsilon;
    }

    protected Paint createDrawPaint(Shape mobj) {
        Paint paint = new Paint();
        paint.setMode(PaintMode.STROKE);
        setColor(paint, mobj.getMp().getDrawColor());
        float th = mobj.getMp().getThickness().floatValue();
        applyThickness(mobj, paint);
        return paint;
    }

    protected Paint createFillPaint(Shape mobj) {
        Paint paint = new Paint();
        paint.setMode(PaintMode.FILL);
        setColor(paint, mobj.getMp().getFillColor());
        return paint;
    }

    protected Paint createFillAndDrawPaint(Shape mobj) {
        Paint paint = new Paint();
        paint.setMode(PaintMode.STROKE_AND_FILL);
        setColor(paint, mobj.getMp().getDrawColor());
        float th = mobj.getMp().getThickness().floatValue();
        //Stroke width 4=height of media???
        applyThickness(mobj, paint);
        return paint;
    }

    private void applyThickness(Shape mobj, Paint paint) {
        paint.setStrokeWidth((float) renderer.ThicknessToMathWidth(mobj.getMp().getThickness()));
    }


    private void setColor(Paint paint, PaintStyle color) {
        if (color instanceof JMColor) {
            JMColor jmColor = (JMColor) color;
            paint.setColor4f(new Color4f((float) jmColor.r, (float) jmColor.g, (float) jmColor.b, (float) jmColor.getAlpha()));
        }
    }

    public Color4f JMColorToColor4f(JMColor jmColor) {
        return new Color4f((float) jmColor.r, (float) jmColor.g, (float) jmColor.b, (float) jmColor.getAlpha());
    }

    public Matrix33 getInverseMatrix(Matrix33 m) {
        float[] mat = m.getMat(); // matriz en orden de filas: 9 elementos
        float a = mat[0], b = mat[1], c = mat[2];
        float d = mat[3], e = mat[4], f = mat[5];
        float g = mat[6], h = mat[7], i = mat[8];

        float det = a * (e * i - f * h) -
                b * (d * i - f * g) +
                c * (d * h - e * g);

        if (Math.abs(det) < 1e-6) return null; // No invertible

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

    public float[] applyMatrix(Matrix33 m, float x, float y) {
        float[] mat = m.getMat();
        float xNew = mat[0] * x + mat[1] * y + mat[2];
        float yNew = mat[3] * x + mat[4] * y + mat[5];
        float w = mat[6] * x + mat[7] * y + mat[8];

        if (Math.abs(w) < 1e-6) w = 1; // evitar división por cero

        return new float[]{xNew / w, yNew / w};
    }

    public Matrix33 projectToCamera(Matrix33 cameraMatrixObject, Vec anchor, Matrix33 fixedCameraMatrix) {
        Matrix33 inv = getInverseMatrix(fixedCameraMatrix);
        float[] coordOrig = new float[]{(float) anchor.x, (float) anchor.y};
        float[] screenCoordinates = applyMatrix(cameraMatrixObject, coordOrig[0],coordOrig[1]);
        float[] coo = applyMatrix(inv, screenCoordinates[0], screenCoordinates[1]);
         Matrix33 resul = Matrix33.makeTranslate(coo[0] - coordOrig[0], coo[1] - coordOrig[1]);
        resul= fixedCameraMatrix.makeConcat(resul);//First translate, second change of coordinates
        return resul;
    }
}
