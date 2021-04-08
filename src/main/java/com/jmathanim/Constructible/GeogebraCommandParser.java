/*
 * Copyright (C) 2021 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
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
package com.jmathanim.Constructible;

import com.jmathanim.Constructible.Conics.ConstrCircleCenter3Points;
import com.jmathanim.Constructible.Conics.ConstrCircleCenterPoint;
import com.jmathanim.Constructible.Conics.ConstrCircleCenterRadius;
import com.jmathanim.Constructible.Lines.ConstrLineOrthogonal;
import com.jmathanim.Constructible.Lines.ConstrLineParallel;
import com.jmathanim.Constructible.Lines.ConstrLinePointPoint;
import com.jmathanim.Constructible.Lines.ConstrPerpBisectorPointPoint;
import com.jmathanim.Constructible.Lines.ConstrPerpBisectorSegment;
import com.jmathanim.Constructible.Lines.ConstrSegmentPointPoint;
import com.jmathanim.Constructible.Lines.HasDirection;
import com.jmathanim.Styling.JMColor;
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Scalar;
import com.jmathanim.mathobjects.Shape;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.w3c.dom.Element;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class GeogebraCommandParser {

    protected final HashMap<String, MathObject> geogebraElements;

    public GeogebraCommandParser() {
        this.geogebraElements = new HashMap<>();
    }

    /**
     * Process a geogebra argument. May be a name of an existing object, a point
     * or a scalar
     *
     * @param argument String with the argument
     * @return The MathObject generated
     */
    private MathObject parseArgument(String argument) {
        if (geogebraElements.containsKey(argument)) {
            return geogebraElements.get(argument);
        }
        try {//try if it is a number...
            double value = Double.valueOf(argument);
            return new Scalar(value);
        } catch (NumberFormatException ex) {
            //Nothing to do here, pass to the next guess
        }

        //Try if it is a point expressed in (a,b) form (an anoynimous point)
        Pattern pattern = Pattern.compile("\\((.*),(.*)\\)");
        Matcher matcher = pattern.matcher(argument);
        if (matcher.find()) {
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
            return Point.at(Double.valueOf(matcher.group(1)), Double.valueOf(matcher.group(2)));
        }

        //Nothing recognized so far, throw an exception
        try {
            throw new Exception("Don't know how to parse this argument " + argument);
        } catch (Exception ex) {
            Logger.getLogger(GeogebraLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public MathObject get(Object key) {
        return geogebraElements.get(key);
    }

    public boolean containsKey(Object key) {
        return geogebraElements.containsKey(key);
    }

    public void registerGeogebraElement(String label, MathObject resul) {
        resul.label = label;
        geogebraElements.put(label, resul);
    }

    private String getInputArgument(Element el, int num) {
        Element elInput = firstElementWithTag(el, "input");
        return elInput.getAttribute("a" + num);
    }

    private String getOutputArgument(Element el, int num) {
        Element elOutput = firstElementWithTag(el, "output");
        return elOutput.getAttribute("a" + num);
    }

    protected MODrawProperties parseStylingOptions(Element el) {
        MODrawProperties resul = MODrawProperties.makeNullValues();

        //Visibility
        Element show = firstElementWithTag(el, "show");
        resul.visible = ("true".equals(show.getAttribute("object")));

        //Layer
        Element layer = firstElementWithTag(el, "layer");
        resul.setLayer(Integer.valueOf(layer.getAttribute("val")));

        //Color
        Element objColor = firstElementWithTag(el, "objColor");
        int r = Integer.valueOf(objColor.getAttribute("r"));
        int g = Integer.valueOf(objColor.getAttribute("g"));
        int b = Integer.valueOf(objColor.getAttribute("b"));
        double alpha = Double.valueOf(objColor.getAttribute("alpha"));
        JMColor col = JMColor.rgbInt(r, g, b, 255);
        JMColor colFill = JMColor.rgbInt(r, g, b, 255);
        colFill.alpha = alpha;

        resul.setDrawColor(col);
        resul.setFillColor(colFill);

        //Line style
        Element lineStyle = firstElementWithTag(el, "lineStyle");
        if (lineStyle != null) {
            double thickness = Double.valueOf(lineStyle.getAttribute("thickness"));
            resul.setThickness(thickness);
        }
        //Point size
        Element pointSize = firstElementWithTag(el, "pointSize");
        if (pointSize != null) {
            double thickness = Double.valueOf(pointSize.getAttribute("val")) / 3;//Scaling factor guessed...
            resul.setThickness(thickness);
        }

        return resul;
    }

    private Element firstElementWithTag(Element el, String name) {
        if (el.getElementsByTagName(name).getLength() > 0) {
            Element elInput = (Element) el.getElementsByTagName(name).item(0);
            return elInput;
        } else {
            return null;
        }
    }

    private MathObject[] getArrayOfParameters(Element el) {
        Element elInput = firstElementWithTag(el, "input");
        MathObject[] objs = new MathObject[elInput.getAttributes().getLength()];
        for (int i = 0; i < elInput.getAttributes().getLength(); i++) {
            String label = elInput.getAttribute("a" + i);
            objs[i] = geogebraElements.get(label);
        }
        return objs;
    }

    private String[] getArrayOfOutputs(Element el) {
        Element elInput = firstElementWithTag(el, "output");
        String[] outputs = new String[elInput.getAttributes().getLength()];
        for (int i = 0; i < elInput.getAttributes().getLength(); i++) {
            String output = elInput.getAttribute("a" + i);
            outputs[i] = output;
        }
        return outputs;
    }

    //COMMANDS
    protected void processPoint(Element el) {
        String label = el.getAttribute("label");
        //Get the coordinates
        Element elCoords = firstElementWithTag(el, "coords");
        double x = Double.valueOf(elCoords.getAttribute("x"));
        double y = Double.valueOf(elCoords.getAttribute("y"));
        //TODO: Add a z value here
        Point resul = Point.at(x, y);
        resul.label = label;
        geogebraElements.put(label, resul);
//        resul.getMp().copyFrom(parseStylingOptions(el));
        JMathAnimScene.logger.info("Imported point {}", label);
    }

    protected void processSegmentCommand(Element el) {
        Element elInput = firstElementWithTag(el, "input");
        String labelPoint1 = elInput.getAttribute("a0");
        String labelPoint2 = elInput.getAttribute("a1");
        Point p1 = (Point) geogebraElements.get(labelPoint1);
        Point p2 = (Point) geogebraElements.get(labelPoint2);
        MathObject resul = ConstrSegmentPointPoint.make(p1, p2);
        String label = firstElementWithTag(el, "output").getAttribute("a0");
        resul.label = label;
        geogebraElements.put(label, resul);
        JMathAnimScene.logger.info("Generated segment {}", label);
    }

    protected void processLineCommand(Element el) {
        String label = getOutputArgument(el, 0);
        MathObject[] params = getArrayOfParameters(el);
        Point A = (Point) params[0]; //First argument is always a point
        MathObject B = params[1];
        if (B instanceof Point) {//A line given by 2 points
            registerGeogebraElement(label, ConstrLinePointPoint.make(A, (Point) B));
            return;
        }
        if (B instanceof HasDirection) {//Line parallel
            registerGeogebraElement(label, ConstrLineParallel.make(A, (HasDirection) B));
        }
    }

    protected void processOrthogonalLine(Element el) {
        String label = getOutputArgument(el, 0);
        MathObject[] params = getArrayOfParameters(el);
        Point A = (Point) params[0]; //First argument is always a point
        MathObject B = params[1];
        if (B instanceof HasDirection) {
            registerGeogebraElement(label, ConstrLineOrthogonal.make(A, (HasDirection) B));
        }
    }

    void processPerpBisector(Element el) {
        String label = getOutputArgument(el, 0);
        MathObject[] params = getArrayOfParameters(el);
        if (params.length == 2) {//2 points
            Point A = (Point) params[0];
            Point B = (Point) params[1];
            registerGeogebraElement(label, ConstrPerpBisectorPointPoint.make(A, B));
        }
        if (params.length == 1) {//1 segment
            ConstrSegmentPointPoint seg = (ConstrSegmentPointPoint) params[0];
            registerGeogebraElement(label, ConstrPerpBisectorSegment.make(seg));
        }

    }

    protected void processPolygonCommand(Element el) {
        String[] outputs = getArrayOfOutputs(el);
        String label = outputs[0];
        MathObject[] objs = getArrayOfParameters(el);
        //Array of points of the polygon
        Point[] points = new Point[objs.length];
        for (int i = 0; i < objs.length; i++) {
            points[i] = (Point) objs[i];
        }
        Shape resul = Shape.polygon(points);
        geogebraElements.put(label, resul);
        
        
        //Now, build all segments
        for (int i = 0; i < points.length; i++) {
            int i2=(i<outputs.length-2 ? i+1 : 0);
            System.out.println(outputs[i+1]+"-->"+i+",  "+i2);
            registerGeogebraElement(outputs[i+1], ConstrSegmentPointPoint.make(points[i], points[i2]));
        }

    }

    protected void processCircleCommand(Element el) {
        String label = getOutputArgument(el, 0);
        Element elInput = firstElementWithTag(el, "input");
        int numberOfArguments = elInput.getAttributes().getLength();//Number of arguments
        if (numberOfArguments == 3) {//A circle defined by 3 points
            String str0 = elInput.getAttribute("a0");
            String str1 = elInput.getAttribute("a1");
            String str2 = elInput.getAttribute("a2");

            Point arg0 = (Point) parseArgument(str0);
            Point arg1 = (Point) parseArgument(str1);
            Point arg2 = (Point) parseArgument(str2);
            Constructible resul = ConstrCircleCenter3Points.make(arg0, arg1, arg2);
            registerGeogebraElement(label, resul);
            JMathAnimScene.logger.debug("Imported Geogebra Circle " + label + " by 3 points: " + arg0 + ", " + arg1 + ",  " + arg2);
            return;

        }

        if (numberOfArguments == 2) {
            String str0 = elInput.getAttribute("a0");
            String str1 = elInput.getAttribute("a1");

            MathObject arg0 = parseArgument(str0);
            MathObject arg1 = parseArgument(str1);

            //A circle with center a point and another one in the perimeter
            if ((arg0 instanceof Point) && (arg1 instanceof Point)) {
                Point p0 = (Point) arg0;
                Point p1 = (Point) arg1;
                Constructible resul = ConstrCircleCenterPoint.make(p0, p1);
                registerGeogebraElement(label, resul);
                JMathAnimScene.logger.debug("Imported Geogebra Circle " + label + ", center " + p0 + ", point " + p1);
                return;
            }
            if ((arg0 instanceof Point) && (arg1 instanceof Scalar)) {
                Point p0 = (Point) arg0;
                Scalar sc0 = (Scalar) arg1;
                Constructible resul = ConstrCircleCenterRadius.make(p0, sc0);
                registerGeogebraElement(label, resul);
                JMathAnimScene.logger.debug("Imported Geogebra Circle " + label + ", center " + p0 + ", radius " + sc0);
                return;

            }

        }
    }

}
