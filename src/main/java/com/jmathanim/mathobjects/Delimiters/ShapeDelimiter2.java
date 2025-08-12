package com.jmathanim.mathobjects.Delimiters;

import com.jmathanim.Utils.ResourceLoader;
import com.jmathanim.mathobjects.*;

public class ShapeDelimiter2 extends Delimiter2 {

    private final Shape delimiterShape;
    private final MathObjectGroup delimiterShapeGroup;
    private SVGMathObject body;


    protected ShapeDelimiter2(Point A, Point B, Type type, double gap) {
        super(A, B, type, gap);
        minimumWidthToShrink = .5;
        delimiterShape = new Shape();
        mpDelimiter.add(delimiterShape.getMp());
        delimiterShapeGroup = MathObjectGroup.make(delimiterShape);
    }

    public static ShapeDelimiter2 make(Point A, Point B, Delimiter2.Type type, double gap) {
        ShapeDelimiter2 resul = new ShapeDelimiter2(A, B, type, gap);
        ResourceLoader rl = new ResourceLoader();
        String name;
        switch (type) {
            case PARENTHESIS:
                name = "#parenthesis.svg";
                break;
            case BRACKET:
                name = "#bracket.svg";
                break;
            default:
                name = "#braces.svg";
                break;
        }
        resul.body = new SVGMathObject(rl.getResource(name, "shapeResources/delimiters"));
//        resul.mpDelimiter.add(resul.body);
        resul.style("latexdefault");
        return resul;
    }

    @Override
    public void copyStateFrom(MathObject obj) {
        super.copyStateFrom(obj);
        if (!(obj instanceof ShapeDelimiter2)) {
            return;
        }
        ShapeDelimiter2 del = (ShapeDelimiter2) obj;
        delimiterShape.copyStateFrom(del.delimiterShape);

    }


    @Override
    protected void buildDelimiterShape() {
//
//        if (amplitudeScale == 0) {
//            return;//Nothing
//        }
//
//        double width = A.to(B).norm() * amplitudeScale;//The final width of the delimiter
//        double angle = A.to(B).getAngle();
//        MultiShapeObject bodyCopy = body.copy();
//        delimiterShape.getPath().clear();
//
//        if (type == Delimiter2.Type.BRACE) {
//            minimumWidthToShrink = .5;
//            double wr = .25 * delimiterScale * UsefulLambdas.allocateTo(0, minimumWidthToShrink).applyAsDouble(width);
//            bodyCopy.setWidth(wr);
//            double hasToGrow = Math.max(0, width - wr);
//            // 0,1,2,3,4,5 shapes Shapes 1 and 4 are extensible
//            double wSpace = .5 * (hasToGrow);
//            delimiterShape.getPath().jmPathPoints.addAll(bodyCopy.get(0).getPath().jmPathPoints);
//            delimiterShape.merge(bodyCopy.get(2).shift(wSpace, 0), true, false)
//                    .merge(bodyCopy.get(1).shift(2 * wSpace, 0), true, false)
//                    .merge(bodyCopy.get(3).shift(wSpace, 0), true, true);
//        }
//        if (type == Delimiter2.Type.BRACKET) {
//            minimumWidthToShrink = .5;
//            double wr = .8 * delimiterScale * UsefulLambdas.allocateTo(0, minimumWidthToShrink).applyAsDouble(width);
//            bodyCopy.setWidth(wr);
//            double hasToGrow = Math.max(0, width - wr);
//            double wSpace = hasToGrow;
//            delimiterShape.merge(bodyCopy.get(0), false, false);
//            delimiterShape.merge(bodyCopy.get(1).shift(wSpace, 0), true, true);
//        }
//        if (type == Delimiter2.Type.PARENTHESIS) {
//            minimumWidthToShrink = .5;
//            double wr = 0.48 * delimiterScale * UsefulLambdas.allocateTo(0, minimumWidthToShrink).applyAsDouble(width);
//            bodyCopy.setWidth(wr);
//            double hasToGrow = Math.max(0, width - wr);
//            double wSpace = hasToGrow;
//            delimiterShape.merge(bodyCopy.get(0), false, false);
//            delimiterShape.merge(bodyCopy.get(1).shift(wSpace, 0), true, true);
//        }
//        delimiterToDraw.clear();
//        delimiterToDraw.add(delimiterShape);
//
//        Rect bb = delimiterShape.getBoundingBox();
//        delimiterShape.shift(0, gap * amplitudeScale);
//
//        labelMarkPoint.stackTo(delimiterShape, Anchor.Type.UPPER, labelMarkGap * amplitudeScale);
//
//        if (delimiterLabel != null) {
//            delimiterLabelToDraw = delimiterLabel.copy();
//            delimiterToDraw.add(delimiterLabelToDraw);
//
//            delimiterLabelToDraw.scale(amplitudeScale);
//
//            //Manages rotation of label
//            switch (rotateLabel) {
//                case FIXED:
//                    delimiterLabelToDraw.rotate(-angle);
//                    break;
//                case ROTATE:
//                    break;
//                case SMART:
////                delimiterLabelToDraw.rotate(-angle);
//                    if ((angle > .5 * PI) && (angle < 1.5 * PI)) {
//                        delimiterLabelToDraw.rotate(PI);
//                    }
//            }
//            delimiterLabelToDraw.stackTo(Anchor.Type.LOWER, labelMarkPoint, Anchor.Type.UPPER, 0);
//        }
//        AffineJTransform tr = AffineJTransform.createDirect2DIsomorphic(bb.getDL(), bb.getDR(), A, B, 1);
//
//        tr.applyTransform(delimiterToDraw);
////        tr.applyTransform(labelMarkPoint);
//
//        return delimiterToDraw;
    }

}
