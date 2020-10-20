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
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.Shape;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
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
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class JavaFXRenderer extends Renderer {

    private static final double XMIN_DEFAULT = -2;
    private static final double XMAX_DEFAULT = 2;

    public CameraFX2D camera;
    private Scene fxScene;
    private Group group;
    private final ArrayList<Node> fxnodes;

    private VideoEncoder videoEncoder;
    private File saveFilePath;

    public JavaFXRenderer(JMathAnimScene parentScene) throws Exception {
        super(parentScene);
        camera = new CameraFX2D(cnf.mediaW, cnf.mediaH);
        camera.setMathXY(XMIN_DEFAULT, XMAX_DEFAULT, 0);
        fxnodes = new ArrayList<>();

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

            if (cnf.drawShadow) {
                //..................................
            }

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
                StandaloneSnapshot.FXStarter.stage.setScene(fxScene);
                if (cnf.showPreview) {
                    JMathAnimScene.logger.debug("Creating preview window");
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
    public void setCamera(Camera c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T extends Camera> T getCamera() {
        return (T) camera;
    }

    @Override
    public Camera getFixedCamera() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setCameraSize(int w, int h) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void saveFrame(int frameCount) throws Exception {
        WritableImage img2;
        BufferedImage bi = new BufferedImage(cnf.mediaH, cnf.mediaW, BufferedImage.TYPE_INT_ARGB);
        FutureTask<WritableImage> task = new FutureTask<>(new Callable<WritableImage>() {
            @Override
            public WritableImage call() throws Exception {
                WritableImage img;//=new WritableImage(cnf.mediaW, cnf.mediaH);
                group.getChildren().addAll(fxnodes);
                final SnapshotParameters params = new SnapshotParameters();
                params.setFill(Color.ANTIQUEWHITE);
                Rectangle2D r = new Rectangle2D(0, 0, cnf.mediaW, cnf.mediaH);
                params.setViewport(r);
                params.setCamera(fxScene.getCamera());

                img = fxScene.getRoot().snapshot(params, null);
                return img;
            }
        });

        Platform.runLater(task);
        img2 = task.get();
//        BufferedImage bi = SwingFXUtils.fromFXImage(img2, null);
        bi = SwingFXUtils.fromFXImage(img2, null);
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
        FutureTask<Integer> task = new FutureTask<>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                // Code to execute in FX Thread should go here
                group.getChildren().clear();
                return 1;
            }
        });

        Platform.runLater(task);
        try {
            task.get();//This way I ensure task is executed before continuing this thread
        } catch (InterruptedException ex) {
            Logger.getLogger(JavaFXRenderer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(JavaFXRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }

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
//            path.setWindingRule(GeneralPath.WIND_NON_ZERO);

            DropShadow dropShadow = new DropShadow();
            dropShadow.setRadius(5.0);
            dropShadow.setOffsetX(6.0);
            dropShadow.setOffsetY(6.0);
            dropShadow.setColor(Color.color(0.4, 0.5, 0.5));//IMplement with JColor: Make JColor extends FX Color?
            path.setEffect(dropShadow);
            fxnodes.add(path);
        }
    }

    @Override
    public void drawAbsoluteCopy(Shape sh, Vec anchor) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private Path createPathFromJMPath(Shape mobj, JMPath c, CameraFX2D cam) {
        Path path = new Path();
        Vec p = c.getJMPoint(0).p.v;
        double[] scr = cam.mathToScreenFX(p.x, p.y);
        path.getElements().add(new MoveTo(scr[0], scr[1]));

        for (int n = 1; n < c.size()+1; n++) {
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
                if (n < c.size()+1) {//If it is the last point, don't move (it creates a strange point at the beginning)
                    path.getElements().add(new MoveTo(xy[0], xy[1]));
                }
            }
        }

        return path;
    }

}
