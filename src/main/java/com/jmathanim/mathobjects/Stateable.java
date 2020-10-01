/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

/**
 * Represents and object which can save and restore its state
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public interface Stateable {

    /**
     * Save the state of the current object
     */
    public void saveState();

    /**
     * Restore the previously saved state
     */
    public void restoreState();
}
