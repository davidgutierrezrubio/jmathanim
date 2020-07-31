/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Renderers;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Cameras.Camera2D;
import com.jmathanim.Utils.ConfigUtils;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
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
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com This class uses
 * Java2D to render the image
 */
public class Java2DRenderer extends Renderer {

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
    private ArrayList<int[]> pointsList;
    private boolean closePath;

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
    public void drawPolygon() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
//        String fname = "c:\\media\\screen-" + String.format("%05d", frameCount) + ".png";
//        File file = new File(fname);
//        try {
//            ImageIO.write(bufferedImage, "png", file);
//        } catch (IOException ex) {
//            Logger.getLogger(JMathAnimScene.class.getName()).log(Level.SEVERE, null, ex);
//        }
        BufferedImage screen = MediaPictureConverterFactory.convertToType(bufferedImage, BufferedImage.TYPE_3BYTE_BGR);
        MediaPictureConverter converter = MediaPictureConverterFactory.createConverter(screen, picture);
        //      @param timestamp the time stamp which should be attached to the the
        //       video picture (in microseconds).
//        long rf = (long) (((double) frameCount) * rationalFrameRate.getDouble());
//        System.out.println("Saving frame: " + frameCount);
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

//    @Override
//    public void createPath(double xx, double yy) {
//        closePath = false;
//        pointsList = new ArrayList<int[]>();
//        int[] scr = camera.mathToScreen(xx, yy);
//        pointsList.add(scr);
//    }
//
//    @Override
//    public void addPointToPath(double xx, double yy) {
//
//        int[] scr = camera.mathToScreen(xx, yy);
//        pointsList.add(scr);
//    }
//
//    @Override
//    public void closePath() {
//        closePath = true;
//    }
    @Override
    public void drawPath(Curve c) {
        path = new Path2D.Double();
        int numPoints = c.size();
        int minimumPoints=0;
        
        switch (c.curveType)
        {
            case Curve.STRAIGHT:
                minimumPoints=2;
                break;
            case Curve.CURVED:
                minimumPoints=4;
                break;
        }
        if (numPoints >= minimumPoints) 
        {
            //TODO: Convert this in its own reusable method
            //First, I move the curve to the first point
            Vec p = c.getPoint(0);
            int[] scr = camera.mathToScreen(p);
            path.moveTo(scr[0], scr[1]);
            //Now I iterate to get the next points
            if (!c.isClosed()) {
                numPoints--; //Don't draw last point
            }
            for (int n = 0; n < numPoints; n++) {
                int i = (n + 1) % c.size(); //Next point (first if actually we are in last)
                Vec point = c.getPoint(i);
                Vec cpoint1 = c.getControlPoint1(n);
                Vec cpoint2 = c.getControlPoint2(n);

                int[] xy = camera.mathToScreen(point);
                int[] cxy1 = camera.mathToScreen(cpoint1);
                int[] cxy2 = camera.mathToScreen(cpoint2);
                debugPoint(xy);
                debugCPoint(cxy1);
                debugCPoint(cxy2);

                if (c.curveType == Curve.CURVED) {
                    path.curveTo(cxy1[0], cxy1[1], cxy2[0], cxy2[1], xy[0], xy[1]);
                }
                if (c.curveType == Curve.STRAIGHT) {
                    path.lineTo(xy[0], xy[1]);
                }
//                path.lineTo(x3, y3);

            }

            if (c.isClosed()) {
                path.closePath();
            }
            g2d.setColor(color);
            g2d.draw(path);
        }
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

    public void debugPoint(int x, int y) {
        g2d.drawOval(x, y, 5, 5);
    }

    public void debugPoint(int[] xy) {
        g2d.setColor(Color.BLUE);
        g2d.drawOval(xy[0], xy[1], 5, 5);
    }

    public void debugCPoint(int[] xy) {
        g2d.setColor(Color.PINK);
        g2d.drawRect(xy[0], xy[1], 10, 10);
    }
}
