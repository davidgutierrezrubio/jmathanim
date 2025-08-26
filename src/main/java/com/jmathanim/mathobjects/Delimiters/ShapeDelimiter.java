package com.jmathanim.mathobjects.Delimiters;

import com.jmathanim.Enum.AnchorType;
import com.jmathanim.Enum.DelimiterType;
import com.jmathanim.Utils.*;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Coordinates;
import com.jmathanim.mathobjects.NullMathObject;
import com.jmathanim.mathobjects.Shapes.MultiShapeObject;

import static com.jmathanim.jmathanim.JMathAnimScene.PI;

public class ShapeDelimiter extends Delimiter {

    private MultiShapeObject body;


    protected ShapeDelimiter(Coordinates A, Coordinates B, DelimiterType type, double gap) {
        super(A, B, type, gap);
        minimumWidthToShrink = .5;
    }

    public static ShapeDelimiter make(Coordinates A, Coordinates B, DelimiterType type, double gap) {
        ShapeDelimiter resul = new ShapeDelimiter(A, B, type, gap);
        ResourceLoader rl = new ResourceLoader();
        String name;
        switch (resul.type) {
            case PARENTHESIS:
                name = "#parenthesis.svg";
                break;
            case BRACKET:
                name = "#bracket.svg";
                break;
            case INVISIBLE:
                name = "#braces.svg";//Use this, make it invisible
            default:
                name = "#braces.svg";
                break;
        }
        try {
        if (name != null) {
            resul.body = SVGUtils.importSVG(rl.getResource(name, "shapeResources/delimiters"));
        } else resul.body = null;
        } catch (Exception ex) {
            JMathAnimScene.logger.error("An exception occurred creating shape delimiter. A null delimiter will be created");
            JMathAnimScene.logger.error(ex.getMessage());
            resul.body = null;
        }
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
        Vec AA = A.interpolate(B, .5 * (1 - amplitudeScale));
        Vec BB = A.interpolate(B, .5 * (1 + amplitudeScale));
        MultiShapeObject bodyCopy = body.copy();
            delimiterShapeToDraw.getPath().clear();

        if (type == DelimiterType.BRACE) {
            minimumWidthToShrink = .5;
            double wr = .25 * delimiterScale * UsefulLambdas.allocateTo(0, minimumWidthToShrink).applyAsDouble(width);
            bodyCopy.setWidth(wr);
            double hasToGrow = Math.max(0, width - wr);
            // 0,1,2,3,4,5 shapes Shapes 1 and 4 are extensible
            double wSpace = .5 * (hasToGrow);
            delimiterShapeToDraw.getPath().getJmPathPoints().addAll(bodyCopy.get(0).getPath().getJmPathPoints());
            delimiterShapeToDraw.merge(bodyCopy.get(2).shift(wSpace, 0), true, false)
                    .merge(bodyCopy.get(1).shift(2 * wSpace, 0), true, false)
                    .merge(bodyCopy.get(3).shift(wSpace, 0), true, true);
        }
        if (type == DelimiterType.BRACKET) {
            minimumWidthToShrink = .5;
            double wr = .8 * delimiterScale * UsefulLambdas.allocateTo(0, minimumWidthToShrink).applyAsDouble(width);
            bodyCopy.setWidth(wr);
            double hasToGrow = Math.max(0, width - wr);
            double wSpace = hasToGrow;
            delimiterShapeToDraw.merge(bodyCopy.get(0), false, false);
            delimiterShapeToDraw.merge(bodyCopy.get(1).shift(wSpace, 0), true, true);
        }
        if (type == DelimiterType.PARENTHESIS) {
            minimumWidthToShrink = .5;
            double wr = 0.48 * delimiterScale * UsefulLambdas.allocateTo(0, minimumWidthToShrink).applyAsDouble(width);
            bodyCopy.setWidth(wr);
            double hasToGrow = Math.max(0, width - wr);
            double wSpace = hasToGrow;
            delimiterShapeToDraw.merge(bodyCopy.get(0), false, false);
            delimiterShapeToDraw.merge(bodyCopy.get(1).shift(wSpace, 0), true, true);
        }


        Rect bb = delimiterShapeToDraw.getBoundingBox();
        delimiterShapeToDraw.shift(0, gap * amplitudeScale);

//        labelMarkPoint.stackTo(delimiterShapeToDraw, AnchorType.UPPER, labelMarkGap * amplitudeScale);
        labelMarkPoint.copyCoordinatesFrom(Anchor.getAnchorPoint(delimiterShapeToDraw, AnchorType.UPPER, labelMarkGap * amplitudeScale));

        if (!(getLabel() instanceof NullMathObject)) {

            delimiterLabelRigidBox.resetMatrix();
            getLabel().update(scene);
            double labelAmplitudeScale = UsefulLambdas.allocateTo(0, getLabel().getWidth() * 1.5).applyAsDouble(width);

//            double gapToUse = hgap * realAmplitudeScale;
            delimiterLabelRigidBox.scale(labelAmplitudeScale);

            //Manages rotation of label
            switch (rotationType) {
                case FIXED:
                    delimiterLabelRigidBox.rotate(-angle);
                    break;
                case ROTATE:
                    break;
                case SMART:
//                delimiterLabelToDraw.rotate(-angle);
                    if ((angle > .5 * PI) && (angle < 1.5 * PI)) {
                        delimiterLabelRigidBox.rotate(PI);
                    }
            }
            delimiterLabelRigidBox.stackTo(AnchorType.LOWER, labelMarkPoint, AnchorType.UPPER, 0);
//            groupElementsToBeDrawn.add(getLabel());
//            if (amplitudeScale != 1)
//                delimiterLabelToDraw.scale(this.amplitudeScale);
        }
        AffineJTransform tr = AffineJTransform.createDirect2DIsomorphic(bb.getDL(), bb.getDR(), AA, BB, 1);

        if (this.type== DelimiterType.INVISIBLE) {
            delimiterShapeToDraw.visible(false);
        }


//        if (amplitudeScale != 1)
//            groupElementsToBeDrawn.scale(this.amplitudeScale);
        tr.applyTransform(groupElementsToBeDrawn);
    }

}
