package ru.ya.vsz.terricon.util;

import java.util.OptionalDouble;
import java.util.function.Function;

import static java.lang.Character.isDigit;

/**
 * Выделяет числа из произвольного формата и шума.
 */
public class NumberParser {
    private static final double BASE = 10;

    private NumberParser() {
    }

    public static OptionalDouble parseDouble(String s) {
        int length = s.length();
        FiniteAutomate fa = new FiniteAutomate();

        for (int i = 0; i < length && !fa.isError; i++) {
            fa.onChar(s.charAt(i));
        }

        if (fa.isError && fa.startNumberPosition == -1) {
            return OptionalDouble.empty();
        }
        if (fa.finishNumberPosition == -1) {
            fa.finishNumberPosition = length - 1;
        }
        if (fa.fractionDenominator > 0) {
            fa.value = fa.value / fa.fractionDenominator;
        }
        return OptionalDouble.of(fa.value);
    }

    private static class FiniteAutomate {
        private interface State extends Function<Character, State>{}
        private interface FiniteAutomateStates { // вместо enum
            State start(char ch);
            State numberDigit(char ch);
            State numberGroupOrFractionSeparator(char ch); //  '.' ',' ' '
            State numberDigitAfterFirstSeparator(char ch);
            State numberGroupSeparator(char ch);
            State fractionSeparator(char ch);
            State fractionDigit(char ch);
            State afterNumber(char ch);
            State error(char ch);
        }

        private int charCounter = 0;
        private boolean isError = false;
        private char lastFractionSeparatorChar;
        private char lastNumberGroupSeparatorChar;
        private int startNumberPosition = -1;
        private int finishNumberPosition = -1;
        private double value = 0;
        private double fractionDenominator = 0;

        private final FiniteAutomateStates finiteAutomateTransitions =
            new FiniteAutomateStates() { // вместо switch
                public State start(char ch) {
                    if (isDigit(ch)) {
                        pushDigit(ch);
                        startNumberPosition = charCounter;
                        return this::numberDigit;
                    }
                    return this::start;
                }

                public State numberDigit(char ch) {
                    if (isDigit(ch)) {
                        pushDigit(ch);
                        return this::numberDigit;
                    }
                    if (isFractionSeparator(ch)) {
                        lastFractionSeparatorChar = ch;
                        fractionDenominator = 1;
                        return this::numberGroupOrFractionSeparator;
                    }
                    if (isGroupSeparator(ch)) {
                        lastFractionSeparatorChar = ch;
                        return this::numberGroupSeparator;
                    }
                    isError = true;
                    return this::error;
                }

                public State numberGroupOrFractionSeparator(char ch) {
                    if (isDigit(ch)) {
                        pushDigit(ch);
                        return this::numberDigitAfterFirstSeparator;
                    } else {
                        finishNumberPosition = charCounter;
                        return this::afterNumber;
                    }
                }

                @Override
                public State numberDigitAfterFirstSeparator(char ch) {
                    if (isDigit(ch)) {
                        pushDigit(ch);
                        return this::numberDigitAfterFirstSeparator;
                    }
                    if (lastFractionSeparatorChar == ch) {
                        fractionDenominator = 0;
                        return this::numberGroupSeparator;
                    }
                    finishNumberPosition = charCounter;
                    return this::afterNumber;
                }

                @Override
                public State numberGroupSeparator(char ch) {
                    if (isDigit(ch)) {
                        pushDigit(ch);
                        return this::numberDigitAfterFirstSeparator;
                    }
                    finishNumberPosition = charCounter;
                    return this::afterNumber;
                }

                @Override
                public State fractionSeparator(char ch) {
                    return null;
                }

                @Override
                public State fractionDigit(char ch) {
                    return null;
                }

                public State afterNumber(char ch) {
                    if (isDigit(ch)) {
                        isError = true;
                        return this::error;
                    }
                    return this::afterNumber;
                }

                public State error(char ch) {
                    return this::error;
                }
            };
        State state = finiteAutomateTransitions::start;

        private boolean isFractionSeparator(char ch) {
            return ch == '.' || ch == ',';
        }

        private boolean isGroupSeparator(char ch) {
            return ch == ' ';
        }

        private void pushDigit(char ch) {
            value = value * BASE + (ch - '0');
            fractionDenominator *= BASE;
        }

        public void onChar(char ch) {
            state = state.apply(ch);
            charCounter++;
        }

    }


    public static void main(String[] args) {
        System.out.println(parseDouble("sdjhfg dkjuhd, dudh 12 467,23 kasdhfa, osdu "));
        System.out.println(parseDouble("sdjhfg dkjuhd, dudh 12 467, kasdhfa, osdu "));
        System.out.println(parseDouble("12 467"));
        System.out.println(parseDouble("12467руб."));
        System.out.println(parseDouble("12,467.55 руб."));
        System.out.println(parseDouble("12.467,55 руб."));
        System.out.println(parseDouble("12,467,552 руб."));

    }
}
