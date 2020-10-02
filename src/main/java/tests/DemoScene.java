/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import com.jmathanim.Animations.ShowCreation;
import com.jmathanim.Animations.commands.Commands;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.ConfigLoader;
import com.jmathanim.Utils.JMColor;
import com.jmathanim.jmathanim.Scene2D;
import com.jmathanim.mathobjects.LaTeXMathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class DemoScene extends Scene2D {

    @Override
    public void setupSketch() {
        ConfigLoader.parseFile("production.xml");
//        ConfigLoader.parseFile("preview.xml");
        ConfigLoader.parseFile("dark.xml");
    }

    @Override
    public void runSketch() {
        ShowCreation sc1, sc2;
        LaTeXMathObject description, commandText;
        Shape reg1, reg2;
        //Title
        makeTitle();

        //Slide 1
        System.out.println("Slide 1");
        Shape circle = Shape.circle().scale(.5).shift(-1, 0);
        Shape square = Shape.square().scale(1).shift(1, -1).drawColor(JMColor.BLUE);
        sc1 = new ShowCreation(circle, 2);
        sc2 = new ShowCreation(square, 2);
        playAnimation(sc1, sc2);
        description = createDescription("Basic shapes");
        commandText = createCommandText("{\\tt Shape.circle()} or {\\tt Shape.square()}");
        waitSeconds(5);
        play.fadeOut(description);
        play.fadeOut(commandText);//TODO: Add fadeOutAll animation

        //Slide 2
        System.out.println("Slide 2");
        description = createDescription("Supports named styles and animated changes");
        commandText = createCommandText("{\\tt play.setStyle(circle,'solidblue',3)}");
        playAnimation(Commands.setStyle(circle, "solidblue", 3));
        waitSeconds(5);
        play.fadeOut(description);
        play.fadeOut(commandText);

        //Slide 3
        System.out.println("Slide 3");
        description = createDescription("Animated transformation point-to-point");
        commandText = createCommandText("{\\tt play.transform(circle,square,3)}");
        play.transform(circle, square, 3);
        waitSeconds(5);
        play.fadeOut(description);
        play.fadeOut(commandText);

        play.fadeOut(circle);
        play.fadeOut(square);
        
        //Slide 4
        System.out.println("Slide 4");
        waitSeconds(2);
         reg1 = Shape.regularPolygon(5).style("solidred").scale(.7).shift(-1, -.5).rotate(PI / 5);
        reg2 = Shape.regularPolygon(5).style("solidblue").scale(.4).shift(1, 0);
        sc1 = new ShowCreation(reg1, 2);
        sc2 = new ShowCreation(reg2, 2);
        playAnimation(sc1, sc2);

        description = createDescription("Type of transform is automatically selected, using homotopys if applicable");
        commandText = createCommandText("{\\tt play.transform(reg1,reg2,3)}");
        play.transform(reg2, reg1, 3);
        waitSeconds(5);
        play.fadeOut(description);
        play.fadeOut(commandText);
        remove(reg2);
        play.fadeOut(reg1);

        //Slide 5
        System.out.println("Slide 5");
        waitSeconds(2);
        reg1 = Shape.regularPolygon(6).style("solidred").scale(.7).shift(-1, -1.1).rotate(PI / 5);
        reg2 = Shape.regularPolygon(5).style("solidblue").scale(.4).shift(1, 0);
        sc1 = new ShowCreation(reg1, 2);
        sc2 = new ShowCreation(reg2, 2);
        playAnimation(sc1, sc2);
        

        description = createDescription("In this case, a point-to-point transform is chosen");
        commandText = createCommandText("{\\tt play.transform(reg1,reg2,5)}");
        play.transform(reg2, reg1, 5);
        waitSeconds(5);
        play.fadeOut(description);
        play.fadeOut(commandText);
    }

    private void makeTitle() {
        LaTeXMathObject title = new LaTeXMathObject("JMathAnim demo");
        title.stackTo(Anchor.BY_CENTER);
        play.growIn(title, PI / 10, 2);
        waitSeconds(3);
        play.shrinkOut(title, -PI / 10, 2);
        waitSeconds(1);
    }

    private LaTeXMathObject createDescription(String text) {
        LaTeXMathObject latex = new LaTeXMathObject(text);
        play.fadeIn(latex.scale(.3).stackTo(Anchor.UPPER, .1, .1));
        return latex;
    }

    private LaTeXMathObject createCommandText(String text) {
        LaTeXMathObject latex = new LaTeXMathObject(text);
        play.fadeIn(latex.scale(.3).stackTo(Anchor.LOWER, .1, .1));
        return latex;
    }
}
