[home](https://davidgutierrezrubio.github.io/jmathanim/) [back](../index.html)

# Disabling and enabling animations
Suppose you are writing a rather long animation.  Usually this process involves several test runs to check if everything goes as planned. If you are fine tuning the last part of the animation, you don't need to run all from the beginning to do this. Instead, you can add these methods to your code:

```java
disableAnimations();
//...animation code that is already tested and don't need to see it again before generating the final movie
enableAnimations();
//...animation code that I want to preview
```

The `disableAnimations()` and `enableAnimations()` methods allows to temporarily disable animations and frame generations. Updating and object creations are done, but the non-essential parts, like drawing, writing to movie, or performing the animations is omitted, dramatically increasing speed. You can also use this to generate a movie with only specific parts of the sketch.

# Updaters

An updater is an object whose state is automatically updated right before doing the draws on the screen. Any class that implements the `Updateable` interface can be registered as an updater. This interface implements the two following methods:

```java
public int getUpdateLevel();
public void update(JMathAnimScene scene);
```

The `getUpdateLevel` method returns the order of updating this object. Objects with level 0 update first, then all with level 1, etc. Thus, if you have an updater  A that depends on that another updater B to be previously updated before, you should set the update level of A greater than of B. All necessary updating commands should be set in the `update` method.

Any updater must be registered on the scene to be used, with the `registerUpdateable`method. Similarly, there is the `unregisterUpdateable` method that does the opposite.

Every MathObjects implements the interface `Updateable` , and is registered when added to the scene.	

For example, let's suppose we have the following simple animation, where a `Point` object named `A` moves from the point (1,1) to (-1,1):

```java
add(new Axes());
Point A = Point.at(1, 1);
play.shift(3,-2,0,A);
waitSeconds(3);
```

We want to create a `Point`subclass that automatically locates at the normalized coordinates of point `A`, that is, the projection of `A` into the unit circle. As the `Point` class implements the `updateable` interface, the easiest way is to subclass the `Point` and override the `getUpdateLevel` and `update` methods.

```java
class UnitPoint extends Point {
    Point sourcePoint;

    public UnitPoint(Point sourcePoint) {
        this.sourcePoint = sourcePoint;
    }

    @Override
    public int getUpdateLevel() {
        //The update level of the source point plus 1, ensures that this object is updated after the source object
        return sourcePoint.getUpdateLevel() + 1;
    }

    @Override
    public void update(JMathAnimScene scene) {
        double norm = sourcePoint.v.norm();
        if (norm != 0) {
            this.v.x = sourcePoint.v.x / norm;
            this.v.y = sourcePoint.v.y / norm;
        }
    }
}
```

and modify the scene, adding an instance of this class:

```java
add(new Axes());
Point A = Point.at(1, 1);
UnitPoint B = new UnitPoint(A);
B.drawColor(JMColor.RED);
add(B);
play.shift(3, -2, 0, A);
waitSeconds(3);
```

Generates the following animation:

![Updater01](Updater01.gif)

# Predefinied updaters

JMathAnim has some built-in updaters that maybe useful:

## Camera always ajusted to objects

With the `CameraAlwaysAdjusting`  updater, you can force the camera to show all objects in the scene. The camera will zoom out when needed, but not zoom in. Admits 3 parameters: the camera (currently there is only one), and the horizontal and vertical gaps. For example:

```java
registerUpdateable(new CameraAlwaysAdjusting(camera, .1, .1));
```

## Stacks permanently an object to another

```java
Shape circ1=Shape.circle().scale(.3).fillColor(JMColor.BLUE).thickness(3);
Shape circ2=circ1.copy();
Shape circ3=circ1.copy();
Shape circ4=circ1.copy();
Shape sq=Shape.square().center().thickness(4);
add(circ1,circ2,circ3,circ4);
        
//Stacks permantently the LEFT of circ1 with the RIGHT of sq
registerUpdateable(new AnchoredMathObject(circ1, Anchor.Type.LEFT,sq, Anchor.Type.RIGHT));

//Stacks permantently the RIGHT of circ1 with the LEFT of sq
registerUpdateable(new AnchoredMathObject(circ2, Anchor.Type.RIGHT,sq, Anchor.Type.LEFT));

//Stacks permantently the LOWER of circ1 with the UPPER of sq
registerUpdateable(new AnchoredMathObject(circ3, Anchor.Type.LOWER,sq, Anchor.Type.UPPER));

//Stacks permantently the UPPER of circ1 with the LOWER of sq
registerUpdateable(new AnchoredMathObject(circ4, Anchor.Type.UPPER,sq, Anchor.Type.LOWER));
        
play.rotate(3, 90*DEGREES, sq);
waitSeconds(3);
```

![Updater02](Updater02.gif)

## Transformed path 

A path that is always the image of another path, using an affine transformation.

```java
Shape sq = Shape.square().fillColor("seagreen").thickness(3);//The original path
Point A = Point.at(0, 0); //A maps to D
Point B = Point.at(1, 0); //B maps to E
Point C = Point.at(0, 1); //C maps to F
Point D = Point.at(1.5, -.5).dotStyle(DotSyle.CROSS);
Point E = Point.at(2, 0).dotStyle(DotSyle.CROSS);
Point F = Point.at(1.75, .75).dotStyle(DotSyle.CROSS);

AffineJTransform transform = AffineJTransform.createAffineTransformation(A, B, C, D, E, F, 1);
Shape sqTransformed = new TransformedJMPath(sq, transform);//The transformed path
sqTransformed.fillColor("steelblue").thickness(3);
add(sqTransformed, sq, A, B, C, D, E, F);

camera.adjustToAllObjects();
play.rotate(5, 90 * DEGREES, sq);
waitSeconds(5);
```

![Updater03](Updater03.gif)

## Trail

A trail is a `Shape` subclass that updates every frame adding the position of a marker point.  Let's draw a cycloid, using a combined `shift` and `rotate` animation:

```java
double circleRadius=.25;
Shape circle = Shape.circle()
    .scale(circleRadius).fillColor("royalblue")
    .thickness(3).stackToScreen(Anchor.Type.LEFT)
    .rotate(-90 * DEGREES);//Rotate it so that point 0 touches the floor
//By default a circle shape has 4 point, so point 0 and 2 make a diameter
Shape radius=Shape.segment(circle.getPoint(0),circle.getPoint(2)).layer(1).thickness(2);
//Note that, as radius is created with point instances of the Shape circle, we don't need to animate radius, only circle

Line floor = Line.XAxis().stackTo(circle, Anchor.Type.LOWER);//The "floor"
add(floor,radius);//Add everyhing (no need to add circle because it will automatically added with the shift and rotate animation)

Trail trail = new Trail(circle.getPoint(0));  //The Trail object
add(trail.layer(1).thickness(6).drawColor(JMColor.parse("tomato")));
//Ok, time to move this!
Animation shift = Commands.shift(10, 4 * PI*circleRadius, 0, circle);
Animation rotate = Commands.rotate(10, -4 * PI, circle).setUseObjectState(false);
playAnimation(shift, rotate);
waitSeconds(3);
```
![trail01](trail01.gif)

# The addOnce method

A useful method when creating procedural animations is the `addOnce(obj)` method. This method will add the specified object(s) to the scene but remove them after they are being drawed, so they "live" only for a frame. This method may be useful when you need to create an object for every frame, draw, and remove it because you will use another object in the next frame. 

# Current status of methods implemented to MathObjects

Not all `MathObject` and `Animation` combinations are compatible. Below is a table that shows, at the current version of the library, what you can and cannot do:

| MathObject      | Affine transforms related: Shift, scale, rotate , grow in, shrink out, highlight | ShowCreation animation | Transform animation                                          |
| --------------- | ------------------------------------------------------------ | ---------------------- | ------------------------------------------------------------ |
| Point           | Yes                                                          | No (use fade in)       | No                                                           |
| Shape           | Yes                                                          | Yes                    | Yes                                                          |
| Line            | Yes                                                          | Yes                    | Yes                                                          |
| Axes            | No                                                           | Yes                    | No                                                           |
| LaTeXMathObject | Yes                                                          | Yes                    | Yes (also you can use the specialized `TransformMathExpression` method) |
| Arrow2D         | Yes                                                          | Yes                    | Yes (delegates in the homothecy transform)                   |
| Delimiter       | No (you have the transform the anchor points instead)        | Yes                    | No (transform anchor points instead)                         |

[home](https://davidgutierrezrubio.github.io/jmathanim/) [back](../index.html)

