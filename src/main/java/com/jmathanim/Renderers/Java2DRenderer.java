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
import com.jmathanim.Utils.MathObjectDrawingProperties;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Shape;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com This class uses
 * Java2D to render the image
 */
public class Java2DRenderer extends Renderer {
    
    private static final boolean DEBUG_PATH_POINTS = true; //Draw control points and vertices
    private static final boolean PRINT_DEBUG = false; //Draw control points and vertices
    private static final boolean BOUNDING_BOX_DEBUG = false; //Draw bounding boxes

    public boolean createMovie;
    public boolean showPreview;
    
    private final BufferedImage bufferedImage;
    private final Graphics2D g2d;
    public Camera2D camera;
    public Camera2D fixedCamera;
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
    private JFrame frame;
    private JPanel panel;
    private JMathAnimScene parentScene;

    //To limit fps in preview window
    double interpolation = 0;
    final int TICKS_PER_SECOND = 5;
    final int SKIP_TICKS = 1000 / TICKS_PER_SECOND;
    final int MAX_FRAMESKIP = 5;
    
    public Java2DRenderer(JMathAnimScene parentScene) {
        this(parentScene, true, false);//default values
    }
    
    public Java2DRenderer(JMathAnimScene parentScene, boolean createMovie, boolean showPreview) {
        this.parentScene = parentScene;
        cnf = parentScene.conf;
        this.createMovie = createMovie;
        this.showPreview = showPreview;
        camera = new Camera2D(cnf.mediaW, cnf.mediaH);
        //The Fixed camera doesn't change. It is used to display fixed-size objects
        //like heads of arrows or text
        fixedCamera = new Camera2D(cnf.mediaW, cnf.mediaH);

        fixedCamera.setMathXY(-5, 5, 0);//Fixed camera is 10 units by default
        super.setSize(cnf.mediaW, cnf.mediaH);
        
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
        
        if (showPreview) {
            frame = new JFrame("Previsualization");
            frame.setSize(width, height);//TODO: Scale window to fixed size
            panel = new JPanel();
            frame.add(panel);
            frame.setVisible(true);
        }
        
        if (createMovie) {
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
    }
    
    @Override
    public void drawCircle(double x, double y, double radius) {
        
        g2d.setColor(borderColor);
        double mx = x - .5 * radius;
        double my = y + .5 * radius;
        int[] screenx = camera.mathToScreen(mx, my);
        int screenRadius = camera.mathToScreen(radius);
        g2d.fillOval(screenx[0], screenx[1], screenRadius, screenRadius);
    }
    
    @Override
    public void drawDot(Point p) {
        setStroke(p);
        int[] xx = camera.mathToScreen(p.v.x, p.v.y);
        
        g2d.setColor(p.mp.drawColor);
        g2d.drawLine(xx[0], xx[1], xx[0], xx[1]);
    }
    
    @Override
    public void saveFrame(int frameCount) {
        if (showPreview) {
            //Draw into a window
            Graphics gr = panel.getGraphics();
            gr.drawImage(bufferedImage, 0, 0, null);

//            //Ensure fps is set to movie
//            double next_game_tick = System.currentTimeMillis();
//            int loops;
//
//                loops = 0;
//                while (System.currentTimeMillis() > next_game_tick
//                        && loops < MAX_FRAMESKIP) {
//
//
//                    next_game_tick += SKIP_TICKS;
//                    loops++;
//                }
//            try {
//                Thread.sleep(10);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(Java2DRenderer.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }
        if (createMovie) {
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
    }
    
    @Override
    public void finish() {
        if (createMovie) {
            /**
             * Encoders, like decoders, sometimes cache pictures so it can do
             * the right key-frame optimizations. So, they need to be flushed as
             * well. As with the decoders, the convention is to pass in a null
             * input until the output is not complete.
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
        if (showPreview) {
            frame.setVisible(false);
            frame.dispose();
        }
        System.exit(0);
    }
    
    @Override
    public void clear() {
        g2d.setColor(Color.BLACK);//TODO: Poner en opciones
        g2d.fillRect(0, 0, width, height);
    }
    
    @Override
    public void setStroke(MathObject obj) {
        final double thickness = obj.mp.getThickness(this);
        int strokeSize;
        if (!obj.mp.absoluteThickness) {
            strokeSize = camera.mathToScreen(.0025 * thickness); //TODO: Another way to compute this
        } else {
            strokeSize = (int) thickness;
        }
        
        switch (obj.mp.dashStyle) {
            case MathObjectDrawingProperties.SOLID:
                BasicStroke basicStroke = new BasicStroke(strokeSize, CAP_ROUND, JOIN_ROUND);
                g2d.setStroke(basicStroke);
                break;
            case MathObjectDrawingProperties.DASHED:
                float[] dashedPattern = {5.0f * strokeSize, 2.0f * strokeSize};
                Stroke dashedStroke = new BasicStroke(strokeSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, dashedPattern, 1.0f);
                g2d.setStroke(dashedStroke);
                break;
            case MathObjectDrawingProperties.DOTTED:
                float[] dottedPattern = {1f, 2.0f * strokeSize};
                Stroke dottedStroke = new BasicStroke(strokeSize, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND, 10.0f, dottedPattern, 1.0f);
                g2d.setStroke(dottedStroke);
                break;
        }
        
    }
    
    @Override
    public void drawPath(Shape mobj) {
        drawPath(mobj, camera);
    }
    
    public void drawPath(Shape mobj, Camera2D cam) {
        
        JMPath c = mobj.getPath();
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
            path = createPathFromJMPath(mobj, cam);
            
            if (mobj.mp.fill) {
                g2d.setPaint(mobj.mp.fillColor);
                g2d.fill(path);
            }
            //Border is always drawed
            g2d.setColor(mobj.mp.drawColor);
            g2d.draw(path);
            setStroke(mobj);
            if (PRINT_DEBUG) {
                System.out.println("Drawing " + c);
            }
            if (BOUNDING_BOX_DEBUG) {
                debugBoundingBox(c.getBoundingBox());
            }
        }
    }
    
    public Path2D.Double createPathFromJMPath(Shape mobj, Camera2D cam) {
        JMPath c = mobj.getPath();
        Path2D.Double resul = new Path2D.Double();

        //TODO: Convert this in its own reusable method
        //First, I move the curve to the first point
        Vec p = c.getPoint(0).p.v;
        if (DEBUG_PATH_POINTS) {
            debugPathPoint(c.getPoint(0), c);
        }
        int[] scr = cam.mathToScreen(p);
        resul.moveTo(scr[0], scr[1]);
        //Now I iterate to get the next points
        int numPoints = c.size();
        if (c.isClosed()) {
            numPoints++; //Draw fron n to 0 
        }
        //Draw from point [n] to point [n+1]
        int prev[] = {scr[0], scr[1]};
        int xy[] = {scr[0], scr[1]};
        
        for (int n = 1; n < numPoints; n++) {
            
            Vec point = c.getPoint(n).p.v;
            Vec cpoint1 = c.getPoint(n - 1).cp1.v;
            Vec cpoint2 = c.getPoint(n).cp2.v;
            prev[0] = xy[0];
            prev[1] = xy[1];
            xy = cam.mathToScreen(point);
            
            int[] cxy1 = cam.mathToScreen(cpoint1);
            int[] cxy2 = cam.mathToScreen(cpoint2);
            if (DEBUG_PATH_POINTS) {
                debugPathPoint(c.getPoint(n), c);
            }
//            if ((prev[0]!=xy[0]) | (prev[1]!=xy[1])){
            if (true) {
                if (c.getPoint(n).isVisible) {
                    if (c.getPoint(n).isCurved) {
                        resul.curveTo(cxy1[0], cxy1[1], cxy2[0], cxy2[1], xy[0], xy[1]);
                    } else {
                        resul.lineTo(xy[0], xy[1]);
//                        System.out.println("Line from "+prev[0]+", "+prev[1]+" to "+xy[0]+","+xy[1]);
                    }
                } else {
                    resul.moveTo(xy[0], xy[1]);
//                g2d.drawString("M", xy[0], xy[1]);
                }
            }
        }
        if (c.isClosed()) {
            //closePath method draws a straight line to the last moveTo, so we
            //have to move first to the first point of our path
            resul.moveTo(scr[0], scr[1]);
            resul.closePath();
        }
//        if (c.isClosed()) {
//            if (c.getPoint(0).isVisible) {
//                resul.closePath();
//            } else {
//                int[] xy = camera.mathToScreen(c.getPoint(0).p.v);
//                resul.moveTo(xy[0], xy[1]);
//            }
//        }
        return resul;
    }
    
    @Override
    public void setCameraSize(int w, int h) {
        camera.setSize(w, h);
    }
    
    public void debugPathPoint(JMPathPoint p, JMPath path) {
        int[] x = camera.mathToScreen(p.p.v.x, p.p.v.y);
        debugCPoint(camera.mathToScreen(p.cp1.v.x, p.cp1.v.y));
        debugCPoint(camera.mathToScreen(p.cp2.v.x, p.cp2.v.y));
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
        debugText(String.valueOf(path.jmPathPoints.indexOf(p)), x[0] + 5, x[1]);
        
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
        Font font = new Font("Serif", Font.PLAIN, 12);
        g2d.setFont(font);
        g2d.setColor(Color.WHITE);
        g2d.drawString(texto, x, y);
    }
    
    public void debugBoundingBox(Rect r) {
        double[] ULCorner = {r.xmin, r.ymax};
        double[] DRCorner = {r.xmax, r.ymin};
        int[] scUL = camera.mathToScreen(ULCorner[0], ULCorner[1]);
        int[] scDR = camera.mathToScreen(DRCorner[0], DRCorner[1]);
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.setStroke(new BasicStroke(1, CAP_ROUND, JOIN_ROUND));
        g2d.drawRect(scUL[0], scUL[1], scDR[0] - scUL[0], scDR[1] - scUL[1]);
    }

    /**
     * Returns equivalent position from default camera to fixed camera.
     *
     * @param v
     * @return
     */
    public Vec defaultToFixedCamera(Vec v) {
        double width1 = camera.getMathBoundaries().getWidth();
        int[] ms = camera.mathToScreen(v);
        double[] coords = fixedCamera.screenToMath(ms[0], ms[1]);
//        System.out.println(width1 + ",   " + v + "    MS(" + ms[0] + ", " + ms[1] + ")");

        return new Vec(coords[0], coords[1]);
        
    }
    
    @Override
    public void drawAbsoluteCopy(Shape sh, Vec v) {
        Shape shape = sh.copy();
        Vec vFixed = defaultToFixedCamera(v);
        shape.shift(vFixed.minus(v));
        drawPath(shape, fixedCamera);
    }

    @Override
    public Camera getFixedCamera() {
        return fixedCamera;
    }
}
