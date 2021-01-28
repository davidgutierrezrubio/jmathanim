![logo](logo.gif)

JMathAnim is a library written in JAVA intended to simplify the process of doing mathematical animations. 
Current version is 0.8.7. 

Although with limited features, it can be used to create many, non too complex animations. If you use it and find a bug/missing feature, feel free to fill an issue [here](https://github.com/davidgutierrezrubio/jmathanim/issues). For any other question, you can contact me at davidgutierrezrubio@gmail.com.

## Why Java?

The short answer is: why not?

The long answer is: See the next point.

## But...why not Python?

Yes, Python has become the de facto language for mathematicians and other related areas, mostly for the excellent numpy library. If you want to make math animations with Python, you can try the manim library from [3Blue1Brown](https://www.youtube.com/channel/UCYO_jab_esuFRV4b17AJtAw) (or much better, the [manimce](https://pypi.org/project/manimce/) community version). In fact, this project was based initially on the manim library.

Personally, after years of experience with both languages, I prefer Java over Python for some reasons:

* Static typing vs dynamic typing. With Java and a good IDE like NetBeans or Eclipse, static typing ensures clever suggestions about the available methods for any variable you declare. If you're lazy (like me) you can do 80% of the writing just autocompleting. Although Python introduced the [possibility](https://www.python.org/dev/peps/pep-0484/) of static typing, I still prefer the Java way.
* Easier installation. JMathAnim is a maven based, so all the required libraries are automatically downloaded prior to the first run. You just create a maven project and a few lines to the `pom.xml` to have a functional implementation of the library.
* The JavaFX library is a well designed, mature library to create graphics seamlessly integrating 2D and 3D objects. Currently JMathAnim doesn't use the 3D capabilities of this library, but is expected to do so in a future.

If you are used to another OO languages, like Python, it is relatively easy to switch to Java, at least the basic level to use this library.

# Gallery
[Here you can find some examples made with this library ](Gallery/Gallery.html)

# What's new?
Version 0.8.8-SNAPSHOT:

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
