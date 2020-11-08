## Applying styles

Each `MathObject` has one `MODrawingProperties` object that stores drawing parameters.

## Basic styles

Each object has 2 colors: the draw color (changed with `.setColor`), used drawing the contour  y the fill color (changed with `.fillColor`), used to fill the object. Each color is stores in a `JMColor`object, with the components red, green, blue and alpha. The `.thickness`method sets the thickness of the stroke used to draw the object.

``` java
Shape r=Shape.regularPolygon(5).fillColor(JMColor.parse("CADETBLUE")).drawColor(JMColor.parse("#041137")).thickness(5);
```

<img src="02_01_colors.png" alt="image-20201105234514407" style="zoom:25%;" />

Here we can see the method `.parse` to define a color. All JavaFX color names are supported, as well as hexadecimal format `#RRGGBBAA` (8 hexadecimal digits), `#RRGGBB` (6 hexadecimal digits) and `#RGB` (4 hexadecimal digits) . Also, both methods to change colors are overloaded so that `drawColor(string)`is equivalent to `drawColor(JMColor.parse(string))`, and the same with `fillColor`.

The `dashStyle`method sets the dash used to draw the outline, chosen from the enum `DashStyle`. Currently, there are 3 different styles, `SOLID`, `DASHED`and `DOTTED`. The following code creates 3 pentagons with these dash styles.

```java
Shape r1 = Shape.regularPolygon(5).thickness(5);
Shape r2=r1.copy().stackTo(r1, Anchor.RIGHT,.1);
Shape r3=r1.copy().stackTo(r2, Anchor.RIGHT,.1);
r1.dashStyle(DashStyle.SOLID);
r2.dashStyle(DashStyle.DASHED);
r3.dashStyle(DashStyle.DOTTED);
add(LaTeXMathObject.make("{\\tt SOLID}").stackTo(r1, Anchor.BY_CENTER));
add(LaTeXMathObject.make("{\\tt DASHED}").stackTo(r2, Anchor.BY_CENTER));
add(LaTeXMathObject.make("{\\tt DOTTED}").stackTo(r3, Anchor.BY_CENTER));
add(r1,r2,r3);
camera.adjustToAllObjects();
waitSeconds(5);
```

<img src="dashStyles.png" alt="image-20201105235906935" style="zoom: 67%;" />

## Saving styles 

A concrete combination of drawing parameters can be saved in styles. The `config`objects stores the saved styles and has methods to manage them. To apply a style to an object, use the method `.style(styleName)`.

```java
Shape triangle=Shape.regularPolygon(3).thickness(5).fillColor("RED");
//Creates style named solidRed
config.createStyleFrom(triangle, "solidRed");
Shape circle=Shape.circle().stackTo(triangle,Anchor.LEFT);
//Apply style to circle
circle.style("solidRed");
add(triangle,circle);
waitSeconds(5);
```

## Configuring the scene

The `Scene`class has an instance of `JMathAnimConfig` class, named `config`, that allows to personalize global aspects of the animation. Most of these methods should be called only on the `setupSketch()`part of the animation. Invoking `config`methods in the `runSketch()`could lead to unpredictable behaviour.

```java
//Methods to adjust output
config.setMediaW(800);//Adjusts width output to 800px
config.setMediaH(600);//Adjusts height output to 600px
config.setFPS(25);//Adjusts frames per second of video output to 25 fps

config.setLowQuality();//Predefined adjusts: 854x480 video, at 30fps
config.setMediumQuality();//Predefined adjusts: 854x480 video, at 30fps
config.setHighQuality();//Predefined adjusts: 1920x1080 video, at 60fps

config.setCreateMovie(true);//Generates a mp4 files with the animation
config.setOutputDir("C:\\media");//Specifies output directory at c:\media
config.setOutputDir("media");//Specifies output directory at <PROJECT_DIR>\media (this is the default value)
config.setOutputFileName("animation");//Specifies video filename as animationWWW.mp4 where WWW is the width output (by default, the output file name is the name of the scene class)

config.setShowPreviewWindow(true); //Show the preview window (by default: true)


config.setBackgroundColor(JMColor.parse("WHITE)"));//Sets background color to white
config.setBackGroundImage("background.png");//Sets the background image, located at RESOURCES_DIR. If null, no image background is applied
config.setDrawShadow(true); //Apply shadow effect to the scene, using javafx shadow effect
config.setShadowParameters(10,15,15,.5f);//Sets shadow parameters (kernel 10, offsets 15 and 15, shadow alpha .5f)


config.setResourcesDir("c:\\resources");Specifies resources directory at c:\resources

   
 
```



As well as configuring output, you can also configure where the resources are. Resources can be background images, predefined styles, svg objects like arrow heads, etc. By default, the resources directory is located at `<PROJECT_DIR>\resources`and has the following structure:

```
resources\
	   +--  arrows\ svg heads of arrows
	   +--  config\ xml config files go here
	   rest of files (images, another svg, etc.)
```



## Loading config files

All the settings an definitions can be stored in `XML`files and loaded with the `ConfigLoader`class. This class holds the static method `ConfigLoader.parseFile("file.xml")` . These config files has to be located at the `RESOURCES_DIR\config`directory. Here is an example of a basic config file that I use for previewing, called `preview.xml`. The `<video>`tag controls aspects related to movie output:

```XML
<JMathAnimConfig>
    <video>
        <size width="1066" height="600" fps="30"/>
        <createMovie>false</createMovie>
        <showPreviewWindow>true</showPreviewWindow>
    </video>
</JMathAnimConfig>
```

And this for production, called `production.xml`. The `background` tag controls aspects like image or color background, or shadow effect.

```XML
<JMathAnimConfig>
    <video>
        <size width="1920" height="1080" fps="60"/>
        <createMovie>true</createMovie>
        <outputDir>c:\media</outputDir>
        <showPreviewWindow>false</showPreviewWindow>
    </video>
         <background>
        <shadows kernelSize="8" offsetX="15" offsetY="15" alpha=".5">true</shadows>
        <image>background1080.png</image>
    </background>
</JMathAnimConfig>
```

This way, in the `setupSketch()`method, you can change program behavior just changing the config file loaded.

You can have several config files with different, independent aspects. This is the `light.xml`config I used in examples shown:

```xml
<JMathAnimConfig>
    <include>dots.xml</include>
    <background>
        <color>#FDFDFD</color>
    </background>
    <styles>
        <style name="default">
            <drawColor>black</drawColor>
            <fillColor>#00000000</fillColor>
            <thickness>1</thickness>
        </style>
        <style name="latexdefault">
            <drawColor>black</drawColor>
            <fillColor>black</fillColor>
            <thickness>.5</thickness>
        </style>
         <style name="functionGraphDefault">
            <drawColor>black</drawColor>
            <fillColor>TRANSPARENT</fillColor>
            <thickness>.5</thickness>
        </style>
        <style name="solidred">
            <drawColor>black</drawColor>
            <fillColor>red</fillColor>
            <thickness>2</thickness>
        </style>
        <style name="solidblue">
            <drawColor>black</drawColor>
            <fillColor>blue</fillColor>
            <thickness>3</thickness>
        </style>
    </styles>
</JMathAnimConfig>
```

The `<styles>` tag allows defining styles to apply to your animation. There are 3 named styles that are important: `default`, `latexDefault`and `functionGraphDefault` (names are case-insensitive). The styles `latexdefault`and `functionGraphDefault` are applied by default to all `LaTexMathObject`and `FunctionGraph`objects. The style `default`is applied to the rest of MathObjects. If no style with that names are defined, a default style with black stroke and no fill will be applied.

The `<include>` tag that appears at the beginning loads another config files.  In this case, a `dots.xml`file with styles to dots are defined:

```XML
<JMathAnimConfig>  
    <styles>
        <style name="dotRedCircle">
            <drawColor>RED</drawColor>
            <fillColor>TRANSPARENT</fillColor>
            <thickness>.5</thickness>
            <dotStyle>circle</dotStyle>
        </style>
        <style name="dotBlueCross">
            <drawColor>#6ca2e0</drawColor>
            <fillColor>TRANSPARENT</fillColor>
            <thickness>.5</thickness>
            <dotStyle>cross</dotStyle>
        </style>
        <style name="dotYellowPlus">
            <drawColor>#FCE16D</drawColor>
            <fillColor>TRANSPARENT</fillColor>
            <thickness>.5</thickness>
            <dotStyle>plus</dotStyle>
        </style>
    </styles>
</JMathAnimConfig>
```

