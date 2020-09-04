/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Utils;

import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.Point;
import java.util.Arrays;
import java.util.Iterator;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class SVGImporter {

    double currentX = 171;
    double currentY = 0;
    double previousX = 171;
    double previousY = 0;

    JMPath jmpath;

    public SVGImporter() {
    }

    public JMPath PSVGtoPath(String s) {
        jmpath = new JMPath();
        jmpath.open();
        processPathCommands(jmpath, s);
        return jmpath;
    }

    public void processPathCommands(JMPath path, String s) {
        JMPathPoint previousPoint = new JMPathPoint(new Point(0, 0), true, 0);
        String t = s.replace("M", " M ");
        t = t.replace("H", " H ");
        t = t.replace("V", " V ");
        t = t.replace("C", " C ");
        t = t.replace("S", " S ");
        t = t.replace("L", " L ");
        t = t.replace("Z", " Z ");
        t = t.replaceAll("^ +| +$|( )+", "$1");//Removes duplicate spaces
        String[] tokens = t.split(" ");
        Iterator<String> it = Arrays.stream(tokens).iterator();
        int n = 0;
        int nmax = tokens.length;
        double cx1, cx2, cy1, cy2;
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
                    previousPoint = pathLineTo(path, currentX, currentY);
                    previousPoint.isVisible = false;
//                    previousPoint = pathM(path, currentX, currentY);
                    break;

                case "L": //Line
                    getPoint(it);
                    previousPoint = pathLineTo(path, currentX, currentY);
                    break;
                case "H": //Horizontal line
                    getPointX(it);
                    previousPoint = pathLineTo(path, currentX, currentY);
                    break;
                case "V": //Vertical line
                    getPointY(it);
                    previousPoint = pathLineTo(path, currentX, currentY);
                    break;
                case "C": //Cubic Bezier
                    cx1 = Double.parseDouble(it.next());
                    cy1 = -Double.parseDouble(it.next());
                    cx2 = Double.parseDouble(it.next());
                    cy2 = -Double.parseDouble(it.next());
                    getPoint(it);
                    previousPoint = pathCubicBezier(path, previousPoint, cx1, cy1, cx2, cy2, currentX, currentY);
                    break;
                case "S": //Simplified Cubic Bezier. Take first control point as a reflection of previous one
                    cx1 = previousPoint.p.v.x - (previousPoint.cp2.v.x - previousPoint.p.v.x);
                    cy1 = previousPoint.p.v.y - (previousPoint.cp2.v.y - previousPoint.p.v.y);
                    cx2 = Double.parseDouble(it.next());
                    cy2 = -Double.parseDouble(it.next());
                    getPoint(it);
                    previousPoint = pathCubicBezier(path, previousPoint, cx1, cy1, cx2, cy2, currentX, currentY);
                    break;
                case "Z":
//                    previousPoint = pathLineTo(path, initialX, initialY);
                    path.close();
                    break;
                default:
                    System.out.println("Unknow command: <" + token + ">");
            }
        }

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
