package com.jmathanim.Renderers.SkijaRenderer;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.MathObjects.AbstractJMImage;
import com.jmathanim.MathObjects.AbstractShape;
import com.jmathanim.MathObjects.MathObject;
import com.jmathanim.Renderers.FXRenderer.JavaFXRenderer;
import com.jmathanim.Renderers.MovieEncoders.SoundItem;
import com.jmathanim.Renderers.MovieEncoders.XugglerVideoEncoder;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Styling.RendererEffects;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimConfig;
import com.jmathanim.jmathanim.JMathAnimScene;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SkijaRenderer extends Renderer {
    private static final double XMIN_DEFAULT = -2;
    private static final double XMAX_DEFAULT = 2;
    private final double correctionThickness;
    private final Camera camera;
    private final Camera fixedCamera;
    private final AtomicBoolean keepRunning = new AtomicBoolean(true);
    private final SkijaHandler skijaHandler;
    private XugglerVideoEncoder videoEncoder;
    private File saveFilePath;

    public SkijaRenderer(JMathAnimScene parentScene) {
        super(parentScene);//super method initializes config object
                camera = new Camera(scene, config.getMediaWidth(), config.getMediaHeight());
        fixedCamera = new Camera(scene, config.getMediaWidth(), config.getMediaHeight());
        camera.initialize(XMIN_DEFAULT, XMAX_DEFAULT, 0);
        fixedCamera.initialize(XMIN_DEFAULT, XMAX_DEFAULT, 0);
        correctionThickness = config.getMediaWidth() * 1d / 1066;//Correction factor for thickness
        skijaHandler = new SkijaSwingHandler(JMathAnimConfig.getConfig(),keepRunning);
        //Gl handler is slow??
//        skijaHandler = new SkijaGLHandler(JMathAnimConfig.getConfig(), keepRunning);
        skijaHandler.setRenderer(this);


    }

    @Override
    public RendererEffects buildRendererEffects() {
        return null;
    }

    @Override
    public void initialize() {
        skijaHandler.initialize();
        try {
            prepareEncoder();
        } catch (Exception ex) {
            JMathAnimScene.logger.error("Error creating video encoder");
            Logger.getLogger(JavaFXRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    private void prepareEncoder() throws Exception {
        if (config.isCreateMovie()) {
            JMathAnimScene.logger.debug("Preparing video encoder");
            videoEncoder = new XugglerVideoEncoder();
            File tempPath = new File(config.getOutputDir().getCanonicalPath());
            tempPath.mkdirs();
            saveFilePath = new File(config.getOutputDir().getCanonicalPath() + File.separator
                    + config.getOutputFileName() + "_" + config.getMediaHeight() + ".mp4");
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
        if (config.isShowPreview() && skijaHandler.isPreviewWindowVisible()) {
            skijaHandler.updateImagePreviewWindow(renderedImage);
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

        JMathAnimScene.logger.info(
                String.format("%d frames created, %.2fs total time", frameCount, (1.f * frameCount) / config.getFps()));

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


        skijaHandler.finish();
    }

    @Override
    protected BufferedImage getRenderedImage(int frameCount) {
        BufferedImage img = skijaHandler.getRenderedImage(frameCount);
        return img;
    }

    @Override
    public void clearAndPrepareCanvasForAnotherFrame() {
        super.clearAndPrepareCanvasForAnotherFrame();
        skijaHandler.clearAndPrepareCanvasForAnotherFrame();
    }

    @Override
    public void drawPath(AbstractShape<?> mobj) {
        Camera cam = mobj.getCamera();
        if (cam == null) {
            cam = camera;
        }
        drawPath(mobj, cam);
    }

    @Override
    public void drawPath(AbstractShape<?> mobj, Vec shiftVector, Camera camera) {
        skijaHandler.drawPath(mobj, camera);
    }

    @Override
    public void drawAbsoluteCopy(AbstractShape<?> sh, Vec anchor) {
        skijaHandler.drawAbsoluteCopy(sh, anchor, fixedCamera);
    }


    @Override
    public com.jmathanim.Utils.Rect createImage(InputStream stream) {
        JMathAnimScene.logger.warn("createImage not implemented yet, sorry!");
        return null;
    }

    @Override
    public void drawImage(AbstractJMImage<?> obj, Camera cam) {
        JMathAnimScene.logger.warn("drawImage not implemented yet, sorry!");

    }

    @Override
    public void debugText(String text, Vec loc) {
        JMathAnimScene.logger.warn("debugText not implemented yet, sorry!");

    }

    @Override
    public double MathWidthToThickness(double w) {
        return skijaHandler.MathWidthToThickness(w);
    }

    @Override
    public double ThicknessToMathWidth(double th) {
        return skijaHandler.ThicknessToMathWidth(th);
    }

    @Override
    public double ThicknessToMathWidth(MathObject obj) {
        return skijaHandler.ThicknessToMathWidth(obj.getMp());
    }

    @Override
    public void addSound(SoundItem soundItem) {
        JMathAnimScene.logger.warn("addSound not implemented yet, sorry!");

    }
}

