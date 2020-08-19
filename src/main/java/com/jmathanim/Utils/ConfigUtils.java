/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Utils;

import com.jmathanim.Renderers.Renderer;
import java.util.Properties;
import java.util.Set;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class ConfigUtils {

    /**
     * Merges into cnf Properties object the values of defaultConfig (given in
     * an Array of strings {"Key", "Value", etc.} and configParam, given in a
     * Properties object
     *
     * @param cnf
     * @param defaultConfig
     * @param configParam
     * @return cnf
     */
    
    
    public static Properties digest_config_old(Properties cnf, String[] defaultConfig, Properties configParam) {
        if (configParam == null) {
            configParam = new Properties();
        }
        for (int i = 0; i < defaultConfig.length; i += 2) {
            String key = defaultConfig[i];
            String value = defaultConfig[i + 1];
            cnf.setProperty(key, value); //Load default into cnf
        }
        //Now load configParam into conf
        Set<String> keys = configParam.stringPropertyNames();
        for (String key : keys) {
            String value = configParam.getProperty(key);
            cnf.setProperty(key, value);
        }
        return cnf;
    }

    /**
     * Parse a length, given as a string, usually as a property. If length ends
     * with a "w", it means a length relative to the witdh of the screen.
     *
     * @param r
     * @param length
     * @return Length, appropiate to render in given renderer.
     */
    public static double parseLength(Renderer r, String length) {
        double resul;
        //Gets the last char of the string
        String lastchar = length.substring(length.length() - 1).toUpperCase();

        switch (lastchar) {
            case "W":
                double l1 = Double.parseDouble(length.substring(0, length.length() - 1));
                resul = r.getCamera().relScalarToWidth(l1);
                break;
            default:
                resul = Double.parseDouble(length);

        }

        return resul;

    }
}
