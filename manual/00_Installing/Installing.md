[home](https://davidgutierrezrubio.github.io/jmathanim/) [back](../index.html)

# Creating a JMathAnim Project

JMathAnim is a Java library using Maven as deployment tool. As as result, you can easily include it in any project using a modern IDE. For example, let's see how to create a JMathAnim project in Netbeans:

1) Create a new Java project with Maven:

![image-20201110145052433](01_Install.png)

2) Set the name of the project. The Artifact, group id, version and package are not really necessary if you don't intend to publish this to a repository. Select "Finish" and the project will be created.

![image-20201110145157629](02_Install.png)


# Adding Maven dependencies
3) In the "Project files" section, we find the `pom.xml`file. This file is very important for a Maven based project. Among many things, it allows us to specify external dependencies. Double click to edit:

![image-20201110145320736](03_Install.png)

4) You will have a file like this:

![image-20201110145453859](04_Install.png)

You need to add the dependencies to add JMathAnim library to your project. You can use the latest released version (right now v0.8.6) or a snapshot version. Currently I recommend using development snapshots because released versions <1.0.0 may still have bugs, that are periodically solved in the snapshot versions before releasing a new one.

If you want the released version, add

```xml
<dependencies>
    <dependency>
        <groupId>com.github.davidgutierrezrubio</groupId>
        <artifactId>JMathAnim</artifactId>
        <version>0.8.6</version>
    </dependency>
</dependencies>
```

If you want to use the snapshot version, you need to add also the repository where it is located:

```xml
<repositories>
    <repository>
      <id>snapshots-repo</id>
      <url>https://oss.sonatype.org/content/repositories/snapshots</url>
      <releases><enabled>false</enabled></releases>
      <snapshots><enabled>true</enabled></snapshots>
    </repository>
</repositories>
<dependencies>
    <dependency>
        <groupId>com.github.davidgutierrezrubio</groupId>
        <artifactId>JMathAnim</artifactId>
        <version>0.8.7-SNAPSHOT</version>
    </dependency>
</dependencies>
```

4) In this example, I use the 0.8.3-snapshot version:

![image-20201110150506506](05_Install.png)

5) Once saved the file, created a Java file in the default package:

![image-20201110150617006](06_install.png)

6) In my example, I created a file named `myScene.java` and a class with the same name:

![image-20201110150746477](07_install.png)

# Writing the basic code
7) Hit the "Clean and build" button. This will tell Maven to download all needed dependencies. Should download JavaFX and Xuggler, which, depending on you bandwidth, may take a few minutes. Then we will make this class a subclass of `Scene2D`. If everything goes well, the autocomplete should show the `Scene2D`class, and it will require you to implement the 2 abstract methods `setupSketch()` and `runSketch()`:

![image-20201110150929426](08_install.png)

(Oops, I missed the convention that class names should start with a uppercase letter, sorry!)

8) Pressing alt+enter in Netbeans allows you to automatically add required imports and implement abstract methods:

![image-20201110151215103](09_install.png)

9) We're almost there... Add some basic code to test it. Delete the `throw` lines and put the codes as shown in the image:

![image-20201110151332189](10_install.png)

10) Finally, to be able to run that scene, you need to add a `main` method. In this same class (or another file if you prefer) we need to define a `public static void main` method. Hopefully, Netbeans as a shortcut for this, simply locate a top-level of the class and write `psvm`+TAB, and the text `public static void main(String[] args)` will be automatically created. Add the following code to your recently created`main`:



![image-20201110151702319](11_install.png)

And execute the code, pressing F6. You will be prompted for the `main` method the first time, just choose the class we created and hit "Select main class". If everything went as planned, you should see a preview window with this animation:

![12_install](12_install.gif)

And that's it! You have JMathAnim ready to create beautiful, educational, mathematical animations. Enjoy!

[home](https://davidgutierrezrubio.github.io/jmathanim/) [back](../index.html)