/*
 * Copyright (C) 2022 David Gutierrez Rubio davidgutierrezrubio@gmail.com
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
package com.jmathanim.mathobjects.Text;

import com.jmathanim.Styling.JMColor;
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.Styling.PaintStyle;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.EmptyRect;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.SVGUtils;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
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
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class AbstractLaTeXMathObject extends SVGMathObject {

    protected Anchor.Type anchor;

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

    // Default scale for latex objects (relative to screen height)
    // This factor represents % of height relative to the screen that a "X"
    // character has
    public static final double DEFAULT_SCALE_FACTOR = .05;
    protected final AffineJTransform modelMatrix;
    protected String text;
    CompileMode mode = CompileMode.JLaTexMath;
    protected File latexFile;
    protected String baseFileName;
    protected File outputDir;

    protected AbstractLaTeXMathObject(Anchor.Type anchor) {
        super();
        this.anchor = anchor;
        modelMatrix = new AffineJTransform();
    }

    @Override
    public AbstractLaTeXMathObject applyAffineTransform(AffineJTransform transform) {
        super.applyAffineTransform(transform);
        AffineJTransform compose = modelMatrix.compose(transform);
        modelMatrix.copyFrom(compose);
        return this;
    }
    
   

    protected void changeInnerLaTeX(String text) {
       
        AffineJTransform modelMatrixBackup = modelMatrix.copy();
//        if (text.equals(this.text)) {
//            return;
//        } else {
//            this.text = text;
//        }
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
                svgu.importSVG(f.toURI().toURL(), this, MODrawProperties.createFromStyle("latexdefault"));
            } catch (IOException ex) {
                if (ex.getLocalizedMessage().toUpperCase().startsWith("CANNOT RUN PROGRAM")) {
                    JMathAnimScene.logger.error("Oops, it seems JMathAnim cannot find your LaTeX executable." + " Make sure you have LaTeX installed on your system and the latex program" + " is accesible from your path");
                } else {
                    JMathAnimScene.logger.error("An unknown I/O error. Maybe you don't have permissions" + "to write files on your working directory or there is not enough space on disk.");
                }
                JMathAnimScene.logger.warn("An empty LaTeXMathObject will be created");
            } catch (Exception ex) {
                JMathAnimScene.logger.error("An unknown  error happened trying to create a LaTeXMathObject");
                JMathAnimScene.logger.warn("An empty LaTeXMathObject will be created");
            }
        } else {
            Element root = generateDOMTreeFromLaTeX(this.text);
            SVGUtils svgUtils = new SVGUtils(scene);
//            try {
//                svgUtils.writeElementToXMLFile(root, "PRUEBA.xml");
//            } catch (Exception ex) {
//                Logger.getLogger(AbstractLaTeXMathObject.class.getName()).log(Level.SEVERE, null, ex);
//            }
            svgUtils.importSVGFromDOM(root, this);
        }
        int n = 0;
        for (Shape sh : shapes) {
            // Workaround: Draw color should be the same as fill color
            sh.drawColor(sh.getMp().getFillColor());
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
        double hm = 2.5; //Default height view
        double sc = DEFAULT_SCALE_FACTOR * .4 * hm / 6.8 * 2.5;
        if ((mode == CompileMode.JLaTexMath) || (mode == CompileMode.RawJLaTexMath)) {
            sc *= 0.24906237699889464;
        }
        this.scale(sc, sc, 1);
        this.stackTo(anchor,Point.origin(), Anchor.Type.CENTER,0);
        modelMatrix.copyFrom(modelMatrixBackup);
        for (Shape sh : shapes) {
//            sh.getMp().copyFrom(mpMultiShape);
            sh.applyAffineTransform(modelMatrix);
        }
    }
  @Override
    protected Rect computeBoundingBox() {
        Rect resul = null;
        for (Shape jmp : shapes) {
            if (!jmp.isEmpty()) {
                resul = Rect.union(resul, jmp.getBoundingBox());
            }
        }

        if (resul == null) {
            return new EmptyRect();
        } else {
            return resul;
        }
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
        String beginDocument = "\\documentclass[preview]{standalone}\n" + "\\usepackage{xcolor}\n" + "\\usepackage{amssymb}" + "\\usepackage{amsmath}" + "\\begin{document}\n";
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
        if (!svgFile.exists()) {
            // If file is already created, don't do it again
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

    private void runExternalCommand(String command) throws IOException, InterruptedException {
        String line;
        Process p = Runtime.getRuntime().exec(command, null, outputDir);
        BufferedReader bre;
        try (final BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
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
    public void copyStateFrom(MathObject obj) {
         super.copyStateFrom(obj);
        if (obj instanceof AbstractLaTeXMathObject) {
            AbstractLaTeXMathObject copy = (AbstractLaTeXMathObject) obj;
            getMp().copyFrom(copy.getMp());
            shapes.clear();
            for (Shape sh : copy.shapes) {
                final Shape copyShape = sh.copy();
                add(copyShape);
            }
            this.absoluteSize = copy.absoluteSize;
            this.modelMatrix.copyFrom(copy.modelMatrix);
        }
    }

    @Override
    public int size() {
        return shapes.size();
    }

    public String getText() {
        return text;
    }

    /**
     * Changes both draw and fill colors
     *
     * @param str A string representing a color. May be a JavaFX color name like
     * CYAN or a hexadecimal number like #F3A0CD
     * @return This object
     */
    public AbstractLaTeXMathObject setColor(String str) {
        return setColor(JMColor.parse(str));
    }

    /**
     * Changes both draw and fill colors
     *
     * @param col The JMColor
     * @return This object
     */
    public AbstractLaTeXMathObject setColor(JMColor col) {
        this.drawColor(col);
        this.fillColor(col);
        return this;
    }

    /**
     * Changes both draw and fill colors to the given glyphs
     *
     * @param str A string representing a color. May be a JavaFX color name like
     * CYAN or a hexadecimal number like #F3A0CD
     * @param indices Indices of glyps to change colors. 0 is the first glyph
     * @return This object
     */
    public AbstractLaTeXMathObject setColor(String str, int... indices) {
        return setColor(JMColor.parse(str), indices);
    }

    /**
     * Changes both draw and fill colors to the given glyphs
     *
     * @param col The JMColor
     * @param indices Indices of glyps to change colors. 0 is the first glyph
     * @return This object
     */
    public AbstractLaTeXMathObject setColor(PaintStyle col, int... indices) {
        for (int i : indices) {
            this.get(i).drawColor(col);
            this.get(i).fillColor(col);
        }
        return this;
    }

//    public Anchor.Type getAnchor() {
//        return anchor;
//    }
//
//    public <T extends AbstractLaTeXMathObject> T setAnchor(Anchor.Type anchor) {
//        this.anchor = anchor;
//        return (T) this;
//    }
}
