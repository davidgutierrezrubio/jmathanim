package com.jmathanim.Renderers.SkijaRenderer;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Renderers.MovieEncoders.SoundItem;
import com.jmathanim.Renderers.MovieEncoders.XugglerVideoEncoder;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Styling.RendererEffects;
import com.jmathanim.Utils.*;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.AbstractJMImage;
import com.jmathanim.mathobjects.JMImage;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Shape;
import io.github.humbleui.skija.Image;
import io.github.humbleui.skija.ImageInfo;
import io.github.humbleui.skija.Matrix33;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class SkijaRenderer extends Renderer {
    private static final double XMIN_DEFAULT = -2;
    private static final double XMAX_DEFAULT = 2;
    private final double correctionThickness;
    private final Camera camera;
    private final Camera fixedCamera;
    private final AtomicBoolean keepRunning = new AtomicBoolean(true);
    private final SkijaHandler skijaHandler;
    private final HashMap<String, Image> imageDictionary;
    private XugglerVideoEncoder videoEncoder;
    private File saveFilePath;

    public SkijaRenderer(JMathAnimScene parentScene) {
        super(parentScene);//super method initializes config object
        camera = new Camera(scene, config.mediaW, config.mediaH);
        fixedCamera = new Camera(scene, config.mediaW, config.mediaH);
        camera.initialize(XMIN_DEFAULT, XMAX_DEFAULT, 0);
        fixedCamera.initialize(XMIN_DEFAULT, XMAX_DEFAULT, 0);
        correctionThickness = config.mediaW * 1d / 1066;//Correction factor for thickness
        skijaHandler = new SkijaSwingHandler(JMathAnimConfig.getConfig(), keepRunning);
        //Gl handler is slow??
//        skijaHandler = new SkijaGLHandler(JMathAnimConfig.getConfig(), keepRunning);
        skijaHandler.setRenderer(this);
        imageDictionary = new HashMap<>();


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
        }
    }


    private void prepareEncoder() throws Exception {
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


        skijaHandler.finish();
    }

    @Override
    protected BufferedImage getRenderedImage(int frameCount) {
        BufferedImage img = skijaHandler.getRenderedImage(frameCount);
        return img;
    }

    @Override
    public void clearAndPrepareCanvasForAnotherFrame() {
        skijaHandler.clearAndPrepareCanvasForAnotherFrame();
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
        skijaHandler.drawPath(mobj, camera);
    }

    @Override
    public void drawAbsoluteCopy(Shape sh, Vec anchor) {
        skijaHandler.drawAbsoluteCopy(sh, anchor, fixedCamera);
    }


    @Override
    public com.jmathanim.Utils.Rect createImage(JMImage jImage,InputStream stream) {
        Image img = null;
        try {
            if (imageDictionary.containsKey(jImage.getId())) {
                img = imageDictionary.get(jImage.getId());
            }else
            img = Image.makeDeferredFromEncodedBytes(stream.readAllBytes());
        } catch (IOException e) {
            JMathAnimScene.logger.error("Image " + stream.toString() + " not found");
            return new EmptyRect();
        }
        //Saves image into dictionary
        imageDictionary.put(jImage.getId(), img);
        ImageInfo info = img.getImageInfo();
        double w = camera.getWidth() * info.getWidth() / getMediaWidth();
        double h = camera.getHeight() * info.getHeight() / getMediaHeight();
        return Rect.centeredUnitCube().scale(w, h);
    }

    @Override
    public void drawImage(AbstractJMImage obj, Camera cam) {
        AffineJTransform tr = obj.getCurrentViewTransform();

        skijaHandler.drawImage(obj, cam,imageDictionary.getOrDefault(obj.getId(),null));
    }

    @Override
    public void debugText(String text, Vec loc) {
        float[] coords = mathCoordinatesToScreenCoordinates(camera, (float) loc.x, (float) loc.y);
        skijaHandler.skijaUtils.drawDebugText(coords[0], coords[1], text);

    }

    @Override
    public double MathWidthToThickness(double w) {
        return skijaHandler.MathWidthToThickness(w);
    }

    @Override
    public double ThicknessToMathWidth(double th) {
        System.out.println("ThicknessToMathWidth(double th)  ");
        return 0;//skijaHandler.ThicknessToMathWidth(th);
    }

    @Override
    public double ThicknessToMathWidth(MathObject obj) {
        return skijaHandler.ThicknessToMathWidth(obj);
    }

    @Override
    public void addSound(SoundItem soundItem) {
        JMathAnimScene.logger.warn("addSound not implemented yet, sorry!");
    }

    float[] mathCoordinatesToScreenCoordinates(Camera cam, float x, float y) {
        Matrix33 matrix = skijaHandler.retrieveCameraMatrix(cam);
        return skijaHandler.skijaUtils.applyMatrix(matrix, x, y);
    }

}

