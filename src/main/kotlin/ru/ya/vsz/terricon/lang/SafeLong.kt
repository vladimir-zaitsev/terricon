package ru.ya.vsz.terricons.lang

import java.math.BigInteger
import java.math.BigInteger.ONE

/**
 * BigInteger decorator for overflow-safe operations with long values.
 *
 * @author Vladimir Zaytsev <vzay@yandex-team.ru>
 * @since 09-02-2022
 */
@Suppress("NOTHING_TO_INLINE", "unused")
class SafeLong(
    private val longValue: Long,
    private val value: BigInteger? = null,
) : Comparable<SafeLong> {
    private inline fun getValue(): BigInteger {
        return value ?: longValue.toBigInteger()
    }

    fun longValue(): Long {
        return longValue
    }

    /**
     * Enables the use of the `+` operator for [SafeLong] instances.
     * @throws ArithmeticException if the result of operation will not exactly fit in a {@code long}.
     */
    operator fun plus(other: SafeLong): SafeLong {
        val result = getValue() + other.getValue()
        try {
            return SafeLong(result.longValueExact(), result)
        } catch (ex: ArithmeticException) {
            throw ArithmeticException("Result of $longValue + ${other.longValue} is out of long range")
        }
    }

    /**
     * Enables the use of the `-` operator for [SafeLong] instances.
     * @throws ArithmeticException if the result of operation will not exactly fit in a {@code long}.
     */
    operator fun minus(other: SafeLong): SafeLong {
        val result = getValue() - other.getValue()
        try {
            return SafeLong(result.longValueExact(), result)
        } catch (ex: ArithmeticException) {
            throw ArithmeticException("Result of $longValue - ${other.longValue} is out of long range")
        }
    }

    /**
     * Enables the use of the `*` operator for [SafeLong] instances.
     * @throws ArithmeticException if the result of operation will not exactly fit in a {@code long}.
     */
    operator fun times(other: SafeLong): SafeLong {
        val result = getValue() * other.getValue()
        try {
            return SafeLong(result.longValueExact(), result)
        } catch (ex: ArithmeticException) {
            throw ArithmeticException("Result of $longValue * ${other.longValue} is out of long range")
        }
    }

    /**
     * Enables the use of the `/` operator for [SafeLong] instances.
     * @throws ArithmeticException if the result of operation will not exactly fit in a {@code long}.
     */
    operator fun div(other: SafeLong): SafeLong {
        val result = getValue() / other.getValue()
        try {
            return SafeLong(result.longValueExact(), result)
        } catch (ex: ArithmeticException) {
            throw ArithmeticException("Result of $longValue / ${other.longValue} is out of long range")
        }
    }

    /**
     * Enables the use of the `%` operator for [SafeLong] instances.
     * @throws ArithmeticException if the result of operation will not exactly fit in a {@code long}.
     */
    operator fun rem(other: SafeLong): SafeLong {
        val result = getValue() % other.getValue()
        try {
            return SafeLong(result.longValueExact(), result)
        } catch (ex: ArithmeticException) {
            throw ArithmeticException("Result of $longValue % ${other.longValue} is out of long range")
        }
    }

    /**
     * Enables the use of the unary `-` operator for [SafeLong] instances.
     * @throws ArithmeticException if the result of operation will not exactly fit in a {@code long}.
     */
    operator fun unaryMinus(): SafeLong {
        val result = -getValue()
        try {
            return SafeLong(result.longValueExact(), result)
        } catch (ex: ArithmeticException) {
            throw ArithmeticException("Result of -$longValue is out of long range")
        }
    }

    /**
     * Enables the use of the `++` operator for [SafeLong] instances.
     * @throws ArithmeticException if the result of operation will not exactly fit in a {@code long}.
     */
    operator fun inc(): SafeLong {
        val result = getValue() + ONE
        try {
            return SafeLong(result.longValueExact(), result)
        } catch (ex: ArithmeticException) {
            throw ArithmeticException("Result of ++$longValue is out of long range")
        }
    }

    /**
     * Enables the use of the `--` operator for [SafeLong] instances.
     * @throws ArithmeticException if the result of operation will not exactly fit in a {@code long}.
     */
    operator fun dec(): SafeLong {
        val result = getValue() - ONE
        try {
            return SafeLong(result.longValueExact(), result)
        } catch (ex: ArithmeticException) {
            throw ArithmeticException("Result of --$longValue is out of long range")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SafeLong

        if (longValue != other.longValue) return false

        return true
    }

    override fun hashCode(): Int {
        return longValue.hashCode()
    }

    override fun toString(): String {
        return "$longValue"
    }

    override fun compareTo(other: SafeLong): Int {
        return longValue.compareTo(other.longValue)
    }
}

fun Long.safe() = SafeLong(this)
