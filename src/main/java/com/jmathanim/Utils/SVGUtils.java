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

import com.jmathanim.Enum.GradientCycleMethod;
import com.jmathanim.MathObjects.Coordinates;
import com.jmathanim.MathObjects.Shape;
import com.jmathanim.MathObjects.Shapes.JMPath;
import com.jmathanim.MathObjects.Shapes.JMPathPoint;
import com.jmathanim.Styling.GradientStop;
import com.jmathanim.Styling.JMColor;
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.jmathanim.LogUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.jmathanim.jmathanim.JMathAnimScene.logger;

/**
 * A class with useful methods to handle SVG files
 */
public class SVGUtils {

    private static final double CONTROL_POINT_RATIO = 2d / 3;


    private static String sanitizeTokens(String tok) {
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
        return tok2;
    }

    public static String sanitizeCommandsString(String input) {
        return input.replace("-", " -") // Avoid errors with strings like "142.11998-.948884"
                .replace("e -", "e-") // Avoid errors with numbers in scientific format
                .replace("E -", "E-") // Avoid errors with numbers in scientific format
                .replaceAll("([MmHhVvCcSsLlZzQqAa])", " $1 ") // Add spaces before and after all SVG commands
                .replaceAll(",", " ") // Replace all commas with spaces
                .replaceAll("^ +| +$|( )+", "$1")
                .replaceAll("(\\d*\\.\\d+)(?=(\\.\\d+))", "$1 ");
    }

    private static String sanitizeString(String input) {
        // Avoid errors with strings like "142.11998-.948884"
        // Avoid errors with numbers in scientific format
        // Avoid errors with numbers in scientific format
        // Add spaces before and after all SVG commands
        // Replace all commas with spaces
        // Remove duplicate spaces
        return input.replace("-", " -") // Avoid errors with strings like "142.11998-.948884"
                .replace("e -", "e-") // Avoid errors with numbers in scientific format
                .replace("E -", "E-") // Avoid errors with numbers in scientific format
                .replaceAll("([MmHhVvCcSsLlZzQqAa])", " $1 ") // Add spaces before and after all SVG commands
                .replaceAll(",", " ") // Replace all commas with spaces
                .replaceAll("^ +| +$|( )+", "$1");
    }


    /**
     * Convert Parameters fx,fy from svg radial radient to javafx proper ones
     * @param cx Center x
     * @param cy Center y
     * @param r Radius
     * @param fx Center of focus x
     * @param fy Center of focus y
     * @return An array with focusAngle and focusDistance
     */
    public static double[] convertSvgFocusToJfxFocus(double cx, double cy, double r, double fx, double fy) {
        double deltaX = fx - cx;
        double deltaY = fy - cy;
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        double focusDistance = distance / r; // Normalizar al radio
        focusDistance = Math.min(1.0, Math.max(0.0, focusDistance));
        double angleRad = Math.atan2(-deltaY, deltaX);
        double focusAngle = Math.toDegrees(angleRad);

        // Normalizar el ángulo a [0, 360) si es necesario (atan2 devuelve [-180, 180])
        if (focusAngle < 0) {
            focusAngle += 360.0;
        }

        return new double[]{focusAngle, focusDistance};
    }



    static void processGradientStops(GradientStop stops, Element gradientElement) {
        NodeList nList = gradientElement.getChildNodes();
        // localMP holds the base MODrawProperties to apply to all childs
        MODrawProperties mpCopy;
        int length = nList.getLength();
        for (int nchild = 0; nchild < length; nchild++) {
            Node node = nList.item(nchild);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) node;
                switch (el.getTagName()) {
                    case "stop":
                        processGradientStop(stops, el);
                }
            }
        }
    }
    public static void processGradientStop(GradientStop stops, Element el) {
        double offset = parseStringValueWithPercentageNumber(el.getAttribute("offset"));
        JMColor color=JMColor.rgba(1,1,1,1);

        String styleString = el.getAttribute("styleString");
        if (!styleString.isEmpty()) {
             color = parseStopStyle(styleString);
        }
        String stopColor = el.getAttribute("stop-color");
        if (!stopColor.isEmpty()) {
            color = JMColor.parse(stopColor);
        }
        String stopOpacity = el.getAttribute("stop-opacity");
        if (!stopOpacity.isEmpty()) {
            color.setAlpha(parseStringValueWithPercentageNumber(stopOpacity));
        }
        stops.add(offset, color);

    }
    public static JMColor parseStopStyle(String styleString) {
        if (styleString == null || styleString.trim().isEmpty()) {
            logger.warn("Color format incorrect for <stop> element. Returning BLACK color instead");
            return JMColor.rgba(0,0,0,1);
        }
        styleString=styleString.replaceAll(" ", "");

        // 1. Usar Regex para encontrar los 4 grupos de números (R, G, B, O)
        // Patrón: stop-color:rgb(NUM1,NUM2,NUM3);stop-opacity:NUM4
        // [\d\.]+ es usado para capturar números, incluyendo decimales.
        Pattern pattern = Pattern.compile("rgb\\((\\d+),(\\d+),(\\d+)\\);stop-opacity:([\\d\\.]+)");
        Matcher matcher = pattern.matcher(styleString.trim());

        if (matcher.find()) {
            double[] result = new double[4];

            // Los números RGB son enteros, la opacidad es un double
            // Los grupos de la regex corresponden a:
            // Grupo 1: R
            // Grupo 2: G
            // Grupo 3: B
            // Grupo 4: Opacidad

            // R, G, B
            for (int i = 0; i < 3; i++) {
                result[i] = Double.parseDouble(matcher.group(i + 1));
            }

            // Opacidad
            result[3] = Double.parseDouble(matcher.group(4));

            return JMColor.rgba(result[0]/255, result[1]/255, result[2]/255, result[3]);

        } else {
           logger.warn("Color format incorrect for <stop> element. Returning default color instead");
           return JMColor.rgba(0,0,0,1);
        }
    }


    public static GradientCycleMethod getSvgSpreadMethod(String cycle) {
        cycle=cycle.toLowerCase().trim();
        switch (cycle) {
            case "pad":
            case "":
                return GradientCycleMethod.NO_CYCLE;
            case "reflect":
                return GradientCycleMethod.REFLECT ;
            case "repeat":
                return GradientCycleMethod.REPEAT;
            default:
               logger.warn("Gradient cycle not recognized:"+ LogUtils.method("<" + cycle + ">")+
                       ". Using "+LogUtils.method("NO_CYCLE")+" instead");
                return GradientCycleMethod.NO_CYCLE;
        }
    }



    public static double parseStringValueWithPercentageNumber(String valorString) {
        if (valorString == null || valorString.trim().isEmpty()) {
          return 0;
        }
        String trimmedValue = valorString.trim();
        if (trimmedValue.endsWith("%")) {
            String percentageString = trimmedValue.substring(0, trimmedValue.length() - 1);
            double percentage = Double.parseDouble(percentageString);
            return percentage / 100.0;
        } else {
            return Double.parseDouble(trimmedValue);
        }
    }





    public static ArrayList<String> getPointTokens(String s) {
        String t = sanitizeString(s);
        String[] tokens_1 = t.split(" ");
        ArrayList<String> tokens = new ArrayList<>();
        for (String tok : tokens_1) {
            String tok2 = sanitizeTokens(tok);
            tokens.addAll(Arrays.asList(tok2.split(" ")));
        }
        return tokens;
    }


    public static double[] calculateControlPoint(double pointX, double pointY, double controlX, double controlY) {
        double derivedX = pointX + CONTROL_POINT_RATIO * (controlX - pointX);
        double derivedY = pointY + CONTROL_POINT_RATIO * (controlY - pointY);
        return new double[]{derivedX, derivedY};
    }




    // Adds a simple point to the path, with control points equal to the point
    public static JMPathPoint pathLineTo(JMPath path, double currentX, double currentY, boolean isVisible) {
        JMPathPoint point = new JMPathPoint(Vec.to(currentX, currentY), isVisible);
        point.setSegmentToThisPointCurved(false);
        point.getVExit().x = currentX;
        point.getVExit().y = currentY;
        point.getVEnter().x = currentX;
        point.getVEnter().y = currentY;
        path.addJMPoint(point);
        return point;
    }







    /**
     * Writes an XML DOM object to an XML File
     *
     * @param rootElement Root element
     * @param fileName    File name
     * @throws Exception
     */
    private static void writeElementToXMLFile(Element rootElement, String fileName) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document document = docBuilder.newDocument();

        // Importa gradientElement elemento al nuevo documento
        Node importedNode = document.importNode(rootElement, true);
        document.appendChild(importedNode);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        // Opcional: Establecer opciones de formato
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        DOMSource source = new DOMSource(document);

        StreamResult result = new StreamResult(fileName);

        transformer.transform(source, result);
    }

    /**
     * Compute SVG Arc
     *
     * @param originPoint  Origin Point
     * @param rx           Radius-X
     * @param ry           Radius-Y
     * @param axisRotation Rotation axis
     * @param large        Large flag (0,1)
     * @param sweep        Sweep flag (0,1)
     * @param destinyPoint Destiny Point
     * @return The created curve
     */
    public static Shape computeSVGArc(Coordinates<?> originPoint, double rx, double ry, double axisRotation, int large, int sweep, Coordinates<?> destinyPoint) {
        Vec O1 = (large == sweep ? originPoint.getVec().copy() : destinyPoint.getVec().copy());
        Vec O2 = (large == sweep ? destinyPoint.getVec().copy() : originPoint.getVec().copy());
        AffineJTransform tr = AffineJTransform.create2DRotationTransform(O1, axisRotation);

        double rad;
        if (rx < ry) {
            tr = tr.compose(AffineJTransform.createScaleTransform(O1, ry / rx, 1, 1));
            rad = ry;
        } else {
            tr = tr.compose(AffineJTransform.createScaleTransform(O1, 1, rx / ry, 1));
            rad = rx;
        }
        O2.applyAffineTransform(tr);
        //If radius is too small, upscale it
        double halfDistanceO1O2 = O1.to(O2).norm() * .5;
        if (rad < halfDistanceO1O2) {
            rad = halfDistanceO1O2;
        }
        Shape resul = Shape.arc(O1, O2, rad, (large == 0));
        resul.applyAffineTransform(tr.getInverse());
        return resul;
    }

    public static String maintainNumbersOnlyFromString(String input) {
        return input.replaceAll("[^0-9.]", "");
    }
}


