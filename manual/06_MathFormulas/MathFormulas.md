# Mathematical formulas

Math formulas, as we have seen, are created with the `LaTexMathObject` class. The process to create the formula from the latex text is as follows:

1. A LaTeX document is generated from the passed String (a preamble is created and some latex packages included).
2. A hash of the document is computed, which will be the name of the tex file, to be saved in the project_root/text directory.
3. The library tries to invoke the `LaTeX` executable to compile the file into a dvi, and then the `dvi2svgm` utility to convert the dvi to svg format. If the tex file already exists, this step is omitted, as `JMathAnim` assumes the svg is already generated from previous runs.
4. The svg is imported using the `SVGMathObject` methods, from which `LaTexMathObject` inherits.

For example if we generate a math expression with the command

```java
LaTeXMathObject sum=LaTeXMathObject.make("$2+2=4$");
```

It will generate 5 shapes, that we can access via `get` method. Thus, the command `sum.get(0)` will return the first shape ( the "2" glyph), `sum.get(1)` will return the "+" glyph, etc. These indices will be important to specify exactly how we want the animation from one expression to another to be done.

The major drawback of this approach is that is not always clear which glyph of the formula corresponds to a given index.  For that, we have the method `formulaHelper` that takes a varargs of `LatexMathObject` objects, or `String` with the LaTeX code, overlays the shape number for each one, stacks the formulas vertically, zooms and adds them to the scene. For example:

```java
LaTeXMathObject t1 = LaTeXMathObject.make("$e^{i\\pi}+1=0$");
LaTeXMathObject t2 = LaTeXMathObject.make("$\\sqrt{a+b}\\leq\\sqrt{a}+\\sqrt{b}$");
formulaHelper(t1, t2);
waitSeconds(5);
```

We'll obtain the following image for 5 seconds. You can make an screenshot to examine it more deeply:

![image-20210101131234148](equationFormHelper.png)

It is recommended, before doing any animation that involves math formulas, to run this method to get useful references when defining animations.

## Setting color to specific parts of a math expression

If you want to apply colors to some parts of a math expression, you can use the `\color` commands of LaTeX or you can use the `setColor`method and specify the indexes to apply the color. For example:

```java
LaTeXMathObject formula=LaTeXMathObject.make("$(a+b)^2=a^2+b^2+2ab$").center();
camera.adjustToAllObjects();
formula.setColor(JMColor.parse("darkred"),1,7,14);//Color dark red to indexes 1, 7 and 14
formula.setColor(JMColor.parse("darkgreen"),3,10,15);//Color dark green to indexes 3,10 and 15
add(formula);
waitSeconds(5);
```



![image-20210101123616556](equationColor.png)

Note that the method changes both draw and fill colors.

## The slice method

Sometimes you may need to separate a formula in parts so you can animate them separately. The `slice`method extracts a subformula with the given indexes and removes the extracted shapes from the original formula. For example, suppose we have the formula `$A=b\cdot h$` and want to initially show the "b" and "h" glyphs in a rectangle, prior to put them in their original place:

```java
Shape rectangle = Shape.square().scale(2,1).center().fillColor("chocolate");
LaTeXMathObject formula = LaTeXMathObject.make("$A=b\\cdot h$")
    .stackToScreen(Anchor.Type.UPPER, .1, .1)
    .layer(1);
formula.setColor("steelblue",2);
formula.setColor("crimson",4);

LaTeXMathObject sliceBase=formula.slice(2);//Extracts the "b" from the formula
LaTeXMathObject sliceHeight=formula.slice(4);//Extracts the "h from the formula

Point sliceBaseCenter=sliceBase.getCenter();//Save the centers into these points
Point sliceHeightCenter=sliceHeight.getCenter();

//Position the sliced parts in the rectangle
sliceBase.stackTo(rectangle, Anchor.Type.LOWER,.1);
sliceHeight.stackTo(rectangle, Anchor.Type.RIGHT,.1);

play.showCreation(rectangle, sliceBase,sliceHeight);
play.showCreation(formula);//Draws the formula, except "b" and "h"
ShiftAnimation anim1 = Commands.stackTo(3, sliceBaseCenter, Anchor.Type.BY_CENTER, 0, sliceBase);
ShiftAnimation anim2 = Commands.stackTo(3, sliceHeightCenter, Anchor.Type.BY_CENTER, 0, sliceHeight);
playAnimation(anim1,anim2);//Restores the slices to their original positions
waitSeconds(2);
```

![equationSlice](equationSlice.gif)

Note that, although the sliced formula has only one element, the indexes remain the same. So, if the "b" glyph was in position 2 in the original formula, it is still in position 2 in the sliced formula, so that `sliceBase.get(2)` returns the "b" shape. The other indexes stores empty shapes.

There is another version of the method, with a boolean flag that controls if the sliced shapes are removed from the original math expression or not. For example, in the previous example `LaTeXMathObject sliceBase=formula.slice(false,2)` will create a slice of the "b" glyph without altering the original formula.

The `slice`method can be a powerful tool if you want to operate on part of complex math expressions. If you want to progressively show a complex formula, the easiest option is to slice it in the different parts you will be showing at  a concrete point of the animation.

## Animating transforms between equations

Suppose we are planning to perform an animation that solves a simple equation x+2=0. We define the two math expressions:

```java
LaTeXMathObject t1=LaTeXMathObject.make("$x+2=0$");
LaTeXMathObject t2=LaTeXMathObject.make("$x=-2$");
```

and we want to define a precise, self-explaining animation, that transforms the first equation into the second.

If we simply try with the command

```java
play.transform(4, t1, t2);
```

we obtain the following:

![equation01](equation01.gif)

It's nice, but not illustrative. It would be better to force the "+2" to convert into "-2", and the originals "x" and "=" glyphs to their counterparts "x" and "=" glyphs. For this we have the `TransformMathExpression` animation class. But first, we must be clear about the indexes of the different shapes that compose the latex objects. For that, we use the method `formulaHelper`:

```java
formulaHelper(t1,t2);
waitSeconds(5);
```

![equation02](equation02.png)

So, we need an animation that maps shape 0 onto shape 0, shape 3 onto shape 1, etc.

We create a new `TransformMathExpression` animation object, with the expected parameters:

```java
TransformMathExpression tr=new TransformMathExpression(5, t1, t2);
```

The `TransformMathExpression` objects admits several commands to define the precise transform we want to do. In this case, we want to transform the original shape 0 (x) to destiny shape 0 (x). This is stated with the command`map`

```java
tr.map(0,0);//Transforms orig-shape 0 to dst-shape 0
```

We also want to map the "+" sign (shape 1) into the "-" sign (shape 2), with the command

```java
tr.map(1,2);//Transforms orig-shape 1 to dst-shape 2
```

and the original "2" (shape 2) into the destiny "2" (shape 3):

```java
tr.map(2,3);
```

And finally, the "=" sign (shape 3) into the another "=" sign (shape 1):

```java
tr.map(3,1);//Transforms orig-shape 3 to dst-shape 1
```

What about shape 4 (the "0" sign)? If we don't specify a destination, this shape is marked for removal, with a `fadeOut` animation by default. If we play this animation with the `playAnimation` method we have:

![equation03](equation03.gif)

Ok, this is better, but there is still something that looks odd.  When manipulating equations, it's always desirable to mark the "=" sign a as pivotal point. We can achieve this by forcing that the origin and destiny formulas are aligned by the "=" sign. This sign is at position 3 in origin formula and in position 1 in destiny. With the command

```
t2.alignCenter(1, t1, 3);
```

we make that the center of glyph number 1 of `t2` (its "=" sign) matches the center of the glyph number 3 of `t1` (its "=" sign). If we execute this method prior to the animation now we have:

![equation04](equation04.gif)

## Range mapping

If we need to map a bunch of consecutive origin indexes into another bunch of consecutive destiny indexes , the method `mapRange(OrigA,OrigB,dst)` do exactly this. The command

```java
tr.mapRange(3,7,13);
```

is equivalent to

```java
tr.map(3,13);
tr.map(4,14);
tr.map(5,15);
tr.map(6,16);
tr.map(7,17);
```

## Grouping 

Suppose we have the following complex number expressions.  The second one is the first simplified. We want to animate a descriptive transition from `t1` to `t2`.

```java
LaTeXMathObject t1 = LaTeXMathObject.make("$2+3{\\color{blue}i}+5-{\\color{blue}i}$");
LaTeXMathObject t2 = LaTeXMathObject.make("$7+2{\\color{blue}i}$");
```

It is desirably that the "2" and "+5" shapes morph into single shape "7". For the complex part, the coefficients "+3" and "-" should morph into "+2", and the two "i" symbols from the origin should morph into the single "i" in the destination. This can be achieved defining groups, with the methods ` defineOrigGroup(name, i1,i2,...)` and `defineDstGroup(name, i1,i2,...)`. First of all, let's see a clear view of the indexes, with the `formulaHelper` method, as seen before:

![image-20201121172930831](equation05.png)

We need to map orig-shapes 0, 4 and 5 into destiny-shape 7. We define a group in the origin with these indexes:

```java
tr.defineOrigGroup("realPart", 0,4,5);
```

We can use any string to name that group, with the only restriction that can't begin with an underscore "_".

Now we can map this group into the "7" shape of the destiny, which has index 0:

```java
tr.map("realPart",0);
```

Now we have to map "+3" and "-" of the imaginary part into "+2" of the destiny expression. We define one group in the origin, as we have done before:

```java
tr.defineOrigGroup("imagCoef", 1,2,6);//Shapes 1,2 and 6 in the original formula
tr.defineDstGroup("imagCoefDst", 1,2);//Shapes 1 and 2 in the destiny formula
tr.map("imagCoef","imagCoefDst");
```

As you can see, the `map` method admits any pair of group names or indexes.

Finally, create a group with the "i" symbols in the origin (shapes 3 and 7) and map it into the "i" of the destiny (shape 3):

```java
tr.defineOrigGroup("i", 3,7);
tr.map("i",3);
```

Here is the complete source code, with a `stackTo`command to position the second formula under the first one:

```java
LaTeXMathObject t1 = LaTeXMathObject.make("$2+3{\\color{blue}i}+5-{\\color{blue}i}$");
LaTeXMathObject t2 = LaTeXMathObject.make("$7+2{\\color{blue}i}$");
t2.stackTo(t1, Anchor.Type.LOWER,.5);
camera.zoomToObjects(t1,t2);
TransformMathExpression tr = new TransformMathExpression(5, t1, t2);
tr.defineOrigGroup("realPart", 0,4,5);
tr.map("realPart",0);
tr.defineOrigGroup("imagCoef", 1,2,6);
tr.defineDstGroup("imagCoefDst", 1,2);
tr.map("imagCoef","imagCoefDst");
tr.defineOrigGroup("i", 3,7);
tr.map("i",3);
playAnimation(tr);
waitSeconds(5);
```

![equation05](equation05.gif)

## Effects

Each mapping from one shape (or group) to another can be decorated with some effects. These can be added right after the `map` command, with the following methods. Let's show them with an example. Suppose we want to animate the commutative property of the sum. Define the 2 `LaTexMathObject` objects and make an animation:

```java
LaTeXMathObject t1=LaTeXMathObject.make("$a+b$");
LaTeXMathObject t2=LaTeXMathObject.make("$b+a$");
t2.alignCenter(1, t1, 1);//Align both expressions in the "=" sign
camera.zoomToObjects(t1,t2);
TransformMathExpression tr=new TransformMathExpression(5, t1, t2);
tr.map(1,1);//= into =
tr.map(0,2);//a into b
tr.map(2,0);//b into a
playAnimation(tr);
waitSeconds(1);
```

We have the following animation:

![equation06](equation06.gif)

We will apply effects adding them to the `tr.map(0,2)` and `tr.map(2,0)` commands. First, the `.addScaleEffect(t)` applies a scaling factor back and forth.

```java
tr.map(0,2).addScaleEffect(.7);
tr.map(2,0).addScaleEffect(1./.7);
```

![equation07](equation07.gif)

The `.addAlphaEffect(t)`  changes the alpha (draw and fill) back and forth:

```java
tr.map(0,2).addAlphaEffect(.7);
tr.map(2,0).addAlphaEffect(.7);
```

![equation08](equation08.gif)

The `.addRotateEffect(n)`  rotates n times the shape.

```java
tr.map(0,2).addRotateEffect(1);
tr.map(2,0).addRotateEffect(-1);
```

![equation09](equation09.gif)

The `.addJumpEffect(t)`  applies a jump with a vector of 90º clockwise.

```java
tr.map(0,2).addJumpEffect(1);
tr.map(2,0).addJumpEffect(1);//Note that this "jump" is downward
```

![equation10](equation10.gif)

By default, the shape of the jump is a semicircle, so that only the sign of the parameter is relevant. Other jump types can be specified since version 0.9.0, defined in the enum `AnimationEffect.JumpType`: `SEMICIRCLE, PARABOLICAL, ELLIPTICAL, TRIANGULAR, SINUSOIDAL, SINUSOIDAL2, CRANE`. These are explained with detail in the animation effects chapter. For example, you can set that the "a" glyph moves like grabbed by a crane, and that the "b" glyph follows a path resembling a triangular roof. We use the 

```java
tr.map(0, 2).addJumpEffect(t1.getHeight(), AnimationEffect.JumpType.CRANE);
tr.map(2, 0).addJumpEffect(t1.getHeight(), AnimationEffect.JumpType.TRIANGULAR);//Note that this "jump" is downward
```

![equation11a](equation11a.gif)



We use the method `getHeight()` to get the actual height of the formula, which will be the height of the jumps.

And, finally, you can nest any of these effects:

```java
tr.map(0,2).addJumpEffect(.1).addRotateEffect(-1).addScaleEffect(.5);
tr.map(2,0).addJumpEffect(.1).addRotateEffect(1).addScaleEffect(.5);
```

![equation11](equation11.gif)

## Shapes marked for removal or adding

Any shape whose index is not mapped to a destiny index or group is marked for removal. Currently, there are 6  types, defined in the enum `TransformMathExpression.RemoveType`: `FADE_OUT, SHRINK_OUT, MOVE_OUT_UP, MOVE_OUT_LEFT, MOVE_OUT_RIGHT, MOVE_OUT_DOWN`.

In a similar way, any destiny shape not mapped by a origin index or group is marked for adding, with one of the following ways, defined in the enum `TransformMathExpression.AddType`: `FADE_IN, GROW_IN, MOVE_IN_UP, MOVE_IN_LEFT, MOVE_IN_RIGHT, MOVE_IN_DOWN`.

By default, `FADE_OUT` and `FADE_IN` are chosen for removing and adding. With the `setRemovingStyle` and `setAddingStyle` we can define individually the style for each shape.

We'll show all of this in one single, beautiful, self-explicative, dizzying animation:

```java
LaTeXMathObject t1 = LaTeXMathObject.make("ABCDEF").center();
LaTeXMathObject t2 = LaTeXMathObject.make("123456").center();
camera.zoomToObjects(t1, t2);
camera.scale(2);
TransformMathExpression tr = new TransformMathExpression(10, t1, t2);
//If we don't map anything, all origin shapes are marked for removal and 
//all destiny shapes are marked for adding.

tr.setRemovingStyle(TransformMathExpression.RemoveType.FADE_OUT, 0);
tr.setRemovingStyle(TransformMathExpression.RemoveType.SHRINK_OUT, 1);
tr.setRemovingStyle(TransformMathExpression.RemoveType.MOVE_OUT_DOWN, 2);
tr.setRemovingStyle(TransformMathExpression.RemoveType.MOVE_OUT_LEFT, 3);
tr.setRemovingStyle(TransformMathExpression.RemoveType.MOVE_OUT_RIGHT, 4);
tr.setRemovingStyle(TransformMathExpression.RemoveType.MOVE_OUT_UP, 5);
        
tr.setAddingStyle(TransformMathExpression.AddType.FADE_IN, 0);
tr.setAddingStyle(TransformMathExpression.AddType.GROW_IN, 1);
tr.setAddingStyle(TransformMathExpression.AddType.MOVE_IN_DOWN, 2);
tr.setAddingStyle(TransformMathExpression.AddType.MOVE_IN_LEFT, 3);
tr.setAddingStyle(TransformMathExpression.AddType.MOVE_IN_RIGHT, 4);
tr.setAddingStyle(TransformMathExpression.AddType.MOVE_IN_UP, 5);
        
playAnimation(tr);
waitSeconds(3);
```

![equation12](equation12.gif)

And finally, we show how the initial animation will look applying colors, mapping, and effects:

````java
LaTeXMathObject t1 = LaTeXMathObject.make("$x+2=0$");
LaTeXMathObject t2 = LaTeXMathObject.make("$x=-2$");
//Add some colors
t1.setColor(JMColor.parse("darkolivegreen"), 0);
t2.setColor(JMColor.parse("darkolivegreen"), 0);
t1.setColor(JMColor.parse("maroon"), 2);
t2.setColor(JMColor.parse("maroon"), 2, 3);

t2.alignCenter(1, t1, 3);//Align centers
camera.zoomToObjects(t1, t2);
TransformMathExpression tr = new TransformMathExpression(5, t1, t2);
tr.map(0, 0);//Transforms orig-shape 0 to dst-shape 0
tr.map(1, 2)
    .addJumpEffect(t1.getHeight())
    .setTransformStyle(TransformMathExpression.TransformType.FLIP_VERTICALLY);//Transforms using a vertical flip orig-shape 1 to dst-shape 2 and adds a jump effect with the height of t1
tr.map(2, 3)
    .addJumpEffect(t1.getHeight());//Transforms orig-shape 2 to dst-shape 3 and adds a jump effect with the height of t1
tr.map(3, 1);//Transforms orig-shape 3 to dst-shape 1
tr.setRemovingStyle(TransformMathExpression.RemoveType.SHRINK_OUT, 4);
playAnimation(tr);
waitSeconds(3);
````

![equation13](equation13.gif)