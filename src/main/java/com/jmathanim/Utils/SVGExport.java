package com.jmathanim.Utils;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Enum.GradientCycleMethod;
import com.jmathanim.Enum.StrokeLineCap;
import com.jmathanim.Enum.StrokeLineJoin;
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
    private MathObjectGroup objects;
    private boolean useMathView;
    Rect svgView;

    public SVGExport(JMathAnimScene scene) {
        this.scene = scene;
        svgCode = new StringBuilder();
        svgCodeDefs = new StringBuilder();
        svgCodeDefs.append("<defs>\n");
        objects = MathObjectGroup.make();
        styleCounter = 0;
        paintStyleNames = new HashMap<>();
        useMathView=false;

    }

    public boolean isUseMathView() {
        return useMathView;
    }

    public void setUseMathView(boolean useMathView) {
        this.useMathView = useMathView;
    }

    public String getSVGCode() {
        objects = getFlattenedListOfMathObjects();

        svgCode.append(generateSVGHeaderForSVGExport());
        //get a flattened list of all objects in the scene

        //First, process all objects to look for different color and gradients
        populateStyles();
        svgCodeDefs.append("</defs>\n");
        svgCode.append(svgCodeDefs);

        //Now process the objects!
        for (MathObject<?> ob : objects) {
            processObjectsToSVG(ob);
        }

        svgCode.append("</g>\n</svg>");
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
            svg.append("Z");
        }

        svg.append("\" ");
        //Color attributes
        PaintStyle<?> drawColor = shape.getMp().getDrawColor();
        if (drawColor instanceof JMColor) {
            svg.append(svgJMColorAttributes("stroke", (JMColor) drawColor)).append(" ");
        } else {
            //Must be a style stored in defs
            svg.append("stroke=\"url(#").append(paintStyleNames.get(drawColor)).append(")\" ");
        }

        PaintStyle<?> fillColor = shape.getMp().getFillColor();
        if (fillColor instanceof JMColor) {
            svg.append(svgJMColorAttributes("fill", (JMColor) fillColor)).append(" ");
        } else {
            //Must be a style stored in defs
            svg.append("fill=\"url(#").append(paintStyleNames.get(fillColor)).append(")\" ");
        }


        svg.append(svgThickness(shape.getMp().getThickness(), shape.getCamera())).append(" ");
        svg.append(getSvgStrokeLineCap(shape.getMp().getLineCap())).append(" ");
        svg.append(getSvgStrokeLineJoin(shape.getMp().getLineJoin())).append(" ");
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
        double w = svgView.getWidth();//camera.getMathView().getWidth();
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
            svgCodeDefs.append(jmRadialGradientToSVGDefinition(rg, styleName));
        }

        if (paintStyle instanceof JMImagePattern) {
            JMImagePattern im = (JMImagePattern) paintStyle;
        }
        paintStyleNames.put(paintStyle, styleName);
        styleCounter++;
    }

    private String getSvgStrokeLineCap(StrokeLineCap cap) {
        String svgValue;
        switch (cap) {
            case SQUARE:
                svgValue = "square";
                break;
            case BUTT:
                svgValue = "butt";
                break;
            case ROUND:
                svgValue = "round";
                break;
            default:
                throw new IllegalArgumentException("StrokeLineCap not supported: " + cap);
        }
        return "stroke-linecap=\"" + svgValue + "\"";
    }
    private String getSvgStrokeLineJoin(StrokeLineJoin join) {
        String svgValue;

        switch (join) {
            case MITER:
                svgValue = "miter";
                break;
            case BEVEL:
                svgValue = "bevel";
                break;
            case ROUND:
                svgValue = "round";
                break;
            default:
                throw new IllegalArgumentException("StrokeLineJoin not supported: " + join);
        }

        return "stroke-linejoin=\"" + svgValue + "\"";
    }

    private String jmRadialGradientToSVGDefinition(JMRadialGradient rg, String name) {
        StringBuilder svg = new StringBuilder();
        svg.append("<radialGradient id=\"").append(name)
                .append("\" ")
                .append("cx=\"")
                .append(rg.getCenter().x * 100)
                .append("%\" ")
                .append("cy=\"")
                .append((1 - rg.getCenter().y) *100)
                .append("%\" ")
                .append("r=\"")
                .append(rg.getRadius() * 100)
                .append("%\" ")
                .append("gradientUnits=\"")
                .append(rg.isRelativeToShape() ? "objectBoundingBox" : "userSpaceOnUse")
                .append("\" ")
                .append(getSvgSpreadMethod(rg.getCycleMethod()))
                .append(">\n");
        TreeMap<Double, JMColor> colors = rg.getStops().getColorTreeMap();
        addStopsToGradientDefinition(colors, svg);
        return svg.append("</radialGradient>\n").toString();
    }


    private String jmLinearGradientToSVGDefinition(JMLinearGradient lg, String name) {
        StringBuilder svg = new StringBuilder();
        svg.append("<linearGradient id=\"").append(name)
                .append("\" ")
                .append("x1=\"")
                .append(lg.getStart().x * 100)
                .append("%\" ")
                .append("y1=\"")
                .append((1 - lg.getStart().y) * 100)
                .append("%\" ")
                .append("x2=\"")
                .append(lg.getEnd().x * 100)
                .append("%\" ")
                .append("y2=\"")
                .append((1 - lg.getEnd().y) * 100)
                .append("%\" ")
                .append("gradientUnits=\"")
                .append(lg.isRelativeToShape() ? "objectBoundingBox" : "userSpaceOnUse")
                .append("\" ")
                .append(getSvgSpreadMethod(lg.getCycleMethod()))
                .append(">\n");
        TreeMap<Double, JMColor> colors = lg.getStops().getColorTreeMap();
        addStopsToGradientDefinition(colors, svg);
        return svg.append("</linearGradient>\n").toString();
    }

    private static void addStopsToGradientDefinition(TreeMap<Double, JMColor> colors, StringBuilder svg) {
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
    }

    private String getSvgSpreadMethod(GradientCycleMethod cycle) {
        String svgValue;
        switch (cycle) {
            case NO_CYCLE:
                // NO_CYCLE corresponde al comportamiento por defecto de SVG: "pad"
                svgValue = "pad";
                break;
            case REFLECT:
                svgValue = "reflect";
                break;
            case REPEAT:
                svgValue = "repeat";
                break;
            default:
                throw new IllegalArgumentException("GradientCycleMethod not supported: " + cycle);
        }

        return "spreadMethod=\"" + svgValue + "\"";
    }




    private MathObjectGroup getFlattenedListOfMathObjects() {
        MathObjectGroup mg=MathObjectGroup.make();
        for (MathObject<?> mo : scene.getMathObjects()) {
            getFlattenedListOfMathObjectsAux(mo, mg);
        }
        mg.getObjects().sort((MathObject<?> o1, MathObject<?> o2) -> o1.getLayer().compareTo(o2.getLayer()));

        return mg;
    }

    private void getFlattenedListOfMathObjectsAux(MathObject<?> mo, MathObjectGroup resul) {
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
        double cminX = scene.getCamera().getMathView().xmin;
        double cmaxX = scene.getCamera().getMathView().xmax;
        double cminY = scene.getCamera().getMathView().ymin;
        double cmaxY = scene.getCamera().getMathView().ymax;
        int cwidthPx = scene.getConfig().getMediaWidth();
        int cheightPx = scene.getConfig().getMediaHeight();

        double minX,minY,maxX,maxY,translateY;
        int widthPx,heightPx;
        if (useMathView) {
            minX=cminX;
            minY=cminY;
            maxX=cmaxX;
            maxY=cmaxY;
            widthPx=cwidthPx;
            heightPx=cheightPx;
            translateY=-minY - maxY;
        }else{
            Rect r = objects.getBoundingBox();
            minX=r.xmin;
            minY=r.ymin;
            maxX=r.xmax;
            maxY=r.ymax;
            widthPx= (int) (cwidthPx*(maxX-minX)/(cmaxX-cminX));
            heightPx= (int) (cheightPx*(maxY-minY)/(cmaxY-cminY));
            translateY=-minY - maxY;
        }

        svgView = new Rect(minX, minY, maxX, maxY);


        double viewBoxWidth = maxX - minX;
        double viewBoxHeight = maxY - minY;

        String svgHeader = String.format(
                "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"%d\" height=\"%d\" viewBox=\"%f %f %f %f\">\n" +
                        "  <g transform=\"scale(1,-1) translate(0,%f)\">\n",
                widthPx, heightPx,
                minX, minY, viewBoxWidth, viewBoxHeight, translateY
        );

        return svgHeader;
    }
}
