package ru.ya.vsz.terricon.lang;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;

public class LongMapCollector<T> implements Collector<T, Long2ObjectMap<T>, Long2ObjectMap<T>> {
    static final Set<Characteristics> CH_ID
        = Collections.unmodifiableSet(EnumSet.of(Characteristics.IDENTITY_FINISH));

    private int initialSize = Long2ObjectOpenHashMap.DEFAULT_INITIAL_SIZE;
    private Supplier<Long2ObjectMap<T>> supplier;
    private BiConsumer<Long2ObjectMap<T>, T> accumulator;
    private BinaryOperator<Long2ObjectMap<T>> combiner;
    private Function<Long2ObjectMap<T>, Long2ObjectMap<T>> finisher;

    public LongMapCollector(ToLongFunction<T> keyFunction) {
        this((map, m) -> map.put(keyFunction.applyAsLong(m), m));
    }

    public LongMapCollector(BiConsumer<Long2ObjectMap<T>, T> accumulator) {
        this.accumulator = accumulator;
        supplier = () -> new Long2ObjectOpenHashMap<>(initialSize);
        combiner = (m1, m2) -> {
            for (Long2ObjectMap.Entry<T> e :m1.long2ObjectEntrySet()) {
                m1.put(e.getLongKey(), e.getValue());
            }
            return m1;
        };
        finisher = Function.identity();
    }

    public LongMapCollector<T> withInitialSize(int initialSize) {
        this.initialSize = initialSize;
        return this;
    }

    public LongMapCollector<T> withSupplier(Supplier<Long2ObjectMap<T>> supplier) {
        this.supplier = supplier;
        return this;
    }

    public LongMapCollector<T> withCombiner(BinaryOperator<Long2ObjectMap<T>> combiner) {
        this.combiner = combiner;
        return this;
    }

    public LongMapCollector<T>  withFinisher(Function<Long2ObjectMap<T>, Long2ObjectMap<T>> finisher) {
        this.finisher = finisher;
        return this;
    }

    @Override
    public Supplier<Long2ObjectMap<T>> supplier() {
        return supplier;
    }

    @Override
    public BiConsumer<Long2ObjectMap<T>, T> accumulator() {
        return accumulator;
    }

    @Override
    public BinaryOperator<Long2ObjectMap<T>> combiner() {
        return combiner;
    }

    @Override
    public Function<Long2ObjectMap<T>, Long2ObjectMap<T>> finisher() {
        return finisher;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return CH_ID;
    }

    public static void main(String[] args) {
        List<Long> list = Arrays.asList(1L, 2L, 3L);

        Long2ObjectMap<Long> res = list.stream().collect(
            Collector.<Long, Long2ObjectMap<Long>>of(
                () -> new Long2ObjectOpenHashMap<>(list.size()),
                (map, l) -> map.put(l.longValue(), l),
                (m1, m2) -> null
            )
        );
        System.out.println(res);

        Long2ObjectMap<Long> res2 = list.stream().collect(new LongMapCollector<>(Long::longValue)
            .withInitialSize(list.size())
        );
        System.out.println(res2);
    }
}
