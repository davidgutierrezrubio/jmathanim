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

import com.jmathanim.jmathanim.JMathAnimScene;
import java.util.HashMap;

/**
 * A Hashmap subclass that ensures key strings are always uppercase
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 * @param <K> Key subclass
 * @param <V> Object subclass
 */
public class HashMapUpper<K, V> extends HashMap<String, V> {

    private final String dictName;

    public HashMapUpper(String dictName) {
        this.dictName = dictName;
    }

    @Override
    public V put(String key, V value) {
        return super.put(key.toUpperCase(), value);
    }

    @Override
    public V get(Object key) {
        if (!(key instanceof String)) {
            return null;
        }
        String name = ((String) key).toUpperCase();
        V result = super.get(name);
        if (result == null) {
            JMathAnimScene.logger.warn("Key " + name + " not found in " + dictName + " dictionary");
        }
        return result;
    }

}
