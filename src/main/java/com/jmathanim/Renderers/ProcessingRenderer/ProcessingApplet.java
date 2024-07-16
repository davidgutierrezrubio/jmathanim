/*
 * Copyright (C) 2024 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
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
package com.jmathanim.Renderers.ProcessingRenderer;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Styling.JMColor;
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.Styling.PaintStyle;
import com.jmathanim.Styling.Stylable;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Shape;
import java.awt.image.BufferedImage;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import processing.core.PApplet;
import static processing.core.PConstants.P3D;
import processing.core.PGraphics;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class ProcessingApplet extends PApplet {

    private final JMathAnimConfig config;
    private CountDownLatch drawLatch;
    private CountDownLatch drawLatch2;
    public BufferedImage renderedImage;
    private final Object lock = new Object();

    private PGraphics pg;
    private static CountDownLatch latch;
    private static BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private final ProcessingRenderer pRenderer;
    public double correctionThickness;
    private static final double MIN_THICKNESS = .2d;

    public ProcessingApplet(ProcessingRenderer pRenderer, JMathAnimConfig config, CountDownLatch latch) {
        this.config = config;
        this.pRenderer = pRenderer;
        ProcessingApplet.latch = latch;

        correctionThickness = config.mediaW * 1d / 1066;//Correction factor for thickness
    }

    public void settings() {
        size(config.mediaW, config.mediaH, P3D); // Tamaño de la ventana con P3D

    }

    public void setup() {
        smooth(4);
        pg = createGraphics(config.mediaW, config.mediaH, P3D);
        pg.smooth(4);
        surface.setResizable(false);
        latch.countDown(); // Signal that setup is complete
        noLoop();
    }

    public void draw() {

        synchronized (lock) {
            processQueue();
            image(pg, 0, 0); // Dibuja el PGraphics en la ventana principal
        }
        if (this.drawLatch2 != null) {
            this.drawLatch2.countDown();
        }
    }

    public synchronized void redrawAndWait(CountDownLatch latch, CountDownLatch latch2) {
        this.drawLatch2 = latch2;
        redraw(); // Force redraw THIS DONT REDRAW INMEDIATELY BUT IN THE NEXT THREAD LOOP!!!!!!!!
        latch.countDown();

    }

    private void processQueue() {
        while (!queue.isEmpty()) {
            try {
                queue.take().run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void beginDraw() {
        queue.add(() -> {
            synchronized (pg) {
                pg.beginDraw();
                pg.clear();
                PaintStyle bgColor = config.getBackgroundColor();
                if (bgColor instanceof JMColor) {
                    JMColor c = (JMColor) bgColor;
                    pg.background((float)(255*c.r),(float)(255*c.g),(float)(255*c.b));
                }
            }
        });
    }

    public void endDraw(CountDownLatch drawLatch) {
        queue.add(() -> {
            synchronized (pg) {
                pg.endDraw();
                pg.loadPixels();
                renderedImage = pgToBufferedImage(pg);
            }
            if (drawLatch != null) {
                drawLatch.countDown(); // Signal that drawing is done
            }
        });

    }

    public void drawCircle(int x, int y, int radius) {
        queue.add(() -> {
            synchronized (pg) {
                pg.fill(0, 100, 255); // Color del círculo
                pg.ellipse(x, y, radius * 2, radius * 2); // Dibuja el círculo
            }
        });
    }

    public void applyStyle(MathObject obj, Stylable style) {
        JMColor drawColor = (JMColor) style.getDrawColor();
        JMColor fillColor = (JMColor) style.getFillColor();
        float fillAlpha = (float) (255 * fillColor.getAlpha());
        float drawAlpha = (float) (255 * drawColor.getAlpha());
        float th = computeThickness(obj, style.getThickness());
//        queue.add(() -> {
//            synchronized (pg) {
                pg.stroke((float) (255 * drawColor.r), (float) (255 * drawColor.g), (float) (255 * drawColor.b), drawAlpha);
                pg.fill((float) (255 * fillColor.r), (float) (255 * fillColor.g), (float) (255 * fillColor.b), fillAlpha);
                pg.strokeWeight(th);
//            }
//        });
    }

    public void drawPath(Shape mobj, Camera camera) {
        queue.add(() -> {
            setupCamera(camera);
            applyStyle(mobj, mobj.getMp());
            pg.beginShape();
            for (int i = 0; i < mobj.size(); i++) {
                JMPathPoint p = mobj.get(i);
                float x = (float) p.p.v.x;
                float y = (float) p.p.v.y;
                float z = (float) p.p.v.z;
                pg.vertex(x, y, z);
            }
            pg.endShape();
        });
    }

    void setupCamera(Camera camera) {
        Rect bb = camera.getMathView();
        float centerX = (float) bb.getCenter().v.x;
        float centerY = (float) bb.getCenter().v.y;
// Calcular la altura visible en base al ancho y la proporción de la ventana
        float h = (float) bb.getHeight();
        float w = (float) bb.getWidth();
//        pg.translate(width / 2, height / 2, 0);
//        pg.ortho(centerX-w/2,centerX+w/2,centerY-h/2,centerY+h/2);

        // Configurar la vista ortográfica
        pg.ortho(centerX - w / 2, centerX + w / 2, centerY + h / 2, centerY - h / 2, -10, 10);
        // Posicionar la cámara en el centro de la pantalla
        pg.camera(centerX, centerY, (float) ((4 / 2.0) / tan((float) (PI * 30.0 / 180.0))), centerX, centerY, 0, 0, 1, 0);
    }

//    public void drawShape(
//            float[] xx1, float yy1[], float zz1[],
//            float[] mx1, float[] my1, float mz1[],
//            float[] mx2, float[] my2, float mz2[],
//            float[] xx2, float yy2[], float zz2[],
//            boolean closed) {
//        queue.add(() -> {
//            synchronized (pg) {
//                pg.beginShape();
//                pg.bezierDetail(50);
//                pg.vertex(xx1[0], yy1[0], zz1[0]);
//                for (int i = 0; i < xx1.length - (closed ? 0 : 1); i++) {
////                    pg.vertex(xx[i], yy[i]);
//                    pg.bezierVertex(
//                            mx1[i], my1[i], mz1[i],
//                            mx2[i], my2[i], mz2[i],
//                            xx2[i], yy2[i], zz2[i]
//                    );
//                }
//                if (closed) {
//                    pg.endShape(CLOSE);
//                } else {
//                    pg.endShape();
//                }
//            }
//        });
//    }
    public synchronized BufferedImage getRenderedImage() {
        return pgToBufferedImage(pg);
    }

    private BufferedImage pgToBufferedImage(PGraphics pg) {
        BufferedImage img = new BufferedImage(pg.width, pg.height, BufferedImage.TYPE_INT_ARGB);
        img.setRGB(0, 0, pg.width, pg.height, pg.pixels, 0, pg.width);
        return img;
    }

    public void exitSketch() {
        exit(); // Termina el sketch de Processing
    }

    public float computeThickness(MathObject mobj, double thickness) {
        Camera cam;
        if (mobj != null) {
            cam = (mobj.getMp().isAbsoluteThickness() ? config.getFixedCamera() : config.getCamera());
        } else {
            cam = config.getCamera();
        }

        //We use the correction factor mediaW/1066 in order to obtain the same apparent thickness
        //regardless of the resolution chosen. The reference value 1066 is the width in the preview settings
        return (float) Math.max(thickness / cam.getMathView().getWidth() * correctionThickness, MIN_THICKNESS);
//        return Math.max(mobj.getMp().getThickness() / cam.getMathView().getWidth() * 2.5d, MIN_THICKNESS);
    }
}
