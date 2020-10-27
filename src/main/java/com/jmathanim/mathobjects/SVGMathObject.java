/*
 * Copyright (C) 2020 David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
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
package com.jmathanim.mathobjects;

import com.jmathanim.Cameras.CameraFX2D;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.JMColor;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.MODrawProperties;
import com.jmathanim.jmathanim.JMathAnimScene;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.smartcardio.ATR;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class manages import from SVG files and converting them into multipath
 * objects
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class SVGMathObject extends MultiShapeObject {

    protected String filename;
    double currentX = 0;
    double currentY = 0;
    double closeX = 0;
    double closeY = 0;
    double previousX = 0;
    double previousY = 0;
    Double anchorX = null;
    Double anchorY = null;

    JMPath importJMPathTemp;//Path to temporary import SVG Path commmands
    private JMColor currentFillColor;
    private JMColor currentDrawColor;
    private double currentStrokeSize = .5d;

    public static SVGMathObject make(String fname) {
        return new SVGMathObject(fname);
    }

    //This empty constructor is needed
    public SVGMathObject() {
    }

    public SVGMathObject(String fname) {
        filename = fname;
        this.setObjectType(MathObject.SVG);
        if (!"".equals(filename))
        try {
            importSVG(new File(filename));
        } catch (Exception ex) {
            Logger.getLogger(SVGMathObject.class.getName()).log(Level.SEVERE, null, ex);
        }
        currentFillColor = mp.fillColor.copy();
        currentDrawColor = mp.drawColor.copy();
    }

    protected final void importSVG(File file) throws Exception {
        JMathAnimScene.logger.info("Importing SVG file {}",file.getCanonicalPath());
        System.out.println("A1");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        System.out.println("A2");
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        System.out.println("A3");
        org.w3c.dom.Document doc = dBuilder.parse(file);
        System.out.println("A4");
        
        //Look for svg elements in the root document
        processChildNodes((doc.getDocumentElement()));
        System.out.println("A2");
        NodeList listGroups = doc.getElementsByTagName("g");
        for (int n = 0; n < listGroups.getLength(); n++) {
            System.out.println("A"+(n+3));
            Element gNode = (Element) listGroups.item(n);
            processAttributeCommands(gNode, mp);
            mp.thickness = .1;
            //Look for svg elements inside this group
            processChildNodes(gNode);
        }
        putAt(new Point(0, 0), Anchor.UL);
    }

    private void processChildNodes(Element gNode) throws NumberFormatException {
        NodeList nList = gNode.getChildNodes();

        for (int nchild = 0; nchild < nList.getLength(); nchild++) {
            Node node = nList.item(nchild);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) node;
                MODrawProperties ShMp = this.mp.copy();
                ShMp.absoluteThickness = false;
//                    ShMp.fillColor.set(JMColor.random());
                switch (el.getTagName()) {
                    case "path":
                        JMathAnimScene.logger.info("Parsing path");
                        //Needs to parse style options too
                        
                    try {
                        JMPath path = processPathCommands(el.getAttribute("d"));
                        processAttributeCommands(el, ShMp);
                        if (path.jmPathPoints.size() > 0) {
                            path.pathType = JMPath.SVG_PATH; //Mark this as a SVG path
                            addJMPathObject(path, ShMp); //Add this path to the array of JMPathObjects
                            JMathAnimScene.logger.info("Path parsed succesfully");
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(SVGMathObject.class.getName()).log(Level.SEVERE, null, ex);
                    }
                        
                    break;
                    case "rect":
                        double x = Double.parseDouble(el.getAttribute("x"));
                        double y = -Double.parseDouble(el.getAttribute("y"));
                        double w = Double.parseDouble(el.getAttribute("width"));
                        double h = -Double.parseDouble(el.getAttribute("height"));
                        shapes.add(Shape.rectangle(new Point(x, y), new Point(x + w, y + h)).setMp(ShMp));
                        break;
                    case "circle":
                        double cx = Double.parseDouble(el.getAttribute("cx"));
                        double cy = -Double.parseDouble(el.getAttribute("cy"));
                        double radius = Double.parseDouble(el.getAttribute("r"));
                        shapes.add(Shape.circle().scale(radius).shift(cx, cy).setMp(ShMp));
                        break;
                    case "ellipse":
                        double cxe = Double.parseDouble(el.getAttribute("cx"));
                        double cye = -Double.parseDouble(el.getAttribute("cy"));
                        double rxe = Double.parseDouble(el.getAttribute("rx"));
                        double rye = -Double.parseDouble(el.getAttribute("ry"));
                        shapes.add(Shape.circle().scale(rxe, rye).shift(cxe, cye).setMp(ShMp));
                        break;

                }

            }
        }
    }

    /**
     * Takes a string of SVG Path commands and converts then into a JMPathObject
     * Only fill attribute is parsed into the path
     *
     * @param s The string of commands
     * @return The JMPathObject
     */
    public JMPath processPathCommands(String s) throws Exception {
        JMPath resul = new JMPath();
        JMPathPoint previousPoint = new JMPathPoint(new Point(0, 0), true, 0);
        String t = s.replace("M", " M ");
        t = t.replace("m", " m ");
        t = t.replace("-", " -");//Avoid errors with strings like "142.11998-.948884"
        t = t.replace("e -", "e-");//Avoid errors with numbers in scientific format
        t = t.replace("H", " H ");//Adding "" to all commmands helps me to differentiate easily from coordinates
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
        t = t.replaceAll(",", " ");//Delete all commas
        t = t.replaceAll("^ +| +$|( )+", "$1");//Removes duplicate spaces

        //Look for second decimal points and add a space. Chains like this "-.5.4" should change to "-.5 .4"
        //TODO: Do it in a more efficient way, maybe with regex patterns
        String[] tokens_1 = t.split(" ");
        ArrayList<String> tokens = new ArrayList<>();
        boolean closeAtTheEnd = false;
        for (String tok : tokens_1) {
            StringBuilder st = new StringBuilder(tok);
            String tok2 = tok;
            int index = st.indexOf(".");
            if (index > -1) {
                if (st.indexOf(".", index + 1) > -1) {//If there is a second point

                    st.setCharAt(index, '|');//Replace first decimal point by '|'

                    tok2 = st.toString();
                    tok2 = tok2.replace(".", " .");
                    tok2 = tok2.replace("- .", "-.");
                    tok2 = tok2.replace("|", ".");
                }
            }
            tokens.addAll(Arrays.asList(tok2.split(" ")));
        }

        debugSVG(tokens);
        Iterator<String> it = tokens.iterator();
        int n = 0;
        int nmax = tokens.size();
        double cx1, cx2, cy1, cy2;
        double xx, yy;
        String previousCommand = "";
        Double initialX = null;
        Double initialY = null;
        currentX = 0;
        currentY = 0;
        previousPoint.p.v.x = 0;
        previousPoint.p.v.y = 0;
        while (it.hasNext()) {
            //Main loop, looking for commands
            String token = it.next().trim();
            switch (token) {
                case "":
                    break;
                case "A":
                    throw new Exception("Arc command A still not implemented. Sorry.");
                case "a":
                    throw new Exception("Arc command a still not implemented. Sorry.");
                case "M":
                    previousCommand = token;
                    getPoint(it.next(), it.next());
                    initialX = currentX;
                    initialY = currentY;
                    //First point. Creatline do the same as a the first point
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
                    //First point. Creatline do the same as a the first point
                    previousPoint = pathLineTo(resul, currentX, currentY, false);
                    previousPoint.isThisSegmentVisible = false;
//                    previousPoint = pathM(path, currentX, currentY);
                    break;

                case "L": //Line
                    previousCommand = token;
                    getPoint(it.next(), it.next());
                    previousPoint = pathLineTo(resul, currentX, currentY, true);
                    break;
                case "l": //Line
                    previousCommand = token;
                    xx = previousPoint.p.v.x;
                    yy = previousPoint.p.v.y;
                    getPoint(it.next(), it.next());
                    currentX += xx;
                    currentY += yy;
                    previousPoint = pathLineTo(resul, currentX, currentY, true);
                    break;

                case "H": //Horizontal line
                    previousCommand = token;

                    getPointX(it.next());
                    previousPoint = pathLineTo(resul, currentX, currentY, true);
                    break;

                case "h": //Horizontal line
                    previousCommand = token;

                    xx = previousPoint.p.v.x;
                    getPointX(it.next());
                    currentX += xx;
                    previousPoint = pathLineTo(resul, currentX, currentY, true);
                    break;
                case "V": //Vertical line
                    previousCommand = token;

                    getPointY(it.next());
                    previousPoint = pathLineTo(resul, currentX, currentY, true);
                    break;
                case "v": //Vertical line
                    previousCommand = token;

                    yy = previousPoint.p.v.y;
                    getPointY(it.next());
                    currentY += yy;
                    previousPoint = pathLineTo(resul, currentX, currentY, true);
                    break;
                case "C": //Cubic Bezier
                    previousCommand = token;

                    cx1 = Double.parseDouble(it.next());
                    cy1 = -Double.parseDouble(it.next());
                    cx2 = Double.parseDouble(it.next());
                    cy2 = -Double.parseDouble(it.next());
                    getPoint(it.next(), it.next());
                    previousPoint = pathCubicBezier(resul, previousPoint, cx1, cy1, cx2, cy2, currentX, currentY);
                    break;
                //c 1,1 2,2 3,3 4,4 5,5 6,6 would become C 1,1 2,2 3,3 C 7,7 8,8 9,9
                case "c": //Cubic Bezier
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
                case "S": //Simplified Cubic Bezier. Take first control point as a reflection of previous one
                    previousCommand = token;

                    cx1 = previousPoint.p.v.x - (previousPoint.cp2.v.x - previousPoint.p.v.x);
                    cy1 = previousPoint.p.v.y - (previousPoint.cp2.v.y - previousPoint.p.v.y);
                    cx2 = Double.parseDouble(it.next());
                    cy2 = -Double.parseDouble(it.next());
                    getPoint(it.next(), it.next());
                    previousPoint = pathCubicBezier(resul, previousPoint, cx1, cy1, cx2, cy2, currentX, currentY);
                    break;

                case "s": //Simplified relative Cubic Bezier. Take first control point as a reflection of previous one
                    previousCommand = token;

                    cx1 = previousPoint.p.v.x - (previousPoint.cp2.v.x - previousPoint.p.v.x);
                    cy1 = previousPoint.p.v.y - (previousPoint.cp2.v.y - previousPoint.p.v.y);
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
                    if (!"".equals(token.substring(0, 1))) //Not a command, but a point!
                    {
                        switch (previousCommand) {
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
                            case "S": //Simplified Cubic Bezier. Take first control point as a reflection of previous one
                                cx1 = previousPoint.p.v.x - (previousPoint.cp2.v.x - previousPoint.p.v.x);
                                cy1 = previousPoint.p.v.y - (previousPoint.cp2.v.y - previousPoint.p.v.y);
                                cx2 = Double.parseDouble(token);
                                cy2 = -Double.parseDouble(it.next());
                                getPoint(it.next(), it.next());
                                previousPoint = pathCubicBezier(resul, previousPoint, cx1, cy1, cx2, cy2, currentX, currentY);
                                break;
                            case "s": //Simplified relative Cubic Bezier. Take first control point as a reflection of previous one
                                cx1 = previousPoint.p.v.x - (previousPoint.cp2.v.x - previousPoint.p.v.x);
                                cy1 = previousPoint.p.v.y - (previousPoint.cp2.v.y - previousPoint.p.v.y);
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
                                System.out.println("Unknow repeated command: <" + token + ">");

                        }

                    }
            }
        }

        return resul;
    }

    public void getPoint(String x, String y) throws NumberFormatException {
        getPointX(x);
        getPointY(y);
    }

    public void getPointX(String x) throws NumberFormatException {
        previousX = currentX;
        currentX = Double.parseDouble(x);
    }

    public void getPointY(String y) throws NumberFormatException {
        previousY = currentY;
        currentY = -Double.parseDouble(y);
    }

//    private JMPathPoint pathM(JMPath path, double x, double y) {
//        JMPathPoint point = new JMPathPoint(new Point(currentX, currentY), true, JMPathPoint.TYPE_VERTEX);
//        point.isCurved = false;
//        point.isVisible = false;
//        point.cp1.v.x = currentX;
//        point.cp1.v.y = currentY;
//        point.cp2.v.x = currentX;
//        point.cp2.v.y = currentY;
//        path.addPoint(point);
//        return point;
//    }
    private JMPathPoint pathCubicBezier(JMPath path, JMPathPoint previousPoint, double cx1, double cy1, double cx2, double cy2, double x, double y) {
        JMPathPoint point = new JMPathPoint(new Point(currentX, currentY), true, JMPathPoint.TYPE_VERTEX);
        point.isCurved = true;
        previousPoint.cp1.v.x = cx1;
        previousPoint.cp1.v.y = cy1;
        point.cp2.v.x = cx2;
        point.cp2.v.y = cy2;
        path.addJMPoint(point);
        return point;
    }

    //Adds a simple point to the path, with control points equal to the point
    private JMPathPoint pathLineTo(JMPath path, double currentX, double currentY, boolean isVisible) {
        JMPathPoint point = new JMPathPoint(new Point(currentX, currentY), isVisible, JMPathPoint.TYPE_VERTEX);
        point.isCurved = false;
        point.cp1.v.x = currentX;
        point.cp1.v.y = currentY;
        point.cp2.v.x = currentX;
        point.cp2.v.y = currentY;
        path.addJMPoint(point);
        return point;
    }

    public void debugSVG(ArrayList<String> tokens) {
        FileWriter fw;
        PrintWriter pw;
        try {
            fw = new FileWriter("c:\\media\\debugsvg.txt");
            pw = new PrintWriter(fw);
            for (String tok : tokens) {
                pw.print(tok + "\n");
            }
            pw.close();
        } catch (IOException ex) {
            Logger.getLogger(LaTeXMathObject.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void processAttributeCommands(Element el, MODrawProperties ShMp) {
        if (!"".equals(el.getAttribute("style"))) {
            parseStyleAttribute(el.getAttribute("style"), ShMp);
        }
        if (!"".equals(el.getAttribute("stroke"))) {
            ShMp.drawColor.copyFrom(JMColor.parseColorID(el.getAttribute("stroke")));
        }

        if (!"".equals(el.getAttribute("stroke-width"))) {
            double th = Double.parseDouble(el.getAttribute("stroke-width"));
            CameraFX2D cam = (CameraFX2D) JMathAnimConfig.getConfig().getCamera();
//              ShMp.thickness=cam.mathToScreenFX(th)/cam.getMathView().getWidth();
            ShMp.thickness = cam.mathToScreenFX(th) / 4;
        }

        if (!"".equals(el.getAttribute("fill"))) {
            ShMp.fillColor.copyFrom(JMColor.parseColorID(el.getAttribute("fill")));
        }

    }

    private void parseStyleAttribute(String str, MODrawProperties ShMp) {
        String[] decls = str.split(";");
        for (String pairs : decls) {
            String[] decl = pairs.split(":");
            switch (decl[0]) {
                case "fill":
                    ShMp.fillColor.copyFrom(JMColor.parseColorID(decl[1]));
            }

        }

    }
}
