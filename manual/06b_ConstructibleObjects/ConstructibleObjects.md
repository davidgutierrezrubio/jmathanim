[home](https://davidgutierrezrubio.github.io/jmathanim/) [back](../index.html)

# Constructible objects
Since version 0.9.5, JMathAnim introduces constructible objects. These objects inherit from the abstract class `Constructible` which is itself a subclass of `MathObject`, so they have all common properties of these, like styling.

A constructible object is a mathobject that is built much like constructive geometry works. Thats it, computing parallels, intersections, etc. The main difference with a normal `MathObject` is that they are dependent on other `Constructible`objects, and most of them are "rigid" in the sense they cannot be shifted, rotated or scaled (well, they can, but without effect). All `Constructible` objects have name with the prefix `CT`.

An example is worth thousand non-example words:

```java
CTPoint A = CTPoint.make(Point.at(0, 0)).dotStyle(Point.DotSyle.CROSS).drawColor("blue");
CTPoint B = CTPoint.make(Point.at(1, 1)).dotStyle(Point.DotSyle.CROSS).drawColor("blue");;
CTSegment segment = CTSegment.make(A, B).drawColor("red").thickness(10);
CTPerpBisector perpBisector = CTPerpBisector.make(segment).drawColor("darkgreen").thickness(10);
CTCircle circle=CTCircle.makeCenterPoint(A, B).dashStyle(DashStyle.DASHED).drawColor("gray");
add(A, B, segment, perpBisector,circle);
play.shift(5, 0, -1, B);
waitSeconds(3);
```

Will give an animation like this:

<img src="../06a_DealingWithPaths/01_basicExample.gif" alt="01_basicExample" style="zoom:67%;" />

In this example, we have created 2 `CTPoint`objects. These are almost identical to the classical `Point`object. The `CTSegment` object represents a segment between 2 `CTPoint` objects. Note that this object is not a `Shape` in the sense that you can transform it into another `Shape` for example, but it always remains as a segment. These objects are more rigid but they allows richer properties depending on the context.

The `CTPerpBisector`does as its name says, builds the perpendicular bisector of the given segment.

Another object showed is the `CTCircle`. The static method `makeCenterPoint(A,B)`builds the circle with center `A` that pass through the point `B`.

Finally, we perform a `shift` animation to `B`. Note that all objects dependent on `B` are updated accordingly.

Each `Constructible` object has its own static creations methods, with several parameters. For example the `CTPerpBisector`admits a static builder from a `CTSegment` but also wit 2 `CTPoint` objects. Also several builder method that admits `CTPoint` are overloaded so that admit `Point`objects (they simply wrap them into new `CTPoint` instances).

## CTIntersection

The `CTIntersection` object extends the `CTPoint` object and represents the intersection point between 2 constructible objects. At the current version, several static builders can be used:

```java
CTIntersectionPoint p1=CTIntersectionPoint.make()
```



[home](https://davidgutierrezrubio.github.io/jmathanim/) [back](../index.html)

