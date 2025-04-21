package com.jmathanim.Renderers.SkijaRenderer;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Renderers.FXRenderer.JavaFXRenderer;
import com.jmathanim.Renderers.MovieEncoders.SoundItem;
import com.jmathanim.Renderers.MovieEncoders.XugglerVideoEncoder;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Styling.PaintStyle;
import com.jmathanim.Styling.RendererEffects;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.AbstractJMImage;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Shape;
import io.github.humbleui.skija.*;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SkijaRenderer extends Renderer {
    private static final double XMIN_DEFAULT = -2;
    private static final double XMAX_DEFAULT = 2;
    private final double correctionThickness;
    private final Camera camera;
    private final Camera fixedCamera;
    private final AtomicBoolean keepRunning = new AtomicBoolean(true);
    private XugglerVideoEncoder videoEncoder;
    private File saveFilePath;
    private SkijaPreviewWindow previewWindow;
    private Surface surface;
    private Canvas canvas;
    AtomicReference<JFrame> frameRef = new AtomicReference<>();
    private Matrix33 transformCamera;
    private final HashMap<Camera,Matrix33> cameraMatrix;
    private final SkijaUtils skijaUtils;

    public SkijaRenderer(JMathAnimScene parentScene) {
        super(parentScene);//super method initializes config object
        camera = new Camera(scene, config.mediaW, config.mediaH);
        fixedCamera = new Camera(scene, config.mediaW, config.mediaH);
        camera.initialize(XMIN_DEFAULT, XMAX_DEFAULT, 0);
        fixedCamera.initialize(XMIN_DEFAULT, XMAX_DEFAULT, 0);
        correctionThickness = config.mediaW * 1d / 1066;//Correction factor for thickness
        cameraMatrix = new HashMap<>();
        skijaUtils = new SkijaUtils(config,this);

    }

    @Override
    public RendererEffects buildRendererEffects() {
        return null;
    }

    @Override
    public void initialize() {
        JMathAnimScene.logger.debug("Initializing Skija renderer");
        this.surface = Surface.makeRasterN32Premul(config.mediaW, config.mediaH);
        if (false) {

            DirectContext context = DirectContext.makeGL();
            try {
                Thread.sleep(50000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            this.surface = Surface.makeRenderTarget(
                    context,
                    true,
                    ImageInfo.makeN32Premul(config.mediaW, config.mediaH)
            );
            while (surface.getCanvas()==null) {
                System.out.println("Wait for opengl...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        cameraMatrix.put(camera,skijaUtils.createCameraView(camera));
        cameraMatrix.put(fixedCamera,skijaUtils.createCameraView(fixedCamera));

        System.out.println("NOOOOOO");
        this.canvas = surface.getCanvas();
        preparePreviewWindow();
        try {
            prepareEncoder();
        } catch (Exception ex) {
            JMathAnimScene.logger.error("Error creating video encoder");
            Logger.getLogger(JavaFXRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void preparePreviewWindow() {
        if (config.isShowPreview()) { //Initialize preview window if flag config set, null otherwise
            this.previewWindow = new SkijaPreviewWindow(config.mediaW, config.mediaH, keepRunning,frameRef);
            previewWindow.show();
        } else {
            this.previewWindow = null;
        }
    }


    public final void prepareEncoder() throws Exception {
        if (config.isCreateMovie()) {
            JMathAnimScene.logger.debug("Preparing video encoder");
            videoEncoder = new XugglerVideoEncoder();
            File tempPath = new File(config.getOutputDir().getCanonicalPath());
            tempPath.mkdirs();
            saveFilePath = new File(config.getOutputDir().getCanonicalPath() + File.separator
                    + config.getOutputFileName() + "_" + config.mediaH + ".mp4");
            JMathAnimScene.logger.info("Creating movie encoder for {}", saveFilePath);
            config.setSaveFilePath(saveFilePath);
            videoEncoder.createEncoder(config);
        }
//        if (config.drawShadow) {
//            dropShadow = new DropShadow();
//            dropShadow.setRadius(config.shadowKernelSize);
//            dropShadow.setOffsetX(config.shadowOffsetX);
//            dropShadow.setOffsetY(config.shadowOffsetY);
//            dropShadow.setColor(Color.color(0, 0, 0, config.shadowAlpha));
//        }
//
//        dropShadow = new DropShadow();
//        dropShadow.setRadius(config.shadowKernelSize);
//        dropShadow.setOffsetX(config.shadowOffsetX);
//        dropShadow.setOffsetY(config.shadowOffsetY);
//        dropShadow.setColor(Color.color(0, 0, 0, config.shadowAlpha));

    }


    @Override
    public <T extends Camera> T getCamera() {
        return (T) camera;
    }

    @Override
    public <T extends Camera> T getFixedCamera() {
        return (T) fixedCamera;
    }

    @Override
    public void saveFrame(int frameCount) {
        BufferedImage renderedImage = getRenderedImage(frameCount);
        if (config.isShowPreview() && previewWindow.isVisible()) {
            previewWindow.updateImage(renderedImage);
        }
        if (config.isCreateMovie()) {
            videoEncoder.writeFrame(renderedImage, frameCount);
        }
        if (config.isSaveToPNG()) {
            String filename = config.getOutputFileName() + String.format("%06d", frameCount) + ".png";
            writeImageToPNG(filename, renderedImage, "png");
        }
    }

    @Override
    public void finish(int frameCount) {
        if (config.isShowPreview()) {
            JMathAnimScene.logger.debug("Closing preview window");

            while (frameRef.get() == null) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            JFrame frame = frameRef.get();
                SwingUtilities.invokeLater(frame::dispose);
        }
        JMathAnimScene.logger.info(
                String.format("%d frames created, %.2fs total time", frameCount, (1.f * frameCount) / config.fps));
        if (config.isCreateMovie()) {
            /**
             * Encoders, like decoders, sometimes cache pictures so it can do
             * the right key-frame optimizations. So, they need to be flushed as
             * well. As with the decoders, the convention is to pass in a null
             * input until the output is not complete.
             */
            JMathAnimScene.logger.info("Finishing movie...");
            videoEncoder.finish();
            if (videoEncoder.isFramesGenerated()) {
                JMathAnimScene.logger.info("Movie created at " + saveFilePath);
            }

        }



    }

    @Override
    protected BufferedImage getRenderedImage(int frameCount) {
        return SkijaToBufferedImage.convertTo3ByteBGR(surface);
    }

    @Override
    public void clearAndPrepareCanvasForAnotherFrame() {
        canvas.clear(0xFFFFFFFF);//TODO: Add colors or background images
        cameraMatrix.clear();//Clear transform matrices from all cameras
    }

    @Override
    public void drawPath(Shape mobj) {
        Camera cam = mobj.getCamera();
        if (cam == null) {
            cam = camera;
        }
        drawPath(mobj, cam);
    }

    @Override
    public void drawPath(Shape mobj, Camera camera) {

        canvas.save();
        //Check if transform is created for this camera in this frame...
        canvas.concat(retrieveCameraMatrix(camera));

        applyPaintCommands(mobj);
        canvas.restore();
    }

    private Matrix33 retrieveCameraMatrix(Camera camera) {
        if (cameraMatrix.containsKey(camera)) {
            return cameraMatrix.get(camera);
        }else{
            transformCamera = skijaUtils.createCameraView(camera);
            cameraMatrix.put(camera, transformCamera);
            return transformCamera;
        }
    }

    /**
     * Overloaded method for use with drawAbsoluteCopy
     * @param mobj Shape to draw
     * @param mat Transformation matrix
     */
    private void drawPath(Shape mobj,Matrix33 mat) {
        canvas.save();
        canvas.concat(mat);
        applyPaintCommands(mobj);
        canvas.restore();
    }

    private void applyPaintCommands(Shape mobj) {
        PaintStyle drawStyle= mobj.getMp().getDrawColor();
        PaintStyle fillStyle= mobj.getMp().getFillColor();
        Path path = skijaUtils.convertJMPathToSkijaPath(mobj.getPath());
        if (drawStyle.equals(fillStyle)) {
            Paint paint=skijaUtils.createFillAndDrawPaint(mobj);
            canvas.drawPath(path, paint);
        }
        else {
            //Fill and draw contour
            Paint paintFill=skijaUtils.createFillPaint(mobj);
            canvas.drawPath(path, paintFill);
            Paint paintStroke=skijaUtils.createDrawPaint(mobj);
            canvas.drawPath(path, paintStroke);
        }
    }



    @Override
    public void drawAbsoluteCopy(Shape sh, Vec anchor) {
        Shape shape = sh.copy();
        Matrix33 projecToCameraMat = skijaUtils.projectToCamera(retrieveCameraMatrix(sh.getCamera()),anchor,retrieveCameraMatrix(fixedCamera));

        drawPath(sh, projecToCameraMat);
    }


    @Override
    public com.jmathanim.Utils.Rect createImage(InputStream stream) {
        JMathAnimScene.logger.warn("createImage not implemented yet, sorry!");
        return null;
    }

    @Override
    public void drawImage(AbstractJMImage obj, Camera cam) {
        JMathAnimScene.logger.warn("drawImage not implemented yet, sorry!");

    }

    @Override
    public void debugText(String text, Vec loc) {
        JMathAnimScene.logger.warn("debugText not implemented yet, sorry!");

    }

    @Override
    public double MathWidthToThickness(double w) {
        return w * 1066;
    }

    @Override
    public double ThicknessToMathWidth(double th) {
        return th / 1066;
    }

    @Override
    public double ThicknessToMathWidth(MathObject obj) {
        Camera cam = (obj.getMp().isAbsoluteThickness() ? fixedCamera : camera);
        return obj.getMp().getThickness() / 1066 * 4 / cam.getMathView().getWidth();
    }

    @Override
    public void addSound(SoundItem soundItem) {
        JMathAnimScene.logger.warn("addSound not implemented yet, sorry!");

    }
}

