package com.jmathanim.Renderers.SkijaRenderer;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Styling.JMColor;
import com.jmathanim.Styling.JMGradient;
import com.jmathanim.Styling.PaintStyle;
import com.jmathanim.Styling.Stylable;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Shape;
import io.github.humbleui.skija.*;
import org.apache.tools.ant.types.selectors.SelectSelector;
import org.slf4j.ILoggerFactory;

import java.awt.image.BufferedImage;
import java.util.HashMap;

public abstract class SkijaHandler {
    protected final JMathAnimConfig config;
    protected final HashMap<Camera, Matrix33> cameraMatrix;
    protected Surface surface;
    protected Canvas canvas;
    protected SkijaUtils skijaUtils;
    protected Matrix33 transformCamera;
    protected Camera camera;
    protected Camera fixedCamera;
    protected SkijaRenderer renderer;


    public SkijaHandler(JMathAnimConfig config) {
        this.config = config;
        cameraMatrix = new HashMap<>();

    }


    /**
     * Overloaded method for use with drawAbsoluteCopy
     *
     * @param mobj Shape to draw
     * @param mat  Transformation matrix
     */
    protected void drawPath(Shape mobj, Matrix33 mat) {
        canvas.save();
        canvas.concat(mat);
        applyPaintCommands(mobj);
        canvas.restore();
    }


    public void drawAbsoluteCopy(Shape sh, Vec anchor, Camera fixedCamera) {
        Shape shape = sh.copy();
        Matrix33 projecToCameraMat = skijaUtils.projectToCamera(retrieveCameraMatrix(sh.getCamera()), anchor, retrieveCameraMatrix(fixedCamera));

        drawPath(sh, projecToCameraMat);
    }

    protected void applyPaintCommands(Shape mobj) {
        PaintStyle drawStyle = mobj.getMp().getDrawColor();
        PaintStyle fillStyle = mobj.getMp().getFillColor();
        Path path = skijaUtils.convertJMPathToSkijaPath(mobj.getPath());
        boolean draw = (drawStyle.getAlpha() > 0);
        boolean fill = (fillStyle.getAlpha() > 0);

        if (!draw & !fill) return;

        if (drawStyle.equals(fillStyle)) {
            Paint paint = skijaUtils.createFillAndDrawPaint(mobj);
            canvas.drawPath(path, paint);
        } else {
            //Fill and draw contour
            if (fill) {
                Paint paintFill = skijaUtils.createFillPaint(mobj);
                canvas.drawPath(path, paintFill);
            }
            if (draw) {
                Paint paintStroke = skijaUtils.createDrawPaint(mobj);
                canvas.drawPath(path, paintStroke);
            }
        }
    }


    protected Matrix33 retrieveCameraMatrix(Camera camera) {
        if (cameraMatrix.containsKey(camera)) {
            return cameraMatrix.get(camera);
        } else {
            transformCamera = skijaUtils.createCameraView(camera);
            cameraMatrix.put(camera, transformCamera);
            return transformCamera;
        }
    }

    abstract protected void preparePreviewWindow();

    public void initialize() {
        this.camera = config.getCamera();
        this.fixedCamera = config.getFixedCamera();
        skijaUtils = new SkijaUtils(this);
    }

    protected abstract void closeWindow();

    public abstract void drawPath(Shape mobj, Camera camera);


    public double MathWidthToThickness(double w) {
        return w * 1066;
    }


    public double ThicknessToMathWidth(MathObject obj) {
        //It seems in skija, thickness 4 equals to full width of screen aware of resolution

        Double thickness = obj.getMp().getThickness();
        if (obj.getMp().isAbsoluteThickness()) {
            double l=camera.getWidth()/fixedCamera.getWidth();
            double th = thickness / 250d / fixedCamera.getWidth();
//            return th*l;
            return (obj.isAbsoluteSize() ? th : th *l);
        }
        else {
            return  thickness/250d / camera.getWidth();
        }

//        double a = thickness*250d / cam.getWidth();
//        double b = thickness / 1066d * 4d / cam.getWidth();
//        a=a*camera.getWidth()/fixedCamera.getWidth();
//        return a;
    }

    //
//    public void saveFrame(int frameCount) {
//        BufferedImage renderedImage = getRenderedImage(frameCount);
//        if (config.isShowPreview() && previewWindow.isVisible()) {
//            previewWindow.updateImage(renderedImage);
//        }
//        if (config.isCreateMovie()) {
//            videoEncoder.writeFrame(renderedImage, frameCount);
//        }
//        if (config.isSaveToPNG()) {
//            String filename = config.getOutputFileName() + String.format("%06d", frameCount) + ".png";
//            writeImageToPNG(filename, renderedImage, "png");
//        }
//    }
    protected abstract BufferedImage getRenderedImage(int frameCount);


    protected void clearAndPrepareCanvasForAnotherFrame() {
        //This should be done in Skija Thread
        PaintStyle color = config.getBackgroundColor();
        if (color instanceof JMColor) {
            JMColor jmColor = (JMColor) color;
            canvas.clear(skijaUtils.jmColorToInt(jmColor));
        }
        if (color instanceof JMGradient) {
            JMGradient jmGradient = (JMGradient) color;
            JMathAnimScene.logger.warn("Gradient background not implemented yet");
        }
        cameraMatrix.clear();//Clear transform matrices from all cameras
    }

    ;

    protected abstract boolean isPreviewWindowVisible();

    protected abstract void updateImagePreviewWindow(BufferedImage image);


    public void setRenderer(SkijaRenderer skijaRenderer) {
        this.renderer = skijaRenderer;
    }

    public void finish() {
        closeWindow();
    }
}

