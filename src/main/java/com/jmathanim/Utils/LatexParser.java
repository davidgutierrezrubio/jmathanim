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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.scilab.forge.jlatexmath.Atom;
import org.scilab.forge.jlatexmath.BigOperatorAtom;
import org.scilab.forge.jlatexmath.CharAtom;
import org.scilab.forge.jlatexmath.EmptyAtom;
import org.scilab.forge.jlatexmath.FencedAtom;
import org.scilab.forge.jlatexmath.FractionAtom;
import org.scilab.forge.jlatexmath.NthRoot;
import org.scilab.forge.jlatexmath.RomanAtom;
import org.scilab.forge.jlatexmath.RowAtom;
import org.scilab.forge.jlatexmath.ScriptsAtom;
import org.scilab.forge.jlatexmath.SpaceAtom;
import org.scilab.forge.jlatexmath.SymbolAtom;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TypedAtom;

public class LatexParser {

    public final ArrayList<LatexToken> tokens;

    public enum Modifier {
        NORMAL, TYPED
    }
    public Modifier modifier;

    public LatexParser() {
        this.tokens = new ArrayList<>();
        this.modifier = Modifier.NORMAL;
    }

    public void parse(String texto) {
        this.tokens.clear();
        TeXFormula formula = new TeXFormula(texto);
        Atom root = formula.root;
        try {
            parseAtom(root);
        } catch (Exception ex) {
            JMathAnimScene.logger.warn("Error parsing LaTeX structure of " + texto);
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

    }

}
