package com.jmathanim.jmathanim.Groovy;

import ch.qos.logback.classic.Level;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.jmathanim.LogUtils;
import com.jmathanim.jmathanim.Scene2D;
import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import javafx.application.Platform;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static com.jmathanim.jmathanim.LogUtils.RESET;

public class GroovyExecutor extends Scene2D {

    private final GroovyExecutor scene;
    private final String groovyScriptFilename;
    private final Binding binding;
    private final CompilerConfiguration compilerConfig;
    private final GroovyShell shell;
    private String userCodeOriginal;
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
        binding = createBindings();
        compilerConfig = createCompilerConfiguration();

        shell = new GroovyShell(this.getClass().getClassLoader(), binding, compilerConfig);

        try {
            userCodeOriginal = new String(Files.readAllBytes(Paths.get(groovyScriptFileName)));
            userCode = GroovyUtils.processSourceCode(userCodeOriginal);
            script = shell.parse(userCode);

        } catch (IOException e) {
            logger.error("File not found: " + groovyScriptFileName);
            System.out.println(e.getLocalizedMessage());
            e.printStackTrace();

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
            File file = new File(arg);
            String absPath = file.getAbsolutePath();
            logger.info("Running Groovy Script " + LogUtils.BLUE + absPath + RESET);
            JMathAnimScene tr = new GroovyExecutor(absPath);
            tr.getConfig().setScriptMode(true);
            tr.execute();
            if (tr.getConfig().isJavaFXRunning()) {
                isJavaFXRunning = true;
            }
        }
        // This is tricky, as I cannot shutdown javafx and initialize it again in the same execution
        //If I want to execute several consecutive scripts I must delegate this to the main class
        if (isJavaFXRunning)
            Platform.exit(); // Close JavaFX Toolkit
        System.exit(0);
    }

    private CompilerConfiguration createCompilerConfiguration() {
        CompilerConfiguration compilerConfig = new CompilerConfiguration();
        ImportCustomizer imports = new ImportCustomizer();

        imports.addStarImports(
                "com.jmathanim.Animations",
                "com.jmathanim.Animations.MathTransform",
                "com.jmathanim.Animations.Strategies",
                "com.jmathanim.Animations.Strategies.ShowCreation",
                "com.jmathanim.Animations.Strategies.Transform",
                "com.jmathanim.Animations.Strategies.Transform.Optimizers",
                "com.jmathanim.Cameras",
                "com.jmathanim.Constructible",
                "com.jmathanim.Constructible.Conics",
                "com.jmathanim.Constructible.Lines",
                "com.jmathanim.Constructible.Others",
                "com.jmathanim.Constructible.Points",
                "com.jmathanim.Constructible.Transforms",
                "com.jmathanim.Enum",
                "com.jmathanim.MathObjects",
                "com.jmathanim.MathObjects.Axes",
                "com.jmathanim.MathObjects.Delimiters",
                "com.jmathanim.MathObjects.Shapes",
                "com.jmathanim.MathObjects.Text",
                "com.jmathanim.MathObjects.Text.TextUpdaters",
                "com.jmathanim.MathObjects.Tippable",
                "com.jmathanim.MathObjects.UpdateableObjects",
                "com.jmathanim.MathObjects.Updaters",
                "com.jmathanim.Utils",
                "com.jmathanim.Styling"
        );

        addAllPackagesFromJar("com.jmathanim", imports);

        compilerConfig.addCompilationCustomizers(imports);
        return compilerConfig;
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
        binding.setVariable("camera", getCamera());
        binding.setVariable("fixedCamera", getFixedCamera());
        boolean hasRunSketch = false;
        try {
            for (Method method : script.getClass().getMethods()) {
                if (method.getName().equals("runSketch") && method.getParameterCount() == 0) {
                    hasRunSketch = true;
                    break;
                }
            }

            if (hasRunSketch) {
                script.invokeMethod("runSketch", null);
            } else {//If script does not have runSketch method, just run root code
                script.run();
            }

        } catch (Exception e) {
            logger.error(LogUtils.RED + "Error running Groovy Script" + RESET);
//    GroovyExceptionInfo.processGroovyError(e,scriptText);
            GroovyUtils.processGroovyError(e, userCode, userCodeOriginal);
        }

    }


    private Binding createBindings() {
        Binding binding = new Binding();
        binding.setVariable("scene", scene);

        binding.setVariable("PI", PI);
        binding.setVariable("DEGREES", DEGREES);

        binding.setVariable("play", play);
        binding.setVariable("config", config);

        //Creates Closure for add method
//        Closure<Void> addClosure = new Closure<Void>(this) {
//            public Void doCall(Object arg) {
//                if (arg instanceof MathObject) {
//                    scene.add((MathObject) arg);
//                } else if (arg instanceof MathObject[]) {
//                    scene.add((MathObject[]) arg);
//                } else if (arg instanceof GeogebraLoader) {
//                    scene.add((GeogebraLoader) arg);
//                } else {
//                    throw new IllegalArgumentException("Not supported for add(): " + arg.getClass());
//                }
//                return null;
//            }
//
//            public Void doCall(Object... args) {
//                if (args.length == 0) {
//                    throw new IllegalArgumentException("Se requiere al menos un argumento");
//                }
//                // Si todos los argumentos son MathObject, conviértelos a un array
//                if (Arrays.stream(args).allMatch(arg -> arg instanceof MathObject)) {
//                    MathObject[] objs = Arrays.stream(args)
//                            .map(arg -> (MathObject) arg)
//                            .toArray(MathObject[]::new);
//                    scene.add(objs);
//                } else {
//                    throw new IllegalArgumentException("Tipos no soportados en múltiples argumentos");
//                }
//                return null;
//            }
//
//        };
//        binding.setVariable("add", addClosure);


        Closure<Void> waitClosure = new Closure<Void>(this) {
            public Void doCall(double time) {
                scene.waitSeconds(time);
                return null;
            }
        };
//        binding.setVariable("waitSeconds", waitClosure);

        Closure<Void> advanceFrameClosure = new Closure<Void>(this) {
            public Void doCall() {
                scene.advanceFrame();
                return null;
            }
        };
//        binding.setVariable("advanceFrame", advanceFrameClosure);
        return binding;
    }


    private void addAllPackagesFromJar(String basePackage, ImportCustomizer imports) {
        try {
            // Obtiene la ubicación del jar actual
            URL jarUrl = getClass().getProtectionDomain().getCodeSource().getLocation();
            File jarFile = new File(jarUrl.toURI());

            if (!jarFile.getName().endsWith(".jar")) {
                JMathAnimScene.logger.warn("Not executing from JAR, using only manual imports");
                imports.addStarImports(basePackage);
                return;
            }

            try (JarFile jar = new JarFile(jarFile)) {
                Set<String> packages = new HashSet<>();
                String prefix = basePackage.replace('.', '/') + "/";

                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();
                    if (name.endsWith(".class") && name.startsWith(prefix)) {
                        int lastSlash = name.lastIndexOf('/');
                        if (lastSlash > 0) {
                            String pkg = name.substring(0, lastSlash).replace('/', '.');
                            packages.add(pkg);
                        }
                    }
                }

                for (String pkg : packages) {
                    imports.addStarImports(pkg);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
