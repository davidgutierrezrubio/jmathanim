/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Utils.ConfigUtils;
import com.jmathanim.Utils.Vec;
import java.util.Properties;
/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public abstract class MathObject implements Drawable {

    String[] DEFAULT_CONFIG_MATHOBJECT = {
        "VISIBLE", "TRUE",
        "ALPHA", "1",
        "COLOR","255"
    };
    Properties cnf;

    public MathObject() {
        this(null);
    }

    public MathObject(Properties configParam) {
        cnf = new Properties();
        ConfigUtils.digest_config(cnf, DEFAULT_CONFIG_MATHOBJECT, configParam);
    }

    public abstract Vec getCenter();
}
