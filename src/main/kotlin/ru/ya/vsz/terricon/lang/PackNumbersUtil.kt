package ru.ya.vsz.terricon.lang

const val HI_BIT_MASK: ULong  = 0b11111111_11111111_11111111_11111111_00000000_00000000_00000000_00000000u
const val LO_BIT_MASK: ULong  = 0b00000000_00000000_00000000_00000000_11111111_11111111_11111111_11111111u
const val EXP_BIT_MASK: ULong = 0b01111111_11110000_00000000_00000000_00000000_00000000_00000000_00000000u

fun pack(d: Double): Long {
    return java.lang.Double.doubleToRawLongBits(d)
}

fun isPackedDouble(l: Long): Boolean {
    return HI_BIT_MASK and l.toULong() != EXP_BIT_MASK
}

fun unpackDouble(l: Long): Double {
    require(isPackedDouble(l)) { "Not a packed double" }
    return java.lang.Double.longBitsToDouble(l)
}

fun pack(i: Int): Long {
    return (EXP_BIT_MASK or i.toULong()).toLong()
}

fun isPackedInt(l: Long): Boolean {
    return HI_BIT_MASK and l.toULong() == EXP_BIT_MASK
}

fun unpackInt(l: Long): Int {
    require(isPackedInt(l)) { "Not a packed integer" }
    return (LO_BIT_MASK and l.toULong()).toInt()
}

fun packInts(firstInt: Int, secondInt: Int): Long {
    return firstInt.toLong() shl Integer.SIZE or Integer.toUnsignedLong(secondInt)
}

fun unpackFirstInt(paramValuePair: Long): Int {
    return (paramValuePair shr Integer.SIZE).toInt()
}

fun unpackSecondInt(paramValuePair: Long): Int {
    return paramValuePair.toInt()
}
