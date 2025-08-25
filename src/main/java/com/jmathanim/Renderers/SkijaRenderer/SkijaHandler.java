package com.jmathanim.Renderers.SkijaRenderer;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Styling.DrawStyleProperties;
import com.jmathanim.Styling.PaintStyle;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.AbstractShape;
import io.github.humbleui.skija.*;

import java.awt.image.BufferedImage;
import java.util.HashMap;

public abstract class SkijaHandler {
    protected final JMathAnimConfig config;
    protected Surface surface;
    protected Canvas canvas;
    protected SkijaUtils skijaUtils;
    protected Matrix33 transformCamera;
    protected final HashMap<Camera, Matrix33> cameraMatrix;
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
    protected void drawPath(AbstractShape<?> mobj, Matrix33 mat) {
        canvas.save();
        canvas.concat(mat);
        applyPaintCommands(mobj);
        canvas.restore();
    }




    public void drawAbsoluteCopy(AbstractShape<?> sh, Vec anchor,Camera fixedCamera) {
        Matrix33 projecToCameraMat = skijaUtils.projectToCamera(retrieveCameraMatrix(sh.getCamera()), anchor, retrieveCameraMatrix(fixedCamera));

        drawPath(sh, projecToCameraMat);
    }

    protected void applyPaintCommands(AbstractShape<?> mobj) {
        PaintStyle drawStyle = mobj.getMp().getDrawColor();
        PaintStyle fillStyle = mobj.getMp().getFillColor();
        Path path = skijaUtils.convertJMPathToSkijaPath(mobj.getPath());
        if (drawStyle.equals(fillStyle)) {
            Paint paint = skijaUtils.createFillAndDrawPaint(mobj,mobj.getMp());
            canvas.drawPath(path, paint);
        } else {
            //Fill and draw contour
            Paint paintFill = skijaUtils.createFillPaint(mobj,mobj.getMp());
            canvas.drawPath(path, paintFill);
            Paint paintStroke = skijaUtils.createDrawPaint(mobj,mobj.getMp());
            canvas.drawPath(path, paintStroke);
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
        this.camera=config.getCamera();
        this.fixedCamera=config.getFixedCamera();
        skijaUtils = new SkijaUtils(this);
    }

    protected abstract void closeWindow();

    public abstract void drawPath(AbstractShape<?> mobj, Camera camera);



    public double MathWidthToThickness(double w) {
        return w * 1066;
    }

    public double ThicknessToMathWidth(double th) {
        return th / 1066;
    }

    public double ThicknessToMathWidth(DrawStyleProperties drawStyleProperties) {
        Camera cam = (drawStyleProperties.isAbsoluteThickness() ? fixedCamera : camera);
        return drawStyleProperties.getThickness() / 1066 * 4 / cam.getMathView().getWidth();
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
    protected abstract  BufferedImage getRenderedImage(int frameCount);


    protected void clearAndPrepareCanvasForAnotherFrame() {
        //This should be done in Skija Thread
        canvas.clear(0xFFFFFFFF);//TODO: Add colors or background images
        cameraMatrix.clear();//Clear transform matrices from all cameras
    }

    protected abstract boolean isPreviewWindowVisible();

    protected abstract void updateImagePreviewWindow(BufferedImage image);


    public void setRenderer(SkijaRenderer skijaRenderer) {
        this.renderer=skijaRenderer;
    }

    public void finish() {
        closeWindow();
    }
}

