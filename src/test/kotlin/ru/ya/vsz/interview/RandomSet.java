package ru.ya.vsz.interview;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <a href="https://femida.yandex-team.ru/problems/7821">Случайное множество</a>
 * Написать множество, хранящее целые числа, поддерживающее следующие операции:
 * 1) добавле ние значения в множество
 * 2) проверка есть ли значение в множестве
 * 3) удаление значения из множества
 * 4) получение случайного значения из множества.
 * Все операции должны работать за O(1)
 */
public interface RandomSet {
    void add(int value);

    boolean contains(int value);

    Integer getRandom();

    void remove(int value);
}

class RandomSetTest implements RandomSet {
    private final Random random = new Random(9523342);

    //=========================================================================

    private final Map<Integer, Integer> valueToIndex = new HashMap<>();
    private final List<Integer> values = new ArrayList<>();

    public RandomSetTest() {
    }

    @Override
    public void add(int value) {
        if (valueToIndex.containsKey(value)) {
            return;
        }
        int newIndex = values.size();
        values.add(value);
        valueToIndex.put(value, newIndex);
    }

    @Override
    public boolean contains(int value) {
        return valueToIndex.containsKey(value);
    }

    @Override
    public Integer getRandom() {
        if (values.isEmpty()) {
            return null;
        }
        int randomIndex = random.nextInt(values.size());
        return values.get(randomIndex);
    }

    @Override
    public void remove(int value) {
        Integer indexToRemove = valueToIndex.get(value);
        if (indexToRemove == null) {
            // no element
            return;
        }
        int lastIndex = values.size() - 1;
        if (indexToRemove != lastIndex) {
            int lastValue = values.get(lastIndex);
            values.set(indexToRemove, lastValue);
            valueToIndex.put(lastValue, indexToRemove);
        }
        values.remove(lastIndex);
        valueToIndex.remove(value);
    }

    //=========================================================================

    @Test
    public void test() {
        RandomSetTest set = new RandomSetTest();
        set.add(1);
        set.add(2);
        set.add(3);
        set.add(4);
        set.add(5);

        assertTrue(set.contains(1));
        assertTrue(set.contains(2));
        assertTrue(set.contains(3));
        assertTrue(set.contains(4));
        assertTrue(set.contains(5));

        assertEquals(4, set.getRandom());
        assertEquals(3, set.getRandom());
        assertEquals(1, set.getRandom());
        assertEquals(2, set.getRandom());
        assertEquals(4, set.getRandom());

        set.remove(3);
        assertTrue(set.contains(1));
        assertTrue(set.contains(2));
        assertFalse(set.contains(3));
        assertTrue(set.contains(4));
        assertTrue(set.contains(5));

        assertEquals(1, set.getRandom());
        assertEquals(4, set.getRandom());
        assertEquals(5, set.getRandom());
        assertEquals(4, set.getRandom());
        assertEquals(2, set.getRandom());

        set.remove(5);
        assertTrue(set.contains(1));
        assertTrue(set.contains(2));
        assertFalse(set.contains(3));
        assertTrue(set.contains(4));
        assertFalse(set.contains(5));

        assertEquals(4, set.getRandom());
        assertEquals(1, set.getRandom());
        assertEquals(4, set.getRandom());
        assertEquals(1, set.getRandom());
        assertEquals(4, set.getRandom());
        assertEquals(4, set.getRandom());
        assertEquals(1, set.getRandom());
        assertEquals(1, set.getRandom());
        assertEquals(4, set.getRandom());
        assertEquals(1, set.getRandom());
        assertEquals(1, set.getRandom());
        assertEquals(2, set.getRandom());
        assertEquals(4, set.getRandom());
        assertEquals(1, set.getRandom());
        assertEquals(4, set.getRandom());
        assertEquals(4, set.getRandom());
        assertEquals(1, set.getRandom());
        assertEquals(1, set.getRandom());
        assertEquals(2, set.getRandom());

        // удаляем отсутствующий элемент
        set.remove(6);

        // удаляем всё
        set.remove(1);
        set.remove(2);
        set.remove(3);
        set.remove(4);
        set.remove(5);

        // проверяем, что ничего больше нет
        set.remove(4);

        // пытаемся получить из пустого
        assertNull(set.getRandom());

        // добавляем повторно
        set.add(1);
        set.add(1);
        // а удаляем один раз
        set.remove(1);
        assertNull(set.getRandom());
    }

    private void assertEquals(int expected, Integer actual) {
        assertNotNull(actual);
        int actualInt = actual;
        Assertions.assertEquals(expected, actualInt);
    }
}
