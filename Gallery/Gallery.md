[home](https://davidgutierrezrubio.github.io/jmathanim/) [back](../index.html)

# Examples gallery

## Taylor expansion of sin(x)

```java
public void runSketch() throws Exception {
    int orderTaylor = 8;
    Axes axes = new Axes();
    axes.addXTicksLegend("$\\pi$", PI);
    axes.addXTicksLegend("$-\\pi$", -PI);
    axes.addXTicksLegend("$2\\pi$", 2 * PI);
    axes.addXTicksLegend("$-2\\pi$", -2 * PI);
    axes.generateYTicks(-4, 4, 1);
    add(axes);
    final double xmin = -2 * PI - .2;
    final double xmax = 2 * PI + .2;
    FunctionGraph sinFunction = FunctionGraph.make(t -> Math.sin(t), xmin, xmax);
    sinFunction.thickness(3).drawColor("#682c0e");
    camera.adjustToObjects(sinFunction);

    play.showCreation(sinFunction);
    waitSeconds(1);

    FunctionGraph taylor[] = new FunctionGraph[orderTaylor];
    LaTeXMathObject texts[] = new LaTeXMathObject[orderTaylor];
    for (int n = 1; n < orderTaylor; n++) {
        taylor[n] = FunctionGraph.make(TaylorExpansionSin(n),xmin,xmax).drawColor("#153e90").thickness(4);
        texts[n]=LaTeXMathObject.make("Taylor order "+n).scale(3).stackToScreen(Anchor.Type.UL,.2,.2);
        texts[n].setColor("#153e90").layer(2);
    }
    final Rect r = texts[1].getBoundingBox().addGap(.1, .1);
    Shape box=Shape.rectangle(r).fillColor(JMColor.WHITE).thickness(3).layer(1);
    play.showCreation(taylor[1],texts[1],box);
    for (int n = 2; n < orderTaylor; n++) {
        add(taylor[n-1].copy().thickness(1).drawColor(JMColor.GRAY));
        Transform transformFunction = new Transform(2, taylor[n-1], taylor[n]);
        Transform transformText=new Transform(2, texts[n-1], texts[n]);
        playAnimation(transformFunction,transformText);
    }
    waitSeconds(5);
}

public final DoubleUnaryOperator TaylorExpansionSin(int order) {
    return x->{
        double resul=0;
        double potX=x;
        int sign=1;
        for (int n = 0; n < order; n++) {
            int k=2*n+1;
            resul+=potX/factorial(k)*(sign);
            sign=-sign;
            potX*=x*x;
        }
        return resul;
    };
}
```

You can [see the video here](https://imgur.com/gallery/PjlVtXw).



## The Koch curve

```java
@Override
public void runSketch() throws Exception {
    int numIters = 6;
    Shape[] koch = new Shape[numIters];
    koch[0] = Shape.segment(Point.origin(), Point.unitX());
    for (int n = 1; n < numIters; n++) {
        koch[n] = getNextKochIteration(koch[n - 1]);
    }
    camera.adjustToObjects(koch[numIters - 1]);
    for (int n = 1; n < numIters; n++) {
        play.transform(3, koch[n - 1], koch[n]);
    }
    waitSeconds(5);
}

public Shape getNextKochIteration(Shape previousShape) {
    //A new iteration of the Koch curve is composed of 4 copies of the previous iteration
    //scaled 1/3.
    Shape s1 = previousShape.copy().scale(previousShape.getPoint(0), 1d / 3, 1d / 3);

    Shape s2 = s1.copy().rotate(s1.getPoint(0), PI / 3).shift(s1.getPoint(0).to(s1.getPoint(-1)));
    s2.getJMPoint(0).isThisSegmentVisible = true;//Mark the first point of s2 visible in order to connect it to s1 later

    Shape s3 = s1.copy().rotate(s1.getPoint(0), -PI / 3).shift(s1.getPoint(0).to(s2.getPoint(-1)));
    s3.getJMPoint(0).isThisSegmentVisible = true;//Mark the first point of s3 visible in order to connect it to s2 later

    Shape s4 = s1.copy().shift(s1.getPoint(0).to(s3.getPoint(-1)));
    s4.getJMPoint(0).isThisSegmentVisible = true;//Mark the first point of s4 visible in order to connect it to s3 later

    s1.getPath().addJMPointsFrom(s2.getPath());//Add all points of s2
    s1.getPath().addJMPointsFrom(s3.getPath());//Add all points of s3
    s1.getPath().addJMPointsFrom(s4.getPath());//Add all points of s4

    //This command cleans up the path, removin redundant points, like consecutive equal ones
    s1.getPath().distille();
    return s1;
}
```

You can [see the video here](https://imgur.com/gallery/8jCXGWf).



## The Tusi couple

This example shows the use of anonymous updaters to perform commands in the update cycle of the animation.

```java
  Shape circleBig = Shape.circle().rotate(90 * DEGREES);
		//Circle that moves
        Shape circleSmall = circleBig.copy().scale(.5).shift(0, .5);

		//This shape is the path that will walk circleSmall
        Shape path = circleBig.copy().scale(.5);

		//Outer circle
        circleBig.thickness(2).drawColor(JMColor.BLUE);

        int numPoints = 10;
        Point[] points = new Point[numPoints];
        for (int n = 0; n < numPoints; n++) {
            points[n] = Point.at(0, 0).style("redCircle");//Doesn't matter the initial coordinates
            add(points[n]);
        }
		//Add the diameters
        for (int n = 0; n < numPoints * 2; n++) {
            Point p1 = circleBig.getPath().getPointAt(.5d * n / numPoints);
            Point p2 = circleBig.getPath().getPointAt(.5d * n / numPoints + .5);
            add(Shape.segment(p1, p2).drawColor(JMColor.GRAY).thickness(.5));
        }
		//Register a updateable that puts all points of the circle in the correct position
        registerUpdateable(new Updateable() {
            @Override
            public int getUpdateLevel() {
                return circleSmall.getUpdateLevel() + 1;
            }

            @Override
            public void update(JMathAnimScene scene) {
                for (int n = 0; n < numPoints; n++) {
                    Point p = circleSmall.getPath().getPointAt(1d * n / numPoints);
                    points[n].v.copyFrom(p.v);
                }
            }
        });

        add(circleBig, circleSmall);
        MoveAlongPath mov = new MoveAlongPath(10, path, circleSmall, Anchor.Type.BY_CENTER);
        Animation rot = Commands.rotate(10, -2 * PI, circleSmall).setUseObjectState(false);
        mov.setLambda(t -> t);//Uniform movement
        rot.setLambda(t -> t);//Uniform rotation
        playAnimation(mov, rot);
```

Here you have a GIF from the movie generated:

![Tusi](Tusi.gif)



## The Clock

This example shows how combining simple animations we can have complex ones. The total animation is done with a single `playAnim` call.

```java
        double runtime = 5;

		//This shape hold the points where the numbers will lie
        Shape destiny = Shape.circle().scale(.75).rotate(90 * DEGREES);
        AnimationGroup gr = new AnimationGroup();
        for (int n = 1; n <= 12; n++) {
            //Take 12 points equally spaced inside the path 0, 1/12, 2/12,...11/12
            //We take them in reverse as the circle is built counterclockwise
            Vec sv = destiny.getPath().getPointAt((12 - n) / 12.d).v;
            double waitTime = runtime * .5 * (n - 1) / 11;
			
            //Each number begins centered at (0,0) and moves to a point of shape destiny, with a combined animation
            //of growing, shifting and rotating...
            LaTeXMathObject sq = LaTeXMathObject.make("" + n).center();
            AnimationGroup ag = new AnimationGroup();
            ag.add(Commands.growIn(runtime - waitTime, sq));
            ag.add(Commands.shift(runtime - waitTime, sv, sq).setUseObjectState(false));
            ag.add(Commands.rotate(runtime - waitTime, sv.getAngle() - PI / 2, sq).setUseObjectState(false));
			//...but first let make them wait a variable time, so they don't start at the same time
            Concatenate con = new Concatenate(new WaitAnimation(waitTime), ag);
            gr.add(con);
        }
        playAnimation(gr);
        waitSeconds(3);
```

Here you have a GIF from the movie generated:

![Clock](Clock.gif)

## A Pythagoras Theorem proof

An animation with a background image and the shadow effect

[Link to Youtube](https://www.youtube.com/watch?v=wogadxvkZi0)

## Sum of the first odd numbers

Visual proof of the sum of the 9 first odd numbers
[Link to Youtube](https://www.youtube.com/watch?v=uFhtdXuPPLM)

And here with the 40 first odd numbers...
[Link to Youtube](https://www.youtube.com/watch?v=emEJ-EooNBc)



[home](https://davidgutierrezrubio.github.io/jmathanim/) [back](../index.html)