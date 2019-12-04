/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.util.Properties;
import java.util.Set;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class ConfigUtils {

    /**
     * Merges into cnf Properties object the values of defaultConfig (given in
     * an Array of strings {"Key", "Value", etc.} and configParam, given in
     * a Properties object
     * @param cnf
     * @param defaultConfig
     * @param configParam
     * @return cnf
     */
    public static Properties digest_config(Properties cnf,String[] defaultConfig,Properties configParam) {
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
}
