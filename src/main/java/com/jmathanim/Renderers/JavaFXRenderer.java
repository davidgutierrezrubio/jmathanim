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
package com.jmathanim.Renderers;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Renderers.MovieEncoders.VideoEncoder;
import com.jmathanim.Renderers.MovieEncoders.XugglerVideoEncoder;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.ResourceLoader;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import static com.jmathanim.jmathanim.JMathAnimScene.DEGREES;
import com.jmathanim.mathobjects.AbstractJMImage;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Shape;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class JavaFXRenderer extends Renderer {

    private static final double XMIN_DEFAULT = -2;
    private static final double XMAX_DEFAULT = 2;

    private static final double MIN_THICKNESS = .2d;

    public final FXPathUtils fXPathUtils;
    public Camera camera;
    public Camera fixedCamera;

    private final HashMap<String, Image> images;

    private PerspectiveCamera fxCamera;
    public double FxCamerarotateX = 0;
    public double FxCamerarotateY = 0;
    public double FxCamerarotateZ = 0;

    private Scene fxScene;
    private Group group;
    private Group groupRoot;
    private Group groupBackground;
    private Group groupDebug;
    DropShadow dropShadow;

    private final ArrayList<Node> fxnodes;
    private final ArrayList<Node> debugFXnodes;

    private VideoEncoder videoEncoder;
    private File saveFilePath;
    private int newLineCounter = 0;

    public JavaFXRenderer(JMathAnimScene parentScene) throws Exception {
        super(parentScene);
        camera = new Camera(parentScene, config.mediaW, config.mediaH);
        camera.initialize(XMIN_DEFAULT, XMAX_DEFAULT, 0);
        fixedCamera = new Camera(parentScene, config.mediaW, config.mediaH);
        fixedCamera.initialize(XMIN_DEFAULT, XMAX_DEFAULT, 0);

        fxnodes = new ArrayList<>();
        debugFXnodes = new ArrayList<>();
        images = new HashMap<>();
        fXPathUtils = new FXPathUtils();
        prepareEncoder();
    }

    public final void prepareEncoder() throws Exception {

        JMathAnimScene.logger.info("Preparing encoder");

        initializeJavaFXWindow();

        if (config.isCreateMovie()) {
            videoEncoder = new XugglerVideoEncoder();
            File tempPath = new File(config.getOutputDir().getCanonicalPath());
            tempPath.mkdirs();
            saveFilePath = new File(config.getOutputDir().getCanonicalPath() + File.separator + config.getOutputFileName() + "_" + config.mediaH + ".mp4");
            JMathAnimScene.logger.info("Creating movie encoder for {}", saveFilePath);
            videoEncoder.createEncoder(saveFilePath, config);
        }
        if (config.drawShadow) {
            dropShadow = new DropShadow();
            dropShadow.setRadius(config.shadowKernelSize);
            dropShadow.setOffsetX(config.shadowOffsetX);
            dropShadow.setOffsetY(config.shadowOffsetY);
            dropShadow.setColor(Color.color(0, 0, 0, config.shadowAlpha));
        }
    }

    public final void initializeJavaFXWindow() throws Exception {
        new Thread(() -> Application.launch(StandaloneSnapshot.FXStarter.class)).start();
        // block until FX toolkit initialization is complete:
        StandaloneSnapshot.FXStarter.awaitFXToolkit();

        FutureTask<Integer> task = new FutureTask<>(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                group = new Group();
                groupRoot = new Group();
                groupBackground = new Group();
                groupDebug = new Group();
                //Create background
                if (config.getBackGroundImage() != null) {
                    ImageView background = new ImageView(new Image(config.getBackGroundImage().openStream()));
                    Rectangle2D viewport = new Rectangle2D(0, 0, config.mediaW, config.mediaH);
                    background.setViewport(viewport);
                    groupBackground.getChildren().clear();
                    groupBackground.getChildren().add(background);
                }
                groupRoot.getChildren().add(groupBackground);//Background image
                groupRoot.getChildren().add(group);//Mathobjects
                groupRoot.getChildren().add(groupDebug);//Debug things
                fxScene = new Scene(groupRoot, config.mediaW, config.mediaH);
                fxScene.setFill(config.getBackgroundColor().getFXColor());
                StandaloneSnapshot.FXStarter.stage.setScene(fxScene);
                //Proof with perspective camera
                fxCamera = new PerspectiveCamera();
//                fxCamera.setFieldOfView(.1);
                //These are 3d tests, maybe for the future...
//                fxCamera.getTransforms().addAll(
//                        new Translate(config.mediaW/2, config.mediaH/2, 0),
//                        new Rotate(45, Rotate.X_AXIS),
//                        new Rotate(45, Rotate.Z_AXIS),
//                        new Rotate(45, Rotate.Y_AXIS),
//                        new Translate(-config.mediaW/2, -config.mediaH/2, 0));
                fxScene.setCamera(fxCamera);

                if (config.isShowPreview()) {
                    JMathAnimScene.logger.info("Creating preview window");
                    //TODO: This gaps to add to the window are os-dependent
                    StandaloneSnapshot.FXStarter.stage.setHeight(config.mediaH + 38);
                    StandaloneSnapshot.FXStarter.stage.setWidth(config.mediaW + 16);
                    StandaloneSnapshot.FXStarter.stage.show();
                }
                return 1;
            }
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
        Rectangle clip = new Rectangle(8000, 6000);
        clip.setLayoutX(25);
        clip.setLayoutY(25);
        WritableImage img2;
        BufferedImage bi = new BufferedImage(config.mediaW, config.mediaH, BufferedImage.TYPE_INT_ARGB);
        FutureTask<WritableImage> task = new FutureTask<>(new Callable<WritableImage>() {
            @Override
            public WritableImage call() throws Exception {
                group.getChildren().clear();
                groupDebug.getChildren().clear();

                fxCamera.getTransforms().clear();
                fxCamera.getTransforms().addAll(
                        new Translate(config.mediaW / 2, config.mediaH / 2, 0),
                        new Rotate(FxCamerarotateX, Rotate.X_AXIS),
                        new Rotate(FxCamerarotateY, Rotate.Y_AXIS),
                        new Rotate(FxCamerarotateZ, Rotate.Z_AXIS),
                        new Translate(-config.mediaW / 2, -config.mediaH / 2, 0));

                //Add all elements
                group.getChildren().addAll(fxnodes);
                groupDebug.getChildren().addAll(debugFXnodes);
                if (config.drawShadow) {
                    group.setEffect(dropShadow);
                }
                //Snapshot parameters
                final SnapshotParameters params = new SnapshotParameters();
                params.setFill(config.getBackgroundColor().getFXColor());
                params.setViewport(new Rectangle2D(0, 0, config.mediaW, config.mediaH));
                params.setCamera(fxScene.getCamera());

                return fxScene.getRoot().snapshot(params, null);
            }
        });

        Platform.runLater(task);
        try {
            img2 = task.get();
            bi = SwingFXUtils.fromFXImage(img2, null);
        } catch (InterruptedException ex) {
            Logger.getLogger(JavaFXRenderer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(JavaFXRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (config.isCreateMovie()) {
            if ((frameCount % config.fps) == 0) {
                newLineCounter++;
                newLineCounter = 0;
            }
            videoEncoder.writeFrame(bi, frameCount);
        }
//        File fpng=new File(config.getOutputFileName()+String.format("%03d", frameCount)+".png");
//        try {
//            ImageIO.write(bi, "png", fpng);
//        } catch (IOException ex) {
//            Logger.getLogger(JavaFXRenderer.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    @Override
    public void finish(int frameCount) {
        JMathAnimScene.logger.info(String.format("%d frames created, %.2fs total time", frameCount, (1.f * frameCount) / config.fps));
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
        endJavaFXEngine();

    }

    @Override
    public void clear() {
        fxnodes.clear();
        debugFXnodes.clear();
    }

    @Override
    public void drawPath(Shape mobj) {
        drawPath(mobj, camera);
    }

    private void drawPath(Shape mobj, Camera cam) {

        JMPath c = mobj.getPath();
        int numPoints = c.size();

        if (numPoints >= 2) {
            Path path = fXPathUtils.createFXPathFromJMPath(c, cam);
            applyDrawingStyles(path, mobj);
            fxnodes.add(path);
        }
        if (!"".equals(mobj.getDebugText())) {
            debugText(mobj.getDebugText(), mobj.getCenter().v);
        }
    }

    private void applyDrawingStyles(Path path, Shape mobj) {

        path.setStrokeLineCap(mobj.getMp().getLinecap());
        path.setStrokeLineJoin(StrokeLineJoin.ROUND);
        path.setStrokeType(StrokeType.CENTERED);
//        path.setSmooth(false);

        //Stroke width and color
        path.setStroke(mobj.getMp().getDrawColor().getFXColor());

        //Compute thickness depending on camera
        //A thickness of 1 means a javafx thickness 1 in a 800x600with mathview of width 4
        //In a 800x600, it should mean 1 pixel
        path.setStrokeWidth(computeThickness(mobj));

        //Fill color
        if (mobj.getMp().isFillColorIsDrawColor()) {
            path.setFill(mobj.getMp().getDrawColor().getFXColor());
        } else {
            path.setFill(mobj.getMp().getFillColor().getFXColor());
        }

        //Dash pattern
        switch (mobj.getMp().getDashStyle()) {
            case SOLID:
                break;
            case DASHED:
                path.getStrokeDashArray().addAll(25d, 10d);
                break;
            case DOTTED:
                path.getStrokeDashArray().addAll(2d, 6d);
                break;
        }
    }

    public double computeThickness(MathObject mobj) {
        Camera cam = (mobj.getMp().isAbsoluteThickness() ? fixedCamera : camera);
        return Math.max(mobj.getMp().getThickness() / cam.getMathView().getWidth() * 2.5d, MIN_THICKNESS);
    }

    @Override
    public double getThicknessForMathWidth(double w) {
//        return mathScalar * config.mediaW / (xmax - ymin);
        return camera.mathToScreen(w) / 1.25 * camera.getMathView().getWidth() / 2d;
    }

    @Override
    public void drawAbsoluteCopy(Shape sh, Vec anchor) {
        Shape shape = sh.copy();
        Vec vFixed = defaultToFixedCamera(anchor);
        shape.shift(vFixed.minus(anchor));
        drawPath(shape, fixedCamera);
    }

    /**
     * Returns equivalent position from default camera to fixed camera. For
     * example if you pass the Vec (1,1) to this method, it will return a new
     * set of coordinates so that, when rendered with the fixed camera, appears
     * in the same position as (1,1) with default camera.
     *
     * @param v Vector that marks the position
     * @return The coordinates to be used with the fixed camera
     */
    public Vec defaultToFixedCamera(Vec v) {
        double[] ms = camera.mathToScreenFX(v);
        double[] coords = fixedCamera.screenToMath(ms[0], ms[1]);
        return new Vec(coords[0], coords[1]);

    }

    @Override
    public Rect createImage(String fileName
    ) {
        Rect r = new Rect(0, 0, 0, 0);

        try {
            Image image;
            if (!images.containsKey(fileName)) {//If the image is not already loaded...
                ResourceLoader rl = new ResourceLoader();
                final URL imageResource = rl.getResource(fileName, "images");
                image = new Image(imageResource.openStream());
                images.put(fileName, image);
                JMathAnimScene.logger.info("Loaded image " + fileName);
            } else {
                image = images.get(fileName);
            }

            //UL corner of bounding box initially set to (0,0)
            r.ymin = -camera.screenToMath(image.getHeight());
            r.xmax = camera.screenToMath(image.getWidth());
        } catch (IOException ex) {
            JMathAnimScene.logger.warn("Could'nt load image " + fileName);
        }

        return r;
    }

    @Override
    public void drawImage(AbstractJMImage obj
    ) {
        ImageView imageView;
        if (obj.isCached()) {
            Image image = images.get(obj.getId());
            imageView = new ImageView(image);
        } else {
            imageView = new ImageView(obj.getImage());
        }
        //setting the fit height and width of the image view
        double[] xy = camera.mathToScreenFX(obj.bbox.getUL().v);
        imageView.setX(xy[0]);
        imageView.setY(xy[1]);
        imageView.setFitHeight(camera.mathToScreen(obj.bbox.getHeight()));
        imageView.setFitWidth(camera.mathToScreen(obj.bbox.getWidth()));
        imageView.setPreserveRatio(obj.preserveRatio);
        imageView.setSmooth(true);
        imageView.setCache(true);
        imageView.setOpacity(obj.getMp().getDrawColor().alpha);
        imageView.setRotate(-obj.rotateAngle / DEGREES);
        fxnodes.add(imageView);
    }

    @Override
    public void debugText(String text, Vec loc
    ) {
        double[] xy = camera.mathToScreenFX(loc);
        Text t = new Text(text);
        t.setFont(new Font(16));
        Bounds b1 = t.getLayoutBounds();
        t.setX(xy[0] - .5 * b1.getWidth());
        t.setY(xy[1] + .5 * b1.getHeight());

        Bounds b = t.getLayoutBounds();
        double gap = 2;
        Rectangle rectangle = new Rectangle(b.getMinX() - gap, b.getMinY() - gap, b.getWidth() + gap, b.getHeight() + gap);
        rectangle.setFill(Color.LIGHTBLUE);
        rectangle.setStroke(Color.DARKBLUE);

        debugFXnodes.add(rectangle);
        debugFXnodes.add(t);
    }

    public void addSound(File soundFile, int frameCount) {
        try {
            videoEncoder.addSound(soundFile, frameCount);
        } catch (IOException ex) {
            Logger.getLogger(JavaFXRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
