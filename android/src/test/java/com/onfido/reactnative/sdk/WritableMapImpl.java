package com.onfido.reactnative.sdk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.facebook.react.bridge.Dynamic;
import com.facebook.react.bridge.DynamicFromMap;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WritableMapImpl implements WritableMap {
    private final Map<String, Object> map = new HashMap<>();

    @Override
    public void putNull(@NonNull String key) {
        map.put(key, null);
    }

    @Override
    public void putBoolean(@NonNull String key, boolean value) {
        map.put(key, value);
    }

    @Override
    public void putDouble(@NonNull String key, double value) {
        map.put(key, value);
    }

    @Override
    public void putInt(@NonNull String key, int value) {
        map.put(key, value);
    }

    @Override
    public void putString(@NonNull String key, @Nullable String value) {
        map.put(key, value);
    }

    @Override
    public void putArray(@NonNull String s, @Nullable ReadableArray readableArray) {
        map.put(s, readableArray);
    }

    @Override
    public void putMap(@NonNull String key, @Nullable ReadableMap value) {
        map.put(key, value);
    }

    @Override
    public void merge(@NonNull ReadableMap source) {
        if (source instanceof WritableMapImpl) {
            map.putAll(((WritableMapImpl) source).map);
        }
    }

    @Override
    public WritableMap copy() {
        WritableMapImpl copy = new WritableMapImpl();
        copy.map.putAll(this.map);
        return copy;
    }

    @Override
    public boolean hasKey(@NonNull String key) {
        return map.containsKey(key);
    }

    @Override
    public boolean isNull(@NonNull String key) {
        return map.get(key) == null;
    }

    @Override
    public boolean getBoolean(@NonNull String key) {
        Object value = map.get(key);
        return value instanceof Boolean ? (Boolean) value : false;
    }

    @Override
    public double getDouble(@NonNull String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return 0.0;
    }

    @Override
    public int getInt(@NonNull String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return 0;
    }

    @Override
    @Nullable
    public String getString(@NonNull String key) {
        Object value = map.get(key);
        return value instanceof String ? (String) value : null;
    }

    @Override
    @Nullable
    public ReadableArray getArray(@NonNull String key) {
        Object value = map.get(key);
        return value instanceof ReadableArray ? (ReadableArray) value : null;
    }

    @Override
    @Nullable
    public ReadableMap getMap(@NonNull String key) {
        Object value = map.get(key);
        return value instanceof ReadableMap ? (ReadableMap) value : null;
    }

    @NonNull
    @Override
    public Dynamic getDynamic(@NonNull String key) {
        return DynamicFromMap.create(this, key);
    }

    @NonNull
    @Override
    public ReadableType getType(@NonNull String key) {
        Object value = map.get(key);
        if (value == null) {
            return ReadableType.Null;
        } else if (value instanceof Boolean) {
            return ReadableType.Boolean;
        } else if (value instanceof Number) {
            return ReadableType.Number;
        } else if (value instanceof String) {
            return ReadableType.String;
        } else if (value instanceof ReadableArray) {
            return ReadableType.Array;
        } else if (value instanceof ReadableMap) {
            return ReadableType.Map;
        }
        return ReadableType.Null;
    }

    @NonNull
    @Override
    public Iterator<Map.Entry<String, Object>> getEntryIterator() {
        return map.entrySet().iterator();
    }

    @NonNull
    @Override
    public ReadableMapKeySetIterator keySetIterator() {
        return new ReadableMapKeySetIterator() {
            private final Iterator<String> iterator = map.keySet().iterator();

            @Override
            public boolean hasNextKey() {
                return iterator.hasNext();
            }

            @Override
            public String nextKey() {
                return iterator.next();
            }
        };
    }

    @NonNull
    @Override
    public HashMap<String, Object> toHashMap() {
        return new HashMap<>(map);
    }
}