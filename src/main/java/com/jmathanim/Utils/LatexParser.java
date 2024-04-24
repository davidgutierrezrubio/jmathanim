/*
 * Copyright (C) 2024 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
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

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
import com.jmathanim.Styling.JMColor;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MultiShapeObject;
import com.jmathanim.mathobjects.Shape;
import com.jmathanim.mathobjects.Text.AbstractLaTeXMathObject;
import com.jmathanim.mathobjects.Text.LaTeXMathObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static javafx.scene.paint.Color.color;
import org.scilab.forge.jlatexmath.AccentedAtom;
import org.scilab.forge.jlatexmath.ArrayOfAtoms;
import org.scilab.forge.jlatexmath.Atom;
import org.scilab.forge.jlatexmath.BigDelimiterAtom;
import org.scilab.forge.jlatexmath.BigOperatorAtom;
import org.scilab.forge.jlatexmath.BoldAtom;
import org.scilab.forge.jlatexmath.Box;
import org.scilab.forge.jlatexmath.CharAtom;
import org.scilab.forge.jlatexmath.CharBox;
import org.scilab.forge.jlatexmath.CharFont;
import org.scilab.forge.jlatexmath.DefaultTeXFont;
import org.scilab.forge.jlatexmath.EmptyAtom;
import org.scilab.forge.jlatexmath.FencedAtom;
import org.scilab.forge.jlatexmath.FractionAtom;
import org.scilab.forge.jlatexmath.HorizontalRule;
import org.scilab.forge.jlatexmath.MathAtom;
import org.scilab.forge.jlatexmath.MatrixAtom;
import org.scilab.forge.jlatexmath.MiddleAtom;
import org.scilab.forge.jlatexmath.NthRoot;
import org.scilab.forge.jlatexmath.OverUnderBox;
import org.scilab.forge.jlatexmath.OverUnderDelimiter;
import org.scilab.forge.jlatexmath.OverlinedAtom;
import org.scilab.forge.jlatexmath.RomanAtom;
import org.scilab.forge.jlatexmath.RowAtom;
import org.scilab.forge.jlatexmath.ScaleBox;
import org.scilab.forge.jlatexmath.ScriptsAtom;
import org.scilab.forge.jlatexmath.SpaceAtom;
import org.scilab.forge.jlatexmath.StyleAtom;
import org.scilab.forge.jlatexmath.SymbolAtom;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXEnvironment;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;
import org.scilab.forge.jlatexmath.TeXParser;
import org.scilab.forge.jlatexmath.TypedAtom;
import org.scilab.forge.jlatexmath.UnderOverArrowAtom;

/**
 * Handles the parsing of elements of a LaTeX objects and assign to each
 * generated Shape a LatexToken describing the glyph. It relies in the parsing
 * capabilities of the JLaTeXMath library.
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class LatexParser {

    public final ArrayList<LatexToken> assignedTokens;
    private int boxCounter;
    private TeXFormula formula2;
    public TeXIcon icon;
    private final AbstractLaTeXMathObject latex;
    private TeXFormula formula;

    private boolean takesStyleFromNextFlag;

    public List<MiddleAtom> list;
    private LatexToken previousToken;
    private Atom rootCopy;

    public final ArrayList<LatexToken> tokens;
    public final ArrayList<Box> boxes;

    public enum Modifier {
        NORMAL, //Normal math
        TYPED,//A function name
        RAW_TEXT;//Raw, normal text
    }
    private Integer secondaryType;
    public Modifier modifier;

    public LatexParser(AbstractLaTeXMathObject latex) {
        this.latex = latex;
        this.list = new ArrayList<>();
        this.boxes = new ArrayList<>();
        this.tokens = new ArrayList<>();
        this.assignedTokens = new ArrayList<>();
        this.modifier = Modifier.NORMAL;
        this.secondaryType = LatexToken.SEC_NORMAL;
        takesStyleFromNextFlag = false;
    }

    public TeXFormula getJLatexFormulaParser() {
        return formula;
    }

    public void setJLatexFormulaParser(TeXFormula formula, TeXFormula formula2) {
        this.formula = formula;
        this.formula2 = formula2;
    }

    public void parse() {
        if (this.formula == null) {
            JMathAnimScene.logger.warn("Parser not initializated, cannot parse LaTeX tokens");
            return;
        }

        this.tokens.clear();
        boxes.clear();
        this.assignedTokens.clear();

        Atom root = this.formula.root;
        rootCopy = root.clone();
        try {
            parseAtom(root);
            distilleTokens();
        } catch (Exception ex2) {
            JMathAnimScene.logger.warn("Error parsing LaTeX tokens of " + this.latex.getText() + ":" + ex2.getLocalizedMessage() + " ");
            ex2.printStackTrace();

        }

        try {
            icon = formula2.createTeXIcon(TeXConstants.ALIGN_LEFT, 40);
            DefaultTeXFont font = new DefaultTeXFont(40);
            TeXEnvironment te = new TeXEnvironment(0, font);

            Class[] cArg = new Class[1];
            cArg[0] = TeXEnvironment.class;
            Method metodo = TeXFormula.class.getDeclaredMethod("createBox", cArg);

            metodo.setAccessible(true);
            Box bo = (Box) metodo.invoke(formula2, te);
            boxCounter = 0;

            parseBox(bo);
            assignTokens();
        } catch (Exception ex) {
            JMathAnimScene.logger.warn("Error parsing LaTeX boxes or assigning tokens of " + this.latex.getText() + ":" + ex.getLocalizedMessage());
            ex.printStackTrace();

        }
    }

    private void distilleTokens() {

        //A dot followed by a number should be marked as number too
        for (int i = 1; i < tokens.size(); i++) {
            LatexToken token = tokens.get(i);
            LatexToken prevToken = tokens.get(i - 1);
            if (token.type == LatexToken.TokenType.NUMBER) {
                if ("normaldot".equals(prevToken.string)) {
                    prevToken.type = LatexToken.TokenType.NUMBER;
                }
            }

        }
    }

    private void parseAtom(Object obj) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        //Some Atom subclasses may have null children (like ScripAtom.subscript), so
        //we have to check if the passed object is really an Atom. If null, do nothing.
        Atom atom;
        if (obj instanceof Atom) {
            atom = (Atom) obj;
        } else {
            return;
        }

        Field campo;

//        JMathAnimScene.logger.debug("Parsing " + atom.getClass().getCanonicalName());
        if (atom instanceof StyleAtom) {
            StyleAtom styleAtom = (StyleAtom) atom;
            campo = StyleAtom.class.getDeclaredField("at");
            campo.setAccessible(true);
            Atom field = (Atom) campo.get(styleAtom);
            parseAtom(field);
            return;
        }

        if (atom instanceof RomanAtom) {
            Modifier bkModifier = modifier;
            if (modifier == Modifier.NORMAL) {
                modifier = Modifier.RAW_TEXT;
            }
            RomanAtom romanAtom = (RomanAtom) atom;
            campo = RomanAtom.class.getDeclaredField("base");
            campo.setAccessible(true);
            Atom field = (Atom) campo.get(romanAtom);
            parseAtom(field);
            modifier = bkModifier;
            return;
        }

        if (atom instanceof MathAtom) {
            Modifier bkModifier = modifier;
            modifier = Modifier.NORMAL;
            MathAtom mathAtom = (MathAtom) atom;
            campo = MathAtom.class.getDeclaredField("base");
            campo.setAccessible(true);
            Atom field = (Atom) campo.get(mathAtom);
            parseAtom(field);
            modifier = bkModifier;
            return;
        }

        if (atom instanceof BoldAtom) {
            BoldAtom boldAtom = (BoldAtom) atom;
            campo = BoldAtom.class.getDeclaredField("base");
            campo.setAccessible(true);
            Atom field = (Atom) campo.get(boldAtom);
            parseAtom(field);
            return;
        }
        if (atom instanceof AccentedAtom) {
            AccentedAtom accentedAtom = (AccentedAtom) atom;
            //First accent
            campo = AccentedAtom.class.getDeclaredField("accent");
            campo.setAccessible(true);
            Atom field = (Atom) campo.get(accentedAtom);
            this.takesStyleFromNextFlag = true;
            parseAtom(field);
            this.takesStyleFromNextFlag = false;
            //Then base
            campo = AccentedAtom.class.getDeclaredField("base");
            campo.setAccessible(true);
            field = (Atom) campo.get(accentedAtom);
            parseAtom(field);
            return;
        }

        if (atom instanceof RowAtom) {
            RowAtom rowAtom = (RowAtom) atom;
            campo = RowAtom.class.getDeclaredField("elements");
            campo.setAccessible(true);
            LinkedList<Atom> valor = (LinkedList<Atom>) campo.get(rowAtom);
            for (Atom atom1 : valor) {
                parseAtom(atom1);

            }
            return;
        }
        if (atom instanceof CharAtom) {
            CharAtom charAtom = (CharAtom) atom;
            LatexToken.TokenType type = LatexToken.TokenType.CHAR;
            String name = "" + charAtom.getCharacter();
            addTokenToList(type, name);
            return;
        }
        if (atom instanceof SymbolAtom) {
            SymbolAtom symbolAtom = (SymbolAtom) atom;
            LatexToken.TokenType type = LatexToken.TokenType.SYMBOL;
            String name = "" + symbolAtom.getName();

            addTokenToList(type, name);
            return;
        }

        if (atom instanceof BigDelimiterAtom) {
            BigDelimiterAtom bigDelimiterAtom = (BigDelimiterAtom) atom;
            //Secondary type is stablished later, when assigning tokens from box list.
            addTokenToList(LatexToken.TokenType.SYMBOL, "" + bigDelimiterAtom.delim.getName());
            return;
        }

        if (atom instanceof FencedAtom) {
            FencedAtom fencedAtom = (FencedAtom) atom;
            campo = FencedAtom.class.getDeclaredField("left");
            campo.setAccessible(true);
            parseAtom(campo.get(fencedAtom));

            campo = FencedAtom.class.getDeclaredField("base");
            campo.setAccessible(true);
            parseAtom(campo.get(fencedAtom));

            campo = FencedAtom.class.getDeclaredField("right");
            campo.setAccessible(true);
            parseAtom(campo.get(fencedAtom));

            campo = FencedAtom.class.getDeclaredField("middle");
            campo.setAccessible(true);
            list = (List<MiddleAtom>) campo.get(fencedAtom);
            for (MiddleAtom middleAtom : list) {
                parseAtom(middleAtom);

            }
            return;
        }

        if (atom instanceof NthRoot) {
            NthRoot nthRoot = (NthRoot) atom;

            campo = NthRoot.class.getDeclaredField("root");
            campo.setAccessible(true);
            parseAtom(campo.get(nthRoot));

//            tokens.add(new LatexToken(LatexToken.TokenType.SQRT, "sqrt"));//TODO: IMPROVE
            addTokenToList(LatexToken.TokenType.SQRT, "sqrt");
            campo = NthRoot.class.getDeclaredField("base");
            campo.setAccessible(true);
            parseAtom(campo.get(nthRoot));
            return;
        }

        if (atom instanceof FractionAtom) {
            FractionAtom fractionAtom = (FractionAtom) atom;

            campo = FractionAtom.class.getDeclaredField("numerator");
            campo.setAccessible(true);
            activateSecondaryBit(LatexToken.SEC_NUMERATOR);
            parseAtom(campo.get(fractionAtom));
            deactivateSecondaryBit(LatexToken.SEC_NUMERATOR);

            addTokenToList(LatexToken.TokenType.FRACTION_BAR, "fractionRule");

            campo = FractionAtom.class.getDeclaredField("denominator");
            campo.setAccessible(true);
            activateSecondaryBit(LatexToken.SEC_DENOMINATOR);
            parseAtom(campo.get(fractionAtom));
            deactivateSecondaryBit(LatexToken.SEC_DENOMINATOR);

            return;
        }

        if (atom instanceof TypedAtom) {
            TypedAtom typedAtom = (TypedAtom) atom;
            modifier = Modifier.TYPED;
            Atom aa = typedAtom.getBase();
            parseAtom(aa);
            modifier = Modifier.NORMAL;
            return;
        }

        if (atom instanceof RomanAtom) {
            RomanAtom romanAtom = (RomanAtom) atom;
            campo = RomanAtom.class.getDeclaredField("base");
            campo.setAccessible(true);
            parseAtom(campo.get(romanAtom));
            return;
        }

        if (atom instanceof OverUnderDelimiter) {
            OverUnderDelimiter overUnderDelimiter = (OverUnderDelimiter) atom;
            campo = OverUnderDelimiter.class.getDeclaredField("base");
            campo.setAccessible(true);
            parseAtom(campo.get(overUnderDelimiter));
            campo = OverUnderDelimiter.class.getDeclaredField("symbol");
            campo.setAccessible(true);
            parseAtom(campo.get(overUnderDelimiter));
            //Under/over the brace
            campo = OverUnderDelimiter.class.getDeclaredField("script");
            campo.setAccessible(true);
            parseAtom(campo.get(overUnderDelimiter));
            return;
        }

        if (atom instanceof OverlinedAtom) {
            OverlinedAtom overlinedAtom = (OverlinedAtom) atom;

            this.takesStyleFromNextFlag = true;
            addTokenToList(LatexToken.TokenType.SYMBOL, "overbar");
            this.takesStyleFromNextFlag = false;

            campo = OverlinedAtom.class.getDeclaredField("base");
            campo.setAccessible(true);
            parseAtom(campo.get(overlinedAtom));
        }

        if (atom instanceof UnderOverArrowAtom) {
            UnderOverArrowAtom underOverArrowAtom = (UnderOverArrowAtom) atom;

            campo = UnderOverArrowAtom.class.getDeclaredField("over");
            campo.setAccessible(true);
            boolean over = (boolean) campo.get(underOverArrowAtom);

            //Determine type of arrow 
            //dble =true if double arrow
            //left=true if is left arrow
            campo = UnderOverArrowAtom.class.getDeclaredField("dble");
            campo.setAccessible(true);
            boolean dble = (boolean) campo.get(underOverArrowAtom);

            campo = UnderOverArrowAtom.class.getDeclaredField("left");
            campo.setAccessible(true);
            boolean left = (boolean) campo.get(underOverArrowAtom);
            String arrowTypeName = "overleftrightarrow";
            if (!dble) {
                arrowTypeName = (left ? "overleftarrow" : "overrightarrow");

            }
            campo = UnderOverArrowAtom.class.getDeclaredField("base");
            campo.setAccessible(true);
            Atom base = (Atom) campo.get(underOverArrowAtom);

            if (over) {
                this.takesStyleFromNextFlag = true;
                addTokenToList(LatexToken.TokenType.DELIMITER, arrowTypeName);
                this.takesStyleFromNextFlag = false;
                parseAtom(base);
            } else {
                parseAtom(base);
                addTokenToList(LatexToken.TokenType.DELIMITER, arrowTypeName);
            }
        }

        if (atom instanceof ScriptsAtom) {
            ScriptsAtom scriptsAtom = (ScriptsAtom) atom;
            campo = ScriptsAtom.class.getDeclaredField("base");
            campo.setAccessible(true);
            parseAtom(campo.get(scriptsAtom));

            campo = ScriptsAtom.class.getDeclaredField("superscript");
            campo.setAccessible(true);
            activateSecondaryBit(LatexToken.SEC_SUPERSCRIPT);
            deactivateSecondaryBit(LatexToken.SEC_NORMAL);
            parseAtom(campo.get(scriptsAtom));
            deactivateSecondaryBit(LatexToken.SEC_SUPERSCRIPT);
            activateSecondaryBit(LatexToken.SEC_NORMAL);

            campo = ScriptsAtom.class.getDeclaredField("subscript");
            campo.setAccessible(true);

            activateSecondaryBit(LatexToken.SEC_SUBSCRIPT);
            deactivateSecondaryBit(LatexToken.SEC_NORMAL);
            parseAtom(campo.get(scriptsAtom));
            deactivateSecondaryBit(LatexToken.SEC_SUBSCRIPT);
            activateSecondaryBit(LatexToken.SEC_NORMAL);
            return;
        }

        if (atom instanceof BigOperatorAtom) {
            BigOperatorAtom bigOperatorAtom = (BigOperatorAtom) atom;

            campo = BigOperatorAtom.class.getDeclaredField("over");
            campo.setAccessible(true);
            Atom overAtom = (Atom) campo.get(bigOperatorAtom);

            campo = BigOperatorAtom.class.getDeclaredField("base");
            campo.setAccessible(true);
            Atom baseAtom = (Atom) campo.get(bigOperatorAtom);

            //Retrieve the command name for the big operator
            campo = SymbolAtom.class.getDeclaredField("name");
            campo.setAccessible(true);
            String opName = (String) campo.get(baseAtom);

            campo = BigOperatorAtom.class.getDeclaredField("under");
            campo.setAccessible(true);
            Atom underAtom = (Atom) campo.get(bigOperatorAtom);

            //Depending on the specific operator command, shape order is altered
            if (opName.contains("int")) {
                //base, over, under
                parseAtom(baseAtom);

                activateSecondaryBit(LatexToken.SEC_TO_INDEX);
                deactivateSecondaryBit(LatexToken.SEC_NORMAL);
                parseAtom(overAtom);
                deactivateSecondaryBit(LatexToken.SEC_TO_INDEX);
                activateSecondaryBit(LatexToken.SEC_NORMAL);

                activateSecondaryBit(LatexToken.SEC_FROM_INDEX);
                deactivateSecondaryBit(LatexToken.SEC_NORMAL);
                parseAtom(underAtom);
                deactivateSecondaryBit(LatexToken.SEC_FROM_INDEX);
                activateSecondaryBit(LatexToken.SEC_NORMAL);

            } else {//over, base, under

                activateSecondaryBit(LatexToken.SEC_TO_INDEX);
                deactivateSecondaryBit(LatexToken.SEC_NORMAL);
                parseAtom(overAtom);
                deactivateSecondaryBit(LatexToken.SEC_TO_INDEX);
                activateSecondaryBit(LatexToken.SEC_NORMAL);

                parseAtom(baseAtom);

                activateSecondaryBit(LatexToken.SEC_FROM_INDEX);
                deactivateSecondaryBit(LatexToken.SEC_NORMAL);
                parseAtom(underAtom);
                deactivateSecondaryBit(LatexToken.SEC_FROM_INDEX);
                activateSecondaryBit(LatexToken.SEC_NORMAL);
            }

            return;
        }
        if (atom instanceof MatrixAtom) {
            MatrixAtom matrixAtom = (MatrixAtom) atom;
            campo = MatrixAtom.class.getDeclaredField("matrix");
            campo.setAccessible(true);
            ArrayOfAtoms arrayofAtoms = (ArrayOfAtoms) campo.get(matrixAtom);
            LinkedList<LinkedList<Atom>> array = arrayofAtoms.array;
            for (LinkedList<Atom> linkedList : array) {
                for (Atom atom1 : linkedList) {
                    parseAtom(atom1);

                }

            }
            return;
        }

    }

    protected void addTokenToList(LatexToken.TokenType type, String name) {
        switch (modifier) {
            case TYPED:
                type = LatexToken.TokenType.NAMED_FUNCTION;
                break;
            case RAW_TEXT:
                type=LatexToken.TokenType.NON_MATH_CHAR;
                break;
        }

        LatexToken token = new LatexToken(type, name);
//        if (secondaryType != 0) {
//            if (token.secondaryFlags == null) {
//                token.secondaryFlags = 0;
//            }
        token.activateSecondaryBit(secondaryType);
//        }
        token.takesStyleFromNext = this.takesStyleFromNextFlag;
        tokens.add(token);
        previousToken = token;
    }

    private void parseBox(Box bo) throws Exception {
        Field campo;

        if (bo instanceof CharBox) {
            CharBox charBox = (CharBox) bo;
            boxes.add(charBox);
            CharFont bb = getFontFromCharBox(bo);
//            System.out.println(boxes.size()+" Charbox "+bb.fontId+"  "+((int)bb.c)+"  "+bb.c);

        }

        if (bo instanceof OverUnderBox) {
            OverUnderBox overUnderBox = (OverUnderBox) bo;
            campo = OverUnderBox.class.getDeclaredField("base");
            campo.setAccessible(true);
            Box baseBox = (Box) campo.get(overUnderBox);
            parseBox(baseBox);
            campo = OverUnderBox.class.getDeclaredField("del");
            campo.setAccessible(true);
            Box delBox = (Box) campo.get(overUnderBox);
            parseBox(delBox);
        }

        if (bo instanceof org.scilab.forge.jlatexmath.HorizontalRule) {
            boxes.add(bo);
        }

        if (bo instanceof ScaleBox) {
            ScaleBox scaleBox = (ScaleBox) bo;
            campo = ScaleBox.class.getDeclaredField("box");
            campo.setAccessible(true);
            Box box = (Box) campo.get(scaleBox);
            parseBox(box);
        }

        campo = Box.class.getDeclaredField("children");
        campo.setAccessible(true);
        LinkedList<Box> children = (LinkedList<Box>) campo.get(bo);
        for (Box box : children) {
            parseBox(box);
        }
    }

    protected CharFont getFontFromCharBox(Box charBox) {
        if (charBox instanceof CharBox) {
            try {
                CharBox charBox1 = (CharBox) charBox;
                Field campo;
                campo = CharBox.class.getDeclaredField("cf");
                campo.setAccessible(true);
                CharFont cf = (CharFont) campo.get(charBox1);
                return cf;
            } catch (Exception ex) {
                Logger.getLogger(LatexParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
//            System.out.println("Intentado extraer font de algo que no es charbox!");
            return null;
        }
        return null;
    }

    public void assignTokens() {
//        if (tokens.size() == boxes.size()) {//Easy
//            assignedTokens.addAll(tokens);
//            return;
//        }
        boxCounter = 0;
        for (int n = 0; n < tokens.size(); n++) {
            LatexToken token = tokens.get(n);
            switch (token.type) {
                case NON_MATH_CHAR, CHAR, FRACTION_BAR, GREEK_LETTER, NAMED_FUNCTION, OPERATOR, BINARY_OPERATOR, RELATION, NUMBER, ARROW:
                    //These are supposed to be the "easy tokens"
                    assignedTokens.add(token);
                    boxCounter++;
                    break;
                case DELIMITER:
                    processDelimiter(token);
                    break;
                case SYMBOL:
                    processSymbol(token);
                    break;
                case SQRT:
                    processSQRT(token);
                    break;
                default:
                    JMathAnimScene.logger.warn("Token not recognized when trying to assign: " + token);
                    break;
            }

        }
    }

    private void processSQRT(LatexToken token) {
        while (!(boxes.get(boxCounter) instanceof HorizontalRule)) {
            assignedTokens.add(token);
            boxCounter++;
        }
        assignedTokens.add(token);
        boxCounter++;
    }

    private void processSymbol(LatexToken token) {
        switch (token.string) {
            case "vert":
                processDelimiter(token);//vert can be a delimiter
                break;
            case "Vert":
                processDelimiter(token);//Vert can be a delimiter
                break;
            default:
                assignedTokens.add(token);
                boxCounter++;
        }
    }

    private boolean compareCharFont(Box b, int fontId, int c) {
        CharFont cf = getFontFromCharBox(b);
        return ((cf.fontId == fontId) & (cf.c == (char) c));
    }

    private void processDelimiter(LatexToken token) {
        //TODO: Add ALL delimiters: https://docs.aspose.com/tex/java/latex-math-delimiters/
        //Para añadir delimiter nuevo: incluirlo en array delimiters de LatexToken
        switch (token.string) {
            case "lbrack":
                scanBigDelimiter(token,
                        18, 40, //Normal 
                        1, 161, //\big 
                        1, 179, //\Big
                        1, 195, //\Bigg
                        1, 181, //\Bigg4
                        1, 48,//Extensible upper part
                        1, 64,//Extensible lower part
                        1, 0,//Extensible over right part
                        1, 0//Extensible over left part
                );

                break;
            case "rbrack":
                scanBigDelimiter(token,
                        18, 41, //Normal
                        1, 162, //\big
                        1, 180, //\Big
                        1, 33, //\Bigg
                        1, 182, //\Bigg4
                        1, 49, //Extensible upper part
                        1, 65,//Extensible lower part
                        1, 0,//Extensible over right part
                        1, 0//Extensible over left part
                );
                break;
            case "lbrace":
                scanBigDelimiter(token,
                        8, 102, //Normal
                        1, 169, //\big
                        1, 110, //\Big
                        1, 40, //\Bigg
                        1, 189, //\Bigg4
                        1, 56, //Extensible upper part
                        1, 58,//Extensible lower part
                        1, 56,//Extensible over right part
                        1, 58//Extensible over left part
                );
                break;
            case "rbrace":
                scanBigDelimiter(token,
                        8, 103, //Normal
                        1, 170, //\big
                        1, 111, //\Big
                        1, 41, //\Bigg
                        1, 190, //\Bigg4
                        1, 57, //Extensible upper part
                        1, 59,//Extensible lower part
                        1, 0,//Extensible over right part
                        1, 0//Extensible over left part
                );
                break;
            case "lsqbrack":
                scanBigDelimiter(token,
                        18, 91, //Normal
                        1, 163, //\big
                        1, 104, //\Big
                        1, 34, //\Bigg
                        1, 183, //\Bigg2
                        1, 50,//Extensible upper part
                        1, 52,//Extensible lower part
                        1, 0,//Extensible over right part
                        1, 0//Extensible over left part
                );
                break;
            case "rsqbrack":
                scanBigDelimiter(token,
                        18, 93, //Normal
                        1, 164, //\big
                        1, 105, //\Big
                        1, 35, //\Bigg
                        1, 184, //\Bigg4
                        1, 51,//Extensible upper part
                        1, 53,//Extensible lower part
                        1, 0,//Extensible over right part
                        1, 0//Extensible over left part
                );
                break;
            case "vert":
                token.type = LatexToken.TokenType.DELIMITER;
                scanBigDelimiter(token,
                        8, 106,//Normal (LR)
                        0, 0, //\big (LR)
                        0, 0, //\Big(LR)
                        0, 0, //\Bigg (LR)
                        0, 0, //\Bigg4
                        1, 175,//Extensible upper part
                        1, 175,//Extensible lower part
                        1, 0,//Extensible over right part
                        1, 0//Extensible over left part
                );
                break;
            case "Vert":
                token.type = LatexToken.TokenType.DELIMITER;
                scanBigDelimiter(token,
                        8, 107,//Normal (LR)
                        0, 0, //\big (LR)
                        0, 0, //\Big(LR)
                        0, 0, //\Bigg (LR)
                        0, 0, //\Bigg4
                        1, 176,//Extensible upper part
                        1, 176,//Extensible lower part
                        1, 0,//Extensible over right part
                        1, 0//Extensible over left part
                );
                break;
            case "lfloor":
                scanBigDelimiter(token,
                        8, 98, //Normal
                        1, 165, //\big
                        1, 106, //\Big
                        1, 36, //\Bigg
                        1, 185, //\Bigg4
                        1, 54,//Extensible upper part
                        1, 52,//Extensible lower part
                        1, 0,//Extensible over right part
                        1, 0//Extensible over left part
                );
                break;
            case "rfloor":
                scanBigDelimiter(token,
                        8, 99, //Normal
                        1, 166, //\big
                        1, 107, //\Big
                        1, 37, //\Bigg
                        1, 186, //\Bigg4
                        1, 55,//Extensible upper part
                        1, 53,//Extensible lower part
                        1, 0,//Extensible over right part
                        1, 0//Extensible over left part
                );
                break;
            case "lceil":
                scanBigDelimiter(token,
                        8, 100, //Normal
                        1, 167, //\big
                        1, 108, //\Big
                        1, 38, //\Bigg
                        1, 187, //\Bigg4
                        1, 50,//Extensible upper part
                        1, 54,//Extensible lower part
                        1, 0,//Extensible over right part
                        1, 0//Extensible over left part
                );
                break;
            case "rceil":
                scanBigDelimiter(token,
                        8, 101, //Normal
                        1, 168, //\big
                        1, 109, //\Big
                        1, 39, //\Bigg
                        1, 188, //\Bigg4
                        1, 51,//Extensible upper part
                        1, 55,//Extensible lower part
                        1, 0,//Extensible over right part
                        1, 0//Extensible over left part
                );
                break;

            case "langle":
                scanBigDelimiter(token,
                        8, 104, //Normal
                        1, 173, //\big
                        1, 68, //\Big
                        1, 191, //\Bigg
                        0, 0, //\Bigg4
                        1, 42,//Extensible upper part
                        1, -1,//Extensible lower part (-1=none, only upper symbol)
                        1, 0,//Extensible over right part
                        1, 0//Extensible over left part
                );
                break;
            case "rangle":
                scanBigDelimiter(token,
                        8, 105, //Normal
                        1, 174, //\big
                        1, 69, //\Big
                        1, 192, //\Bigg
                        0, 0, //\Bigg4
                        1, 43,//Extensible upper part
                        1, -1,//Extensible lower part (-1=none, only upper symbol)
                        1, 0,//Extensible over right part
                        1, 0//Extensible over left part
                );
                break;
            case "overrightarrow":
                token.type = LatexToken.TokenType.ARROW;
                token.activateSecondaryBit(LatexToken.SEC_RIGHT_ARROW);
                scanBigDelimiter(token,
                        8, 33, //Normal
                        0, 0, //\big
                        0, 0, //\Big
                        0, 0, //\Bigg
                        0, 0, //\Bigg4
                        8, 161,//Extensible upper part
                        8, 33,//Extensible lower part (-1=none, only upper symbol)
                        1, 0,//Extensible over right part
                        1, 0//Extensible over left part
                );
                token.deactivateSecondaryBit(LatexToken.SEC_RIGHT_ARROW);
                break;
            case "overleftarrow":
                //This case es special, as begins with cf(8,195) and may have
                //arbitrary (0 or more) cf(8,161) tokens, without a terminating token
//                scanBigDelimiter(token,
//                        8, 195, //Normal
//                        1, 174, //\big
//                        1, 69, //\Big
//                        1, 192, //\Bigg
//                        0, 0, //\Bigg4
//                        8, 195,//Extensible upper part
//                        8, 161,//Extensible lower part (-1=none, only upper symbol)
//                        1, 0,//Extensible over right part
//                        1, 0//Extensible over left part
//                );
                scanLeftArrow(token, 8, 195, 8, 161);

                break;
            case "overleftrightarrow":
                token.type = LatexToken.TokenType.ARROW;
                token.activateSecondaryBit(LatexToken.SEC_LEFTRIGHT_ARROW);
                scanBigDelimiter(token,
                        8, 33, //Normal FIX THIS
                        1, 174, //\big
                        1, 69, //\Big
                        1, 192, //\Bigg
                        0, 0, //\Bigg4
                        8, 195,//Extensible upper part
                        8, 33,//Extensible lower part (-1=none, only upper symbol)
                        1, 0,//Extensible over right part
                        1, 0//Extensible over left part
                );
                token.deactivateSecondaryBit(LatexToken.SEC_LEFTRIGHT_ARROW);
                break;

//             case "lname":
//                scanBigDelimiter(token,
//                        8,0, //Normal
//                        1,0, //\big
//                        1,0, //\Big
//                        1,0,//Extensible upper part
//                        1,0);//Extensible lower part
//                break;
//            case "rname":
//                scanBigDelimiter(token,
//                        8,0, //Normal
//                        1,0, //\big
//                        1,0, //\Big
//                        1,0,//Extensible upper part
//                        1,0);//Extensible lower part
//                break;
        }
    }

    protected void scanLeftArrow(LatexToken token, int cfStart, int cStart, int cfContinue, int cContinue) {
        Box b = boxes.get(boxCounter);
        if (compareCharFont(b, cfStart, cStart)) {
            token.activateSecondaryBit(LatexToken.SEC_LEFT_ARROW);
            token.type = LatexToken.TokenType.ARROW;
            assignedTokens.add(token);
            boxCounter++;
            b = boxes.get(boxCounter);
            while (compareCharFont(b, cfContinue, cContinue)) {
                assignedTokens.add(token);
                boxCounter++;
                b = boxes.get(boxCounter);
            }
            token.deactivateSecondaryBit(LatexToken.SEC_LEFT_ARROW);
        }

    }

    protected void scanBigDelimiter(LatexToken token,
            int cfSmall, int cSmall,
            int cfBig1, int cBig1,
            int cfBig2, int cBig2,
            int cfBig3, int cBig3,
            int cfBig4, int cBig4,
            int cfExtensibleUpper, int cExtensibleUpper,
            int cfExtensibleLower, int cExtensibleLower,
            int cfExtensibleOverRight, int cExtensibleOverRight,
            int cfExtensibleOverLeft, int cExtensibleOverLeft
    ) {
        Box b = boxes.get(boxCounter);

        if (compareCharFont(b, cfSmall, cSmall)) {//Small delimiter
            token.activateSecondaryBit(LatexToken.SEC_DELIMITER_NORMAL);
            assignedTokens.add(token);
            token.deactivateSecondaryBit(LatexToken.SEC_DELIMITER_NORMAL);
            boxCounter++;
            return;
        }

        if (compareCharFont(b, cfBig1, cBig1)) {//\big delimiter
            token.activateSecondaryBit(LatexToken.SEC_DELIMITER_BIG1);
            assignedTokens.add(token);
            token.deactivateSecondaryBit(LatexToken.SEC_DELIMITER_BIG1);
            boxCounter++;
            return;
        }
        if (compareCharFont(b, cfBig2, cBig2)) {//\Big delimiter
            token.activateSecondaryBit(LatexToken.SEC_DELIMITER_BIG2);
            assignedTokens.add(token);
            token.deactivateSecondaryBit(LatexToken.SEC_DELIMITER_BIG2);
            boxCounter++;
            return;
        }
        if (compareCharFont(b, cfBig3, cBig3)) {//\Big delimiter
            token.activateSecondaryBit(LatexToken.SEC_DELIMITER_BIG3);
            assignedTokens.add(token);
            token.deactivateSecondaryBit(LatexToken.SEC_DELIMITER_BIG3);
            boxCounter++;
            return;
        }
        if (compareCharFont(b, cfBig4, cBig4)) {//\Big4 delimiter
            token.activateSecondaryBit(LatexToken.SEC_DELIMITER_BIG4);
            assignedTokens.add(token);
            token.deactivateSecondaryBit(LatexToken.SEC_DELIMITER_BIG4);
            boxCounter++;
            return;
        }

        if (compareCharFont(b, cfExtensibleUpper, cExtensibleUpper)) {//A big left upper side delimiter
            token.activateSecondaryBit(LatexToken.SEC_DELIMITER_EXTENSIBLE);
            assignedTokens.add(token);
            token.deactivateSecondaryBit(LatexToken.SEC_DELIMITER_EXTENSIBLE);
            boxCounter++;
            if (cExtensibleLower == -1) {//Special case where extensible delimiter is exactly 1 char (like \langle)
                return;
            }
            boolean trailStarted = false;
            while (true) {
                if (boxCounter == boxes.size()) {
                    break;
                }
                boolean found = compareCharFont(boxes.get(boxCounter), cfExtensibleLower, cExtensibleLower);

                if (found) {
                    trailStarted = true;
                }

                if (trailStarted & !found) {
                    break;
                }
                token.activateSecondaryBit(LatexToken.SEC_DELIMITER_EXTENSIBLE);
                assignedTokens.add(token);
                token.deactivateSecondaryBit(LatexToken.SEC_DELIMITER_EXTENSIBLE);
                boxCounter++;
            }

        }
    }

    private void activateSecondaryBit(int bit) {
        if (secondaryType == null) {
            secondaryType = 0;
        }
        secondaryType |= bit;
    }

    private void deactivateSecondaryBit(int bit) {
        if (secondaryType == null) {
            return;
        }
        secondaryType &= ~bit;
    }
}
