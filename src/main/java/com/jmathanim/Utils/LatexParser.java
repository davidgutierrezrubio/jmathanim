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
import org.scilab.forge.jlatexmath.MatrixAtom;
import org.scilab.forge.jlatexmath.MiddleAtom;
import org.scilab.forge.jlatexmath.NthRoot;
import org.scilab.forge.jlatexmath.RomanAtom;
import org.scilab.forge.jlatexmath.RowAtom;
import org.scilab.forge.jlatexmath.ScriptsAtom;
import org.scilab.forge.jlatexmath.SpaceAtom;
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
    private final LaTeXMathObject latex;

    public List<MiddleAtom> list;

    public final ArrayList<LatexToken> tokens;
    public final ArrayList<Box> boxes;

    public enum Modifier {
        NORMAL, TYPED
    }
    public Modifier modifier;

    public LatexParser(LaTeXMathObject latex) {
        this.latex = latex;
        this.list = new ArrayList<MiddleAtom>();
        this.boxes = new ArrayList<Box>();
        this.tokens = new ArrayList<>();
        this.assignedTokens = new ArrayList<>();
        this.modifier = Modifier.NORMAL;
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

    public void colorizeTokens(JMColor color, LatexToken.TokenType tokenType, LatexToken.DelimiterType delType, String name) {
        Integer[] indices = getMatchingIndices(new LatexToken(tokenType, delType, name, true));
        for (Integer indice : indices) {
            latex.get(indice).color(color);
        }
    }

    public void parse() {
        String texto = this.latex.getText();
        this.tokens.clear();
        this.assignedTokens.clear();
        TeXFormula formula = new TeXFormula(texto);
        TeXIcon icon = formula.createTeXIcon(TeXConstants.ALIGN_LEFT, 40);

        Atom root = formula.root;
        try {
            parseAtom(root);
        } catch (Exception ex2) {
            JMathAnimScene.logger.warn("Error parsing LaTeX structure of " + texto);
        }

        Field campo;
        try {

            DefaultTeXFont font = new DefaultTeXFont(40);
            TeXEnvironment te = new TeXEnvironment(0, font);

            Class[] cArg = new Class[1];
            cArg[0] = TeXEnvironment.class;
            Method metodo = TeXFormula.class.getDeclaredMethod("createBox", cArg);

            metodo.setAccessible(true);
            Box bo = (Box) metodo.invoke(formula, te);
            parseBox(bo);
            assignTokens();
            System.out.println("Parse finished");
        } catch (Exception ex) {
            Logger.getLogger(LatexParser.class.getName()).log(Level.SEVERE, null, ex);

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

        JMathAnimScene.logger.info("Parsing " + atom.getClass().getCanonicalName());

        if (atom instanceof EmptyAtom) {
            //Nothing to do here, remove this code later...
        }

        if (atom instanceof RowAtom) {
            RowAtom rowAtom = (RowAtom) atom;
            campo = RowAtom.class.getDeclaredField("elements");
            campo.setAccessible(true);
            LinkedList<Atom> valor = (LinkedList<Atom>) campo.get(rowAtom);
            for (Atom atom1 : valor) {
                parseAtom(atom1);

            }
        }
        if (atom instanceof CharAtom) {
            CharAtom charAtom = (CharAtom) atom;
            LatexToken.TokenType type = LatexToken.TokenType.CHAR;
            switch (modifier) {
                case TYPED:
                    type = LatexToken.TokenType.NAMED_FUNCTION;
                    break;
            }
            tokens.add(new LatexToken(type, "" + charAtom.getCharacter(), charAtom.isMathMode()));//TODO: IMPROVE
        }
        if (atom instanceof SymbolAtom) {
            SymbolAtom symbolAtom = (SymbolAtom) atom;
            tokens.add(new LatexToken(LatexToken.TokenType.SYMBOL, "" + symbolAtom.getName(), true));//TODO: IMPROVE
        }

        if (atom instanceof BigDelimiterAtom) {
            BigDelimiterAtom bigDelimiterAtom = (BigDelimiterAtom) atom;
            tokens.add(new LatexToken(LatexToken.TokenType.SYMBOL, "" + bigDelimiterAtom.delim.getName(), true));//TODO: IMPROVE
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
        }

        if (atom instanceof NthRoot) {
            NthRoot nthRoot = (NthRoot) atom;

            campo = NthRoot.class.getDeclaredField("root");
            campo.setAccessible(true);
            parseAtom(campo.get(nthRoot));

            tokens.add(new LatexToken(LatexToken.TokenType.SQRT, "sqrt", true));//TODO: IMPROVE
            tokens.add(new LatexToken(LatexToken.TokenType.SQRT, "sqrt", true));//TODO: IMPROVE

            campo = NthRoot.class.getDeclaredField("base");
            campo.setAccessible(true);
            parseAtom(campo.get(nthRoot));
        }

        if (atom instanceof FractionAtom) {
            FractionAtom fractionAtom = (FractionAtom) atom;

            campo = FractionAtom.class.getDeclaredField("numerator");
            campo.setAccessible(true);
            parseAtom(campo.get(fractionAtom));

            tokens.add(new LatexToken(LatexToken.TokenType.FRACTIONBAR, "fractionRule", true));//TODO: IMPROVE

            campo = FractionAtom.class.getDeclaredField("denominator");
            campo.setAccessible(true);
            parseAtom(campo.get(fractionAtom));
        }

        if (atom instanceof TypedAtom) {
            TypedAtom typedAtom = (TypedAtom) atom;
            modifier = Modifier.TYPED;
            Atom aa = typedAtom.getBase();
            parseAtom(aa);
            modifier = Modifier.NORMAL;
        }

        if (atom instanceof RomanAtom) {
            RomanAtom romanAtom = (RomanAtom) atom;
            campo = RomanAtom.class.getDeclaredField("base");
            campo.setAccessible(true);
            parseAtom(campo.get(romanAtom));
        }

        if (atom instanceof ScriptsAtom) {
            ScriptsAtom scriptsAtom = (ScriptsAtom) atom;
            campo = ScriptsAtom.class.getDeclaredField("base");
            campo.setAccessible(true);
            parseAtom(campo.get(scriptsAtom));

            campo = ScriptsAtom.class.getDeclaredField("superscript");
            campo.setAccessible(true);
            parseAtom(campo.get(scriptsAtom));

            campo = ScriptsAtom.class.getDeclaredField("subscript");
            campo.setAccessible(true);
            parseAtom(campo.get(scriptsAtom));
        }

        if (atom instanceof BigOperatorAtom) {
            BigOperatorAtom bigOperatorAtom = (BigOperatorAtom) atom;

            campo = BigOperatorAtom.class.getDeclaredField("over");
            campo.setAccessible(true);
            parseAtom((Atom) campo.get(bigOperatorAtom));

            campo = BigOperatorAtom.class.getDeclaredField("base");
            campo.setAccessible(true);
            parseAtom((Atom) campo.get(bigOperatorAtom));

            campo = BigOperatorAtom.class.getDeclaredField("under");
            campo.setAccessible(true);
            parseAtom((Atom) campo.get(bigOperatorAtom));
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
        }

    }

    private void parseBox(Box bo) throws Exception {
        Field campo;
//        System.out.println("Parsing Box: "+bo.getClass().getCanonicalName()+"  "+bo);

        if (bo instanceof CharBox) {
            CharBox charBox = (CharBox) bo;
            CharFont cf = getFontFromCharBox(charBox);
            System.out.println((boxes.size()) + "   Char: " + cf.fontId + " " + (int) cf.c);
            boxes.add(charBox);
        }

        if (bo instanceof org.scilab.forge.jlatexmath.HorizontalRule) {
            System.out.println((boxes.size()) + "   Horizontal rule");
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
            System.out.println("Intentado extraer font de algo que no es charbox!");
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
                case CHAR, FRACTIONBAR, GREEKLETTER, NAMED_FUNCTION, OPERATOR, RELATION, NUMBER:
                    //These are supposed to be the "easy tokens"
                    assignedTokens.add(token);
                    boxCounter++;
                    break;
                case DELIMITER:
                    processDelimiter(token);
                    break;
                case SYMBOL:
                    processSymbol(token);

            }

        }
    }

    private void processSymbol(LatexToken token) {
        switch (token.name) {
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
        return ((cf.fontId == fontId) & (cf.c == (char) c));
    }

    private void processDelimiter(LatexToken token) {
        //TODO: Add ALL delimiters: https://docs.aspose.com/tex/java/latex-math-delimiters/
        //Para añadir delimiter nuevo: incluirlo en array delimiters de LatexToken
        switch (token.name) {
            case "lbrack":
                scanBigDelimiter(token,
                        18, 40, //Normal 
                        1, 161, //\big 
                        1, 179, //\Big
                        1, 48,//Extensible upper part
                        1, 64);//Extensible lower part
                break;
            case "rbrack":
                scanBigDelimiter(token,
                        18, 41, //Normal
                        1, 162, //\big
                        1, 180, //\Big
                        1, 49, //Extensible upper part
                        1, 65);//Extensible lower part
                break;
            case "lbrace":
                scanBigDelimiter(token,
                        8, 102, //Normal
                        1, 169, //\big
                        1, 110, //\Big
                        1, 56, //Extensible upper part
                        1, 58);//Extensible lower part
                break;
            case "rbrace":
                scanBigDelimiter(token,
                        8, 103, //Normal
                        1, 170, //\big
                        1, 111, //\Big
                        1, 57, //Extensible upper part
                        1, 59);//Extensible lower part
                break;
            case "lsqbrack":
                scanBigDelimiter(token,
                        18, 91, //Normal
                        1, 163, //\big
                        1, 104, //\Big
                        1, 50,//Extensible upper part
                        1, 52);//Extensible lower part
                break;
            case "rsqbrack":
                scanBigDelimiter(token,
                        18, 93, //Normal
                        1, 164, //\big
                        1, 105, //\Big
                        1, 51,//Extensible upper part
                        1, 53);//Extensible lower part
                break;
            case "vert":
                token.type = LatexToken.TokenType.DELIMITER;
                scanBigDelimiter(token,
                        8, 106,//Normal (LR)
                        0, 0, //\big (LR)
                        0, 0, //\Big(LR)
                        1, 175,//Extensible upper part
                        1, 175);//Extensible lower part
                break;
                 case "Vert":
                token.type = LatexToken.TokenType.DELIMITER;
                scanBigDelimiter(token,
                        8, 107,//Normal (LR)
                        0, 0, //\big (LR)
                        0, 0, //\Big(LR)
                        1, 176,//Extensible upper part
                        1, 176);//Extensible lower part
                break;
            case "lfloor":
                scanBigDelimiter(token,
                        8, 98, //Normal
                        1, 165, //\big
                        1, 106, //\Big
                        1, 54,//Extensible upper part
                        1, 52);//Extensible lower part
                break;
            case "rfloor":
                scanBigDelimiter(token,
                        8, 99, //Normal
                        1, 166, //\big
                        1, 107, //\Big
                        1, 55,//Extensible upper part
                        1, 53);//Extensible lower part
                break;
            case "lceil":
                scanBigDelimiter(token,
                        8, 100, //Normal
                        1, 167, //\big
                        1, 108, //\Big
                        1, 50,//Extensible upper part
                        1, 54);//Extensible lower part
                break;
            case "rceil":
                scanBigDelimiter(token,
                        8, 101, //Normal
                        1, 168, //\big
                        1, 109, //\Big
                        1, 51,//Extensible upper part
                        1, 55);//Extensible lower part
                break;

            case "langle":
                scanBigDelimiter(token,
                        8, 104, //Normal
                        1, 173, //\big
                        1, 68, //\Big
                        1, 42,//Extensible upper part
                        1, -1);//Extensible lower part
                break;
            case "rangle":
                scanBigDelimiter(token,
                        8, 105, //Normal
                        1, 174, //\big
                        1, 69, //\Big
                        1, 43,//Extensible upper part
                        1, -1);//Extensible lower part
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
            int cfExtensibleUpper, int cExtensibleUpper,
            int cfExtensibleLower, int cExtensibleLower) {
        Box b = boxes.get(boxCounter);

        if (compareCharFont(b, cfSmall, cSmall)) {//Small delimiter
            token.delimiterType = LatexToken.DelimiterType.NORMAL;
            assignedTokens.add(token);
            boxCounter++;
            return;
        }

        if (compareCharFont(b, cfBig1, cBig1)) {//\big delimiter
            token.delimiterType = LatexToken.DelimiterType.BIG1;
            assignedTokens.add(token);
            boxCounter++;
            return;
        }
        if (compareCharFont(b, cfBig2, cBig2)) {//\Big delimiter
            token.delimiterType = LatexToken.DelimiterType.BIG2;
            assignedTokens.add(token);
            boxCounter++;
            return;
        }

        if (compareCharFont(b, cfExtensibleUpper, cExtensibleUpper)) {//A big left upper side delimiter
            token.delimiterType = LatexToken.DelimiterType.EXTENSIBLE;
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
                token.delimiterType = LatexToken.DelimiterType.EXTENSIBLE;
                assignedTokens.add(token);
                boxCounter++;
            }

        }
    }

}
