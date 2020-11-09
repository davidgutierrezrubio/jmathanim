JMathAnim is a library written in JAVA intended to simplify the process of doing mathematical animations. 

Current version is 0.8.2. Although with limited features, it can be used to create many, non too complex animations. If you use it and find a bug/missing feature, feel free to fill an issue [here](https://github.com/davidgutierrezrubio/jmathanim/issues).

For any other question, you can contact me at davidgutierrezrubio@gmail.com.

### Prerrequisites
A machine with JDK installed

### Install
The easiest way to use it is creating a Maven project in your favorite IDE (Netbeans, Eclipse,...) and adding the following dependency to your `pom.xml` file:
```XML
<dependency>
  <groupId>com.github.davidgutierrezrubio</groupId>
  <artifactId>JMathAnim</artifactId>
  <version>0.8.0</version>
</dependency>
```

If you want to use snapshots version, you need to add the following code instead:

```XML
<dependency>
  <groupId>com.github.davidgutierrezrubio</groupId>
  <artifactId>JMathAnim</artifactId>
  <version>0.8.2-SNAPSHOT</version>
</dependency>
```

and specify the repository for the snapshots version.
```XML
<repository>
  id>snapshots-repo</id>
  <url>https://oss.sonatype.org/content/repositories/snapshots</url>
  <releases><enabled>false</enabled></releases>
  <snapshots><enabled>true</enabled></snapshots>
</repository>
```

### Manual
A basic user's manual can be found [here](manual/index.html)

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
	play.rotate(30*DEGREES,s);
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
The following method performs most of what is shown when running the program:
```java
play.showCreation(s);
```
This method plays an animation showing the square being created, with a default duration of 2 seconds. After the animation ends, the square is added to the scene.
The following command works in a similar way, except in this case it animates a rotation of the square, rotating it 30 degrees clockwise.
```java
play.rotate(30*DEGREES,s);
```
The `waitSeconds` method simply waits for the specified amount of seconds, adding this frozen frames to the final output:
```java
waitSeconds(5);
```

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
