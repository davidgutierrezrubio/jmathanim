package com.jmathanim.jmathanim.Groovy;

import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.jmathanim.LogUtils;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
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
import java.util.stream.Collectors;

import static com.jmathanim.jmathanim.JMathAnimScene.*;
import static com.jmathanim.jmathanim.LogUtils.RESET;

public class GroovyUtils {
    private final JMathAnimScene scene;
    public String userCodeOriginal;
    public String userCode;
    private Script script = null;

    private final String groovyScriptFilename;

    public GroovyUtils(String groovyScriptFilename,JMathAnimScene scene) {
        this.groovyScriptFilename = groovyScriptFilename;
        this.scene=scene;
        Binding binding = GroovyUtils.createBindings(scene);
        CompilerConfiguration compilerConfig = GroovyUtils.createCompilerConfiguration(scene);

        GroovyShell shell = new GroovyShell(scene.getClass().getClassLoader(), binding, compilerConfig);

        try {
            userCodeOriginal = new String(Files.readAllBytes(Paths.get(groovyScriptFilename)));
            userCode = GroovyUtils.processSourceCode(userCodeOriginal);
            script = shell.parse(userCode);

        } catch (IOException e) {
            logger.error("File not found: " + groovyScriptFilename);
            System.out.println(e.getLocalizedMessage());
            e.printStackTrace();

            System.exit(1);
        }
    }

    public void runSetup() {

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
        scene.config.setOutputFileName(fileNameGroovyWithoutExtension);
        if (!hasSetupSketch) {//no init
//            logger.setLevel(Level.DEBUG);
//            logger.info("No init() method, switching to default configuration (preview, dark style)");
//            scene.config.parseFile("#preview.xml");
//            scene.config.parseFile("#dark.xml");
//            scene.config.setLimitFPS(true);
////            config.setDefaultLambda(t -> t);
        } else {
            try {
                script.invokeMethod("setupSketch", null);
            } catch (Exception e) {
                System.out.println("Error parsing config script");
                System.exit(1);
            }

        }
    }

    public void runSketch() {
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

    // Métodos auxiliares
    public static String addLineNumbers(String script) {
        String[] lines = script.split("\n");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            sb.append("/* LINE ").append(i+1).append(" */ ").append(lines[i]).append("\n");
        }
        return sb.toString();
    }

    public static void processGroovyError(Exception e, String userCode, String userCodeWithoutImports) {
        // Buscar directamente en el stack trace

        for (StackTraceElement ste : e.getStackTrace()) {
            if (ste.getFileName() != null && ste.getFileName().contains("Script1.groovy")) {
                int lineNumber = ste.getLineNumber();
                int lineGap=countLines(userCode)-countLines(userCodeWithoutImports);
                String[] scriptLines = userCode.split("\n");
                logger.error("At line " + LogUtils.PURPLE+(lineNumber-lineGap) + RESET+": "+" " + LogUtils.PURPLE+ scriptLines[lineNumber - 1].trim()+RESET);
                logger.error(LogUtils.RED+e.getMessage()+RESET);
                break;
            }
        }
    }
    private static int countLines(String str){
        String[] lines = str.split("\r\n|\r|\n");
        return  lines.length;
    }

    public static String processSourceCode(String userCode) {

        //Groovy uses $ as escape code
        String sourceCode=userCode.replace("$$", "\\$\\$");

        return removeFieldLines(sourceCode);


    }
    public static String removeFieldLines(String script) {
        return script.lines()
                .filter(line -> !line.contains("@Field") && !line.contains("import groovy.transform.Field"))
                .collect(Collectors.joining("\n"));
    }

    public static Binding createBindings(JMathAnimScene scene) {
        Binding binding = new Binding();
        binding.setVariable("scene", scene);

        binding.setVariable("PI", PI);
        binding.setVariable("DEGREES", DEGREES);

        binding.setVariable("play", scene.play);
        binding.setVariable("config", scene.config);

        binding.setVariable("camera", scene.getCamera());
        binding.setVariable("fixedCamera", scene.getFixedCamera());

        //Creates Closure for add method
//        Closure<Void> addClosure = new Closure<Void>(this) {
//            public Void doCall(Object arg) {
//                if (arg instanceof MathObject) {
//                    scene.add((MathObject) arg);
//                } else if (arg instanceof MathObject<?>[]) {
//                    scene.add((MathObject<?>[]) arg);
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
//                    MathObject<?>[] objs = Arrays.stream(args)
//                            .map(arg -> (MathObject) arg)
//                            .toArray(MathObject<?>[]::new);
//                    scene.add(objs);
//                } else {
//                    throw new IllegalArgumentException("Tipos no soportados en múltiples argumentos");
//                }
//                return null;
//            }
//
//        };
//        binding.setVariable("add", addClosure);


//        Closure<Void> waitClosure = new Closure<Void>(this) {
//            public Void doCall(double time) {
//                scene.waitSeconds(time);
//                return null;
//            }
//        };
//        binding.setVariable("waitSeconds", waitClosure);

//        Closure<Void> advanceFrameClosure = new Closure<Void>(this) {
//            public Void doCall() {
//                scene.advanceFrame();
//                return null;
//            }
//        };
//        binding.setVariable("advanceFrame", advanceFrameClosure);
        return binding;
    }

    public static CompilerConfiguration createCompilerConfiguration(JMathAnimScene scene) {
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

        GroovyUtils.addAllPackagesFromJar(scene,"com.jmathanim", imports);

        compilerConfig.addCompilationCustomizers(imports);
        return compilerConfig;
    }
    public static void addAllPackagesFromJar(JMathAnimScene scene,String basePackage, ImportCustomizer imports) {
        try {
            // Obtiene la ubicación del jar actual
            URL jarUrl = scene.getClass().getProtectionDomain().getCodeSource().getLocation();
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
