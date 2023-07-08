![logo](logo.gif)

JMathAnim is a library written in JAVA intended to simplify the process of doing
mathematical animations. Current version is 0.9.8.

If you use it and find a bug/missing feature, feel free to fill an issue
[here](https://github.com/davidgutierrezrubio/jmathanim/issues). For any other
question, you can contact me at davidgutierrezrubio@gmail.com.

You can also follow my [Youtube](https://www.youtube.com/channel/UCeczwEqSrAwZbPdHADN8rfQ) channel
where I occasionally upload math videos and my
[Twitter](https://twitter.com/DavidCalculin) account (both mostly in Spanish).

Gallery
=======

[Here you can find some examples made with this library](Gallery/Gallery.html)

Manual
======

[A basic user's manual can be found here](manual/index.html).

What's new?
===========

Version 0.9.9-SNAPSHOT (06/07/2023)

* Added `PointOwner` interface to improve import of Geogebra Point on Object.
* Added `CTTangentLine` class that represents the tangent lines from a point to a circle.

Version 0.9.8 (08/04/23)

* Added `LogoInterpreter` class to create `Shape` objects from a String of LOGO commands.
* Improved handling of delay effect for animations.

Version 0.9.7 (09/20/22)

-   Improved drawing algorithm in `FunctionGraph`for functions with infinity slope at some points.
-   Rewritten `TippableObject` and `LabelTip` classes, allowing more flexibility to animate tippable objects.
-   Added horizontal, vertical and both scaling capabilities to `shrinkOut`, `growIn` and `TransformMathExpression`.
-   Fixed bug where default adding animation was not used in `TransformMathExpression`.
-   Improved workaround for JavaFX bug that displays artifacts when drawing cubic lines when control points are equal to vertices.
-   Sounds are here! Added the possibility of including sounds in the
    animations, using an external `ffmpeg` executable. You can find a brief tutorial in the advanced topics section of the user manual.

Version 0.9.6 (03/06/22)

-   Removed thickness from arrow heads.

-   Added `saveImage(filename)` to save still frames into a image file.

-   `JMImage`objects now can be transformed using any affine transformation.

-   Updated to JavaFX 18

-   Fixed annoying bug that prevented JavaFX to properly draw cubic curves when
    control points matched destiny point

-   Removed extra frame that appeared at the end of an animation.

-   Implemented proper `copy()` method for `PointOnFunctionGraph`.

-   Fixed bugs and polished code in `ParametricCurve` class.

Version 0.9.5 (23/03/2022)

-   Added first version of `CrossMathElements`animation, that can be used to
    cross single or multiple elements of a formula.

-   Update levels are now computed at creation time, increasing the performance.

-   Added Constructible objects, like `CTLine`, `CTCircle`or
    `CTIntersectionPoint`.

-   Added Geogebra import capabilities. Some simple Geogebra documents can be
    imported and converted into Constructible objects.

-   Added `Ray` object

-   Added `polygon`and `polyline`import capabilities for SVG objects.

-   Now `LaTeXMathObject` instances are by default compiled using the excellent
    `JLaTeXMath`library, making it considerably faster and removing the need to
    install a LaTeX distribution.

Version 0.9.4 released (26/12/2021)

-   Added configuration flag `config.setShowDebugFrameNumbers` to superimpose
    frame number on animation.

-   Improved implementation of delimiters. Now they are proper Shape objects
    instead of pure fill objects .

-   Labels now can be easily added to delimiters with the method .setLabel.

-   Improved handling of thickness property.

-   SVG import now properly handles thickness and transform attributes.

-   Fixed a bug that prevented properly styling sliced MultiShapeObjects or
    LaTexMathObjects.

Version 0.9.3:

-   Added ContourHighlight animation.

-   Fixed a bug in `reverse`method when applied to paths with both curved and
    straight parts.

-   Improved `merge` method for `Shape` objects.

-   Added Delimiter.stackTo builder to automatically stack a delimiter to a
    MathObject

-   Improved drawing algorithms for `FunctionGraph`, including adaptative
    sampling and continuity check.

-   Added `saveToPNG` option to save each frame into a separate png file.

-   Added support for gradients through `JMLinearGradient` and
    `JMRadialGradient` classes

-   Added `JMImagePattern` class

Version 0.9.2:

-   Added lambda functions to add effects to animations

-   Added ComposeLayout for MathObjectGroup objects

Version 0.9.1:

-   Fixed bug in boolean operation union

-   Fixed bug loading internal resources

Version 0.9.0:

-   Fixed bug loading styles from SVG objects, including LaTeXMathObject

-   Added reset() method to the JMathAnimScene class

-   Added effects to transform animations

-   Added `contains` method to `Shape` objects to check if a point lies inside
    the `Shape`.

-   Added boolean operations `union`, `intersect` and `substract` to `Shape`
    objects.

-   Added updateable `BooleanShape`. \# Prerrequisites A machine with JDK 1.8
    installed. A Java IDE like [Netbeans](https://netbeans.org/) or
    [Eclipse](https://www.eclipse.org/projects/) is recommended.

Currently, the library is tested on

-   Windows 10 with Apache Netbeans 12.1 and Oracle JDK 1.8.0_261

-   LUbuntu 20.04, with Apache Netbeans 12.1 and openjdk 11.0.7

Installing
==========

This library is [Maven](https://maven.apache.org/) based, which means it is very
easy, using a modern Java IDE, to create a project that automatically downloads
all needed dependencies to run your animations. A detailed step-by-step tutorial
to create a project that uses JMathAnim is detailed
[here](manual\00_Installing\Installing.html).

Limitations
===========

Right now, the library is still in development, so there may be bugs and missing
features.

Dependencies
============

This library uses the following 3rd party libraries: \*
[JavaFX](https://openjfx.io/) for 2D graphics generation. \*
[Xuggler](http://www.xuggle.com/xuggler/) for video creation. \*
[JLatexMath](https://github.com/opencollab/jlatexmath) for LaTeX shapes
generation (text and mathematical expressions). \* [Logback
Project](http://logback.qos.ch/) For logging purposes. \* Optionally, a working
LaTeX distribution accessible from your path can be used to generate texts and
formulas as an alternative way to
[JLatexMath](https://github.com/opencollab/jlatexmath)

License
=======

This project is licensed under the GNU v3 license
(https://www.gnu.org/licenses/gpl-3.0.html)

