![logo](logo.gif)

JMathAnim is a library written in JAVA intended to simplify the process of doing mathematical animations. 

[Gallery of examples](Gallery/Gallery.html)

Current version is 0.8.5. Although with limited features, it can be used to create many, non too complex animations. If you use it and find a bug/missing feature, feel free to fill an issue [here](https://github.com/davidgutierrezrubio/jmathanim/issues). For any other question, you can contact me at davidgutierrezrubio@gmail.com.

### What's new?
Version 0.8.5:

- Included the Delimiter objects, extensible delimiters like parenthesis, braces or brackets.

Version 0.8.6-SNAPSHOT:

- Fixed a bug when animation the creation of a `Arrow2D` object.
- Fixed bug when zooming arrows.
- Added double arrows with the `makeDoubleArrow2D` static method.
- Added type 3 of arrow.


### Prerrequisites
A machine with JDK 1.8 installed. A Java IDE editor like [Netbeans](https://netbeans.org/) or [Eclipse](https://www.eclipse.org/projects/) is recommended.

Currently, the library is tested on

* Windows 10 with Apache Netbeans 12.1 and Oracle JDK 1.8.0_261
* LUbuntu 20.04, with Apache Netbeans 12.1 an openjdk 11.0.7

### Installing

This library is [Maven](https://maven.apache.org/) based, which means it is very easy, using a modern Java IDE, to create a project that automatically downloads all needed dependencies to run your animations. A detailed step-by-step tutorial to create a project that uses JMathAnim is detailed [here](manual\00_Installing\Installing.html).

### Manual
A basic user's manual can be found [here](manual/index.html).

## Limitations

Right now, the library is still in development, so there may be bugs and missing features.

## Dependencies

This library uses the following 3rd party libraries:

* [JavaFX](https://openjfx.io/) For the graphics part.
* [Xuggler](http://www.xuggle.com/xuggler/) For creating videos.
* [Logback Project](http://logback.qos.ch/) For logging purposes.


## License

This project is licensed under the GNU v3 license (https://www.gnu.org/licenses/gpl-3.0.html)