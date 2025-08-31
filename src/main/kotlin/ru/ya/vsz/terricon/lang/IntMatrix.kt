package ru.ya.vsz.terricon.lang

import java.util.*

class IntMatrix(val width: Int, val height: Int) {
    private val store: IntArray = IntArray(width * height)

    operator fun get(i: Int, j: Int): Int {
        if (i < 0 || j < 0 || i >= width || j >= height) {
            throw IndexOutOfBoundsException()
        }
        return store[i + j * width]
    }

    operator fun set(i: Int, j: Int, value: Int) {
        if (i < 0 || j < 0 || i >= width || j >= height) {
            throw IndexOutOfBoundsException()
        }
        store[i + j * width] = value
    }

    fun fill(value: Int) {
        Arrays.fill(store, value)
    }
}
