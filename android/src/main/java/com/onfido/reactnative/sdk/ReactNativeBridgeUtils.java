package com.onfido.reactnative.sdk;

import java.lang.reflect.Field;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;

/**
 * Utility methods for the SDK bridge.
 */
class ReactNativeBridgeUtiles {
    /**
     * This converts a simple object with public fields into WritableMap objects.
     * Specifically:
     * <ul>
     * <li>This does not convert arrays, Iterables, or any other complex object.</li>
     * <li>It does not use Java bean getters, or private, protected, or package fields.</li>
     * <li>This does not check for circular dependencies: A stack overflow error will occur if there are cycles.</li>
     * </ul>
     * 
     * @param o The object to convert
     * @return The object values as nested WritableMaps
     * @throws Exception if there are any errors converting the object.
     */
    public static WritableMap convertPublicFieldsToWritableMap(Object o) throws IllegalAccessException {
        WritableMap map = Arguments.createMap();

        Field[] declaredFields = o.getClass().getFields(); // NOTE: getFields gets only the public fields.
        for (Field field : declaredFields) {
            String key = field.getName();
            Object value = field.get(o);
            if (value == null) {
                // noop: Don't add null properties to the map.
            } else if (value instanceof Iterable) {
                // noop: This is currently not supported.
            } else if (value instanceof Boolean) {
                map.putBoolean(key, (Boolean) value);
            } else if (value instanceof Integer) {
                map.putInt(key, (Integer) value);
            } else if (value instanceof Double) {
                map.putDouble(key, (Double) value);
            } else if (value instanceof String)  {
                map.putString(key, (String) value);
            } else if (value instanceof Object)  {
                map.putMap(key, convertPublicFieldsToWritableMap(value));
            }
        }
        return map;
    }
}