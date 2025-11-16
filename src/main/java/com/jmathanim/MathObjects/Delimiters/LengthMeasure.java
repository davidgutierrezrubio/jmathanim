package com.jmathanim.MathObjects.Delimiters;

import com.jmathanim.Constructible.NullMathObject;
import com.jmathanim.Enum.AnchorType;
import com.jmathanim.Enum.DelimiterType;
import com.jmathanim.MathObjects.Coordinates;
import com.jmathanim.MathObjects.Shape;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.UsefulLambdas;
import com.jmathanim.Utils.Vec;

import static com.jmathanim.jmathanim.JMathAnimScene.PI;

public class LengthMeasure extends Delimiter {

    private final double hgap;
    private Double thicknessShape;

    protected LengthMeasure(Coordinates<?> A, Coordinates<?> B, DelimiterType type, double gap) {
        super(A, B, type, gap);
        this.gap = gap;
        hgap = .05;
        minimumWidthToShrink = .75;
    }

    protected static LengthMeasure makeLengthMeasure(Coordinates<?> A, Coordinates<?> B, DelimiterType type, double gap) {
        LengthMeasure resul = new LengthMeasure(A, B, type, gap);
        resul.buildDelimiterShape();
        return resul;
    }

    @Override
    protected void buildDelimiterShape() {


        double width = A.to(B).norm();
        double angle = A.to(B).getAngle();
        Vec AA = Vec.to(0, 0);
        Vec BB = Vec.to(width, 0);

        delimiterShapeToDraw.getPath().clear();
//        groupElementsToBeDrawn.clear();
//        groupElementsToBeDrawn.add(delimiterLabelBackup, delimiterShapeToDraw);

        double vCenter = .025 * delimiterScale;
//        Shape verticalBar = Shape.segment(Point.at(0, 0), Point.at(0, 2 * vCenter));
        double xOffset = 0;
        switch (type) {
            case LENGTH_ARROW:
                xOffset = vCenter;
                break;
            case LENGTH_BRACKET:
                xOffset = 0;
                break;
        }
        Shape verticalBar = Shape.polyLine(Vec.to(xOffset, -vCenter), Vec.to(0, 0), Vec.to(xOffset, vCenter));
        delimiterShapeToDraw.getPath().addJMPointsFrom(verticalBar.getPath());

        if (getLabel() instanceof NullMathObject) {

            final Shape segment = Shape.segment(Vec.to(0, 0), Vec.to(width, 0));
            delimiterShapeToDraw.getPath().addJMPointsFrom(segment.getPath());
        } else {
            delimiterLabelRigidBox.resetMatrix();


//            double realAmplitudeScale = UsefulLambdas.allocateTo(0, minimumWidthToShrink).applyAsDouble(width);
            double realAmplitudeScale = UsefulLambdas.allocateTo(0, getLabel().getWidth() * 2.5).applyAsDouble(width);
            double gapToUse = hgap * realAmplitudeScale;
            double segmentLength = .5 * (width - getLabel().getWidth()) - gapToUse;
            delimiterLabelRigidBox.scale(realAmplitudeScale);
//            delimiterLabelRigidBox.performMathObjectUpdateActions(scene);



//        segmentLength*=amplitudeScale;
            final Shape segment = Shape.segment(Vec.to(0, 0), Vec.to(segmentLength, 0));
            delimiterShapeToDraw.getPath().addJMPointsFrom(segment.getPath());

            //Manages rotation of label
            switch (rotationType) {
                case FIXED:
                    delimiterLabelRigidBox.rotate(-angle);
                    break;
                case ROTATE:
                    break;
                case SMART:
                    if ((angle > .5 * PI) && (angle < 1.5 * PI)) {
                        delimiterLabelRigidBox.rotate(PI);
                    }
            }

//            delimiterLabelRigidBox.stackTo(segment, AnchorType.RIGHT, gapToUse);
            delimiterLabelRigidBox.stack()
                    .withDestinyAnchor(AnchorType.RIGHT)
                    .withGaps(gapToUse)
                    .toObject(segment);


            labelMarkPoint.copyCoordinatesFrom(delimiterLabelRigidBox.getCenter());
//            Shape segCopy = segment.copy().stackTo(BB, AnchorType.LEFT);
            Shape segCopy = segment.copy().stack()
                    .withDestinyAnchor(AnchorType.LEFT)
                    .toObject(BB);

            delimiterShapeToDraw.getPath().addJMPointsFrom(segCopy.getPath());
            delimiterLabelRigidBox.shift(0, +gap * amplitudeScale);

//            delimiterLabelRigidBox.getMp().copyFrom(mpDelimiter.get(1));
        }
        Shape vertCopy = verticalBar.copy().scale(Vec.to(0, 0), -1, 1).shift(width, 0);
        delimiterShapeToDraw.getPath().addJMPointsFrom(vertCopy.getPath());
        delimiterShapeToDraw.shift(0, +gap * amplitudeScale);


//        delimiterShapeToDraw.getMp().copyFrom(mpDelimiterShape);

        if (amplitudeScale != 1) {
            delimiterShapeToDraw.scale(amplitudeScale);
            delimiterLabelRigidBox.scale(amplitudeScale);
            delimiterShapeToDraw.thickness(thicknessShape * amplitudeScale);
        } else {
            thicknessShape = delimiterShapeToDraw.getMp().getThickness();
        }
        delimiterShapeToDraw.fillAlpha(0);
        AffineJTransform tr = AffineJTransform.createDirect2DIsomorphic(AA, BB, A, B, 1);
        tr.applyTransform(groupElementsToBeDrawn);
    }

    public enum TYPE {
        ARROW, SIMPLE
    }


}
