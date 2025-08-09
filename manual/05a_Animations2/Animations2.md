[home](https://davidgutierrezrubio.github.io/jmathanim/) [back](../index.html)


# Combining animations
Suppose you want a square to perform a shift and rotation at the same time. The first approach may be to play both animations at the same time. However, if you try with a code like this:

```java
Shape sq = Shape.square().fillColor("seagreen").thickness(6).center();
Animation shift = Commands.shift(5, 1, 0, sq);
Animation rotate = Commands.rotate(5, PI/2, sq);
playAnimation(shift, rotate);
waitSeconds(3);
```

You will obtain a square rotating, but not shifting at all. The reason is that each animation saves the state of the object in the `initialize` method and restores it at each call of `doAnim` where the changes are made. So, the restore state call of the `rotate` animation erases the changes made by the `shift` animation. The solution is quite simple, as every animation has the method `.setUseObjectState that activates or deactivates the saving and restoring of states. `In this case, as the `rotate` animation is executed each frame after the `shift`, we let this one manage states and deactivate it for the `rotate` animation.

```java
Shape sq = Shape.square().fillColor("seagreen").thickness(6).center();
Animation shift = Commands.shift(5, 1, 0, sq);
Animation rotate = Commands.rotate(5, -PI/2, sq).setUseObjectState(false);
playAnimation(shift, rotate);
waitSeconds(3);
```

Now the square properly shifts and rotates:

![StateFlagAnimation01](StateFlagAnimation01.gif)

# Adding effects to animations

Several animations inherit from a subclass called `AnimationWithEffects` that allows you to apply certain effects. Currently, those animations are `Transform`, `FlipTransform`, `TransformMathExpression`, `shift`, `stackTo`, `align`, `moveIn`, `moveOut` and `setLayout`. We saw how to apply these effects in the `TransformMathExpression` animations in the math formulas chapter.

## The jump effect

The `.setJumpHeight(double height)` adds a jump effect to the object(s) being animated. The direction of the jump is the shift vector (the center of the object at its initial state to the center at its ending state) rotated 90 degrees clockwise. A negative height can be specified. We will show an example of adding a jump effect to a `FlipTransform` animation:

```java
Shape hexagon = Shape.regularPolygon(6)
    .scale(.25)
    .moveTo(Point.relAt(.25, .5))
    .fillColor("steelblue");
Shape triangle = Shape.regularPolygon(3)
    .scale(.5)
    .moveTo(Point.relAt(.75, .5))
    .fillColor("orange");
FlipTransform anim = new FlipTransform(5, FlipTransform.FlipType.HORIZONTAL, hexagon, triangle);
anim.addJumpEffect(.5); //adds a jump effect
playAnimation(anim);
```





![jumpEffect](jumpEffect.gif)

By default, the trajectory is a parabola (except in the `TransformMathExpression` which is semicircular). You can specify other jump types by adding a second parameter to the `addJumpEffect` method, defined in the `AnimationEffect.JumpType` enum.

```java
anim.addJumpEffect(.5,AnimationEffect.JumpType.CRANE);//A crane effect, with height .5
anim.addJumpEffect(.5,AnimationEffect.JumpType.ELLIPTICAL); //Elliptical path, with height .5
anim.addJumpEffect(.5,AnimationEffect.JumpType.PARABOLICAL); //Parabolical path, with height .5
anim.addJumpEffect(.5,AnimationEffect.JumpType.SEMICIRCLE); //A semicircular path (the height is ignored, except for the sign)
anim.addJumpEffect(.5,AnimationEffect.JumpType.SINUSOIDAL); //A path with a sinusoidal shape, from 0 to PI, and height .5
anim.addJumpEffect(.5,AnimationEffect.JumpType.SINUSOIDAL2); //A path with a sinusoidal shape, from 0 to 2*PI, and height .5
anim.addJumpEffect(.5,AnimationEffect.JumpType.TRIANGULAR); //A path resembling a triangular roof, with height .5
```

Here you can see the different paths:

![jumpPaths](jumpPaths.gif)

## The scale effect

The `.setScaleEffect(double scale)` adds a back and forth scale effect:

```java
Shape pol = Shape.regularPolygon(6).scale(.25).center().fillColor("steelblue");
ShiftAnimation anim = Commands.shift(3, 1,0, pol);//shifts pol with vector (1,0)
anim.addScaleEffect(2); //adds a scale effect
playAnimation(anim);
```



![scaleEffect](scaleEffect.gif)

## The alpha scale effect

The `.setAlphaScaleEffect(double alphaScale)` adds a back and forth alpha effect:

```java
Shape pol = Shape.regularPolygon(6).scale(.25).center().fillColor("steelblue");
ShiftAnimation anim = Commands.shift(5, 1, 0, pol);//shifts pol with vector (1,0)
anim.addAlphaScaleEffect(.2);
playAnimation(anim);
```

![alphaEffect](alphaEffect.gif)

## The rotation effect

The `.setRotateEffect(int numTurns)` adds a rotation, making the specified number of turns.

```java
Shape pol = Shape.regularPolygon(6).scale(.25).center().fillColor("steelblue");
ShiftAnimation anim = Commands.shift(3, 1,0, pol);//shifts pol with vector (1,0)
anim.addRotationEffect(-1); //Make a complete turn clockwise
playAnimation(anim);
```

![rotateEffect](rotateEffect.gif)

And, in case you are wondering, yes, these effects can be nested:

```java
Shape square = Shape.square()
    .scale(.25)
    .moveTo(Point.relAt(.25, .5))
    .fillColor("steelblue");
Shape circle = Shape.circle()
    .scale(.25)
    .moveTo(Point.relAt(.75, .5))
    .fillColor("firebrick");
Transform anim = new Transform(5, square, circle);
anim.addRotationEffect(1)
    .addScaleEffect(.5)
    .addJumpEffect(.5, AnimationEffect.JumpType.FOLIUM);
playAnimation(anim);
waitSeconds(3);
```

![nestedShiftEffects](nestedShiftEffects.gif)

# Effects in shift animations

The shifting-type animations (`shift`,  `stackTo`, `align`, `moveIn`, `moveOut` and `setLayout`)  all inherit from the `ShiftAnimation`class. These methods allow additional effects:

## Rotation by any angle

Apart from the `.addRotationEffect` you can also use the method `.addRotationEffectByAngle` to specify an arbitrary rotation angle. However, keep in mind that animations like `setLayout` or `stackTo` compute the shifting vectors without taking this into account.

## Setting animations for individual objects

All methods to add effects have overloaded methods in the  `ShiftAnimation` class where you can set an effect for a particular object added to the animation. For example, let's suppose we have a MathObjectGroup with 10 squares and want to shift them, each one with a different rotation angle. We can accomplish this by creating 10 individual shift animations and setting the rotation angle effect for each one, but we can achieve the same effect with a single animation:

```java
MathObjectGroup squares = MathObjectGroup.make();
for (int n = 0; n < 10; n++) {
    squares.add(Shape.square().scale(.1).fillColor(JMColor.random()));
}
squares.setLayout(MathObjectGroup.Layout.RIGHT,.1).center();

//Note that in the animation we pass squares.toArray() instead of squares. This way we are passing the
//10 squares instead of a single object
ShiftAnimation anim=Commands.shift(5, 0,-1, squares.toArray());
for (int n = 0; n < 10; n++) {
    anim.addRotationEffectByAngle(squares.get(n),PI*n/9);//Sets different rotation angles for each object
}
playAnimation(anim);
waitSeconds(2);
```

![shiftAnimEffect1](shiftAnimEffect1.gif)



## The delay effect

This effect can be applied to shifting-type animations when several objects are animated. Instead of moving all objects at the same time, a delay is applied, creating the effect of a queue of moving objects. For example, let's create a simple animation that changes the layout of a group of squares, leaving commented the line that adds the delay effect:

```java
MathObjectGroup smallSquaresGroup = MathObjectGroup.make();
for (int n = 0; n < 10; n++) {
    smallSquaresGroup.add(Shape.square().scale(.1).fillColor(JMColor.random()));
}
Shape centralSquare = Shape.square().scale(.25).stackToScreen(Anchor.Type.LOWER, .1, .1);
smallSquaresGroup.setLayout(centralSquare, MathObjectGroup.Layout.LEFT, 0);
add(smallSquaresGroup,centralSquare);
waitSeconds(1);
ShiftAnimation anim = Commands.setLayout(5, centralSquare, MathObjectGroup.Layout.UPPER, 0, smallSquaresGroup);
//anim.addDelayEffect(.5);
playAnimation(anim);
```

Generates the following animation:

![delayEffect1](delayEffect1.gif)

Note that all squares begin and end their paths at the same time.

Now if we uncomment the method `anim.addDelayEffect(.5)` we have this:

![delayEffect2](delayEffect2.gif)

When applying a delay effect with a parameter 0<t<1< span="">, each individual animation runtime is reduced by the factor 1-t and distributed evenly over the total runtime of the animation. Thus, for example an </t<1<>`addDelayEffect(.3)` will reduce all single animations to 70% of the total runtime.

If you change the parameter .5 to .75, with `anim.addDelayEffect(.75)` the animation produced looks like this:

![delayEffect3](delayEffect3.gif)


# Controlling the animations with lambda functions

Each animation has a lambda object that represents a function from [0,1] to [0,1]. This function takes the parameter t in the `doAnim(double t)` method and computes a new time value to apply the animation. The primary reason for this is to prevent the animations from performing in a linear way without a smooth beginning or ending, but they can be used for many more effects.

The class `UsefulLambdas`, as its name suggests, holds several static methods that return different lambdas to obtain different effects to your animations. For example, the following code will draw the graphs (with time in the x-axis) of the different lambdas defined:

```java
@Override
public void setupSketch() {
	config.parseFile("#preview.xml");
	config.parseFile("#light.xml");
}

@Override
public void runSketch() {
    MathObjectGroup functions = MathObjectGroup.make(
        drawGraphFor(UsefulLambdas.smooth(), "{\\tt smooth()}"),
        drawGraphFor(UsefulLambdas.smooth(.25d), "{\\tt smooth(.25d)}"),
        drawGraphFor(UsefulLambdas.allocateTo(.25, .75), "{\\tt allocate(.25,.75)}"),
        drawGraphFor(UsefulLambdas.reverse(), "{\\tt reverse()}"),
        drawGraphFor(UsefulLambdas.bounce1(), "{\\tt bounce1()}"),
        drawGraphFor(UsefulLambdas.bounce2(), "{\\tt bounce2()}"),
        drawGraphFor(UsefulLambdas.backAndForthBounce1(), "{\\tt backAndForthBounce1()}"),
        drawGraphFor(UsefulLambdas.backAndForthBounce2(), "{\\tt backAndForthBounce2()}")
    );
    functions.setLayout(new BoxLayout(Point.origin(), 4, AbstractBoxLayout.Direction.RIGHT_DOWN, .25, .25));
    add(functions);
    camera.zoomToAllObjects();
    waitSeconds(3);
}

private MathObjectGroup drawGraphFor(DoubleUnaryOperator lambda, String name) {
    MathObjectGroup resul = MathObjectGroup.make();
    FunctionGraph fg = FunctionGraph.make(lambda, 0, 1).thickness(8).drawColor("darkblue");
    LaTeXMathObject text = LaTeXMathObject.make(name).scale(.5).stackTo(fg, Anchor.Type.LOWER, .2);
    Shape segX = Shape.segment(Point.at(-.1, 0), Point.at(1.1, 0));
    Shape segY = segX.copy().rotate(Point.origin(), .5 * PI);
    resul.add(fg, text, segX, segY);
    return resul;
}
```



![image-20210430141530681](lambdas01.png)

These graphs should be interpreted considering that the x-axis is the time from 0 to 1, and the y-axis is the amount of animation done (from 0 to 1 too). Thus, a lambda function *g* that starts the animation and ends it properly should satisfy the conditions *g(0)=*0 and *g(1)=*1. Note that the `reverse()` method returns a lambda function which does the opposite. It is used to play animations backwards in time.

All animations use the `smooth()`method by default. There is a version with a parameter from 0 to 1 that controls the smoothness (0=straight line, 1=fully smoothed). The default value is 0.9.

If you want an animation that plays in a linear way, you can use the method `.setLambda(t->t)` or `.setLamba(UsefulLambdas.smooth(0))`.

The bounce methods simulate a single or double bounce. Note that the `backAndForthBounce` methods take the value 0 when t=1, which means the animation is not complete at the end of the cycle, but the object is restored a their initial state.

The lambda functions are `DoubleUnaryOperator` Java objects, which support compositions via the `.compose` method. With the lambdas .allocate and reverse we can build useful variations of the base lambdas.

The `allocate(a,b)` lambda performs a time scaling from a to b where 0<a<b<1< span="">. For example, an animation with a lambda function `allocate(.25,.75)`will start at 25% of the runtime and finish at 75%, which can be handy when playing with other animations simultaneously. For example, consider the previous code that shifts and rotates a square at the same time:

```java
Shape sq = Shape.square().scale(.5).style("solidblue").moveTo(Point.at(-1, 0));
AnimationGroup ag = AnimationGroup.make(
    Commands.shift(6, 2, 0, sq),
    Commands.rotate(6, PI * .5, sq)
    		.setUseObjectState(false)
);
playAnimation(ag);
```

The shifting and rotation both begin and end at the same time. Suppose we want the square to perform the rotation at some intermediate point. We may achieve this effect easily by using lambdas. Add the following code to the definition of the rotate animation:

```java
 Commands.rotate(6, PI * .5, sq)
    		.setUseObjectState(false)
     		.setLambda(UsefulLambdas.smooth().compose(UsefulLambdas.allocateTo(.4, .6)))
```

You will get the following animation:

![lambdas02](lambdas02.gif)

The allocate lambda rescales the time so that the rotation starts at 40% of runtime and ends at 60%. The allocate lambda itself is linear, so we compose it with the smooth lambda to get a smooth rotation.

Another example if we change the lambda of the rotate animation with the line:

```java
 .setLambda(UsefulLambdas.bounce2().compose(UsefulLambdas.allocateTo(.2, .75)))
```

we have a bounce effect between the 20% and 75% of the animation runtime:

![lambdas03](lambdas03.gif)



With the following code, you can see the graphs of the lambda parameters and how they affect the animation. You can experiment changing the definitions of `rotateLambda` and `shiftLambda`, and create your own lambda functions with the syntax `t->f(t)`.

```java
@Override
public void setupSketch() {
	config.parseFile("#preview.xml");
	config.parseFile("#light.xml");
}

@Override
public void runSketch() {
    Axes axes = new Axes();
    axes.generatePrimaryXTicks(0, 1, .25);
    axes.generatePrimaryYTicks(0, 1, .25);
    add(axes);
    //Lambdas for rotate and shift animation
    DoubleUnaryOperator rotateLambda = UsefulLambdas.smooth().compose(UsefulLambdas.allocateTo(.3, .6));
    DoubleUnaryOperator shiftLambda = UsefulLambdas.bounce1();

    //Graph of the shift lambda
    FunctionGraph fgShift = FunctionGraph.make(shiftLambda, 0, 1).drawColor("brown").thickness(6);

    //This is an updateable point permanently in the graph of the function
    PointOnFunctionGraph pointFgShift = new PointOnFunctionGraph(0, fgShift)
            .drawColor("darkblue").thickness(40);
    MathObject legendShift = LaTeXMathObject.make("shift")
            .setColor("brown").scale(.5);

    //Ensures the text legendShift is always located above the point pointFgShift
    registerUpdateable(
            new AnchoredMathObject(legendShift, Anchor.Type.LEFT, pointFgShift, Anchor.Type.RIGHT, .05)
    );

    //add the function graph, the point and the legend to the scene
    add(legendShift, fgShift, pointFgShift);

    //We do the same for the graph of the rotate lambda
    FunctionGraph fgRotate = FunctionGraph.make(rotateLambda, 0, 1)
            .drawColor("orange").thickness(6);
    PointOnFunctionGraph pointFgRotate = new PointOnFunctionGraph(0, fgRotate)
            .drawColor("darkred").thickness(40);
    MathObject legendRotate = LaTeXMathObject.make("rotate")
            .setColor("orange").scale(.5);

    //Ensures the text legendRotate is always located above the point pointFgRotate
    registerUpdateable(
            new AnchoredMathObject(legendRotate, Anchor.Type.LOWER, pointFgRotate, Anchor.Type.UPPER, .05)
    );

    add(legendRotate, fgRotate, pointFgRotate);

    camera.setMathXY(-1, 2, .25);
    //The square that we will animate
    Shape sq = Shape.square()
            .scale(.25)
            .style("solidblue")
            .moveTo(Point.at(0, -.25));
    add(sq);
    AnimationGroup ag = AnimationGroup.make(
            Commands.shift(6, 1, 0, pointFgShift)
                    .setLambda(t -> t),//Move point in the graph of lambda shift
            Commands.shift(6, 1, 0, pointFgRotate)
                    .setLambda(t -> t),//Move point in the graph of lambda rotate
            Commands.shift(6, 1, 0, sq)//Shift the square...
                    .setLambda(shiftLambda),
            Commands.rotate(6, PI * .5, sq)//...and rotate it
                    .setUseObjectState(false)
                    .setLambda(rotateLambda)
    );
    playAnimation(ag);
    waitSeconds(1);
}
```

![lambdas04](lambdas04.gif)



# Making procedural animations

For procedural animations, we mean animations made "manually" by performing the modifications to the objects and advancing a frame, much like a stop motion artist would do. This method is needed for complex movements that cannot be done with the predefined animations. For this, the `JMathAnimScene`class has a protected variable, `dt`, that holds the time step for each frame. The `advanceFrame()`method does all the necessary procedures to create the frame and save it. For example, let's make a program that moves a point with uniformly random steps:

```java
A=Point.origin();
add(A);
double numberOfSeconds=10;
for (double t = 0;  t< numberOfSeconds; t+=dt) {
	A.shift((1-2*Math.random())*dt,(1-2*Math.random())  *dt);
	advanceFrame();
}
```

If you execute it, you'll obtain a rather nervous point:

![procedural01](procedural01.gif)

## Combining predefined procedural and animations

Suppose you want to show the nervous point, but at the same time you want to execute a rotation on a square, for example. Of course, you could do this in a purely procedural way, but you can also use the `rotate` animation. After defining it, you must initialize it and, prior to each call of the `advanceFrame`, invoke the `processAnimation`method of the animation. 

```java
Point A=Point.origin();
Shape square=Shape.square().center();
add(A,square);
Animation rotation=Commands.rotate(5,90*DEGREES,square);//Define the animation
rotation.initialize(this);//Initialize the animation
double numberOfSeconds=10;
for (double t = 0;  t< numberOfSeconds; t+=dt) {
	A.shift((1-2*Math.random())*dt,(1-2*Math.random())*dt);
	rotation.processAnimation();//Do whatever the animation needs for every frame here
	advanceFrame();
}
```

Note that when the rotation is finished subsequent calls to `processAnimation` have no effect:

![procedural02](procedural02.gif)




# Reusing animations

The exact flow of any animation is as follows:

1) Creation of the animation object, where most of the necessary auxiliary objects are created.

2) Initialization. In this method the states of all objects involved in the animation are stored (state at t=0)

3) For each value of t, namely t', the `doAnim(t)` computes the actual frame of the animation. To do this:

   1) Restore all objects to its initial state t=0.
   2) Apply the required transformations to recreate the animation at time t=t'.

4) At the exit of the animation, the method `cleanAnimationAt(t)` performs the necessary cleaning operations, depending on the moment of the animation that we want to exit. For example, most transformation or creation animations use intermediate, auxiliary objects that should be deleted when exiting at the beginning or end.

We recall specially the point 3). If we want to reuse the created animation in other context, the animation should be reinitializated, otherwise it will use the old object states from previous run. For example, suppose we have a rectangle we want to rotate 45 degrees and then rotate it back to its initial position. Suppose we want to simply use the same animation but using the lambda `reverse()` which plays backwards in time.
```java
Shape sq=Shape.square().scale(2,1).center().style("solidgreen");
Animation rotate=Commands.rotate(2, 45*DEGREES, sq).setLambda(t->t);
playAnimation(rotate);
waitSeconds(1);
playAnimation(rotate.setLambda(UsefulLambdas.reverse()));
waitSeconds(1);
````
The animation we got looks like this, which is not that we expected:

![resettingAnimations1](resettingAnimations1.gif)

The problem lies in the second call to the `rotate` animation. After being called again, the animation reinitialises and saves the states of the animated objects (in this case the rectangle) according to its current state.

We can avoid the automatic reinitialisation of animations by simply setting the flag `setShouldResetAtFinish` to `false`
```java
Shape sq=Shape.square().scale(2,1).center().style("solidgreen");
Animation rotate=Commands.rotate(2, 45*DEGREES, sq).setLambda(t->t);
rotate.setShouldResetAtFinish(false);
playAnimation(rotate);
waitSeconds(1);
playAnimation(rotate.setLambda(UsefulLambdas.reverse()));
waitSeconds(1);
````
The result is much better now, except for one small detail at the end...

![resettingAnimations1](resettingAnimations2.gif)

The rectangle disappears at the end! Why does this happen? Well, sometimes JMathAnim is just too "smart". Remember that for some animations, like move or rotate, if the object is not in the scene, it is automatically added? Well, if you play the animation in reverse and exit at t=0, JMathAnim will try to leave everything as it found it, i.e. with the rectangle outside the scene. If you don't want this to happen, just make sure that the rectangle is added to the scene before it is animated:

```java
Shape sq=Shape.square().scale(2,1).center().style("solidgreen");
add(sq);
Animation rotate=Commands.rotate(2, 45*DEGREES, sq).setLambda(t->t);
rotate.setShouldResetAtFinish(false);
playAnimation(rotate);
waitSeconds(1);
playAnimation(rotate.setLambda(UsefulLambdas.reverse()));
waitSeconds(1);
````
![resettingAnimations1](resettingAnimations3.gif)





# Creating complex animations

There are special subclasses of `Animation`that allows to build more complex animations using previously defined ones.

## The wait animation

This `WaitAnimation` does what it says. It simply waits for a specified amount of time. Sounds exciting, right? This is used mostly when you want to combine simple animations into complex ones and need to add some waiting time between them.

## The AnimationGroup animation

The `AnimationGroup` plays all the animations at the same time. It finishes when the last one has ended. The example of the combined shift and rotate can be written as

```java
Shape sq = Shape.square().fillColor("seagreen").thickness(7).center();
Animation shift = Commands.shift(5, 1, 0, sq);
Animation rotate = Commands.rotate(5, -PI/2, sq).setUseObjectState(false);
AnimationGroup ag=new AnimationGroup(shift,rotate);
playAnimation(ag);
waitSeconds(3);
```

The `AnimationGroup` class also admits the `addDelayEffect`method. You can try different parameters for the delay effect:

```java
Shape[] rects=new Shape[10];
AnimationGroup ag=AnimationGroup.make();
for (int i = 0; i < 10; i++) {
    //Create 10 rectangles
    rects[i]=Shape.square().center().fillColor("orange").fillAlpha(.2);
    //Create an animation for each square scaling it with parameters (.2,.7,1)
    ag.add(Commands.scale(5, Point.origin(),2, .7, 1,rects[i]));
}
add(rects);
ag.addDelayEffect(.5);//A delay effect of 50%
playAnimation(ag);
waitSeconds(1);
```

![delayEffect4](delayEffect4.gif)



## The concatenate animation

The `Concatenate`class allows you to play animations in sequence.

```java
Shape sq = Shape.square().fillColor("seagreen").thickness(7).center();
Animation shift = Commands.shift(2, 1, 0, sq);
Animation rotate = Commands.rotate(2, -PI/2, sq);
Concatenate c=new Concatenate(shift,rotate);
playAnimation(c);
waitSeconds(1);
```

![concatenate01](concatenate01.gif)

## The JoinAnimation

The `JoinAnimation` class is similar to the previous `Concatenate` animation, but treats all contained animations as one. For example, the code:

```java
Shape sq = Shape.regularPolygon(5).center().style("solidred");
JoinAnimation anim = JoinAnimation
        .make(6,
        ShowCreation.make(2, sq),
        Commands.shift(1, 1, 0, sq),
        Commands.rotate(1, PI / 4, sq));
playAnimation(anim);
waitSeconds(3);
```

This code will create a single animation with a total duration of 6 seconds, that creates the pentagon, shifts it and finally performs a rotation. The duration of each subanimation is proportional to its runtime. So, the `Showcreation` will take twice the time of the `shift`  and `rotate `animations:

![joinAnimation1](joinAnimation1.gif)

The great advantage over `Concatenate` is that you can apply lambdas to the whole animation as one: If you add the following line right before the `playAnim` method.

```java
anim.setLambda(UsefulLambdas.backAndForth());
```

You will get the animation played back and forth:

![joinAnimation2](joinAnimation2.gif)



The default lambda in the `JoinAnimation` class is linear `t->t`.


In the next example, we use an animation to "unwrap" the hexagon, and later reuse it with a different lambda to wrap it again. Note the use of the method `.setShouldResetAtFinish(false)`, which deactivates the reset of the animation to be able to use it again without overwriting the initial state of the objects.

```java
Shape polygon = Shape.regularPolygon(6)
    .scale(.5)
    .stackToScreen(Anchor.Type.LEFT,.1,.1)
    .drawColor("steelblue")
    .thickness(20);
polygon.getPath().openPath();
polygon.rotate(-60 * DEGREES);
add(polygon);
JoinAnimation unwrap = JoinAnimation.make(5);
for (int i = 1; i < polygon.size(); i++) {
    //MathObjectGroup with vertices i...6
    MathObjectGroup vertices = MathObjectGroup.make();
    vertices.getObjects().addAll(polygon.getPath().jmPathPoints.subList(i, polygon.size()));
    unwrap.add(Commands.rotate(1, polygon.get(i).p, -60 * DEGREES, vertices).setLambda(t->t));
}
unwrap.setShouldResetAtFinish(false);
playAnimation(unwrap);

Point A=polygon.getBoundingBox().getLeft();
Point B=polygon.getBoundingBox().getRight();
Delimiter del=Delimiter.make(B, A, Delimiter.Type.LENGTH_ARROW, .1);
del.setLabel("Perimeter", .1);
del.getLabel().scale(.5);
play.showCreation(del);
waitSeconds(3);
play.fadeOut(del);
//Set lambda of unwrap to play reverse
unwrap.setLambda(UsefulLambdas.reverse());
playAnimation(unwrap);//play it again
waitSeconds(3);
```
Gives the following animation:

![joinAnimation3](joinAnimation3.gif)
[home](https://davidgutierrezrubio.github.io/jmathanim/) [back](../index.html)

