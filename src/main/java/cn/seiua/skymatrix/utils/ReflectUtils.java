package cn.seiua.skymatrix.utils;

import cn.seiua.skymatrix.client.component.SModule;
import com.alibaba.fastjson.annotation.JSONField;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReflectUtils {

    private static boolean withAnnotation1(Class c, Class target) {

        for (Annotation an : c.getAnnotations()) {
            if (an.annotationType() == target) {
                return true;
            }

            if (!(an instanceof Retention || an instanceof Target || an instanceof Documented)) {
                if (c.getName().contains("kotlin")) {
                    return false;
                }
                return withAnnotation1(an.annotationType(), target);
            }

        }
        return false;
    }

    public static boolean withAnnotation(Class c, Class target) {
        if (c == null || target == null) {
            throw new NullPointerException("NMSL");
        }
        if (!c.isInterface()) {
            return withAnnotation1(c, target);
        }
        return false;
    }

    public static List<Object> IsCoincidentWith(HashMap<Class, Object> from, Class target) {
        if (from == null || target == null) {
            throw new NullPointerException("NMSL");
        }
        List<Object> ret = new ArrayList<>();
        for (Class key : from.keySet()) {
            if (target == key || target.isAssignableFrom(key)) {
                ret.add(from.get(key));
            }
        }

        return ret.size() == 0 ? null : ret;


    }

    public static String getModuleName(Class c) {
        SModule module = (SModule) c.getAnnotation(SModule.class);

        return module.category() + "." + module.name();
    }

    public static String getModuleName(Object o) {
        return getModuleName(o.getClass());
    }

    public static void copyData(Object o, Object oo) {
        try {
            if (o.getClass() == oo.getClass()) {
                for (Field f : o.getClass().getDeclaredFields()) {
                    if (f.getAnnotation(JSONField.class) == null) continue;
                    f.setAccessible(true);
                    Object obj = f.get(oo);
                    Object obj1 = f.get(o);
                    // map
                    if (obj1 instanceof Map<?, ?>) {
                        Map mobj = (Map) obj;
                        ((Map) obj1).putAll(mobj);
                        continue;
                    }
                    if (obj != null) f.set(o, f.get(oo));

                }
            } else {
                throw new RuntimeException("NMSL");

            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
