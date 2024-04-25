/*
 * Copyright (C) 2020 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
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
package com.jmathanim.Styling;

import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.LatexStyle;
import com.jmathanim.Utils.LatexStyleItem;
import com.jmathanim.Utils.LatexToken;
import com.jmathanim.Utils.ResourceLoader;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Point;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.paint.CycleMethod;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class ConfigLoader {

    private static ResourceLoader resourceLoader;

    public static void parseFile(String filename) {
        resourceLoader = new ResourceLoader();
        try {
            JMathAnimConfig config = JMathAnimConfig.getConfig();
            URL configURL = resourceLoader.getResource(filename, "config");
            JMathAnimScene.logger.info("Loading config file {}", filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setValidating(false);
            dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//            dBuilder.setEntityResolver(new EntityResolver() {
//                @Override
//                public InputSource resolveEntity(String publicId, String systemId) {
//                    System.out.println("-------------"+publicId+"   "+systemId);
//                    return new InputSource(new StringReader("")); // Returns a valid dummy source
//                }
//            });
            InputStream stream = configURL.openStream();
            Document doc = dBuilder.parse(stream);
            doc.getDocumentElement().normalize();

            Element root = doc.getDocumentElement();
            if (!"JMathAnimConfig".equals(root.getNodeName())) {
                try {
                    throw new Exception("XML File doesn't contain a valid config file");
                } catch (Exception ex) {
                    Logger.getLogger(ConfigLoader.class.getName()).log(Level.SEVERE, null, ex);
                    System.exit(1);
                }
            }

            NodeList nodeList = root.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                // Verificar si el nodo es un elemento (etiqueta)
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    // Llamar al método correspondiente según el nombre de la etiqueta
                    switch (element.getTagName()) {
                        case "include":
                            parseLoadConfigOptions(element);
                            break;
                        case "video":
                            parseVideoOptions(config, element);
                            break;
                        case "background":
                            parseBackgroundOptions(config, element);
                            break;
                        case "styles":
                            parseStyles(config, element);
                            break;
                        case "latexStyles":
                            parseLatexStyles(config, element);
                            break;
                        // Agregar más casos según sea necesario para otras etiquetas
                    }
                }
            }

//            parseLoadConfigOptions(root.getElementsByTagName("include"));
//            parseVideoOptions(config, root.getElementsByTagName("video"));
//            parseBackgroundOptions(config, root.getElementsByTagName("background"));
//            parseStyles(config, root.getElementsByTagName("styles"));
//            parseLatexStyles(config, root.getElementsByTagName("latexStyles"));
        } catch (IOException | SAXException | ParserConfigurationException | NullPointerException ex) {
            JMathAnimScene.logger.error("Error loading config file " + filename + ": " + ex.toString());
        }
    }

    private static void parseVideoOptions(JMathAnimConfig config, Element video) {
        NodeList videoChilds = video.getChildNodes();
        for (int n = 0; n < videoChilds.getLength(); n++) {
            Node item = videoChilds.item(n);
            switch (item.getNodeName()) {
                case "size":
                    Element el = (Element) item;
                    config.mediaW = Integer.parseInt(el.getAttribute("width"));
                    config.mediaH = Integer.parseInt(el.getAttribute("height"));
                    config.fps = Integer.parseInt(el.getAttribute("fps"));
                    JMathAnimScene.logger.debug("Config read: Dimensions set to ({},{}), {} fps", config.mediaW,
                            config.mediaH, config.fps);
                    break;
                case "createMovie":
                    final boolean createMovie = Boolean.parseBoolean(item.getTextContent());
                    config.setCreateMovie(createMovie);
                    JMathAnimScene.logger.debug("Config read: Create movie set to {}", createMovie);
                    break;
                case "saveToPNG":
                    final boolean saveToPNG = Boolean.parseBoolean(item.getTextContent());
                    config.setSaveToPNG(saveToPNG);
                    JMathAnimScene.logger.debug("Config read: Save to PNG flag set to {}", saveToPNG);
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
                case "outputFileName":
                    config.setOutputFileName(item.getTextContent());
                    JMathAnimScene.logger.debug("Config read: Output filename set to {}", item.getTextContent());
                    break;
            }
        }
    }

    private static void parseBackgroundOptions(JMathAnimConfig config, Element background) throws IOException {
        NodeList bgChilds = background.getChildNodes();
        for (int n = 0; n < bgChilds.getLength(); n++) {
            Node item = bgChilds.item(n);
            switch (item.getNodeName()) {
                case "color":
                    String colorId = item.getTextContent();
                    config.setBackgroundColor(JMColor.parse(colorId));
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
                        config.setBackGroundImage(resourceLoader.getResource(backgroundFilename, "images"));
//                        config.backGroundImage = config.getResourcesDir().getCanonicalPath() + File.pathSeparator + backgroundFilename;
                        JMathAnimScene.logger.debug("Config read: Background image set to {}", backgroundFilename);
                    }
                    break;
            }

        }
    }

    private static void parseStyles(JMathAnimConfig config, Element styles) {
        NodeList templChilds = styles.getElementsByTagName("style");
        for (int n = 0; n < templChilds.getLength(); n++) {
            Node item = templChilds.item(n);
            if (item.getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) item;
                String baseStyle = el.getAttribute("base");
                MODrawProperties mp = parseMathObjectDrawingProperties(config, baseStyle, item);
                String styleName = el.getAttribute("name").toUpperCase().trim();
                config.getStyles().put(styleName, mp);
                JMathAnimScene.logger.debug("Parsed style " + styleName);
            }

        }
    }

    private static void parseLatexStyles(JMathAnimConfig config, Element latexStyles) {
        NodeList templChilds = latexStyles.getElementsByTagName("latexStyle");
        for (int n = 0; n < templChilds.getLength(); n++) {
            Node item = templChilds.item(n);
            if (item.getNodeType() == Node.ELEMENT_NODE) {
                Element elementLatexStyle = (Element) item;
                String baseStyle = elementLatexStyle.getAttribute("baseLatexStyle");
                LatexStyle latexStyle = parseLatexStyle(config, baseStyle, elementLatexStyle);
                String styleName = elementLatexStyle.getAttribute("name").toUpperCase();
                config.getLatexStyles().put(styleName, latexStyle);
                JMathAnimScene.logger.debug("Parsed LaTeX style " + styleName);
            }
        }
    }

    private static LatexStyle parseLatexStyle(JMathAnimConfig config, String baseLatexStyle, Element parent) {
        LatexStyle latexStyle;
        if (!"".equals(baseLatexStyle)) {
            latexStyle = config.getLatexStyles().get(baseLatexStyle).copy();
        } else {
            latexStyle = new LatexStyle();
        }

        NodeList templChilds = parent.getElementsByTagName("latexStyleItem");
        for (int n = 0; n < templChilds.getLength(); n++) {
            Node item = templChilds.item(n);
            if (item.getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) item;
                LatexStyleItem latexStyleItem = parseLatexStyleItem(config, el);
                latexStyle.add(latexStyleItem);
            }
        }

        return latexStyle;

    }

    private static LatexStyleItem parseLatexStyleItem(JMathAnimConfig config, Element parent) {

        //Conditions item. LatexToken inside <conditions> tag
        Element conditionElement = getFirstChildElementWithName(parent, "conditions");
        LatexToken ltEquals = parseLatexToken(getFirstChildElementWithName(conditionElement, "equals"));
        LatexToken ltEqualsPrev = parseLatexToken(getFirstChildElementWithName(conditionElement, "equalsPrev"));
        LatexToken ltEqualsAfter = parseLatexToken(getFirstChildElementWithName(conditionElement, "equalsAfter"));
        LatexToken ltDiff = parseLatexToken(getFirstChildElementWithName(conditionElement, "differs"));
        LatexToken ltDiffPrev = parseLatexToken(getFirstChildElementWithName(conditionElement, "differsPrev"));
        LatexToken ltDiffAfter = parseLatexToken(getFirstChildElementWithName(conditionElement, "differsAfter"));
        LatexStyleItem latexStyleItem = new LatexStyleItem();
        latexStyleItem.tokenEq = ltEquals;
        latexStyleItem.tokenEqPrev = ltEqualsPrev;
        latexStyleItem.tokenEqAfter = ltEqualsAfter;
        latexStyleItem.tokenDif = ltDiff;
        latexStyleItem.tokenDifPrev = ltDiffPrev;
        latexStyleItem.tokenDifAfter = ltDiffAfter;
        Element styleElement = getFirstChildElementWithName(parent, "style");
        String baseStyle = styleElement.getAttribute("base");
        if ("".equals(baseStyle)) {
            baseStyle = "LATEXDEFAULT";
        }
        MODrawProperties mo = parseMathObjectDrawingProperties(config, baseStyle, styleElement);
        latexStyleItem.style = mo;
        return latexStyleItem;
    }

    private static LatexToken parseLatexToken(Element el) {
        if (el == null) {//No node, no token!
            return null;
        }
        String type = getFirstChildValueByName(el, "type");
        String subTypeStr = getFirstChildValueByName(el, "subtype");
        String string = getFirstChildValueByName(el, "string");

        LatexToken.TokenType tokenType = null;

        if (type != null) {
            //Convert String type to the corresponding enum value, catching possible errors
            type=type.toUpperCase();
            try {
                tokenType = LatexToken.TokenType.valueOf(type);
            } catch (IllegalArgumentException e) {
                JMathAnimScene.logger.warn("Token type " + type + " not recognized parsing LatexToken config file");
                tokenType = null;
            }
        }

        //Parse string "SEC_NORMAL,SEC_NUMERATOR,..." into integer with proper bits set to 1
        Integer tokenSubType = null;
        if (subTypeStr != null) {
            tokenSubType = 0;
            for (String str : subTypeStr.split(",")) {
                str=str.toUpperCase();
                Field campo;
                try {
                    campo = LatexToken.class.getField(str); //Get variable with that name
                    int value = campo.getInt(null);
                    tokenSubType |= value;
                } catch (NoSuchFieldException ex) {
                    JMathAnimScene.logger.warn("SubToken type " + str + " not recognized parsing LatexToken config file");
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    JMathAnimScene.logger.warn("Illegal argument exception parsing LatexToken config file");
                }

            }
        }

        return new LatexToken(tokenType, tokenSubType, string);
    }

    public static String getFirstChildValueByName(Element parent, String name) {
        NodeList nodeList = parent.getElementsByTagName(name);

        if (nodeList.getLength() > 0) {
            Node firstChild = nodeList.item(0).getFirstChild();
            if (firstChild != null) {
                String resul = firstChild.getNodeValue();
                if ("".equals(resul)) {
                    return null;
                } else {
                    return resul;
                }
            }
        }
        return null;
    }

    public static Element getFirstChildElementWithName(Element parent, String name) {
        NodeList nodeList = parent.getElementsByTagName(name);

        if (nodeList.getLength() > 0) {
            Node firstChild = nodeList.item(0);
            if (firstChild != null) {
                return (Element) firstChild;
            }
        }
        return null;
    }

    private static MODrawProperties parseMathObjectDrawingProperties(JMathAnimConfig config, String baseStyle, Node template) {
        MODrawProperties mp = MODrawProperties.makeNullValues();
        if (!"".equals(baseStyle)) {
            MODrawProperties baseStyleMP = config.getStyles().get(baseStyle);
            mp.copyFrom(baseStyleMP);
        }
        PaintStyle color;
        NodeList childs = template.getChildNodes();
        for (int n = 0; n < childs.getLength(); n++) {
            Node item = childs.item(n);
            String name = item.getNodeName();
            switch (name) {
                case "drawColor":
                    color = parsePaintStyle(config, item);
                    mp.setDrawColor(color);
                    break;
                case "fillColor":
                    mp.setFillColor(parsePaintStyle(config, item));
                    break;
                case "color":
                    color = parsePaintStyle(config, item);
                    mp.setDrawColor(color);
                    mp.setFillColor(color);
                    break;
                case "thickness":
                    Double th = Double.valueOf(item.getTextContent());
                    mp.setThickness(th);
                    break;
                case "fillAlpha":
                    mp.setFillAlpha(Double.parseDouble(item.getTextContent()));
                    break;
                case "drawAlpha":
                    mp.setDrawAlpha(Double.parseDouble(item.getTextContent()));
                    break;
                case "layer":
                    mp.setLayer(Integer.parseInt(item.getTextContent()));
                    break;
                case "dashStyle":
                    mp.setDashStyle(MODrawProperties.parseDashStyle(item.getTextContent()));
                    break;
                case "absoluteThickness":
                    mp.setAbsoluteThickness(Boolean.valueOf(item.getTextContent()));
                    break;
                case "dotStyle":
                    mp.setDotStyle(MODrawProperties.parseDotStyle(item.getTextContent()));
                    break;
                case "#text":
                    break;
                case "scaleArrowHead1":
                    mp.setScaleArrowHead1(Double.valueOf(item.getTextContent()));
                    break;
                case "scaleArrowHead2":
                    mp.setScaleArrowHead2(Double.valueOf(item.getTextContent()));
                    break;
                default:
                    JMathAnimScene.logger.warn("Tag {} not recognized", name);

            }
        }
        return mp;
    }

    private static PaintStyle parsePaintStyle(JMathAnimConfig config, Node node) {
        NodeList childs = node.getChildNodes();
        for (int n = 0; n < childs.getLength(); n++) {
            Node item = childs.item(n);
            if (item instanceof Text) {//A single text, containing a color def
                String st = item.getTextContent().trim();
                if (!"".equals(st)) {
                    return JMColor.parse(st);
                }
            }

            if (item instanceof Element) {
                String name = item.getNodeName();
                switch (name) {
                    case "getDrawColor"://Get the draw color/gradient from other style
                        String styleDrawColor = ((Element) item).getAttribute("style").toUpperCase();
                        return config.getStyles().get(styleDrawColor).getDrawColor();
                    case "getFillColor"://Get the fill color/gradient from other style
                        String styleFillColor = ((Element) item).getAttribute("style").toUpperCase();
                        return config.getStyles().get(styleFillColor).getFillColor();
                    case "linearGradient":
                        return parseLinearGradient(config, (Element) item);
                    case "radialGradient":
                        return parseRadialGradient(config, (Element) item);

                }

            }

        }

        return null;
    }

    private static JMLinearGradient parseLinearGradient(JMathAnimConfig config, Element gradientElement) {
        NodeList starts = gradientElement.getElementsByTagName("start");
        Element start = (Element) starts.item(0);
        Double x = Double.valueOf(start.getAttribute("x"));
        Double y = Double.valueOf(start.getAttribute("y"));
        Point startP = Point.at(x, y);

        NodeList ends = gradientElement.getElementsByTagName("end");
        Element end = (Element) ends.item(0);
        x = Double.valueOf(end.getAttribute("x"));
        y = Double.valueOf(end.getAttribute("y"));
        Point endP = Point.at(x, y);
        JMLinearGradient resul = new JMLinearGradient(startP, endP);

        //Now process the stops
        NodeList stopList = gradientElement.getElementsByTagName("stops");
        if (stopList.getLength() > 0) {
            Element stopsEl = (Element) stopList.item(0);
            NodeList childs = stopsEl.getChildNodes();
            for (int n = 0; n < childs.getLength(); n++) {
                Node item = childs.item(n);
                if (item instanceof Element) {
                    Element stopEl = (Element) item;
                    if ("stop".equals(stopEl.getNodeName())) {
                        double stopTime = Double.parseDouble(stopEl.getAttribute("t"));
                        JMColor col = JMColor.parse(stopEl.getTextContent());
                        resul.add(stopTime, col);
                    }
                }
            }
        }
        //Relative flag
        String relativeText = gradientElement.getAttribute("relative");
        boolean relative = Boolean.parseBoolean(relativeText);//Default:false
        resul.setRelativeToShape(relative);
        resul.setCycleMethod(CycleMethod.NO_CYCLE);

        //Cycle method: none, repeat, reflect
        String cycleMethod = gradientElement.getAttribute("cycle").trim().toUpperCase();
        switch (cycleMethod) {
            case "NONE":
                resul.setCycleMethod(CycleMethod.NO_CYCLE);
                break;
            case "REPEAT":
                resul.setCycleMethod(CycleMethod.REPEAT);
                break;
            case "REFLECT":
                resul.setCycleMethod(CycleMethod.REFLECT);
                break;
        }
        return resul;
    }

    private static JMRadialGradient parseRadialGradient(JMathAnimConfig config, Element gradientElement) {
        NodeList starts = gradientElement.getElementsByTagName("center");
        Element start = (Element) starts.item(0);
        Double x = Double.valueOf(start.getAttribute("x"));
        Double y = Double.valueOf(start.getAttribute("y"));
        Point centerP = Point.at(x, y);

        NodeList radiuses = gradientElement.getElementsByTagName("radius");
        Element radiusEl = (Element) radiuses.item(0);
        double radius = Double.parseDouble(radiusEl.getTextContent());
        JMRadialGradient resul = new JMRadialGradient(centerP, radius);

        //Now process the stops
        NodeList stopList = gradientElement.getElementsByTagName("stops");
        if (stopList.getLength() > 0) {
            Element stopsEl = (Element) stopList.item(0);
            NodeList childs = stopsEl.getChildNodes();
            for (int n = 0; n < childs.getLength(); n++) {
                Node item = childs.item(n);
                if (item instanceof Element) {
                    Element stopEl = (Element) item;
                    if ("stop".equals(stopEl.getNodeName())) {
                        double stopTime = Double.parseDouble(stopEl.getAttribute("t"));
                        JMColor col = JMColor.parse(stopEl.getTextContent());
                        resul.add(stopTime, col);
                    }
                }
            }

        }
        //Relative flag
        String relativeText = gradientElement.getAttribute("relative");
        boolean relative = Boolean.parseBoolean(relativeText);//Default:false
        resul.setRelativeToShape(relative);
        resul.setCycleMethod(CycleMethod.NO_CYCLE);

        //Cycle method: none, repeat, reflect
        String cycleMethod = gradientElement.getAttribute("cycle").trim().toUpperCase();
        switch (cycleMethod) {
            case "NONE":
                resul.setCycleMethod(CycleMethod.NO_CYCLE);
                break;
            case "REPEAT":
                resul.setCycleMethod(CycleMethod.REPEAT);
                break;
            case "REFLECT":
                resul.setCycleMethod(CycleMethod.REFLECT);
                break;
        }
        return resul;
    }

    private static void parseLoadConfigOptions(Element include) {
        JMathAnimScene.logger.debug("Including file {}", include.getTextContent());
        ConfigLoader.parseFile(include.getTextContent());
    }
}
