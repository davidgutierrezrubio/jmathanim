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
 * Base class for a link between 2 or more objects
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class Link {
    
    public Link() {
    }

    /**
     * Apply current link
     *
     * @return True if link has been applied successfully. False otherwise.
     */
    public abstract boolean apply();
    
}
