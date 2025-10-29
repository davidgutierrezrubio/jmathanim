module jmathanim {
    requires commons.math3;
    requires io.github.humbleui.skija.shared;
    requires io.github.humbleui.skija.windows.x64;
    requires java.datatransfer;
    requires java.desktop;
    requires java.logging;
    requires java.xml;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.swing;
    requires jlatexmath;
    requires jlatexmath.font.greek;
    requires org.apache.commons.io;
    requires org.apache.groovy;
    requires org.apache.xmlgraphics.batik.dom;
    requires org.apache.xmlgraphics.batik.svggen;
    requires xuggle.xuggler;




    exports com.jmathanim.jmathanim;
    exports com.jmathanim.Animations;
//    exports com.jmathanim.Animations.Strategies;
    exports com.jmathanim.Animations.Strategies.ShowCreation;
    exports com.jmathanim.Animations.Strategies.Transform;
    exports com.jmathanim.Animations.Strategies.Transform.Optimizers;
    exports com.jmathanim.Renderers;
    exports com.jmathanim.Renderers.FXRenderer;
    exports com.jmathanim.Renderers.MovieEncoders;
    exports com.jmathanim.Cameras;
    exports com.jmathanim.Constructible;
    exports com.jmathanim.Constructible.Conics;
    exports com.jmathanim.Constructible.Lines;
    exports com.jmathanim.Constructible.Points;
    exports com.jmathanim.Constructible.Transforms;

    exports com.jmathanim.Enum;
    exports com.jmathanim.MathObjects;
    exports com.jmathanim.MathObjects.Text;
    exports com.jmathanim.MathObjects.Shapes;
    exports com.jmathanim.MathObjects.Delimiters;
    exports com.jmathanim.MathObjects.Axes;
    exports com.jmathanim.MathObjects.UpdateableObjects;
    exports com.jmathanim.MathObjects.Polyhedrons;
    exports com.jmathanim.MathObjects.Tippable;
    exports com.jmathanim.MathObjects.Updaters;
    exports com.jmathanim.Styling;
    exports com.jmathanim.Utils;
    exports com.jmathanim.Utils.Layouts;
    exports com.jmathanim.Animations.MathTransform;

    opens com.jmathanim.jmathanim.Groovy to javafx.graphics;
}