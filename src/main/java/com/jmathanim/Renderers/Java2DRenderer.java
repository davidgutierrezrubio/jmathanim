/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Renderers;

import com.jmathanim.Utils.ConfigUtils;
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
import java.awt.BasicStroke;
import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.JOIN_ROUND;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class Java2DRenderer extends Renderer {

    private final BufferedImage bufferedImage;
    private final Graphics2D g2d;
    private Muxer muxer;
    private MuxerFormat format;
    private Codec codec;
    private Encoder encoder;
    private PixelFormat.Type pixelformat;
    private Rational rationalFrameRate;
    private MediaPacket packet;
    private MediaPicture picture;
    private final Properties cnf;
    String[] DEFAULT_CONFIG_JAVA2DRENDERER = {
        "FPS", "5",//TODO: This option is too global, should'nt be here!
        "ALPHA", "1",
        "BACKGROUND_COLOR","0"
    };
    protected Path2D.Double path;

    public Java2DRenderer(Properties configParam) {
        cnf = new Properties();
        ConfigUtils.digest_config(cnf, DEFAULT_CONFIG_JAVA2DRENDERER, configParam);
        int w = Integer.parseInt(cnf.getProperty("WIDTH"));
        int h = Integer.parseInt(cnf.getProperty("HEIGHT"));
        super.setSize(w, h);

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

    public final void prepareEncoder() {
        muxer = Muxer.make("c:\\media\\pinicula.mp4", null, "mp4");
        format = muxer.getFormat();
        codec = Codec.findEncodingCodec(format.getDefaultVideoCodecId());
        encoder = Encoder.make(codec);
        encoder.setWidth(width);
        encoder.setHeight(height);
        pixelformat = PixelFormat.Type.PIX_FMT_YUV420P;
        encoder.setPixelFormat(pixelformat);

        rationalFrameRate = Rational.make(1, Integer.parseInt(cnf.getProperty("FPS")));
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
        int[] screenx = camera.mathToScreen(x, y);
        int screenRadius = camera.mathToScreen(radius);
        g2d.drawOval(screenx[0], screenx[1], screenRadius, screenRadius);
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
        long rf = (long) (((double)frameCount)*rationalFrameRate.getDouble());
        System.out.println("Saving frame: "+frameCount);
        converter.toPicture(picture, screen, rf);
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
    }

    @Override
    public void drawArc(double x, double y, double radius, double angle) {
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
    public void createPath(double xx,double yy) {
        path=new Path2D.Double();
        int[] scr = camera.mathToScreen(xx, yy);
        path.moveTo(scr[0], scr[1]);
    }

    @Override
    public void addPointToPath(double xx,double yy) {
        int[] scr = camera.mathToScreen(xx, yy);
        path.lineTo(scr[0], scr[1]);
    }

    @Override
    public void closePath() {
        path.closePath();
    }
    @Override
    public void drawPath() {
        g2d.setColor(color);
        g2d.draw(path);
    }

}
