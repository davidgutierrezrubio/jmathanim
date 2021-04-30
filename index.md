![logo](logo.gif)

JMathAnim is a library written in JAVA intended to simplify the process of doing mathematical animations. Current version is 0.9.2. 

If you use it and find a bug/missing feature, feel free to fill an issue [here](https://github.com/davidgutierrezrubio/jmathanim/issues). For any other question, you can contact me at davidgutierrezrubio@gmail.com.

You can also follow my [Youtube](https://www.youtube.com/channel/UCeczwEqSrAwZbPdHADN8rfQ) channel where I occasionally upload math videos and my [Twitter](https://twitter.com/DavidCalculin) account (both mostly in Spanish).

# Gallery
[Here you can find some examples made with this library ](Gallery/Gallery.html)

# What's new?
Version 0.9.2:

* Added lambda functions to add effects to animations

* Added ComposeLayout for MathObjectGroup objects

Version 0.9.1:

* Fixed bug in boolean operation union 
* Fixed bug loading internal resources

Version 0.9.0:

* Fixed bug loading styles from SVG objects, including LaTeXMathObject
* Added reset() method to the JMathAnimScene class

* Added effects to transform animations

* Added `contains` method to `Shape` objects to check if a point lies inside the `Shape`.
* Added  boolean operations `union`, `intersect` and  `substract` to `Shape` objects.
* Added updateable `BooleanShape`.
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