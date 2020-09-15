/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class manages import from SVG files and converting them into multipath
 * objects
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class SVGMathObject extends MultiShapeObject {

    protected String filename;
    double currentX = 0;
    double currentY = 0;
    double previousX = 0;
    double previousY = 0;
    Double anchorX = null;
    Double anchorY = null;

    JMPath importJMPathTemp;//Path to temporary import SVG Path commmands

    public SVGMathObject() {
    }

    public SVGMathObject(String fname) {
        filename = fname;
        if (filename != "")
        try {
            importSVG(new File(filename));
        } catch (Exception ex) {
            Logger.getLogger(SVGMathObject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void importSVG(File file) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        org.w3c.dom.Document doc = dBuilder.parse(file);

        NodeList nList = doc.getElementsByTagName("path");

        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;

                JMPath path = processPathCommands(eElement.getAttribute("d"));

                if (path.jmPathPoints.size() > 0) {
                    path.pathType = JMPath.SVG_PATH; //Mark this as a SVG path

                    if (eElement.getAttribute("fill") == "none") {
                        mp.fill = false;
                    } else {
                        mp.fill = true;
                    }

                    addJMPathObject(path); //Add this path to the array of JMPathObjects
                    if (anchorX == null) //If this is the first path I encountered...
                    {
                        //Mark the UL corner of its boundingbox as the point of reference
                        //This corner will become (0,0)
                        Rect r = path.getBoundingBox();
                        anchorX = r.xmin;
                        anchorY = r.ymax;
                    }
                }
                path.shift(new Vec(-anchorX, -anchorY));//
                //By default scale sizes so that SVG points matches Screen points
            }
        }
        //All paths imported
        double mathH = JMathAnimConfig.getConfig().getCamera().screenToMath(22);
        double scale = 5 * mathH / getBoundingBox().getHeight();//10% of screen
        scale(new Point(0, 0), scale, scale, scale);
    }

    /**
     * Takes a string of SVG Path commands and converts then into a JMPathObject
     * Only fill attribute is parsed into the path
     *
     * @param s The string of commands
     * @return The JMPathObject
     */
    public JMPath processPathCommands(String s) {
        JMPath resul = new JMPath();
        JMPathPoint previousPoint = new JMPathPoint(new Point(0, 0), true, 0);
        String t = s.replace("M", " M ");
        t = t.replace("-", " -");//Avoid errors with strings like "142.11998-.948884"
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
        t = t.replaceAll(",", " ");//Delete all commas
        t = t.replaceAll("^ +| +$|( )+", "$1");//Removes duplicate spaces
        String[] tokens = t.split(" ");
        Iterator<String> it = Arrays.stream(tokens).iterator();
        int n = 0;
        int nmax = tokens.length;
        double cx1, cx2, cy1, cy2;
        double xx, yy;
        Double initialX = null;
        Double initialY = null;
        while (it.hasNext()) {
            //Main loop, looking for commands
            String token = it.next().trim();
            switch (token) {
                case "":
                    break;
                case "M":
                    getPoint(it);
                    initialX = currentX;
                    initialY = currentY;
                    //First point. Creatline do the same as a the first point
                    previousPoint = pathLineTo(resul, currentX, currentY);
                    previousPoint.isVisible = false;
//                    previousPoint = pathM(path, currentX, currentY);
                    break;

                case "L": //Line
                    getPoint(it);
                    previousPoint = pathLineTo(resul, currentX, currentY);
                    break;
                case "l": //Line
                    xx = previousPoint.p.v.x;
                    yy = previousPoint.p.v.y;
                    getPoint(it);
                    currentX += xx;
                    currentY += yy;
                    previousPoint = pathLineTo(resul, currentX, currentY);
                    break;

                case "H": //Horizontal line
                    getPointX(it);
                    previousPoint = pathLineTo(resul, currentX, currentY);
                    break;

                case "h": //Horizontal line
                    xx = previousPoint.p.v.x;
                    getPointX(it);
                    currentX += xx;
                    previousPoint = pathLineTo(resul, currentX, currentY);
                    break;
                case "V": //Vertical line
                    getPointY(it);
                    previousPoint = pathLineTo(resul, currentX, currentY);
                    break;
                case "v": //Vertical line
                    yy = previousPoint.p.v.y;
                    getPointY(it);
                    currentY += yy;
                    previousPoint = pathLineTo(resul, currentX, currentY);
                    break;
                case "C": //Cubic Bezier
                    cx1 = Double.parseDouble(it.next());
                    cy1 = -Double.parseDouble(it.next());
                    cx2 = Double.parseDouble(it.next());
                    cy2 = -Double.parseDouble(it.next());
                    getPoint(it);
                    previousPoint = pathCubicBezier(resul, previousPoint, cx1, cy1, cx2, cy2, currentX, currentY);
                    break;
                //c 1,1 2,2 3,3 4,4 5,5 6,6 would become C 1,1 2,2 3,3 C 7,7 8,8 9,9
                case "c": //Cubic Bezier
                    xx = previousPoint.p.v.x;
                    yy = previousPoint.p.v.y;
                    cx1 = xx + Double.parseDouble(it.next());
                    cy1 = yy - Double.parseDouble(it.next());
                    cx2 = xx + Double.parseDouble(it.next());
                    cy2 = yy - Double.parseDouble(it.next());
                    getPoint(it);
                    currentX += xx;
                    currentY += yy;

                    previousPoint = pathCubicBezier(resul, previousPoint, cx1, cy1, cx2, cy2, currentX, currentY);
                    break;
                case "S": //Simplified Cubic Bezier. Take first control point as a reflection of previous one
                    cx1 = previousPoint.p.v.x - (previousPoint.cp2.v.x - previousPoint.p.v.x);
                    cy1 = previousPoint.p.v.y - (previousPoint.cp2.v.y - previousPoint.p.v.y);
                    cx2 = Double.parseDouble(it.next());
                    cy2 = -Double.parseDouble(it.next());
                    getPoint(it);
                    previousPoint = pathCubicBezier(resul, previousPoint, cx1, cy1, cx2, cy2, currentX, currentY);
                    break;

                case "s": //Simplified relative Cubic Bezier. Take first control point as a reflection of previous one
                    cx1 = previousPoint.p.v.x - (previousPoint.cp2.v.x - previousPoint.p.v.x);
                    cy1 = previousPoint.p.v.y - (previousPoint.cp2.v.y - previousPoint.p.v.y);
                    xx = previousPoint.p.v.x;
                    yy = previousPoint.p.v.y;
                    cx2 = xx + Double.parseDouble(it.next());
                    cy2 = yy - Double.parseDouble(it.next());
                    getPoint(it);
                    currentX += xx;
                    currentY += yy;
                    previousPoint = pathCubicBezier(resul, previousPoint, cx1, cy1, cx2, cy2, currentX, currentY);
                    break;
                case "Z":
                    resul.close();
                    break;
                case "z":
                    resul.close();
                    break;
                default:
                    System.out.println("Unknow command: <" + token + ">");
            }
        }
        return resul;
    }

    public void getRelPoint(double xx, double yy, Iterator<String> it) throws NumberFormatException {
        getRelPointX(xx, it);
        getRelPointY(yy, it);
    }

    public void getPoint(Iterator<String> it) throws NumberFormatException {
        getPointX(it);
        getPointY(it);
    }

    public void getPointX(Iterator<String> it) throws NumberFormatException {
        previousX = currentX;
        currentX = Double.parseDouble(it.next());
    }

    public void getPointY(Iterator<String> it) throws NumberFormatException {
        previousY = currentY;
        currentY = -Double.parseDouble(it.next());
    }

    public void getRelPointX(double xx, Iterator<String> it) throws NumberFormatException {
        previousX = currentX;
        currentX = xx + Double.parseDouble(it.next());
    }

    public void getRelPointY(double yy, Iterator<String> it) throws NumberFormatException {
        previousY = currentY;
        currentY = yy - Double.parseDouble(it.next());
    }

    private JMPathPoint pathM(JMPath path, double x, double y) {
        JMPathPoint point = new JMPathPoint(new Point(currentX, currentY), true, JMPathPoint.TYPE_VERTEX);
        point.isCurved = false;
        point.isVisible = false;
        point.cp1.v.x = currentX;
        point.cp1.v.y = currentY;
        point.cp2.v.x = currentX;
        point.cp2.v.y = currentY;
        path.addPoint(point);
        return point;
    }

    private JMPathPoint pathCubicBezier(JMPath path, JMPathPoint previousPoint, double cx1, double cy1, double cx2, double cy2, double x, double y) {
        JMPathPoint point = new JMPathPoint(new Point(currentX, currentY), true, JMPathPoint.TYPE_VERTEX);
        point.isCurved = true;
        previousPoint.cp1.v.x = cx1;
        previousPoint.cp1.v.y = cy1;
        point.cp2.v.x = cx2;
        point.cp2.v.y = cy2;
        path.addPoint(point);
        return point;
    }

    //Adds a simple point to the path, with control points equal to the point
    private JMPathPoint pathLineTo(JMPath path, double currentX, double currentY) {
        JMPathPoint point = new JMPathPoint(new Point(currentX, currentY), true, JMPathPoint.TYPE_VERTEX);
        point.isCurved = false;
        point.cp1.v.x = currentX;
        point.cp1.v.y = currentY;
        point.cp2.v.x = currentX;
        point.cp2.v.y = currentY;
        path.addPoint(point);
        return point;
    }
}
