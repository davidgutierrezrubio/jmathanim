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
package com.jmathanim.MathObjects.Text;

import com.jmathanim.Cameras.Camera3D;
import com.jmathanim.Enum.AnchorType;
import com.jmathanim.MathObjects.*;
import com.jmathanim.MathObjects.Shapes.MultiShapeObject;
import com.jmathanim.Styling.JMColor;
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.Styling.PaintStyle;
import com.jmathanim.Utils.*;
import com.jmathanim.jmathanim.JMathAnimConfig;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.jmathanim.LogUtils;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.DOMTreeManager;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.scilab.forge.jlatexmath.ParseException;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class AbstractLatexMathObject<T extends AbstractLatexMathObject<T>>
        extends AbstractMultiShapeObject<T, LatexShape> {

    // Default scale for latex objects (relative to screen height)
    // This factor represents % of height relative to the screen that a "X"
    // character has
    public static final double DEFAULT_SCALE_FACTOR = .05;
    public final HashMap<Integer, Scalar> variables;
    protected final AffineJTransform modelMatrix;
    protected AnchorType anchor;
    protected LatexParser latexParser;
    protected String text;
    protected File latexFile;
    protected String baseFileName;
    protected File outputDir;
    protected String origText;
    CompileMode mode = CompileMode.JLaTexMath;
    DecimalFormat df;
    private Point anchor3DA;
    private Point anchor3DC;
    private Point anchor3DD;

    protected AbstractLatexMathObject(AnchorType anchor) {
        super(LatexShape.class);
        this.anchor = anchor;
        modelMatrix = new AffineJTransform();
        this.latexParser = new LatexParser(this);
        variables = new HashMap<>();
        df = new DecimalFormat("0.00");
        df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.UK));
        for (int n = 0; n < 9; n++) {
            variables.put(n, Scalar.make(0));
        }
    }

    public void setLatexStyle(LatexStyle latexStyle) {
        this.getMp().setLatexStyle(latexStyle);
        if (latexStyle != null) {
            latexStyle.apply(this);
        }
    }

    public void setLatexStyle(String latexStyleName) {
        HashMapUpper<String, LatexStyle> latexStyles = JMathAnimConfig.getConfig().getLatexStyles();
        if (latexStyles.containsKey(latexStyleName.toUpperCase())) {
            setLatexStyle(latexStyles.get(latexStyleName));
        }
    }

    @Override
    public T applyAffineTransform(AffineJTransform affineJTransform) {
        super.applyAffineTransform(affineJTransform);
        AffineJTransform compose = modelMatrix.compose(affineJTransform);
        modelMatrix.copyFrom(compose);
        return (T) this;
    }

    protected void changeInnerLaTeX(String text) {
        scene = JMathAnimConfig.getConfig().getScene();
        AffineJTransform modelMatrixBackup = modelMatrix.copy();
//        if (text.equals(this.text)) {
//            return;
//        } else {
//            this.text = text;
//        }
        this.text = text;
        for (LatexShape sh : this) {
            scene.remove(sh);
        }
        clearShapes();
        if (mode == CompileMode.CompileFile) {
            try {
                generateLaTeXDocument();
                File f = new File(compileLaTeXFile());
                clearShapes();
                addShapesFrom(SVGUtils.importSVG(f.toURI().toURL(), MODrawProperties.createFromStyle("latexdefault")));
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
            Element root = null;
            try {
                root = generateDOMTreeFromLaTeX(this.text);
                addShapesFrom(SVGUtils.importSVGFromDOM(root));
                latexParser.parse();
            } catch (ParseException e) {
              JMathAnimScene.logger.error("LaTeX compilation error. Perhaps there is an error in the code "+
                              LogUtils.GREEN+this.text+LogUtils.RESET+" ==> "+LogUtils.RED+e.getMessage()+LogUtils.RESET
                      );

                JMathAnimScene.logger.warn("An empty LaTeXMathObject will be created");
            }

        }

        int n = 0;
        mpMultiShape.getObjects().clear();
        for (LatexShape sh : this) {
            mpMultiShape.add(sh);
            sh.getMp().copyFrom(mpMultiShape);
            // Workaround: Draw color should be the same as fill color
//            sh.drawColor(sh.getMp().getFillColor());
            sh.objectLabel = String.valueOf(n);
            n++;
            if (isAddedToScene) {
                scene.add(sh);
            }
        }
        if (getMp().getLatexStyle() != null) {
            getMp().getLatexStyle().apply(this);
        }

        if (size() == 0) {
            return;
        }
        double hm = 2.5; //Default height view
        double sc = DEFAULT_SCALE_FACTOR * .4 * hm / 6.8 * 2.5;
        if ((mode == CompileMode.JLaTexMath) || (mode == CompileMode.RawJLaTexMath)) {
            sc *= 0.24906237699889464;
        }
        this.scale(sc, sc, 1);
        Vec v = Vec.to(0, 0);
        switch (anchor) {
            case CENTER:
                v = Anchor.getAnchorPoint(this, anchor).mult(-1);
                break;
            case UPPER:
                v = Anchor.getAnchorPoint(this, anchor).mult(-1);
                break;
            case LOWER:
                v = Anchor.getAnchorPoint(this, anchor).mult(-1);
                break;
            case LEFT:
                v = Anchor.getAnchorPoint(this.get(0), anchor).mult(-1);
                break;
            case LEFT_AND_ALIGNED_UPPER:
                v = Anchor.getAnchorPoint(this.get(0), anchor).mult(-1);
                break;
            case LEFT_AND_ALIGNED_LOWER:
                v = Anchor.getAnchorPoint(this.get(0), anchor).mult(-1);
                break;
            case LOWER_AND_ALIGNED_LEFT:
                v = Anchor.getAnchorPoint(this.get(0), anchor).mult(-1);
                break;
            case UPPER_AND_ALIGNED_LEFT:
                v = Anchor.getAnchorPoint(this.get(0), anchor).mult(-1);
                break;
            case RIGHT:
                v = Anchor.getAnchorPoint(this.get(-1), anchor).mult(-1);
                break;
            case RIGHT_AND_ALIGNED_UPPER:
                v = Anchor.getAnchorPoint(this.get(-1), anchor).mult(-1);
                break;
            case RIGHT_AND_ALIGNED_LOWER:
                v = Anchor.getAnchorPoint(this.get(-1), anchor).mult(-1);
                break;
            case LOWER_AND_ALIGNED_RIGHT:
                v = Anchor.getAnchorPoint(this.get(-1), anchor).mult(-1);
                break;
            case UPPER_AND_ALIGNED_RIGHT:
                v = Anchor.getAnchorPoint(this.get(-1), anchor).mult(-1);
                break;

        }
        //this.stackTo(anchor,Point.origin(), Type.CENTER,0);

        shift(v);

        modelMatrix.copyFrom(modelMatrixBackup);
        for (LatexShape sh : this) {
//            sh.getMp().copyFrom(mpMultiShape);
            sh.applyAffineTransform(modelMatrix);
        }
    }

    private void addShapesFrom(MultiShapeObject latexdefault) {
        for (Shape sh : latexdefault) {
            add(shapeToLatexShape(sh));
        }
    }

    public LatexShape shapeToLatexShape(Shape sh) {
        LatexShape resul = new LatexShape();
        resul.getPath().getJmPathPoints().addAll(sh.getPath().getJmPathPoints());
        resul.getMp().copyFrom(sh.getMp());
        return resul;
    }

    public LatexParser getLatexParser() {
        return latexParser;
    }

    @Override
    protected Rect computeBoundingBox() {
        Rect resul = null;
        for (LatexShape jmp : this) {
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

    private Element generateDOMTreeFromLaTeX(String text) throws ParseException{
        Writer out = null;
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        String latexText;
        if (mode == CompileMode.JLaTexMath) {
            latexText = "\\mbox{" + text + "}";
        } else {
            latexText = text;
        }
        //Needs to use 2 repeated parsers as the first one gets corrupted after createbox method
        //Will throw ParseException if LaTeX code has errors
        TeXFormula formula = null;
        TeXFormula formula2 = null;
            formula = new TeXFormula(latexText);
            formula2 = new TeXFormula(latexText);
//        TeXIcon icon = formula2.createTeXIcon(TeXConstants.ALIGN_LEFT, 40);
        latexParser.setJLatexFormulaParser(formula, formula2);
        latexParser.parse();
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);
        // Create an instance of the SVG Generator.
        SVGGeneratorContext ctx = SVGGeneratorContext.createDefault(document);
        SVGGraphics2D svgGenerator = new SVGGraphics2D(ctx, true);
        ctx.setEmbeddedFontsOn(true);
        latexParser.icon.paintIcon(null, svgGenerator, 0, 0);
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
            Logger.getLogger(LatexMathObject.class.getName()).log(Level.SEVERE, null, ex);
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


    /**
     * Static constructor
     *
     * @param text        LaTex text to compile
     * @param compileMode How to generate the shapes from LaTeX string. A value from the enum CompileMode.
     * @param anchor      Anchor to align. Default is CENTER. If LEFT, text will be anchored in its left margin to the
     *                    reference point
     * @return The LaTexMathObject
     */


    /**
     * Changes the current LaTeX expression, updating the whole object as needed.The JMNumber for example, uses this.The
     * new formula generated will be center-aligned with the replaced one. In case the old formula was empty (no shapes)
     * it will be centered on the screen.
     *
     * @param text The new LaTeX string
     * @return This object
     */
    public T setLaTeX(String text) {
        origText = text;
        text = replaceInnerReferencesInText(text);
        changeInnerLaTeX(text);
        return (T) this;
    }

    protected String replaceInnerReferencesInText(String text) {
        for (Integer index : variables.keySet()) {
            text = text.replace("{#" + index + "}", df.format(variables.get(index).getValue()));
        }
        return text;
    }

    @Override
    public void copyStateFrom(Stateable obj) {
        if (!(obj instanceof AbstractLatexMathObject)) return;
        AbstractLatexMathObject<?> laTeXMathObject = (AbstractLatexMathObject<?>) obj;
        super.copyStateFrom(obj);

        this.text = laTeXMathObject.getText();
//            getMp().copyFrom(obj.getMp());
//            clearShapes();
//            for (Shape sh : obj) {
//                final Shape copyShape = sh.copy();
//                add(copyShape);
//            }
        this.absoluteSize = laTeXMathObject.absoluteSize;
        this.modelMatrix.copyFrom(laTeXMathObject.modelMatrix);
        this.getMp().setLatexStyle(laTeXMathObject.getMp().getLatexStyle());//TODO: Create a specialized subclass por Latex objects
    }

    public String getText() {
        return text;
    }


    /**
     * Changes both draw and fill colors to the given glyphs
     *
     * @param str     A string representing a color. May be a JavaFX color name like CYAN or a hexadecimal number like
     *                #F3A0CD
     * @param indices Indices of glyps to change colors. 0 is the first glyph
     * @return This object
     */
    public T setColorsToIndices(String str, int... indices) {
        return (T) setColorsToIndices(JMColor.parse(str), indices);
    }

    /**
     * Changes both draw and fill colors to the given glyphs
     *
     * @param col     The JMColor
     * @param indices Indices of glyps to change colors. 0 is the first glyph
     * @return This object
     */
    public T setColorsToIndices(PaintStyle col, int... indices) {
        for (int i : indices) {
            if ((i >= 0) && (i < size())) {
                this.get(i).drawColor(col);
                this.get(i).fillColor(col);
            } else {
                JMathAnimScene.logger.warn("Index " + i + " out of bounds when applying setColor to LaTeX");
            }
        }
        return (T) this;
    }


    @Override
    protected LatexShape createEmptyShapeAt(int index) {
        LatexShape laTeXShape = new LatexShape();
        if (index < size()) {
            shapes.set(index, laTeXShape);
        } else {
            shapes.add(index, laTeXShape);
        }
        mpMultiShape.add(laTeXShape);
        return laTeXShape;
    }


    /**
     * Returns a MultiShapeObject with all shapes with match given token LaTex code must be compiled with the JLaTeXMath
     * option to use this method.
     *
     * @param token Token to match
     * @return A MultiShapeObject with all matching shapes
     */
    public T getShapesWith(LatexToken token) {
        T resul = makeNewEmptyInstance();
        if (latexParser == null) {
            return resul;
        }
        for (int i = 0; i < latexParser.getTokensList().size(); i++) {
            LatexToken shapeToken = latexParser.get(i);
            if (token.match(shapeToken)) {
                resul.add(get(i));
            }
        }
        return resul;
    }

    public AffineJTransform getModelMatrix() {
        return modelMatrix;
    }

    private void alignTo3DView() {
        if (scene.getCamera() instanceof Camera3D) {
            Camera3D cam = (Camera3D) scene.getCamera();
            Point anchor3DCdest = anchor3DA.copy().shift(cam.up);
            Point anchor3DDdest = anchor3DA.copy().shift(cam.look.to(cam.eye));
            AffineJTransform tr = AffineJTransform.createDirect3DIsomorphic(
                    anchor3DA, anchor3DD, anchor3DC,
                    anchor3DA.copy(), anchor3DDdest, anchor3DCdest,
                    1);
            tr.applyTransform(this);
//            tr.applyTransform(anchor3DA);
            tr.applyTransform(anchor3DC);
            tr.applyTransform(anchor3DD);
        }
    }

    public DecimalFormat getDecimalFormat() {
        return df;
    }

    public Scalar getArg(int n) {
        Scalar resul = variables.get(n);
        if (resul == null) {
            variables.put(n, Scalar.make(0));
        }
        return variables.get(n);
    }

    /**
     * Sets the decimal format for the arguments.
     *
     * @param format A string representing a format for the DecimalFormat class.
     */
    public void setArgumentsFormat(String format) {
        df = new DecimalFormat(format);
    }

    /**
     * Determines how LaTeX shapes will be created
     */
    public enum CompileMode {
        /**
         * Uses JLaTexMath library, enclosing text in a mbox. JLaTexMath compiles by default in math mode so this is
         * necessary in order to generate normal text by default. This is the default mode used when creating a
         * LaTeXMathObject without arguments.
         */
        JLaTexMath,
        /**
         * Uses JLaTexMath library with the unaltered text. Default to math mode. by default in math mode.
         */
        RawJLaTexMath,
        /**
         * Invokes an external LaTeX system to compile file and dvisvg to generate a svg object to import it. This is
         * used for compatibiliy reasons and in the rare cases JLaTeXMath cannot compile the LaTeX string.
         */
        CompileFile
    }
}
