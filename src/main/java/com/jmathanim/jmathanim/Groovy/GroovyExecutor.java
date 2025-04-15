package com.jmathanim.jmathanim.Groovy;

import ch.qos.logback.classic.Level;
import com.jmathanim.Constructible.GeogebraLoader;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.jmathanim.LogUtils;
import com.jmathanim.jmathanim.Scene2D;
import com.jmathanim.mathobjects.MathObject;
import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static com.jmathanim.jmathanim.LogUtils.RESET;

public class GroovyExecutor extends Scene2D {

    private final GroovyExecutor scene;
    private String groovyScriptFilename;
    private Binding binding;
    private GroovyShell shell;
    private String userCodeWithoutImports;
    private String userCode;
    private Script script = null;

    /**
     * Creates a new Scene2D to execute the given Groovy script file
     *
     * @param groovyScriptFileName Filename of Groovy Script filename
     */
    public GroovyExecutor(String groovyScriptFileName) {
        super();
        this.groovyScriptFilename = groovyScriptFileName;
        scene = this;
        binding = new Binding();
        createBindings();


        shell = new GroovyShell(binding);


        try {
            //TODO:add parse errors log
//            userCode = new String(Files.readAllBytes(Paths.get(args[0])));

            userCodeWithoutImports = new String(Files.readAllBytes(Paths.get(groovyScriptFileName)));
            userCode = GroovyUtils.addImportsToSourceCode(userCodeWithoutImports);
            script = shell.parse(userCode);

        } catch (IOException e) {
            logger.error("File not found: " + groovyScriptFileName);
            System.exit(1);
        }
    }

    /**
     * Main method of GroovyExecutor
     *
     * @param args
     */
    public static void main(String[] args) {
//        JMathAnimScene tr = new AnimacionesFabrica();
        if (args.length == 0) {
            //print fancy help menu!
            System.out.println("Usage: java -jar <JMathAnim jar file> <groovy script files>");
            System.exit(0);
        }
        boolean isJavaFXRunning = false;
        for (String arg : args) {
            logger.info("Running Groovy Script " + LogUtils.BLUE + arg + RESET);
            JMathAnimScene tr = new GroovyExecutor(arg);
            tr.execute();
            if (tr.getConfig().isJavaFXRunning()) {
                isJavaFXRunning = true;
            }
        }
        // Al finalizar todos los scripts, cerrar JavaFX y terminar
        if (isJavaFXRunning)
            Platform.exit(); // Cierra JavaFX Toolkit
        System.exit(0);  // Termina la JVM
    }

    private void createBindings() {
        binding.setVariable("scene", scene);

        binding.setVariable("PI", PI);
        binding.setVariable("DEGREES", DEGREES);

        binding.setVariable("play", play);
        binding.setVariable("config", config);


        //Creates Closure for add method
        Closure<Void> addClosure = new Closure<Void>(this) {
            public Void doCall(Object arg) {
                if (arg instanceof MathObject) {
                    scene.add((MathObject) arg);
                } else if (arg instanceof MathObject[]) {
                    scene.add((MathObject[]) arg);
                } else if (arg instanceof GeogebraLoader) {
                    scene.add((GeogebraLoader) arg);
                } else {
                    throw new IllegalArgumentException("Not supported for add(): " + arg.getClass());
                }
                return null;
            }

            public Void doCall(Object... args) {
                if (args.length == 0) {
                    throw new IllegalArgumentException("Se requiere al menos un argumento");
                }
                // Si todos los argumentos son MathObject, conviértelos a un array
                if (Arrays.stream(args).allMatch(arg -> arg instanceof MathObject)) {
                    MathObject[] objs = Arrays.stream(args)
                            .map(arg -> (MathObject) arg)
                            .toArray(MathObject[]::new);
                    scene.add(objs);
                } else {
                    throw new IllegalArgumentException("Tipos no soportados en múltiples argumentos");
                }
                return null;
            }

            ;
        };
        binding.setVariable("add", addClosure);


        Closure<Void> waitClosure = new Closure<Void>(this) {
            public Void doCall(double time) {
                scene.waitSeconds(time);
                return null;
            }
        };
        binding.setVariable("waitSeconds", waitClosure);

        Closure<Void> advanceFrameClosure = new Closure<Void>(this) {
            public Void doCall() {
                scene.advanceFrame();
                return null;
            }
        };
        binding.setVariable("advanceFrame", advanceFrameClosure);
    }

    @Override
    public void setupSketch() {

        boolean hasSetupSketch = false;
        for (Method method : script.getClass().getMethods()) {
            if (method.getName().equals("setupSketch") && method.getParameterCount() == 0) {
                hasSetupSketch = true;
                break;
            }
        }
        File file = new File(groovyScriptFilename);
        String fileNameGroovy = file.getName();
        String fileNameGroovyWithoutExtension = fileNameGroovy.substring(0, fileNameGroovy.lastIndexOf("."));
        config.setOutputFileName(fileNameGroovyWithoutExtension);
        if (!hasSetupSketch) {//no init
            logger.setLevel(Level.DEBUG);
            logger.info("No init() method, switching to default configuration (preview, dark style)");
            config.parseFile("#preview.xml");
            config.parseFile("#dark.xml");
            config.setLimitFPS(true);
//            config.setDefaultLambda(t -> t);
        } else {
            try {
                script.invokeMethod("setupSketch", null);
            } catch (Exception e) {
                System.out.println("Error parsing config script");
                System.exit(1);
            }

        }
    }

    @Override
    public void runSketch() throws Exception {
        //these bindings have to be done in runSketch to ensure camera and fixedCamera are created
        binding.setVariable("camera", camera);
        binding.setVariable("fixedCamera", fixedCamera);
        boolean hasRunSketch=false;
        try {
            for (Method method : script.getClass().getMethods()) {
                if (method.getName().equals("runSketch") && method.getParameterCount() == 0) {
                    hasRunSketch = true;
                    break;
                }
            }

            if (hasRunSketch) {
                script.invokeMethod("runSketch", null);
            }
            else
            {//If script does not have runSketch method, just run root code
                script.run();
            }

        } catch (Exception e) {
            logger.error(LogUtils.RED + "Error running Groovy Script" + RESET);
//    GroovyExceptionInfo.processGroovyError(e,scriptText);
            GroovyUtils.processGroovyError(e, userCode, userCodeWithoutImports);
        }

    }
}

