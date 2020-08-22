/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Renderers;

import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.Cameras.Camera;
import com.jmathanim.Cameras.Camera2D;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.JMPathPoint;
import io.humble.video.Codec;
import io.humble.video.Encoder;
import io.humble.video.MediaPacket;
import io.humble.video.MediaPicture;
import io.humble.video.Muxer;
import io.humble.video.MuxerFormat;
import io.humble.video.PixelFormat;
import io.humble.video.Rational;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.JOIN_ROUND;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com This class uses
 * Java2D to render the image
 */
public class Java2DRenderer extends Renderer {

    private static final boolean DEBUG = true; //Draw control points and vertices
    private static final boolean PRINT_DEBUG = false; //Draw control points and vertices
    private final BufferedImage bufferedImage;
    private final Graphics2D g2d;
    public Camera2D camera;
    private Muxer muxer;
    private MuxerFormat format;
    private Codec codec;
    private Encoder encoder;
    private PixelFormat.Type pixelformat;
    private Rational rationalFrameRate;
    private MediaPacket packet;
    private MediaPicture picture;
    String[] DEFAULT_CONFIG_JAVA2DRENDERER = {
        "ALPHA", "1",
        "BACKGROUND_COLOR", "0"
    };
    protected Path2D.Double path;
    final String saveFilePath = "c:\\media\\pinicula.mp4";
    private final JMathAnimConfig cnf;

    public Java2DRenderer(JMathAnimScene parentScene) {
        cnf = parentScene.conf;
        camera = new Camera2D(cnf.width, cnf.height);
        super.setSize(cnf.width, cnf.height);

        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        g2d = bufferedImage.createGraphics();
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2d.setRenderingHints(rh);
        prepareEncoder();

    }

    @Override
    public Camera2D getCamera() {
        return camera;
    }

    /**
     *
     * @param camera
     */
    public void setCamera(Camera camera) {
        this.camera = (Camera2D) camera;
        camera.setSize(width, height);
    }

    public final void prepareEncoder() {

        System.out.println("Prepare encoder...");
        muxer = Muxer.make(saveFilePath, null, "mp4");
        format = muxer.getFormat();
        codec = Codec.findEncodingCodec(format.getDefaultVideoCodecId());
        encoder = Encoder.make(codec);
        encoder.setWidth(width);
        encoder.setHeight(height);
        pixelformat = PixelFormat.Type.PIX_FMT_YUV420P;
        encoder.setPixelFormat(pixelformat);

//        rationalFrameRate = Rational.make(1, Integer.parseInt(cnf.getProperty("FPS")));
        rationalFrameRate = Rational.make(1, cnf.fps);
        encoder.setTimeBase(rationalFrameRate);
        if (format.getFlag(MuxerFormat.Flag.GLOBAL_HEADER)) {
            encoder.setFlag(Encoder.Flag.FLAG_GLOBAL_HEADER, true);
        }
        /**
         * Open the encoder.
         */
        encoder.open(null, null);
        /**
         * Add this stream to the muxer.
         */
        muxer.addNewStream(encoder);

        try {
            /**
             * And open the muxer for business.
             */
            muxer.open(null, null);
        } catch (InterruptedException | IOException ex) {
            Logger.getLogger(Java2DRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
        packet = MediaPacket.make();
        picture = MediaPicture.make(
                encoder.getWidth(),
                encoder.getHeight(),
                pixelformat);
        picture.setTimeBase(rationalFrameRate);
    }

    @Override
    public void drawCircle(double x, double y, double radius) {
        g2d.setColor(color);
        double mx = x - .5 * radius;
        double my = y + .5 * radius;
        int[] screenx = camera.mathToScreen(mx, my);
        int screenRadius = camera.mathToScreen(radius);
        g2d.fillOval(screenx[0], screenx[1], screenRadius, screenRadius);
//        g2d.drawRect(screenx[0],screenx[1], screenRadius, screenRadius);
    }

    @Override
    public void drawLine(double x1, double y1, double x2, double y2) {
        g2d.setColor(color);
        int[] screenx1 = camera.mathToScreen(x1, y1);
        int[] screenx2 = camera.mathToScreen(x2, y2);
        g2d.drawLine(screenx1[0], screenx1[1], screenx2[0], screenx2[1]);
    }

    @Override
    public void saveFrame(int frameCount) {
        BufferedImage screen = MediaPictureConverterFactory.convertToType(bufferedImage, BufferedImage.TYPE_3BYTE_BGR);
        MediaPictureConverter converter = MediaPictureConverterFactory.createConverter(screen, picture);
        converter.toPicture(picture, screen, frameCount);
        do {
            encoder.encode(packet, picture);
            if (packet.isComplete()) {
                muxer.write(packet, false);
            }
        } while (packet.isComplete());
    }

    @Override
    public void finish() {
        /**
         * Encoders, like decoders, sometimes cache pictures so it can do the
         * right key-frame optimizations. So, they need to be flushed as well.
         * As with the decoders, the convention is to pass in a null input until
         * the output is not complete.
         */
        System.out.println("Finishing movie...");
        do {
            encoder.encode(packet, null);
            if (packet.isComplete()) {
                muxer.write(packet, false);
            }
        } while (packet.isComplete());

        /**
         * Finally, let's clean up after ourselves.
         */
        muxer.close();
        System.out.println("Movie created at " + saveFilePath);
    }

    @Override
    public void drawArc(double x, double y, double radius, double angle) {
        g2d.setColor(color);
    }

    @Override
    public void clear() {
        g2d.setColor(Color.BLACK);//TODO: Poner en opciones
        g2d.fillRect(0, 0, width, height);
    }

    @Override
    public void setStroke(double mathSize) {
        int strokeSize = camera.mathToScreen(mathSize); //TODO: Another way to compute this
        g2d.setStroke(new BasicStroke(strokeSize, CAP_ROUND, JOIN_ROUND));
    }

    @Override
    public void drawPath(JMPath c) {

        int numPoints = c.size();
        int minimumPoints = 2;

//        switch (c.curveType) {
//            case JMPath.STRAIGHT:
//                minimumPoints = 2;
//                break;
//            case JMPath.CURVED:
//                minimumPoints = 4;
//                break;
//            default:
//                throw new UnsupportedOperationException("Error: Illegal type of JMPath: " + c.curveType);
//        }
        if (numPoints >= minimumPoints) {
            path = createPathFromJMPath(c);
            g2d.setColor(color);
            g2d.draw(path);
            if (PRINT_DEBUG) {
                System.out.println("Drawing " + c);
            }
        }
    }

    public Path2D.Double createPathFromJMPath(JMPath c) {
        Path2D.Double resul = new Path2D.Double();
        int numPoints = c.size();
        //TODO: Convert this in its own reusable method
        //First, I move the curve to the first point
        Vec p = c.getPoint(0).p.v;
        int[] scr = camera.mathToScreen(p);
        resul.moveTo(scr[0], scr[1]);
        //Now I iterate to get the next points
        if (!c.isClosed()) {
            numPoints--; //Don't draw last point
        }
        //Draw from point [n] to point [n+1]
        for (int n = 0; n < numPoints; n++) {

            Vec point = c.getPoint(n + 1).p.v;
            Vec cpoint1 = c.getPoint(n).cp1.v;
            Vec cpoint2 = c.getPoint(n + 1).cp2.v;

            int[] xy = camera.mathToScreen(point);
            int[] cxy1 = camera.mathToScreen(cpoint1);
            int[] cxy2 = camera.mathToScreen(cpoint2);
            if (DEBUG) {
//                debugPoint(xy);
//                debugCPoint(cxy1);
//                debugCPoint(cxy2);
                debugPathPoint(c.getPoint(n));
            }
            if (c.getPoint(n + 1).isVisible) {
                if (c.getPoint(n + 1).isCurved) {
                    resul.curveTo(cxy1[0], cxy1[1], cxy2[0], cxy2[1], xy[0], xy[1]);
                } else {
                    resul.lineTo(xy[0], xy[1]);
                }
            } else {
                resul.moveTo(xy[0], xy[1]);
            }
        }

        if (c.isClosed()) {
            resul.closePath();
        }
        return resul;
    }

    @Override
    public void setAlpha(double alpha) {
        AlphaComposite alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) alpha);
        g2d.setComposite(alcom);
    }

    @Override
    public void setCameraSize(int w, int h) {
        camera.setSize(w, h);
    }

    public void debugPathPoint(JMPathPoint p) {
        int[] x = camera.mathToScreen(p.p.v.x, p.p.v.y);

        if (p.type == JMPathPoint.TYPE_VERTEX) {
            g2d.setColor(Color.GREEN);
            
        }
        if (p.type == JMPathPoint.TYPE_INTERPOLATION_POINT) {
            g2d.setColor(Color.GRAY);
            
        }
        if (p.isCurved) {
            g2d.drawOval(x[0] - 4, x[1] - 4, 8, 8);
        } else {
            g2d.drawRect(x[0] - 2, x[1] - 2, 4, 4);
        }
    }

    public void debugPoint(int x, int y) {
        g2d.drawOval(x - 2, y - 2, 4, 4);
    }

    public void debugPoint(int[] xy) {
        g2d.setColor(Color.BLUE);
        g2d.drawOval(xy[0] - 4, xy[1] - 4, 8, 8);
    }

    public void debugCPoint(int[] xy) {
        g2d.setColor(Color.PINK);
        g2d.drawRect(xy[0] - 4, xy[1] - 4, 8, 8);
    }

    public void debugText(String texto, int x, int y) {
        System.out.println("DEBUG TEXT: " + texto);
        Font font = new Font("Serif", Font.PLAIN, 12);
        g2d.setFont(font);
        g2d.setColor(Color.WHITE);
        g2d.drawString(texto, x, y);
    }

}
