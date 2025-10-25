/*
 * Copyright (C) 2020 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.jmathanim.Renderers.FXRenderer;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.MathObjects.AbstractJMImage;
import com.jmathanim.MathObjects.AbstractShape;
import com.jmathanim.MathObjects.DebugTools;
import com.jmathanim.MathObjects.MathObject;
import com.jmathanim.MathObjects.Shapes.JMPath;
import com.jmathanim.Renderers.MovieEncoders.SoundItem;
import com.jmathanim.Renderers.MovieEncoders.VideoEncoder;
import com.jmathanim.Renderers.MovieEncoders.XugglerVideoEncoder;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Styling.RendererEffects;
import com.jmathanim.Utils.EmptyRect;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.ResourceLoader;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimConfig;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.jmathanim.LogUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class JavaFXRenderer extends Renderer {


    private static final double MIN_THICKNESS = .2d;
    private static final double THICKNESS_EQUIVALENT_TO_SCREEN_WIDTH=5000;
    public final JavaFXRendererUtils fXRendererUtilsJava;
    protected final ArrayList<Node> fxnodes;
    protected final ArrayList<Node> debugFXnodes;
    protected final ArrayList<Node> javaFXNodes;
    private final HashMap<JMPath, Path> storedPaths;
    private final HashMap<String, Image> images;
    public final Camera camera;
    public final Camera fixedCamera;
//    public double FxCamerarotateX = 0;
//    public double FxCamerarotateY = 0;
//    public double FxCamerarotateZ = 0;
    public double correctionThickness;
    protected PerspectiveCamera fxCamera;
    protected Scene fxScene;
    protected Group group;
    protected Group groupRoot;
    protected Group groupBackground;
    protected Group groupDebug;
    protected DropShadow dropShadow;
    protected VideoEncoder videoEncoder;
    protected File saveFilePath;

    public JavaFXRenderer(JMathAnimScene parentScene) throws Exception {
        super(parentScene);
        fxnodes = new ArrayList<>();
        debugFXnodes = new ArrayList<>();
        javaFXNodes = new ArrayList<>();
        images = new HashMap<>();
        storedPaths = new HashMap<>();
        fXRendererUtilsJava = new JavaFXRendererUtils();
        camera=parentScene.getCamera();
        fixedCamera=parentScene.getFixedCamera();
//        camera = new Camera(scene, config.mediaW, config.mediaH);
//        fixedCamera = new Camera(scene, config.mediaW, config.mediaH);
        correctionThickness = config.getMediaWidth() * 1d / THICKNESS_EQUIVALENT_TO_SCREEN_WIDTH;//Correction factor for thickness

    }

    @Override
    public void initialize() {

        try {
            prepareEncoder();
        } catch (Exception ex) {
            Logger.getLogger(JavaFXRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public final void prepareEncoder() throws Exception {

        JMathAnimScene.logger.debug("Preparing encoder");

        initializeJavaFXWindow();

        if (config.isCreateMovie()) {
            videoEncoder = new XugglerVideoEncoder();
            File tempPath = new File(config.getOutputDir().getCanonicalPath());
            tempPath.mkdirs();
            saveFilePath = new File(config.getOutputDir().getCanonicalPath() + File.separator
                    + config.getOutputFileName() + "_" + config.getMediaHeight() + ".mp4");
            JMathAnimScene.logger.info("Creating movie encoder for {}", saveFilePath);
            config.setSaveFilePath(saveFilePath);
            videoEncoder.createEncoder(config);
        }
        if (config.drawShadow) {
            dropShadow = new DropShadow();
            dropShadow.setRadius(config.shadowKernelSize);
            dropShadow.setOffsetX(config.shadowOffsetX);
            dropShadow.setOffsetY(config.shadowOffsetY);
            dropShadow.setColor(Color.color(0, 0, 0, config.shadowAlpha));
        }

        dropShadow = new DropShadow();
        dropShadow.setRadius(config.shadowKernelSize);
        dropShadow.setOffsetX(config.shadowOffsetX);
        dropShadow.setOffsetY(config.shadowOffsetY);
        dropShadow.setColor(Color.color(0, 0, 0, config.shadowAlpha));

    }

    public final void initializeJavaFXWindow() throws Exception {
        if (!JMathAnimConfig.getConfig().isJavaFXRunning()) {
            new Thread(() -> Application.launch(StandaloneSnapshot.FXStarter.class)).start();

            JMathAnimConfig.getConfig().setJavaFXRunning(true);
        }
        // block until FX toolkit initialization is complete:
        StandaloneSnapshot.FXStarter.waitForInit();
        JavaFXRenderer r = this;
        FutureTask<Integer> task = new FutureTask<>(() -> {
            group = new Group();
            groupRoot = new Group();
            groupBackground = new Group();
            groupDebug = new Group();
            // Create background
            if (config.getBackGroundImage() != null) {
                ImageView background = new ImageView(new Image(config.getBackGroundImage().openStream()));
                Rectangle2D viewport = new Rectangle2D(0, 0, config.getMediaWidth(), config.getMediaHeight());
                background.setViewport(viewport);
                groupBackground.getChildren().clear();
                groupBackground.getChildren().add(background);
            }
            groupRoot.getChildren().add(groupBackground);// Background image
            groupRoot.getChildren().add(group);// Mathobjects
            groupRoot.getChildren().add(groupDebug);// Debug things
            fxScene = new Scene(groupRoot, config.getMediaWidth(), config.getMediaHeight());
            fxScene.setFill(config.getBackgroundColor().getFXPaint(r, camera));
            StandaloneSnapshot.FXStarter.stage.setScene(fxScene);
            // Proof with perspective camera
            fxCamera = new PerspectiveCamera();
//                fxCamera.setFieldOfView(.1);
// These are 3d tests, maybe for the future...
//                fxCamera.getTransforms().addAll(
//                        new Translate(config.mediaW/2, config.mediaH/2, 0),
//                        new Rotate(45, Rotate.X_AXIS),
//                        new Rotate(45, Rotate.Z_AXIS),
//                        new Rotate(45, Rotate.Y_AXIS),
//                        new Translate(-config.mediaW/2, -config.mediaH/2, 0));
            fxScene.setCamera(fxCamera);

            if (config.isShowPreview()) {
                JMathAnimScene.logger.debug("Creating preview window");
                // TODO: This gaps to add to the window are os-dependent
                StandaloneSnapshot.FXStarter.stage.setHeight(config.getMediaHeight() + 38);
                StandaloneSnapshot.FXStarter.stage.setWidth(config.getMediaWidth() + 16);
                StandaloneSnapshot.FXStarter.stage.show();
            }
            return 1;
        });

        Platform.runLater(task);
        task.get();

    }

    public final void endJavaFXEngine() {
        Platform.exit();
    }

    @Override
    public Camera getCamera() {
        return camera;
    }

    @Override
    public Camera getFixedCamera() {
        return fixedCamera;
    }

    @Override
    public void saveFrame(int frameCount) {
        BufferedImage renderedImage = getRenderedImage(frameCount);

        if (config.isCreateMovie()) {
            videoEncoder.writeFrame(renderedImage, frameCount);
        }
        if (config.isSaveToPNG()) {
            String filename = config.getOutputFileName() + String.format("%06d", frameCount) + ".png";
            writeImageToPNG(filename, renderedImage, "png");
        }
    }

    @Override
    protected BufferedImage getRenderedImage(int frameCount) {
        WritableImage img2;
        BufferedImage bi = new BufferedImage(config.getMediaWidth(), config.getMediaHeight(), BufferedImage.TYPE_INT_ARGB);
        JavaFXRenderer r = this;
        FutureTask<WritableImage> task = new FutureTask<>(() -> {
            fxScene.setFill(config.getBackgroundColor().getFXPaint(r, camera));
            group.getChildren().clear();
            groupDebug.getChildren().clear();

            fxCamera.getTransforms().clear();
            fxCamera.getTransforms().addAll(new Translate(config.getMediaWidth() / 2, config.getMediaHeight() / 2, 0),
//                    new Rotate(FxCamerarotateX, Rotate.X_AXIS), new Rotate(FxCamerarotateY, Rotate.Y_AXIS),
//                    new Rotate(FxCamerarotateZ, Rotate.Z_AXIS),
                    new Translate(-config.getMediaWidth() / 2, -config.getMediaHeight() / 2, 0));

            // Add all elements
            group.getChildren().addAll(fxnodes);
            if (config.showFrameNumbers) {
                showDebugFrame(frameCount, 1d * frameCount / config.getFps());
            }
            groupDebug.getChildren().addAll(debugFXnodes);
            groupDebug.getChildren().addAll(javaFXNodes);
            if (config.drawShadow) {
                group.setEffect(dropShadow);
            }
            // Snapshot parameters
            final SnapshotParameters params = new SnapshotParameters();
            params.setFill(config.getBackgroundColor().getFXPaint(r, camera));
            params.setViewport(new Rectangle2D(0, 0, config.getMediaWidth(), config.getMediaHeight()));
            params.setCamera(fxScene.getCamera());

            return fxScene.getRoot().snapshot(params, null);
        });
        Platform.runLater(task);
        try {
            img2 = task.get();
            bi = SwingFXUtils.fromFXImage(img2, null);
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(JavaFXRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
        fxnodes.clear();
        debugFXnodes.clear();
        return bi;
    }

    @Override
    public void finish(int frameCount) {

        JMathAnimScene.logger.info(
                String.format("%d frames created, " + LogUtils.GREEN + "%.2fs" + LogUtils.RESET + " total time", frameCount, (1.f * frameCount) / config.getFps()));
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
        if (!config.isScriptMode())
            endJavaFXEngine();

    }

    @Override
    public void clearAndPrepareCanvasForAnotherFrame() {
        super.clearAndPrepareCanvasForAnotherFrame();
//        fxnodes.clear();
//        debugFXnodes.clear();
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
    public void drawPath(AbstractShape<?> mobj, Vec shiftVector, Camera cam) {
        if (cam == null) {
            //If the object has not a camera assigned yet, set it to default
            cam = getCamera();
            mobj.setCamera(cam);
        }
        JMPath objectPath = mobj.getPath();

        if (objectPath.size() >= 2) {
            Path path;
//            if (mobj.isRigid()) {
//                if (false){
////                if (storedPaths.containsKey(objectPath)){
//                    path=storedPaths.get(objectPath);
//                    path.getTransforms().clear();
//                }else {
//                    path = FXPathUtils.createFXPathFromJMPath(objectPath, cam);
//                    storedPaths.put(objectPath,path);
//                }
//                path.getTransforms().add(FXPathUtils.camToScreenAffineTransform(mobj.getCamera()));
//                path.getTransforms().add(new Scale(1, -1));
//                Affine tr = FXPathUtils.affineJToAffine(mobj.getModelMatrix());
//                path.getTransforms().add(tr);
//                path.getTransforms().add(new Scale(1, -1));
//                path.getTransforms().add(FXPathUtils.screenToCamAffineTransfrom(mobj.getCamera()));
//            } else {
            path = JavaFXRendererUtils.createFXPathFromJMPath(objectPath, shiftVector,cam);
//            }
            applyDrawingStyles(path, mobj);
            applyRendererEffects(path, mobj.getRendererEffects());
            path.setClip(new Rectangle(cam.upperLeftX, cam.upperLeftY, cam.getScreenWidth(), cam.getScreenHeight()));
            fxnodes.add(path);
        }
        String debugText = DebugTools.getDebugText(mobj);
        if (!"".equals(debugText)) {
            debugText(debugText, mobj.getCenter());
        }
    }


    private void applyDrawingStyles(Path path, AbstractShape<?> mobj) {

        path.setStrokeLineCap(mobj.getMp().getLineCap());
        path.setStrokeLineJoin(mobj.getMp().getLineJoin());
        path.setStrokeType(StrokeType.CENTERED);
//        path.setSmooth(false);

        // Stroke width and color
        path.setStroke(mobj.getMp().getDrawColor().getFXPaint(this, camera));
        double th = computeThickness(mobj);

        // Compute thickness depending on camera
        // A thickness of 1 means a javafx thickness 1 in a 800x600with mathview of
        // width 4
        // In a 800x600, it should mean 1 pixel
        path.setStrokeWidth(th);

        // Fill color
        path.setFill(mobj.getMp().getFillColor().getFXPaint(this, camera));

        // Dash pattern
        switch (mobj.getMp().getDashStyle()) {
            case SOLID:
                break;
            case DASHED:
                path.getStrokeDashArray().addAll(MathWidthToThickness(.025), MathWidthToThickness(.005));
                path.setStrokeLineCap(StrokeLineCap.BUTT);
                break;
            case DOTTED:
//                path.getStrokeDashArray().addAll(2d*th,6d*th);
                path.getStrokeDashArray().addAll(MathWidthToThickness(.0025), MathWidthToThickness(.005));
                path.setStrokeLineCap(StrokeLineCap.BUTT);
                break;
            case DASHDOTTED:
                path.getStrokeDashArray().addAll(MathWidthToThickness(.025), MathWidthToThickness(.005), MathWidthToThickness(.0025), MathWidthToThickness(.005));
                path.setStrokeLineCap(StrokeLineCap.BUTT);
                break;
        }
    }

    private void applyRendererEffects(Node node, RendererEffects rendererEffects) {
        if (rendererEffects.getGaussianBlurRadius() > 0) {
            node.setEffect(new GaussianBlur(rendererEffects.getGaussianBlurRadius()));
        }

        if (rendererEffects.getShadowKernelSize() > 0) {
            dropShadow = new DropShadow();
            dropShadow.setRadius(rendererEffects.getShadowKernelSize());
            dropShadow.setOffsetX(rendererEffects.getShadowOffsetX());
            dropShadow.setOffsetY(rendererEffects.getShadowOffsetY());
            dropShadow.setColor(rendererEffects.getShadowColor().getFXColor());
            node.setEffect(dropShadow);
        }

    }

    public double computeThickness(MathObject mobj) {

        Camera cam = (mobj.getMp().isAbsoluteThickness() ? fixedCamera : camera);
        //We use the correction factor mediaW/1066 in order to obtain the same apparent thickness
        //regardless of the resolution chosen. The reference value 1066 is the width in the preview settings
//        double th1 = Math.max(mobj.getMp().getThickness() / cam.getMathView().getWidth() * correctionThickness, MIN_THICKNESS);

        double width = cam.getMathView().getWidth();
        double th = Math.max(
                mobj.getMp().getThickness() * correctionThickness / width * 4d
                , MIN_THICKNESS);
        return th;


//        return Math.max(mobj.getMp().getThickness() / cam.getMathView().getWidth() * 2.5d, MIN_THICKNESS);
    }

    @Override
    public double MathWidthToThickness(double w) {
//        return mathScalar * config.mediaW / (xmax - ymin);
//        return camera.mathToScreen(w) / 1.25 * camera.getMathView().getWidth() / 2d;
//        return w * 1066;
        return w*THICKNESS_EQUIVALENT_TO_SCREEN_WIDTH/camera.getMathView().getWidth();
    }

    @Override
    public double ThicknessToMathWidth(double th) {

        return th*fixedCamera.getMathView().getWidth()/ THICKNESS_EQUIVALENT_TO_SCREEN_WIDTH;

//        return th / 1066;

    }

    @Override
    public double ThicknessToMathWidth(MathObject<?> obj) {
        Camera cam = (obj.getMp().isAbsoluteThickness() ? fixedCamera : camera);

//        return obj.getMp().getThickness() / 1066 * 4 / cam.getMathView().getWidth();
        double th= obj.getMp().getThickness();
        return th*cam.getMathView().getWidth()/ THICKNESS_EQUIVALENT_TO_SCREEN_WIDTH;
    }

    @Override
    public void drawAbsoluteCopy(AbstractShape<?> sh, Vec anchor) {
        Vec vFixed = defaultToFixedCamera(anchor);
        drawPath(sh, vFixed.minus(anchor),fixedCamera);
    }

    /**
     * Returns equivalent position from default camera to fixed camera. For example if you pass the Vec (1,1) to this
     * method, it will return a new set of coordinates so that, when rendered with the fixed camera, appears in the same
     * position as (1,1) with default camera.
     *
     * @param v Vector that marks the position
     * @return The coordinates to be used with the fixed camera
     */
    public Vec defaultToFixedCamera(Vec v) {
        double[] ms = camera.mathToScreenFX(v);
        double[] coords = fixedCamera.screenToMath(ms[0], ms[1]);
        return Vec.to(coords[0], coords[1]);

    }

    @Override
    public Rect createImage(InputStream stream) {
        String fileName = stream.toString();
        Image image;
        if (!images.containsKey(fileName)) {// If the image is not already loaded...
            try {
                ResourceLoader rl = new ResourceLoader();
                final URL imageResource = rl.getResource(fileName, "images");
                image = new Image(stream);
                images.put(fileName, image);
                JMathAnimScene.logger.info("Loaded image " + fileName);
            } catch (FileNotFoundException e) {
                JMathAnimScene.logger.error("File " + LogUtils.CYAN + fileName + LogUtils.RESET + " not found. Returning EmptyRect");
                return new EmptyRect();
            }
        } else {
            image = images.get(fileName);
        }

        return getBboxFromImageCatalog(fileName);
    }

    private Rect getBboxFromImageCatalog(String fileName) {
        Image image = images.get(fileName);
        // UL corner of bounding box initially set to (0,0)
        Rect r = new Rect(0, 0, 0, 0);
        r.ymin = -fixedCamera.screenToMath(image.getHeight());
        r.xmax = fixedCamera.screenToMath(image.getWidth());
        return r;
    }

    public Image getImageFromCatalog(AbstractJMImage obj) {
        return images.get(obj.getId());
    }

    @Override
    public void drawImage(AbstractJMImage obj, Camera cam) {
        Rect bbox = getBboxFromImageCatalog(obj.getId());
        ImageView imageView;
        if (obj.isCached()) {
            Image image = getImageFromCatalog(obj);
            imageView = new ImageView(image);
        } else {
            imageView = new ImageView(obj.getImage());
        }
        imageView.setFitHeight(bbox.getHeight());
        imageView.setFitWidth(bbox.getWidth());

        imageView.setOpacity(obj.getMp().getDrawColor().getAlpha());

        Affine camToScreen = JavaFXRendererUtils.camToScreenAffineTransform(cam);
        imageView.getTransforms().add(camToScreen);

//        //Swap y coordinate
        imageView.getTransforms().add(new Scale(1, -1));
        imageView.getTransforms().add(JavaFXRendererUtils.affineJToAffine(obj.getCurrentViewTransform()));
        imageView.getTransforms().add(new Scale(1, -1));
        fxnodes.add(imageView);
    }

    @Override
    public void debugText(String text, Vec loc) {
        double[] xy = camera.mathToScreenFX(loc);
        Text t = new Text(text);
        t.setFont(new Font(16));
        Bounds b1 = t.getLayoutBounds();
        t.setX(xy[0] - .5 * b1.getWidth());
        t.setY(xy[1] + .5 * b1.getHeight());

        Bounds b = t.getLayoutBounds();
        double gap = 2;
        Rectangle rectangle = new Rectangle(b.getMinX() - gap, b.getMinY() - gap, b.getWidth() + gap,
                b.getHeight() + gap);
        rectangle.setFill(Color.LIGHTBLUE);
        rectangle.setOpacity(.7);
        rectangle.setStroke(Color.DARKBLUE);

        debugFXnodes.add(rectangle);
        debugFXnodes.add(t);
    }

    public void addJavaFXNode(Node node) {
        javaFXNodes.add(node);

    }


    public void removeJavaFXNode(Node node) {
        javaFXNodes.remove(node);
    }


    protected void showDebugFrame(int numFrame, double time) {
        Text t = new Text("Frame: " + numFrame + " (" + String.format("%.2f", time) + "s)");
        t.setFont(Font.font("Verdana", FontWeight.BOLD, 48));
        t.setFill(Color.ALICEBLUE);
        t.setStroke(Color.BLACK);
        t.setX(0);
        t.setY(0);
        t.setTextOrigin(VPos.TOP);
        debugFXnodes.add(t);
    }

    @Override
    public void addSound(SoundItem soundItem) {
        try {
            videoEncoder.addSound(soundItem);
        } catch (NullPointerException ex) {
            //Do nothing
        }
    }

    @Override
    public RendererEffects buildRendererEffects() {
        return new JavaFXRendererEffects();
    }

}
