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
import java.util.Iterator;
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
import org.scilab.forge.jlatexmath.JavaFontRenderingAtom;
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
import org.scilab.forge.jlatexmath.VRowAtom;

/**
 * Handles the parsing of elements of a LaTeX objects and assign to each
 * generated Shape a LatexToken describing the glyph. It relies in the parsing
 * capabilities of the JLaTeXMath library.
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class LatexParser implements Iterable<LatexToken> {

    private final ArrayList<LatexToken> assignedTokens;
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
    private int delimiterDepth;

    public LatexParser(AbstractLaTeXMathObject latex) {
        this.latex = latex;
        this.list = new ArrayList<>();
        this.boxes = new ArrayList<>();
        this.tokens = new ArrayList<>();
        this.assignedTokens = new ArrayList<>();
        this.modifier = Modifier.NORMAL;
        this.secondaryType = LatexToken.SEC_NORMAL;
        takesStyleFromNextFlag = false;
        delimiterDepth = 0;
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
        delimiterDepth = 0;
        this.modifier = Modifier.NORMAL;

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
            if (token.getType() == LatexToken.TokenType.NUMBER) {
                if ("normaldot".equals(prevToken.getString())) {
                    prevToken.setType(LatexToken.TokenType.NUMBER);
                }
            }
            //If a dot is next to a NON_MATH_CHAR, we assume it is also a non math char
            if ("normaldot".equals(token.getString())) {
                if (prevToken.getType() == LatexToken.TokenType.NON_MATH_CHAR) {
                    token.setType(LatexToken.TokenType.NON_MATH_CHAR);
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

        Field classField;

//        JMathAnimScene.logger.debug("Parsing " + atom.getClass().getCanonicalName());
        if (atom instanceof StyleAtom) {
            StyleAtom styleAtom = (StyleAtom) atom;
            classField = StyleAtom.class.getDeclaredField("at");
            classField.setAccessible(true);
            Atom field = (Atom) classField.get(styleAtom);
            parseAtom(field);
            return;
        }

        if (atom instanceof BoldAtom) {
            BoldAtom boldAtom = (BoldAtom) atom;
            classField = BoldAtom.class.getDeclaredField("base");
            classField.setAccessible(true);
            activateSecondaryBit(LatexToken.SEC_BOLD_FONT);
            Atom field = (Atom) classField.get(boldAtom);
            parseAtom(field);
            deactivateSecondaryBit(LatexToken.SEC_BOLD_FONT);
            return;
        }

        if (atom instanceof RomanAtom) {
            Modifier bkModifier = modifier;
            if (modifier == Modifier.NORMAL) {
                modifier = Modifier.RAW_TEXT;
            }
            RomanAtom romanAtom = (RomanAtom) atom;
            classField = RomanAtom.class.getDeclaredField("base");
            classField.setAccessible(true);
            Atom field = (Atom) classField.get(romanAtom);
            parseAtom(field);
            modifier = bkModifier;
            return;
        }

        if (atom instanceof MathAtom) {
            Modifier bkModifier = modifier;
            modifier = Modifier.NORMAL;
            MathAtom mathAtom = (MathAtom) atom;
            classField = MathAtom.class.getDeclaredField("base");
            classField.setAccessible(true);
            Atom field = (Atom) classField.get(mathAtom);
            parseAtom(field);
//            modifier = bkModifier;
            return;
        }

        if (atom instanceof BoldAtom) {
            BoldAtom boldAtom = (BoldAtom) atom;
            classField = BoldAtom.class.getDeclaredField("base");
            classField.setAccessible(true);
            Atom field = (Atom) classField.get(boldAtom);
            parseAtom(field);
            return;
        }

        if (atom instanceof JavaFontRenderingAtom) {//For symbols like º or ª
            JavaFontRenderingAtom javaFontRenderingAtom = (JavaFontRenderingAtom) atom;
            classField = JavaFontRenderingAtom.class.getDeclaredField("str");
            classField.setAccessible(true);
            String str = (String) classField.get(javaFontRenderingAtom);

            LatexToken.TokenType type = LatexToken.TokenType.CHAR;
            String name = "" + str;
            addTokenToList(type, name);

            return;
        }

        if (atom instanceof AccentedAtom) {
            AccentedAtom accentedAtom = (AccentedAtom) atom;
            //First accent
            classField = AccentedAtom.class.getDeclaredField("accent");
            classField.setAccessible(true);
            Atom field = (Atom) classField.get(accentedAtom);
            this.takesStyleFromNextFlag = true;
            parseAtom(field);
            this.takesStyleFromNextFlag = false;
            //Then base
            classField = AccentedAtom.class.getDeclaredField("base");
            classField.setAccessible(true);
            field = (Atom) classField.get(accentedAtom);
            parseAtom(field);
            return;
        }

        if (atom instanceof RowAtom) {
            RowAtom rowAtom = (RowAtom) atom;
            classField = RowAtom.class.getDeclaredField("elements");
            classField.setAccessible(true);
            LinkedList<Atom> elements = (LinkedList<Atom>) classField.get(rowAtom);
            for (Atom atomElement : elements) {
                parseAtom(atomElement);

            }
            return;
        }

        if (atom instanceof VRowAtom) {
            VRowAtom vRowAtom = (VRowAtom) atom;
            classField = VRowAtom.class.getDeclaredField("elements");
            classField.setAccessible(true);
            LinkedList<Atom> elements = (LinkedList<Atom>) classField.get(vRowAtom);
            for (Atom atomElement : elements) {
                parseAtom(atomElement);

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
            classField = FencedAtom.class.getDeclaredField("left");
            classField.setAccessible(true);
            parseAtom(classField.get(fencedAtom));

            classField = FencedAtom.class.getDeclaredField("base");
            classField.setAccessible(true);
            parseAtom(classField.get(fencedAtom));

            classField = FencedAtom.class.getDeclaredField("right");
            classField.setAccessible(true);
            parseAtom(classField.get(fencedAtom));

            classField = FencedAtom.class.getDeclaredField("middle");
            classField.setAccessible(true);
            list = (List<MiddleAtom>) classField.get(fencedAtom);
            for (MiddleAtom middleAtom : list) {
                parseAtom(middleAtom);

            }
            return;
        }

        if (atom instanceof NthRoot) {
            NthRoot nthRoot = (NthRoot) atom;

            classField = NthRoot.class.getDeclaredField("root");
            classField.setAccessible(true);
            parseAtom(classField.get(nthRoot));

//            tokens.add(new LatexToken(LatexToken.TokenType.SQRT, "sqrt"));//TODO: IMPROVE
            addTokenToList(LatexToken.TokenType.SQRT, "sqrt");
            classField = NthRoot.class.getDeclaredField("base");
            classField.setAccessible(true);
            parseAtom(classField.get(nthRoot));
            return;
        }

        if (atom instanceof FractionAtom) {
            FractionAtom fractionAtom = (FractionAtom) atom;

            classField = FractionAtom.class.getDeclaredField("numerator");
            classField.setAccessible(true);
            activateSecondaryBit(LatexToken.SEC_NUMERATOR);
            parseAtom(classField.get(fractionAtom));
            deactivateSecondaryBit(LatexToken.SEC_NUMERATOR);

            addTokenToList(LatexToken.TokenType.FRACTION_BAR, "fractionRule");

            classField = FractionAtom.class.getDeclaredField("denominator");
            classField.setAccessible(true);
            activateSecondaryBit(LatexToken.SEC_DENOMINATOR);
            parseAtom(classField.get(fractionAtom));
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
            classField = RomanAtom.class.getDeclaredField("base");
            classField.setAccessible(true);
            parseAtom(classField.get(romanAtom));
            return;
        }

        if (atom instanceof OverUnderDelimiter) {
            OverUnderDelimiter overUnderDelimiter = (OverUnderDelimiter) atom;
            classField = OverUnderDelimiter.class.getDeclaredField("base");
            classField.setAccessible(true);
            parseAtom(classField.get(overUnderDelimiter));
            classField = OverUnderDelimiter.class.getDeclaredField("symbol");
            classField.setAccessible(true);
            parseAtom(classField.get(overUnderDelimiter));
            //Under/over the brace
            classField = OverUnderDelimiter.class.getDeclaredField("script");
            classField.setAccessible(true);
            parseAtom(classField.get(overUnderDelimiter));
            return;
        }

        if (atom instanceof OverlinedAtom) {
            OverlinedAtom overlinedAtom = (OverlinedAtom) atom;

            this.takesStyleFromNextFlag = true;
            addTokenToList(LatexToken.TokenType.SYMBOL, "overbar");
            this.takesStyleFromNextFlag = false;

            classField = OverlinedAtom.class.getDeclaredField("base");
            classField.setAccessible(true);
            parseAtom(classField.get(overlinedAtom));
        }

        if (atom instanceof UnderOverArrowAtom) {
            UnderOverArrowAtom underOverArrowAtom = (UnderOverArrowAtom) atom;

            classField = UnderOverArrowAtom.class.getDeclaredField("over");
            classField.setAccessible(true);
            boolean over = (boolean) classField.get(underOverArrowAtom);

            //Determine type of arrow 
            //dble =true if double arrow
            //left=true if is left arrow
            classField = UnderOverArrowAtom.class.getDeclaredField("dble");
            classField.setAccessible(true);
            boolean dble = (boolean) classField.get(underOverArrowAtom);

            classField = UnderOverArrowAtom.class.getDeclaredField("left");
            classField.setAccessible(true);
            boolean left = (boolean) classField.get(underOverArrowAtom);
            String arrowTypeName = "overleftrightarrow";
            if (!dble) {
                arrowTypeName = (left ? "overleftarrow" : "overrightarrow");

            }
            classField = UnderOverArrowAtom.class.getDeclaredField("base");
            classField.setAccessible(true);
            Atom base = (Atom) classField.get(underOverArrowAtom);

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
            classField = ScriptsAtom.class.getDeclaredField("base");
            classField.setAccessible(true);
            parseAtom(classField.get(scriptsAtom));

            classField = ScriptsAtom.class.getDeclaredField("superscript");
            classField.setAccessible(true);
            activateSecondaryBit(LatexToken.SEC_SUPERSCRIPT);
            deactivateSecondaryBit(LatexToken.SEC_NORMAL);
            parseAtom(classField.get(scriptsAtom));
            deactivateSecondaryBit(LatexToken.SEC_SUPERSCRIPT);
            activateSecondaryBit(LatexToken.SEC_NORMAL);

            classField = ScriptsAtom.class.getDeclaredField("subscript");
            classField.setAccessible(true);

            activateSecondaryBit(LatexToken.SEC_SUBSCRIPT);
            deactivateSecondaryBit(LatexToken.SEC_NORMAL);
            parseAtom(classField.get(scriptsAtom));
            deactivateSecondaryBit(LatexToken.SEC_SUBSCRIPT);
            activateSecondaryBit(LatexToken.SEC_NORMAL);
            return;
        }

        if (atom instanceof BigOperatorAtom) {
            String opName = null;
            BigOperatorAtom bigOperatorAtom = (BigOperatorAtom) atom;

            classField = BigOperatorAtom.class.getDeclaredField("over");
            classField.setAccessible(true);
            Atom overAtom = (Atom) classField.get(bigOperatorAtom);

            classField = BigOperatorAtom.class.getDeclaredField("base");
            classField.setAccessible(true);
            Atom baseAtom = (Atom) classField.get(bigOperatorAtom);

            classField = BigOperatorAtom.class.getDeclaredField("under");
            classField.setAccessible(true);
            Atom underAtom = (Atom) classField.get(bigOperatorAtom);

            if (baseAtom instanceof TypedAtom) {//Its something like \\sin^2
                TypedAtom typedAtom = (TypedAtom) baseAtom;
                //base, over, under
                parseAtom(typedAtom);
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
                return;

            } else {
                //Retrieve the command name for the big operator
                classField = SymbolAtom.class.getDeclaredField("name");
                classField.setAccessible(true);
                opName = (String) classField.get(baseAtom);
            }

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
            classField = MatrixAtom.class.getDeclaredField("matrix");
            classField.setAccessible(true);
            ArrayOfAtoms arrayofAtoms = (ArrayOfAtoms) classField.get(matrixAtom);
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
        if (type == LatexToken.TokenType.CHAR) {
            switch (modifier) {
                case TYPED:
                    type = LatexToken.TokenType.NAMED_FUNCTION;
                    break;
                case RAW_TEXT:
                    type = LatexToken.TokenType.NON_MATH_CHAR;
                    break;
            }
        }

        LatexToken token = LatexToken.make()
                .setType(type)
                .setString(name)
                .setDelimiterDepth(this.delimiterDepth);
        token.activateSecondaryFlag(secondaryType);
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
            switch (token.getType()) {
                case NON_MATH_CHAR, CHAR, FRACTION_BAR, GREEK_LETTER, NAMED_FUNCTION, OPERATOR, BINARY_OPERATOR, RELATION, NUMBER, ARROW:
                    //These are supposed to be the "easy tokens"
                    addAssignedTokenToList(token);
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

    public void addAssignedTokenToList(LatexToken token) {
        token.setDelimiterDepth(this.delimiterDepth);
        assignedTokens.add(token);
        boxCounter++;
    }

    private void processSQRT(LatexToken token) {
        while (!(boxes.get(boxCounter) instanceof HorizontalRule)) {
            assignedTokens.add(token);
            boxCounter++;
        }
        addAssignedTokenToList(token);
    }

    private void processSymbol(LatexToken token) {
        switch (token.getString()) {
            case "vert":
                processDelimiter(token);//vert can be a delimiter
                break;
            case "Vert":
                processDelimiter(token);//Vert can be a delimiter
                break;
            default:
                addAssignedTokenToList(token);
        }
    }

    private boolean compareCharFont(Box b, int fontId, int c) {
        CharFont cf = getFontFromCharBox(b);
        return ((cf.fontId == fontId) & (cf.c == (char) c));
    }

    private void processDelimiter(LatexToken token) {
        //TODO: Add ALL delimiters: https://docs.aspose.com/tex/java/latex-math-delimiters/
        //Para añadir delimiter nuevo: incluirlo en array delimiters de LatexToken
        switch (token.getString()) {
            case "lbrack":
                this.delimiterDepth++;
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
                this.delimiterDepth--;
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
                this.delimiterDepth++;
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
                this.delimiterDepth--;
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
                this.delimiterDepth++;
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
                this.delimiterDepth--;
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
                token.setType(LatexToken.TokenType.DELIMITER);
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
                token.setType(LatexToken.TokenType.DELIMITER);
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
                this.delimiterDepth++;
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
                this.delimiterDepth--;
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
                this.delimiterDepth++;
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
                this.delimiterDepth--;
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
                this.delimiterDepth++;
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
                this.delimiterDepth--;
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
                token.setType(LatexToken.TokenType.ARROW);
                token.activateSecondaryFlag(LatexToken.SEC_RIGHT_ARROW);
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
                token.deactivateSecondaryFlag(LatexToken.SEC_RIGHT_ARROW);
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
                token.setType(LatexToken.TokenType.ARROW);
                token.activateSecondaryFlag(LatexToken.SEC_LEFTRIGHT_ARROW);
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
                token.deactivateSecondaryFlag(LatexToken.SEC_LEFTRIGHT_ARROW);
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
            token.activateSecondaryFlag(LatexToken.SEC_LEFT_ARROW);
            token.setType(LatexToken.TokenType.ARROW);
            assignedTokens.add(token);
            boxCounter++;
            b = boxes.get(boxCounter);
            while (compareCharFont(b, cfContinue, cContinue)) {
                assignedTokens.add(token);
                boxCounter++;
                b = boxes.get(boxCounter);
            }
            token.deactivateSecondaryFlag(LatexToken.SEC_LEFT_ARROW);
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
            token.activateSecondaryFlag(LatexToken.SEC_DELIMITER_NORMAL);
            assignedTokens.add(token);
            token.deactivateSecondaryFlag(LatexToken.SEC_DELIMITER_NORMAL);
            boxCounter++;
            return;
        }

        if (compareCharFont(b, cfBig1, cBig1)) {//\big delimiter
            token.activateSecondaryFlag(LatexToken.SEC_DELIMITER_BIG1);
            assignedTokens.add(token);
            token.deactivateSecondaryFlag(LatexToken.SEC_DELIMITER_BIG1);
            boxCounter++;
            return;
        }
        if (compareCharFont(b, cfBig2, cBig2)) {//\Big delimiter
            token.activateSecondaryFlag(LatexToken.SEC_DELIMITER_BIG2);
            assignedTokens.add(token);
            token.deactivateSecondaryFlag(LatexToken.SEC_DELIMITER_BIG2);
            boxCounter++;
            return;
        }
        if (compareCharFont(b, cfBig3, cBig3)) {//\Big delimiter
            token.activateSecondaryFlag(LatexToken.SEC_DELIMITER_BIG3);
            assignedTokens.add(token);
            token.deactivateSecondaryFlag(LatexToken.SEC_DELIMITER_BIG3);
            boxCounter++;
            return;
        }
        if (compareCharFont(b, cfBig4, cBig4)) {//\Big4 delimiter
            token.activateSecondaryFlag(LatexToken.SEC_DELIMITER_BIG4);
            assignedTokens.add(token);
            token.deactivateSecondaryFlag(LatexToken.SEC_DELIMITER_BIG4);
            boxCounter++;
            return;
        }

        if (compareCharFont(b, cfExtensibleUpper, cExtensibleUpper)) {//A big left upper side delimiter
            token.activateSecondaryFlag(LatexToken.SEC_DELIMITER_EXTENSIBLE);
            assignedTokens.add(token);
            token.deactivateSecondaryFlag(LatexToken.SEC_DELIMITER_EXTENSIBLE);
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
                token.activateSecondaryFlag(LatexToken.SEC_DELIMITER_EXTENSIBLE);
                assignedTokens.add(token);
                token.deactivateSecondaryFlag(LatexToken.SEC_DELIMITER_EXTENSIBLE);
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

    /**
     * Returns the given assigned token
     *
     * @param index The index of the token
     * @return The token
     */
    public LatexToken get(int index) {
        return assignedTokens.get(index);
    }

    /**
     * Returns the identified token list
     *
     * @return The token list
     */
    public ArrayList<LatexToken> getTokensList() {
        return assignedTokens;
    }

    /**
     * Returns the number of identified tokens. It must be less or equal than
     * the number of generated Shape objects.
     *
     * @return The number of tokens
     */
    public int size() {
        return assignedTokens.size();
    }

    @Override
    public Iterator<LatexToken> iterator() {
        return assignedTokens.iterator();
    }

}
