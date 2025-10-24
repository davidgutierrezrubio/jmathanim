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
}
