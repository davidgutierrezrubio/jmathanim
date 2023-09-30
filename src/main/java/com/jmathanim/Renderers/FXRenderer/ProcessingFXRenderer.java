/*
 * Copyright (C) 2023 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jmathanim.Renderers.FXRenderer;

import com.jmathanim.jmathanim.JMathAnimScene;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class ProcessingFXRenderer extends JavaFXRenderer {

    PApplet processing;

    public ProcessingFXRenderer(JMathAnimScene parentScene) throws Exception {
        super(parentScene);
        processing = new PApplet();
        processing.size(800,600);
    }

    @Override
    protected BufferedImage getRenderedImage(int frameCount) {
        WritableImage img2;
        BufferedImage bi = new BufferedImage(config.mediaW, config.mediaH, BufferedImage.TYPE_INT_ARGB);
        JavaFXRenderer r = this;
        FutureTask<WritableImage> task = new FutureTask<>(() -> {
            fxScene.setFill(config.getBackgroundColor().getFXPaint(r, camera));
            group.getChildren().clear();
            groupDebug.getChildren().clear();

            fxCamera.getTransforms().clear();
            fxCamera.getTransforms().addAll(new Translate(config.mediaW / 2, config.mediaH / 2, 0),
                    new Rotate(FxCamerarotateX, Rotate.X_AXIS), new Rotate(FxCamerarotateY, Rotate.Y_AXIS),
                    new Rotate(FxCamerarotateZ, Rotate.Z_AXIS),
                    new Translate(-config.mediaW / 2, -config.mediaH / 2, 0));

            // Add all elements
//            group.getChildren().addAll(fxnodes);
//            group.getChildren().add(getProcessingCanvas());
            if (config.showFrameNumbers) {
                showDebugFrame(frameCount, 1d * frameCount / config.fps);
            }
            groupDebug.getChildren().addAll(debugFXnodes);
            if (config.drawShadow) {
                group.setEffect(dropShadow);
            }
            // Snapshot parameters
            final SnapshotParameters params = new SnapshotParameters();
            params.setFill(config.getBackgroundColor().getFXPaint(r, camera));
            params.setViewport(new Rectangle2D(0, 0, config.mediaW, config.mediaH));
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
        return bi;
    }

    public Canvas getProcessingCanvas(int width, int height) {
        //Drawing of elements goes here
        PGraphics pg = createImageWithProcessing(width, height);
        PImage get=pg.get();
        Image img1 = pg.image;
        // Crear un canvas de JavaFX y dibujar la imagen generada en el objeto PGraphics
        Canvas canvas = new Canvas(pg.width, pg.height);
        BufferedImage img2 = toBufferedImage(img1);
        WritableImage writableImage = SwingFXUtils.toFXImage(img2, null);
        canvas.getGraphicsContext2D().drawImage(writableImage, 0, 0);
        return canvas;
    }

    private PGraphics createImageWithProcessing(int width, int height) {
        // Crear un objeto PGraphics y dibujar la imagen
        PGraphics pg = processing.createGraphics(width,height);
        pg.setSize(width, height);
        pg.beginDraw();
        pg.background(255);
        pg.fill(255, 0, 0);
        pg.rect(50, 50, 100, 100);
        pg.endDraw();

        return pg;
    }

    private static BufferedImage toBufferedImage(java.awt.Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }

        // Crear una BufferedImage con la misma configuración que la imagen original
        BufferedImage bufferedImage = new BufferedImage(
                image.getWidth(null),
                image.getHeight(null),
                BufferedImage.TYPE_INT_ARGB
        );

        // Dibujar la imagen original en la BufferedImage
        java.awt.Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        return bufferedImage;
    }
}
