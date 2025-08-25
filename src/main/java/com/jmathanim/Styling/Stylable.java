/*
 * Copyright (C) 2022 David
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
package com.jmathanim.Styling;

import com.jmathanim.Enum.DashStyle;

/**
 *
 * @author David
 */
public interface Stylable<T extends Stylable<T>> {
     DrawStyleProperties getMp();

     /**
      * Sets the draw color of the object
      *
      * @param dc A JMcolor object with the draw color
      * @return The MathObject subclass
      */
     default T drawColor(PaintStyle<?> dc) {
          getMp().setDrawColor(dc);
          return (T) this;
     }

     /**
      * Sets the draw color of the object. Overloaded method.
      *
      * @param str A string representing the draw color, as in the JMcolor.parse method
      * @return The MathObject subclass
      */
     default T drawColor(String str) {
          drawColor(JMColor.parse(str));
          return (T) this;
     }
     /**
      * Sets the alpha component of the draw color
      *
      * @param alpha Alpha value, between 0 (transparent) and 1 (opaque)
      * @return The MathObject subclass
      */
     default T drawAlpha(double alpha) {
          getMp().setDrawAlpha(alpha);
          return (T) this;
     }

     /**
      * Sets the alpha component of the fill color
      *
      * @param alpha Alpha value, between 0 (transparent) and 1 (opaque)
      * @return This MathObject subclass
      */
     default T fillAlpha(double alpha) {
          getMp().setFillAlpha(alpha);
          return (T) this;
     }

     /**
      * Sets the thickness to draw the contour of the object
      *
      * @param newThickness Thickness
      * @return This MathObject subclass
      */
     default T thickness(double newThickness) {
          getMp().setThickness(newThickness);
          return (T) this;
     }

     /**
      * Sets the dashStyle, from one of the types defined in the enum DashStyle
      *
      * @param dashStyle A value from enum DashStyle
      * @return This MathObject subclass
      */
     default T dashStyle(DashStyle dashStyle) {
          getMp().setDashStyle(dashStyle);
          return (T) this;
     }

     /**
      * Sets the flag visible. If false, the object won't be draw using the renderer, although it still will be in the
      * scene.
      *
      * @param visible True if objet is visible, false otherwise
      * @return This MathObject subclass
      */
     default T visible(boolean visible) {
          getMp().setVisible(visible);
          return (T) this;
     }

     /**
      * Changes both draw and fill color
      *
      * @param dc A PaintStyle object. Can be a JMColor, a gradient or image pattern
      * @return This object
      */
     default T color(PaintStyle dc) {
          drawColor(dc);
          fillColor(dc);
          return (T) this;
     }

     /**
      * Overloaded method. Sets both draw and fill color
      *
      * @param str A string representing the draw color, as in the JMcolor.parse method
      * @return This object
      */
     default T color(String str) {
          drawColor(str);
          fillColor(str);
          return (T) this;
     }


     /**
      * Sets the fill color of the object
      *
      * @param fc A JMcolor object with the fill color
      * @return The MathObject subclass
      */
     default T fillColor(PaintStyle fc) {
          getMp().setFillColor(fc);
          return (T) this;
     }

     /**
      * Sets the fill color of the object. Overloaded method.
      *
      * @param str A string representing the fill color, as in the JMcolor.parse method
      * @return The MathObject subclass
      */
     default T fillColor(String str) {
          fillColor(JMColor.parse(str));
          return (T) this;
     }
}
