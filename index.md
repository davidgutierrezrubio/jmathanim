JMathAnim is a library written in JAVA intended to simplify the process of doing mathematical animations. 

Current version is 0.8.2. Although with limited features, it can be used to create many, non too complex animations. If you use it and find a bug/missing feature, feel free to fill an issue [here](https://github.com/davidgutierrezrubio/jmathanim/issues).

Currently, the library is tested on

* Windows 10 with Apache Netbeans 12.1 and Oracle JDK 1.8.0_261
* LUbuntu 20.04, with Apache Netbeans 12.1 an openjdk 11.0.7

For any other question, you can contact me at davidgutierrezrubio@gmail.com.

### Prerrequisites
A machine with JDK 1.8 installed. A Java IDE editor like [Netbeans](https://netbeans.org/) or [Eclipse](https://www.eclipse.org/projects/) is recommended.

### Manual
A basic user's manual can be found [here](manual/index.html).

### Basic Example
We will show a very basic example to illustrate the structure of an animation:
```java
public class myFirstScene extends Scene2D {
    
    @Override
    public void setupSketch() {
    }
    
    @Override
    public void runSketch() throws Exception {
        Shape s = Shape.square();
        play.showCreation(s);
	play.rotate(2,30*DEGREES,s);
        waitSeconds(5);
    }
	
    public static void main(String[] args) {
        JMathAnimScene scene = new myFirstScene();
        scene.execute();
    }
}
```
If you run this program, a window will open showing an animation where a white square is drawed, then rotated, and a few seconds (maybe 5 or more), the windows is closed.
The core of animation lies in the `myFirstScene` class, which is an extension of `Scene2D` class. This class has two methods, `setupSketch`, which manages the basic configuration of the animation, like fps, size, output, etc. and `runSketch`, which performs the animation itself.
In this example, the `setupSketch` method is empty, so the default configuration is loaded. In the `runSketch` method we have the first command:
```java
Shape s = Shape.square();
```
This command creates a `Shape` object which represents a 2D figure, closed or not. This class has several static, convenience methods to easily create most common shapes. With the method `square`, we create an unit square with lower left corner at (0,0). Note that defining this doesn't add the square to the scene, so for now it won't be drawed.

The following methods performs most of what is shown when running the program:
```java
play.showCreation(s);
```
The `showCreation` method performs an animation showing the creation of the object, drawing it from scratch. Different ways to animate the creation are available, depending of type of object to show.
```java
play.rotate(2,30*DEGREES,s);
```
The `rotate` method plays an animation showing the square being created, with a duration of 2 seconds. After the animation ends, the square is added to the scene.
The following command works in a similar way, except in this case it animates a rotation of the square, rotating it 30 degrees clockwise.
```java
waitSeconds(5);
```
The `waitSeconds` method simply waits for the specified amount of seconds, adding this frozen frames to the final output:

If you are satisfied with the result and want to create a movie, you may add the following command in the `setupSketch`method:
```java
    @Override
    public void setupSketch() {
    	config.setCreateMovie(true);
    }
```
By default, it will create a movie into a subfolder of the project called `media`, with the name `<name_of_my_class>_widthInPX.mp4`.


## Limitations

Right now, the library is still in development, so there may be bugs and missing features.

## Dependencies

This library uses the following 3rd party libraries:

* [JavaFX](https://openjfx.io/) For the graphics part.
* [Xuggler](http://www.xuggle.com/xuggler/) For creating videos.
* [Logback Project](http://logback.qos.ch/) For logging purposes.


## License

This project is licensed under the GNU v3 license (https://www.gnu.org/licenses/gpl-3.0.html)
