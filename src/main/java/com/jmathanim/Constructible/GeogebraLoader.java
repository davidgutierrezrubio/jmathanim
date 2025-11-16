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

import com.jmathanim.Cameras.hasCameraParameters;
import com.jmathanim.Constructible.Lines.CTLine;
import com.jmathanim.MathObjects.MathObject;
import com.jmathanim.MathObjects.Shapes.Line;
import com.jmathanim.Styling.PaintStyle;
import com.jmathanim.Utils.ResourceLoader;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.jmathanim.LogUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class GeogebraLoader implements Iterable<Constructible<?>>, hasCameraParameters {

    private final ResourceLoader rl;
    private URL url;
    private final String fileName;
    private ZipFile zipFile;
    private ZipEntry zipEntry;
    private InputStream inputStream;
    private final GeogebraCommandParser cp;
    private double xmax;
    private double xmin;
    private double yCenter;

    private GeogebraLoader(String fileName) {
        this.fileName=fileName;
        rl = new ResourceLoader();
        try {
            url = rl.getExternalResource(fileName, "geogebra");
        } catch (FileNotFoundException e) {
           JMathAnimScene.logger.warn("Geogebra file "+ LogUtils.CYAN+fileName+LogUtils.RESET+" not found");
        }
        this.cp = new GeogebraCommandParser();
        cp.registerGeogebraElement("xAxis", CTLine.make(Line.XAxis()).visible(false));
        cp.registerGeogebraElement("yAxis", CTLine.make(Line.YAxis()).visible(false));
    }

    /**
     * Creates a new GeogebraLoader object and parses contents of given Geogebra
     * file
     *
     * @param fileName Filename. Special characters of resource loading are
     * used. By default, the work folder is resources/geogebra.
     * @return The created object
     */
    public static GeogebraLoader parse(String fileName) {
        GeogebraLoader resul = new GeogebraLoader(fileName);
        resul.parseFile();
        return resul;
    }

    private void parseFile() {
        try {
            File file = new File(url.getFile());
            Path filePath = file.toPath();
            if (!Files.exists(filePath)) {
                JMathAnimScene.logger.error("File not found: "+LogUtils.fileName(file.toString()));
                return;
            }
            JMathAnimScene.logger.info("Loading Geogebra file "+LogUtils.fileName(fileName));
            zipFile = new ZipFile(url.getFile());
            zipEntry = zipFile.getEntry("geogebra.xml");
            if (zipEntry == null) {
                JMathAnimScene.logger.error("Error: Geogebra file "+LogUtils.fileName(fileName)+" does not contain entry 'geogebra.xml'.");
                return;
            }
            inputStream = this.zipFile.getInputStream(zipEntry);
            parseGeogebraContents(inputStream);
        } catch (IOException ex) {
            JMathAnimScene.logger.warn("Error importing Geogebra file");
        }

    }

    private void parseGeogebraContents(InputStream inputStream) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        Document doc = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(inputStream);
            doc.getDocumentElement().normalize();
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(GeogebraLoader.class.getName()).log(Level.SEVERE, null, ex);
        }

        Element root = doc.getDocumentElement();
        if (!"geogebra".equals(root.getNodeName())) {
            try {
                throw new Exception("XML File doesn't contain a valid Geogebra file");
            } catch (Exception ex) {
                Logger.getLogger(GeogebraLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // Iterate over all tags. 
        NodeList nodes = root.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);

            if (node instanceof Element) {
                Element el = (Element) node;
                switch (el.getNodeName()) {
                    case "construction":
                        parseConstructionChildren(el);//Element and Command tags are the interesting ones here
                        break;
                    case "euclidianView"://Data of the current view
                        parseEuclidianView(el);
                        break;
                }
            }
        }

    }

    /**
     * This method parse all Element and Command tags inside the construction
     * node
     *
     * @param constructionNode The construction node
     */
    private void parseConstructionChildren(Element constructionNode) {
        NodeList nodes = constructionNode.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);

            if (node instanceof Element) {
                Element el = (Element) node;
                switch (el.getNodeName()) {
                    case "element":
                        parseGeogebraElement(el);
                        break;
                    case "command":
                        parseGeogebraCommand(el);
                        break;
                    case "expression":
                        parseExpression(el);
                        break;
                }
            }
        }
    }

    private void parseExpression(Element el) {
        String expression = el.getAttribute("exp");
        String label = el.getAttribute("label");
        cp.registerExpression(label, expression);
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
                cp.processPoint(el);
                break;
            case "text":
                cp.processLaTeXObjectElement(el);
                break;
            case "image":
                cp.processImageElement(el, zipFile);
            case "numeric":
                cp.processNumericElement(el);
                break;
            case "function":
                cp.processFunctionElement(el);
                break;
        }

        // If element already belongs to the hashMap, process styling options
        if (cp.containsKey(label)) {
            Constructible<?> ob = cp.get(label);
            ob.getMp().copyFrom(cp.parseStylingOptions(el));
            //Workaround: LaTeX Geogebra objects have the same fill and draw color
            if (ob instanceof CTLaTeX) {
                PaintStyle<?> col = ob.getMp().getDrawColor();
                ob.getMp().setFillColor(col);
            }
        } else {
            JMathAnimScene.logger.warn("Element " + label + " in Geogebra file without Command tag");
        }

    }

    /**
     * Parse a Geogebra command. This command defines how the object is built.
     * The element tag that will be read later includes styling data
     *
     * @param el
     */
    private void parseGeogebraCommand(Element el) {
        String name = el.getAttribute("name");
        switch (name) {
            case "Segment":
                cp.processSegmentCommand(el);
                break;
            case "Polygon":
                cp.processPolygonCommand(el);
                break;
            case "Circle":
                cp.processCircleCommand(el);
                break;
            case "Line":
                cp.processLineCommand(el);
                break;
            case "OrthogonalLine":
                cp.processOrthogonalLine(el);
                break;
            case "LineBisector":
                cp.processPerpBisector(el);
                break;
            case "AngularBisector":
                cp.processAngleBisector(el);
                break;
            case "Ray":
                cp.processRayCommand(el);
                break;
            case "Vector":
                cp.processVectorCommand(el);
                break;
            case "Intersect":
                cp.processIntersectionCommand(el);
                break;
            case "Tangent":
                cp.processTangentCommand(el);
                break;
            case "Point": //A Point on object
                cp.processPointOnObject(el);
                break;
            case "Ellipse":
                cp.processEllipse(el);
                break;
            case "Semicircle":
                cp.processSemicircle(el);
                break;
            case "CircleArc":
                cp.processCircleArc(el);
                break;
            case "CircleSector":
                cp.processCircleSector(el);
                break;
            case "Mirror":
                cp.processMirror(el);
                break;
            case "Translate":
                cp.processTranslate(el);
                break;
            case "Rotate":
                cp.processRotate(el);
                break;
            case "Midpoint":
                cp.processMidPoint(el);
                break;
            //TODO: A lot of commands to implement still
            default:
                JMathAnimScene.logger.warn("Geogebra element " + name + " not implemented yet, sorry.");
        }
    }

    /**
     * Get Constructible object with given name
     *
     * @param key Name of object. Should be the same as in the Geogebra file
     * @return The Constructible object imported
     */
    public Constructible<?> get(String key) {
            return cp.get(key);
    }

    /**
     * Get all Constructible objects imported
     *
     * @return An array with all objects imported
     */
    public Constructible<?>[] getObjects() {
        return cp.geogebraElements.values().toArray(new Constructible[0]);
    }

    /**
     * Get the dictionary with imported Geogebra elements. This dictionary has
     * the name of objects as keys and the objects as values.
     *
     * @return The dictionary
     */
    public HashMap<String, Constructible<?>> getDict() {
        return cp.geogebraElements;
    }


    @Override
    public Iterator<Constructible<?>> iterator() {
        return cp.geogebraElements.values().iterator();
    }

    private void parseEuclidianView(Element euclidianViewNode) {
        NodeList nodes = euclidianViewNode.getChildNodes();
        double width = 4, height = 2.25;
        double xZero = 0, yZero = 0, xScale = 1, yScale = 1;
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);

            if (node instanceof Element) {
                Element el = (Element) node;
                switch (el.getNodeName()) {
                    case "size":
                        width = Double.valueOf(el.getAttribute("width"));
                        height = Double.valueOf(el.getAttribute("height"));
                        break;
                    case "coordSystem":
                        xZero = Double.valueOf(el.getAttribute("xZero"));
                        yZero = Double.valueOf(el.getAttribute("yZero"));
                        xScale = Double.valueOf(el.getAttribute("scale"));
                        yScale = Double.valueOf(el.getAttribute("yscale"));
                        break;
                }
            }
        }
        this.xmin = -xZero / xScale;
        this.xmax = (width - xZero) / xScale;

        double ymin = (yZero - height) / yScale;
        double ymax = yZero / yScale;
        this.yCenter = .5 * (ymin + ymax);
    }

    @Override
    public double getMinX() {
        return xmin;
    }

    @Override
    public double getMaxX() {
        return xmax;
    }

    @Override
    public double getYCenter() {
        return yCenter;
    }

    /**
     * Return all internal MathObjects from the imported Constructibles
     *
     * @return An array of MathObjects
     */
    public MathObject<?>[] getMathObjects() {
        MathObject<?>[] resul = new MathObject[cp.geogebraElements.size()];
        int counter = 0;
        for (String key : cp.geogebraElements.keySet()) {
            resul[counter] = cp.geogebraElements.get(key).getMathObject();
            counter++;
        }
        return resul;
    }
}
