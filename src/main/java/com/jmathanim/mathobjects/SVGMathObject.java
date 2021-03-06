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
package com.jmathanim.mathobjects;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Styling.JMColor;
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.ResourceLoader;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.JMPathPoint.JMPathPointType;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class manages import from SVG files and converting them into multipath
 * objects
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class SVGMathObject extends MultiShapeObject {

	protected String filename;
	double currentX = 0;
	double currentY = 0;
	double closeX = 0;
	double closeY = 0;
	double previousX = 0;
	double previousY = 0;
	Double anchorX = null;
	Double anchorY = null;

	JMPath importJMPathTemp;// Path to temporary import SVG Path commmands
//    private JMColor currentFillColor;
//    private JMColor currentDrawColor;
//    private double currentStrokeSize = .5d;

	public static SVGMathObject make(String fname) {
		return new SVGMathObject(fname);
	}

	// This empty constructor is needed
	public SVGMathObject() {
		super();
	}

	public SVGMathObject(String fname) {
		super();
		ResourceLoader rl = new ResourceLoader();
		URL urlImage = rl.getResource(fname, "images");
		try {
			importSVG(urlImage);
		} catch (Exception ex) {
			Logger.getLogger(SVGMathObject.class.getName()).log(Level.SEVERE, null, ex);
		}
//        currentFillColor = getMp().getFillColor().copy();
//        currentDrawColor = getMp().getDrawColor().copy();
	}

	public SVGMathObject(URL url) {
		super();
		try {
			importSVG(url);
		} catch (Exception ex) {
			Logger.getLogger(SVGMathObject.class.getName()).log(Level.SEVERE, null, ex);
		}
//        currentFillColor = getMp().getFillColor().copy();
//        currentDrawColor = getMp().getDrawColor().copy();
	}

	protected final void importSVG(File file) throws Exception {
		JMathAnimScene.logger.debug("Importing SVG file {}", file.getCanonicalPath());
		importSVG(file.toURI().toURL());
	}

	protected final void importSVG(URL urlSvg) throws Exception {

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		// Disabling these features will speed up the load of the svg
		dbFactory.setFeature("http://xml.org/sax/features/namespaces", false);
		dbFactory.setFeature("http://xml.org/sax/features/validation", false);
		dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
		dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		org.w3c.dom.Document doc = dBuilder.parse(urlSvg.openStream());

		// Look for svg elements in the root document
		getMp().setAbsoluteThickness(false);// Default behaviour
		processChildNodes((doc.getDocumentElement()), getMp().getFirstMP());
		stackTo(new Point(0, 0), Anchor.Type.UL);
	}

	private void processChildNodes(Element gNode, MODrawProperties localMP) throws NumberFormatException {
		NodeList nList = gNode.getChildNodes();
		// localMP holds the base MODrawProperties to apply to all childs
		MODrawProperties mpCopy;
		for (int nchild = 0; nchild < nList.getLength(); nchild++) {
			Node node = nList.item(nchild);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element el = (Element) node;
//                MODrawProperties ShMp = this.getMp().getFirstMP().copy();
//                ShMp.absoluteThickness = false;
//                    ShMp.fillColor.set(JMColor.random());
				switch (el.getTagName()) {
				case "g":
					mpCopy = localMP.copy();
					processAttributeCommands(el, mpCopy);
					mpCopy.setThickness(.1);// TODO: Should delete this?
					processChildNodes(el, mpCopy);
					break;
				case "path":
					try {
						JMPath path = processPathCommands(el.getAttribute("d"));
						mpCopy = localMP.copy();
						processAttributeCommands(el, mpCopy);
						if (path.jmPathPoints.size() > 0) {
							path.pathType = JMPath.SVG_PATH; // Mark this as a SVG path
							add(new Shape(path, mpCopy));
						}
					} catch (Exception ex) {
						Logger.getLogger(SVGMathObject.class.getName()).log(Level.SEVERE, null, ex);
					}

					break;
				case "rect":
					mpCopy = localMP.copy();
					processAttributeCommands(el, mpCopy);
					double x = Double.parseDouble(el.getAttribute("x"));
					double y = -Double.parseDouble(el.getAttribute("y"));
					double w = Double.parseDouble(el.getAttribute("width"));
					double h = -Double.parseDouble(el.getAttribute("height"));
					shapes.add(Shape.rectangle(new Point(x, y), new Point(x + w, y + h)).setMp(mpCopy));
					break;
				case "circle":
					mpCopy = localMP.copy();
					processAttributeCommands(el, mpCopy);
					double cx = Double.parseDouble(el.getAttribute("cx"));
					double cy = -Double.parseDouble(el.getAttribute("cy"));
					double radius = Double.parseDouble(el.getAttribute("r"));
					shapes.add(Shape.circle().scale(radius).shift(cx, cy).setMp(mpCopy));
					break;
				case "ellipse":
					mpCopy = localMP.copy();
					processAttributeCommands(el, mpCopy);
					double cxe = Double.parseDouble(el.getAttribute("cx"));
					double cye = -Double.parseDouble(el.getAttribute("cy"));
					double rxe = Double.parseDouble(el.getAttribute("rx"));
					double rye = -Double.parseDouble(el.getAttribute("ry"));
					shapes.add(Shape.circle().scale(rxe, rye).shift(cxe, cye).setMp(mpCopy));
					break;

				}

			}
		}
	}

	/**
	 * Takes a string of SVG Path commands and converts then into a JMPathObject
	 * Only fill attribute is parsed into the path
	 *
	 * @param s The string of commands
	 * @return The JMPathObject
	 */
	public JMPath processPathCommands(String s) throws Exception {
		JMPath resul = new JMPath();
		JMPathPoint previousPoint = new JMPathPoint(new Point(0, 0), true, JMPathPointType.VERTEX);
		String t = s.replace("-", " -");// Avoid errors with strings like "142.11998-.948884"
		t = t.replace("e -", "e-");// Avoid errors with numbers in scientific format
		t = t.replace("E -", "E-");// Avoid errors with numbers in scientific format
		t = t.replace("M", " M ");// Adding spaces before and after to all commmands helps me to differentiate
									// easily from coordinates
		t = t.replace("m", " m ");
		t = t.replace("H", " H ");
		t = t.replace("h", " h ");
		t = t.replace("V", " V ");
		t = t.replace("v", " v ");
		t = t.replace("C", " C ");
		t = t.replace("c", " c ");
		t = t.replace("S", " S ");
		t = t.replace("s", " s ");
		t = t.replace("L", " L ");
		t = t.replace("l", " l ");
		t = t.replace("Z", " Z ");
		t = t.replace("z", " z ");
		t = t.replaceAll(",", " ");// Replace all commas with spaces
		t = t.replaceAll("^ +| +$|( )+", "$1");// Removes duplicate spaces

		// Look for second decimal points and add a space. Chains like this "-.5.4"
		// should change to "-.5 .4"
		// TODO: Do it in a more efficient way, maybe with regex patterns
		String[] tokens_1 = t.split(" ");
		ArrayList<String> tokens = new ArrayList<>();
		for (String tok : tokens_1) {
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
			tokens.addAll(Arrays.asList(tok2.split(" ")));
		}

		Iterator<String> it = tokens.iterator();
		double cx1, cx2, cy1, cy2;
		double xx, yy;
		String previousCommand = "";
		currentX = 0;
		currentY = 0;
		previousPoint.p.v.x = 0;
		previousPoint.p.v.y = 0;
		while (it.hasNext()) {
			// Main loop, looking for commands
			String token = it.next().trim();
			switch (token) {
			case "":
				break;
			case "A":
				throw new Exception("Arc command A still not implemented. Sorry.");
			case "a":
				throw new Exception("Arc command a still not implemented. Sorry.");
			case "M":
				previousCommand = token;
				getPoint(it.next(), it.next());
				// First point. Creatline do the same as a the first point
				previousPoint = pathLineTo(resul, currentX, currentY, false);
				closeX = currentX;
				closeY = currentY;
				previousPoint.isThisSegmentVisible = false;
//                    previousPoint = pathM(path, currentX, currentY);
				break;
			case "m":
				previousCommand = token;
				xx = previousPoint.p.v.x;
				yy = previousPoint.p.v.y;
				getPoint(it.next(), it.next());
				currentX += xx;
				currentY += yy;
				closeX = currentX;
				closeY = currentY;
				// First point. Creatline do the same as a the first point
				previousPoint = pathLineTo(resul, currentX, currentY, false);
				previousPoint.isThisSegmentVisible = false;
//                    previousPoint = pathM(path, currentX, currentY);
				break;

			case "L": // Line
				previousCommand = token;
				getPoint(it.next(), it.next());
				previousPoint = pathLineTo(resul, currentX, currentY, true);
				break;
			case "l": // Line
				previousCommand = token;
				xx = previousPoint.p.v.x;
				yy = previousPoint.p.v.y;
				getPoint(it.next(), it.next());
				currentX += xx;
				currentY += yy;
				previousPoint = pathLineTo(resul, currentX, currentY, true);
				break;

			case "H": // Horizontal line
				previousCommand = token;

				getPointX(it.next());
				previousPoint = pathLineTo(resul, currentX, currentY, true);
				break;

			case "h": // Horizontal line
				previousCommand = token;

				xx = previousPoint.p.v.x;
				getPointX(it.next());
				currentX += xx;
				previousPoint = pathLineTo(resul, currentX, currentY, true);
				break;
			case "V": // Vertical line
				previousCommand = token;

				getPointY(it.next());
				previousPoint = pathLineTo(resul, currentX, currentY, true);
				break;
			case "v": // Vertical line
				previousCommand = token;

				yy = previousPoint.p.v.y;
				getPointY(it.next());
				currentY += yy;
				previousPoint = pathLineTo(resul, currentX, currentY, true);
				break;
			case "C": // Cubic Bezier
				previousCommand = token;

				cx1 = Double.parseDouble(it.next());
				cy1 = -Double.parseDouble(it.next());
				cx2 = Double.parseDouble(it.next());
				cy2 = -Double.parseDouble(it.next());
				getPoint(it.next(), it.next());
				previousPoint = pathCubicBezier(resul, previousPoint, cx1, cy1, cx2, cy2, currentX, currentY);
				break;
			// c 1,1 2,2 3,3 4,4 5,5 6,6 would become C 1,1 2,2 3,3 C 7,7 8,8 9,9
			case "c": // Cubic Bezier
				previousCommand = token;

				xx = previousPoint.p.v.x;
				yy = previousPoint.p.v.y;
				cx1 = xx + Double.parseDouble(it.next());
				cy1 = yy - Double.parseDouble(it.next());
				cx2 = xx + Double.parseDouble(it.next());
				cy2 = yy - Double.parseDouble(it.next());
				getPoint(it.next(), it.next());
				currentX += xx;
				currentY += yy;

				previousPoint = pathCubicBezier(resul, previousPoint, cx1, cy1, cx2, cy2, currentX, currentY);
				break;
			case "S": // Simplified Cubic Bezier. Take first control point as a reflection of previous
						// one
				previousCommand = token;

				cx1 = previousPoint.p.v.x - (previousPoint.cpEnter.v.x - previousPoint.p.v.x);
				cy1 = previousPoint.p.v.y - (previousPoint.cpEnter.v.y - previousPoint.p.v.y);
				cx2 = Double.parseDouble(it.next());
				cy2 = -Double.parseDouble(it.next());
				getPoint(it.next(), it.next());
				previousPoint = pathCubicBezier(resul, previousPoint, cx1, cy1, cx2, cy2, currentX, currentY);
				break;

			case "s": // Simplified relative Cubic Bezier. Take first control point as a reflection of
						// previous one
				previousCommand = token;

				cx1 = previousPoint.p.v.x - (previousPoint.cpEnter.v.x - previousPoint.p.v.x);
				cy1 = previousPoint.p.v.y - (previousPoint.cpEnter.v.y - previousPoint.p.v.y);
				xx = previousPoint.p.v.x;
				yy = previousPoint.p.v.y;
				cx2 = xx + Double.parseDouble(it.next());
				cy2 = yy - Double.parseDouble(it.next());
				getPoint(it.next(), it.next());
				currentX += xx;
				currentY += yy;
				previousPoint = pathCubicBezier(resul, previousPoint, cx1, cy1, cx2, cy2, currentX, currentY);
				break;
			case "Z":
				previousCommand = token;
				previousPoint = pathLineTo(resul, closeX, closeY, true);
				break;
			case "z":
				previousCommand = token;
				previousPoint = pathLineTo(resul, closeX, closeY, true);
				break;
			default:
				if (!"".equals(token.substring(0, 1))) // Not a command, but a point!
				{
					switch (previousCommand) {
					case "M":
						previousCommand = "L";
						getPointX(token);
						getPointY(it.next());
						previousPoint = pathLineTo(resul, currentX, currentY, true);
						break;
					case "L":
						previousCommand = "L";
						getPointX(token);
						getPointY(it.next());
						previousPoint = pathLineTo(resul, currentX, currentY, true);
						break;
					case "m":
						previousCommand = "l";
						xx = previousPoint.p.v.x;
						yy = previousPoint.p.v.y;
						getPointX(token);
						getPointY(it.next());
						currentX += xx;
						currentY += yy;
						previousPoint = pathLineTo(resul, currentX, currentY, true);
						break;
					case "l":
						previousCommand = "l";
						xx = previousPoint.p.v.x;
						yy = previousPoint.p.v.y;
						getPointX(token);
						getPointY(it.next());
						currentX += xx;
						currentY += yy;
						previousPoint = pathLineTo(resul, currentX, currentY, true);
						break;
					case "C":
						cx1 = Double.parseDouble(token);
						cy1 = -Double.parseDouble(it.next());
						cx2 = Double.parseDouble(it.next());
						cy2 = -Double.parseDouble(it.next());
						getPoint(it.next(), it.next());
						previousPoint = pathCubicBezier(resul, previousPoint, cx1, cy1, cx2, cy2, currentX, currentY);
						break;
					case "c":
						xx = previousPoint.p.v.x;
						yy = previousPoint.p.v.y;
						cx1 = xx + Double.parseDouble(token);
						cy1 = yy - Double.parseDouble(it.next());
						cx2 = xx + Double.parseDouble(it.next());
						cy2 = yy - Double.parseDouble(it.next());
						getPoint(it.next(), it.next());
						currentX += xx;
						currentY += yy;
						previousPoint = pathCubicBezier(resul, previousPoint, cx1, cy1, cx2, cy2, currentX, currentY);
						break;
					case "S": // Simplified Cubic Bezier. Take first control point as a reflection of previous
								// one
						cx1 = previousPoint.p.v.x - (previousPoint.cpEnter.v.x - previousPoint.p.v.x);
						cy1 = previousPoint.p.v.y - (previousPoint.cpEnter.v.y - previousPoint.p.v.y);
						cx2 = Double.parseDouble(token);
						cy2 = -Double.parseDouble(it.next());
						getPoint(it.next(), it.next());
						previousPoint = pathCubicBezier(resul, previousPoint, cx1, cy1, cx2, cy2, currentX, currentY);
						break;
					case "s": // Simplified relative Cubic Bezier. Take first control point as a reflection of
								// previous one
						cx1 = previousPoint.p.v.x - (previousPoint.cpEnter.v.x - previousPoint.p.v.x);
						cy1 = previousPoint.p.v.y - (previousPoint.cpEnter.v.y - previousPoint.p.v.y);
						xx = previousPoint.p.v.x;
						yy = previousPoint.p.v.y;
						cx2 = xx + Double.parseDouble(token);
						cy2 = yy - Double.parseDouble(it.next());
						getPoint(it.next(), it.next());
						currentX += xx;
						currentY += yy;
						previousPoint = pathCubicBezier(resul, previousPoint, cx1, cy1, cx2, cy2, currentX, currentY);
						break;
					default:
						JMathAnimScene.logger.error("Unknow repeated command: <" + token + ">");

					}

				}
			}
		}

		return resul;
	}

	private void getPoint(String x, String y) throws NumberFormatException {
		getPointX(x);
		getPointY(y);
	}

	private void getPointX(String x) throws NumberFormatException {
		previousX = currentX;
		currentX = Double.parseDouble(x);
	}

	private void getPointY(String y) throws NumberFormatException {
		previousY = currentY;
		currentY = -Double.parseDouble(y);
	}

	private JMPathPoint pathCubicBezier(JMPath path, JMPathPoint previousPoint, double cx1, double cy1, double cx2,
			double cy2, double x, double y) {
		JMPathPoint point = new JMPathPoint(new Point(currentX, currentY), true, JMPathPointType.VERTEX);
		point.isCurved = true;
		previousPoint.cpExit.v.x = cx1;
		previousPoint.cpExit.v.y = cy1;
		point.cpEnter.v.x = cx2;
		point.cpEnter.v.y = cy2;
		path.addJMPoint(point);
		return point;
	}

	// Adds a simple point to the path, with control points equal to the point
	private JMPathPoint pathLineTo(JMPath path, double currentX, double currentY, boolean isVisible) {
		JMPathPoint point = new JMPathPoint(new Point(currentX, currentY), isVisible, JMPathPointType.VERTEX);
		point.isCurved = false;
		point.cpExit.v.x = currentX;
		point.cpExit.v.y = currentY;
		point.cpEnter.v.x = currentX;
		point.cpEnter.v.y = currentY;
		path.addJMPoint(point);
		return point;
	}

	private void processAttributeCommands(Element el, MODrawProperties ShMp) {
		if (!"".equals(el.getAttribute("style"))) {
			parseStyleAttribute(el.getAttribute("style"), ShMp);
		}
		if (!"".equals(el.getAttribute("stroke"))) {
			JMColor strokeColor = JMColor.parse(el.getAttribute("stroke"));
			ShMp.setDrawColor(strokeColor);
		}

		if (!"".equals(el.getAttribute("stroke-width"))) {
			double th = Double.parseDouble(el.getAttribute("stroke-width"));
			Camera cam = (Camera) JMathAnimConfig.getConfig().getCamera();
//              ShMp.thickness=cam.mathToScreenFX(th)/cam.getMathView().getWidth();
			ShMp.thickness = cam.mathToScreen(th) / 4;
		}

		if (!"".equals(el.getAttribute("fill"))) {
			JMColor fillColor = JMColor.parse(el.getAttribute("fill"));
			ShMp.setFillColor(fillColor);
		}

	}

	private void parseStyleAttribute(String str, MODrawProperties ShMp) {
		String[] decls = str.split(";");
		for (String pairs : decls) {
			String[] decl = pairs.split(":");
			switch (decl[0]) {
			case "fill":
				JMColor fillColor = JMColor.parse(decl[1]);
				ShMp.setFillColor(fillColor);
				break;
			case "stroke":
				JMColor strokeColor = JMColor.parse(decl[1]);
				ShMp.setDrawColor(strokeColor);
				break;
			}

		}
	}

	@Override
	public SVGMathObject copy() {
		SVGMathObject resul = new SVGMathObject();
		for (Shape sh : shapes) {
			final Shape copy = sh.copy();
			resul.add(copy);
		}
		resul.getMp().copyFrom(getMp());
		resul.absoluteSize = this.absoluteSize;
		return resul;
	}
}
