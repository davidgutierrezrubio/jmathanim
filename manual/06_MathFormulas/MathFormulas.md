# Mathematical formulas

Math formulas are created with the `LaTexMathObject` class. The process to create the formula from the latex text is as follows:

1. A latex document is generated from the formula (a preamble is created and some latex packages included).
2. A hash of the document is computed, which will be the name of the tex file, to be saved in the project_root/text directory.
3. The library tries to invoke the `LaTeX` executable to compile the file into a dvi, and then the `dvi2svgm` utility to convert the dvi to svg format. If the tex file already existed, this step is omitted, as `JMathAnim` assumes the svg is already generated from previous runs.
4. The svg is imported using the `SVGMathObject` methods, from which `LaTexMathObject` inherits.

As a result, in `JMAthAnim`, a math formula is just an array of `Shape` objects, that can be transformed as any other shape object,using then `.get(int n)` method.

The major drawback of this approach is that is not always clear which glyph of the formula corresponds to a given index.  For that, we have the method `formulaHelper` that takes a varargs of `LatexMathObject` objects, or `String` with the LaTeX code, overlays the shape number for each one, stacks the formulas vertically, zooms and adds them to the scene. If we execute, with the previous objects `t1` and `t2`:

```java
LaTeXMathObject t1=LaTeXMathObject.make("$x+2=0$");
LaTeXMathObject t2=LaTeXMathObject.make("$x=-2$");
formulaHelper(t1,t2);
waitSeconds(5);
```

We'll obtain the following image for 5 seconds. You can make an screenshot to examine it more deeply:

![image-20201121131228596](file://C:/Users/David/Documents/NetBeansProjects/jmathanim-web/jmathanim/manual/05_Animations/equation02.png?lastModify=1609495659)

It is recommended, before doing any animation that involves math formulas, to run this method to get useful references when defining animations.

