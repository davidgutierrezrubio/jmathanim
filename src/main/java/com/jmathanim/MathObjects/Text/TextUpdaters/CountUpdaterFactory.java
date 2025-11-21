package com.jmathanim.MathObjects.Text.TextUpdaters;

import com.jmathanim.MathObjects.MathObjectGroup;
import com.jmathanim.MathObjects.Text.LatexMathObject;
import com.jmathanim.MathObjects.Updaters.Updater;
import com.jmathanim.jmathanim.JMathAnimScene;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;

public class CountUpdaterFactory extends TextUpdaterFactory{

    private final Object objectToCount;

    public CountUpdaterFactory(LatexMathObject t, Object objectToCount, String format) {
        super(t,format);
        this.objectToCount=objectToCount;
        this.addUpdater(new Updater() {


            @Override
            public void applyAfter() {

            }
            @Override
            public void applyBefore() {
                t.getArg(0).setValue(getElementCount(objectToCount));
            }
        });
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
