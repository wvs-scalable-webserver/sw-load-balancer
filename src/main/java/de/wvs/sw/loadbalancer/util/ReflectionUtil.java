package de.wvs.sw.loadbalancer.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Marvin Erkes on 04.02.2020.
 */
public final class ReflectionUtil {

    private static Logger logger = LoggerFactory.getLogger(ReflectionUtil.class);

    private ReflectionUtil() {
        // No instance
    }

    public static void setAtomicLong(Object object, String field, long value) {

        try {
            Field f = object.getClass().getDeclaredField(field);
            f.setAccessible(true);
            ((AtomicLong) f.get(object)).set(value);
            f.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
