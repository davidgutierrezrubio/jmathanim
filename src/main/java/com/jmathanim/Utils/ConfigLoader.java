/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Utils;

import com.jmathanim.mathobjects.Arrow2D;
import com.jmathanim.mathobjects.SVGMathObject;
import java.io.File;
import java.io.IOException;
import static java.lang.Boolean.parseBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class ConfigLoader {

    public static void parseFile(String filename) {
        try {
            JMathAnimConfig config = JMathAnimConfig.getConfig();
            File resourcesDir = new File("resources");
            config.resourcesDir = resourcesDir;
            String baseFileName = resourcesDir.getCanonicalPath() + "\\" + filename;
            File inputFile = new File(baseFileName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            Element root = doc.getDocumentElement();
            String n = root.getNodeName();
            if (root.getNodeName() != "JMathAnimConfig") {
                try {
                    throw new Exception("XML File doesn't contain a valid config file");
                } catch (Exception ex) {
                    Logger.getLogger(ConfigLoader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            parseVideoOptions(config, root.getElementsByTagName("video").item(0));
            parseBackgroundOptions(config, root.getElementsByTagName("background").item(0));
            final Element item = (Element) root.getElementsByTagName("templates").item(0);
            System.out.println("Parsing " + item.getNodeName());
            parseTemplates(config, item);

        } catch (IOException ex) {
            Logger.getLogger(ConfigLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(ConfigLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ConfigLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void parseVideoOptions(JMathAnimConfig config, Node video) {
        NodeList videoChilds = video.getChildNodes();
        for (int n = 0; n < videoChilds.getLength(); n++) {
            Node item = videoChilds.item(n);
            switch (item.getNodeName()) {
                case "size":
                    Element el = (Element) item;
                    config.mediaW = Integer.parseInt(el.getAttribute("width"));
                    config.mediaH = Integer.parseInt(el.getAttribute("height"));
                    config.fps = Integer.parseInt(el.getAttribute("fps"));
                    break;
                case "createMovie":
                    config.setCreateMovie(Boolean.parseBoolean(item.getTextContent()));
                    break;
                case "showPreviewWindow":
                    config.setShowPreviewWindow(Boolean.parseBoolean(item.getTextContent()));
                    break;
            }

        }
    }

    private static void parseBackgroundOptions(JMathAnimConfig config, Node background) throws IOException {
        NodeList bgChilds = background.getChildNodes();
        for (int n = 0; n < bgChilds.getLength(); n++) {
            Node item = bgChilds.item(n);
            switch (item.getNodeName()) {
                case "color":
                    String colorId = item.getTextContent();
                    config.setBackgroundColor(JMColor.parseColorID(colorId));
                    break;
                case "shadows":
                    config.drawShadow = Boolean.parseBoolean(item.getTextContent());
                    Element el = (Element) item;
                    config.shadowKernelSize=Integer.parseInt(el.getAttribute("kernelSize"));
                    config.shadowOffsetX=Integer.parseInt(el.getAttribute("offsetX"));
                    config.shadowOffsetY=Integer.parseInt(el.getAttribute("offsetY"));
                    config.shadowAlpha=Float.parseFloat(el.getAttribute("alpha"));
                    break;
                case "image":
                    String backgroundFilename = item.getTextContent();
                    if (backgroundFilename != "") {
                        config.backGroundImage = config.resourcesDir.getCanonicalPath() + "\\" + backgroundFilename;
                    }
                    break;
            }

        }
    }

    public static void parseTemplates(JMathAnimConfig config, Element templates) {
        NodeList templChilds = templates.getElementsByTagName("template");
        for (int n = 0; n < templChilds.getLength(); n++) {
            Node item = templChilds.item(n);
            MathObjectDrawingProperties mp = parseMathObjectDrawingProperties(item);
            if (item.getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) item;
                config.templates.put(el.getAttribute("name"), mp);
            }

        }

    }

    public static MathObjectDrawingProperties parseMathObjectDrawingProperties(Node template) {
        MathObjectDrawingProperties mp = new MathObjectDrawingProperties();
        NodeList childs = template.getChildNodes();
        for (int n = 0; n < childs.getLength(); n++) {
            Node item = childs.item(n);
            switch (item.getNodeName()) {
                case "drawColor":
                    mp.drawColor.set(JMColor.parseColorID(item.getTextContent()));
                    break;
                case "fillColor":
                    mp.fillColor.set(JMColor.parseColorID(item.getTextContent()));
                    break;
                case "thickness":
                    mp.thickness = Double.parseDouble(item.getTextContent());
                    break;
                case "dashStyle":
                    mp.dashStyle = Integer.parseInt(item.getTextContent());
                    break;
                case "absoluteThickness":
                    mp.absoluteThickness = Boolean.parseBoolean(item.getTextContent());
                    break;
            }
        }
        return mp;
    }
}
