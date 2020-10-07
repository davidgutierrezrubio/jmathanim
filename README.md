# JMathAnim
A (mostly) port of manim to java

## Introduction

JMathAnim is a library written in JAVA intended to simplify the process of doing mathematical animations.

### Prerrequisites
A machine with JDK installed

### Install

If you are using a JAVA IDE like NetBeans, Eclipse, or Intellij Idea, the easiest way to install is to add the following lines to your `pom.xml` file.
```
maven code goes here
```
### Basic Example
We will show a very basic example to illustrate the structure of an animation:
```
public class myFirstScene extends Scene2D {
    
    @Override
    public void setupSketch() {
    }
    
    @Override
    public void runSketch() {
        Shape s = Shape.square();
        play.showCreation(s);
	play.rotate(s,30*DEGREES);
        waitSeconds(5);
    }
	
	public static void main(String[] args) {
        JMathAnimScene scene = new myScene();
        scene.execute();
    }
}
```
If you run this program, a window will open showing an animation where a white square is drawed, then rotated, and a few seconds (maybe 5 or more), the windows is closed.
The core of animation lies in the `myFirstScene` class, which is an extension of `Scene2D` class. This class has two methods, `setupSketch`, which manages the basic configuration of the animation, like fps, size, output, etc. and `runSketch`, which performs the animation itself.
In this example, the `setupSketch` method is empty, so the default configuration is loaded. In the `runSketch` method we have the first command:
```
Shape s = Shape.square();
```
This command creates a `Shape` object which represents a 2D figure, closed or not. This class has several static, convenience methods to easily create most common shapes.With the method `square`, we create an unit square with lower left corner at (0,0). Note that defining this doesn't add the square to the scene, so for now it won't be drawed.
The following method performs most of what is shown when running the program:
```
play.showCreation(s);
```
This method plays an animation where the square is created. The default duration of this animation is 2 seconds. After the animation ends, the square is added to the scene.
The following command works in a similar way, except in this case it animates a rotation of the square, rotating it 30 degrees clockwise.
```
play.rotate(s,30*DEGREES);
```
The next command simply waits for the specified amount of seconds, adding this frozen frames to the final output:
```
waitSeconds(5);
```

## Dependencies

This library uses the following 3rd party libraries:

* [Humble video encoder](https://github.com/artclarke/humble-video) For creating videos.



## License

This project is licensed under the GNU v3 license (https://www.gnu.org/licenses/gpl-3.0.html)
