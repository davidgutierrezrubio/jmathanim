/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools  Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
    double closeX = 0;
    double closeY = 0;
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
//        scale(new Point(0, 0), scale, scale, scale);
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
        t = t.replace("m", " m ");
        t = t.replace("-", " -");//Avoid errors with strings like "142.11998-.948884"
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
            for (String tAdd : tok2.split(" ")) {
                tokens.add(tAdd);
            }
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
                case "M":
                    previousCommand = token;
                    getPoint(it.next(), it.next());
                    initialX = currentX;
                    initialY = currentY;
                    //First point. Creatline do the same as a the first point
                    previousPoint = pathLineTo(resul, currentX, currentY, false);
                    closeX = currentX;
                    closeY = currentY;
                    previousPoint.isVisible = false;
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
                    previousPoint.isVisible = false;
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
                    closeAtTheEnd = true;
//                    resul.close();
                    break;
                case "z":
                    previousCommand = token;
                    previousPoint = pathLineTo(resul, closeX, closeY, true);
                    closeAtTheEnd = true;
//                    resul.close();
                    break;
                default:
                    if (token.substring(0, 1) != "") //Not a command, but a point!
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
        if (closeAtTheEnd) {
            resul.close();
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
            Logger.getLogger(LaTeXMathObject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
