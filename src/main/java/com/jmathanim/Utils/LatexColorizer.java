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

import com.jmathanim.mathobjects.Text.AbstractLaTeXMathObject;
import java.util.ArrayList;

/**
 * This class assign colors to elements of LaTeXMathObject, according to their identifying tokens
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class LatexColorizer {
    private final ArrayList<LatexColorizerItem> colorizerItems;

    public static LatexColorizer make() {
        return new LatexColorizer();
    }
    
    public LatexColorizer() {
        this.colorizerItems = new ArrayList<>();
    }
    
     public LatexColorizer setColorTo(LatexToken.TokenType type,Integer secType,String name, String colorName) {
       LatexColorizerItem  colorizerItem=new LatexColorizerItem();
       colorizerItem.tokenEq=new LatexToken(type,secType,name);
       colorizerItem.setColor(colorName);
        colorizerItems.add(colorizerItem);
        return this;
    }
     
      public LatexColorizer setColorToChar(String name, String colorName) {
       LatexColorizerItem  colorizerItem=new LatexColorizerItem();
       colorizerItem.tokenEq=new LatexToken(LatexToken.TokenType.CHAR,name);
       colorizerItem.setColor(colorName);
        colorizerItems.add(colorizerItem);
        return this;
    }
    public void apply(AbstractLaTeXMathObject latex) {
        for (LatexColorizerItem colorizerItem : colorizerItems) {
            colorizerItem.apply(latex);
            
        }
    }

    public boolean add(LatexColorizerItem e) {
        return colorizerItems.add(e);
    }
    
}
