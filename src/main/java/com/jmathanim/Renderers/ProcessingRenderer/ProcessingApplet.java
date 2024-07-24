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
import com.jmathanim.Cameras.Camera3D;
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
import com.jogamp.opengl.GL2;
import java.awt.image.BufferedImage;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import processing.core.PApplet;
import static processing.core.PConstants.P3D;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PGL;
import processing.opengl.PGraphicsOpenGL;

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

    PGL pgl;
    GL2 gl;

    public CountDownLatch setupLatch;
    private static BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private final ProcessingRenderer pRenderer;
    public double correctionThickness;
    private static final double MIN_THICKNESS = .2d;

    public ProcessingApplet(ProcessingRenderer pRenderer, JMathAnimConfig config, CountDownLatch latch) {
        this.config = config;
        this.pRenderer = pRenderer;
        this.setupLatch = latch;

        correctionThickness = config.mediaW * 1d / 1066;//Correction factor for thickness
    }

    public void settings() {
        size(config.mediaW, config.mediaH, P3D); // Tamaño de la ventana con P3D
        smooth(4);
        noLoop();
        setupLatch.countDown(); // Signal that setup is complete
    }

    public void setup() {

        pg = createGraphics(config.mediaW, config.mediaH, P3D);
        pg.smooth(4);
        surface.setResizable(false);
        setupLatch.countDown(); // Signal that setup is complete
    }

    public void draw() {

        synchronized (lock) {

            while (!queue.isEmpty()) {
                try {
                    queue.take().run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            image(pg, 0, 0); // Dibuja el PGraphics en la ventana principal
        }
        if (drawLatch != null) {
            drawLatch.countDown(); // Signal that drawing is done
        }
        if (this.drawLatch2 != null) {
            this.drawLatch2.countDown();
        }
    }

    public synchronized void redrawAndWait(CountDownLatch latch, CountDownLatch latch2) {
        this.drawLatch2 = latch2;
        redraw(); // Force redraw THIS DONT REDRAW INMEDIATELY BUT IN THE NEXT THREAD LOOP!!!!!!!!
    }

    public void beginDraw() {
        queue.add(() -> {
            synchronized (pg) {
                clear();
                pg.beginDraw();
                PaintStyle bgColor = config.getBackgroundColor();
                if (bgColor instanceof JMColor) {
                    JMColor c = (JMColor) bgColor;
//                    pg.background(100, 200, 100,255);
                    pg.background((float) (255 * c.r), (float) (255 * c.g), (float) (255 * c.b), 255);
//                    background((float) (255 * c.r), (float) (255 * c.g), (float) (255 * c.b));
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
        pg.strokeCap(ROUND);
//            }
//        });
    }

    public void drawPath(Shape mobj, Camera3D camera) {
        queue.add(() -> {
            boolean isContour = false;
            applyCameraNew(camera);
            applyStyle(mobj, mobj.getMp());
            pg.beginShape();
            pg.bezierDetail(50);
            //First point, a pg.vertex command
            Vec v = mobj.get(0).p.v;
            pg.vertex((float) v.x, (float) v.y, (float) v.z);

            for (int i = 0; i <= mobj.size(); i++) {
                JMPathPoint p1 = mobj.get(i);
                JMPathPoint p2 = mobj.get(i + 1);
                float[] x = getJPMPathPointCoordinates(p1, p2);
                if (p2.isThisSegmentVisible) {

                    if (p2.isCurved) {
                        pg.bezierVertex(
                                x[3], x[4], x[5],
                                x[6], x[7], x[8],
                                x[9], x[10], x[11]
                        );
                    } else {
                        pg.vertex(x[9], x[10], x[11]);
                    }
                } else {//Time to do contours!!
                    if (isContour) {
                        pg.endContour();
                    }
                    pg.beginContour();
                    isContour = true;
                    pg.vertex(x[9], x[10], x[11]);
                }
            }
            if (isContour) {
                pg.endContour();
            }
            pg.endShape();
        });
    }

    public float[] getJPMPathPointCoordinates(JMPathPoint p1, JMPathPoint p2) {
        float[] x = new float[12];
        x[0] = (float) p1.p.v.x;
        x[1] = (float) p1.p.v.y;
        x[2] = (float) p1.p.v.z;

        x[3] = (float) p1.cpExit.v.x;
        x[4] = (float) p1.cpExit.v.y;
        x[5] = (float) p1.cpExit.v.z;

        x[6] = (float) p2.cpEnter.v.x;
        x[7] = (float) p2.cpEnter.v.y;
        x[8] = (float) p2.cpEnter.v.z;

        x[9] = (float) p2.p.v.x;
        x[10] = (float) p2.p.v.y;
        x[11] = (float) p2.p.v.z;
        return x;
    }

    private void applyCameraNew(Camera3D camera) {
        pg.scale(1, -1, 1);
        // Calcular el vector de dirección de la cámara (dónde está mirando)
        Vec direction = camera.eye.to(camera.look).normalize();
        direction.y = -direction.y;
        direction.z = -direction.z;
        // Definir el vector "arriba" para asegurar que el eje Z está hacia arriba
        Vec up = new Vec(0, 1, 0);
        up.y=-up.y;
        up.z=-up.z;
        double interpFactor = 100 * (Math.abs(direction.y) + Math.abs(direction.x));
        if (interpFactor > 1) {
            up = Vec.to(0, 0, 1);
        } else {
            up = up.interpolate(Vec.to(0, 0, 1), interpFactor).normalize();
        }
        System.out.println("aa" + up);

        // Configurar la proyección de perspectiva
        float fov = PI / 3; // Campo de visión
        float aspect = width * 1f / height;
        float zNear = 0.1f;
        float zFar = 100;
        pg.perspective(fov, aspect, zNear, zFar);
        float eyeX = (float) camera.eye.v.x;
        float eyeY = (float) camera.eye.v.y;
        float eyeZ = -(float) camera.eye.v.z;
        float lookX = (float) camera.look.v.x;
        float lookY = (float) camera.look.v.y;
        float lookZ = -(float) camera.look.v.z;
        // Configurar la cámara
        pg.camera(eyeX, eyeY, eyeZ, // Posición de la cámara
                lookX, lookY, lookZ, // Punto al que la cámara está mirando
                (float) up.x, (float) up.y, (float) up.z);      // Vector "arriba"

    }

    private void applyCameraOld(Camera3D camera) {

        Rect bb = camera.getMathView();
        float centerX = (float) bb.getCenter().v.x;
        float centerY = (float) bb.getCenter().v.y;
// Calcular la altura visible en base al ancho y la proporción de la ventana
        float h = (float) bb.getHeight();
        float w = (float) bb.getWidth();
//        pg.translate(width / 2, height / 2, 0);
//        pg.ortho(centerX-w/2,centerX+w/2,centerY-h/2,centerY+h/2);
        boolean prueba3D = true;
        if (!prueba3D) {
            // Configurar la vista ortográfica. Esto funciona para la cámara 2D
            pg.ortho(centerX - w / 2, centerX + w / 2, centerY + h / 2, centerY - h / 2, -10, 10);
            // Posicionar la cámara en el centro de la pantalla
            pg.camera(centerX, centerY, (float) ((4 / 2.0) / tan((float) (PI * 30.0 / 180.0))), centerX, centerY, 0, 0, 1, 0);
//        pg.rotateX(-PI / 6);
//        pg.rotateY(PI / 3);
        } else {
            //pruebas camara 3D
//            pg.ortho(centerX - w / 2, centerX + w / 2, centerY + h / 2, centerY - h / 2, -10, 10);
//            pg.perspective();

            float fov = (float) (PI / 4.0);
            float cameraZ = (float) ((height / 2.0) / tan((float) (fov / 2.0)));
            pg.perspective(fov, width * 1f / height, .1f, 100);

            // Calcular el vector "arriba" dinámicamente
            // Asegurar que el eje Y apunte hacia arriba en la pantalla
            float eyeX = (float) camera.eye.v.x;
            float eyeY = (float) camera.eye.v.y;
            float eyeZ = (float) camera.eye.v.z;
            float lookX = (float) camera.look.v.x;
            float lookY = (float) camera.look.v.y;
            float lookZ = (float) camera.look.v.z;
            float upX = 0;
            float upY = 0;
            float upZ = 1;

            if (abs(eyeX - lookX) < 0.01 && abs(eyeZ - lookZ) < 0.01) {
                // Si estamos mirando directamente hacia abajo o hacia arriba, ajustar el vector "arriba"
                upX = 0;
                upY = eyeY > lookY ? -1 : 1;
                upZ = 0; // Determinar la dirección "arriba"
            }

            pg.camera(
                    eyeX,
                    eyeY,
                    eyeZ,
                    lookX,
                    lookY,
                    lookZ,
                    upX, upY, upZ
            );
            pg.scale(1, -1, -1);
        }

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
        BufferedImage img = new BufferedImage(pg.width, pg.height, BufferedImage.TYPE_INT_RGB);
        img.setRGB(0, 0, pg.width, pg.height, pg.pixels, 0, pg.width);
        return img;
    }
//    BufferedImage pgToBufferedImage(PGraphics pg) {
//        PImage pImg = pg.get();
//        BufferedImage bImg = new BufferedImage(pImg.width, pImg.height, BufferedImage.TYPE_INT_RGB);
//        for (int y = 0; y < pImg.height; y++) {
//            for (int x = 0; x < pImg.width; x++) {
//                int pixel = pImg.get(x, y);
//                bImg.setRGB(x, y, pixel);
//            }
//        }
//        return bImg;
//    }

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
