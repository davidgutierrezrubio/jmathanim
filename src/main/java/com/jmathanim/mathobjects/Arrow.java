/*
 * Copyright (C) 2023 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Cameras.Camera3D;
import com.jmathanim.Constructible.Conics.CTCircleArc;
import com.jmathanim.Constructible.Constructible;
import com.jmathanim.Constructible.Lines.CTLine;
import com.jmathanim.Constructible.Lines.CTPerpBisector;
import com.jmathanim.Constructible.Points.CTIntersectionPoint;
import com.jmathanim.Enum.AnchorType;
import com.jmathanim.Enum.ArrowType;
import com.jmathanim.Enum.SlopeDirectionType;
import com.jmathanim.Styling.DrawStylePropertiesObjectsArray;
import com.jmathanim.Utils.*;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.jmathanim.LogUtils;
import com.jmathanim.mathobjects.Text.AbstractLatexMathObject;
import com.jmathanim.mathobjects.Tippable.LabelTip;
import com.jmathanim.mathobjects.updaters.Updater;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;

import static com.jmathanim.jmathanim.JMathAnimScene.PI;
import static com.jmathanim.jmathanim.JMathAnimScene.logger;

/**
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Arrow extends Constructible<Arrow> {

    public final Shape labelArcUpside;
    public final Shape labelArcDownside;
    protected final DrawStylePropertiesObjectsArray mpArrow;
    protected final MathObjectGroup groupElementsToBeDrawn;
    private final Vec Acopy, Bcopy;
    private final Shape shapeToDraw;
    private final JMPath head1, head2;
    private final Coordinates<?> A, B;
    private double amplitudeScale;
    private double angle;
    private double baseHeight1;
    private double baseHeight2;
    private double baseRealHeight1;
    private double baseRealHeight2;
    private double headStartMultiplier, headEndMultiplier;
    private double gapA, gapB;
    private double baseDist1, baseDist2;
    private double arrowThickness;
    private ArrowType typeA, typeB;
    private LabelTip labelTip;
    private labelTypeEnum labelType;
    private String stringFormat;

    private Arrow(Coordinates<?> A, Coordinates<?> B) {
        this.A = A;
        this.B = B;
        labelType = labelTypeEnum.NORMAL;
        this.labelArcUpside = new Shape();
        this.labelArcDownside = new Shape();
        this.labelTip = null;
        head1 = new JMPath();
        head2 = new JMPath();
        arrowThickness = 20;//TODO: Default value. This should be in config file
        setAmplitudeScale(1d);
        headStartMultiplier = 1d;
        headEndMultiplier = 1d;
        shapeToDraw = new Shape();
        Acopy = this.A.getVec().copy();
        Bcopy = this.B.getVec().copy();
        mpArrow = new DrawStylePropertiesObjectsArray();
        mpArrow.add(shapeToDraw);
        groupElementsToBeDrawn = MathObjectGroup.make(shapeToDraw);
        getMp().loadFromStyle("ARROWDEFAULT");
    }

    /**
     * Returns a shape for a given ArrowType
     *
     * @param type Arrow type, a value of enum ArrowType
     * @return A Shape with the arrow head generated
     */
    public static Shape buildArrowHead(ArrowType type) {
        Shape resul;
        try {
            resul = new Shape(loadArrowHeadPath(type));
        } catch (Exception e) {
            JMathAnimScene.logger.error("An exception occurred loading arrow models, returning empty Shape");
            JMathAnimScene.logger.error(e.getMessage());
            resul = new Shape();
        }
        resul.style("arrowdefault");
        resul.getPath().closePath();
        return resul;
    }

    /**
     * Creates a new Arrow object with given start/end points and arrow type.
     *
     * @param A    Starting point
     * @param B    Ending point
     * @param type Arrow type. A value of enum Arrowtype
     * @return The created object
     */
    public static Arrow make(Coordinates<?> A, Coordinates<?> B, ArrowType type) {
        Arrow resul = new Arrow(A, B);
        resul.typeA = ArrowType.NONE_BUTT;
        resul.typeB = type;
        resul.safeLoadModels();
        resul.rebuildShape();
        resul.scene = JMathAnimConfig.getConfig().getScene();
        return resul;
    }

    public static Arrow makeDouble(Coordinates<?> A, Coordinates<?> B, ArrowType typeA, ArrowType typeB) {
        Arrow resul = new Arrow(A, B);
        resul.typeA = typeA;
        resul.typeB = typeB;
        resul.angle = 0;
        resul.safeLoadModels();
        resul.rebuildShape();
        resul.scene = JMathAnimConfig.getConfig().getScene();
        return resul;
    }

    public static JMPath loadArrowHeadPath(ArrowType type) throws Exception {
        ResourceLoader rl = new ResourceLoader();
        URL arrowUrl;
        JMPath resul;
        switch (type) {
            //Always FIRST point to the RIGHT,
            //LAST point to the LEFT
            case NONE_BUTT:
                resul = new JMPath();
                resul.addPoint(Vec.to(1, 0), Vec.to(0, 0));
                resul.get(0).setSegmentToThisPointVisible(false);
                return resul;
            case NONE_ROUND:
                resul = Shape.arc(PI).getPath();
                resul.setProperty("gap", -1d);
                return resul;
            case NONE_SQUARE:
                resul = new JMPath();
                resul.addPoint(Vec.to(1, 0), Vec.to(0, 0));
                resul.get(0).setSegmentToThisPointVisible(false);
                return resul;
            case ARROW1:
                return loadArrowHeadFile("#arrow1.svg", "#arrow1.bin", "shapeResources/arrows", rl);
            case ARROW2:
//                arrowUrl = rl.getResource("#arrow2.svg", "shapeResources/arrows");
//                return SVGUtils.importSVG(arrowUrl).get(0).getPath();
                return loadArrowHeadFile("#arrow2.svg", "#arrow2.bin", "shapeResources/arrows", rl);
            case ARROW3:
//                arrowUrl = rl.getResource("#arrow3.svg", "shapeResources/arrows");
//                return SVGUtils.importSVG(arrowUrl).get(0).getPath();
                return loadArrowHeadFile("#arrow3.svg", "#arrow3.bin", "shapeResources/arrows", rl);
            case SQUARE:
//                arrowUrl = rl.getResource("#ArrowSquare.svg", "shapeResources/arrows");
//                resul = SVGUtils.importSVG(arrowUrl).get(0).getPath();
                resul = loadArrowHeadFile("#ArrowSquare.svg", "#ArrowSquare.bin", "shapeResources/arrows", rl);
                resul.setProperty("gap", -resul.getHeight() * .5);
                return resul;
            case BULLET:
                resul = Shape.arc(1.75 * PI).getPath();
                AffineJTransform tr = AffineJTransform.createDirect2DIsomorphic(resul.get(0),
                        resul.get(-1),
                        resul.get(0),
                        resul.get(0).getV().add(-1, 0),
                        1);
                resul.applyAffineTransform(tr);
                resul.setProperty("gap", -resul.getHeight() * .5);
                return resul;
            default:
                throw new AssertionError();
        }
    }

    private static JMPath loadArrowHeadFile(String arrowSVGName, String arrowSerializedName, String folder, ResourceLoader rl) {
        URL arrowUrl;
        JMPath headPath;

        try {
            arrowUrl = rl.getResource(arrowSerializedName, folder);
            ObjectInputStream in = new ObjectInputStream(arrowUrl.openStream());
            headPath = (JMPath) in.readObject();
            logger.debug("Loader serialized arrow head " + arrowSerializedName);
            return headPath;
        } catch (ClassNotFoundException e) {
            logger.warn("ClassNotFoundException when trying to load serialized object "
                    + LogUtils.YELLOW + arrowSerializedName + LogUtils.RESET +
                    ". Loading SVG instead"
            );
        } catch (FileNotFoundException e) {
            logger.warn("FileNotFoundException when trying to load serialized object "
                    + LogUtils.YELLOW + arrowSerializedName + LogUtils.RESET +
                    ". Loading SVG instead"
            );
        } catch (IOException e) {
            logger.warn("IOException when trying to load serialized object "
                    + LogUtils.YELLOW + arrowSerializedName + LogUtils.RESET +
                    ". Loading SVG instead"
            );
        }
        try {
            arrowUrl = rl.getResource(arrowSVGName, folder);
            return SVGUtils.importSVG(arrowUrl).get(0).getPath();
        } catch (FileNotFoundException ex) {
            logger.warn("FileNotFoundException when trying to load SVG internal object "
                    + LogUtils.YELLOW + arrowSVGName + LogUtils.RESET +
                    ". Switching to NONE_BUTT");
            try {
                return loadArrowHeadPath(ArrowType.NONE_BUTT);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void safeLoadModels() {
        try {
            loadModels();
        } catch (Exception e) {
            JMathAnimScene.logger.error("An exception occurred loading arrow models, switch to NONE_BUTT");
            JMathAnimScene.logger.error(e.getMessage());
            typeA = ArrowType.NONE_BUTT;
            typeB = ArrowType.NONE_BUTT;
        }
    }

    @Override
    public DrawStylePropertiesObjectsArray getMp() {
        return mpArrow;
    }

    @Override
    public MathObject getMathObject() {
        return groupElementsToBeDrawn;
    }

    private void loadModels() throws Exception {
        JMPath h1 = loadArrowHeadPath(typeA);
        JMPath h2 = loadArrowHeadPath(typeB);
        h2.scale(-1, -1);

        if (h1.getProperty("gap") != null) {
            gapA = (double) h1.getProperty("gap");
        } else {
            gapA = 0;
        }
        if (h2.getProperty("gap") != null) {
            gapB = (double) h2.getProperty("gap");
        } else {
            gapB = 0;
        }

        head1.getPath().clear();
        head2.getPath().clear();
        head1.getPath().addJMPointsFrom(h1.getPath());
        head2.getPath().addJMPointsFrom(h2.getPath());
        baseDist1 = head1.get(0).to(head1.get(-1)).norm();
        baseDist2 = head2.get(0).to(head2.get(-1)).norm();
        baseHeight1 = head1.getBoundingBox().ymax - head1.get(0).getV().y;
        baseHeight2 = head2.get(0).getV().y - head2.getBoundingBox().ymin;
        baseRealHeight1 = head1.getHeight();
        baseRealHeight2 = head2.getHeight();
    }

    @Override
    public void rebuildShape() {
        if (isFreeMathObject()) return;
        if (scene == null) {
            return;
        }
        //The distScale manages which scale should be the arrow drawn. It is used mostly by ShowCreation animation

        Acopy.copyCoordinatesFrom(A);
        Bcopy.copyCoordinatesFrom(A.getVec().interpolate(B, getAmplitudeScale()));
        JMPath h1A = head1.copy();
        JMPath h1B = head2.copy();
        double dist = Acopy.to(Bcopy).norm();
        if (labelTip != null) {
            labelTip.update(scene);
            labelTip.getMathObject().scale(labelTip.pivotPointRefMathObject, getAmplitudeScale());

        }


        //Scale heads to adjust to thickness
        double rThickness = scene.getRenderer().ThicknessToMathWidth(arrowThickness * getAmplitudeScale());

        //hh=total height of arrow heads if distance between head points was 1 unit apart
        double hh = (baseRealHeight1 - gapA) / baseDist1 + (baseRealHeight2 - gapB) / baseDist2;
//        rThickness = Math.min(rThickness, .75 * dist / hh);
        rThickness = Math.min(rThickness, dist / hh);
        h1A.scale(headStartMultiplier * rThickness / baseDist1);
        h1B.scale(headEndMultiplier * rThickness / baseDist2);

        double rbaseHeight1 = baseHeight1 * headStartMultiplier * rThickness / baseDist1;
        double rbaseHeight2 = baseHeight2 * headEndMultiplier * rThickness / baseDist2;
        double aa = h1B.getHeight();

        Vec medA = h1A.get(0).getVec().interpolate(h1A.get(-1), .5);

        Vec p1b = h1B.get(0).getV();
        Vec p2b = h1B.get(-1).getV();
        Vec medB = p1b.interpolate(p2b, .5);

        h1B.shift(medB.to(medA));
//        scene.add(h1B.copy().layer(3).drawColor("blue"));
//        h1B.shift(h1B.getPoint(-1).to(h1A.getPoint(0)));//Align points 0 of bot shapes
        double rgapA = gapA * headStartMultiplier * rThickness / baseDist1;
        double rgapB = gapB * headEndMultiplier * rThickness / baseDist2;
        shapeToDraw.getPath().clear();
        double longBody = dist - rbaseHeight1 - rbaseHeight2 - rgapA - rgapB;
        Vec endPOintBEfore = h1B.getBoundingBox().getLower().add(0, -rgapB);
        h1B.shift(0, -longBody);
        Vec startPoint = h1A.getBoundingBox().getUpper().add(0, rgapA);
        Vec endPoint = h1B.getBoundingBox().getLower().add(0, -rgapB);

        if (angle == 0) {
            shapeToDraw.getPath().addJMPointsFrom(h1A.getPath());
            shapeToDraw.merge(h1B, true, true);

            labelArcUpside.getPath().clear();
            labelArcUpside.getPath().addPoint(h1A.get(0).copy());
            labelArcUpside.getPath().addPoint(h1B.get(-1).copy());
            labelArcUpside.get(0).setSegmentToThisPointVisible(false);

            labelArcDownside.getPath().clear();
            labelArcDownside.getPath().addPoint(h1A.get(-1).copy());
            labelArcDownside.getPath().addPoint(h1B.get(0).copy());
            labelArcDownside.get(0).setSegmentToThisPointVisible(false);

        } else {

            h1A.rotate(startPoint, angle);
            h1B.rotate(endPoint, -angle);

            //Rectas
//        CTLine ct1 = CTLine.makeLengthMeasure(h1A.get(0).p, h1A.get(-1).p);
            CTPerpBisector ct1 = CTPerpBisector.make(h1A.get(0), h1B.get(-1));
            CTLine ct2 = CTLine.make(h1B.get(0), h1B.get(-1));
            CTIntersectionPoint inter = CTIntersectionPoint.make(ct1, ct2);
            CTCircleArc arc1, arc2;
            Shape shArc1, shArc2;
            AffineJTransform tr;
            if (angle > 0) {
                arc1 = CTCircleArc.make(inter.getMathObject(), h1B.get(-1), h1A.get(0));
                arc2 = CTCircleArc.make(inter.getMathObject(), h1B.get(0), h1A.get(-1));
                shArc1 = (Shape) arc1.getMathObject();
                shArc2 = (Shape) arc2.getMathObject();

                tr = AffineJTransform.createDirect2DIsomorphic(shArc1.get(0), shArc1.get(-1), h1B.get(-1), h1A.get(0), 1);
                shArc1.applyAffineTransform(tr);
                tr = AffineJTransform.createDirect2DIsomorphic(shArc2.get(0), shArc2.get(-1), h1B.get(0), h1A.get(-1), 1);
                shArc2.applyAffineTransform(tr);
            } else {
                arc1 = CTCircleArc.make(inter.getMathObject(), h1A.get(0), h1B.get(-1));
                arc2 = CTCircleArc.make(inter.getMathObject(), h1A.get(-1), h1B.get(0));
                shArc1 = (Shape) arc1.getMathObject();
                shArc2 = (Shape) arc2.getMathObject();

                //Move arrow extremes to the right point
                tr = AffineJTransform.createDirect2DIsomorphic(shArc1.get(0), shArc1.get(-1), h1A.get(0), h1B.get(-1), 1);
                shArc1.applyAffineTransform(tr);
                tr = AffineJTransform.createDirect2DIsomorphic(shArc2.get(0), shArc2.get(-1), h1A.get(-1), h1B.get(0), 1);
                shArc2.applyAffineTransform(tr);
            }

            if (angle > 0) {
                shArc2.reverse();
            } else {
                shArc1.reverse();
            }
            labelArcUpside.getPath().clear();
            labelArcUpside.getPath().copyStateFrom(shArc1.getPath());
            labelArcDownside.getPath().clear();
            labelArcDownside.getPath().copyStateFrom(shArc2.getPath());
            labelArcUpside.getPath().reverse();

            //Build the shape, adding h1c and merging with h2cF
            shapeToDraw.getPath().addJMPointsFrom(h1A.getPath());
            shapeToDraw.merge(shArc2, true, false);
            shapeToDraw.merge(h1B, true, false);
            shapeToDraw.merge(shArc1, true, true);
        }

        //Finally, shift and rotate the built Shape to match A and B points
//        AffineJTransform trShift = AffineJTransform.createTranslationTransform(startPoint.to(Acopy));
//        AffineJTransform trRotate = AffineJTransform.create2DRotationTransform(Acopy, Acopy.to(Bcopy).getAngle() + .5 * PI);
//        shapeToDraw.getPath().applyAffineTransform(trShift);
//        shapeToDraw.getPath().applyAffineTransform(trRotate);

        Vec C = null;
        Vec z1 = null;
        AffineJTransform tr;
        boolean is3D = scene.getCamera() instanceof Camera3D;
        if (is3D) {
            z1 = startPoint.copy().add(Vec.to(0, 0, 1));

            Vec v = Acopy.to(Bcopy);
            if ((v.x == 0) && (v.y == 0)) {
                C = Acopy.getVec().copy().add(Vec.to(0, 1, 0));
            } else {
                C = Acopy.getVec().copy().add(Vec.to(0, 0, 1));
            }
        }


        if (is3D) {
            tr = AffineJTransform.createDirect3DIsomorphic(startPoint, endPoint, z1, Acopy, Bcopy, C, 1);
        } else {
            tr = AffineJTransform.createDirect2DIsomorphic(startPoint, endPoint, Acopy, Bcopy, 1);
        }

        shapeToDraw.getPath().applyAffineTransform(tr);
        labelArcUpside.getPath().applyAffineTransform(tr);
        labelArcDownside.getPath().applyAffineTransform(tr);


        //Now, rotate to face camera3d..
        if (is3D) {
            Camera3D cam = (Camera3D) scene.getCamera();
//            Point C = A.copy().shift(0, 0, 1);
            Vec v = cam.look.to(cam.eye);
            Vec C2 = Acopy.copy().add(v);
            tr = AffineJTransform.createDirect3DIsomorphic(Acopy, Bcopy, C, Acopy, Bcopy, C2, 1);
//            shapeToDraw.applyAffineTransform(tr);
            labelArcUpside.applyAffineTransform(tr);
            labelArcDownside.applyAffineTransform(tr);
        }
    }

    @Override
    public Arrow copy() {
        Arrow copy = new Arrow(A.copy(), B.copy());
        if (labelTip != null) {
            boolean upperSide = true;
            Object upperObj = labelTip.getProperty("upperSide");
            if (upperObj != null) upperSide = (boolean) upperObj;
            copy.labelType=labelType;
            if (labelType == labelTypeEnum.DISTANCE) {
                copy.addLengthLabelTip(stringFormat,  upperSide);
                copy.getLabelTip().setDistanceToShapeRelative(getLabelTip().isDistanceToShapeRelative());
                copy.getLabelTip().setDistanceToShape(getLabelTip().getDistanceToShape());
            }
            if (labelType == labelTypeEnum.COORDS) {
                copy.addVecLabelTip(labelTip.getDistanceToShape(), stringFormat, upperSide);
                copy.getLabelTip().setDistanceToShapeRelative(getLabelTip().isDistanceToShapeRelative());
                copy.getLabelTip().setDistanceToShape(getLabelTip().getDistanceToShape());
            }
            if (labelType==labelTypeEnum.NORMAL) {
                copy.registerLabel(getLabelTip().copy());
            }
        }
        copy.copyStateFrom(this);
        return copy;
    }
//    LabelTip label = LabelTip.makeLabelTip((upperSide ? labelArcUpside : labelArcDownside), .5, "text");
//        label.setProperty("upperSide", upperSide);//This will be useful when copying labels
//    label.distanceToShape = gap;
//        label.setSlopeDirection((upperSide ? SlopeDirectionType.POSITIVE : SlopeDirectionType.NEGATIVE));
//        label.setAnchor(AnchorType.LOWER);
//    registerLabel(label);
//    labelType = labelTypeEnum.NORMAL;
//        this.stringFormat = "";
//        return label;




    @Override
    public void copyStateFrom(Stateable obj) {
        if (!(obj instanceof Arrow)) return;
        super.copyStateFrom(obj);
        Arrow ar = (Arrow) obj;
        this.arrowThickness = ar.arrowThickness;
        this.angle = ar.angle;
        this.scene = ar.scene;
        this.setAmplitudeScale(ar.getAmplitudeScale());
        this.baseHeight1 = ar.baseHeight1;
        this.baseHeight2 = ar.baseHeight2;
        this.baseRealHeight1 = ar.baseRealHeight1;
        this.baseRealHeight2 = ar.baseRealHeight2;
        this.headStartMultiplier = ar.headStartMultiplier;
        this.headEndMultiplier = ar.headEndMultiplier;
        this.typeA = ar.typeA;
        this.typeB = ar.typeB;
        this.A.copyCoordinatesFrom(ar.A);
        this.B.copyCoordinatesFrom(ar.B);
        this.Acopy.copyCoordinatesFrom(ar.Acopy);
        this.Bcopy.copyCoordinatesFrom(ar.Bcopy);
        this.gapA = ar.gapA;
        this.gapB = ar.gapB;
        this.baseDist1 = ar.baseDist1;
        this.baseDist2 = ar.baseDist2;
        this.setFreeMathObject(ar.isFreeMathObject());


        JMPath copyPath = ar.head1.getPath().copy();
        this.head1.getPath().clear();
        this.head1.getPath().addJMPointsFrom(copyPath);

        copyPath = ar.head2.getPath().copy();
        this.head2.getPath().clear();
        this.head2.getPath().addJMPointsFrom(copyPath);


        copyPath = ar.shapeToDraw.getPath().copy();
        this.shapeToDraw.getPath().clear();
        this.shapeToDraw.getPath().addJMPointsFrom(copyPath);

//            this.getMp().copyFrom(ar.getMp());
        this.shapeToDraw.getMp().copyFrom(ar.shapeToDraw.getMp());

        if (this.labelTip != null) {
            this.labelTip.copyStateFrom(ar.labelTip);
        }
    }

    @Override
    public void update(JMathAnimScene scene) {
        super.update(scene);
        if (labelTip != null) labelTip.update(scene);
        rebuildShape();

    }

    /**
     * Returns the thickness of the arrow line.
     *
     * @return The thickness
     */
    public double getArrowThickness() {
        return arrowThickness;
    }

    /**
     * Sets the thickness of the arrow line. Arrow head sizes are computed accordingly.
     *
     * @param arrowThickness The thickness
     * @return This object.
     */
    public Arrow setArrowThickness(double arrowThickness) {
        this.arrowThickness = arrowThickness;
        rebuildShape();
        return this;
    }

    /**
     * Returns the starting arrow type
     *
     * @return A value of enum ArrowType
     */
    public ArrowType getTypeA() {
        return typeA;
    }

    /**
     * Sets the stating arrow type
     *
     * @param typeA A value of enum ArrowType
     */
    public void setTypeA(ArrowType typeA) {
        if (typeA != this.typeA) {
            this.typeA = typeA;
            try {
                loadModels();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            rebuildShape();
        }
    }

    /**
     * Returns the ending arrow type
     *
     * @return typeB A value of enum ArrowType
     */
    public ArrowType getTypeB() {
        return typeB;
    }

    /**
     * Sets the ending arrow type
     *
     * @param typeB A value of enum ArrowType
     */
    public void setTypeB(ArrowType typeB) {
        if (typeB != this.typeB) {
            this.typeB = typeB;
            try {
                loadModels();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            rebuildShape();
        }
    }

    @Override
    public Arrow applyAffineTransform(AffineJTransform affineJTransform) {
        if (isFreeMathObject())
            super.applyAffineTransform(affineJTransform);
//        Acopy.applyAffineTransform(tr);
//        Bcopy.applyAffineTransform(tr);
        if (!isFreeMathObject()) {
            A.getVec().applyAffineTransform(affineJTransform);
            B.getVec().applyAffineTransform(affineJTransform);
        }
//        rebuildShape();
        return this;
    }

    /**
     * Sets the curvature of the arrow. A value of 0 gives a straight arrow. A value of PI/2 gives an arrow with a
     * semicircle shape
     *
     * @param angle Angle in radians of curvature
     * @return This object
     */
    public Arrow setCurvature(double angle) {
        this.angle = angle;
        rebuildShape();
        return this;
    }

    @Override
    protected Rect computeBoundingBox() {
        rebuildShape();
        Rect bb = shapeToDraw.getPath().getBoundingBox();
        if (labelTip != null) {
            return Rect.union(bb, labelTip.getBoundingBox());
        } else return bb;
    }

    /**
     * Returns the starting point
     *
     * @return A reference to the starting Point object
     */
    public Coordinates<?> getStart() {
        return A;
    }


    /**
     * Returns the ending point
     *
     * @return A reference to the ending Point object
     */
    public Coordinates<?> getEnd() {
        return B;
    }


    /**
     * Returns the head start scale. This value scales the start of the arrow.
     *
     * @return Scale head start. A value of 1 means no change.
     */
    public double getStartScale() {
        return headStartMultiplier;
    }

    /**
     * Sets the head start scale. This value scales the start of the arrow.
     *
     * @param startScale Scale head start. A value of 1 means no change.
     * @return This object
     */
    public Arrow setStartScale(double startScale) {
        this.headStartMultiplier = startScale;
        rebuildShape();
        return this;
    }

    /**
     * Returns the head end scale. This value scales the end of the arrow.
     *
     * @return Scale head end. A value of 1 means no change.
     */
    public double getEndScale() {
        return headEndMultiplier;
    }

    /**
     * Sets the head end scale. This value scales the start of the arrow.
     *
     * @param endScale Scale head end. A value of 1 means no change.
     * @return This object
     */
    public Arrow setEndScale(double endScale) {
        this.headEndMultiplier = endScale;
        rebuildShape();
        return this;
    }

    public LabelTip getLabelTip() {
        return labelTip;
    }




    private void registerLabel(LabelTip labelTip) {
        this.labelTip = labelTip;
        labelType = labelTypeEnum.NORMAL;
        mpArrow.add(this.labelTip);
        groupElementsToBeDrawn.clear();
        groupElementsToBeDrawn.add(shapeToDraw, this.labelTip);
    }


    public LabelTip addLabelTip(String text, boolean upperSide) {
        LabelTip label = LabelTip.makeLabelTip((upperSide ? labelArcUpside : labelArcDownside), .5, text);
        label.setProperty("upperSide", upperSide);//This will be useful when copying labels
        label.setDistanceToShape(.1);
        label.setDistanceToShapeRelative(true);
        label.setSlopeDirection((upperSide ? SlopeDirectionType.POSITIVE : SlopeDirectionType.NEGATIVE));
        label.setAnchor(AnchorType.LOWER);
        registerLabel(label);
        labelType = labelTypeEnum.NORMAL;
        this.stringFormat = "";
        return label;
    }

    /**
     * Adds a label with the length.The points mark the beginning and end of the delimiter.The delimiter lies at the
     * "left" of vector AB.
     *
     * @param format Format to print the length, for example "0.00"
     * @return The created LabelTip object
     */
    public LabelTip addLengthLabelTip(String format, boolean upperSide) {

        LabelTip label = LabelTip.makeLabelTip((upperSide ? labelArcUpside : labelArcDownside), .5, "${#0}$");
        label.setProperty("upperSide", upperSide);//This will be useful when copying labels
        label.setDistanceToShape(.1);
        label.setDistanceToShapeRelative(true);
        label.setSlopeDirection((upperSide ? SlopeDirectionType.POSITIVE : SlopeDirectionType.NEGATIVE));
        label.setAnchor(AnchorType.LOWER);
        registerLabel(label);


        labelType = labelTypeEnum.DISTANCE;
        this.stringFormat = format;

        AbstractLatexMathObject<?> t = labelTip.getLaTeXObject();
        t.setArgumentsFormat(format);


        t.registerUpdater(new Updater() {
//            @Override
//            public int computeUpdateLevel() {
//                return Math.max(A.getUpdateLevel(), B.getUpdateLevel()) + 1;
//            }

            @Override
            public void update(JMathAnimScene scene) {
                t.getArg(0).setValue(A.to(B).norm());

            }
        });
        return label;
    }

    /**
     * Adds a label with the vector coordinates.The points mark the beginning and end of the delimiter.The delimiter
     * lies at the "left" of vector AB.
     *
     * @param gap    Gap between control delimiter and label
     * @param format Format to print the numbers, for example "0.00"
     * @return The created LabelTip object
     */
    public LabelTip addVecLabelTip(double gap, String format, boolean upperSide) {
        LabelTip label = LabelTip.makeLabelTip((upperSide ? labelArcUpside : labelArcDownside), .5, "$({#0},{#1})$");
        label.setProperty("upperSide", upperSide);//This will be useful when copying labels
        label.setDistanceToShape(gap);
        label.setAnchor(AnchorType.LOWER);
        registerLabel(label);
        labelType = labelTypeEnum.COORDS;
        AbstractLatexMathObject<?> t = labelTip.getLaTeXObject();
        t.setArgumentsFormat(format);
        this.stringFormat = format;
        labelTip.registerUpdater(new Updater() {
//            @Override
//            public void computeUpdateLevel() {
//                this.updateLevel = Math.max(A.getUpdateLevel(), B.getUpdateLevel()) + 1;
//            }

            @Override
            public void update(JMathAnimScene scene) {
                Vec vAB = A.to(B);
                t.getArg(0).setValue(vAB.x);
                t.getArg(1).setValue(vAB.y);
            }
        });

        return label;
//        return (LaTeXMathObject) arrowLabel.getRefMathObject();
    }
//
//    @Override
//    public void draw(JMathAnimScene scene, Renderer r, Camera cam) {
//        super.draw(scene, r, cam);
//    }

    /**
     * Returns the scale of the amplitude of arrow. A value of 1 draws the arrow from one anchor point to another.
     * Smaller values scales the arrow in the same proportion. This value is used mainly for showCreation
     * animations-like.
     *
     * @return The amplitude scale. A value from 0 to 1
     */
    public double getAmplitudeScale() {
        return amplitudeScale;
    }

    /**
     * Sets the scale of the amplitude of arrow. A value of 1 draws the arrow from one anchor point to another. Smaller
     * values scales the arrow in the same proportion. This value is used mainly for showCreation animations-like.
     *
     * @param amplitudeScale The delimiter scale, from 0 to 1. Values are automatically cropped to this interval.
     */
    public <T extends Arrow> T setAmplitudeScale(double amplitudeScale) {
        this.amplitudeScale = Math.max(Math.min(amplitudeScale, 1), 0);
        return (T) this;
    }

    /**
     * Returns the shape of the arrow to draw
     *
     * @return A Shape object
     */
    public Shape getArrowShape() {
        return shapeToDraw;
    }

    @Override
    public Arrow setFreeMathObject(boolean isMathObjectFree) {
        super.setFreeMathObject(isMathObjectFree);
        if (getLabelTip() != null) {
            getLabelTip().setFreeMathObject(isMathObjectFree);
        }
        return this;
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        super.registerUpdateableHook(scene);
        dependsOn(scene, A, B);
    }

    private enum labelTypeEnum {NORMAL, DISTANCE, COORDS}
}
