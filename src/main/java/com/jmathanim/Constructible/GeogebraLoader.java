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

import com.jmathanim.Utils.ResourceLoader;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class GeogebraLoader {
    
    private ResourceLoader rl;
    private final URL url;
    private ZipFile zipFile;
    private ZipEntry zipEntry;
    private InputStream inputStream;
    private final GeogebraCommandParser cp;
    
    private GeogebraLoader(String fileName) {
        rl = new ResourceLoader();
        url = rl.getResource(fileName, "geogebra");
        this.cp = new GeogebraCommandParser();
    }
    
    public static GeogebraLoader make(String fileName) {
        GeogebraLoader resul = new GeogebraLoader(fileName);
        resul.parseFile(fileName);
        return resul;
    }
    
    private void parseFile(String fileName) {
        try {
            JMathAnimScene.logger.info("Loading Geogebra file {}", fileName);
            zipFile = new ZipFile(url.getFile());
            zipEntry = zipFile.getEntry("geogebra.xml");
            inputStream = this.zipFile.getInputStream(zipEntry);
            parseGeogebraContents(inputStream);
        } catch (IOException ex) {
            Logger.getLogger(GeogebraLoader.class.getName()).log(Level.SEVERE, null, ex);
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
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(GeogebraLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(GeogebraLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GeogebraLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Element root = doc.getDocumentElement();
        String n = root.getNodeName();
        if (!"geogebra".equals(root.getNodeName())) {
            try {
                throw new Exception("XML File doesn't contain a valid Geogebra file");
            } catch (Exception ex) {
                Logger.getLogger(GeogebraLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
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
                cp.processPoint(el);
                break;
        }

        //If element already belongs to the hashMap, process styling options
        if (cp.containsKey(label)) {
            cp.get(label).getMp().copyFrom(cp.parseStylingOptions(el));
        }
        
    }
    
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
        }
    }
    
    public MathObject get(Object key) {
        return cp.get(key);
    }
    
    public Collection<MathObject> getObjects() {
        return cp.geogebraElements.values();
    }
    
    public HashMap<String, MathObject> getDict() {
        return cp.geogebraElements;
    }
    
}
