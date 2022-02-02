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

import com.jmathanim.Constructible.Points.CTPoint;
import com.jmathanim.Constructible.Conics.CTCircle;
import com.jmathanim.Constructible.Lines.CTAngleBisector;
import com.jmathanim.Constructible.Lines.CTLine;
import com.jmathanim.Constructible.Lines.CTLineOrthogonal;
import com.jmathanim.Constructible.Lines.CTPerpBisector;
import com.jmathanim.Constructible.Lines.CTPolygon;
import com.jmathanim.Constructible.Lines.CTRay;
import com.jmathanim.Constructible.Lines.CTSegment;
import com.jmathanim.Constructible.Lines.CTVector;
import com.jmathanim.Constructible.Lines.HasDirection;
import com.jmathanim.Constructible.Points.CTIntersectionPoint;
import com.jmathanim.Styling.JMColor;
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.NullMathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Scalar;
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

    protected final HashMap<String, Constructible> geogebraElements;

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
        try {// try if it is a number...
            double value = Double.valueOf(argument);
            return Scalar.make(value);
        } catch (NumberFormatException ex) {
            // Nothing to do here, pass to the next guess
        }

        // Try if it is a point expressed in (a,b) form (an anoynimous point)
        Pattern pattern = Pattern.compile("\\((.*),(.*)\\)");
        Matcher matcher = pattern.matcher(argument);
        if (matcher.find()) {
            System.out.println(matcher.group(1));
            return CTPoint.make(Point.at(Double.valueOf(matcher.group(1)), Double.valueOf(matcher.group(2))));
        }

        // Nothing recognized so far, throw an exception
        try {
            throw new Exception("Don't know how to parse this argument " + argument);
        } catch (Exception ex) {
            Logger.getLogger(GeogebraLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Constructible get(String key) {
        if (containsKey(key)) {
            return geogebraElements.get(key);
        } else {
            return new NullMathObject();
        }
    }

    public boolean containsKey(String key) {
        return geogebraElements.containsKey(key);
    }

    public void registerGeogebraElement(String label, Constructible resul) {
        resul.objectLabel = label;
        geogebraElements.put(label, resul);
    }

//    private String getInputArgument(Element el, int num) {
//        Element elInput = firstElementWithTag(el, "input");
//        return elInput.getAttribute("a" + num);
//    }
    private String getOutputArgument(Element el, int num) {
        Element elOutput = firstElementWithTag(el, "output");
        return elOutput.getAttribute("a" + num);
    }

    protected MODrawProperties parseStylingOptions(Element el) {
        MODrawProperties resul = MODrawProperties.makeNullValues();

        // Visibility
        Element show = firstElementWithTag(el, "show");
        resul.visible = ("true".equals(show.getAttribute("object")));

        // Layer
        Element layer = firstElementWithTag(el, "layer");
        resul.setLayer(Integer.valueOf(layer.getAttribute("val")));

        // Color
        Element objColor = firstElementWithTag(el, "objColor");
        int r = Integer.valueOf(objColor.getAttribute("r"));
        int g = Integer.valueOf(objColor.getAttribute("g"));
        int b = Integer.valueOf(objColor.getAttribute("b"));
        double alpha = Double.valueOf(objColor.getAttribute("alpha"));
        JMColor col = JMColor.rgbInt(r, g, b, 255);
        JMColor colFill = JMColor.rgbInt(r, g, b, 255);
        colFill.setAlpha(alpha);

        resul.setDrawColor(col);
        resul.setFillColor(colFill);

        // Line style. Only thickness
        //TODO: Parse dash style too
        Element lineStyle = firstElementWithTag(el, "lineStyle");
        if (lineStyle != null) {
            double thickness = Double.valueOf(lineStyle.getAttribute("thickness"));
            resul.setThickness(thickness);
            //Dash Style
            //0 :         MODrawProperties.DashStyle.SOLID
            //10,15:         MODrawProperties.DashStyle.DASHED
            //20:        MODrawProperties.DashStyle.DOTTED
            //30:         MODrawProperties.DashStyle.DASHDOTTED
            MODrawProperties.DashStyle dashStyle;
            int dashType = Integer.valueOf(lineStyle.getAttribute("type"));
            switch (dashType) {
                case 0:
                    dashStyle = MODrawProperties.DashStyle.SOLID;
                    break;
                case 10:
                case 15:
                    dashStyle = MODrawProperties.DashStyle.DASHED;
                    break;
                case 20:
                    dashStyle = MODrawProperties.DashStyle.DOTTED;
                    break;
                case 30:
                    dashStyle = MODrawProperties.DashStyle.DASHDOTTED;
                    break;
                default:
                    dashStyle = MODrawProperties.DashStyle.SOLID;
            }
            resul.setDashStyle(dashStyle);
        }
        // Point size
        Element pointSize = firstElementWithTag(el, "pointSize");
        if (pointSize != null) {
            double thickness = Double.valueOf(pointSize.getAttribute("val")) * 6;// Scaling factor guessed...
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
            objs[i] = parseArgument(label);
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

    // COMMANDS
    protected void processPoint(Element el) {
        String label = el.getAttribute("label");
        //If this object is already added, do not process it
        //for example if it is the result of an intersection command
        if (!geogebraElements.containsKey(label)) {
            // Get the coordinates
            Element elCoords = firstElementWithTag(el, "coords");
            double x = Double.valueOf(elCoords.getAttribute("x"));
            double y = Double.valueOf(elCoords.getAttribute("y"));
            Element pointSize = firstElementWithTag(el, "pointSize");
            double th = Double.valueOf(pointSize.getAttribute("val")) * 30;
            // TODO: Add a z value here
            CTPoint resul = CTPoint.make(Point.at(x, y));
            resul.thickness(th);
            resul.objectLabel = label;
            geogebraElements.put(label, resul);
            JMathAnimScene.logger.debug("Imported point {}", label);
        }
    }

    protected void processSegmentCommand(Element el) {
        Element elInput = firstElementWithTag(el, "input");
        String labelPoint1 = elInput.getAttribute("a0");
        String labelPoint2 = elInput.getAttribute("a1");
        CTPoint p1 = (CTPoint) geogebraElements.get(labelPoint1);
        CTPoint p2 = (CTPoint) geogebraElements.get(labelPoint2);
        CTSegment resul = CTSegment.make(p1, p2);
        String label = firstElementWithTag(el, "output").getAttribute("a0");
        resul.objectLabel = label;
        geogebraElements.put(label, resul);
        JMathAnimScene.logger.debug("Generated segment {}", label);
    }

    protected void processLineCommand(Element el) {
        String label = getOutputArgument(el, 0);
        MathObject[] params = getArrayOfParameters(el);
        CTPoint A = (CTPoint) params[0]; // First argument is always a point
        MathObject B = params[1];
        if (B instanceof CTPoint) {// A line given by 2 points
            registerGeogebraElement(label, CTLine.make(A, (CTPoint) B));
            return;
        }
        if (B instanceof HasDirection) {// Line parallel
            registerGeogebraElement(label, CTLine.make(A, (HasDirection) B));
        }
    }

    protected void processRayCommand(Element el) {
        String label = getOutputArgument(el, 0);
        MathObject[] params = getArrayOfParameters(el);
        CTPoint A = (CTPoint) params[0]; // First argument is always a point
        MathObject B = params[1];
        if (B instanceof CTPoint) {// A line given by 2 points
            registerGeogebraElement(label, CTRay.make(A, (CTPoint) B));
            return;
        }
        if (B instanceof HasDirection) {// Line parallel
            registerGeogebraElement(label, CTRay.make(A, (HasDirection) B));
        }
    }

    protected void processVectorCommand(Element el) {
        String label = getOutputArgument(el, 0);

        MathObject[] params = getArrayOfParameters(el);
        CTPoint A;
        CTPoint B;
        if (params.length > 1) {
            A = (CTPoint) params[0];
            B = (CTPoint) params[1];
        } else {
            A = CTPoint.make(Point.origin());
            B = (CTPoint) params[0];
        }

        registerGeogebraElement(label, CTVector.make(A, B));
    }

    protected void processOrthogonalLine(Element el) {
        String label = getOutputArgument(el, 0);
        MathObject[] params = getArrayOfParameters(el);
        CTPoint A = (CTPoint) params[0]; // First argument is always a point
        MathObject B = params[1];
        if (B instanceof HasDirection) {
            registerGeogebraElement(label, CTLineOrthogonal.make(A, (HasDirection) B));
        }
    }

    void processPerpBisector(Element el) {
        String label = getOutputArgument(el, 0);
        MathObject[] params = getArrayOfParameters(el);
        if (params.length == 2) {// 2 points
            CTPoint A = (CTPoint) params[0];
            CTPoint B = (CTPoint) params[1];
            registerGeogebraElement(label, CTPerpBisector.make(A, B));
        }
        //TODO: Implement this. A perpendicular from a segment
//        if (params.length == 1) {// 1 segment
//            ConstrSegment seg = (ConstrSegment) params[0];
//            registerGeogebraElement(label, ConstrPerpBisectorSegment.make(seg));
//        }
    }

    protected void processAngleBisector(Element el) {
        String label = getOutputArgument(el, 0);
        MathObject[] params = getArrayOfParameters(el);
        if (params.length == 3) {// 3 points
            CTPoint A = (CTPoint) params[0];
            CTPoint B = (CTPoint) params[1];
            CTPoint C = (CTPoint) params[2];
            registerGeogebraElement(label, CTAngleBisector.make(A, B, C));
        }
    }

    protected void processPolygonCommand(Element el) {
        String[] outputs = getArrayOfOutputs(el);
        String label = outputs[0];
        MathObject[] objs = getArrayOfParameters(el);
        // Array of points of the polygon
        CTPoint[] points = new CTPoint[objs.length];
        for (int i = 0; i < objs.length; i++) {
            points[i] = (CTPoint) objs[i];
        }
        CTPolygon resul = CTPolygon.make(points);
        geogebraElements.put(label, resul);

        // Now, build all segments
        for (int i = 0; i < points.length; i++) {
            int i2 = (i < outputs.length - 2 ? i + 1 : 0);
            registerGeogebraElement(outputs[i + 1], CTSegment.make(points[i], points[i2]));
        }

    }

    protected void processCircleCommand(Element el) {
        String label = getOutputArgument(el, 0);
        Element elInput = firstElementWithTag(el, "input");
        int numberOfArguments = elInput.getAttributes().getLength();// Number of arguments
        if (numberOfArguments == 3) {// A circle defined by 3 points
            String str0 = elInput.getAttribute("a0");
            String str1 = elInput.getAttribute("a1");
            String str2 = elInput.getAttribute("a2");

            CTPoint arg0 = (CTPoint) parseArgument(str0);
            CTPoint arg1 = (CTPoint) parseArgument(str1);
            CTPoint arg2 = (CTPoint) parseArgument(str2);
            Constructible resul = CTCircle.make(arg0, arg1, arg2);
            registerGeogebraElement(label, resul);
            JMathAnimScene.logger
                    .debug("Imported Geogebra Circle " + label + " by 3 points: " + arg0 + ", " + arg1 + ",  " + arg2);
            return;

        }

        if (numberOfArguments == 2) {
            String str0 = elInput.getAttribute("a0");
            String str1 = elInput.getAttribute("a1");

            MathObject arg0 = parseArgument(str0);
            MathObject arg1 = parseArgument(str1);

            // A circle with center a point and another one in the perimeter
            if ((arg0 instanceof CTPoint) && (arg1 instanceof CTPoint)) {
                CTPoint p0 = (CTPoint) arg0;
                CTPoint p1 = (CTPoint) arg1;
                Constructible resul = CTCircle.make(p0, p1);
                registerGeogebraElement(label, resul);
                JMathAnimScene.logger.debug("Imported Geogebra Circle " + label + ", center " + p0 + ", point " + p1);
                return;
            }
            if ((arg0 instanceof CTPoint) && (arg1 instanceof Scalar)) {
                CTPoint p0 = (CTPoint) arg0;
                Scalar sc0 = (Scalar) arg1;
                Constructible resul = CTCircle.make(p0, sc0);
                registerGeogebraElement(label, resul);
                JMathAnimScene.logger.debug("Imported Geogebra Circle " + label + ", center " + p0 + ", radius " + sc0);
            }

        }
    }

    void processIntersectionCommand(Element el) {
        String label = getOutputArgument(el, 0);
        MathObject[] objs = getArrayOfParameters(el);
        Constructible ob1=(Constructible) objs[0];
        Constructible ob2=(Constructible) objs[1];
        registerGeogebraElement(label, CTIntersectionPoint.make(ob1,ob2));
        JMathAnimScene.logger.debug("Imported intersection point of " + objs[0] + " and " + objs[1]);
    }

}
