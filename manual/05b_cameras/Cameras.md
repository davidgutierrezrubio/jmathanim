[home](https://davidgutierrezrubio.github.io/jmathanim/) [back](../index.html)

# Cameras

Since version V0.9.12, JMathAnim can manage several cameras. We will see how to use them to easily achieve some effects.

There are 2 predefined cameras built in JMathAnim, the default camera, that can be access through the public variable `camera` or the `getCamera()` method in your `Scene2D` class, and the fixed camera, accesible through `getFixedCamera()` method.

All `MathObject`s have a `Camera` object associated, that manages the conversion from math coordinates to screen coordinates. The math coordinates are the usual coordinates in the plane, that is, the origin at (0,0), y-coordinate increases upwards. Screen coordinates manage where the actual painting is done in the screen, movie frame, or image generated. They behave in a different way, for instance, the point (0,0) is always a the lower-left corner of the window, and y decreases upwards, but you don't have to worry about them. That's what `Camera` class is designed for. From now we will always refer to math coordinates.

The default `camera` object, as we have seen before, is centered at (0,0) with x ranging from -2 to 2. The y range is computed according to the aspect ratio of the screen. For example, if you are generating a 16:9 video, for example, y will range from -1.125 to 1.125. So what a `Camera` is essentially doing is mapping its math range into the screen or movie frame. A `MathObject` outside the `camera` range  will be "outside the screen" and partially drawn or not drawn at all.

As we have seen, the `Camera` objects can be shifted and scaled (not rotated...yet)



[home](https://davidgutierrezrubio.github.io/jmathanim/) [back](../index.html)