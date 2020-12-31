[home](https://davidgutierrezrubio.github.io/jmathanim/) [back](../index.html)

# Transforming Objects
All classes that inherit from `MathObject` can be transformed. Several methods for shifting, rotating, scaling or aligning are defined, and most of them have their animated version. Note that also most of these methods return the object self, so that they can be applied consecutively like this  `object.method1().method2()…​`.

# Positioning objects

## Shift

The `shift` command shifts the object by the specified vector

``` java
Shape c=Shape.circle().shift(Vec.to(1,1));//An unit circle, centered at (1,1)
```

A simpler 2D-version is also provided:

``` java
Shape sq=Shape.square().shift(-3,0);//An unit square, lower left vertex at (-3,0)
```

## MoveTo
The `moveTo` command shifts the object so that its center is positioned at the given coordinates. Note that the center is the center of the bounding box of the object, not the geometrical center. For regular polygons, for example, they don’t necessarily match.

``` java
Shape r=Shape.regularPolygon(5).moveTo(3,3);//A pentagon, with its bounding box centered at (3,3)
```

## StackTo
The `StackTo` command works in a similar way that `putAt`, but allows to position an object relative to another one. For example, the following code creates 4 circles, and stacks them into a square in different ways:

``` java
Shape c1 = Shape.circle().fillColor("orange").fillAlpha(.3);
Shape c2 = c1.copy().fillColor("violet").fillAlpha(.6);
Shape c3 = c1.copy().fillColor("darkgreen").fillAlpha(.5);
Shape c4 = c1.copy().fillColor("darkblue").fillAlpha(.5);
Shape sq = Shape.square().fillColor("darkred").fillAlpha(.3);
c1.stackTo(sq, Anchor.Type.LEFT, .1);//Stacks circle to the left of the square, with a gap of .1 units
c2.stackTo(sq, Anchor.Type.RIGHT, .1);//Stacks circle to the right of the square, with a gap of .1 units
c3.stackTo(sq, Anchor.Type.UPPER);//Stacks circle to the upper side of the square, with no gap
c4.stackTo(sq, Anchor.Type.BY_CENTER);//Stacks circle center-to-center with the square
add(c1, c2, c3, c4, sq);//Add everything to the scene
camera.adjustToAllObjects(); //Adjust camera, so that everyone gets into the photo
waitSeconds(5);//That is, smile for the screenshot!
```

which produces the following image:

<img src="02_stackToExample.png" alt="02 stackToExample" style="zoom:50%;" />

You’ll notice two new methods here: The `copy()` method returns a copy of the object, and the `camera.adjustToAllObjects()` does as it says, rescales the camera so that everything fits into view.  The `fillColor` and `fillAlpha` sets the filling of the object. 

The `stackTo` command allows to easily generate aligned objects:

``` java
Shape previousPol = Shape.regularPolygon(3).fillColor(JMColor.random()).thickness(3);//First polygon, with random fill color
add(previousPol);
for (int n = 4; n < 10; n++) {
    Shape pol = Shape.regularPolygon(n).fillColor(JMColor.random()).thickness(3).stackTo(previousPol, Anchor.Type.RIGHT);
    add(pol);
    previousPol = pol;//New polygon becomes previous in the next iteration
}
camera.adjustToAllObjects();//Everyone should appear in the photo
waitSeconds(5);//Time for screenshot, but you already should know that
```

Which produces this regular polygons pattern. Note that all polygons are vertically aligned and their bounding boxes are stacked horizontally.

![02b stackToExample2](02b_stackToExample2.png)

You can change the `Anchor` to other values to see how these anchors work. Apart from `UPPER`, `LOWER`, `LEFT`, `RIGHT` and`BY_CENTER` there are also `UR` (up-right), `UL` (up-left), `DR` (down-right) and `DL` (down-left).

## StackToScreen

This methods is similar to `stackTo`, but it positions the object relative to the current view.

``` java
Shape sq=Shape.square();
add(sq.stackToScreen(Anchor.Type.LEFT));//Stack square to the left of the screen, with no gaps
add(sq.copy().stackToScreen(Anchor.Type.RIGHT,.3,.1));//Stack a copy of square to the left of the screen,with gaps of .3 horizontal and .1 vertical (here only horizontal one is used)
add(Shape.circle().stackToScreen(Anchor.Type.UL));//Stack a unit circle to the upper left corner of the screen, with no gaps
waitSeconds(5);
```

There is shortcut method if you want to simply put the object at the center screen. The method `.center()` is equivalent to `.stackToScreen(Anchor.Type.BY_CENTER)`.

## Aligning objects

The `MathObject`class has the method `align` which aligns the object with another one, using one of the aligns in the enum `Align: LEFT, RIGHT, UPPER, LOWER, HCENTER, VCENTER`.

```java
Line floor=Line.XAxis();
add(floor);
for (int n = 4; n < 10; n++) {
    Shape pol = Shape.regularPolygon(n).center().shift(Point.random().v).scale(Math.random()*.5);
    Shape pol2=pol.copy()
        .fillColor(JMColor.random())
        .thickness(3)
        .align(floor, MathObject.Align.LOWER);//Align bottom of the object with floor
    add(pol,pol2);
}
camera.adjustToAllObjects();//Everyone should appear in the photo
waitSeconds(5);//Smile!
```



<img src="03b_alignExample.png" alt="image-20201231181216715" style="zoom: 67%;" />

# Scaling objects
All `MathObject` instances can be scaled with the `scale` command. Scaling can be done from a given scale center or by default, the center of the object  bounding box.

``` java
add(Shape.circle().shift(-1, 0).scale(.5, 1));//x-scale and y-scale around center
add(Shape.circle().shift(0, 1).scale(Point.at(0, 0), 1.3, .2));//x-scale and y-scale around (0,0)
add(Shape.square().shift(1, 0).scale(.3)); //Uniform scale around center
waitSeconds(5);
```

produces the result:

<img src="04_ScaleExample1.png" alt="04 scaleExample1" style="zoom:50%;" />

# Rotating objects
The `rotate` command rotates the object around a given center (or the center of the object if none given). The angle is specified in radians, but can also be given in degrees using the `DEGREES` constant. The format is `object.rotate(center_of_rotation,angle)` or `object.rotate(angle)`. As in the `scale` method, if no center is specified, the center of the bounding box is chosen as rotation center.

For example:

``` java
Shape ellipse=Shape.circle().scale(.5,1);//Creates an ellipse
Point rotationCenter=Point.at(.5,0);
for (int n = 0; n < 180; n+=20) {
    add(ellipse.copy().rotate(rotationCenter,n*DEGREES));
}
waitSeconds(5);
```

Gives this spirograh-like picture:

<img src="05_rotateExample1.png" alt="05 rotateExample1" style="zoom:50%;" />

# Affine Transforms
`shift`, `rotate` and `scale` are particular cases of a more general affine transform implemented by the `AffineJTransform` class. This class defines general affine transforms in the 2D plane, and has several static convenience methods for some of the most common transforms:

The `createTranslationTransform(Vec v)` or `createTranslationTransform(Point A, Point B)` creates a traslation transform. The `shift` command is just a shortcut for this transform. 

The `create2DRotationTransform(Point center, double angle)` creates a rotation transform, used in the `rotate` command.

The `createScaleTransform(Point center, double sx, double sy, double sz)` creates a scaling transform. The z-scale factor is here for
compatibility to extend to the 3D case, but is currently not used. Used in the `scale` command.

Given any `MathObject` instance, there are 2 main methods to use an `AffineJTransform` object on it:

-   The `transform.applyTransform(object)` method transforms and modifies the current object, returning `void`.
    
-   The `transform.getTransformed(object)` returns a copy of the object transformed. The original object is unaltered.

## Homothecies
The `createDirect2DHomothecy(Point A, Point B, Point C, Point D, double alpha)` is a combination of shifting, rotating and uniform scaling. This method generates the (only) direct homothecy that maps the points (A,B) into points (C,D). The `alpha` parameter is used for animations, as a value of `alpha=0` returns the identity transform and `alpha=1` returns the full transform. Intermediate values return intermediate transforms, interpolating the shifting, rotating, and scaling parameters adequately.

Look at the following example:

``` java
Shape sq = Shape.square().shift(-1.5, -1).fillColor("darkgreen").fillAlpha(.3);//Square, fill color dark green, and opacity 30%
Point A = sq.getPoint(0).drawColor("darkblue");//First vertex of the square (lower-left corner), dark blue color
Point B = sq.getPoint(1).drawColor("darkblue");//First vertex of the square (lower-right corner), dark blue color
Point C = Point.at(1.5, -1).drawColor("darkred");//Destiny point of A, dark red color
Point D = Point.at(1.7, .5).drawColor("darkred");//Destiny point of B, dark red color
add(A, B, C, D);
for (double alpha = 0; alpha <= 1; alpha += .2) {
    AffineJTransform transform = AffineJTransform.createDirect2DHomothecy(A, B, C, D, alpha);
    add(transform.getTransformedObject(sq));//Adds a copy of the square, transformed
}
waitSeconds(5);
```

Produces the following sequence of interpolated transforms from one square to another. Note that an homothecy may change scale of objects, but proportions are unaltered:



<img src="06_homothecy1.png" alt="06 homothecy1" style="zoom:50%;" />

Notice also another new method here, the `getPoint(n)` method in a `Shape`, will return the n-th point at the shape.

> **WARNING**: You should be careful, when defining the parameters of a transformation like `createDirect2DHomothecy(A, B, C, D, alpha)` if the points `A, B, C, D` are going to be actually modified by the transformation itself (for example, A is an instance of a point of the shape you are transforming). The safe approach in this case should be using copies of the points as parameters, with the `.copy()` method.

## Reflections
If we want to make a reflection of an object, we can use the static methods `createReflection` and `createReflectionByAxis`. They differ in the way the transformation is specified:

-   `createReflection(Point A, Point B, double alpha)` creates the (only) reflection that maps the point `A` into point `B`. The reflection axis is the perpendicular bisector of the segment joining the two points.
    
-   `createReflectionByAxis(Point E1, Point E2, double alpha)` creates the (only) reflection with axis the line specified by the points `E1` and `E2`.

In both cases, the `alpha` parameter works in a similar way than the homothecy transform.

An example of `createReflection` is showed in the following source code:

``` java
Shape sq = Shape.regularPolygon(5).fillColor("violet").fillAlpha(.3);//Regular pentagon, fill violet, opacity 30%
Point A = sq.getPoint(0).copy().drawColor("darkblue");//Copy of the first vertex of the pentagon(lower-right corner), color dark blue
Point B = A.copy().shift(.5, -.2).drawColor("darkred");//Copy of A, shifted (.5,-2), color dark red
add(A, B);
for (double alpha = 0; alpha <= 1; alpha += .2) {
    AffineJTransform transform = AffineJTransform.createReflection(A, B, alpha);//Reflection that maps A into B
    add(transform.getTransformedObject(sq));//Adds a copy of the pentagon, transformed
}
camera.adjustToAllObjects();
waitSeconds(5);
```

<img src="07_reflectionExample1.png" alt="07 reflectionExample1" style="zoom:50%;" />
## General affine transforms
There is also a more general way to define an affine transform using `createAffineTransformation(Point A, Point B, Point C, Point D, Point E, Point F, double lambda)`. It returns the (only) affine transform that maps the points (A,B,C) into (D,E,F), with the `lambda` interpolation parameter as in the previous methods. Here’s an example:

``` java
Shape sq = Shape.square().drawColor("brown").thickness(3);
Shape circ = Shape.circle().scale(.5).shift(.5, .5).fillColor("orange").fillAlpha(.1);//A circle inscribed into the square
//We create the points with layer(1) so that the draw over the square and circles (by default in layer 0)
Point A = Point.at(0, 0).drawColor("darkblue").layer(1); 
Point B = Point.at(1, 0).drawColor("darkblue").layer(1); 
Point C = Point.at(0, 1).drawColor("darkblue").layer(1); 
Point D = Point.at(1.5, -.5).dotStyle(DotSyle.PLUS).thickness(2).drawColor("darkgreen");
Point E = Point.at(2, 0).dotStyle(DotSyle.PLUS).thickness(2).drawColor("darkgreen");
Point F = Point.at(1.75, .75).dotStyle(DotSyle.PLUS).thickness(2).drawColor("darkgreen");
add(sq, circ, A, B, C, D, E, F);

for (double alpha = 0; alpha <= 1; alpha += .2) {
    //A maps to D,B maps to E, C maps to F
    AffineJTransform transform = AffineJTransform.createAffineTransformation(A, B, C, D, E, F, alpha);
    add(transform.getTransformedObject(sq));//Adds a copy of the square, transformed
    add(transform.getTransformedObject(circ));//Adds a copy of the circle, transformed
}
camera.adjustToAllObjects();
waitSeconds(5);
```

That produces the following image:

<img src="08_GeneralAffineExample1.png" alt="08 GeneralAffineExample1" style="zoom:50%;" />

[home](https://davidgutierrezrubio.github.io/jmathanim/) [back](../index.html)