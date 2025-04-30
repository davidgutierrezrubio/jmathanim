package com.jmathanim.Renderers.SkijaRenderer;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Shape;
import io.github.humbleui.skija.Surface;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class SkijaSwingHandler extends SkijaHandler {


    private final AtomicBoolean keepRunning;
    private SkijaSwingPreviewWindow previewWindow;
    AtomicReference<JFrame> frameRef = new AtomicReference<>();


    public SkijaSwingHandler(JMathAnimConfig config, AtomicBoolean keepRunning) {
        super(config);
        this.keepRunning=keepRunning;

    }

    @Override
    protected void preparePreviewWindow() {
        if (config.isShowPreview()) { //Initialize preview window if flag config set, null otherwise
            this.previewWindow = new SkijaSwingPreviewWindow(config.mediaW, config.mediaH, keepRunning, frameRef);
            previewWindow.show();
        } else {
            this.previewWindow = null;
        }
    }

    @Override
    public void initialize() {
        super.initialize();
        JMathAnimScene.logger.debug("Initializing Skija renderer");
        this.surface = Surface.makeRasterN32Premul(config.mediaW, config.mediaH);
        this.canvas = surface.getCanvas();

        skijaUtils = new SkijaUtils(this);
        cameraMatrix.put(camera, skijaUtils.createCameraView(camera));
        cameraMatrix.put(fixedCamera, skijaUtils.createCameraView(fixedCamera));

        preparePreviewWindow();

    }

    @Override
    protected void closeWindow() {
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
    }

    @Override
    public void drawPath(Shape mobj, Camera camera) {

        canvas.save();
        //Check if transform is created for this camera in this frame...
        canvas.concat(retrieveCameraMatrix(camera));
        applyPaintCommands(mobj);
        canvas.restore();
    }

    @Override
    protected boolean isPreviewWindowVisible() {
        return previewWindow.isVisible();
    }
    @Override
    protected void updateImagePreviewWindow(BufferedImage image) {
        previewWindow.updateImage(image);
    }
    @Override
    protected BufferedImage getRenderedImage(int frameCount) {
        return SkijaToBufferedImage.convertTo3ByteBGR(surface);
    }

}
