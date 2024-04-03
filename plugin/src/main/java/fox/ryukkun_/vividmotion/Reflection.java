package fox.ryukkun_.vividmotion;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class Reflection {
    public static Method findMethod(Class<?> source, Class<?> _return, Class<?>... find) {
        for (Method m : source.getMethods()) {
            if (Arrays.equals(m.getParameterTypes(), find) && m.getReturnType() == _return) {
                return m;
            }
        }
        return null;
    }


    public static Field findField(Class<?> source, Class<?> find) {
        for (Field f : source.getFields()) {
            if (f.getType() == find) {
                return f;
            }
        }
        return null;
    }
}
