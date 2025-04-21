package com.jmathanim.Renderers.SkijaRenderer;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.JMPathPoint;
import io.github.humbleui.skija.Matrix33;
import io.github.humbleui.skija.Path;

import java.util.HashMap;

public class SkijaUtils {
    private final JMathAnimConfig config;
    private final HashMap<JMPath,Path> paths;

    public SkijaUtils(JMathAnimConfig config) {
        this.config=config;
        paths = new HashMap<>();
    }
    public Matrix33 createCameraView(Camera camera) {
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

    public Path convertJMPathToSkijaPath(JMPath jmpath) {
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
    protected  boolean isAbsEquiv(Vec a, Vec b, double epsilon) {
        final double nn = Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
        return nn < epsilon;
    }
}
