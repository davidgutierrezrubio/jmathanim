package com.jmathanim.Utils;

import com.jmathanim.Enum.StrokeLineCap;
import com.jmathanim.Enum.StrokeLineJoin;
import com.jmathanim.MathObjects.Point;
import com.jmathanim.MathObjects.Shape;
import com.jmathanim.MathObjects.Shapes.JMPath;
import com.jmathanim.MathObjects.Shapes.JMPathPoint;
import com.jmathanim.MathObjects.Shapes.MultiShapeObject;
import com.jmathanim.Styling.*;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.jmathanim.LogUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.jmathanim.Utils.SVGUtils.*;
import static com.jmathanim.jmathanim.JMathAnimScene.DEGREES;
import static com.jmathanim.jmathanim.JMathAnimScene.logger;

public class SVGImport {
    private final HashMap<String, Object> defsObjects;
    private final JMathAnimScene scene;
    URL svgURL;
    private double height;
    private double width;
    private double currentX;
    private double currentY;
    private double closeX;
    private double closeY;
    private double previousX;
    private double previousY;
    private AffineJTransform currentTransform;

    public SVGImport(JMathAnimScene scene) {
        this.scene = scene;
        defsObjects = new HashMap<>();
        currentX = 0;
        currentY = 0;
        closeX = 0;
        closeY = 0;
        previousX = 0;
        previousY = 0;
        currentTransform = new AffineJTransform();
        width = 0;
        height = 0;
    }


    /**
     * Imports a SVG object and converts it into a MultiShapeObject
     *
     * @param fileName Filename of the SVG file, with modifier tags
     * @return The MultiShapeObject created
     * @throws Exception
     */
    public  MultiShapeObject importSVG(String fileName) {
        ResourceLoader rl = new ResourceLoader();

        try {
            URL url = rl.getExternalResource(fileName, "images");
            logger.debug("Importing SVG file " + LogUtils.fileName(url.toString()));
            return importSVG(url, MODrawProperties.makeNullValues());
        } catch (Exception e) {
            logger.error("An exception ocurred loading SVG file " + fileName + ". Returning empty MultiShapeObject instead");
            logger.error(e.getMessage());
            return MultiShapeObject.make();
        }
    }

    public  MultiShapeObject importSVG(InputStream is) {
        try {
            return importSVG(is, MODrawProperties.makeNullValues());
        } catch (Exception e) {
            logger.error("An exception ocurred loading SVG file from inputStream. Returning empty MultiShapeObject instead");
            logger.error(e.getMessage());
            return MultiShapeObject.make();
        }
    }

    public  MultiShapeObject importSVG(URL urlSvg) {
        try {
            return importSVG(urlSvg, MODrawProperties.makeNullValues());
        } catch (Exception e) {
            logger.error("An exception ocurred loading SVG file from url " + urlSvg.getPath() + ". Returning empty MultiShapeObject instead");
            logger.error(e.getMessage());
            return MultiShapeObject.make();
        }
    }

    public  MultiShapeObject importSVG(URL urlSvg, MODrawProperties base) throws Exception {
        JMathAnimScene.logger.debug("Importing SVG file " + LogUtils.fileName(urlSvg.toString()));
        return importSVG(urlSvg.openStream(), base);
    }

    public  MultiShapeObject importSVG(InputStream is, MODrawProperties base) throws Exception {
        MultiShapeObject msh = MultiShapeObject.make();

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        // Disabling these features will speed up the load of the svg
        dbFactory.setFeature("http://xml.org/sax/features/namespaces", false);
        dbFactory.setFeature("http://xml.org/sax/features/validation", false);
        dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(is);

        // Look for svg elements in the root document
        currentTransform = new AffineJTransform();
        Element root = doc.getDocumentElement();
        processSVGAttributes(root);
        MODrawProperties mpCopy = base.copy();
        processStyleAttributeCommands(root, mpCopy);
        processChildNodes(root, mpCopy, currentTransform, msh);
        msh.getMp().setAbsoluteThickness(false);//Ensure this
        return msh;
    }

    public  MultiShapeObject importSVGFromDOM(Element root) {
        currentTransform = new AffineJTransform();
        MultiShapeObject msh = MultiShapeObject.make();
        processChildNodes(root, msh.getMp().getFirstMP(), currentTransform, msh);
        return msh;
    }
    private void processSVGAttributes(Element el) {
        if (!el.getAttribute("width").isEmpty()) {
            width = Double.parseDouble(SVGUtils.maintainNumbersOnlyFromString(el.getAttribute("width")));
        }
        if (!el.getAttribute("height").isEmpty()) {
            height = Double.parseDouble(SVGUtils.maintainNumbersOnlyFromString(el.getAttribute("height")));
        }
        if (!el.getAttribute("viewBox").isEmpty()) {
            //format: viewBox="0 0 900 625.73422"
            ArrayList<String> tokens = SVGUtils.getPointTokens(el.getAttribute("viewBox"));
            width = Double.parseDouble(tokens.get(2));
            height = Double.parseDouble(tokens.get(3));
        }
    }


    private  void processStyleAttributeCommands(Element gradientElement, MODrawProperties ShMp) {
        if (!gradientElement.getAttribute("style").isEmpty()) {
            parseStyleAttribute(gradientElement.getAttribute("style"), ShMp);
        }
        if (!gradientElement.getAttribute("stroke").isEmpty()) {
            String stroke = gradientElement.getAttribute("stroke");
            PaintStyle<?> strokeColor = processPaintStyleTag(stroke);
            ShMp.setDrawColor(strokeColor);
            if (stroke.equals("none"))   ShMp.setThickness(0d);
        }

        if (!gradientElement.getAttribute("stroke-width").isEmpty()) {
            double th = Double.parseDouble(gradientElement.getAttribute("stroke-width"));
            ShMp.setThickness(computeThicknessFromSVGThickness(th));
        }

        if (!gradientElement.getAttribute("fill").isEmpty()) {
            PaintStyle<?> fillColor = processPaintStyleTag(gradientElement.getAttribute("fill"));
            ShMp.setFillColor(fillColor);
        }

        String linecap = gradientElement.getAttribute("stroke-linecap");
        if (!linecap.isEmpty()) {
            switch (linecap) {
                case "round":
                    ShMp.setLinecap(StrokeLineCap.ROUND);
                    break;
                case "butt":
                    ShMp.setLinecap(StrokeLineCap.BUTT);
                    break;
                case "square":
                    ShMp.setLinecap(StrokeLineCap.SQUARE);
                    break;
                default:
                    logger.warn("stroke-linecap parameter "+LogUtils.method(linecap)+" not recognized. Ignoring.");
            }
        }
        String linejoin = gradientElement.getAttribute("stroke-linejoin");
        if (!linejoin.isEmpty()) {
            switch (linejoin) {
                case "miter":
                    ShMp.setLineJoin(StrokeLineJoin.MITER);
                    break;
                case "bevel":
                    ShMp.setLineJoin(StrokeLineJoin.BEVEL);
                    break;
                case "round":
                    ShMp.setLineJoin(StrokeLineJoin.ROUND);
                    break;
                default:
                    logger.warn("stroke-linejoin parameter "+LogUtils.method(linejoin)+" not recognized. Ignoring.");
            }
        }
    }

    private double computeThicknessFromSVGThickness(double th) {
        if ((width == 0) || (height == 0)) {
            //Default values if no width/height are defined in SVG file
            width = 300;
            height = 150;
        }
//        double porc= th/width;//% de ancho pantalla
//        System.out.println("SVG Import: svgTh:"+th+" thickness:"+th / width*5000);
        return th / width*5000;

    }

    private  PaintStyle<?> processPaintStyleTag(String referenceString) {
        if (referenceString == null || referenceString.trim().isEmpty()) {
            logger.warn("Reference string null or empty, returning default color instead");
            return JMColor.rgba(0, 0, 0, 1);
        }

        String trimmed = referenceString.trim();
        final String PREFIX = "url(#";
        final String SUFFIX = ")";

        // 1. Verificar si la cadena comienza y termina con el formato esperado
        if (trimmed.startsWith(PREFIX) && trimmed.endsWith(SUFFIX)) {
            // 2. Extraer el contenido entre el prefijo y el sufijo
            // trimmed.substring(5, trimmed.length() - 1)

            // La longitud de "url(#" es 5.
            int startIndex = PREFIX.length();

            // Restamos 1 para excluir el ')' final
            int endIndex = trimmed.length() - SUFFIX.length();

            // 3. Devolver el ID extraído.
            // También verificamos que no sea una cadena vacía después de la extracción, aunque es improbable
            // en el contexto SVG.
            if (endIndex > startIndex) {
                //A name found
                String name=trimmed.substring(startIndex, endIndex);
                if (defsObjects.containsKey(name)) {
                    return (PaintStyle<?>) defsObjects.get(name);
                }else
                {
                    logger.warn("Style "+LogUtils.method(name)+"is not defined, returning default color instead");
                    return JMColor.rgba(0, 0, 0, 1);
                }
            }
        }
//            if (referenceString.equals("none")) {
//                return JMColor.rgba(0, 0, 0, 0);
//            }
        return JMColor.parse(referenceString);


//        logger.warn("Style string "+LogUtils.method(trimmed)+" is not valid, returning default color instead");
//            return JMColor.rgba(0, 0, 0, 1);
    }

    private  void processChildNodes(Element gNode, MODrawProperties localMP, AffineJTransform transform, MultiShapeObject msh) throws NumberFormatException {
        Shape shape;
        NodeList nList = gNode.getChildNodes();
        // localMP holds the base MODrawProperties to apply to all childs
        MODrawProperties mpCopy;
        for (int nchild = 0; nchild < nList.getLength(); nchild++) {
            Node node = nList.item(nchild);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) node;
                mpCopy = localMP.copy();
                processStyleAttributeCommands(el, mpCopy);

                AffineJTransform transfCopy = transform.copy();
                processTransformAttributeCommands(el, transfCopy);
                switch (el.getTagName()) {
                    case "g":
                        processChildNodes(el, mpCopy, transfCopy, msh);
                        break;
                    case "path":
                        try {
                            JMPath path = processPathCommands(el.getAttribute("d"));
                            path.distille();
                            PathUtils pathUtils = new PathUtils();
                            pathUtils.determineStraightSegments(path);
                            if (!path.getJmPathPoints().isEmpty()) {
                                shape = new Shape(path);
                                shape.getMp().copyFrom(mpCopy);
                                transfCopy.applyTransform(shape);
                                msh.add(shape);
                            }
                        } catch (Exception ex) {
                            logger.error("Error processing SVG path " + el.getAttribute("d"));
                        }
                        break;
                    case "polygon":
                        try {
                            Shape pol = processPolygonPoints(el.getAttribute("points"), true);
                            if (!pol.isEmpty()) {
                                transfCopy.applyTransform(pol);
                                pol.getMp().copyFrom(mpCopy);
                                msh.add(pol);
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(SVGUtils.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;
                    case "polyline":
                        try {
                            Shape pol = processPolygonPoints(el.getAttribute("points"), false);
                            pol.getPath().openPath();
                            if (!pol.isEmpty()) {
                                transfCopy.applyTransform(pol);
                                pol.getMp().copyFrom(mpCopy);
                                msh.add(pol);
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(SVGUtils.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;
                    case "rect":
                        double x = Double.parseDouble(el.getAttribute("x"));
                        double y = -Double.parseDouble(el.getAttribute("y"));
                        double w = Double.parseDouble(el.getAttribute("width"));
                        double h = -Double.parseDouble(el.getAttribute("height"));
                        shape = Shape.rectangle(Vec.to(x, y), Vec.to(x + w, y + h)).setMp(mpCopy);
                        transfCopy.applyTransform(shape);
                        msh.add(shape);
                        break;
                    case "circle":
                        double cx = Double.parseDouble(el.getAttribute("cx"));
                        double cy = -Double.parseDouble(el.getAttribute("cy"));
                        double radius = Double.parseDouble(el.getAttribute("r"));
                        shape = Shape.circle().scale(radius).shift(cx, cy).setMp(mpCopy);
                        transfCopy.applyTransform(shape);
                        msh.add(shape);
                        break;
                    case "ellipse":
                        double cxe = Double.parseDouble(el.getAttribute("cx"));
                        double cye = -Double.parseDouble(el.getAttribute("cy"));
                        double rxe = Double.parseDouble(el.getAttribute("rx"));
                        double rye = -Double.parseDouble(el.getAttribute("ry"));
                        shape = Shape.circle().scale(rxe, rye).shift(cxe, cye).setMp(mpCopy);
                        transfCopy.applyTransform(shape);
                        msh.add(shape);
                        break;
                    case "defs":
                        processDefs(el);
                        break;
                    case "metadata":
                        break;
                    default:
                        JMathAnimScene.logger.warn("Unknow command: <" + el.getTagName() + ">");
                }
            }
        }
    }
    private  Shape processPolygonPoints(String s, boolean polygon) {
        ArrayList<Vec> points = new ArrayList<>();
        ArrayList<String> tokens = getPointTokens(s);
        Shape resul;
        Iterator<String> it = tokens.iterator();
        while (it.hasNext()) {
            getPoint(it.next(), it.next());
            points.add(Vec.to(currentX, currentY));
        }
        if (polygon) {
            resul = Shape.polygon(points.toArray(new Vec[0]));
        } else {
            resul = Shape.polyLine(points.toArray(new Vec[0]));
        }
        return resul;
    }

    private  void processDefs(Element defNode) {
        NodeList nList = defNode.getChildNodes();
        // localMP holds the base MODrawProperties to apply to all childs
        MODrawProperties mpCopy;
        int length = nList.getLength();
        for (int nchild = 0; nchild < length; nchild++) {
            Node node = nList.item(nchild);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) node;
                switch (el.getTagName()) {
                    case "linearGradient":
                        processLinearGradient(el);
                        break;
                    case "radialGradient":
                        processRadialGradient(el);
                        break;
                }
            }
        }
    }

    private  void processLinearGradient(Element el) {
        Vec start = Vec.to(parseStringValueWithPercentageNumber(el.getAttribute("x1")), 1-parseStringValueWithPercentageNumber(el.getAttribute("y1")));
        Vec end= Vec.to(parseStringValueWithPercentageNumber(el.getAttribute("x2")), 1-parseStringValueWithPercentageNumber(el.getAttribute("y2")));
        JMLinearGradient lg=JMLinearGradient.make(start,end);
        lg.setCycleMethod(getSvgSpreadMethod(el.getAttribute("spreadMethod")));
        lg.setRelativeToShape(el.getAttribute("gradientUnits").equals("objectBoundingBox"));
        processGradientStops(lg.getStops(),el);
        logger.debug("Storing "+LogUtils.method(el.getAttribute("id"))+" with object "+LogUtils.method(lg.getClass().getSimpleName()));
        defsObjects.put(el.getAttribute("id"), lg);
    }
    private  void processRadialGradient(Element el) {
        Vec center = Vec.to(parseStringValueWithPercentageNumber(el.getAttribute("cx")), 1-parseStringValueWithPercentageNumber(el.getAttribute("cy")));
        double radius=parseStringValueWithPercentageNumber(el.getAttribute("r"));
        JMRadialGradient rg = JMRadialGradient.make(center, radius);
        String fx = el.getAttribute("fx");
        if (!fx.isEmpty()) {
            double[] focus = convertSvgFocusToJfxFocus(center.x, center.y, radius, parseStringValueWithPercentageNumber(fx), 1 - parseStringValueWithPercentageNumber(el.getAttribute("fy")));
            rg.setFocusAngle(focus[0]);
            rg.setFocusDistance(focus[1]);
        }
        rg.setCycleMethod(getSvgSpreadMethod(el.getAttribute("spreadMethod")));
        rg.setRelativeToShape(el.getAttribute("gradientUnits").equals("objectBoundingBox"));
        processGradientStops(rg.getStops(),el);
        logger.debug("Storing "+LogUtils.method(el.getAttribute("id"))+" with object "+LogUtils.method(rg.getClass().getSimpleName()));

        defsObjects.put(el.getAttribute("id"), rg);
    }



    private  void processTransformAttributeCommands(Element gradientElement, AffineJTransform currentTransform) {
        if (!"".equals(gradientElement.getAttribute("transform"))) {
            parseTransformAttribute(gradientElement.getAttribute("transform"), currentTransform);
        }
    }

    private  void parseStyleAttribute(String str, MODrawProperties ShMp) {
        str = str.replaceAll("(?<=[;:])\\s*", "");
        String[] decls = str.split(";");
        for (String pairs : decls) {
            String[] decl = pairs.split(":");
            switch (decl[0]) {
                case "fill":
                    JMColor fillColor = JMColor.parse(decl[1]);
                    ShMp.setFillColor(fillColor);
                    break;
                case "stroke":
                    JMColor strokeColor = JMColor.parse(decl[1]);
                    ShMp.setDrawColor(strokeColor);
                    break;
                case "stroke-width":
                    double th = Double.parseDouble(decl[1]);
                    //Esto no es correcto!
                    double th2 = scene.getRenderer().MathWidthToThickness(th);
                    ShMp.setThickness(computeThicknessFromSVGThickness(th));

            }

        }
    }


    public  AffineJTransform parseTransformAttribute(String trans, AffineJTransform currentTransform) {
        ArrayList<AffineJTransform> transforms = new ArrayList<>();
        //First level: commands+arguments
        String delims = "[()]+";

        String[] tokens = trans.split(delims);
        Iterator<String> it = Arrays.stream(tokens).iterator();
        while (it.hasNext()) {
            String command = it.next().trim();
            String arguments = it.next().trim();
            AffineJTransform tr = parseTransformCommand(command.toUpperCase(), arguments);
            transforms.add(tr);//Add it at position 0 so the array is inverted
        }

        //Now compose all transforms, right to left. As the array is inverted
        //we iterate normally over the array
        AffineJTransform resul = currentTransform.copy();
        for (AffineJTransform tr : transforms) {
            resul = tr.compose(resul);
        }
        currentTransform.copyFrom(resul);
        return resul;
    }

    private  AffineJTransform parseTransformCommand(String command, String arguments) {
        AffineJTransform resul = new AffineJTransform();//An identity transform
        String argDelims = "[ ,]+";
        String[] args = arguments.split(argDelims);
        double a, b, c, d, e, f;
        switch (command) {
            case "MATRIX":
                //matrix(a,b,c,d,e,f)
                //e, f: image of point (0,0)
                //(a,b) image of vector (1,0)
                //(c,d) image of vector (0,1)
                a = Double.parseDouble(args[0]);
                b = Double.parseDouble(args[1]);
                c = Double.parseDouble(args[2]);
                d = Double.parseDouble(args[3]);
                e = Double.parseDouble(args[4]);
                f = Double.parseDouble(args[5]);
                resul.setOriginImg(e, f);
                resul.setV1Img(a, b);
                resul.setV2Img(c, d);
                break;
            case "TRANSLATE":
                //(a,b) traslation vector
                a = Double.parseDouble(args[0]);
                try {
                    b = Double.parseDouble(args[1]);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    b = 0;
                }
                resul.setOriginImg(a, b);
                resul.setV1Img(1, 0);
                resul.setV2Img(0, 1);
                break;
            case "SCALE":
                //(a,b) traslation vector
                a = Double.parseDouble(args[0]);
                try {
                    b = Double.parseDouble(args[1]);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    b = a;
                }
                resul = AffineJTransform.createScaleTransform(Vec.to(0, 0), a, b);
                break;
            case "ROTATE":
                //(a x y) or (a)
                //a=rotation vector in degrees
                a = Double.parseDouble(args[0]);
                try {
                    b = Double.parseDouble(args[1]);
                    c = Double.parseDouble(args[2]);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    b = 0;
                    c = 0;
                }
                resul = AffineJTransform.create2DRotationTransform(Vec.to(b, c), a * DEGREES);
                break;
            case "SKEWX":
                //(a) angle of skewness
                //equivalente to matrix(1,0,tan(a),1,0,0)
                a = 1;
                b = 0;
                c = Math.tan(Double.parseDouble(args[0]) * DEGREES);
                d = 1;
                e = 0;
                f = 0;
                resul.setOriginImg(e, f);
                resul.setV1Img(a, b);
                resul.setV2Img(c, d);
                break;
            case "SKEWY":
                //(a) angle of skewness
                //equivalente to matrix(1,0,tan(a),1,0,0)
                a = 1;
                b = Math.tan(Double.parseDouble(args[0]) * DEGREES);
                c = 0;
                d = 1;
                e = 0;
                f = 0;
                resul.setOriginImg(e, f);
                resul.setV1Img(a, b);
                resul.setV2Img(c, d);
                break;
        }
        AffineJTransform sc1 = AffineJTransform.createScaleTransform(Vec.to(0, 0), 1, -1);
        AffineJTransform sc2 = AffineJTransform.createScaleTransform(Vec.to(0, 0), 1, -1);
        resul = sc1.compose(resul).compose(sc2);
        return resul;
    }


    /**
     * Takes a string of SVG Path commands and converts then into a JMPathObject Only fill attribute is parsed into the
     * path
     *
     * @param s The string of commands
     * @return The JMPathObject
     */
    private  JMPath processPathCommands(String s) throws Exception {
        JMPath resul = new JMPath();
        double qx0,//Quadratic Bezier coefficients
                qy0,
                qx1,
                qy1,
                qx2,
                qy2;
        JMPathPoint previousPoint = new JMPathPoint(Vec.to(0, 0), true);
        String processedCommandsString = sanitizeCommandsString(s);
        ArrayList<String> tokens = getPointTokens(processedCommandsString);

        Iterator<String> it = tokens.iterator();
        double cx1, cx2, cy1, cy2;
        double xx, yy;
        String previousCommand = "";
        currentX = 0;
        currentY = 0;
        previousPoint.getV().x = 0;
        previousPoint.getV().y = 0;
        while (it.hasNext()) {
            // Main loop, looking for commands
            String token = it.next().trim();
            switch (token) {
                case "":
                    break;
                case "A":
                    previousCommand = token;
                    previousPoint = processArcCommand(resul, it);
                    break;
                case "a":
                    previousCommand = token;
                    previousPoint = processArcCommand(resul, it);
                    break;
                case "M":
                    previousCommand = token;
                    getPoint(it.next(), it.next());
                    // First point. Creatline do the same as a the first point
                    previousPoint = pathLineTo(resul, currentX, currentY, false);
                    closeX = currentX;
                    closeY = currentY;
                    previousPoint.setSegmentToThisPointVisible(false);
//                    previousPoint = pathM(path, currentX, currentY);
                    break;
                case "m":
                    previousCommand = token;
                    xx = previousPoint.getV().x;
                    yy = previousPoint.getV().y;
                    getPoint(it.next(), it.next());
                    currentX += xx;
                    currentY += yy;
                    closeX = currentX;
                    closeY = currentY;
                    // First point. Creatline do the same as a the first point
                    previousPoint = pathLineTo(resul, currentX, currentY, false);
                    previousPoint.setSegmentToThisPointVisible(false);
//                    previousPoint = pathM(path, currentX, currentY);
                    break;

                case "L": // Line
                    previousCommand = token;
                    getPoint(it.next(), it.next());
                    previousPoint = pathLineTo(resul, currentX, currentY, true);
                    break;
                case "l": // Line
                    previousCommand = token;
                    xx = previousPoint.getV().x;
                    yy = previousPoint.getV().y;
                    getPoint(it.next(), it.next());
                    currentX += xx;
                    currentY += yy;
                    previousPoint = pathLineTo(resul, currentX, currentY, true);
                    break;

                case "H": // Horizontal line
                    previousCommand = token;

                    getPointX(it.next());
                    previousPoint = pathLineTo(resul, currentX, currentY, true);
                    break;

                case "h": // Horizontal line
                    previousCommand = token;

                    xx = previousPoint.getV().x;
                    getPointX(it.next());
                    currentX += xx;
                    previousPoint = pathLineTo(resul, currentX, currentY, true);
                    break;
                case "V": // Vertical line
                    previousCommand = token;

                    getPointY(it.next());
                    previousPoint = pathLineTo(resul, currentX, currentY, true);
                    break;
                case "v": // Vertical line
                    previousCommand = token;

                    yy = previousPoint.getV().y;
                    getPointY(it.next());
                    currentY += yy;
                    previousPoint = pathLineTo(resul, currentX, currentY, true);
                    break;
                case "Q": // Quadratic Bezier
                    previousCommand = token;

                    qx0 = currentX;
                    qy0 = currentY;
                    qx1 = Double.parseDouble(it.next());
                    qy1 = -Double.parseDouble(it.next());
                    getPoint(it.next(), it.next());
                    qx2 = currentX;
                    qy2 = currentY;

                    previousPoint = pathQuadraticBezier(resul, previousPoint, qx0, qy0, qx1, qy1, qx2, qy2);
                    break;
                case "C": // Cubic Bezier
                    previousCommand = token;

                    cx1 = Double.parseDouble(it.next());
                    cy1 = -Double.parseDouble(it.next());
                    cx2 = Double.parseDouble(it.next());
                    cy2 = -Double.parseDouble(it.next());
                    getPoint(it.next(), it.next());
                    previousPoint = pathCubicBezier(resul, previousPoint, cx1, cy1, cx2, cy2, currentX, currentY);
                    break;
                // c 1,1 2,2 3,3 4,4 5,5 6,6 would become C 1,1 2,2 3,3 C 7,7 8,8 9,9
                case "q": // Quadratic Bezier
                    previousCommand = token;
                    qx0 = previousPoint.getV().x;
                    qy0 = previousPoint.getV().y;
                    qx1 = qx0 + Double.parseDouble(it.next());
                    qy1 = qy0 - Double.parseDouble(it.next());
                    getPoint(it.next(), it.next());
                    currentX += qx0;
                    currentY += qy0;
                    qx2 = currentX;
                    qy2 = currentY;

                    previousPoint = pathQuadraticBezier(resul, previousPoint, qx0, qy0, qx1, qy1, qx2, qy2);
                    break;

                case "c": // Cubic Bezier
                    previousCommand = token;

                    xx = previousPoint.getV().x;
                    yy = previousPoint.getV().y;
                    cx1 = xx + Double.parseDouble(it.next());
                    cy1 = yy - Double.parseDouble(it.next());
                    cx2 = xx + Double.parseDouble(it.next());
                    cy2 = yy - Double.parseDouble(it.next());
                    getPoint(it.next(), it.next());
                    currentX += xx;
                    currentY += yy;

                    previousPoint = pathCubicBezier(resul, previousPoint, cx1, cy1, cx2, cy2, currentX, currentY);
                    break;
                case "S": // Simplified Cubic Bezier. Take first control point as a reflection of previous
                    // one
                    previousCommand = token;

                    cx1 = previousPoint.getV().x - (previousPoint.getVEnter().x - previousPoint.getV().x);
                    cy1 = previousPoint.getV().y - (previousPoint.getVEnter().y - previousPoint.getV().y);
                    cx2 = Double.parseDouble(it.next());
                    cy2 = -Double.parseDouble(it.next());
                    getPoint(it.next(), it.next());
                    previousPoint = pathCubicBezier(resul, previousPoint, cx1, cy1, cx2, cy2, currentX, currentY);
                    break;

                case "s": // Simplified relative Cubic Bezier. Take first control point as a reflection of
                    // previous one
                    previousCommand = token;

                    cx1 = previousPoint.getV().x - (previousPoint.getVEnter().x - previousPoint.getV().x);
                    cy1 = previousPoint.getV().y - (previousPoint.getVEnter().y - previousPoint.getV().y);
                    xx = previousPoint.getV().x;
                    yy = previousPoint.getV().y;
                    cx2 = xx + Double.parseDouble(it.next());
                    cy2 = yy - Double.parseDouble(it.next());
                    getPoint(it.next(), it.next());
                    currentX += xx;
                    currentY += yy;
                    previousPoint = pathCubicBezier(resul, previousPoint, cx1, cy1, cx2, cy2, currentX, currentY);
                    break;
                case "Z":
                    previousCommand = token;
                    previousPoint = pathLineTo(resul, closeX, closeY, true);
                    break;
                case "z":
                    previousCommand = token;
                    previousPoint = pathLineTo(resul, closeX, closeY, true);
                    break;
                default:
                    if (!"".equals(token.substring(0, 1))) // Not a command, but a point!
                    {
                        switch (previousCommand) {
                            case "H":
                                previousCommand = "H";
                                getPointX(token);
                                previousPoint = pathLineTo(resul, currentX, currentY, true);
                                break;
                            case "V":
                                previousCommand = "V";
                                getPointY(token);
                                previousPoint = pathLineTo(resul, currentX, currentY, true);
                                break;
                            case "h":
                                previousCommand = "h";
                                xx = previousPoint.getV().x;
                                getPointX(token);
                                currentX += xx;
                                previousPoint = pathLineTo(resul, currentX, currentY, true);
                                break;
                            case "v":
                                previousCommand = "v";
                                yy = previousPoint.getV().y;
                                getPointY(token);
                                currentY += yy;
                                previousPoint = pathLineTo(resul, currentX, currentY, true);
                                break;
                            case "M":
                                previousCommand = "L";
                                getPointX(token);
                                getPointY(it.next());
                                previousPoint = pathLineTo(resul, currentX, currentY, true);
                                break;
                            case "L":
                                previousCommand = "L";
                                getPointX(token);
                                getPointY(it.next());
                                previousPoint = pathLineTo(resul, currentX, currentY, true);
                                break;
                            case "m":
                                previousCommand = "l";
                                xx = previousPoint.getV().x;
                                yy = previousPoint.getV().y;
                                getPointX(token);
                                getPointY(it.next());
                                currentX += xx;
                                currentY += yy;
                                previousPoint = pathLineTo(resul, currentX, currentY, true);
                                break;
                            case "l":
                                previousCommand = "l";
                                xx = previousPoint.getV().x;
                                yy = previousPoint.getV().y;
                                getPointX(token);
                                getPointY(it.next());
                                currentX += xx;
                                currentY += yy;
                                previousPoint = pathLineTo(resul, currentX, currentY, true);
                                break;
                            case "C":
                                cx1 = Double.parseDouble(token);
                                cy1 = -Double.parseDouble(it.next());
                                cx2 = Double.parseDouble(it.next());
                                cy2 = -Double.parseDouble(it.next());
                                getPoint(it.next(), it.next());
                                previousPoint = pathCubicBezier(resul, previousPoint, cx1, cy1, cx2, cy2, currentX, currentY);
                                break;
                            case "c":
                                xx = previousPoint.getV().x;
                                yy = previousPoint.getV().y;
                                cx1 = xx + Double.parseDouble(token);
                                cy1 = yy - Double.parseDouble(it.next());
                                cx2 = xx + Double.parseDouble(it.next());
                                cy2 = yy - Double.parseDouble(it.next());
                                getPoint(it.next(), it.next());
                                currentX += xx;
                                currentY += yy;
                                previousPoint = pathCubicBezier(resul, previousPoint, cx1, cy1, cx2, cy2, currentX, currentY);
                                break;
                            case "S": // Simplified Cubic Bezier. Take first control point as a reflection of previous
                                // one
                                cx1 = previousPoint.getV().x - (previousPoint.getVEnter().x - previousPoint.getV().x);
                                cy1 = previousPoint.getV().y - (previousPoint.getVEnter().y - previousPoint.getV().y);
                                cx2 = Double.parseDouble(token);
                                cy2 = -Double.parseDouble(it.next());
                                getPoint(it.next(), it.next());
                                previousPoint = pathCubicBezier(resul, previousPoint, cx1, cy1, cx2, cy2, currentX, currentY);
                                break;
                            case "s": // Simplified relative Cubic Bezier. Take first control point as a reflection of
                                // previous one
                                cx1 = previousPoint.getV().x - (previousPoint.getVEnter().x - previousPoint.getV().x);
                                cy1 = previousPoint.getV().y - (previousPoint.getVEnter().y - previousPoint.getV().y);
                                xx = previousPoint.getV().x;
                                yy = previousPoint.getV().y;
                                cx2 = xx + Double.parseDouble(token);
                                cy2 = yy - Double.parseDouble(it.next());
                                getPoint(it.next(), it.next());
                                currentX += xx;
                                currentY += yy;
                                previousPoint = pathCubicBezier(resul, previousPoint, cx1, cy1, cx2, cy2, currentX, currentY);
                                break;
                            default:
                                JMathAnimScene.logger.error("Unknow repeated command: <" + token + ">");

                        }

                    }
            }
        }

        return resul;
    }

    private  JMPathPoint processArcCommand(JMPath resul, Iterator<String> it) {

        double rx = Double.parseDouble(it.next());
        double ry = Double.parseDouble(it.next());
        double rotationAngle = Double.parseDouble(it.next());
        int large = Integer.parseInt(it.next());
        String next = it.next();
        int sweep = Integer.parseInt(next);

        //previousX,previousY; origin point
        //currentX,currentY; destiny point
        getPoint(it.next(), it.next());
        Vec O1 = Vec.to(previousX, -previousY);
        Vec O2 = Vec.to(currentX, -currentY);
        sweep = 1 - sweep;
        Shape arc = computeSVGArc(
                O1,
                rx,
                ry,
                -rotationAngle,
                large,
                sweep,
                O2);
        if (large != sweep) arc.reverse();

        arc.scale(Point.origin(), 1, -1);
        resul.getJmPathPoints().addAll(arc.getPath().getJmPathPoints());
        return arc.get(-1);
    }

    public  void getPoint(String x, String y) throws NumberFormatException {
        getPointX(x);
        getPointY(y);
    }

    private  void getPointX(String x) throws NumberFormatException {
        previousX = currentX;
        currentX = Double.parseDouble(x);
    }

    private  void getPointY(String y) throws NumberFormatException {
        previousY = currentY;
        currentY = -Double.parseDouble(y);
    }


    /**
     * Creates a cubic Bezier path segment and adds it to the provided JMPath. This method sets the control points for
     * the cubic Bézier curve and adds the new point as a curved vertex to the path.
     *
     * @param path          The JMPath to which the cubic Bézier segment will be added.
     * @param previousPoint The previous point in the path, used to define the exit control point.
     * @param cx1           The x-coordinate of the first control point for the cubic Bézier curve.
     * @param cy1           The y-coordinate of the first control point for the cubic Bézier curve.
     * @param cx2           The x-coordinate of the second control point for the cubic Bézier curve.
     * @param cy2           The y-coordinate of the second control point for the cubic Bézier curve.
     * @param x             The x-coordinate of the ending point of the cubic Bézier segment.
     * @param y             The y-coordinate of the ending point of the cubic Bézier segment.
     * @return The last JMPathPoint created for this segment, representing its endpoint.
     */
    private  JMPathPoint pathCubicBezier(JMPath path, JMPathPoint previousPoint, double cx1, double cy1, double cx2,
                                               double cy2, double x, double y) {
        JMPathPoint point = new JMPathPoint(Vec.to(currentX, currentY), true);
        point.setSegmentToThisPointCurved(true);
        previousPoint.getVExit().x = cx1;
        previousPoint.getVExit().y = cy1;
        point.getVEnter().x = cx2;
        point.getVEnter().y = cy2;
        path.addJMPoint(point);
        return point;
    }

    /**
     * Creates a quadratic Bézier path segment and adds it to the provided JMPath. This method calculates intermediate
     * control points needed to approximate the quadratic Bézier curve using a cubic Bézier curve and then delegates the
     * processing to a cubic Bezier method.
     *
     * @param pathResult    The JMPath to which the quadratic Bézier segment will be added.
     * @param previousPoint The previous point in the path, used as a reference for continuity.
     * @param startX        The x-coordinate of the starting point of the quadratic Bézier segment.
     * @param startY        The y-coordinate of the starting point of the quadratic Bézier segment.
     * @param controlX      The x-coordinate of the control point for the quadratic Bézier curve.
     * @param controlY      The y-coordinate of the control point for the quadratic Bézier curve.
     * @param endX          The x-coordinate of the ending point of the quadratic Bézier segment.
     * @param endY          The y-coordinate of the ending point of the quadratic Bézier segment.
     * @return The last JMPathPoint created for this segment, representing its endpoint.
     */
    private  JMPathPoint pathQuadraticBezier(JMPath pathResult, JMPathPoint previousPoint, double startX, double startY, double controlX, double controlY, double endX, double endY) {
        double[] firstControlPoint = calculateControlPoint(startX, startY, controlX, controlY);
        double[] secondControlPoint = calculateControlPoint(endX, endY, controlX, controlY);

        JMPathPoint previous = pathCubicBezier(pathResult, previousPoint,
                firstControlPoint[0], firstControlPoint[1],
                secondControlPoint[0], secondControlPoint[1],
                currentX, currentY);
        return previous;
    }
}


