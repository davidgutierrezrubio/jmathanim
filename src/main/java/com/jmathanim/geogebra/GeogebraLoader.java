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
package com.jmathanim.geogebra;

import com.jmathanim.Styling.JMColor;
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.Utils.ResourceLoader;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Scalar;
import com.jmathanim.mathobjects.Shape;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class GeogebraLoader implements Iterable<MathObject> {

    private ResourceLoader rl;
    private final URL url;
    private ZipFile zipFile;
    private ZipEntry zipEntry;
    private InputStream inputStream;
    public final HashMap<String, MathObject> geogebraElements;

    private GeogebraLoader(String fileName) {
        geogebraElements = new HashMap<>();
        rl = new ResourceLoader();
        url = rl.getResource(fileName, "geogebra");

    }

    public static GeogebraLoader make(String fileName) {
        GeogebraLoader resul = new GeogebraLoader(fileName);
        resul.parseFile(fileName);
        return resul;
    }

    private void parseFile(String fileName) {
        JMathAnimScene.logger.info("Loading Geogebra file {}", fileName);
        try {
            zipFile = new ZipFile(url.getFile());
            zipEntry = zipFile.getEntry("geogebra.xml");
            inputStream = this.zipFile.getInputStream(zipEntry);
            parseGeogebraContents(inputStream);
        } catch (IOException ex) {
            JMathAnimScene.logger.error("Error trying to load the Geogebra file {} ", fileName);
        } catch (ParserConfigurationException ex) {
            JMathAnimScene.logger.error("Error parsing Geogebra file {} ", fileName);
        } catch (SAXException ex) {
            JMathAnimScene.logger.error("Error parsing Geogebra file {} ", fileName);
        } catch (Exception ex) {
            JMathAnimScene.logger.error("XML File {} doesn't contain a valid Geogebra file ", fileName);
        }
    }

    @Override
    public Iterator<MathObject> iterator() {
        return geogebraElements.values().iterator();
    }

    private void parseGeogebraContents(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException, Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(inputStream);
        doc.getDocumentElement().normalize();

        Element root = doc.getDocumentElement();
        String n = root.getNodeName();
        if (!"geogebra".equals(root.getNodeName())) {
            throw new Exception("XML File doesn't contain a valid Geogebra file");
        }

        //Iterate over all tags. Element and Command tags are the interesting ones
        NodeList nodes = root.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);

            if (node instanceof Element) {
                Element el = (Element) node;
                if (el.getNodeName() == "construction") {
                    parseConstructionChildren(el);
                }

            }
        }

    }

    private void parseConstructionChildren(Element constructionNode) {
        NodeList nodes = constructionNode.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);

            if (node instanceof Element) {
                Element el = (Element) node;
                if (el.getNodeName() == "element") {
                    parseGeogebraElement(el);
                }
                if (el.getNodeName() == "command") {
                    parseGeogebraCommand(el);
                }

            }
        }

    }

    /**
     * Parse a geogebra element tag
     *
     * @param el
     */
    private void parseGeogebraElement(Element el) {
        String type = el.getAttribute("type");
        String label = el.getAttribute("label");
        switch (type) {
            case "point":
                processPoint(el);
                break;
        }

        //If element already belongs to the hashMap, process styling options
        if (geogebraElements.containsKey(label)) {
            geogebraElements.get(label).getMp().copyFrom(parseStylingOptions(el));
        }

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

    private void processCircleCommand(Element el) {
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
            Shape resul = GeogebraCircleThreePoints.make(arg0, arg1, arg2);
            registerGeogebraElement(label, resul);
            JMathAnimScene.logger.debug("Imported Geogebra Circle " + label + " by 3 points: " + arg0 + ", " + arg1+",  "+arg2);
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
                Shape resul = new GeogebraCirclePointPoint(p0, p1);
                registerGeogebraElement(label, resul);
                JMathAnimScene.logger.debug("Imported Geogebra Circle " + label + ", center " + p0 + ", point " + p1);
                return;
            }
            if ((arg0 instanceof Point) && (arg1 instanceof Scalar)) {
                Point p0 = (Point) arg0;
                Scalar sc0 = (Scalar) arg1;
                Shape resul = GeogebraCircleCenterRadius.make(p0, sc0);
                registerGeogebraElement(label, resul);
                JMathAnimScene.logger.debug("Imported Geogebra Circle " + label + ", center " + p0 + ", radius " + sc0);
                return;

            }

        }
    }

    public void registerGeogebraElement(String label, MathObject resul) {
        resul.label = label;
        geogebraElements.put(label, resul);
    }

    private void processPoint(Element el) {
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

    private void parseGeogebraCommand(Element el) {
        String name = el.getAttribute("name");
        switch (name) {
            case "Segment":
                processSegmentCommand(el);
                break;
            case "Polygon":
                processPolygonCommand(el);
                break;
            case "Circle":
                processCircleCommand(el);

                break;
        }
    }

    private void processSegmentCommand(Element el) {

        Element elInput = firstElementWithTag(el, "input");
        String labelPoint1 = elInput.getAttribute("a0");
        String labelPoint2 = elInput.getAttribute("a1");
        Point p1 = (Point) geogebraElements.get(labelPoint1);
        Point p2 = (Point) geogebraElements.get(labelPoint2);
        Shape resul = Shape.segment(p1, p2);
        String label = firstElementWithTag(el, "output").getAttribute("a0");
        resul.label = label;
        geogebraElements.put(label, resul);
        JMathAnimScene.logger.info("Generated segment {}", label);
    }

    private void processPolygonCommand(Element el) {
        Element elInput = firstElementWithTag(el, "input");
        String label = firstElementWithTag(el, "output").getAttribute("a0");

        Shape resul = Shape.polygon((Point[]) getArrayOfParameters(elInput));
        geogebraElements.put(label, resul);
    }

    private MathObject[] getArrayOfParameters(Element elInput) {
        MathObject[] points = new Point[elInput.getAttributes().getLength()];
        for (int i = 0; i < elInput.getAttributes().getLength(); i++) {
            String labelPoint = elInput.getAttribute("a" + i);
            points[i] = (Point) geogebraElements.get(labelPoint);
        }
        return points;
    }

    private Element firstElementWithTag(Element el, String name) {
        if (el.getElementsByTagName(name).getLength() > 0) {
            Element elInput = (Element) el.getElementsByTagName(name).item(0);
            return elInput;
        } else {
            return null;
        }
    }

    private String getInputArgument(Element el, int num) {
        Element elInput = firstElementWithTag(el, "input");
        return elInput.getAttribute("a" + num);
    }

    private String getOutputArgument(Element el, int num) {
        Element elOutput = firstElementWithTag(el, "output");
        return elOutput.getAttribute("a" + num);
    }

    private MODrawProperties parseStylingOptions(Element el) {
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

    public MathObject get(Object key) {
        return geogebraElements.get(key);
    }

}
