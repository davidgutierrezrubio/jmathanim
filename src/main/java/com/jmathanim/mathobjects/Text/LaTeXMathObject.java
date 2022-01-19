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
package com.jmathanim.mathobjects.Text;

import com.jmathanim.Styling.JMColor;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.SVGUtils;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MultiShapeObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.SVGMathObject;
import com.jmathanim.mathobjects.Shape;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.DOMTreeManager;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class LaTeXMathObject extends SVGMathObject {

    private String text;

    /**
     * Determines how LaTeX shapes will be created
     */
    public enum CompileMode {
        /**
         * Uses JLaTexMath library, enclosing text in a mbox. JLaTexMath
         * compiles by default in math mode so this is necessary in order to
         * generate normal text by default. This is the default mode used when
         * creating a LaTeXMathObject without arguments.
         */
        JLaTexMath,
        /**
         * Uses JLaTexMath library with the unaltered text. Default to math
         * mode. by default in math mode.
         */
        RawJLaTexMath,
        /**
         * Invokes an external LaTeX system to compile file and dvisvg to
         * generate a svg object to import it. This is used for compatibiliy
         * reasons and in the rare cases JLaTeXMath cannot compile the LaTeX
         * string.
         */
        CompileFile
    }
    CompileMode mode;
    private File latexFile;
    private String baseFileName;
    private File outputDir;
    // Default scale for latex objects (relative to screen height)
    // This factor represents % of height relative to the screen that a "X"
    // character has
    public static final double DEFAULT_SCALE_FACTOR = .05;

    /**
     * Static constructor
     *
     * @param text LaTex text to compile. By default this text is compiled using
     * the compile mode JLaTexMath.
     * @return The LaTexMathObject
     */
    public static LaTeXMathObject make(String text) {
        return make(text, CompileMode.JLaTexMath);
    }

    /**
     * Static constructor
     *
     * @param text LaTex text to compile
     * @param compileMode How to generate the shapes from LaTeX string. A value
     * from the enum CompileMode.
     * @return The LaTexMathObject
     */
    public static LaTeXMathObject make(String text, CompileMode compileMode) {
        LaTeXMathObject t = new LaTeXMathObject();
        t.mode = compileMode;

        if (!"".equals(text)) {
            t.setLaTeX(text);
        }

        if (t.shapes.size() > 0) {
            t.center();
        }
        return t;
    }

    /**
     * Creates a new LaTeX generated text
     */
    protected LaTeXMathObject() {
        super();
        getMp().loadFromStyle("latexdefault");
        getMp().setAbsoluteThickness(true);
        getMp().setFillColor(getMp().getDrawColor());
        getMp().setThickness(1d);
    }

    /**
     * Changes the current LaTeX expression, updating the whole object as
     * needed.The JMNumber for example, uses this.The new formula generated will
     * be center-aligned with the replaced one. In case the old formula was
     * empty (no shapes) it will be centered on the screen.
     *
     * @param <T> Calling subclass
     * @param text The new LaTeX string
     * @return This object
     */
    public <T extends LaTeXMathObject> T setLaTeX(String text) {
        if (text.equals(this.text)) {
            return (T) this;
        } else {
            this.text = text;
        }
        Point center;

        if (shapes.isEmpty()) {
            center = JMathAnimConfig.getConfig().getCamera().getMathView().getCenter();
        } else {
            center = this.getCenter();
        }
        this.text = text;
        for (Shape sh : shapes) {
            scene.remove(sh);
        }

        shapes.clear();

        if (mode == CompileMode.CompileFile) {
            try {
                generateLaTeXDocument();
                File f = new File(compileLaTeXFile());
                SVGUtils svgu = new SVGUtils(scene);
                svgu.importSVG(f.toURI().toURL(), this);
            } catch (IOException ex) {
                if (ex.getLocalizedMessage().toUpperCase().startsWith("CANNOT RUN PROGRAM")) {
                    JMathAnimScene.logger.error("Oops, it seems JMathAnim cannot find your LaTeX executable."
                            + " Make sure you have LaTeX installed on your system and the latex program"
                            + " is accesible from your path");
                } else {
                    JMathAnimScene.logger.error("An unknown I/O error. Maybe you don't have permissions"
                            + "to write files on your working directory or there is not enough space on disk.");
                }
                JMathAnimScene.logger.warn("An empty LaTeXMathObject will be created");
            } catch (Exception ex) {
                JMathAnimScene.logger.error("An unknown  error happened trying to create a LaTeXMathObject");
                JMathAnimScene.logger.warn("An empty LaTeXMathObject will be created");
            }
        } else {
            Element root = generateDOMTreeFromLaTeX(this.text);
            SVGUtils svgUtils = new SVGUtils(scene);
            svgUtils.importSVGFromDOM(root, this);
        }
        this.style("latexdefault");
        int n = 0;
        for (Shape sh : shapes) {// objectLabel them
//            sh.getMp().fillColorIsDrawColor = true;
            sh.objectLabel = String.valueOf(n);
            n++;

            if (isAddedToScene) {
                scene.add(sh);
            }
        }

//        //Apply style to all subshapes
//        for (Shape sh : shapes) {
//            sh.getMp().copyFrom(getMp());
//        }
//Scale
//An "X" character in LaTeX has 6.8 (svg units) pixels height.
//This object should be scale by default to extend over
//DEFAULT_SCALE_FACTOR% of the screen
//        Camera cam = JMathAnimConfig.getConfig().getFixedCamera();
//        double hm = cam.getMathView().getHeight();
//        double hm = cam.screenToMath(cam.screenHeight);
        double hm = 2.5;//Default height view
        double sc = DEFAULT_SCALE_FACTOR * .4 * hm / 6.8 * 2.5;
        if ((mode == CompileMode.JLaTexMath) || (mode == CompileMode.RawJLaTexMath)) {
            sc *= 0.24906237699889464;
        }
        this.scale(getBoundingBox().getUL(), sc, sc, 1);
        this.moveTo(center);
        return (T) this;
    }

    private Element generateDOMTreeFromLaTeX(String text) {
        Writer out = null;
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        String latexText;
        if (mode == CompileMode.JLaTexMath) {
            latexText = "\\mbox{" + text + "}";
        } else {
            latexText = text;
        }
        TeXFormula formula = new TeXFormula(latexText);

        TeXIcon icon = formula.createTeXIcon(TeXConstants.ALIGN_LEFT, 40);
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);
        // Create an instance of the SVG Generator.
        SVGGeneratorContext ctx = SVGGeneratorContext.createDefault(document);
        SVGGraphics2D svgGenerator = new SVGGraphics2D(ctx, true);
        ctx.setEmbeddedFontsOn(true);
        icon.paintIcon(null, svgGenerator, 0, 0);
        DOMTreeManager domTreeManager = svgGenerator.getDOMTreeManager();
        Element domFactory = svgGenerator.getRoot();
        return domFactory;
    }

    /**
     * Prepare LaTeX file and compile it
     */
    private void generateLaTeXDocument() throws IOException {
        // TODO: Add necessary packages here (UTF8?)
        // How to avoid having to write 2 backslashs??
        String beginDocument = "\\documentclass[preview]{standalone}\n" + "\\usepackage{xcolor}\n"
                + "\\usepackage{amssymb}"
                + "\\usepackage{amsmath}"
                + "\\begin{document}\n";

        String endDocument = "\\end{document}";

        String fullDocument = beginDocument + this.text + "\n" + endDocument;
        String hash = getMd5(fullDocument);
        hash = hash.substring(hash.length() - 8);
        outputDir = new File("tex");
        baseFileName = outputDir.getCanonicalPath() + File.separator + hash;
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

    private String getMd5(String input) {
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

    /**
     * Compile the LaTeX File to SVG
     *
     * @return The file name of the generated SVG
     * @throws IOException
     * @throws InterruptedException
     */
    private String compileLaTeXFile() throws IOException, InterruptedException {
        String svgFilename = baseFileName + ".svg";
        File svgFile = new File(svgFilename);
        if (!svgFile.exists()) {// If file is already created, don't do it again
            JMathAnimScene.logger.info("Compiling LaTeX string " + this.text);
            File dviFile = new File(baseFileName + ".dvi");
            String od = outputDir.getCanonicalPath();
            runExternalCommand("latex -output-directory=" + od + " " + this.latexFile.getCanonicalPath());
            JMathAnimScene.logger.debug("Done compiling {}", latexFile.getCanonicalPath());
            runExternalCommand("dvisvgm -n1 " + dviFile.getCanonicalPath());
            JMathAnimScene.logger.debug("Done converting {}", dviFile.getCanonicalPath());
        }
        return svgFilename;
    }

    public void runExternalCommand(String command) throws IOException, InterruptedException {
        String line;
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

    @Override
    public LaTeXMathObject copy() {
        LaTeXMathObject resul = new LaTeXMathObject();
        resul.getMp().copyFrom(getMp());
        for (Shape sh : shapes) {
            final Shape copy = sh.copy();
            resul.add(copy);
        }

        resul.absoluteSize = this.absoluteSize;
        return resul;
    }

    @Override
    public int size() {
        return shapes.size();
    }

    public String getText() {
        return text;
    }

    public LaTeXMathObject setColor(String str) {
        return setColor(JMColor.parse(str));
    }

    public LaTeXMathObject setColor(JMColor col) {
        this.drawColor(col);
        this.fillColor(col);
        return this;
    }

    public LaTeXMathObject setColor(String str, int... indices) {
        return setColor(JMColor.parse(str), indices);
    }

    public LaTeXMathObject setColor(JMColor col, int... indices) {
        for (int i : indices) {
            this.get(i).drawColor(col);
            this.get(i).fillColor(col);
        }
        return this;
    }

}
