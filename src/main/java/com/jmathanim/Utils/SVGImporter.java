/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Utils;

import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.Point;
import com.sun.xml.internal.ws.util.StringUtils;
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
//        String s = "M171.477434 -3.716065H172.603212C172.274445 -2.241594 172.184781 -1.8132 172.184781 -1.145704C172.184781 -0.996264 172.184781 -0.727273 172.264482 -0.388543C172.364109 0.049813 172.473698 0.109589 172.623137 0.109589C172.82239 0.109589 173.031606 -0.069738 173.031606 -0.268991C173.031606 -0.328767 173.031606 -0.348692 172.97183 -0.488169C172.682913 -1.205479 172.682913 -1.853051 172.682913 -2.132005C172.682913 -2.660025 172.752652 -3.198007 172.862241 -3.716065H173.997982C174.127496 -3.716065 174.486151 -3.716065 174.486151 -4.054795C174.486151 -4.293898 174.276936 -4.293898 174.087646 -4.293898H170.750161C170.530983 -4.293898 170.152403 -4.293898 169.714047 -3.825654C169.365354 -3.437111 169.106326 -2.978829 169.106326 -2.929016C169.106326 -2.919054 169.106326 -2.82939 169.225877 -2.82939C169.305578 -2.82939 169.325504 -2.86924 169.385279 -2.948941C169.873449 -3.716065 170.451282 -3.716065 170.650535 -3.716065H171.218405C170.899601 -2.510585 170.361618 -1.305106 169.943187 -0.398506C169.863486 -0.249066 169.863486 -0.229141 169.863486 -0.159402C169.863486 0.029888 170.022888 0.109589 170.152403 0.109589C170.451282 0.109589 170.530983 -0.169365 170.650535 -0.537983C170.790012 -0.996264 170.790012 -1.016189 170.919526 -1.514321L171.477434 -3.716065Z";
        processPathCommands(jmpath, s);
        jmpath.isFilled = false;
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
                    double cx1 = Double.parseDouble(it.next());
                    double cy1 = -Double.parseDouble(it.next());
                    double cx2 = Double.parseDouble(it.next());
                    double cy2 = -Double.parseDouble(it.next());
                    getPoint(it);
                    previousPoint = pathCubicBezier(path, previousPoint, cx1, cy1, cx2, cy2, currentX, currentY);
//                    previousPoint = pathLineTo(path, currentX, currentY);

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
