/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Utils;

import com.jmathanim.jmathanim.JMathAnimScene;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import tests.JMathAnim;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class ConfigLoader {

    public static void parseFile(String filename) {
        try {
            JMathAnimConfig config = JMathAnimConfig.getConfig();
            File resourcesDir = JMathAnimConfig.getConfig().getResourcesDir();
            String baseFileName = resourcesDir.getCanonicalPath() + File.separator + "config" + File.separator + filename;
            JMathAnimScene.logger.info("Loading config file {}", baseFileName);
            File inputFile = new File(baseFileName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            Element root = doc.getDocumentElement();
            String n = root.getNodeName();
            if (!"JMathAnimConfig".equals(root.getNodeName())) {
                try {
                    throw new Exception("XML File doesn't contain a valid config file");
                } catch (Exception ex) {
                    Logger.getLogger(ConfigLoader.class.getName()).log(Level.SEVERE, null, ex);
                    System.exit(1);
                }
            }

            parseVideoOptions(config, root.getElementsByTagName("video"));
            parseBackgroundOptions(config, root.getElementsByTagName("background"));
            parseStyles(config, root.getElementsByTagName("styles"));

        } catch (IOException | SAXException | ParserConfigurationException ex) {
            Logger.getLogger(ConfigLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void parseVideoOptions(JMathAnimConfig config, NodeList videos) {
        for (int k = 0; k < videos.getLength(); k++) {
            NodeList videoChilds = videos.item(k).getChildNodes();
            for (int n = 0; n < videoChilds.getLength(); n++) {
                Node item = videoChilds.item(n);
                switch (item.getNodeName()) {
                    case "size":
                        Element el = (Element) item;
                        config.mediaW = Integer.parseInt(el.getAttribute("width"));
                        config.mediaH = Integer.parseInt(el.getAttribute("height"));
                        config.fps = Integer.parseInt(el.getAttribute("fps"));
                        JMathAnimScene.logger.debug("Config read: Dimensions set to ({},{}), {} fps", config.mediaW, config.mediaH, config.fps);
                        break;
                    case "createMovie":
                        final boolean createMovie = Boolean.parseBoolean(item.getTextContent());
                        config.setCreateMovie(createMovie);
                        JMathAnimScene.logger.debug("Config read: Create movie set to {}", createMovie);
                        break;
                    case "showPreviewWindow":
                        final boolean previewWindow = Boolean.parseBoolean(item.getTextContent());
                        config.setShowPreviewWindow(previewWindow);
                        JMathAnimScene.logger.debug("Config read: Show preview window set to {}", previewWindow);
                        break;
                    case "outputDir":
                        config.setOutputDir(item.getTextContent());
                        JMathAnimScene.logger.debug("Config read: Output dir set to {}", item.getTextContent());
                        break;
                }
            }
        }
    }

    private static void parseBackgroundOptions(JMathAnimConfig config, NodeList backgrounds) throws IOException {
        if (backgrounds.getLength() == 0) {
            return;
        }
        Node background = backgrounds.item(backgrounds.getLength() - 1);//Load only last item 
        NodeList bgChilds = background.getChildNodes();
        for (int n = 0; n < bgChilds.getLength(); n++) {
            Node item = bgChilds.item(n);
            switch (item.getNodeName()) {
                case "color":
                    String colorId = item.getTextContent();
                    config.setBackgroundColor(JMColor.parseColorID(colorId));
                    JMathAnimScene.logger.debug("Config read: Background color set to {}", colorId);
                    break;
                case "shadows":
                    config.drawShadow = Boolean.parseBoolean(item.getTextContent());
                    Element el = (Element) item;
                    config.shadowKernelSize = Integer.parseInt(el.getAttribute("kernelSize"));
                    config.shadowOffsetX = Integer.parseInt(el.getAttribute("offsetX"));
                    config.shadowOffsetY = Integer.parseInt(el.getAttribute("offsetY"));
                    config.shadowAlpha = Float.parseFloat(el.getAttribute("alpha"));
                    JMathAnimScene.logger.debug("Config read: Draw shadows set to {}", config.drawShadow);
                    break;
                case "image":
                    String backgroundFilename = item.getTextContent();
                    if (!"".equals(backgroundFilename)) {
                        config.backGroundImage = config.getResourcesDir().getCanonicalPath() + "\\" + backgroundFilename;
                        JMathAnimScene.logger.debug("Config read: Background image set to {}", backgroundFilename);
                    }
                    break;
            }

        }
    }

    public static void parseStyles(JMathAnimConfig config, NodeList styles) {
        for (int k = 0; k < styles.getLength(); k++) {
            Element elStyle = (Element) styles.item(k);
            NodeList templChilds = elStyle.getElementsByTagName("style");
            for (int n = 0; n < templChilds.getLength(); n++) {
                Node item = templChilds.item(n);
                MathObjectDrawingProperties mp = parseMathObjectDrawingProperties(item);
                if (item.getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) item;
                    config.getStyles().put(el.getAttribute("name"), mp);
                }

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
                    mp.dashStyle = MathObjectDrawingProperties.parseDashStyle(item.getTextContent());
                    break;
                case "absoluteThickness":
                    mp.absoluteThickness = Boolean.parseBoolean(item.getTextContent());
                    break;
            }
        }
        return mp;
    }

}
