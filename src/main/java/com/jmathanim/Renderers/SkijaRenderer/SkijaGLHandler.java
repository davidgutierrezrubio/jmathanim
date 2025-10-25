//package com.jmathanim.Renderers.SkijaRenderer;
//
//import com.jmathanim.Cameras.Camera;
//import com.jmathanim.jmathanim.JMathAnimConfig;
//import com.jmathanim.mathobjects.Shape;
//import io.github.humbleui.skija.*;
//import org.lwjgl.glfw.GLFW;
//import org.lwjgl.opengl.GL;
//import org.lwjgl.system.MemoryUtil;
//
//import java.awt.image.BufferedImage;
//import java.util.ArrayList;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.concurrent.atomic.AtomicReference;
//import java.util.function.Consumer;
//
//public class SkijaGLHandler extends SkijaHandler {
//    private final ArrayList<Consumer<SkijaUtils>> drawCommands = new ArrayList<>();
//    public LinkedBlockingQueue<Runnable> glQueue = new LinkedBlockingQueue<>();
//    private long glfwWindow;
//    private DirectContext context;
//    private AtomicBoolean keepRunning;
//    private boolean waitForRender;
//
//    public SkijaGLHandler(JMathAnimConfig config, AtomicBoolean keepRunning) {
//        super(config);
//        glQueue = new LinkedBlockingQueue<>();
//        this.keepRunning = keepRunning;
//
//    }
//
//    public void initialize() {
//        super.initialize();
//        keepRunning.set(true);
//
//        // Crear un CountDownLatch para esperar a que la ventana esté completamente creada
//        CountDownLatch latch = new CountDownLatch(1);
//        // Crear el hilo del renderer
//        Thread renderThread = new Thread(() -> {
//            if (!GLFW.glfwInit()) {
//                throw new IllegalStateException("No se pudo inicializar GLFW");
//            }
//
//            glfwWindow = GLFW.glfwCreateWindow(config.mediaW, config.mediaH, "Ventana Skija + LWJGL", MemoryUtil.NULL, MemoryUtil.NULL);
//            if (glfwWindow == MemoryUtil.NULL) {
//                throw new RuntimeException("No se pudo crear la ventana");
//            }
//
//            GLFW.glfwMakeContextCurrent(glfwWindow);
//            GLFW.glfwSwapInterval(0); // VSync
//            GL.createCapabilities(); // Inicializa OpenGL
//
//            this.context = DirectContext.makeGL();
//            BackendRenderTarget renderTarget = BackendRenderTarget.makeGL(
//                    config.mediaW,
//                    config.mediaH,
//                    0,
//                    8,
//                    0,
//                    FramebufferFormat.GR_GL_RGBA8
//            );
//            this.surface = Surface.makeFromBackendRenderTarget(
//                    context,
//                    renderTarget,
//                    SurfaceOrigin.BOTTOM_LEFT,
//                    SurfaceColorFormat.RGBA_8888,
//                    ColorSpace.getSRGB()
//            );
//
//            latch.countDown();
//            while (keepRunning.get()) {
//                try {
//                    Runnable command = glQueue.take();// Espera hasta que haya un comando
//                    command.run();
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//
//        });
//
//        renderThread.start();
//
//        // Esperar a que el renderThread haya creado completamente la ventana y el contexto
//        try {
//            latch.await();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//
//
//        this.canvas = surface.getCanvas();
//
//    }
//
//    public void renderFrame() {
//
//        // Ejecutar todos los comandos de dibujo
//        for (Consumer<SkijaUtils> command : drawCommands) {
//            command.accept(skijaUtils);
//        }
//
//        drawCommands.clear(); // Limpiar comandos tras ejecutar
//
//        // Enviar a GPU y mostrar
//        surface.flushAndSubmit();
//        GLFW.glfwSwapBuffers(glfwWindow);
//        GLFW.glfwPollEvents();
//    }
//
//    public void close() {
//        surface.close();
//        context.close();
//    }
//
//
//    @Override
//    protected void preparePreviewWindow() {
//
//    }
//
//    @Override
//    protected void closeWindow() {
//        //Wait for rendering queue to finish...
//        while (!glQueue.isEmpty()) {
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        keepRunning.set(false);
//        try {
//            glQueue.put(() -> {
//                GLFW.glfwDestroyWindow(glfwWindow);
//                GLFW.glfwTerminate();
//            });
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public void drawPath(Shape mobj, Camera camera) {
//
//        drawCommands.add(skijaCommand -> {
//            canvas.save();
//            //Check if transform is created for this camera in this frame...
//            canvas.concat(retrieveCameraMatrix(camera));
//
//            applyPaintCommands(mobj);
//            canvas.restore();
//        });
//    }
//
//    @Override
//    protected boolean isPreviewWindowVisible() {
//        return true;
//    }
//
//    @Override
//    protected void updateImagePreviewWindow(BufferedImage image) {
//        CountDownLatch latch = new CountDownLatch(1);
////        AtomicReference<BufferedImage> resul = new AtomicReference<>();
//        glQueue.add(() -> {
//            this.renderFrame();
//            latch.countDown();
//        });
//        try {
//            latch.await();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    protected BufferedImage getRenderedImage(int frameCount) {
//        CountDownLatch latch = new CountDownLatch(1);
//        AtomicReference<BufferedImage> resul = new AtomicReference<>();
//        glQueue.add(() -> {
//            resul.set(SkijaToBufferedImage.convertGLtoBufferedImage(context, surface));
//            latch.countDown();
//        });
//        try {
//            latch.await();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        return resul.get();
//    }
//
//
//}
