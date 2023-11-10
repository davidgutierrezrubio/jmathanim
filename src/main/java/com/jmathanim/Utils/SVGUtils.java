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

import com.jmathanim.Styling.JMColor;
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.jmathanim.JMathAnimScene;
import static com.jmathanim.jmathanim.JMathAnimScene.DEGREES;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.MultiShapeObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.SVGMathObject;
import com.jmathanim.mathobjects.Shape;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A class with useful methods to handle SVG files
 */
public class SVGUtils {

    private double currentX;
    private double currentY;
    private double closeX;
    private double closeY;
    private double previousX;
    private double previousY;
    private AffineJTransform currentTransform;
    private final JMathAnimScene scene;

//      public static SVGMathObject make(String fname) {
//        SVGMathObject resul = new SVGMathObject();
//        //This is the default style for SVG objects
//        resul.fillColor("black");
//        resul.drawColor("black");
//        double defaultThickness = resul.scene.getRenderer().MathWidthToThickness(1);//Default thickness
//        resul.getMp().setThickness(defaultThickness);
//
//        ResourceLoader rl = new ResourceLoader();
//        URL urlImage = rl.getResource(fname, "images");
//        try {
//            SVGUtils svgu = new SVGUtils(resul.scene);
//            svgu.importSVG(urlImage, resul);
//        } catch (Exception ex) {
//            JMathAnimScene.logger.warn("File "+urlImage+ " not found. Empty SVGMathObject generated");
//        }
//        resul.getMp().setAbsoluteThickness(false);// Default behaviour
//
//        resul.stackTo(new Point(0, 0), Anchor.Type.UL);
//        return resul;
//    }
//    
    public SVGUtils(JMathAnimScene scene) {
        this.scene = scene;
        this.currentX = 0;
        this.currentY = 0;
        this.closeX = 0;
        this.closeY = 0;
        this.previousX = 0;
        this.previousY = 0;
        this.currentTransform = new AffineJTransform();
    }

    public void importSVG(URL urlSvg, MultiShapeObject msh) throws Exception {
        JMathAnimScene.logger.debug("Importing SVG file {}", urlSvg.toString());
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        // Disabling these features will speed up the load of the svg
        dbFactory.setFeature("http://xml.org/sax/features/namespaces", false);
        dbFactory.setFeature("http://xml.org/sax/features/validation", false);
        dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        org.w3c.dom.Document doc = dBuilder.parse(urlSvg.openStream());

        // Look for svg elements in the root document
        currentTransform = new AffineJTransform();
        Element root = doc.getDocumentElement();
         MODrawProperties mpCopy=MODrawProperties.makeNullValues();
        processStyleAttributeCommands(root, mpCopy);
        processChildNodes(root, mpCopy, currentTransform, msh);
    }

    public void importSVGFromDOM(Element root, MultiShapeObject msh) {
        currentTransform = new AffineJTransform();
        processChildNodes(root, msh.getMp().getFirstMP(), currentTransform, msh);
    }

    private void processChildNodes(Element gNode, MODrawProperties localMP, AffineJTransform transform, MultiShapeObject msh) throws NumberFormatException {
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
                        if (!path.jmPathPoints.isEmpty()) {
                            path.pathType = JMPath.SVG_PATH; // Mark this as a SVG path
                            shape = new Shape(path);
                            shape.getMp().copyFrom(mpCopy);
                            transfCopy.applyTransform(shape);
                            msh.add(shape);
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(SVGMathObject.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                    case "polygon":
                        try {
                        Shape pol = processPolygonPoints(el.getAttribute("points"), true);
                        if (pol.size() > 0) {
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
                        if (pol.size() > 0) {
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
                        shape = Shape.rectangle(new Point(x, y), new Point(x + w, y + h)).setMp(mpCopy);
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
                        //Nothing to do here yet...
                        break;
                    default:
                        JMathAnimScene.logger.error("Unknow command: <" + el.getTagName() + ">");
                }
            }
        }
    }

    private Shape processPolygonPoints(String s, boolean polygon) throws Exception {
        ArrayList<Point> points = new ArrayList<>();
        ArrayList<String> tokens = getPointTokens(s);
        Shape resul;
        Iterator<String> it = tokens.iterator();
        while (it.hasNext()) {
            getPoint(it.next(), it.next());
            points.add(Point.at(currentX, currentY));
        }
        if (polygon) {
            resul = Shape.polygon(points.toArray(new Point[0]));
        } else {
            resul = Shape.polyLine(points.toArray(new Point[0]));
        }
        return resul;
    }

    /**
     * Takes a string of SVG Path commands and converts then into a JMPathObject
     * Only fill attribute is parsed into the path
     *
     * @param s The string of commands
     * @return The JMPathObject
     */
    private JMPath processPathCommands(String s) throws Exception {
        JMPath resul = new JMPath();
        double qx0,//Quadratic Bezier coefficients
                qy0,
                qx1,
                qy1,
                qx2,
                qy2;
        JMPathPoint previousPoint = new JMPathPoint(new Point(0, 0), true, JMPathPoint.JMPathPointType.VERTEX);
        ArrayList<String> tokens = getPointTokens(s);

        Iterator<String> it = tokens.iterator();
        double cx1, cx2, cy1, cy2;
        double xx, yy;
        String previousCommand = "";
        currentX = 0;
        currentY = 0;
        previousPoint.p.v.x = 0;
        previousPoint.p.v.y = 0;
        while (it.hasNext()) {
            // Main loop, looking for commands
            String token = it.next().trim();
            switch (token) {
                case "":
                    break;
                case "A":
                    throw new Exception("Arc command A still not implemented. Sorry.");
                case "a":
                    throw new Exception("Arc command a still not implemented. Sorry.");
//                case "Q":
//                    throw new Exception("Quadratic Bezier command Q still not implemented. Sorry.");
//                case "q":
//                    throw new Exception("Quadratic Bezier command q still not implemented. Sorry.");
                case "M":
                    previousCommand = token;
                    getPoint(it.next(), it.next());
                    // First point. Creatline do the same as a the first point
                    previousPoint = pathLineTo(resul, currentX, currentY, false);
                    closeX = currentX;
                    closeY = currentY;
                    previousPoint.isThisSegmentVisible = false;
//                    previousPoint = pathM(path, currentX, currentY);
                    break;
                case "m":
                    previousCommand = token;
                    xx = previousPoint.p.v.x;
                    yy = previousPoint.p.v.y;
                    getPoint(it.next(), it.next());
                    currentX += xx;
                    currentY += yy;
                    closeX = currentX;
                    closeY = currentY;
                    // First point. Creatline do the same as a the first point
                    previousPoint = pathLineTo(resul, currentX, currentY, false);
                    previousPoint.isThisSegmentVisible = false;
//                    previousPoint = pathM(path, currentX, currentY);
                    break;

                case "L": // Line
                    previousCommand = token;
                    getPoint(it.next(), it.next());
                    previousPoint = pathLineTo(resul, currentX, currentY, true);
                    break;
                case "l": // Line
                    previousCommand = token;
                    xx = previousPoint.p.v.x;
                    yy = previousPoint.p.v.y;
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

                    xx = previousPoint.p.v.x;
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

                    yy = previousPoint.p.v.y;
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
                    qx0 = previousPoint.p.v.x;
                    qy0 = previousPoint.p.v.y;
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

                    xx = previousPoint.p.v.x;
                    yy = previousPoint.p.v.y;
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

                    cx1 = previousPoint.p.v.x - (previousPoint.cpEnter.v.x - previousPoint.p.v.x);
                    cy1 = previousPoint.p.v.y - (previousPoint.cpEnter.v.y - previousPoint.p.v.y);
                    cx2 = Double.parseDouble(it.next());
                    cy2 = -Double.parseDouble(it.next());
                    getPoint(it.next(), it.next());
                    previousPoint = pathCubicBezier(resul, previousPoint, cx1, cy1, cx2, cy2, currentX, currentY);
                    break;

                case "s": // Simplified relative Cubic Bezier. Take first control point as a reflection of
                    // previous one
                    previousCommand = token;

                    cx1 = previousPoint.p.v.x - (previousPoint.cpEnter.v.x - previousPoint.p.v.x);
                    cy1 = previousPoint.p.v.y - (previousPoint.cpEnter.v.y - previousPoint.p.v.y);
                    xx = previousPoint.p.v.x;
                    yy = previousPoint.p.v.y;
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
                                getPointX(it.next());
                                previousPoint = pathLineTo(resul, currentX, currentY, true);
                                break;
                            case "V":
                                previousCommand = "V";
                                getPointY(it.next());
                                previousPoint = pathLineTo(resul, currentX, currentY, true);
                                break;
                            case "h":
                                previousCommand = "h";
                                xx = previousPoint.p.v.x;
                                getPointX(it.next());
                                currentX += xx;
                                previousPoint = pathLineTo(resul, currentX, currentY, true);
                                break;
                            case "v":
                                previousCommand = "v";
                                yy = previousPoint.p.v.y;
                                getPointY(it.next());
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
                                xx = previousPoint.p.v.x;
                                yy = previousPoint.p.v.y;
                                getPointX(token);
                                getPointY(it.next());
                                currentX += xx;
                                currentY += yy;
                                previousPoint = pathLineTo(resul, currentX, currentY, true);
                                break;
                            case "l":
                                previousCommand = "l";
                                xx = previousPoint.p.v.x;
                                yy = previousPoint.p.v.y;
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
                                xx = previousPoint.p.v.x;
                                yy = previousPoint.p.v.y;
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
                                cx1 = previousPoint.p.v.x - (previousPoint.cpEnter.v.x - previousPoint.p.v.x);
                                cy1 = previousPoint.p.v.y - (previousPoint.cpEnter.v.y - previousPoint.p.v.y);
                                cx2 = Double.parseDouble(token);
                                cy2 = -Double.parseDouble(it.next());
                                getPoint(it.next(), it.next());
                                previousPoint = pathCubicBezier(resul, previousPoint, cx1, cy1, cx2, cy2, currentX, currentY);
                                break;
                            case "s": // Simplified relative Cubic Bezier. Take first control point as a reflection of
                                // previous one
                                cx1 = previousPoint.p.v.x - (previousPoint.cpEnter.v.x - previousPoint.p.v.x);
                                cy1 = previousPoint.p.v.y - (previousPoint.cpEnter.v.y - previousPoint.p.v.y);
                                xx = previousPoint.p.v.x;
                                yy = previousPoint.p.v.y;
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

    private ArrayList<String> getPointTokens(String s) {
        String t = s.replace("-", " -");// Avoid errors with strings like "142.11998-.948884"
        t = t.replace("e -", "e-");// Avoid errors with numbers in scientific format
        t = t.replace("E -", "E-");// Avoid errors with numbers in scientific format
        t = t.replace("M", " M ");// Adding spaces before and after to all commmands helps me to differentiate
        // easily from coordinates
        t = t.replace("m", " m ");
        t = t.replace("H", " H ");
        t = t.replace("h", " h ");
        t = t.replace("V", " V ");
        t = t.replace("v", " v ");
        t = t.replace("C", " C ");
        t = t.replace("c", " c ");
        t = t.replace("S", " S ");
        t = t.replace("s", " s ");
        t = t.replace("L", " L ");
        t = t.replace("l", " l ");
        t = t.replace("Z", " Z ");
        t = t.replace("z", " z ");
        t = t.replace("q", " q ");
        t = t.replace("Q", " Q ");
        t = t.replace("a", " a ");
        t = t.replace("A", " A ");
        t = t.replaceAll(",", " ");// Replace all commas with spaces
        t = t.replaceAll("^ +| +$|( )+", "$1");// Removes duplicate spaces
        // Look for second decimal points and add a space. Chains like this "-.5.4"
        // should change to "-.5 .4"
        // TODO: Do it in a more efficient way, maybe with regex patterns
        String[] tokens_1 = t.split(" ");
        ArrayList<String> tokens = new ArrayList<>();
        for (String tok : tokens_1) {
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
            tokens.addAll(Arrays.asList(tok2.split(" ")));
        }
        return tokens;
    }

    private JMPathPoint pathQuadraticBezier(JMPath resul, JMPathPoint previousPoint, double qx0, double qy0, double qx1, double qy1, double qx2, double qy2) {
        double cx1;
        double cy1;
        double cx2;
        double cy2;
        cx1 = qx0 + (2d / 3) * (qx1 - qx0);
        cy1 = qy0 + (2d / 3) * (qy1 - qy0);
        cx2 = qx2 + (2d / 3) * (qx1 - qx2);
        cy2 = qy2 + (2d / 3) * (qy1 - qy2);
        previousPoint = pathCubicBezier(resul, previousPoint, cx1, cy1, cx2, cy2, currentX, currentY);
        return previousPoint;
    }

    private void getPoint(String x, String y) throws NumberFormatException {
        getPointX(x);
        getPointY(y);
    }

    private void getPointX(String x) throws NumberFormatException {
        previousX = currentX;
        currentX = Double.parseDouble(x);
    }

    private void getPointY(String y) throws NumberFormatException {
        previousY = currentY;
        currentY = -Double.parseDouble(y);
    }

    private JMPathPoint pathCubicBezier(JMPath path, JMPathPoint previousPoint, double cx1, double cy1, double cx2,
            double cy2, double x, double y) {
        JMPathPoint point = new JMPathPoint(new Point(currentX, currentY), true, JMPathPoint.JMPathPointType.VERTEX);
        point.isCurved = true;
        previousPoint.cpExit.v.x = cx1;
        previousPoint.cpExit.v.y = cy1;
        point.cpEnter.v.x = cx2;
        point.cpEnter.v.y = cy2;
        path.addJMPoint(point);
        return point;
    }

    // Adds a simple point to the path, with control points equal to the point
    private JMPathPoint pathLineTo(JMPath path, double currentX, double currentY, boolean isVisible) {
        JMPathPoint point = new JMPathPoint(new Point(currentX, currentY), isVisible, JMPathPoint.JMPathPointType.VERTEX);
        point.isCurved = false;
        point.cpExit.v.x = currentX;
        point.cpExit.v.y = currentY;
        point.cpEnter.v.x = currentX;
        point.cpEnter.v.y = currentY;
        path.addJMPoint(point);
        return point;
    }

    private void processStyleAttributeCommands(Element el, MODrawProperties ShMp) {
        if (!"".equals(el.getAttribute("style"))) {
            parseStyleAttribute(el.getAttribute("style"), ShMp);
        }
        if (!"".equals(el.getAttribute("stroke"))) {
            JMColor strokeColor = JMColor.parse(el.getAttribute("stroke"));
            ShMp.setDrawColor(strokeColor);
        }

        if (!"".equals(el.getAttribute("stroke-width"))) {
            double th = Double.parseDouble(el.getAttribute("stroke-width"));
            double th2 = scene.getRenderer().MathWidthToThickness(th);
            ShMp.setThickness(th2);
        }

        if (!"".equals(el.getAttribute("fill"))) {
            JMColor fillColor = JMColor.parse(el.getAttribute("fill"));
            ShMp.setFillColor(fillColor);
        }

    }

    private void processTransformAttributeCommands(Element el, AffineJTransform currentTransform) {
        if (!"".equals(el.getAttribute("transform"))) {
            parseTransformAttribute(el.getAttribute("transform"), currentTransform);
        }
    }

    private void parseStyleAttribute(String str, MODrawProperties ShMp) {
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
                    double th2 = scene.getRenderer().MathWidthToThickness(th);
                    ShMp.setThickness(th2);

            }

        }
    }

    private AffineJTransform parseTransformAttribute(String trans, AffineJTransform currentTransform) {
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

    private AffineJTransform parseTransformCommand(String command, String arguments) {
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
                resul = AffineJTransform.createScaleTransform(Point.origin(), a, b);
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
                resul = AffineJTransform.create2DRotationTransform(Point.at(b, c), a * DEGREES);
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
        AffineJTransform sc1 = AffineJTransform.createScaleTransform(Point.origin(), 1, -1);
        AffineJTransform sc2 = AffineJTransform.createScaleTransform(Point.origin(), 1, -1);
        resul = sc1.compose(resul).compose(sc2);
        return resul;
    }

    /**
     * Writes a XML DOM object to a XML File
     *
     * @param rootElement Root element
     * @param fileName File name
     * @throws Exception
     */
    public void writeElementToXMLFile(Element rootElement, String fileName) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document document = docBuilder.newDocument();

        // Importa el elemento al nuevo documento
        Node importedNode = document.importNode(rootElement, true);
        document.appendChild(importedNode);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        // Opcional: Establecer opciones de formato
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        DOMSource source = new DOMSource(document);

        StreamResult result = new StreamResult(fileName);

        transformer.transform(source, result);
    }
}
