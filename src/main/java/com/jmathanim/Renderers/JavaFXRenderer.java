/*
 * Copyright (C) 2020 David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
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
import com.jmathanim.Cameras.CameraFX2D;
import com.jmathanim.Renderers.MovieEncoders.VideoEncoder;
import com.jmathanim.Renderers.MovieEncoders.XugglerVideoEncoder;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.MODrawProperties;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.JMImage;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Shape;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class JavaFXRenderer extends Renderer {

    private static final double XMIN_DEFAULT = -2;
    private static final double XMAX_DEFAULT = 2;

    public CameraFX2D camera;
    public CameraFX2D fixedCamera;

    final HashMap<String, Image> images;

    private PerspectiveCamera fxCamera;
    public double FxCamerarotateX = 0;
    public double FxCamerarotateY = 0;
    public double FxCamerarotateZ = 0;

    private Scene fxScene;
    private Group group;
    DropShadow dropShadow;

    private final ArrayList<Node> fxnodes;

    private VideoEncoder videoEncoder;
    private File saveFilePath;

    public JavaFXRenderer(JMathAnimScene parentScene) throws Exception {
        super(parentScene);
        camera = new CameraFX2D(cnf.mediaW, cnf.mediaH);
        camera.setMathXY(XMIN_DEFAULT, XMAX_DEFAULT, 0);
        fixedCamera = new CameraFX2D(cnf.mediaW, cnf.mediaH);
        fixedCamera.setMathXY(XMIN_DEFAULT, XMAX_DEFAULT, 0);

        fxnodes = new ArrayList<>();
        images = new HashMap<String, Image>();

        prepareEncoder();
    }

    public final void prepareEncoder() throws Exception {

        JMathAnimScene.logger.info("Preparing encoder");

        initializeJavaFXWindow();

        if (cnf.createMovie) {
//            videoEncoder=new JCodecVideoEncoder();
            videoEncoder = new XugglerVideoEncoder();
//            videoEncoder=new HumbleVideoEncoder();
            File tempPath = new File(cnf.getOutputDir().getCanonicalPath());
            tempPath.mkdirs();
            saveFilePath = new File(cnf.getOutputDir().getCanonicalPath() + File.separator + cnf.getOutputFileName() + "_" + cnf.mediaH + ".mp4");
            JMathAnimScene.logger.info("Creating movie encoder for {}", saveFilePath);
//                muxer = Muxer.make(saveFilePath.getCanonicalPath(), null, "mp4");
            videoEncoder.createEncoder(saveFilePath, cnf);
        }
        if (cnf.drawShadow) {
            dropShadow = new DropShadow();
            dropShadow.setRadius(cnf.shadowKernelSize);
            dropShadow.setOffsetX(cnf.shadowOffsetX);
            dropShadow.setOffsetY(cnf.shadowOffsetY);
            dropShadow.setColor(Color.color(0, 0, 0, cnf.shadowAlpha));
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
                fxScene = new Scene(group, cnf.mediaW, cnf.mediaW);
                fxScene.setFill(JMathAnimConfig.getConfig().getBackgroundColor().getFXColor());
                StandaloneSnapshot.FXStarter.stage.setScene(fxScene);
                //Proof with perspective camera
                fxCamera = new PerspectiveCamera();
//                camera.getTransforms().addAll(
//                        new Translate(cnf.mediaW/2, cnf.mediaH/2, 0),
//                        new Rotate(45, Rotate.X_AXIS),
//                        new Rotate(45, Rotate.Z_AXIS),
//                        new Rotate(45, Rotate.Y_AXIS),
//                        new Translate(-cnf.mediaW/2, -cnf.mediaH/2, 0));
                fxScene.setCamera(fxCamera);

                if (cnf.showPreview) {
                    JMathAnimScene.logger.debug("Creating preview window");
                    //TODO: This gaps to add to the window are os-dependent
                    StandaloneSnapshot.FXStarter.stage.setHeight(cnf.mediaH + 38);
                    StandaloneSnapshot.FXStarter.stage.setWidth(cnf.mediaW + 16);
                    StandaloneSnapshot.FXStarter.stage.show();
                }
                return 1;
            }
        });

        Platform.runLater(task);
        task.get();

//        Group group = new Group();
//        scene = new Scene(group, 800, 600);
    }

    public final void endJavaFXEngine() {
        Platform.exit();
    }

//    public void addShadow(){
//         FutureTask<WritableImage> task = new FutureTask<>(new Callable<WritableImage>() {
//            @Override
//            public WritableImage call() throws Exception {
//                WritableImage img;
//                group.getChildren().addAll(fxnodes);
//                img = fxScene.getRoot().snapshot(null, null);
//                return img;
//            }
//        });
//
//        Platform.runLater(task);
//        try {
//            WritableImage img2 = task.get();
//        } catch (InterruptedException | ExecutionException ex) {
//            Logger.getLogger(JavaFXRenderer.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    @Override
    public <T extends Camera> T getCamera() {
        return (T) camera;
    }

    @Override
    public Camera getFixedCamera() {
        return fixedCamera;
    }

    @Override
    public void saveFrame(int frameCount) {
        WritableImage img2;
        BufferedImage bi = new BufferedImage(cnf.mediaH, cnf.mediaW, BufferedImage.TYPE_INT_ARGB);
        FutureTask<WritableImage> task = new FutureTask<>(new Callable<WritableImage>() {
            @Override
            public WritableImage call() throws Exception {
                group.getChildren().clear();
                fxCamera.getTransforms().clear();
                fxCamera.getTransforms().addAll(
                        new Translate(cnf.mediaW / 2, cnf.mediaH / 2, 0),
                        new Rotate(FxCamerarotateX, Rotate.X_AXIS),
                        new Rotate(FxCamerarotateY, Rotate.Y_AXIS),
                        new Rotate(FxCamerarotateZ, Rotate.Z_AXIS),
                        new Translate(-cnf.mediaW / 2, -cnf.mediaH / 2, 0));

                //Create background
                if ((!"".equals(cnf.backGroundImage)) && (cnf.backGroundImage != null)) {
                    File file = new File(cnf.backGroundImage);
                    ImageView background = new ImageView(new Image(file.toURI().toString()));
                    Rectangle2D viewport = new Rectangle2D(0, 0, cnf.mediaW, cnf.mediaW);
                    background.setViewport(viewport);
                    group.getChildren().add(background);
                }
                //Add all elements
                group.getChildren().addAll(fxnodes);

                //Snapshot parameters
                final SnapshotParameters params = new SnapshotParameters();
                params.setFill(JMathAnimConfig.getConfig().getBackgroundColor().getFXColor());
                params.setViewport(new Rectangle2D(0, 0, cnf.mediaW, cnf.mediaH));
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
//        BufferedImage bi = SwingFXUtils.fromFXImage(img2, null);

        if (cnf.createMovie) {
            videoEncoder.writeFrame(bi, frameCount);
//            File file=new File("C:\\media\\frame"+frameCount+".png");
//             ImageIO.write(SwingFXUtils.fromFXImage(img2, null), "png", file);

        }
    }

    @Override
    public void finish(int frameCount) {
        JMathAnimScene.logger.info(String.format("%d frames created, %.2fs total time", frameCount, (1.f * frameCount) / cnf.fps));
        if (cnf.createMovie) {
            /**
             * Encoders, like decoders, sometimes cache pictures so it can do
             * the right key-frame optimizations. So, they need to be flushed as
             * well. As with the decoders, the convention is to pass in a null
             * input until the output is not complete.
             */
            JMathAnimScene.logger.info("Finishing movie...");
            videoEncoder.finish();
            JMathAnimScene.logger.info("Movie created at " + saveFilePath);

        }
        endJavaFXEngine();

    }

    @Override
    public void clear() {
        fxnodes.clear();
    }

    @Override
    public void drawPath(Shape mobj) {
        drawPath(mobj, camera);
    }

    public void drawPath(Shape mobj, CameraFX2D cam) {

        JMPath c = mobj.getPath();
        int numPoints = c.size();

        if (numPoints >= 2) {
            Path path = createPathFromJMPath(mobj, c, cam);

            applyDrawingStyles(path, mobj);

            if (cnf.drawShadow & mobj.mp.castShadows) {
                path.setEffect(dropShadow);
            }
            fxnodes.add(path);
        }
    }

    private void applyDrawingStyles(Path path, Shape mobj) {

        path.setStrokeLineCap(StrokeLineCap.ROUND);
        path.setStrokeType(StrokeType.CENTERED);

        //Stroke width and color
        path.setStroke(mobj.mp.drawColor.getFXColor());
        path.setStrokeWidth(mobj.mp.thickness * 4);

        //Fill color
        path.setFill(mobj.mp.fillColor.getFXColor());

        //Dash pattern
        switch (mobj.mp.dashStyle) {
            case MODrawProperties.SOLID:
                break;
            case MODrawProperties.DASHED:
                path.getStrokeDashArray().addAll(25d, 10d);
                break;
            case MODrawProperties.DOTTED:
                path.getStrokeDashArray().addAll(2d, 6d);
                break;
        }

    }

    @Override
    public void drawAbsoluteCopy(Shape sh, Vec anchor) {
        Shape shape = sh.copy();
        Vec vFixed = defaultToFixedCamera(anchor);
        shape.shift(vFixed.minus(anchor));
        drawPath(shape, fixedCamera);
    }

    /**
     * Returns equivalent position from default camera to fixed camera.
     *
     * @param v
     * @return
     */
    public Vec defaultToFixedCamera(Vec v) {
        double width1 = camera.getMathView().getWidth();
        double[] ms = camera.mathToScreenFX(v);
        double[] coords = fixedCamera.screenToMath(ms[0], ms[1]);
//        System.out.println(width1 + ",   " + v + "    MS(" + ms[0] + ", " + ms[1] + ")");

        return new Vec(coords[0], coords[1]);

    }

    private Path createPathFromJMPath(Shape mobj, JMPath c, CameraFX2D cam) {
        Path path = new Path();

        Vec p = c.getJMPoint(0).p.v;
        double[] scr = cam.mathToScreenFX(p.x, p.y);
        path.getElements().add(new MoveTo(scr[0], scr[1]));

        for (int n = 1; n < c.size() + 1; n++) {
            Vec point = c.getJMPoint(n).p.v;
            Vec cpoint1 = c.getJMPoint(n - 1).cp1.v;
            Vec cpoint2 = c.getJMPoint(n).cp2.v;
            double[] xy = cam.mathToScreenFX(point);

            double[] cxy1 = cam.mathToScreenFX(cpoint1);
            double[] cxy2 = cam.mathToScreenFX(cpoint2);
            if (c.getJMPoint(n).isThisSegmentVisible) {
                if (c.getJMPoint(n).isCurved) {
                    path.getElements().add(new CubicCurveTo(cxy1[0], cxy1[1], cxy2[0], cxy2[1], xy[0], xy[1]));
                } else {
                    path.getElements().add(new LineTo(xy[0], xy[1]));
                }
            } else {
                if (n < c.size() + 1) {//If it is the last point, don't move (it creates a strange point at the beginning)
                    path.getElements().add(new MoveTo(xy[0], xy[1]));
                }
            }
        }

        return path;
    }

    @Override
    public int createImage(String fileName) {
        int imageId = -1;
        try {
            Image image = new Image(new FileInputStream(fileName));
            images.put(fileName, image);
            JMathAnimScene.logger.info("Loaded image " + fileName);
        } catch (FileNotFoundException ex) {
            JMathAnimScene.logger.warn("Could'nt load image " + fileName);

        }
        return imageId;
    }
    //Setting the image view     }

    @Override
    public void drawImage(JMImage obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
