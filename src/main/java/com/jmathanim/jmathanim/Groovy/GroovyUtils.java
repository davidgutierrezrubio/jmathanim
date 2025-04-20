package com.jmathanim.jmathanim.Groovy;

import com.jmathanim.jmathanim.LogUtils;

import java.util.stream.Collectors;

import static com.jmathanim.jmathanim.JMathAnimScene.logger;
import static com.jmathanim.jmathanim.LogUtils.RESET;

public class GroovyUtils {

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
                logger.error(e.getMessage());
                break;
            }
        }
    }
    private static int countLines(String str){
        String[] lines = str.split("\r\n|\r|\n");
        return  lines.length;
    }

    public static String processSourceCode(String userCode) {

       String sourceCode= addImports(userCode);
        return removeFieldLines(sourceCode);


    }

    private static  String addImports(String userCode) {
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
    public static String removeFieldLines(String script) {
        return script.lines()
                .filter(line -> !line.contains("@Field") && !line.contains("import groovy.transform.Field"))
                .collect(Collectors.joining("\n"));
    }
}
