/*
 * Copyright (C) 2023 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
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
package com.jmathanim.mathobjects;

import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.Styling.PaintStyle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public interface StyleHookable {

    /**
     * Called when draw color is changed
     *
     * @param color New color
     */
    public void on_setDrawColor(PaintStyle color);

    /**
     * Called when fill color is changed
     *
     * @param color New color
     */
    public void on_setFillColor(PaintStyle color);

    /**
     * Called when draw alpha is changed
     *
     * @param alpha New alpha value
     */
    public void on_setDrawAlpha(double alpha);

    /**
     * Called when fill alpha is changed
     *
     * @param alpha New alpha value
     */
    public void on_setFillAlpha(double alpha);

    /**
     * Called when thickness is changed.
     *
     * @param thickness New thickness value
     */
    public void on_setThickness(double thickness);

    /**
     * Called when visible flag is changed
     *
     * @param visible The new visible flag value
     */
    public void on_setVisible(boolean visible);

    /**
     * Called when dash style is changed
     *
     * @param style New dash style
     */
    public void on_setDashStyle(MODrawProperties.DashStyle style);

    public void on_setLineCap(StrokeLineCap linecap);

    public void on_setLineJoin(StrokeLineJoin linejoin);
}
