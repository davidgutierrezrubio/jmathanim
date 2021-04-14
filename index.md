![logo](logo.gif)

JMathAnim is a library written in JAVA intended to simplify the process of doing mathematical animations. Current version is 0.8.9. 

If you use it and find a bug/missing feature, feel free to fill an issue [here](https://github.com/davidgutierrezrubio/jmathanim/issues). For any other question, you can contact me at davidgutierrezrubio@gmail.com.

You can also follow my [Youtube](https://www.youtube.com/channel/UCeczwEqSrAwZbPdHADN8rfQ) channel where I occasionally upload math videos and my [Twitter](https://twitter.com/DavidCalculin) account (both mostly in Spanish).

# Gallery
[Here you can find some examples made with this library ](Gallery/Gallery.html)

# What's new?
Version 0.9.0-SNAPSHOT:

* Added effects to transform animations

* Added `contains` method to `Shape` objects to check if a point lies inside the `Shape`.
* Added  boolean operations `union`, `intersect` and  `substract` to `Shape` objects.
* Added updateable `BooleanShape`.

Version 0.8.9:

* This version introduces use of advanced layouts for `MathObjectGroup` objects, allowing to work easily with large sets of MathObjects

* Fixed some bugs and added some javadocs.

Version 0.8.9-SNAPSHOT:

* Improved `setLayout`method and animation for `MathObjectGroup` objects (see manual)
* Added `BoxLayout`, `SpiralLayout`, `HeapLayout` and `PascalLayout`to the `MathObjectGroup` class.
* Fixed a bug that prevented the `showCreation` for `Arrow2D` objects

Version 0.8.8:

* Added `TippableObject`, to put labels, or any marks in points of a Shape.
* Improved cascade behaviour of styling methods for `MathObjectGroup` or `MultiShape` objects.

* Added `getObjectsFromLayers`method that returns a `MathObject[]` array with all objects from the scene in the specified layers.
* Removed method `putAt` . Use `moveTo` instead.

* Changed `BY_CENTER` anchor point name to more concise `CENTER`
* Fixed bug for line objects that did not load the default style

* Disabled XML validation so that SVG files load faster
* Added stackTo animation to fast access method play.stackTo
* Added merge command to Shape objects
* Added annulus and polyLine creation to Shape

* Fixed bug transforming shapes with the flip animation when their centers were not aligned
* Fixed bug parsing styles to SVG objects different from path (rect, circle...)

Version 0.8.7:

* Added `stackTo` and `align`animations.
* Animations `shift, moveIn, moveOut, stackTo, align` now inherit from a common abstract class that allows adding jump, scaling and rotating effects to these animations.
* Added `ShowCreation` method for `Axes` objects.
* Improved axes ticks: 
  * now there are primary and secondary ticks.
  * Secondary ticks can be showed only when reaching certain level of zoom.
  * `axes.setFormat`  allows to specify the precise format the numbers in ticks.
* Bug fixes

Version 0.8.6:

* Fixed time duration of ShowCreation for MultiShapeObjects
* Fixed bounding box for JMImage objects
* Included the Delimiter objects, extensible delimiters like parenthesis, braces or brackets.
* Fixed bug in copy() method of FunctionGraph
* Added showCreation strategy for delimiters
* Fixed a bug when animation the creation of a `Arrow2D` object.
* Fixed bug when zooming arrows.
* Added double arrows with the `makeDoubleArrow2D` static method.
* Added type 3 of arrow.
* Added `ParametricCurve.make` and `ParametricCurve.makePolar` static methods.

# Prerrequisites
A machine with JDK 1.8 installed. A Java IDE like [Netbeans](https://netbeans.org/) or [Eclipse](https://www.eclipse.org/projects/) is recommended.

Currently, the library is tested on

* Windows 10 with Apache Netbeans 12.1 and Oracle JDK 1.8.0_261
* LUbuntu 20.04, with Apache Netbeans 12.1 and openjdk 11.0.7

# Installing
This library is [Maven](https://maven.apache.org/) based, which means it is very easy, using a modern Java IDE, to create a project that automatically downloads all needed dependencies to run your animations. A detailed step-by-step tutorial to create a project that uses JMathAnim is detailed [here](manual\00_Installing\Installing.html).

# Manual
[A basic user's manual can be found here](manual/index.html).

# Limitations
Right now, the library is still in development, so there may be bugs and missing features.

# Dependencies
This library uses the following 3rd party libraries:
* [JavaFX](https://openjfx.io/) For the graphics part.
* [Xuggler](http://www.xuggle.com/xuggler/) For creating videos.
* [Logback Project](http://logback.qos.ch/) For logging purposes.

# License
This project is licensed under the GNU v3 license (https://www.gnu.org/licenses/gpl-3.0.html)
