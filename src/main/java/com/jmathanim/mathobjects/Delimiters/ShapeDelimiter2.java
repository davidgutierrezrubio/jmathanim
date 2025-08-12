package com.jmathanim.mathobjects.Delimiters;

import com.jmathanim.Utils.*;
import com.jmathanim.mathobjects.*;

import static com.jmathanim.jmathanim.JMathAnimScene.PI;

public class ShapeDelimiter2 extends Delimiter2 {

    private SVGMathObject body;


    protected ShapeDelimiter2(Point A, Point B, Type type, double gap) {
        super(A, B, type, gap);
        minimumWidthToShrink = .5;
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
    protected void buildDelimiterShape() {

        if (amplitudeScale == 0) {
            return;//Nothing
        }

        double width = A.to(B).norm() * amplitudeScale;//The final width of the delimiter
        double angle = A.to(B).getAngle();
        Point AA=A.interpolate(B,.5*(1-amplitudeScale));
        Point BB=A.interpolate(B,.5*(1+amplitudeScale));
        MultiShapeObject bodyCopy = body.copy();
        delimiterShapeToDraw.getPath().clear();

        if (type == Delimiter2.Type.BRACE) {
            minimumWidthToShrink = .5;
            double wr = .25 * delimiterScale * UsefulLambdas.allocateTo(0, minimumWidthToShrink).applyAsDouble(width);
            bodyCopy.setWidth(wr);
            double hasToGrow = Math.max(0, width - wr);
            // 0,1,2,3,4,5 shapes Shapes 1 and 4 are extensible
            double wSpace = .5 * (hasToGrow);
            delimiterShapeToDraw.getPath().jmPathPoints.addAll(bodyCopy.get(0).getPath().jmPathPoints);
            delimiterShapeToDraw.merge(bodyCopy.get(2).shift(wSpace, 0), true, false)
                    .merge(bodyCopy.get(1).shift(2 * wSpace, 0), true, false)
                    .merge(bodyCopy.get(3).shift(wSpace, 0), true, true);
        }
        if (type == Delimiter2.Type.BRACKET) {
            minimumWidthToShrink = .5;
            double wr = .8 * delimiterScale * UsefulLambdas.allocateTo(0, minimumWidthToShrink).applyAsDouble(width);
            bodyCopy.setWidth(wr);
            double hasToGrow = Math.max(0, width - wr);
            double wSpace = hasToGrow;
            delimiterShapeToDraw.merge(bodyCopy.get(0), false, false);
            delimiterShapeToDraw.merge(bodyCopy.get(1).shift(wSpace, 0), true, true);
        }
        if (type == Delimiter2.Type.PARENTHESIS) {
            minimumWidthToShrink = .5;
            double wr = 0.48 * delimiterScale * UsefulLambdas.allocateTo(0, minimumWidthToShrink).applyAsDouble(width);
            bodyCopy.setWidth(wr);
            double hasToGrow = Math.max(0, width - wr);
            double wSpace = hasToGrow;
            delimiterShapeToDraw.merge(bodyCopy.get(0), false, false);
            delimiterShapeToDraw.merge(bodyCopy.get(1).shift(wSpace, 0), true, true);
        }
        groupElementsToBeDrawn.clear();
        groupElementsToBeDrawn.add(delimiterShapeToDraw);

        Rect bb = delimiterShapeToDraw.getBoundingBox();
        delimiterShapeToDraw.shift(0, gap * amplitudeScale);

        labelMarkPoint.stackTo(delimiterShapeToDraw, Anchor.Type.UPPER, labelMarkGap * amplitudeScale);

        delimiterLabel.update(scene);
        delimiterLabelToDraw = MathObjectUtils.getSafeCopyOf(delimiterLabel);
        if (!(delimiterLabelToDraw instanceof NullMathObject)) {

            delimiterLabelToDraw = delimiterLabel.copy();

            double labelAmplitudeScale = UsefulLambdas.allocateTo(0, delimiterLabelToDraw.getWidth() * 1.5).applyAsDouble(width);

//            double gapToUse = hgap * realAmplitudeScale;
            delimiterLabelToDraw.scale(labelAmplitudeScale);

            //Manages rotation of label
            switch (rotateLabel) {
                case FIXED:
                    delimiterLabelToDraw.rotate(-angle);
                    break;
                case ROTATE:
                    break;
                case SMART:
//                delimiterLabelToDraw.rotate(-angle);
                    if ((angle > .5 * PI) && (angle < 1.5 * PI)) {
                        delimiterLabelToDraw.rotate(PI);
                    }
            }
            delimiterLabelToDraw.stackTo(Anchor.Type.LOWER, labelMarkPoint, Anchor.Type.UPPER, 0);
            groupElementsToBeDrawn.add(delimiterLabelToDraw);
//            if (amplitudeScale != 1)
//                delimiterLabelToDraw.scale(this.amplitudeScale);
        }

        AffineJTransform tr = AffineJTransform.createDirect2DIsomorphic(bb.getDL(), bb.getDR(), AA, BB, 1);


//        if (amplitudeScale != 1)
//            groupElementsToBeDrawn.scale(this.amplitudeScale);
        tr.applyTransform(groupElementsToBeDrawn);
    }

}
