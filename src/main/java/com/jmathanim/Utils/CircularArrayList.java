/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Utils;

/**
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

        if (this.isEmpty()) {
            throw new ArrayIndexOutOfBoundsException("Circular array with no elements");
        }
        while (index >= this.size()) {
            index -= this.size();
        }

        while (index < 0) {
            index += this.size();
        }

        return super.remove(index);
    }

    @Override
    public void add(int index, E element) {

        if (!this.isEmpty()) {

            while (index >= this.size()) {
                index -= this.size();
            }

            while (index < 0) {
                index += this.size();
            }
        }

        super.add(index, element); //To change body of generated methods, choose Tools | Templates.
    }

}
