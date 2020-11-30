# Examples gallery

## A Pythagoras Theorem proof

This show an scene with a background image and the shadow effect

[Link to Youtube](https://www.youtube.com/watch?v=wogadxvkZi0)

## The Tusi couple

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
        mov.setLambda(t -> t);
        rot.setLambda(t -> t);
        playAnimation(mov, rot);
```

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



![Clock](Clock.gif)