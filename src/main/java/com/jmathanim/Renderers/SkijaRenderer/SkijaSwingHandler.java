package com.jmathanim.Renderers.SkijaRenderer;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.AbstractJMImage;
import com.jmathanim.mathobjects.Shape;
import io.github.humbleui.skija.*;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class SkijaSwingHandler extends SkijaHandler {


    private final AtomicBoolean keepRunning;
    AtomicReference<JFrame> frameRef = new AtomicReference<>();
    private SkijaSwingPreviewWindow previewWindow;


    public SkijaSwingHandler(JMathAnimConfig config, AtomicBoolean keepRunning) {
        super(config);
        this.keepRunning = keepRunning;

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
        this.backgroundSurface = Surface.makeRasterN32Premul(config.mediaW, config.mediaH);
        this.backgroundCanvas = backgroundSurface.getCanvas();

        if (config.isDrawShadow()) {
            this.shadowSurface = Surface.makeRasterN32Premul(config.mediaW, config.mediaH);
            this.shadowCanvas = shadowSurface.getCanvas();
        }

        this.objectsSurface = Surface.makeRasterN32Premul(config.mediaW, config.mediaH);
        this.objectsCanvas = objectsSurface.getCanvas();

        if (!config.isDebugLayerDisabled()) {
            this.debugSurface = Surface.makeRasterN32Premul(config.mediaW, config.mediaH);
            this.debugCanvas = debugSurface.getCanvas();
        }

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
    public void drawPath(Shape shape, Camera camera) {

        objectsCanvas.save();
        //Check if transform is created for this camera in this frame...
        objectsCanvas.concat(retrieveCameraMatrix(camera));
        applyPaintCommands(shape);
        objectsCanvas.restore();
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
        //Here I should perform merge all surfaces into surface
        Image objectsImage = objectsSurface.makeImageSnapshot();


        //First, draw shadow if applicable
        if (config.isDrawShadow()) {
//            int shadowInt = (int) Math.round(Math.max(0, Math.min(1, config.shadowAlpha)) * 255);
//            int shadowColor = (shadowInt << 24); // Negro: R=0, G=0, B=0, solo el canal A cambia
//            ImageFilter blur = ImageFilter.makeBlur(config.shadowKernelSize, config.shadowKernelSize, FilterTileMode.CLAMP);
//            Paint shadowPaint = new Paint()
//                    .setColorFilter(ColorFilter.makeBlend(shadowColor, BlendMode.SRC_IN))
//                    .setImageFilter(blur);
//            backgroundCanvas.save();
//            backgroundCanvas.translate(config.shadowOffsetX, config.shadowOffsetY);
//            backgroundCanvas.drawImage(objectsImage, 0f, 0f, shadowPaint);
//            backgroundCanvas.restore();

//OTRA FORMA
//            Paint shadowPaint = new Paint()
//                    .setColor(0x55000000); // Opaco con alfa reducido
//
//            backgroundCanvas.save();
//            backgroundCanvas.translate(config.shadowOffsetX, config.shadowOffsetY);
//            backgroundCanvas.drawImage(objectsImage, 0f, 0f, shadowPaint);
//            backgroundCanvas.restore();



            float scaleFactor = 0.1f; // o el que quieras, según calidad
            int smallW = (int)(objectsImage.getWidth() * scaleFactor);
            int smallH = (int)(objectsImage.getHeight() * scaleFactor);

// Renderizado más pequeño
            Surface smallSurface = Surface.makeRasterN32Premul(smallW, smallH);
            Canvas smallCanvas = smallSurface.getCanvas();
            smallCanvas.scale(scaleFactor, scaleFactor); // Escala inversa al dibujar
            smallCanvas.drawImage(objectsImage, 0f, 0f);

// Aplicar blur y dibujar
            ImageFilter blur = ImageFilter.makeBlur(config.shadowKernelSize, config.shadowKernelSize, FilterTileMode.CLAMP);
            Paint shadowPaint = new Paint().setImageFilter(blur);
            backgroundCanvas.save();
            backgroundCanvas.translate(config.shadowOffsetX, config.shadowOffsetY);
            backgroundCanvas.scale(1 / scaleFactor, 1 / scaleFactor); // Escala hacia arriba
            backgroundCanvas.drawImage(smallSurface.makeImageSnapshot(), 0f, 0f, shadowPaint);
            backgroundCanvas.restore();


        }
        //Then, objects...
        backgroundCanvas.drawImage(objectsImage, 0f, 0f);

        //And finally, debug layer, if applicable
        if (!config.isDebugLayerDisabled()) {
            Image debugImage=debugSurface.makeImageSnapshot();
            backgroundCanvas.drawImage(debugImage, 0f, 0f);
        }

        return SkijaToBufferedImage.convertTo3ByteBGR(backgroundSurface);
    }

    @Override
    protected void drawImage(AbstractJMImage image, Camera cam,Image img) {
        Rect bb = image.getBoundingBox();
        Vec v=bb.getDL().v;
        ImageInfo info=img.getImageInfo();
        objectsCanvas.save();
        //Check if transform is created for this camera in this frame...
        objectsCanvas.concat(retrieveCameraMatrix(camera));
        objectsCanvas.scale((float) (bb.getWidth() / info.getWidth()), -(float) (bb.getHeight() / info.getHeight()));
//        objectsCanvas.translate(-)
//        objectsCanvas.drawImage(img, 300,300);
        objectsCanvas.drawImage(img, (float) v.x, (float) v.y);
        objectsCanvas.restore();
    }


}
