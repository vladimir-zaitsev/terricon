package ru.ya.vsz.terricon.lang

class BooleanMatrix(val width: Int, val height: Int) {
    private val store: IntMatrix

    init {
        var intWidth = width / Integer.SIZE
        if (width % Integer.SIZE > 0) {
            intWidth++
        }
        store = IntMatrix(intWidth, height)
    }

    operator fun get(i: Int, j: Int): Boolean {
        if (i < 0 || j < 0 || i >= width || j >= height) {
            throw IndexOutOfBoundsException()
        }
        return getBit(store[i / Integer.SIZE, j], i % Integer.SIZE)
    }

    operator fun set(i: Int, j: Int, value: Boolean) {
        val batchIndex = i / Integer.SIZE
        var batch = store[batchIndex, j]
        batch = if (value) {
            setBit(batch, i % Integer.SIZE)
        } else {
            unsetBit(batch, i % Integer.SIZE)
        }
        store[batchIndex, j] = batch
    }

    fun fill(value: Boolean) {
        if (value) {
            store.fill(0.inv())
        } else {
            store.fill(0)
        }
    }

    override fun toString(): String {
        val result = StringBuilder()
        for (j in 0 until height) {
            for (i in 0 until width) {
                result.append(' ')
                result.append(if (get(i, j)) '1' else '0')
            }
            result.append("\n")
        }
        return result.toString()
    }
}

private fun getBit(data: Int, index: Int): Boolean {
    return data and (1 shl index) != 0
}

private fun setBit(data: Int, index: Int): Int {
    return data or (1 shl index)
}

private fun unsetBit(data: Int, index: Int): Int {
    return data and (1 shl index).inv()
}