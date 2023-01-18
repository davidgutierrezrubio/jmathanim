/*
 * Copyright (C) 2020 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
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
package com.jmathanim.Utils;

/**
 * An ArrayList where elements can be accesed cyclicly. get(size()+k) returns
 * get(k)
 *
 * @author David
 */
import java.util.ArrayList;

public class CircularArrayList<E> extends ArrayList<E> {

    private static final long serialVersionUID = 1L;

    @Override
    public E get(int index) throws ArrayIndexOutOfBoundsException {

        if (this.isEmpty()) {
            throw new ArrayIndexOutOfBoundsException("Circular array with no elements");
        }
        while (index >= this.size()) {
            index -= this.size();
        }

        while (index < 0) {
            index += this.size();
        }

        return super.get(index);
    }

    @Override
    public E remove(int index) throws ArrayIndexOutOfBoundsException {
        int size = this.size();
        if (this.isEmpty()) {
            throw new ArrayIndexOutOfBoundsException("Circular array with no elements");
        }
        while (index >= size) {
            index -= size;
        }

        while (index < 0) {
            index += size;
        }

        return super.remove(index);
    }

    @Override
    public void add(int index, E element) {
        int size = this.size();
        if (!this.isEmpty()) {

            while (index >= size) {
                index -= size;
            }

            while (index < 0) {
                index += size;
            }
        }

        super.add(index, element); // To change body of generated methods, choose Tools | Templates.
    }

}
