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

import com.jmathanim.Enum.LatexTokenType;
import com.jmathanim.Utils.*;
import com.jmathanim.jmathanim.JMathAnimConfig;
import com.jmathanim.jmathanim.JMathAnimScene;
import javafx.scene.paint.CycleMethod;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.jmathanim.jmathanim.LogUtils.RESET;
import static com.jmathanim.jmathanim.LogUtils.YELLOW;

/**
 * The ConfigLoader class provides methods for loading and parsing XML configuration files
 * for the JMathAnim framework. It processes configuration details such as video settings,
 * background options, styles, LaTeX configurations, and drawing properties, and applies them
 * to the provided JMathAnimConfig object. This class internally uses a resource loader
 * to resolve file references.
 */
public class ConfigLoader {

    private static ResourceLoader resourceLoader;

    /**
     * Parses an XML configuration file specified by the filename parameter.
     * The method reads the configuration file, validates its structure, and loads
     * various configuration options such as video settings, background styles,
     * and more, based on the XML elements present in the file.
     *
     * @param filename The name of the XML configuration file to parse. The file can be
     *                 internally or externally located and is resolved using the
     *                 {@code ResourceLoader} class.
     */
    public static void parseFile(String filename) {
        resourceLoader = new ResourceLoader();
        try {
            JMathAnimConfig config = JMathAnimConfig.getConfig();
            URL configURL = resourceLoader.getResource(filename, "config");
            JMathAnimScene.logger.info("Loading config file "+YELLOW+"{}"+RESET, filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setValidating(false);
            dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

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


        } catch (IOException | SAXException | ParserConfigurationException | NullPointerException ex) {
            JMathAnimScene.logger.error("Error loading config file " + filename + ": " + ex);
        }
    }

    /**
     * Parses the video configuration options from the given XML element and sets the corresponding
     * properties in the provided {@code JMathAnimConfig} object. The method processes various
     * video-related attributes such as size, frame rate, output directory, and output file name.
     *
     * @param config The {@code JMathAnimConfig} object where the video configuration will be set.
     * @param video  The XML element representing the video configuration options. This element
     *               contains child nodes with attributes like size, createMovie, saveToPNG,
     *               previewWindow, outputDir, and outputFileName that are extracted and applied to
     *               the provided {@code JMathAnimConfig}.
     */
    private static void parseVideoOptions(JMathAnimConfig config, Element video) {
        NodeList videoChilds = video.getChildNodes();
        for (int n = 0; n < videoChilds.getLength(); n++) {
            Node item = videoChilds.item(n);
            switch (item.getNodeName()) {
                case "size":
                    Element el = (Element) item;
                    config.setMediaWidth(Integer.parseInt(el.getAttribute("width")));
                    config.setMediaHeight(Integer.parseInt(el.getAttribute("height")));
                    config.setFPS(Integer.parseInt(el.getAttribute("fps")));
                    JMathAnimScene.logger.debug("Config read: Dimensions set to ({},{}), {} fps", config.getMediaWidth(),
                            config.getMediaHeight(), config.getFps());
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
                case "limitFPS":
                    final boolean limitFPS = Boolean.parseBoolean(item.getTextContent());
                    config.setLimitFPS(limitFPS);
                    JMathAnimScene.logger.debug("Config read: Limit FPS {}", limitFPS);
                    break;
                case "printProgressBar":
                    final boolean printProgressBar = Boolean.parseBoolean(item.getTextContent());
                    config.setPrintProgressBar(printProgressBar);
                    JMathAnimScene.logger.debug("Config read: Print progress bar {}", printProgressBar);
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

    /**
     * Parses the background configuration options from the given XML element and sets the corresponding
     * properties in the provided {@code JMathAnimConfig} object. This method processes various background
     * attributes such as color, shadows, and background image, found in the XML element.
     *
     * @param config The {@code JMathAnimConfig} object to which the parsed background configuration
     *               will be applied. This object holds the application-wide configuration settings.
     * @param background The XML element representing the background configuration options. This element
     *                   may contain child nodes like "color", "shadows", and "image" which designate
     *                   specific background settings.
     * @throws IOException If an I/O error occurs while loading background-related resources, such as images.
     */
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

    /**
     * Parses style elements from the provided XML configuration and maps them to the {@code JMathAnimConfig} object.
     * The method processes all "style" elements within the given {@code styles} element, extracts their attributes,
     * and creates corresponding {@code MODrawProperties} objects that are stored within the configuration's styles map.
     * This allows for the dynamic application of styles throughout the animation framework based on predefined configurations.
     *
     * @param config The {@code JMathAnimConfig} object to which the parsed styles will be added.
     * @param styles The XML element containing "style" child elements. Each child element is expected to
     *               have attributes such as "base" and "name" used for defining styles.
     */
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

    /**
     * Parses LaTeX style elements from the provided XML configuration and maps them to
     * the {@code JMathAnimConfig} object. The method processes all "latexStyle" elements
     * within the given {@code latexStyles} element, extracts their attributes, and creates
     * corresponding {@code LatexStyle} objects. These styles are stored in a map within
     * the configuration and can be referenced by their names in uppercase.
     *
     * @param config The {@code JMathAnimConfig} object where the parsed LaTeX styles
     *               will be added. This object holds the application-wide configuration settings.
     * @param latexStyles The XML element containing "latexStyle" child elements. Each child
     *                    element is expected to have attributes such as "name" and
     *                    "baseLatexStyle" used for defining the LaTeX styles.
     */
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

    /**
     * Parses a LaTeX style from the provided configuration, base style name, and XML element.
     * This method creates a new LatexStyle object either by copying an existing style defined
     * in the configuration or by instantiating a default style. It then iterates through child
     * "latexStyleItem" elements in the parent XML element to add additional style items to the
     * newly created LatexStyle object.
     *
     * @param config The JMathAnimConfig object containing predefined LaTeX styles and configuration settings.
     * @param baseLatexStyle A string representing the name of the base LaTeX style to copy.
     *                       If empty, a new default LatexStyle object is created.
     * @param parent The XML element containing the parent "latexStyle" definition and its child "latexStyleItem" elements.
     * @return A LatexStyle object containing all the parsed style items.
     */
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

    /**
     * Parses a LaTeX style item from the provided XML element and configuration object.
     * This method reads the conditions and style details of a LaTeX style item, creates
     * a new {@code LatexStyleItem} object, and configures it based on the parsed data.
     *
     * @param config The {@code JMathAnimConfig} object containing application-wide configuration
     *               settings and existing styles.
     * @param parent The parent XML element representing the LaTeX style item, which includes
     *               child elements for conditions and style attributes.
     * @return A configured {@code LatexStyleItem} object based on the parsed XML data.
     */
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
        latexStyleItem.mustMatchTo(ltEquals);
        latexStyleItem.previousTokenMustMatchTo(ltEqualsPrev);
        latexStyleItem.nextTokenMustMatchTo(ltEqualsAfter);
        latexStyleItem.mustDifferFrom(ltDiff);
        latexStyleItem.previousTokenMustDifferFrom(ltDiffPrev);
        latexStyleItem.nextTokenMustDifferFrom(ltDiffAfter);
        Element styleElement = getFirstChildElementWithName(parent, "style");
        String baseStyle = styleElement.getAttribute("base");
        if ("".equals(baseStyle)) {
            baseStyle = "LATEXDEFAULT";
        }
        MODrawProperties mo = parseMathObjectDrawingProperties(config, baseStyle, styleElement);
        latexStyleItem.setStyle(mo);
        return latexStyleItem;
    }

    /**
     * Parses a LaTeX token from the provided XML element. This method extracts
     * and converts data from the XML node, including token type, subtype, string,
     * and delimiter depth, to create a corresponding {@code LatexToken} object.
     *
     * @param el The XML element from which the LaTeX token details are extracted.
     *           The element is expected to contain child nodes such as "type",
     *           "subtype", "string", and "delimiterDepth".
     *
     * @return A {@code LatexToken} object created using the data extracted from
     *         the provided XML element, or {@code null} if the input element is
     *         {@code null}.
     */
    private static LatexToken parseLatexToken(Element el) {
        if (el == null) {//No node, no token!
            return null;
        }
        String type = getFirstChildValueByName(el, "type");
        String subTypeStr = getFirstChildValueByName(el, "subtype");
        String string = getFirstChildValueByName(el, "string");
        String delDepthStr = getFirstChildValueByName(el, "delimiterDepth");

        LatexTokenType latexTokenType = null;

        if (type != null) {
            //Convert String type to the corresponding enum value, catching possible errors
            type=type.toUpperCase();
            try {
                latexTokenType = LatexTokenType.valueOf(type);
            } catch (IllegalArgumentException e) {
                JMathAnimScene.logger.warn("Token type " + type + " not recognized parsing LatexToken config file");
                latexTokenType = null;
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
        Integer delimiterDepth=((delDepthStr==null)||("".equals(delDepthStr)) ? null: Integer.valueOf(delDepthStr));
        return LatexToken.make()
                .setType(latexTokenType)
                .setSecondaryTypeFlag(tokenSubType)
                .setString(string)
                .setDelimiterDepth(delimiterDepth);
    }

    private static String getFirstChildValueByName(Element parent, String name) {
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

    /**
     * Retrieves the first child element within the given parent element that matches the specified tag name.
     * This method searches through the child nodes of the provided parent element and returns the first
     * instance that is an {@code Element} and matches the given tag name.
     *
     * @param parent The parent {@code Element} whose child elements are to be searched.
     *               This element provides the context for locating the desired child element.
     * @param name   The tag name of the desired child element. This value is case-sensitive and
     *               must match the tag name of the desired element exactly.
     * @return The first {@code Element} that matches the specified tag name, or {@code null} if no such
     *         child element is found.
     */
    private static Element getFirstChildElementWithName(Element parent, String name) {
    NodeList nodeList = parent.getElementsByTagName(name);
    for (int i = 0; i < nodeList.getLength(); i++) {
        Node child = nodeList.item(i);
        if (child instanceof Element) {
            return (Element) child;
        }
    }
    return null;
}
    
    
    /**
     * Parses drawing properties for a mathematical object from a provided XML configuration node.
     * This method uses a base style, if specified, and overrides specific properties based on the
     * children of the XML node. The resulting drawing properties are encapsulated in a
     * {@code MODrawProperties} object.
     *
     * @param config The {@code JMathAnimConfig} object, which contains styles and other
     *               configuration settings needed for parsing.
     * @param baseStyle A string representing the name of the base style to be applied.
     *                  If empty, no base style is applied, and default properties are used.
     * @param template The XML node containing the template for the drawing properties.
     *                 Its child elements are parsed to determine individual property values,
     *                 such as color, thickness, and layer.
     * @return A {@code MODrawProperties} object containing the parsed drawing
     *         properties based on the base style and the specified XML template.
     */
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
                case "#comment":
                    break;
                default:
                    JMathAnimScene.logger.warn("Tag {} not recognized", name);

            }
        }
        return mp;
    }

    /**
     * Parses a PaintStyle from a given XML node.
     *
     * @param config The configuration object that contains styles and properties used for parsing.
     * @param node   The XML node containing information about the paint style to parse.
     * @return The parsed PaintStyle object based on the provided node, or null if no valid style could be parsed.
     */
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

    /**
     * Parses a linear gradient configuration from an XML element and converts it into a
     * JMLinearGradient object with the specified start and end points, stops, cycle method,
     * and relative positioning.
     *
     * @param config The JMathAnimConfig instance containing the application's configuration.
     *               This parameter can be used to provide additional context or options while
     *               parsing the gradient.
     * @param gradientElement The XML element that represents the gradient. It should contain
     *                        tags defining the start and end points, stops, and other attributes
     *                        such as cycle and relative properties.
     * @return A JMLinearGradient object configured according to the provided XML element. This
     *         object describes the linear gradient with positions, color stops, and associated
     *         properties.
     */
    private static JMLinearGradient parseLinearGradient(JMathAnimConfig config, Element gradientElement) {
        NodeList starts = gradientElement.getElementsByTagName("start");
        Element start = (Element) starts.item(0);
        Double x = Double.valueOf(start.getAttribute("x"));
        Double y = Double.valueOf(start.getAttribute("y"));
        Vec startP = Vec.to(x, y);

        NodeList ends = gradientElement.getElementsByTagName("end");
        Element end = (Element) ends.item(0);
        x = Double.valueOf(end.getAttribute("x"));
        y = Double.valueOf(end.getAttribute("y"));
        Vec endP = Vec.to(x, y);
        JMLinearGradient resul = JMLinearGradient.make(startP, endP);

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

    /**
     * Parses a radial gradient definition from the given XML element and constructs a
     * {@code JMRadialGradient} object based on the provided configuration and gradient data.
     *
     * @param config The configuration object that holds settings for parsing the gradient.
     * @param gradientElement The XML element containing the radial gradient definition,
     *                        including center, radius, stops, and other properties.
     * @return A {@code JMRadialGradient} instance representing the parsed radial gradient
     *         with its center, radius, color stops, cycle method, and relative-to-shape flag.
     */
    private static JMRadialGradient parseRadialGradient(JMathAnimConfig config, Element gradientElement) {
        NodeList starts = gradientElement.getElementsByTagName("center");
        Element start = (Element) starts.item(0);
        Double x = Double.valueOf(start.getAttribute("x"));
        Double y = Double.valueOf(start.getAttribute("y"));
        Vec centerP = Vec.to(x, y);

        NodeList radiuses = gradientElement.getElementsByTagName("radius");
        Element radiusEl = (Element) radiuses.item(0);
        double radius = Double.parseDouble(radiusEl.getTextContent());
        JMRadialGradient resul = JMRadialGradient.make(centerP, radius);

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

    /**
     * Parses the "include" element in the XML configuration to load additional configuration options.
     * This method reads the file path specified in the provided XML element and delegates the file
     * parsing to the {@code ConfigLoader.parseFile()} method.
     *
     * @param include The XML element containing the "include" directive. The element is expected to
     *                contain the file path of the configuration file to be included as its content.
     */
    private static void parseLoadConfigOptions(Element include) {
        JMathAnimScene.logger.debug("Including file {}", include.getTextContent());
        ConfigLoader.parseFile(include.getTextContent());
    }
}
