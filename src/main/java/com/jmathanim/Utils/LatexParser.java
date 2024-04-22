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
import org.scilab.forge.jlatexmath.ArrayOfAtoms;
import org.scilab.forge.jlatexmath.Atom;
import org.scilab.forge.jlatexmath.BigDelimiterAtom;
import org.scilab.forge.jlatexmath.BigOperatorAtom;
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
import org.scilab.forge.jlatexmath.RomanAtom;
import org.scilab.forge.jlatexmath.RowAtom;
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

public class LatexParser {

    public final ArrayList<LatexToken> assignedTokens;
    private int boxCounter;
    private TeXFormula formula2;
    public TeXIcon icon;
    private final AbstractLaTeXMathObject latex;
    private TeXFormula formula;

    public List<MiddleAtom> list;
    private LatexToken previousToken;
    private Atom rootCopy;

    public final ArrayList<LatexToken> tokens;
    public final ArrayList<Box> boxes;

    public enum Modifier {
        NORMAL, TYPED;
    }
    private Integer secondaryType;
    public Modifier modifier;

    public LatexParser(AbstractLaTeXMathObject latex) {
        this.latex = latex;
        this.list = new ArrayList<MiddleAtom>();
        this.boxes = new ArrayList<Box>();
        this.tokens = new ArrayList<>();
        this.assignedTokens = new ArrayList<>();
        this.modifier = Modifier.NORMAL;
        this.secondaryType = LatexToken.SEC_NORMAL;
    }

    public Integer[] getMatchingIndices(LatexToken token) {
        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < assignedTokens.size(); i++) {
            if (assignedTokens.get(i).match(token)) {
                indices.add(i);
            }

        }
        return indices.toArray(Integer[]::new);
    }

    public void colorizeTokens(JMColor color, LatexToken.TokenType tokenType, Integer delType, String name) {
        Integer[] indices = getMatchingIndices(new LatexToken(tokenType, delType, name));
        for (Integer indice : indices) {
            latex.get(indice).color(color);
        }
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
            JMathAnimScene.logger.warn("Error parsing LaTeX tokens of " + this.latex.getText());
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
            JMathAnimScene.logger.warn("Error parsing LaTeX boxes or assigning tokens of " + this.latex.getText());

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

    private void parseAtom(Object obj) throws Exception {
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
            RomanAtom romanAtom = (RomanAtom) atom;
            campo = RomanAtom.class.getDeclaredField("base");
            campo.setAccessible(true);
            Atom field = (Atom) campo.get(romanAtom);
            parseAtom(field);
            return;
        }
        if (atom instanceof MathAtom) {
            MathAtom mathAtom = (MathAtom) atom;
            campo = MathAtom.class.getDeclaredField("base");
            campo.setAccessible(true);
            Atom field = (Atom) campo.get(mathAtom);
            parseAtom(field);
            return;
        }

        if (atom instanceof EmptyAtom) {
            //Nothing to do here, remove this code later...
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
            JMathAnimScene.logger.debug("Parsing char " + name);
            addTokenToList(type, name);
            return;
        }
        if (atom instanceof SymbolAtom) {
            SymbolAtom symbolAtom = (SymbolAtom) atom;
            LatexToken.TokenType type = LatexToken.TokenType.SYMBOL;
            String name = "" + symbolAtom.getName();

            addTokenToList(type, name);
            //TODO: IMPROVE
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
            secondaryType |= LatexToken.SEC_NUMERATOR;
            parseAtom(campo.get(fractionAtom));
            secondaryType &= ~LatexToken.SEC_NUMERATOR;

            addTokenToList(LatexToken.TokenType.FRACTION_BAR, "fractionRule");

            campo = FractionAtom.class.getDeclaredField("denominator");
            campo.setAccessible(true);
            secondaryType |= LatexToken.SEC_DENOMINATOR;
            parseAtom(campo.get(fractionAtom));
            secondaryType &= ~LatexToken.SEC_DENOMINATOR;;
            return;
        }

        if (atom instanceof TypedAtom) {
            TypedAtom typedAtom = (TypedAtom) atom;
            modifier = Modifier.TYPED;
            Atom aa = typedAtom.getBase();
            JMathAnimScene.logger.debug("Entering typed atom");
            parseAtom(aa);
            JMathAnimScene.logger.debug("Exiting typed atom");
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

        if (atom instanceof ScriptsAtom) {
            ScriptsAtom scriptsAtom = (ScriptsAtom) atom;
            campo = ScriptsAtom.class.getDeclaredField("base");
            campo.setAccessible(true);
            parseAtom(campo.get(scriptsAtom));

            campo = ScriptsAtom.class.getDeclaredField("superscript");
            campo.setAccessible(true);
            secondaryType |= LatexToken.SEC_SUPERSCRIPT;//Activate superscript flag
            secondaryType &= ~LatexToken.SEC_NORMAL;//Deactivate normal flag
            parseAtom(campo.get(scriptsAtom));
            secondaryType |= LatexToken.SEC_NORMAL;//Reactivate normal flag
            secondaryType &= ~LatexToken.SEC_SUPERSCRIPT;//Deactivate superscript flag

            campo = ScriptsAtom.class.getDeclaredField("subscript");
            campo.setAccessible(true);
            secondaryType |= LatexToken.SEC_SUBSCRIPT;//Activate superscript flag
            secondaryType &= ~LatexToken.SEC_NORMAL;//Deactivate normal flag
            parseAtom(campo.get(scriptsAtom));
            secondaryType |= LatexToken.SEC_NORMAL;//Reactivate normal flag
            secondaryType &= ~LatexToken.SEC_SUBSCRIPT;//Deactivate superscript flag
            return;
        }

        if (atom instanceof BigOperatorAtom) {
            BigOperatorAtom bigOperatorAtom = (BigOperatorAtom) atom;

            campo = BigOperatorAtom.class.getDeclaredField("base");
            JMathAnimScene.logger.debug("Parsing base from bigoperator");
            campo.setAccessible(true);
            parseAtom((Atom) campo.get(bigOperatorAtom));

            secondaryType |= LatexToken.SEC_TO_INDEX;
            JMathAnimScene.logger.debug("Parsing over from bigoperator");
            campo = BigOperatorAtom.class.getDeclaredField("over");
            campo.setAccessible(true);
            parseAtom((Atom) campo.get(bigOperatorAtom));
            secondaryType &= ~LatexToken.SEC_TO_INDEX;

            secondaryType |= LatexToken.SEC_FROM_INDEX;
            JMathAnimScene.logger.debug("Parsing under from bigoperator");
            campo = BigOperatorAtom.class.getDeclaredField("under");
            campo.setAccessible(true);
            parseAtom((Atom) campo.get(bigOperatorAtom));
            secondaryType &= ~LatexToken.SEC_FROM_INDEX;
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
        }

        LatexToken token = new LatexToken(type, name);
        token.secondaryFlags = secondaryType;
        tokens.add(token);//TODO: IMPROVE
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

        if (bo instanceof org.scilab.forge.jlatexmath.HorizontalRule) {
            boxes.add(bo);
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
                case CHAR, FRACTION_BAR, GREEK_LETTER, NAMED_FUNCTION, OPERATOR, RELATION, NUMBER:
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
                processDelimiter(token);//vert puede ser delimiter
                break;
            case "Vert":
                processDelimiter(token);//Vert puede ser delimiter
                break;
            default:
                assignedTokens.add(token);
                boxCounter++;
        }
    }

    private boolean compareCharFont(Box b, int fontId, int c) {
        CharFont cf = getFontFromCharBox(b);
        int aa = cf.c;
        System.out.println("Delimiter code: " + cf.fontId + ", " + aa);
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
                        1, 64);//Extensible lower part
                break;
            case "rbrack":
                scanBigDelimiter(token,
                        18, 41, //Normal
                        1, 162, //\big
                        1, 180, //\Big
                        1, 33, //\Bigg
                        1, 182, //\Bigg4
                        1, 49, //Extensible upper part
                        1, 65);//Extensible lower part
                break;
            case "lbrace":
                scanBigDelimiter(token,
                        8, 102, //Normal
                        1, 169, //\big
                        1, 110, //\Big
                        1, 40, //\Bigg
                        1, 189, //\Bigg4
                        1, 56, //Extensible upper part
                        1, 58);//Extensible lower part
                break;
            case "rbrace":
                scanBigDelimiter(token,
                        8, 103, //Normal
                        1, 170, //\big
                        1, 111, //\Big
                        1, 41, //\Bigg
                        1, 190, //\Bigg4
                        1, 57, //Extensible upper part
                        1, 59);//Extensible lower part
                break;
            case "lsqbrack":
                scanBigDelimiter(token,
                        18, 91, //Normal
                        1, 163, //\big
                        1, 104, //\Big
                        1, 34, //\Bigg
                        1, 183, //\Bigg2
                        1, 50,//Extensible upper part
                        1, 52);//Extensible lower part
                break;
            case "rsqbrack":
                scanBigDelimiter(token,
                        18, 93, //Normal
                        1, 164, //\big
                        1, 105, //\Big
                        1, 35, //\Bigg
                        1, 184, //\Bigg4
                        1, 51,//Extensible upper part
                        1, 53);//Extensible lower part
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
                        1, 175);//Extensible lower part
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
                        1, 176);//Extensible lower part
                break;
            case "lfloor":
                scanBigDelimiter(token,
                        8, 98, //Normal
                        1, 165, //\big
                        1, 106, //\Big
                        1, 36, //\Bigg
                        1, 185, //\Bigg4
                        1, 54,//Extensible upper part
                        1, 52);//Extensible lower part
                break;
            case "rfloor":
                scanBigDelimiter(token,
                        8, 99, //Normal
                        1, 166, //\big
                        1, 107, //\Big
                        1, 37, //\Bigg
                       1, 186, //\Bigg4
                        1, 55,//Extensible upper part
                        1, 53);//Extensible lower part
                break;
            case "lceil":
                scanBigDelimiter(token,
                        8, 100, //Normal
                        1, 167, //\big
                        1, 108, //\Big
                        1, 38, //\Bigg
                         1, 187, //\Bigg4
                        1, 50,//Extensible upper part
                        1, 54);//Extensible lower part
                break;
            case "rceil":
                scanBigDelimiter(token,
                        8, 101, //Normal
                        1, 168, //\big
                        1, 109, //\Big
                        1, 39, //\Bigg
                         1, 188, //\Bigg4
                        1, 51,//Extensible upper part
                        1, 55);//Extensible lower part
                break;

            case "langle":
                scanBigDelimiter(token,
                        8, 104, //Normal
                        1, 173, //\big
                        1, 68, //\Big
                        1, 191, //\Bigg
                        0, 0, //\Bigg4
                        1, 42,//Extensible upper part
                        1, -1);//Extensible lower part (-1=none, only upper symbol)
                break;
            case "rangle":
                scanBigDelimiter(token,
                        8, 105, //Normal
                        1, 174, //\big
                        1, 69, //\Big
                        1, 192, //\Bigg
                        0, 0, //\Bigg4
                        1, 43,//Extensible upper part
                        1, -1);//Extensible lower part (-1=none, only upper symbol)
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

    protected void scanBigDelimiter(LatexToken token,
            int cfSmall, int cSmall,
            int cfBig1, int cBig1,
            int cfBig2, int cBig2,
            int cfBig3, int cBig3,
            int cfBig4, int cBig4,
            int cfExtensibleUpper, int cExtensibleUpper,
            int cfExtensibleLower, int cExtensibleLower) {
        Box b = boxes.get(boxCounter);

        if (compareCharFont(b, cfSmall, cSmall)) {//Small delimiter
            token.secondaryFlags |= LatexToken.SEC_DELIMITER_NORMAL;
            assignedTokens.add(token);
            token.secondaryFlags &= ~LatexToken.SEC_DELIMITER_NORMAL;
            boxCounter++;
            return;
        }

        if (compareCharFont(b, cfBig1, cBig1)) {//\big delimiter
            token.secondaryFlags |= LatexToken.SEC_DELIMITER_BIG1;
            assignedTokens.add(token);
            token.secondaryFlags &= ~LatexToken.SEC_DELIMITER_BIG1;
            boxCounter++;
            return;
        }
        if (compareCharFont(b, cfBig2, cBig2)) {//\Big delimiter
            token.secondaryFlags |= LatexToken.SEC_DELIMITER_BIG2;
            assignedTokens.add(token);
            token.secondaryFlags &= ~LatexToken.SEC_DELIMITER_BIG2;
            boxCounter++;
            return;
        }
        if (compareCharFont(b, cfBig3, cBig3)) {//\Big delimiter
            token.secondaryFlags |= LatexToken.SEC_DELIMITER_BIG3;
            assignedTokens.add(token);
            token.secondaryFlags &= ~LatexToken.SEC_DELIMITER_BIG3;
            boxCounter++;
            return;
        }
        if (compareCharFont(b, cfBig4, cBig4)) {//\Big4 delimiter
            token.secondaryFlags |= LatexToken.SEC_DELIMITER_BIG4;
            assignedTokens.add(token);
            token.secondaryFlags &= ~LatexToken.SEC_DELIMITER_BIG4;
            boxCounter++;
            return;
        }

        if (compareCharFont(b, cfExtensibleUpper, cExtensibleUpper)) {//A big left upper side delimiter
            token.secondaryFlags |= LatexToken.SEC_DELIMITER_EXTENSIBLE;
            assignedTokens.add(token);
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
                token.secondaryFlags |= LatexToken.SEC_DELIMITER_EXTENSIBLE;
                assignedTokens.add(token);
                token.secondaryFlags &= ~LatexToken.SEC_DELIMITER_EXTENSIBLE;
                boxCounter++;
            }

        }
    }

}
