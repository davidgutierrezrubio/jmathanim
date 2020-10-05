package com.jmathanim.Renderers;

import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.Cameras.Camera;
import com.jmathanim.Cameras.Camera2D;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.MathObjectDrawingProperties;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.Utils.jhlabs.ShadowFilter;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.jmathanim.PreviewWindow;
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
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 * This class uses Java2D to render the image.
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class Java2DRenderer extends Renderer {

    private static final boolean DEBUG_LABELS = false; //Draw label objects
    private static final boolean DEBUG_PATH_POINTS = false; //Draw control points and vertices
    private static final boolean PRINT_DEBUG = false; //Draw control points and vertices
    private static final boolean BOUNDING_BOX_DEBUG = false; //Draw bounding boxes

    private BufferedImage drawBufferImage;
    private BufferedImage finalImage;
    private BufferedImage debugImage;
    private Graphics2D g2draw;
    private Graphics2D g2debug;
    private final Graphics2D g2dFinalImage;
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
    protected Path2D.Double path;
    private final JMathAnimConfig cnf;
    private final JMathAnimScene scene;
    private File saveFilePath;
    //To limit fps in preview window
    double interpolation = 0;
    final int TICKS_PER_SECOND = 5;
    final int SKIP_TICKS = 1000 / TICKS_PER_SECOND;
    final int MAX_FRAMESKIP = 5;
    private Image img;
    private PreviewWindow previewWindow;
    private final RenderingHints rh;
    private ConvolveOp ConvolveShadowOp;

    public Java2DRenderer(JMathAnimScene parentScene) {
        this.scene = parentScene;
        cnf = parentScene.conf;
        camera = new Camera2D(cnf.mediaW, cnf.mediaH);
        //The Fixed camera doesn't change. It is used to display fixed-size objects
        //like heads of arrows or text
        fixedCamera = new Camera2D(cnf.mediaW, cnf.mediaH);

        fixedCamera.setMathXY(-5, 5, 0);//Fixed camera is 10 units by default
        super.setSize(cnf.mediaW, cnf.mediaH);

        drawBufferImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        finalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        debugImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2draw = drawBufferImage.createGraphics();
        g2debug = debugImage.createGraphics();
        g2dFinalImage = finalImage.createGraphics();
        rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2draw.setRenderingHints(rh);
        g2dFinalImage.setRenderingHints(rh);
        prepareEncoder();

        //Proofs
        if (cnf.backGroundImage != null) {
            img = Toolkit.getDefaultToolkit().getImage(cnf.backGroundImage);
        }
//        This tracker waits for image to be fully loaded
        MediaTracker tracker = new MediaTracker(new JLabel());
        tracker.addImage(img, 1);
        try {
            tracker.waitForID(1);
        } catch (InterruptedException ex) {
            Logger.getLogger(Java2DRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Camera2D getCamera() {
        return camera;
    }

    /**
     *
     * @param camera
     */
    @Override
    public void setCamera(Camera camera) {
        this.camera = (Camera2D) camera;
        camera.setSize(width, height);
    }

    public final void prepareEncoder() {

        JMathAnimScene.logger.info("Preparing encoder");

        if (cnf.showPreview) {
            JMathAnimScene.logger.debug("Creating preview window");
            //TODO: Move this to its own class
            previewWindow = new PreviewWindow(this);
            previewWindow.buildGUI();

            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        previewWindow.setVisible(true);
                    }
                });
            } catch (InterruptedException | InvocationTargetException ex) {
                Logger.getLogger(Java2DRenderer.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        if (cnf.createMovie) {
            try {
                File tempPath = new File(cnf.getOutputDir().getCanonicalPath());
                tempPath.mkdirs();
                saveFilePath = new File(cnf.getOutputDir().getCanonicalPath() + File.separator + cnf.getOutputFileName() + "_" + cnf.mediaH + ".mp4");
                JMathAnimScene.logger.info("Creating movie encoder for {}", saveFilePath);
                muxer = Muxer.make(saveFilePath.getCanonicalPath(), null, "mp4");
            } catch (IOException ex) {
                Logger.getLogger(Java2DRenderer.class.getName()).log(Level.SEVERE, null, ex);
            }

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

        if (cnf.drawShadow) {
            computeShadowKernel();
        }

    }

    @Override
    public void drawCircle(double x, double y, double radius) {

//        g2draw.setColor(borderColor.getColor());
        double mx = x - .5 * radius;
        double my = y + .5 * radius;
        int[] screenx = camera.mathToScreen(mx, my);
        int screenRadius = camera.mathToScreen(radius);
        g2draw.fillOval(screenx[0], screenx[1], screenRadius, screenRadius);
    }

    @Override
    public void drawDot(Point p) {
        setStroke(g2draw, p);
//        int[] xx = camera.mathToScreen(p.v.x, p.v.y);
        Path2D.Double resul = new Path2D.Double();
        resul.moveTo(p.v.x, p.v.y);
        resul.lineTo(p.v.x, p.v.y);
        AffineTransform bTr = g2draw.getTransform();
        g2draw.setTransform(getCameratoG2DTransform(camera));
        g2draw.setColor(p.mp.fillColor.getColor());
        g2draw.draw(resul);
        g2draw.fill(resul);
        g2draw.setTransform(bTr);
    }

    @Override
    public void saveFrame(int frameCount) {
        //Draw all layers into finalimage
        if (cnf.drawShadow) {
            BufferedImage shadowImage = computeShadow(drawBufferImage);
            g2dFinalImage.drawImage(shadowImage, cnf.shadowOffsetX, cnf.shadowOffsetY, null);

        }

        g2dFinalImage.drawImage(drawBufferImage, 0, 0, null);
        //This layer is on top of everything, for debugging purposes
        g2dFinalImage.drawImage(debugImage, 0, 0, null);
        if (cnf.showPreview) {

            //Draw into a window
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        Graphics gr = previewWindow.drawPanel.getGraphics();
                        gr.drawImage(finalImage, 0, 0, null);
                        long timeElapsedInNanoSeconds = scene.nanoTime - scene.previousNanoTime;
                        long fpsComputed;
                        if (timeElapsedInNanoSeconds > 0) {
                            fpsComputed = 1000000000 / (timeElapsedInNanoSeconds);
                        } else {
                            fpsComputed = 0;
                        }

                        String statusText = String.format("frame=%d   t=%.2fs    fps=%d", frameCount, (1f * frameCount) / cnf.fps, fpsComputed);
                        previewWindow.statusLabel.setText(statusText);

                        if (cnf.delay) {
                            double tiempo = (1.d / cnf.fps) * 1000;
                            try {
                                long tiempoPasado = timeElapsedInNanoSeconds / 1000000;
//                System.out.println("Tiempo pasado en milisegundos: " + tiempoPasado);
                                long delay = (long) (tiempo - tiempoPasado);
//                System.out.println("delay " + delay);
                                if (delay > 0) {
                                    Thread.sleep(delay);
                                }
                            } catch (InterruptedException ex) {
                                Logger.getLogger(Java2DRenderer.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                    }
                });
            } catch (InterruptedException | InvocationTargetException ex) {
            }
            while (previewWindow.pauseToggleButton.isSelected()) {
                try {
                    Thread.sleep(100);//TODO: Improve this
                } catch (InterruptedException ex) {
                    Logger.getLogger(Java2DRenderer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
        if (cnf.createMovie) {
            BufferedImage screen = MediaPictureConverterFactory.convertToType(finalImage, BufferedImage.TYPE_3BYTE_BGR);
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
    public void finish(int frameCount) {
        System.out.println(String.format("%d frames created, %.2fs total time", frameCount, (1.f * frameCount) / cnf.fps));
        if (cnf.createMovie) {
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
        if (cnf.showPreview) {
            previewWindow.setVisible(false);
            previewWindow.dispose();
        }
        System.exit(0);
    }

    @Override
    public void clear() {
        g2dFinalImage.setColor(JMathAnimConfig.getConfig().getBackgroundColor().getColor());//TODO: Poner en opciones
        g2dFinalImage.fillRect(0, 0, width, height);

        if (img != null) {
//            drawScaledImage(img, g2dFinalImage);
            g2dFinalImage.drawImage(img, 0, 0, null);
        }
        //TODO: Find a more efficient way to erase a Alpha image
        drawBufferImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        debugImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2draw = drawBufferImage.createGraphics();
        g2debug = debugImage.createGraphics();
        g2draw.setRenderingHints(rh);

    }

    public void computeShadowKernel() {
        int dimension = cnf.shadowKernelSize;
        if (dimension > 0) {
            float valor = 1f / (dimension * dimension);
            float[] k = new float[dimension * dimension];
            for (int i = 0; i < dimension * dimension; i++) {
                k[i] = valor;
            }

            Kernel kernel = new Kernel(dimension, dimension, k);
            ConvolveShadowOp = new ConvolveOp(kernel, ConvolveOp.EDGE_ZERO_FILL, rh);
        }
    }

    public BufferedImage computeShadow(BufferedImage img) {
        BufferedImage resul = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
//        ColorModel cm = img.getColorModel();
//        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
//        WritableRaster raster = img.copyData(null);
//        resul = new BufferedImage(cm, raster, isAlphaPremultiplied, null);

        int[] imagePixels = img.getRGB(0, 0, width, height, null, 0, width);
        for (int i = 0; i < imagePixels.length; i++) {
            int color = imagePixels[i];// & 0xff000000;
            color = (int) ((color >> 56) * cnf.shadowAlpha) << 56;//TODO: Check this

            imagePixels[i] = color;
        }

        resul.setRGB(0, 0, width, height, imagePixels, 0, width);
//        if (ConvolveShadowOp != null) {
//            resul = ConvolveShadowOp.filter(resul, null);
//        }
        ShadowFilter fil = new ShadowFilter(cnf.shadowKernelSize, cnf.shadowOffsetX, cnf.shadowOffsetY, cnf.shadowAlpha);
        fil.setShadowOnly(true);
//        GaussianFilter fil = new GaussianFilter(cnf.shadowKernelSize);
        resul = fil.filter(img, null);
        return resul;
    }

    public void drawScaledImage(Image image, Graphics g) {
        int imgWidth = image.getWidth(null);
        int imgHeight = image.getHeight(null);

        double imgAspect = (double) imgHeight / imgWidth;

        int canvasWidth = width;
        int canvasHeight = height;

        double canvasAspect = (double) canvasHeight / canvasWidth;

        int x1 = 0; // top left X position
        int y1 = 0; // top left Y position
        int x2 = 0; // bottom right X position
        int y2 = 0; // bottom right Y position

        if (imgWidth < canvasWidth && imgHeight < canvasHeight) {
            // the image is smaller than the canvas
            x1 = (canvasWidth - imgWidth) / 2;
            y1 = (canvasHeight - imgHeight) / 2;
            x2 = imgWidth + x1;
            y2 = imgHeight + y1;
            g.drawImage(image, x1, y1, x2, y2, 0, 0, imgWidth, imgHeight, null);
        } else {
            if (canvasAspect > imgAspect) {
                y1 = canvasHeight;
                // keep image aspect ratio
                canvasHeight = (int) (canvasWidth * imgAspect);
                y1 = (y1 - canvasHeight) / 2;
            } else {
                x1 = canvasWidth;
                // keep image aspect ratio
                canvasWidth = (int) (canvasHeight / imgAspect);
                x1 = (x1 - canvasWidth) / 2;
            }
            x2 = canvasWidth + x1;
            y2 = canvasHeight + y1;
//            g.drawImage(img, 9, 9, null);
            g.drawImage(image, x1, y1, x2, y2, 0, 0, imgWidth, imgHeight, null);
        }

    }

    public void setStroke(Graphics2D g2d, MathObject obj) {
        final double thickness = obj.mp.getThickness(this) / 200;
//        int strokeSize;
//        if (!obj.mp.absoluteThickness) {
//            strokeSize = camera.mathToScreen(.0025 * thickness); //TODO: Another way to compute this
//        } else {
//            strokeSize = (int) thickness;
//        }
        float strokeSize = (float) thickness;

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

            if (mobj.mp.isFilled()) {
                //Filled paths are better drawn supposing all points are visible...
                //This works well on simply paths, but not path with multiple holes (like letter "B")
//                Path2D.Double pathToFill = createPathFromJMPath(mobj, mobj.getPath().allVisible(), cam);
//                g2draw.setColor(mobj.mp.fillColor.getColor());
//                g2draw.fill(pathToFill);
                AffineTransform bTr = g2draw.getTransform();
                g2draw.setTransform(getCameratoG2DTransform(cam));
                g2draw.setColor(mobj.mp.fillColor.getColor());
                g2draw.fill(path);
                g2draw.setTransform(bTr);

            }
            //Border is always drawed
            AffineTransform bTr = g2draw.getTransform();
            final AffineTransform cameratoG2DTransform = getCameratoG2DTransform(cam);

            g2draw.setColor(mobj.mp.drawColor.getColor());
            setStroke(g2draw, mobj);
            g2draw.setTransform(cameratoG2DTransform);
            g2draw.draw(path);
            g2draw.setTransform(bTr);

            if (PRINT_DEBUG) {
                System.out.println("Drawing " + c);
            }
            if (BOUNDING_BOX_DEBUG) {
                debugBoundingBox(c.getBoundingBox());
            }
        }
    }

    public AffineTransform getCameratoG2DTransform(Camera cam) {
        Rect r = cam.getMathView();
        //First, move UL math corner to screen (0,0)
        AffineTransform tr = AffineTransform.getTranslateInstance(-r.xmin, -r.ymax);
        //Now, scale it so that (xmax-xmin, ymax-ymin) goes to (W,H)
        AffineTransform sc = AffineTransform.getScaleInstance(width / (r.xmax - r.xmin), -height / (r.ymax - r.ymin));
        sc.concatenate(tr);
        return sc;
    }

    public Path2D.Double createPathFromJMPath(Shape mobj, Camera2D cam) {
        return createPathFromJMPath(mobj, mobj.getPath(), cam);
    }

    public Path2D.Double createPathFromJMPath(Shape mobj, JMPath c, Camera2D cam) {
        Path2D.Double resul = new Path2D.Double();
        Vec p = c.getJMPoint(0).p.v;

        if (DEBUG_PATH_POINTS) {
            debugPathPoint(c.getJMPoint(0), c);
        }
        double[] scr = {p.x, p.y};
        if (DEBUG_LABELS) {
            g2debug.setColor(Color.BLACK);
            g2debug.drawString(mobj.label, (float) scr[0], (float) scr[1]);
            g2debug.setColor(Color.WHITE);
            g2debug.drawString(mobj.label, (float) scr[0] + 2, (float) scr[1] + 2);
        }
        resul.moveTo(scr[0], scr[1]);
        //Now I iterate to get the next points
        int numPoints = c.size();
//        int prev[] = {scr[0], scr[1]};
        double xy[] = {scr[0], scr[1]};

        for (int n = 1; n < numPoints + 1; n++) {

            Vec point = c.getJMPoint(n).p.v;
            Vec cpoint1 = c.getJMPoint(n - 1).cp1.v;
            Vec cpoint2 = c.getJMPoint(n).cp2.v;
//            prev[0] = xy[0];
//            prev[1] = xy[1];
//            xy = cam.mathToScreen(point);
            xy[0] = point.x;
            xy[1] = point.y;

//            int[] cxy1 = cam.mathToScreen(cpoint1);
//            int[] cxy2 = cam.mathToScreen(cpoint2);
            double cxy1[] = {cpoint1.x, cpoint1.y};
            double cxy2[] = {cpoint2.x, cpoint2.y};
            if (DEBUG_PATH_POINTS) {
                debugPathPoint(c.getJMPoint(n), c);
            }
            if (c.getJMPoint(n).isThisSegmentVisible) {
                if (c.getJMPoint(n).isCurved) {
                    resul.curveTo(cxy1[0], cxy1[1], cxy2[0], cxy2[1], xy[0], xy[1]);
                } else {
                    resul.lineTo(xy[0], xy[1]);
                }
            } else {
                if (n < numPoints) {//If it is the last point, don't move (it creates a strange point at the beginning)
                    resul.moveTo(xy[0], xy[1]);
                }
            }
        }
//        if (c.getJMPoint(0).isThisSegmentVisible) {
//            if (xy[0] != scr[0] | xy[1] != scr[1]) {
//                resul.moveTo(scr[0], scr[1]);
//            }
//            resul.closePath();
//        }
        if (c.getNumberOfConnectedComponents() == 0) {
            resul.closePath();
        }
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
            g2debug.setColor(Color.GREEN);

        }
        if (p.type == JMPathPoint.TYPE_INTERPOLATION_POINT) {
            g2debug.setColor(Color.GRAY);

        }
        if (p.isCurved) {
            g2debug.drawOval(x[0] - 4, x[1] - 4, 8, 8);
        } else {
            g2debug.drawRect(x[0] - 2, x[1] - 2, 4, 4);
        }
        debugText(String.valueOf(path.jmPathPoints.indexOf(p)), x[0] + 5, x[1]);

    }

    public void debugPoint(int x, int y) {
        g2debug.drawOval(x - 2, y - 2, 4, 4);
    }

    public void debugPoint(int[] xy) {
        g2draw.setColor(Color.BLUE);
        g2draw.drawOval(xy[0] - 4, xy[1] - 4, 8, 8);
    }

    public void debugCPoint(int[] xy) {
        g2draw.setColor(Color.PINK);
        g2draw.drawRect(xy[0] - 4, xy[1] - 4, 8, 8);
    }

    public void debugText(String texto, int x, int y) {
        Font font = new Font("Serif", Font.PLAIN, 12);
        g2draw.setFont(font);
        g2draw.setColor(Color.WHITE);
        g2draw.drawString(texto, x, y);
    }

    public void debugBoundingBox(Rect r) {
        double[] ULCorner = {r.xmin, r.ymax};
        double[] DRCorner = {r.xmax, r.ymin};
        int[] scUL = camera.mathToScreen(ULCorner[0], ULCorner[1]);
        int[] scDR = camera.mathToScreen(DRCorner[0], DRCorner[1]);
        g2draw.setColor(Color.LIGHT_GRAY);
        g2draw.setStroke(new BasicStroke(1, CAP_ROUND, JOIN_ROUND));
        g2draw.drawRect(scUL[0], scUL[1], scDR[0] - scUL[0], scDR[1] - scUL[1]);
    }

    /**
     * Returns equivalent position from default camera to fixed camera.
     *
     * @param v
     * @return
     */
    public Vec defaultToFixedCamera(Vec v) {
        double width1 = camera.getMathView().getWidth();
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
