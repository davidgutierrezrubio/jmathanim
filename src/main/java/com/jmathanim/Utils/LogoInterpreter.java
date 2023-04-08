/*
 * Copyright (C) 2023 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jmathanim.Utils;

import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.Shape;
import java.util.StringTokenizer;

/**
 * This class interprets a string with logo commands and translate it into a
 * Shape object. Current commands supported are FD or FORWARD, BK or BACKWARD,
 * RT o RIGHT, LT or LEFT, PU o PENUP, PD or PENDOWN, and REPEAT. An additional
 * CLO command is added to close generated path
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class LogoInterpreter {

    private double x;
    private double y;
    private boolean visibleFlag;
    private double currentAngle;//Angle in radians
    private JMPath path;

    /**
     * Creates a new instance of LogoInterpreter
     */
    public LogoInterpreter() {
        reset();
    }

    private void processRepeatCmd(StringTokenizer st) {
        String commandsToRepeat = "";
        int numberOfRepetitions = Integer.parseInt(st.nextToken());
        String token = st.nextToken();
        if (!"[".equals(token)) {
            //Error here!
        }
        while (!"]".equals(token)) {
            token = st.nextToken();
            commandsToRepeat += " " + token;
        }
        for (int i = 0; i < numberOfRepetitions; i++) {
            processString(commandsToRepeat);
        }

    }

    /**
     * Resets the drawed path, to generate another from scratch
     */
    public final void reset() {
        x = 0;
        y = 0;
        currentAngle = .5 * Math.PI;//Initial angle
        visibleFlag = true;
        path = new JMPath();
        JMPathPoint p = JMPathPoint.lineTo(x, y);
        p.isThisSegmentVisible = false;
        path.addJMPoint(p);
    }

    /**
     * Process the LOGO commands in the argument and returns a Shape with the
     * resu
     *
     * @param commands a String wiht LOGO commands, commands are
     * case-insensitive
     * @return The generated Shape object
     */
    public Shape toShape(String commands) {
        String cmds = distilleCommandsString(commands);
        processString(cmds);
        return new Shape(path);
    }

    private void processString(String commands) {
        StringTokenizer st = new StringTokenizer(commands, " ", false);
        while (st.hasMoreTokens()) {
            String command = st.nextToken();
            processCommand(command, st);
        }
    }

    private void processCommand(String command, StringTokenizer st) {
        double amount;
        switch (command) {
            case "FD" -> {
                amount = Double.parseDouble(st.nextToken());
                forwardCmd(amount);
            }

            case "BK" -> {
                amount = Double.parseDouble(st.nextToken());
                forwardCmd(-amount);
            }

            case "RT" -> {
                amount = parseAngle(st);
                currentAngle += amount;
            }

            case "LT" -> {
                amount = parseAngle(st);
                currentAngle -= amount;
            }

            case "PU" ->
                visibleFlag = false;

            case "PD" ->
                visibleFlag = true;

            case "REPEAT" ->
                processRepeatCmd(st);

            case "CLO" ->
                path.closePath();
            default ->
                JMathAnimScene.logger.error("Unrecognized LOGO command " + command);

        }
    }

    private String distilleCommandsString(String commands) {
        String result = commands.toUpperCase();

        result = result.replaceAll("\\s*FORWARD\\s*", " FD ");
        result = result.replaceAll("\\s*BACK\\s*", " BK ");
        result = result.replaceAll("\\s*RIGHT\\s*", " RT ");
        result = result.replaceAll("\\s*LEFT\\s*", " LT ");
        result = result.replaceAll("\\s*PENUP\\s*", " PU ");
        result = result.replaceAll("\\s*PENDOWN\\s*", " PD ");
        result = result.replaceAll("\\s*REPEAT\\s*", " REPEAT ");
        result = result.replaceAll("\\s*\\[\\s*", " [ ");
        result = result.replaceAll("\\s*\\]\\s*", " ] ");
        result = result.replaceAll("\\s*CLOSE\\s*", " CLO ");//Command to close path

        result = result.replaceAll("\\s+", " ");//Replaces multiple spaces by single ones
        return result;
    }

    /**
     * Parse angle in degrees from tokenizer and convert it to radians
     *
     * @param st
     * @return Angle in radians
     */
    private double parseAngle(StringTokenizer st) {
        double amount = Double.parseDouble(st.nextToken());
        return amount * Math.PI / 180;

    }

    /**
     * FORWARD (and BACKWARD) commands
     *
     * @param amount
     */
    private void forwardCmd(double amount) {
        x += amount * Math.cos(currentAngle);
        y += amount * Math.sin(currentAngle);
        JMPathPoint p = JMPathPoint.lineTo(x, y);
        p.isThisSegmentVisible = visibleFlag;
        path.addJMPoint(p);
    }
}
