package ru.ya.vsz.terricon.lang;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Collection cartesian product.
 *
 * @author Vladimir Zaytsev <vsz@ya.ru>
 * @since 20-04-2022
 */
public class CollectionCartesianProduct {

    private CollectionCartesianProduct() {
    }

    public static boolean isEmpty(Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }

    public static <A, B> List<Pair<A, B>> leftJoin(Collection<A> a, Collection<B> b) {
        return a.stream().flatMap(aItem -> isEmpty(b)
                ? Stream.of(new Pair<A, B>(aItem, null))
                : b.stream().map(bItem -> new Pair<>(aItem, bItem))
        ).collect(Collectors.toList());
    }

    public static <A, B> List<Pair<A, B>> rightJoin(Collection<A> a, Collection<B> b) {
        return b.stream().flatMap(bItem -> isEmpty(a)
                ? Stream.of(new Pair<A, B>(null, bItem))
                : a.stream().map(aItem -> new Pair<>(aItem, bItem))
        ).collect(Collectors.toList());
    }

    public static <A, B> List<Pair<A, B>> fullJoin(Collection<A> a, Collection<B> b) {
        return isEmpty(b) ? leftJoin(a, b) : rightJoin(a, b);
    }

    public record Pair<A, B>(A a, B b) {
        @Override
            public String toString() {
                return "(" + a + ", " + b + ")";
            }
        }

    public static void main(String[] args) { // throws JsonProcessingException {
        var l1 = List.of(1, 2, 3);
        var l2 = List.of("a", "b", "c");
        List<String> l3 = List.of();

        System.out.println(fullJoin(l1, l2));
        System.out.println(fullJoin(l2, l3));
        System.out.println(fullJoin(l3, l2));
        System.out.println(fullJoin(l3, l3));
    }
}
