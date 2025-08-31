package ru.ya.vsz.terricon.lang

class BooleanArray(size: Int) {
    private val store: BooleanMatrix

    init {
        store = BooleanMatrix(size, 1)
    }

    operator fun get(i: Int): Boolean {
        return store[i, 0]
    }

    operator fun set(i: Int, value: Boolean) {
        store[i, 0] = value
    }

    fun fill(value: Boolean) {
        store.fill(value)
    }

    val size: Int
        get() = store.width

    override fun toString(): String {
        return store.toString()
    }
}
