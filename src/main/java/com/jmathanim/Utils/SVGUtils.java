/*
 * Copyright (C) 2021 David
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
package com.jmathanim.Utils;

import com.jmathanim.MathObjects.Coordinates;
import com.jmathanim.MathObjects.Point;
import com.jmathanim.MathObjects.Shape;
import com.jmathanim.MathObjects.Shapes.JMPath;
import com.jmathanim.MathObjects.Shapes.JMPathPoint;
import com.jmathanim.MathObjects.Shapes.MultiShapeObject;
import com.jmathanim.Styling.JMColor;
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.jmathanim.JMathAnimConfig;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.jmathanim.LogUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.jmathanim.jmathanim.JMathAnimScene.DEGREES;
import static com.jmathanim.jmathanim.JMathAnimScene.logger;

/**
 * A class with useful methods to handle SVG files
 */
public class SVGUtils {

    private static final double CONTROL_POINT_RATIO = 2d / 3;
    private static double height;
    private static JMathAnimScene scene;
    private static double currentX;
    private static double currentY;
    private static double closeX;
    private static double closeY;
    private static double previousX;
    private static double previousY;
    private static AffineJTransform currentTransform;
    private static double width;
    private final static HashMap<String, Object> defsObjects = new HashMap<>();
//
    /// /        this.scene = scene;
//        this.currentX = 0;
//        this.currentY = 0;
//        this.closeX = 0;
//        this.closeY = 0;
//        this.previousX = 0;
//        this.previousY = 0;
//        this.currentTransform = new AffineJTransform();
//        this.width = 0;
//        this.height = 0;
//    }
    private static void resetParameters() {
        currentX = 0;
        currentY = 0;
        closeX = 0;
        closeY = 0;
        previousX = 0;
        previousY = 0;
        currentTransform = new AffineJTransform();
        width = 0;
        height = 0;
        scene = JMathAnimConfig.getConfig().getScene();
        defsObjects.clear();
    }


    private static String sanitizeTokens(String tok) {
        StringBuilder st = new StringBuilder(tok);
        String tok2 = tok;
        int index = st.indexOf(".");
        if (index > -1) {
            if (st.indexOf(".", index + 1) > -1) {// If there is a second point

                st.setCharAt(index, '|');// Replace first decimal point by '|'

                tok2 = st.toString();
                tok2 = tok2.replace(".", " .");
                tok2 = tok2.replace("- .", "-.");
                tok2 = tok2.replace("|", ".");
            }
        }
        return tok2;
    }

    private static String sanitizeCommandsString(String input) {
        return input.replace("-", " -") // Avoid errors with strings like "142.11998-.948884"
                .replace("e -", "e-") // Avoid errors with numbers in scientific format
                .replace("E -", "E-") // Avoid errors with numbers in scientific format
                .replaceAll("([MmHhVvCcSsLlZzQqAa])", " $1 ") // Add spaces before and after all SVG commands
                .replaceAll(",", " ") // Replace all commas with spaces
                .replaceAll("^ +| +$|( )+", "$1")
                .replaceAll("(\\d*\\.\\d+)(?=(\\.\\d+))", "$1 ");
    }

    private static String sanitizeString(String input) {
        // Avoid errors with strings like "142.11998-.948884"
        // Avoid errors with numbers in scientific format
        // Avoid errors with numbers in scientific format
        // Add spaces before and after all SVG commands
        // Replace all commas with spaces
        // Remove duplicate spaces
        return input.replace("-", " -") // Avoid errors with strings like "142.11998-.948884"
                .replace("e -", "e-") // Avoid errors with numbers in scientific format
                .replace("E -", "E-") // Avoid errors with numbers in scientific format
                .replaceAll("([MmHhVvCcSsLlZzQqAa])", " $1 ") // Add spaces before and after all SVG commands
                .replaceAll(",", " ") // Replace all commas with spaces
                .replaceAll("^ +| +$|( )+", "$1");
    }

    /**
     * Imports a SVG object and converts it into a MultiShapeObject
     *
     * @param fileName Filename of the SVG file, with modifier tags
     * @return The MultiShapeObject created
     * @throws Exception
     */
    public static MultiShapeObject importSVG(String fileName) {
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

    public static MultiShapeObject importSVG(InputStream is) {
        try {
            return importSVG(is, MODrawProperties.makeNullValues());
        } catch (Exception e) {
            logger.error("An exception ocurred loading SVG file from inputStream. Returning empty MultiShapeObject instead");
            logger.error(e.getMessage());
            return MultiShapeObject.make();
        }
    }

    public static MultiShapeObject importSVG(URL urlSvg) {
        try {
            return importSVG(urlSvg, MODrawProperties.makeNullValues());
        } catch (Exception e) {
            logger.error("An exception ocurred loading SVG file from url " + urlSvg.getPath() + ". Returning empty MultiShapeObject instead");
            logger.error(e.getMessage());
            return MultiShapeObject.make();
        }
    }

    public static MultiShapeObject importSVG(URL urlSvg, MODrawProperties base) throws Exception {
        JMathAnimScene.logger.debug("Importing SVG file " + LogUtils.fileName(urlSvg.toString()));
        return importSVG(urlSvg.openStream(), base);
    }

    public static MultiShapeObject importSVG(InputStream is, MODrawProperties base) throws Exception {
        resetParameters();
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

    public static MultiShapeObject importSVGFromDOM(Element root) {
        currentTransform = new AffineJTransform();
        MultiShapeObject msh = MultiShapeObject.make();
        processChildNodes(root, msh.getMp().getFirstMP(), currentTransform, msh);
        return msh;
    }

    private static void processSVGAttributes(Element el) {
        if (!el.getAttribute("width").isEmpty()) {
            width = Double.parseDouble(extractNumbers(el.getAttribute("width")));
        }
        if (!el.getAttribute("height").isEmpty()) {
            height = Double.parseDouble(extractNumbers(el.getAttribute("height")));
        }
        if (!el.getAttribute("viewBox").isEmpty()) {
            //format: viewBox="0 0 900 625.73422"
            ArrayList<String> tokens = getPointTokens(el.getAttribute("viewBox"));
            width = Double.parseDouble(tokens.get(2));
            height = Double.parseDouble(tokens.get(3));
        }
    }

    private static void processChildNodes(Element gNode, MODrawProperties localMP, AffineJTransform transform, MultiShapeObject msh) throws NumberFormatException {
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

    private static void processDefs(Element defNode) {
        NodeList nList = defNode.getChildNodes();
        // localMP holds the base MODrawProperties to apply to all childs
        MODrawProperties mpCopy;
        int length = nList.getLength();
        for (int nchild = 0; nchild < length; nchild++) {
            Node node = nList.item(nchild);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) node;
                System.out.println(el.getTagName());

            }
        }
    }


    private static Shape processPolygonPoints(String s, boolean polygon) {
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

    /**
     * Takes a string of SVG Path commands and converts then into a JMPathObject Only fill attribute is parsed into the
     * path
     *
     * @param s The string of commands
     * @return The JMPathObject
     */
    private static JMPath processPathCommands(String s) throws Exception {
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

    private static JMPathPoint processArcCommand(JMPath resul, Iterator<String> it) {

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

    private static ArrayList<String> getPointTokens(String s) {
        String t = sanitizeString(s);
        String[] tokens_1 = t.split(" ");
        ArrayList<String> tokens = new ArrayList<>();
        for (String tok : tokens_1) {
            String tok2 = sanitizeTokens(tok);
            tokens.addAll(Arrays.asList(tok2.split(" ")));
        }
        return tokens;
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
    private static JMPathPoint pathQuadraticBezier(JMPath pathResult, JMPathPoint previousPoint, double startX, double startY, double controlX, double controlY, double endX, double endY) {
        double[] firstControlPoint = calculateControlPoint(startX, startY, controlX, controlY);
        double[] secondControlPoint = calculateControlPoint(endX, endY, controlX, controlY);

        JMPathPoint previous = pathCubicBezier(pathResult, previousPoint,
                firstControlPoint[0], firstControlPoint[1],
                secondControlPoint[0], secondControlPoint[1],
                currentX, currentY);
        return previous;
    }

    private static double[] calculateControlPoint(double pointX, double pointY, double controlX, double controlY) {
        double derivedX = pointX + CONTROL_POINT_RATIO * (controlX - pointX);
        double derivedY = pointY + CONTROL_POINT_RATIO * (controlY - pointY);
        return new double[]{derivedX, derivedY};
    }

    private static void getPoint(String x, String y) throws NumberFormatException {
        getPointX(x);
        getPointY(y);
    }

    private static void getPointX(String x) throws NumberFormatException {
        previousX = currentX;
        currentX = Double.parseDouble(x);
    }

    private static void getPointY(String y) throws NumberFormatException {
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
    private static JMPathPoint pathCubicBezier(JMPath path, JMPathPoint previousPoint, double cx1, double cy1, double cx2,
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

    // Adds a simple point to the path, with control points equal to the point
    private static JMPathPoint pathLineTo(JMPath path, double currentX, double currentY, boolean isVisible) {
        JMPathPoint point = new JMPathPoint(Vec.to(currentX, currentY), isVisible);
        point.setSegmentToThisPointCurved(false);
        point.getVExit().x = currentX;
        point.getVExit().y = currentY;
        point.getVEnter().x = currentX;
        point.getVEnter().y = currentY;
        path.addJMPoint(point);
        return point;
    }

    private static void processStyleAttributeCommands(Element el, MODrawProperties ShMp) {
        if (!"".equals(el.getAttribute("style"))) {
            parseStyleAttribute(el.getAttribute("style"), ShMp);
        }
        if (!"".equals(el.getAttribute("stroke"))) {
            JMColor strokeColor = JMColor.parse(el.getAttribute("stroke"));
            ShMp.setDrawColor(strokeColor);
        }

        if (!"".equals(el.getAttribute("stroke-width"))) {
            double th = Double.parseDouble(el.getAttribute("stroke-width"));
//            double th2 = scene.getRenderer().MathWidthToThickness(th);
            ShMp.setThickness(computeWidth(th));
        }

        if (!"".equals(el.getAttribute("fill"))) {
            JMColor fillColor = JMColor.parse(el.getAttribute("fill"));
            ShMp.setFillColor(fillColor);
        }

    }

    private static double computeWidth(double th) {
        if ((width == 0) || (height == 0)) {
            //Default values if no width/height are defined in SVG file
            width = 300;
            height = 150;
        }
//        double porc= th/width;//% de ancho pantalla
        return th * scene.getFixedCamera().getMathView().getWidth() / width;

    }

    private static void processTransformAttributeCommands(Element el, AffineJTransform currentTransform) {
        if (!"".equals(el.getAttribute("transform"))) {
            parseTransformAttribute(el.getAttribute("transform"), currentTransform);
        }
    }

    private static void parseStyleAttribute(String str, MODrawProperties ShMp) {
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
                    ShMp.setThickness(computeWidth(th));

            }

        }
    }

    private static AffineJTransform parseTransformAttribute(String trans, AffineJTransform currentTransform) {
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

    private static AffineJTransform parseTransformCommand(String command, String arguments) {
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
     * Writes an XML DOM object to an XML File
     *
     * @param rootElement Root element
     * @param fileName    File name
     * @throws Exception
     */
    private static void writeElementToXMLFile(Element rootElement, String fileName) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document document = docBuilder.newDocument();

        // Importa el elemento al nuevo documento
        Node importedNode = document.importNode(rootElement, true);
        document.appendChild(importedNode);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        // Opcional: Establecer opciones de formato
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        DOMSource source = new DOMSource(document);

        StreamResult result = new StreamResult(fileName);

        transformer.transform(source, result);
    }

    /**
     * Compute SVG Arc
     *
     * @param originPoint  Origin Point
     * @param rx           Radius-X
     * @param ry           Radius-Y
     * @param axisRotation Rotation axis
     * @param large        Large flag (0,1)
     * @param sweep        Sweep flag (0,1)
     * @param destinyPoint Destiny Point
     * @return The created curve
     */
    private static Shape computeSVGArc(Coordinates<?> originPoint, double rx, double ry, double axisRotation, int large, int sweep, Coordinates<?> destinyPoint) {
        Vec O1 = (large == sweep ? originPoint.getVec().copy() : destinyPoint.getVec().copy());
        Vec O2 = (large == sweep ? destinyPoint.getVec().copy() : originPoint.getVec().copy());
        AffineJTransform tr = AffineJTransform.create2DRotationTransform(O1, axisRotation);

        double rad;
        if (rx < ry) {
            tr = tr.compose(AffineJTransform.createScaleTransform(O1, ry / rx, 1, 1));
            rad = ry;
        } else {
            tr = tr.compose(AffineJTransform.createScaleTransform(O1, 1, rx / ry, 1));
            rad = rx;
        }
        O2.applyAffineTransform(tr);
        //If radius is too small, upscale it
        double halfDistanceO1O2 = O2.minus(O1).norm() * .5;
        if (rad < halfDistanceO1O2) {
            rad = halfDistanceO1O2;
        }
        Shape resul = Shape.arc(O1, O2, rad, (large == 0));
        resul.applyAffineTransform(tr.getInverse());
        return resul;
    }

    private static String extractNumbers(String input) {
        return input.replaceAll("[^0-9.]", "");
    }
}
