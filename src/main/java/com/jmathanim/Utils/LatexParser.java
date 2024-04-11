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
import com.jmathanim.jmathanim.JMathAnimScene;
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
import org.scilab.forge.jlatexmath.ArrayOfAtoms;
import org.scilab.forge.jlatexmath.Atom;
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

    public List<MiddleAtom> list;

    public final ArrayList<LatexToken> tokens;
    public final ArrayList<Box> boxes;

    public enum Modifier {
        NORMAL, TYPED
    }
    public Modifier modifier;

    public LatexParser() {
        this.list = new ArrayList<MiddleAtom>();
        this.boxes = new ArrayList<Box>();
        this.tokens = new ArrayList<>();
        this.assignedTokens = new ArrayList<>();
        this.modifier = Modifier.NORMAL;
    }

    public void parse(String texto) {
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
        if (tokens.size() == boxes.size()) {//Easy
            assignedTokens.addAll(tokens);
            return;
        }
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
        switch (token.name) {
            case "lbrack":
                scanBigDelimiter(token, 18, 40, 1, 48, 1, 64);
                break;
            case "rbrack":
                scanBigDelimiter(token, 18, 41, 1, 49, 1, 65);
                break;
            case "lbrace":
                scanBigDelimiter(token, 8, 102, 1, 56, 1, 58);
                break;
            case "rbrace":
                scanBigDelimiter(token, 8, 103, 1, 57, 1, 59);
                break;
            case "lsqbrack":
                scanBigDelimiter(token, 18, 91, 1, 50, 1, 52);
                break;
            case "rsqbrack":
                scanBigDelimiter(token, 18, 93, 1, 51, 1, 53);
                break;
            case "vert":
                token.type=LatexToken.TokenType.DELIMITER;
                scanBigDelimiter(token, 8, 106, 1, 175, 1, 175);
                break;
        }
    }

    protected void scanBigDelimiter(LatexToken token, int cfSmall, int cSmall, int cfBigUpper, int cBigUpper, int cfBigLower, int cBigLower) {
        Box b = boxes.get(boxCounter);
        if (compareCharFont(b, cfSmall, cSmall)) {//A small left parenthesis
            assignedTokens.add(token);
            boxCounter++;
        }
        if (compareCharFont(b, cfBigUpper, cBigUpper)) {//A big left upper side delimiter
            assignedTokens.add(token);
            boxCounter++;

            boolean trailStarted = false;
            while (true) {
                if (boxCounter == boxes.size()) {
                    break;
                }
                boolean found = compareCharFont(boxes.get(boxCounter), cfBigLower, cBigLower);

                if (found) {
                    trailStarted = true;
                }

                if (trailStarted & !found) {
                    break;
                }
                assignedTokens.add(token);
                boxCounter++;
            }

        }
    }

}
