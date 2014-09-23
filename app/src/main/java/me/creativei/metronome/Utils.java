package me.creativei.metronome;

import java.lang.reflect.Field;

public class Utils {
    public static int getResourcesId(String resourceId) {
        Class idClass = R.id.class;
        return getResource(resourceId, idClass);
    }

    public static int getResource(String resourceId, Class<?> idClass) {
        try {
            Field idField = idClass.getDeclaredField(resourceId);
            return idField.getInt(idField);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
