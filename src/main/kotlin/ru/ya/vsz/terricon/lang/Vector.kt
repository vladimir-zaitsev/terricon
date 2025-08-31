package ru.ya.vsz.terricon.lang

data class Vector(
    val x: Double, val y: Double,
) {
    operator fun plus(v: Vector): Vector {
        return Vector(x + v.x, y + v.y)
    }

    operator fun times(v: Vector): Double {
        return x * v.x + y * v.y
    }

    operator fun times(a: Double): Vector {
        return Vector(x * a, y * a)
    }

    operator fun div(a: Double): Vector {
        return Vector(x / a, y / a)
    }

}

interface Titled{
    var title: String
}

class Person(private val gender: Boolean) : Titled {
    private var name: String = "no name";
    override var title: String
        get() = (if (gender) "Mr. " else "Mrs. ") + name
        set(value) {name = value}
}

fun main(){
    val person = Person(true)
    person.title = "Ivan";
    println(person.title)
}
