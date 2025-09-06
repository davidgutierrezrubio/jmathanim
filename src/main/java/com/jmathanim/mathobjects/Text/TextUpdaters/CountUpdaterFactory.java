package com.jmathanim.mathobjects.Text.TextUpdaters;

import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObjectGroup;
import com.jmathanim.mathobjects.Text.LatexMathObject;
import com.jmathanim.mathobjects.updaters.Updater;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;

public class CountUpdaterFactory extends TextUpdaterFactory{

    private final Object objectToCount;

    public CountUpdaterFactory(JMathAnimScene scene, LatexMathObject t, Object objectToCount, String format) {
        super(scene, format);
        this.objectToCount=objectToCount;
        this.updater=new Updater() {
            @Override
            public void update(JMathAnimScene scene) {
                t.getArg(0).setValue(getElementCount(objectToCount));
            }
        };
    }

    public Object getObjectToCount() {
        return objectToCount;
    }

    public static int getElementCount(Object obj) {
        if (obj == null) {
            return 0;
        }
        if (obj instanceof MathObjectGroup) {
            return ((MathObjectGroup)obj).size();
        }

        if (obj instanceof Collection) {
            return ((Collection<?>) obj).size();
        }

        if (obj.getClass().isArray()) {
            return Array.getLength(obj);
        }

        try {
            Method m = obj.getClass().getMethod("size");
            if (m.getParameterCount() == 0 &&
                    (m.getReturnType() == int.class || m.getReturnType() == Integer.class)) {
                return (int) m.invoke(obj);
            }
        } catch (Exception e) {
        }

        return 0;
    }
}
