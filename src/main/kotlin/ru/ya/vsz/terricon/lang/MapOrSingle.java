package ru.ya.vsz.terricon.lang;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class MapOrSingle<K, V> {
    private Set<K> keys = new HashSet<>();
    private V singleValue = null;
    private Map<K, V> delegate = new HashMap<>();

    public MapOrSingle() {
    }

    public MapOrSingle(Collection<K> keys) {
        this.keys.addAll(keys);
    }

    @SafeVarargs
    public MapOrSingle(K... keys) {
        this(Arrays.asList(keys));
    }

    public void setKeys(Collection<K> keys) {
        this.keys.clear();
        this.keys.addAll(keys);
    }

    @SafeVarargs
    public final void setKeys(K... keys) {
        setKeys(Arrays.asList(keys));
    }

    public V get(K key) {
        if (singleValue != null) {
            return singleValue;
        } else {
            if (!delegate.containsKey(key)) {
                throw new IllegalArgumentException(
                    format("Value for key '%s' is not defined", key)
                );
            }
            return delegate.get(key);
        }
    }

    public void setSingleValue(V value) {
        if (!delegate.isEmpty()) {
            throw new IllegalArgumentException(
                format("Values for keys '%s' already defined", delegate.keySet())
            );
        }
        if (singleValue != null && !singleValue.equals(value)) {
            throw new IllegalArgumentException(
                format("Different value for all keys already defined (%s)", singleValue));
        }
        this.singleValue = value;
    }

    public void put(K key, V value) {
        if (singleValue != null) {
            throw new IllegalArgumentException(
                format("Value for all keys already defined (%s)", singleValue)
            );
        }
        if (delegate.containsKey(key) && !delegate.get(key).equals(value)) {
            throw new IllegalArgumentException(
                format("Value for key '%s' already defined (%s)",
                    key,
                    delegate.get(key)
                )
            );
        }
        if (!keys.contains(key)) {
            throw new IllegalArgumentException(
                format("Key '%s' is not defined, add it to 'setKeys' call", key)
            );
        }
        delegate.put(key, value);
    }

    public void assertDefined() {
        if (singleValue != null) {
            return;
        }
        if (delegate.isEmpty()) {
            throw new IllegalArgumentException(
                "Values not defined. Call 'setSingleValue' or 'put'"
            );
        }
        if (!delegate.keySet().containsAll(keys)) {
            List<K> undefinedKeys = keys.stream().filter(
                s -> !delegate.keySet().contains(s)
            ).collect(Collectors.toList());
            throw new IllegalArgumentException(
                format("Value for keys '%s' is not defined", undefinedKeys)
            );
        }
    }

    public Collection<K> getKeysByValue(V value) {
        if (value == null) {
            return Collections.emptyList();
        }
        if (singleValue != null && singleValue.equals(value)) {
            if (keys.isEmpty()) {
                return Collections.singletonList(null);
            } else {
                return keys;
            }
        } else if (delegate.containsValue(value)) {
            final Set<K> result = new HashSet<>();
            delegate.forEach((k, v) -> {
                if (value.equals(v)) {
                    result.add(k);
                }
            });
            return result;
        }
        return Collections.emptyList();
    }

    public static void main(String[] args) {
        MapOrSingle<String, String> m = new MapOrSingle<>("A", "B", "C");
        m.put("A", "P");
        m.put("B", "Q");
        m.put("C", "P");

        m.assertDefined();
        System.out.println(m.getKeysByValue("P")); // [A, C]

        MapOrSingle<String, String> n = new MapOrSingle<>("A", "B", "C");
        n.setSingleValue("P");

        n.assertDefined();
        System.out.println(n.getKeysByValue("P")); // [A, B, C]

        MapOrSingle<String, String> o = new MapOrSingle<>();
        o.setSingleValue("P");

        o.assertDefined();
        for (String ignored : o.getKeysByValue("P")) {
            System.out.println("P");
        } // P
    }
}
