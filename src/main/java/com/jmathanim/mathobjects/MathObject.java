/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mathobjects;

import java.util.Properties;
import Utils.ConfigUtils;
import Utils.Vec;
/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public abstract class MathObject implements Drawable {

    String[] DEFAULT_CONFIG = {
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
        ConfigUtils.digest_config(cnf, DEFAULT_CONFIG, configParam);
    }

    public abstract Vec getCenter();
}
