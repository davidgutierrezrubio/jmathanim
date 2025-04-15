package com.jmathanim.jmathanim;

import ch.qos.logback.classic.Level;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GroovyExecutor extends Scene2D {

    private final String[] args;
    private final Binding binding;
    private final GroovyShell shell;
    private Script script=null;

    public GroovyExecutor(String[] args) {
        super();
        this.args = args;
        binding = new Binding();
        binding.setVariable("scene", this);
        binding.setVariable("play", play);
        binding.setVariable("config", config);
        shell = new GroovyShell(binding);

        try {
            //TODO:add parse errors log
            String userCode = new String(Files.readAllBytes(Paths.get(args[0])));

            script = shell.parse(addImportsToSourceCode(userCode));

        } catch (IOException e) {
            logger.error("File not found: "+args[0]);
            System.exit(1);
        }
    }

    private String addImportsToSourceCode(String userCode) {
        StringBuilder fullScript = new StringBuilder();
        fullScript.append("import com.jmathanim.mathobjects.*\n");
        fullScript.append("import com.jmathanim.mathobjects.Axes.*\n");
        fullScript.append("import com.jmathanim.mathobjects.Delimiters.*\n");
        fullScript.append("import com.jmathanim.mathobjects.Text.*\n");
        fullScript.append("import com.jmathanim.mathobjects.Tippable.*\n");
        fullScript.append("import com.jmathanim.mathobjects.updaters.*\n");
        fullScript.append("import com.jmathanim.Animations.*\n");
        fullScript.append("import com.jmathanim.Animations.MathTransform.*\n");
        fullScript.append("import com.jmathanim.Animations.Strategies.*\n");
        fullScript.append("import com.jmathanim.Animations.Strategies.ShowCreation.*\n");
        fullScript.append("import com.jmathanim.Animations.Strategies.Transform.*\n");
        fullScript.append("import com.jmathanim.Animations.Strategies.Transform.Optimizers.*\n");
        fullScript.append("import com.jmathanim.Cameras.*\n");
        fullScript.append("import com.jmathanim.Constructible.*\n");
        fullScript.append("import com.jmathanim.Styling.*\n");
        fullScript.append("import com.jmathanim.Utils.*\n");
        fullScript.append("import com.jmathanim.Utils.Layouts.*\n");
        fullScript.append(userCode);
        return fullScript.toString();

    }



    @Override
    public void setupSketch() {

        boolean hasInit = false;
        for (Method method : script.getClass().getMethods()) {
            if (method.getName().equals("init") && method.getParameterCount() == 0) {
                hasInit = true;
                break;
            }
        }
        if (hasInit) {
            script.invokeMethod("init", null);
        } else {//no init

            logger.setLevel(Level.DEBUG);
            logger.info("No init() method, switching to default configuration (preview, dark style)");
            config.parseFile("#preview.xml");
            config.parseFile("#dark.xml");
            config.setLimitFPS(true);
//            config.setDefaultLambda(t -> t);
        }
    }

        @Override
        public void runSketch () throws Exception {
            for (String a : args) {
                System.out.println(a);
            }
           script.run();

        }

    public static void main(String[] args) {
//        JMathAnimScene tr = new AnimacionesFabrica();
        if (args.length==0) {
            //print fancy help menu!
            System.out.println("Usage: JMathAnim <groovy script files>");
            System.exit(0);
        }
        JMathAnimScene tr = new GroovyExecutor(args);
        tr.execute();
    }

    }


