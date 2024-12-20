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

import com.jmathanim.Constructible.Conics.CTAbstractCircle;
import com.jmathanim.Constructible.Conics.CTCircle;
import com.jmathanim.Constructible.Conics.CTCircleArc;
import com.jmathanim.Constructible.Conics.CTCircleSector;
import com.jmathanim.Constructible.Conics.CTEllipse;
import com.jmathanim.Constructible.Conics.CTSemiCircle;
import com.jmathanim.Constructible.Conics.CTTransformedCircle;
import com.jmathanim.Constructible.Lines.CTAngleBisector;
import com.jmathanim.Constructible.Lines.CTLine;
import com.jmathanim.Constructible.Lines.CTLineOrthogonal;
import com.jmathanim.Constructible.Lines.CTPerpBisector;
import com.jmathanim.Constructible.Lines.CTPolygon;
import com.jmathanim.Constructible.Lines.CTRay;
import com.jmathanim.Constructible.Lines.CTRegularPolygon;
import com.jmathanim.Constructible.Lines.CTSegment;
import com.jmathanim.Constructible.Lines.CTTangentCircleCircle;
import com.jmathanim.Constructible.Lines.CTTangentPointCircle;
import com.jmathanim.Constructible.Lines.CTTransformedLine;
import com.jmathanim.Constructible.Lines.CTVector;
import com.jmathanim.Constructible.Lines.HasDirection;
import com.jmathanim.Constructible.Others.CTFunctionGraph;
import com.jmathanim.Constructible.Others.CTImage;
import com.jmathanim.Constructible.Points.CTIntersectionPoint;
import com.jmathanim.Constructible.Points.CTMidPoint;
import com.jmathanim.Constructible.Points.CTPoint;
import com.jmathanim.Constructible.Points.CTPointOnObject;
import com.jmathanim.Constructible.Transforms.CTMirrorPoint;
import com.jmathanim.Constructible.Transforms.CTRotatedPoint;
import com.jmathanim.Constructible.Transforms.CTTranslatedPoint;
import com.jmathanim.Styling.JMColor;
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.JMImage;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.NullMathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Scalar;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.w3c.dom.Element;

/**
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
class GeogebraCommandParser {

    protected final HashMap<String, Constructible> geogebraElements;
    protected final HashMap<String, String> expressions;

    public GeogebraCommandParser() {
        this.geogebraElements = new HashMap<>();
        this.expressions = new HashMap<>();
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
        Pattern patternPoint = Pattern.compile("\\((.*),(.*)\\)");
        Matcher matcher = patternPoint.matcher(argument);
        if (matcher.find()) {
            try {
                return CTPoint.make(Point.at(Double.parseDouble(matcher.group(1)), Double.parseDouble(matcher.group(2))));
            } catch (NumberFormatException numberFormatException) {
                JMathAnimScene.logger.warn("Unrecognized number in point coordinates at geogebra import: " + argument + ". Returning (0,0) instead!");
                return CTPoint.at(0, 0);
            }
        }
        //Try if it is a command expressed in command[a,b,c..]
        Pattern patternCmd = Pattern.compile("(.*)\\[.*\\]");
        matcher = patternCmd.matcher(argument);
        if (matcher.find()) {
            JMathAnimScene.logger.error("Don't know still how to parse this command in an argument: " + argument);
            return null;
        }

        //Well it's neither a scalar nor a vector nor a command. Maybe an angle?
        final char[] aa = argument.trim().toCharArray();
        if (aa[aa.length - 1] == '°') {//Yes, it is an angle
            double value = Double.valueOf(argument.substring(0, argument.length() - 1));
            return Scalar.make(value * JMathAnimScene.DEGREES);//Angles are given in degrees in Geogebra
        }

        // Nothing recognized so far, returns null and a warning
        JMathAnimScene.logger.warn("Skipped unrecognized argument: " + argument);
//        try {
//            throw new Exception("Don't know how to parse this argument " + argument);
//        } catch (Exception ex) {
//            Logger.getLogger(GeogebraLoader.class.getName()).log(Level.SEVERE, null, ex);
//        }
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
        if (resul != null) {
            resul.setLabel(label);
            geogebraElements.put(label, resul);
        }
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
        resul.setVisible("true".equalsIgnoreCase(show.getAttribute("object")));

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
        int k = 0;
        MathObject[] objs = new MathObject[elInput.getAttributes().getLength()];
        int numberAttributes = elInput.getAttributes().getLength();
        for (int i = 0; i < numberAttributes; i++) {
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

    public void registerExpression(String label, String expression) {
        expressions.put(label, expression);
    }

    // COMMANDS
    protected void processPoint(Element el) {
        String label = el.getAttribute("label");
        //If this object is already added, do not process it
        //for example if it is the result of an intersection command
        CTPoint resul;
        // Get the coordinates
        Element elCoords = firstElementWithTag(el, "coords");
        double x = Double.valueOf(elCoords.getAttribute("x"));
        double y = Double.valueOf(elCoords.getAttribute("y"));
        if (!geogebraElements.containsKey(label)) {
            resul = CTPoint.make(Point.at(x, y));
        } else {
            resul = (CTPoint) geogebraElements.get(label);
            resul.moveTo(x, y);
        }
        Element pointSize = firstElementWithTag(el, "pointSize");
        double th = Double.valueOf(pointSize.getAttribute("val")) * 30;
        // TODO: Add a z value here

        resul.thickness(th);

        Element pointStyle = firstElementWithTag(el, "pointStyle");
        Integer dotStyleCode = Integer.valueOf(pointStyle.getAttribute("val"));
        Point.DotSyle dotStyle;
        switch (dotStyleCode) {
            case 1:
                dotStyle = Point.DotSyle.CROSS;
                break;
            case 3:
                dotStyle = Point.DotSyle.PLUS;
                break;
            default:
                dotStyle = Point.DotSyle.CIRCLE;
        }
        resul.dotStyle(dotStyle);
        resul.objectLabel = label;
        registerGeogebraElement(label, resul);
        JMathAnimScene.logger.debug("Imported Geogebra point {}", label);
    }

    void processImageElement(Element el, ZipFile zipFile) {
        String label = el.getAttribute("label");
        final Element fileEl = firstElementWithTag(el, "file");
        if (fileEl == null) return;
        ZipEntry entry = zipFile.getEntry(fileEl.getAttribute("name"));
        try {
            InputStream fileStream = zipFile.getInputStream(entry);
            JMImage img = new JMImage(fileStream);
            Element elStartPoint1 = (Element) el.getElementsByTagName("startPoint").item(0);
            CTPoint A = (CTPoint) geogebraElements.get(elStartPoint1.getAttribute("exp"));
            Element elStartPoint2 = (Element) el.getElementsByTagName("startPoint").item(1);
            CTPoint B = (CTPoint) geogebraElements.get(elStartPoint2.getAttribute("exp"));
            registerGeogebraElement(label, CTImage.make(A, B, img));

        } catch (IOException ex) {
            JMathAnimScene.logger.error("Could'nt load file for image " + label);
        }

    }

    void processLaTeXObjectElement(Element el) {
        CTPoint anchorPoint;
        double size;
        String label = el.getAttribute("label");
        String text = expressions.get(label);
        text = text.replace("\"", "");
        final Element isLatex = firstElementWithTag(el, "isLaTeX");
        if (isLatex != null) {
            if ("TRUE".equalsIgnoreCase(isLatex.getAttribute("val"))) {
                text = "$" + text + "$";
            }
        }

        //startPoint item defines anchor point (lower left)
        //with attributes x,y...it is a new point
        //with attribute exp it is an existing point
        Element startPointElement = firstElementWithTag(el, "startPoint");
        if (startPointElement != null) {
            String labelAnchorPoint = startPointElement.getAttribute("exp");
            if ("".equals(labelAnchorPoint)) {//Point doesn't exist, create a new one
                double x = Double.parseDouble(startPointElement.getAttribute("x"));
                double y = Double.parseDouble(startPointElement.getAttribute("y"));
                anchorPoint = CTPoint.make(Point.at(x, y));
            } else {
                anchorPoint = (CTPoint) geogebraElements.get(labelAnchorPoint);
            }
        } else {
            anchorPoint = CTPoint.make(Point.random());
        }
        //Size
        Element fontElement = firstElementWithTag(el, "font");
        if (fontElement != null) {
            //TODO: Adjust import scale. Guess correct size
            size = Double.parseDouble(fontElement.getAttribute("size")) / 36;
        } else {
            size = 5d / 36;//Assume size is "small"
        }

        CTLaTeX cTLaTeX = CTLaTeX.make(text, anchorPoint, Anchor.Type.ULEFT, 0).scale(size);
        registerGeogebraElement(label, cTLaTeX);
    }

    protected void processFunctionElement(Element el) {
        String label = el.getAttribute("label");
        String text = expressions.get(label);
        registerGeogebraElement(label, CTFunctionGraph.make(text));
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
        registerGeogebraElement(label, resul);
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

        registerGeogebraElement(label, CTVector.makeVector(A, B));
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
            registerGeogebraElement(label, CTPerpBisector.makePerpBisector(A, B));
        }
        //TODO: Implement this. A perpendicular from a segment
        if (params.length == 1) {// 1 segment
            CTSegment seg = (CTSegment) params[0];
            registerGeogebraElement(label, CTPerpBisector.make(seg));
        }
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
        MathObject[] objs = getArrayOfParameters(el);
        // Array of points of the polygon
        //If a2 is a Scalar, is a regular polygon
        if (objs[2] instanceof Scalar) {
            processRegularPolygonCommand(el);
        } else {
            processSimplePolygonCommand(el);
        }

    }

    protected void processSimplePolygonCommand(Element el) {
        String[] outputs = getArrayOfOutputs(el);
        String label = outputs[0];
        MathObject[] objs = getArrayOfParameters(el);
        CTPoint[] points = new CTPoint[objs.length];
        for (int i = 0; i < objs.length; i++) {
            points[i] = (CTPoint) objs[i];
        }
        CTPolygon resul = CTPolygon.make(points);
        registerGeogebraElement(label, resul);

        // Now, build all segments
        for (int i = 0; i < points.length; i++) {
            int i2 = (i < outputs.length - 2 ? i + 1 : 0);
            registerGeogebraElement(outputs[i + 1], CTSegment.make(points[i], points[i2]));
        }
    }

    protected void processRegularPolygonCommand(Element el) {
        String[] outputs = getArrayOfOutputs(el);

        MathObject[] objs = getArrayOfParameters(el);
        final double dSides = ((Scalar) objs[2]).value;
        int sides = (int) dSides;
        ArrayList<CTSegment> segments = new ArrayList<>();
        ArrayList<CTPoint> vertices = new ArrayList<>();
        //For a regular polygon of n sides, there are 1+n+n-2 (from 0 to 2n-2) outputs:
        //0 output name
        //1...n name of sides (CTSegment)
        //n+1...2n-2 names of generated vertices (apart from 2 given vertices in input)
        String label = outputs[0];

        //First, add the 2 defining points
        vertices.add((CTPoint) objs[0]);
        vertices.add((CTPoint) objs[1]);
        for (int k = sides + 1; k <= 2 * sides - 2; k++) {
            CTPoint P = CTPoint.make(new Point());//Should be computed in the constructor
            vertices.add(P);
            registerGeogebraElement(outputs[k], P);
            JMathAnimScene.logger.debug("Generated Point {}", outputs[k]);
        }

        for (int k = 1; k <= sides; k++) {
            final CTSegment seg = CTSegment.make(vertices.get(k - 1), vertices.get(k % sides));
            segments.add(seg);
            registerGeogebraElement(outputs[k], seg);
            JMathAnimScene.logger.debug("Generated segment {}", outputs[k]);
        }

        registerGeogebraElement(label, CTRegularPolygon.makeFromPointList(vertices));

        JMathAnimScene.logger.debug("Imported Geogebra regular polygon " + label);
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
            Constructible resul = CTCircle.make3Points(arg0, arg1, arg2);
            registerGeogebraElement(label, resul);
            JMathAnimScene.logger.debug("Imported Geogebra Circle " + label + " by 3 points: " + arg0 + ", " + arg1 + ",  " + arg2);
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
                Constructible resul = CTCircle.makeCenterPoint(p0, p1);
                registerGeogebraElement(label, resul);
                JMathAnimScene.logger.debug("Imported Geogebra Circle " + label + ", center " + p0 + ", point " + p1);
                return;
            }
            if ((arg0 instanceof CTPoint) && (arg1 instanceof Scalar)) {
                CTPoint p0 = (CTPoint) arg0;
                Scalar sc0 = (Scalar) arg1;
                Constructible resul = CTCircle.makeCenterRadius(p0, sc0);
                registerGeogebraElement(label, resul);
                JMathAnimScene.logger.debug("Imported Geogebra Circle " + label + ", center " + p0 + ", radius " + sc0);
            }

        }
    }

    void processTangentCommand(Element el) {
        String label = getOutputArgument(el, 0);
        MathObject[] objs = getArrayOfParameters(el);
        Constructible ob1 = (Constructible) objs[0];
        Constructible ob2 = (Constructible) objs[1];
        //If Point-Circle: 2 tangent lines
        //If Circle-Circle: 4 tangent lines (exterior and interior ones)

        if (ob1 instanceof CTPoint) {
            CTPoint point = (CTPoint) ob1;
            CTAbstractCircle circle = (CTAbstractCircle) ob2;
            registerGeogebraElement(label, CTTangentPointCircle.make(point, circle, 0));
            registerGeogebraElement(getOutputArgument(el, 1), CTTangentPointCircle.make(point, circle, 1));
        }

        if ((ob1 instanceof CTAbstractCircle) && (ob2 instanceof CTAbstractCircle)) {
            CTAbstractCircle c1 = (CTAbstractCircle) ob1;
            CTAbstractCircle c2 = (CTAbstractCircle) ob2;
            registerGeogebraElement(label, CTTangentCircleCircle.make(c1, c2, 0));
            registerGeogebraElement(getOutputArgument(el, 1), CTTangentCircleCircle.make(c1, c2, 1));
            registerGeogebraElement(getOutputArgument(el, 2), CTTangentCircleCircle.make(c1, c2, 2));
            registerGeogebraElement(getOutputArgument(el, 3), CTTangentCircleCircle.make(c1, c2, 3));
        }

    }

    void processIntersectionCommand(Element el) {
        int numPoint = 0;
        String label = getOutputArgument(el, 0);
        MathObject[] objs = getArrayOfParameters(el);

        long nonNullArgs = Arrays.stream(objs).filter(obj -> obj != null).count();

        Constructible ob1 = (Constructible) objs[0];
        Constructible ob2 = (Constructible) objs[1];
        if (nonNullArgs > 2) {//Third parameter, intersection number
            //if a2="n" it computes only the n-th intersection point
            //For line(A,B)-circle, "1" stands for closest point to A, "2" for farthest
            numPoint = (int) ((Scalar) objs[2]).value;
            registerGeogebraElement(label, CTIntersectionPoint.make(ob1, ob2, numPoint - 1));
        }
        if (nonNullArgs == 2) {
            if ((ob1 instanceof CTCircle) || (ob2 instanceof CTCircle)) {
                registerGeogebraElement(label, CTIntersectionPoint.make(ob1, ob2, 0));
                registerGeogebraElement(getOutputArgument(el, 1), CTIntersectionPoint.make(ob1, ob2, 1));
            } else {
                registerGeogebraElement(label, CTIntersectionPoint.make(ob1, ob2, 0));
            }
        }

        JMathAnimScene.logger.debug("Imported Geogebra intersection point " + label + " of " + objs[0] + " and " + objs[1]);
    }

    void processPointOnObject(Element el) {
        //TODO: Implement PointIn command for points inside a region (polygon, circle...)
        String label = getOutputArgument(el, 0);
        MathObject[] objs = getArrayOfParameters(el);
        try {
            PointOwner ob1 = (PointOwner) objs[0];
            final CTPointOnObject p = CTPointOnObject.make(ob1);
            registerGeogebraElement(label, p);
            JMathAnimScene.logger.debug("Imported Geogebra point " + label + " on object " + objs[0]);
        } catch (ClassCastException e) {
            JMathAnimScene.logger.warn("Object type " + objs[0].getClass().getName() + " not implement yet to hold a point on object, sorry");
        }

    }

    void processEllipse(Element el) {
        String label = getOutputArgument(el, 0);
        Element elInput = firstElementWithTag(el, "input");
        int numberOfArguments = elInput.getAttributes().getLength();// Number of arguments
        if (numberOfArguments == 3) {// A circle defined by 3 points
            String str0 = elInput.getAttribute("a0");
            String str1 = elInput.getAttribute("a1");
            String str2 = elInput.getAttribute("a2");

            CTPoint focus1 = (CTPoint) parseArgument(str0);
            CTPoint focus2 = (CTPoint) parseArgument(str1);
            CTPoint A = (CTPoint) parseArgument(str2);
            CTEllipse resul = CTEllipse.make(focus1, focus2, A);
            registerGeogebraElement(label, resul);
            JMathAnimScene.logger.debug("Imported Geogebra Ellipse" + label + " by 3 points: " + focus1 + ", " + focus2 + ",  " + A);
        }
    }

    void processMirror(Element el) {//Right now, it only mirror points
        String label = getOutputArgument(el, 0);
        MathObject[] objs = getArrayOfParameters(el);
        //TODO: An image (CTImage) can also be mirrored for example.
        //Trying to import a mirrored image leads a to cast exception
        if (objs[0] instanceof CTPoint) {
            CTPoint pointToMirror = (CTPoint) objs[0];
            Constructible mirrorAxis = (Constructible) objs[1];
            registerGeogebraElement(label, CTMirrorPoint.make(pointToMirror, mirrorAxis));
            JMathAnimScene.logger.debug("Imported Geogebra mirror point " + label + " of " + objs[0] + " with axis " + objs[1]);
        }
        if (objs[0] instanceof CTLine) {
            CTLine lineToMirror = (CTLine) objs[0];
            Constructible mirrorAxis = (Constructible) objs[1];
            if (mirrorAxis instanceof CTLine) {
                Constructible resul = CTTransformedLine.makeAxisReflectionLine(lineToMirror, (CTLine) mirrorAxis);
                registerGeogebraElement(label, resul);
                JMathAnimScene.logger.debug("Imported Geogebra xis mirror line " + label + " of " + objs[0] + " with axis " + objs[1]);
            }
            if (mirrorAxis instanceof CTPoint) {
                Constructible resul = CTTransformedLine.makePointReflectionLine(lineToMirror, (CTPoint) mirrorAxis);
                registerGeogebraElement(label, resul);
                JMathAnimScene.logger.debug("Imported Geogebra central mirror of line " + label + " of " + objs[0] + " with axis " + objs[1]);
            }

        }

    }

    void processTranslate(Element el) {
        String label = getOutputArgument(el, 0);
        MathObject[] objs = getArrayOfParameters(el);
        if (objs[0] instanceof CTPoint) {
            CTPoint pointToTranslate = (CTPoint) objs[0];
            CTVector translateVector = (CTVector) objs[1];
            registerGeogebraElement(label, CTTranslatedPoint.make(pointToTranslate, translateVector));
            JMathAnimScene.logger.debug("Imported Geogebra translate point " + label + " of " + objs[0] + " with vector " + objs[1]);
        }
        if (objs[0] instanceof CTLine) {
            CTLine cTLineToTranslate = (CTLine) objs[0];
            CTVector translateVector = (CTVector) objs[1];
            registerGeogebraElement(label, CTTransformedLine.makeTranslatedLine(cTLineToTranslate, translateVector));
            JMathAnimScene.logger.debug("Imported Geogebra translated line " + label + " of " + objs[0] + " with vector " + objs[1]);

        }
    }

    void processRotate(Element el) {
        String label = getOutputArgument(el, 0);
        MathObject[] objs = getArrayOfParameters(el);
        MathObject aa = objs[0];
        if (objs[0] instanceof CTPoint) {
            CTPoint pointToRotate = (CTPoint) objs[0];
            Scalar angle = (Scalar) objs[1];
            CTPoint rotationCenter = (CTPoint) objs[2];
            registerGeogebraElement(label, CTRotatedPoint.make(pointToRotate, rotationCenter, angle));
            JMathAnimScene.logger.debug("Imported Geogebra rotated point " + label + " of " + objs[0] + " with angle " + objs[1]);
        }
        if (objs[0] instanceof CTLine) {
            CTLine lineToRotate = (CTLine) objs[0];
            Scalar angle = (Scalar) objs[1];
            CTPoint rotationCenter = (CTPoint) objs[2];
            registerGeogebraElement(label, CTTransformedLine.makeRotatedLine(lineToRotate, rotationCenter, angle));
            JMathAnimScene.logger.debug("Imported Geogebra rotated point " + label + " of " + objs[0] + " with angle " + objs[1]);
        }
        if (aa instanceof CTCircle) {
            CTCircle circleToRotate = (CTCircle) objs[0];
            Scalar angle = (Scalar) objs[1];
            CTPoint rotationCenter = (CTPoint) objs[2];
            registerGeogebraElement(label, CTTransformedCircle.makeRotatedCircle(circleToRotate, rotationCenter, angle));
            JMathAnimScene.logger.debug("Imported Geogebra rotated point " + label + " of " + objs[0] + " with angle " + objs[1]);
        }

    }

    void processMidPoint(Element el) {
        String label = getOutputArgument(el, 0);
        MathObject[] objs = getArrayOfParameters(el);
        //If there are 2 elements, they must be 2 points
        if (objs.length == 2) {
            CTPoint A = (CTPoint) objs[0];
            CTPoint B = (CTPoint) objs[1];
            registerGeogebraElement(label, CTMidPoint.make(A, B));
        }
        if (objs.length == 1) {//It must be a segment
            CTSegment segment = (CTSegment) objs[0];
            registerGeogebraElement(label, CTMidPoint.make(segment));
        }
    }

    void processNumericElement(Element el) {
        String label = el.getAttribute("label");
        Element elCoords = firstElementWithTag(el, "value");
        double value = Double.valueOf(elCoords.getAttribute("val"));
        registerGeogebraElement(label, Scalar.make(value));
        JMathAnimScene.logger.debug("Imported Geogebra scalar value " + label + "=" + value);

    }

    void processSemicircle(Element el) {
        String label = getOutputArgument(el, 0);
        MathObject[] objs = getArrayOfParameters(el);
        CTPoint A = (CTPoint) objs[0];
        CTPoint B = (CTPoint) objs[1];
        registerGeogebraElement(label, CTSemiCircle.make(A, B));
    }

    void processCircleArc(Element el) {
        String label = getOutputArgument(el, 0);
        MathObject[] objs = getArrayOfParameters(el);
        CTPoint A = (CTPoint) objs[0];
        CTPoint B = (CTPoint) objs[1];
        CTPoint C = (CTPoint) objs[2];
        registerGeogebraElement(label, CTCircleArc.make(A, B, C));
    }

    void processCircleSector(Element el) {
        String label = getOutputArgument(el, 0);
        MathObject[] objs = getArrayOfParameters(el);
        CTPoint A = (CTPoint) objs[0];
        CTPoint B = (CTPoint) objs[1];
        CTPoint C = (CTPoint) objs[2];
        registerGeogebraElement(label, CTCircleSector.make(A, B, C));
    }
}
