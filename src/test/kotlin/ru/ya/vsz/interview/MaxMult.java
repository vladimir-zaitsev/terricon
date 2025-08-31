package ru.ya.vsz.interview;

import org.junit.jupiter.api.Test;

import static java.lang.Math.abs;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * Дан массив из n целых чисел. Нужно выбрать n-1 чисел так, что бы их произведение было максимальным среди всех
 * возможных n-1 наборов.
 */
public class MaxMult {
    public int[] getMaxMultNums(int[] input) {

        if (input.length == 0) {
            return input;
        }

        int minNeg = Integer.MAX_VALUE;
        int maxNeg = Integer.MIN_VALUE;
        int countNeg = 0;

        int minPos = Integer.MAX_VALUE;
        int countPos = 0;

        Integer toDel = null;

        for (int i : input) {
            if (i < 0) {
                countNeg++;
                if (abs(minNeg) > abs(i)) {
                    minNeg = i;
                }
                if (abs(maxNeg) < abs(i)) {
                    maxNeg = i;
                }
            }

            if (i >= 0) {
                countPos++;
                if (minPos > i) {
                    minPos = i;
                }
            }
        }

        if (countNeg % 2 == 0) {
            if (countPos > 0) {
                toDel = minPos;
            } else {
                toDel = maxNeg;
            }
        } else {
            toDel = minNeg;
        }

        int[] result = new int[input.length - 1];
        int cnt = 0;
        boolean already = false;
        for (int i : input) {
            if (i == toDel && !already) {
                already = true;
                continue;
            }
            result[cnt++] = i;
        }
        return result;
    }

    @Test
    public void test() {
        assertArrayEquals(new int[]{2, 3, 4, 5}, getMaxMultNums(new int[]{1, 2, 3, 4, 5}));
        assertArrayEquals(new int[]{2, 3, 4, 5}, getMaxMultNums(new int[]{1, 2, 3, 4, 5}));
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, getMaxMultNums(new int[]{1, 2, -1, 3, 4, 5}));
        assertArrayEquals(new int[]{-2, -1, 3, 4, 5}, getMaxMultNums(new int[]{1, -2, -1, 3, 4, 5}));
        assertArrayEquals(new int[]{1, -2, -3, 4, 5}, getMaxMultNums(new int[]{1, -2, -1, -3, 4, 5}));
        assertArrayEquals(new int[]{-2, -1, -3}, getMaxMultNums(new int[]{-2, -1, -4, -3}));
    }

}
