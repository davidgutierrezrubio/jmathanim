/*
 * Copyright (C) 2020 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package com.jmathanim.mathobjects;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.jmathanim.JMathAnimScene;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class LaTeXMathObject extends SVGMathObject {

    private final String text;
    private File latexFile;
    private String baseFileName;
    private File outputDir;
    //Default scale for latex objects (relative to screen height)
    //This factor represents % of height relative to the screen that a "X" character has
    public static final double DEFAULT_SCALE_FACTOR = .05;

    
    /**
     * Static constructor
     * @param text LaTex text to compile
     * @return  The LaTexMathObject
     */
    public static LaTeXMathObject make(String text)
    {
        return new LaTeXMathObject(text);
    }
    /**
     * Creates a new LaTeX generated text
     *
     * @param text The text to be compiled. Backslashes in Java strings should
     * be writen with "\\"
     */
    public LaTeXMathObject(String text) {
        super();
        this.setObjectType(MathObjectType.LATEX_MULTISHAPE);
        mp.loadFromStyle("latexdefault");
        this.text = text;

        try {
            generateLaTeXDocument();
            File f = new File(compileLaTeXFile());
            importSVG(f);
        } catch (IOException ex) {
            Logger.getLogger(LaTeXMathObject.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(LaTeXMathObject.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (shapes.size() > 0)//Move UL to (0,0) by default
        {
//            Rect r = getBoundingBox();
//            this.shift(-r.xmin, -r.ymax);
//            r = getBoundingBox();
            putAt(new Point(0, 0), Anchor.UL);
        }
        int n = 0;
        for (Shape sh : shapes) {//label them
            sh.setObjectType(MathObjectType.LATEX_SHAPE);
            sh.label = String.valueOf(n);
            n++;
            sh.mp.absoluteThickness=true;
            sh.thickness(1);
        }

        //Scale
        //An "X" character in LaTeX has 6.8 (svg units) pixels height.
        //This object should be scaled by default to extend over
        //DEFAULT_SCALE_FACTOR% of the screen
        Camera cam = JMathAnimConfig.getConfig().getFixedCamera();
//        double hm = cam.getMathView().getHeight();
        double hm = cam.screenToMath(cam.screenHeight);
        double sc = DEFAULT_SCALE_FACTOR * .4 * hm / 6.8*2.5;
        this.scale(getBoundingBox().getUL(), sc, sc, 1);

    }

    /**
     * Prepare LaTeX file and compile it
     */
    private void generateLaTeXDocument() throws IOException {
        //TODO: Add necessary packages here (UTF8?)
        //How to avoid having to write 2 backslashs??
        String beginDocument = "\\documentclass[preview]{standalone}\n"
                + "\\usepackage{xcolor}\n"
                + "\\begin{document}\n";

        String endDocument = "\\end{document}";

        String fullDocument = beginDocument + this.text + "\n" + endDocument;
        String hash = getMd5(fullDocument);
        hash = hash.substring(hash.length() - 8);
        outputDir = new File("tex");
        baseFileName = outputDir.getCanonicalPath() + "\\" + hash;
        latexFile = new File(baseFileName + ".tex");
        outputDir.mkdirs();
        FileWriter fw;
        PrintWriter pw;
        try {
            fw = new FileWriter(latexFile);
            pw = new PrintWriter(fw);
            pw.print(fullDocument);
            pw.close();
        } catch (IOException ex) {
            Logger.getLogger(LaTeXMathObject.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String getMd5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger bi = new BigInteger(1, messageDigest);
            String hash = bi.toString(16);
            while (hash.length() < 32) {
                hash = "0" + hash;
            }
            return hash;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String compileLaTeXFile() throws IOException, InterruptedException {
        String svgFilename = baseFileName + ".svg";
        File svgFile = new File(svgFilename);
        if (!svgFile.exists()) {//If file is already created, don't do it again
            File dviFile = new File(baseFileName + ".dvi");
            String od = outputDir.getCanonicalPath();
            runExternalCommand("latex -output-directory=" + od + " " + this.latexFile.getCanonicalPath());
            JMathAnimScene.logger.info("Done compiling {}", latexFile.getCanonicalPath());
            runExternalCommand("dvisvgm -n1 " + dviFile.getCanonicalPath());
            JMathAnimScene.logger.info("Done converting {}", dviFile.getCanonicalPath());
        }
        return svgFilename;
    }

    public void runExternalCommand(String command) throws IOException, InterruptedException {
        String line;
        String[] ar = {};
        Process p = Runtime.getRuntime().exec(command, null, outputDir);
        BufferedReader bre;
        try (BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((line = bri.readLine()) != null) {
                JMathAnimScene.logger.debug(line);
            }
        }
        while ((line = bre.readLine()) != null) {
             JMathAnimScene.logger.debug(line);
        }
        bre.close();
        p.waitFor();

    }

//    private void setColor(JMColor color) {
//        for (Shape p : shapes) {
//            p.mp.thickness = .0001;
//            p.drawColor(color);
//            p.mp.setFillAlpha(1);
//            p.fillColor(color); //LaTeX Objects should have by default same fill and draw color
//        }
//    }
}
