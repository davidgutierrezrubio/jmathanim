package com.jmathanim.Utils;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.MathObjects.*;
import com.jmathanim.MathObjects.Shapes.JMPath;
import com.jmathanim.MathObjects.Shapes.JMPathPoint;
import com.jmathanim.Styling.*;
import com.jmathanim.jmathanim.JMathAnimScene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class SVGExport {
    private final JMathAnimScene scene;
    StringBuilder svgCode;
    StringBuilder svgCodeDefs;
    HashMap<PaintStyle<?>, String> paintStyleNames;
    int styleCounter;
    private ArrayList<MathObject<?>> objects;

    public SVGExport(JMathAnimScene scene) {
        this.scene = scene;
        svgCode = new StringBuilder();
        svgCodeDefs = new StringBuilder();
        svgCodeDefs.append("<defs>\n");
        objects = new ArrayList<>();
        styleCounter = 0;
        paintStyleNames = new HashMap<>();
    }

    public String getSVGCode() {
        //get a flattened list of all objects in the scene
        objects = getFlattenedListOfMathObjects();
        //First, process all objects to look for different color and gradients
        populateStyles();
        svgCodeDefs.append("</defs>\n");
        svgCode.append(generateSVGHeaderForSVGExport());
        svgCode.append(svgCodeDefs);

        //Now process the objects!
        for(MathObject<?> ob:objects) {
            processObjectsToSVG(ob);
        }

        svgCode.append("</svg>");
        return svgCode.toString();
    }


    private void processObjectsToSVG(MathObject<?> mathObject) {
        if (mathObject instanceof AbstractShape<?>) {
            svgCode.append(shapeToSVGPath(scene, (AbstractShape<?>) mathObject));
        }
        if (mathObject instanceof AbstractMathGroup<?>) {
            AbstractMathGroup<?> group = (AbstractMathGroup<?>) mathObject;
            ArrayList<MathObject<?>> objects = group.getObjects();
            for (int i = 0; i < objects.size(); i++) {
                MathObject<?> g = objects.get(i);
                processObjectsToSVG(g);
            }
        }
        if (mathObject instanceof AbstractMultiShapeObject<?, ?>) {
            AbstractMultiShapeObject<?, ?> msh = (AbstractMultiShapeObject<?, ?>) mathObject;
            for (int i = 0; i < msh.size(); i++) {
                AbstractShape<?> g = msh.get(i);
                processObjectsToSVG(g);
            }
        }
        if (mathObject instanceof AbstractPoint<?>) {
            AbstractPoint<?> p = (AbstractPoint<?>) mathObject;
            Shape dotShape = p.getDotShape().copy();
            dotShape.setCamera(scene.getFixedCamera());
            Vec anchor = p.v;
            AffineJTransform fixedToMedia = cameraToScreen(scene.getFixedCamera());
            AffineJTransform cameraToMedia = cameraToScreen(scene.getCamera());
            Vec vFixed = anchor.copy().applyAffineTransform(cameraToMedia.compose(fixedToMedia.getInverse()));
            dotShape.shift(vFixed.minus(anchor));

            processObjectsToSVG(dotShape);
        }
    }


    private String shapeToSVGPath(JMathAnimScene scene, AbstractShape<?> shape) {
        JMPath path = shape.getPath();
        if (shape.getCamera() == scene.getFixedCamera()) {
            AffineJTransform fixedToMedia = cameraToScreen(scene.getFixedCamera());
            AffineJTransform cameraToMedia = cameraToScreen(scene.getCamera());
            path = path.copy().applyAffineTransform(fixedToMedia.compose(cameraToMedia.getInverse()));
        }
        StringBuilder svg = new StringBuilder();
        svg.append("<path d=\"M ")
                .append(path.get(0).getV().x)
                .append(" ")
                .append(path.get(0).getV().y)
                .append(" ");
        for (int i = 1; i <= path.size(); i++) {
            JMPathPoint jmp = path.get(i);
            JMPathPoint jmpPrev = path.get(i - 1);

            if (!jmp.isSegmentToThisPointVisible()) {
                svg.append("M ");//Invisible, move to
                svg.append(jmp.getV().x);
                svg.append(" ");
                svg.append(jmp.getV().y);
            } else {
                if ((jmp.isSegmentToThisPointCurved()) && (isEffectivelyCurved(jmpPrev, jmp))) {
                    //Cubic Bezier
                    svg.append("C ")
                            .append(jmpPrev.getVExit().x)
                            .append(" ")
                            .append(jmpPrev.getVExit().y)
                            .append(" ")
                            .append(jmp.getVEnter().x)
                            .append(" ")
                            .append(jmp.getVEnter().y)
                            .append(" ")
                            .append(jmp.getV().x)
                            .append(" ")
                            .append(jmp.getV().y)
                            .append(" ");
                } else {
                    svg.append("L ") //Straight line
                            .append(jmp.getV().x)
                            .append(" ")
                            .append(jmp.getV().y)
                            .append(" ");

                }
            }
        }
        if (path.get(0).isSegmentToThisPointVisible()) {
            svg.append("Z ");
        }

        svg.append("\" ");
        //Color attributes
        PaintStyle<?> drawColor = shape.getMp().getDrawColor();
        if (drawColor instanceof  JMColor) {
            svg.append(svgJMColorAttributes("stroke", (JMColor) drawColor)).append(" ");
        }else {
            //Must be a style stored in defs
            svg.append("stroke=\"url(#").append(paintStyleNames.get(drawColor)).append(")\" ");
        }

        PaintStyle<?> fillColor = shape.getMp().getFillColor();
        if (fillColor instanceof  JMColor) {
            svg.append(svgJMColorAttributes("fill", (JMColor) fillColor)).append(" ");
        }else {
            //Must be a style stored in defs
            svg.append("fill=\"url(#").append(paintStyleNames.get(fillColor)).append(")\" ");
        }


        svg.append(svgThickness(shape.getMp().getThickness(), shape.getCamera())).append(" ");
        return svg.append("/>\n").toString();
    }


    private String svgJMColorAttributes(String name, JMColor color) {
        double red = color.getRed();
        double green = color.getGreen();
        double blue = color.getBlue();
        double alpha = color.getAlpha();
        int r = (int) Math.round(red * 255);
        int g = (int) Math.round(green * 255);
        int b = (int) Math.round(blue * 255);
        return String.format(name + "=\"rgb(%d,%d,%d)\" " + name + "-opacity=\"%.3f\"", r, g, b, alpha);
    }

    private String svgThickness(double thickness, Camera camera) {
        double w = camera.getMathView().getWidth();
        return String.format("stroke-width=\"%.6f\"", thickness / 5000 * w);
    }


    private AffineJTransform cameraToScreen(Camera camera) {
        Rect r = camera.getMathView();
        return AffineJTransform.createAffineTransformation(
                r.getLowerLeft(), r.getUpperLeft(), r.getUpperRight(),
                Vec.to(0, 0), Vec.to(0, camera.getScreenHeight()), Vec.to(camera.getScreenWidth(), camera.getScreenHeight()), 1);

    }


    private void populateStyles() {
        for (MathObject<?> mo : objects) {
            DrawStyleProperties mp = mo.getMp();
            PaintStyle<?> drawStyle = mp.getDrawColor();
            PaintStyle<?> fillStyle = mp.getFillColor();
            processPaintStyle(drawStyle);
            processPaintStyle(fillStyle);
        }
    }

    private void processPaintStyle(PaintStyle<?> paintStyle) {
        if (paintStyleNames.containsKey(paintStyle)) {
            return;//This PaintStyle has already been processed and added to definitions
        }
        String styleName = "style" + styleCounter;
        if (paintStyle instanceof JMLinearGradient) {
            JMLinearGradient lg = (JMLinearGradient) paintStyle;
            svgCodeDefs.append(jmLinearGradientToSVGDefinition(lg, styleName));
        }
        if (paintStyle instanceof JMRadialGradient) {
            JMRadialGradient rg = (JMRadialGradient) paintStyle;
        }

        if (paintStyle instanceof JMImagePattern) {
            JMImagePattern im = (JMImagePattern) paintStyle;
        }
        paintStyleNames.put(paintStyle, styleName);
        styleCounter++;
    }

    public String jmLinearGradientToSVGDefinition(JMLinearGradient lg, String name) {
        StringBuilder svg = new StringBuilder();
        svg.append("<linearGradient id=\"").append(name)
                .append("\" ")
                .append("x1=\"")
                .append(lg.getStart().x * 100)
                .append("%\" ")
                .append("y1=\"")
                .append((1-lg.getStart().y) * 100)
                .append("%\" ")
                .append("x2=\"")
                .append(lg.getEnd().x * 100)
                .append("%\" ")
                .append("y2=\"")
                .append((1-lg.getEnd().y) * 100)
                .append("%\">\n");
        //<stop offset="0%" style="stop-color:rgb(255,0,0);stop-opacity:1" />
        TreeMap<Double, JMColor> colors = lg.getStops().getColorTreeMap();
        for (Double t : colors.keySet()) {
            JMColor col = colors.get(t);
            svg.append("<stop offset=\"")
                    .append(t * 100)
                    .append("%\" ")
                    .append("style=\"stop-color:rgb(")
                    .append(Math.round(col.getRed() * 255))
                    .append(",")
                    .append(Math.round(col.getGreen() * 255))
                    .append(",")
                    .append(Math.round(col.getBlue() * 255))
                    .append(");stop-opacity:")
                    .append(col.getAlpha())
                    .append("\"/>\n");
        }
        return svg.append("</linearGradient>\n").toString();
    }


    private ArrayList<MathObject<?>> getFlattenedListOfMathObjects() {
        ArrayList<MathObject<?>> resul = new ArrayList<>();
        for (MathObject<?> mo : scene.getMathObjects()) {
            getFlattenedListOfMathObjectsAux(mo, resul);
        }
        return resul;
    }

    private void getFlattenedListOfMathObjectsAux(MathObject<?> mo, ArrayList<MathObject<?>> resul) {
        if (mo instanceof AbstractMathGroup) {//This is not necessary, but just in case for future versions...
            AbstractMathGroup<?> abstractMathGroup = (AbstractMathGroup<?>) mo;
            for (MathObject<?> mo2 : abstractMathGroup.getObjects()) {
                getFlattenedListOfMathObjectsAux(mo2, resul);
            }
            return;
        }
        if (mo instanceof AbstractMultiShapeObject<?, ?>) {
            AbstractMultiShapeObject<?, ?> abstractMultiShapeObject = (AbstractMultiShapeObject<?, ?>) mo;
            for (AbstractShape<?> mo2 : abstractMultiShapeObject.getShapes()) {
                getFlattenedListOfMathObjectsAux(mo2, resul);
            }
            return;
        }
        if (mo instanceof RigidBox) {
            RigidBox rm = (RigidBox) mo;
            resul.add(rm.getMathObjectCopyToDraw());
        }
        resul.add(mo);
    }


    private boolean isEffectivelyCurved(JMPathPoint jmpPrev, JMPathPoint jmp) {
        boolean curved = true;
        curved &= !jmpPrev.getVExit().isEquivalentTo(jmpPrev.getV(), .000001);
        curved &= !jmp.getVEnter().isEquivalentTo(jmp.getV(), .000001);
        return curved;
    }


    private String generateSVGHeaderForSVGExport() {
        double minX = scene.getCamera().getMathView().xmin;//Should use rect of all objects instead?
        double maxX = scene.getCamera().getMathView().xmax;
        double minY = scene.getCamera().getMathView().ymin;
        double maxY = scene.getCamera().getMathView().ymax;
        int widthPx = scene.getConfig().getMediaWidth();
        int heightPx = scene.getConfig().getMediaHeight();
        // Calculamos ancho y alto del viewBox en coordenadas matemáticas
        double viewBoxWidth = maxX - minX;
        double viewBoxHeight = maxY - minY;

        // Translate necesario para corregir Y tras invertir el eje
        double translateY = -viewBoxHeight;

        // Generamos la cabecera SVG
        String svgHeader = String.format(
                "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"%d\" height=\"%d\" viewBox=\"%f %f %f %f\">\n" +
                        "  <g transform=\"scale(1,-1) translate(0,%f)\"/>\n",
                widthPx, heightPx,
                minX, minY, viewBoxWidth, viewBoxHeight, -minY - maxY
        );

        return svgHeader;
    }
}
