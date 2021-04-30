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
import com.jmathanim.Utils.ResourceLoader;
import com.jmathanim.jmathanim.JMathAnimScene;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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

			parseLoadConfigOptions(root.getElementsByTagName("include"));
			parseVideoOptions(config, root.getElementsByTagName("video"));
			parseBackgroundOptions(config, root.getElementsByTagName("background"));
			parseStyles(config, root.getElementsByTagName("styles"));

		} catch (IOException | SAXException | ParserConfigurationException | NullPointerException ex) {
			JMathAnimScene.logger.error("Error loading config file " + filename + ": " + ex.toString());
		}
	}

	private static void parseVideoOptions(JMathAnimConfig config, NodeList videos) {
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
					JMathAnimScene.logger.debug("Config read: Dimensions set to ({},{}), {} fps", config.mediaW,
							config.mediaH, config.fps);
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
				case "outputFileName":
					config.setOutputFileName(item.getTextContent());
					JMathAnimScene.logger.debug("Config read: Output filename set to {}", item.getTextContent());
					break;
				}
			}
		}
	}

	private static void parseBackgroundOptions(JMathAnimConfig config, NodeList backgrounds) throws IOException {
		if (backgrounds.getLength() == 0) {
			return;
		}
		Node background = backgrounds.item(backgrounds.getLength() - 1);// Load only last item
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

	private static void parseStyles(JMathAnimConfig config, NodeList styles) {
		for (int k = 0; k < styles.getLength(); k++) {
			Element elStyle = (Element) styles.item(k);
			NodeList templChilds = elStyle.getElementsByTagName("style");
			for (int n = 0; n < templChilds.getLength(); n++) {
				Node item = templChilds.item(n);
				MODrawProperties mp = parseMathObjectDrawingProperties(item);
				if (item.getNodeType() == Node.ELEMENT_NODE) {
					Element el = (Element) item;
					config.getStyles().put(el.getAttribute("name").toUpperCase(), mp);
				}

			}
		}
	}

	private static MODrawProperties parseMathObjectDrawingProperties(Node template) {
		MODrawProperties mp = new MODrawProperties();
		NodeList childs = template.getChildNodes();
		for (int n = 0; n < childs.getLength(); n++) {
			Node item = childs.item(n);
			switch (item.getNodeName()) {
			case "drawColor":
				mp.getDrawColor().copyFrom(JMColor.parse(item.getTextContent()));
				break;
			case "fillColor":
				mp.getFillColor().copyFrom(JMColor.parse(item.getTextContent()));
				break;
			case "thickness":
				mp.thickness = Double.parseDouble(item.getTextContent());
				break;
			case "dashStyle":
				mp.dashStyle = MODrawProperties.parseDashStyle(item.getTextContent());
				break;
			case "absoluteThickness":
				mp.absoluteThickness = Boolean.parseBoolean(item.getTextContent());
				break;
			case "dotStyle":
				mp.dotStyle = MODrawProperties.parseDotStyle(item.getTextContent());
				break;
			}
		}
		return mp;
	}

	private static void parseLoadConfigOptions(NodeList includeTags) {
		for (int n = 0; n < includeTags.getLength(); n++) {
			Node item = includeTags.item(n);
			JMathAnimScene.logger.debug("Including file {}", item.getTextContent());
			ConfigLoader.parseFile(item.getTextContent());
		}
	}

}
