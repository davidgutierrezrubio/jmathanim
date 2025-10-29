package com.jmathanim.jmathanim;

import com.jmathanim.Enum.LogLevel;

import java.io.PrintStream;
import java.lang.System.Logger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.jmathanim.jmathanim.LogUtils.RESET;

public class JMathAnimLogger {
    public static final Logger logger = System.getLogger(JMathAnimLogger.class.getName());
//    private LogLevel logLevel;
    private int numLogLevel;
    private static final PrintStream out = System.out;
    private static final DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");


    public JMathAnimLogger() {
//        logLevel = LogLevel.INFO;
        numLogLevel = 3;
    }

    public void setLevel(LogLevel logLevel) {
//        this.logLevel = logLevel;
        switch (logLevel) {
            case DEBUG:
                numLogLevel = 4;
                break;
            case INFO:
                numLogLevel = 3;
                break;
            case WARN:
                numLogLevel = 2;
                break;
            case ERROR:
                numLogLevel = 1;
                break;
            case OFF:
                numLogLevel = 0;
                break;
        }
    }
    public void setLevel(int logLevel) {
        if (logLevel<0) logLevel=0;
        if (logLevel>4) logLevel=4;
        numLogLevel=logLevel;
    }


    public void log(LogLevel level, String msg) {
        String timestamp = LocalDateTime.now().format(timeFmt);
        String output = String.format("%s %-5s: %s", timestamp, levelToString(level), msg);
        out.println(output);
    }

    public void debug(String logMessage) {
        if (numLogLevel < 4) return;
        log(LogLevel.DEBUG, logMessage);
    }


    public void info(String logMessage) {
        if (numLogLevel < 3) return;
       log(LogLevel.INFO, logMessage);
    }

    public void warn(String logMessage) {
        if (numLogLevel < 2) return;
        log(LogLevel.WARN, logMessage);
    }

    public void error(String logMessage) {
        if (numLogLevel < 1) return;
        log(LogLevel.ERROR, logMessage);
    }


    private String levelToString(LogLevel level) {
        switch (level) {
            case DEBUG:
                return LogUtils.DEBUG+"DEBUG"+RESET;
            case INFO:
                return LogUtils.INFO+"INFO"+RESET;
            case WARN:
                return LogUtils.WARN+"WARN"+RESET;
            case ERROR:
                return LogUtils.ERROR+"ERROR"+RESET;
            case OFF:
                return "OFF";
        }
        return "UNKNOWN";
    }
}
