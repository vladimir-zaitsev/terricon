package ru.ya.vsz.terricon.lang;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class StableSorter {
    private StableSorter() {
    }

    public static <T, K1, K2> List<T> sort(
        List<T> input,
        Function<T, K1> orderingKey1, K1[] keys1,
        Function<T, K2> orderingKey2, K2[] keys2
    ) {
        Map<K1, List<T>> k1Map = new HashMap<>();
        Map<K2, List<T>> k2Map = new HashMap<>();

        // раскладываем по ключам
        for (T value : input) {
            K1 k1 = orderingKey1.apply(value);
            if (k1 != null) {
                k1Map.computeIfAbsent(k1, __ -> new ArrayList<>()).add(value);
            }
            K2 k2 = orderingKey2.apply(value);
            if (k2 != null) {
                k2Map.computeIfAbsent(k2, __ -> new ArrayList<>()).add(value);
            }
        }
        List<T> result = new ArrayList<>(input.size());

        // параллельный цикл по двум наборам
        int i1 = 0;
        int i2 = 0;
        while (i1 < keys1.length
            || i2 < keys2.length
            ) {
            //noinspection Duplicates
            if (i1 < keys1.length) {
                List<T> listByK1 = k1Map.get(
                    keys1[i1]
                );
                if (listByK1 != null) {
                    result.addAll(listByK1);
                }
                i1++;
            }
            //noinspection Duplicates
            if (i2 < keys2.length) {
                List<T> listByK2 = k2Map.get(
                    keys2[i2]
                );
                if (listByK2 != null) {
                    result.addAll(listByK2);
                }
                i2++;
            }
        }

        return result;
    }

    public static <T> List<T> sort(List<T> input, Function<T, Integer> indexFunction) {
        final Map<Integer, List<T>> indexMap = new HashMap<>();
        for (T value : input) {
            indexMap.computeIfAbsent(
                indexFunction.apply(value),
                __ -> new ArrayList<>()
            ).add(value);
        }
        List<T> result = new ArrayList<>(input.size());
        for (List<T> part : indexMap.values()) {
            result.addAll(part);
        }
        return result;
    }

    // Test

    private enum K1 {
        A, B, C
    }

    private enum K2 {
        AA, BB, CC
    }

    private static class U {
        private static final AtomicInteger U_COUNT = new AtomicInteger();

        final int id = U_COUNT.incrementAndGet();
        final K1 k1;
        final K2 k2;

        U(K1 k1, K2 k2) {
            this.k1 = k1;
            this.k2 = k2;
        }

        @Override
        public String toString() {
            return (k1 != null ? k1.toString() : k2.toString()) + id;
        }
    }

    private static U u(K1 k1) {
        return new U(k1, null);
    }

    private static U u(K2 k2) {
        return new U(null, k2);
    }

    public static void main(String[] args) {
        List<U> list = Arrays.asList(u(K2.CC), u(K1.B), u(K2.AA), u(K1.C), u(K1.B));
        List<U> sorted = sort(list, u -> u.k1, K1.values(), u -> u.k2, K2.values());
        System.out.println(sorted);
        List<U> sorted2 = sort(list, u -> (u.k1 != null ? u.k1.ordinal() : u.k2.ordinal()));
        System.out.println(sorted2);
    }
}
