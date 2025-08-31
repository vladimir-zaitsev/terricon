@file:Suppress("MemberVisibilityCanBePrivate")

package ru.ya.vsz.terricon.art

import org.intellij.lang.annotations.Language
import java.awt.Color
import java.awt.Color.*
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

const val PICTURE_WIDTH = 300
const val PICTURE_HEIGHT = 200

var originalRadius = 20
var filterSize = 256
var filter: Array<Boolean> = emptyArray<Boolean>()
var pixelHashSizeX = 50
var pixelHashSizeY = 50

fun original(x: Int, y: Int): Boolean =
    sqr(y - PICTURE_HEIGHT / 2) + sqr(x - PICTURE_WIDTH / 2) < originalRadius * originalRadius

var originalSize = 0

fun original2(x: Int, y: Int): Boolean =
    (sqr(y - PICTURE_HEIGHT / 2) + sqr(x - PICTURE_WIDTH / 2 - originalRadius - 15) < originalRadius * originalRadius) ||
            (sqr(y - PICTURE_HEIGHT / 2) + sqr(x - PICTURE_WIDTH / 2 + originalRadius + 5) < originalRadius * originalRadius)

fun learnFilter(
    originalFunction: (Int, Int) -> Boolean,
    filterSize: Int,
    vararg hashFunctions: (Int, Int) -> Int,
): Array<Boolean> {
    val result = Array(filterSize) { false }
    for (y in 0 until PICTURE_HEIGHT) {
        for (x in 0 until PICTURE_WIDTH) {
            if (originalFunction(x, y)) {
                hashFunctions.forEach { hashFunction ->
                    result[hashFunction(x, y)] = true
                }
            }
        }
    }
    return result
}

fun hash1(x: Int, y: Int): Int {
    var v = x.toLong() + y.toLong() * PICTURE_WIDTH
    v = (v shr 16 xor v) * 0x45d9f3b
    return (abs(v) % filterSize).toInt()
}

fun hash2(x: Int, y: Int): Int {
    var v = x.toLong() + y.toLong() * PICTURE_WIDTH
    v = (v shr 16 xor v) * 0x45d9f3b
    v = (v shr 16 xor v) * 0x45d9f3b
    return (abs(v) % filterSize).toInt()
}

fun hash3(x: Int, y: Int, maxValue: Int = filterSize): Int {
    var v = x.toLong() + y.toLong() * PICTURE_WIDTH
    v = (v shr 16 xor v) * 0x45d9f3b
    v = (v shr 16 xor v) * 0x45d9f3b
    v = (v shr 16 xor v)
    return (abs(v) % maxValue).toInt()
}

fun hash(v: Long, maxValue: Int = filterSize): Int {
    var p = (v shr 16 xor v) * 0x45d9f3b
    p = (p shr 16 xor p) * 0x45d9f3b
    p = (p shr 16 xor p)
    return (abs(p) % maxValue).toInt()
}

fun pixelHashWidth() = PICTURE_WIDTH / pixelHashSizeX
fun pixelHashHeight() = PICTURE_HEIGHT / pixelHashSizeY

fun pixelHash(x: Int, y: Int): Int {
    val pixelX = x / pixelHashSizeX
    val pixelY = y / pixelHashSizeY
    return pixelX + pixelY * pixelHashWidth()
}

fun pixelHashFilterSize() = pixelHashWidth() * pixelHashHeight()

fun pixelHash2(x: Int, y: Int): Int {
    val pixelX = (x + pixelHashSizeX / 2) / pixelHashSizeX
    val pixelY = (y + pixelHashSizeY / 2) / pixelHashSizeY
    return pixelX + pixelY * (pixelHashWidth() + 1)
}

fun pixelHash2FilterSize() = pixelHashFilterSize() + pixelHashWidth() + pixelHashHeight() + 1

fun pixelHash2Rnd(x: Int, y: Int): Int = hash(pixelHash2(x, y).toLong())

fun toRGB(r: Int = 0, g: Int = 0, b: Int = 0, a: Int = 255): Color = Color(r, g, b, a)

fun toYellow(v: Int): Color = toRGB(r = v, g = v)

fun toHSB(h: Float = 1f, s: Float = 1f, b: Float = 1f): Color = getHSBColor(h, s, b)

class Picture(pixelColorFunction: (x: Int, y: Int) -> Color) :
    Article.Picture(PICTURE_WIDTH, PICTURE_HEIGHT, pixelColorFunction)

val darkRed = toRGB(r = 64)
val darkGreen = toRGB(g = 64)

interface IPoint {
    val x: Int
    val y: Int
}

data class Point(
    override val x: Int,
    override val y: Int,
) : IPoint

var marginX = 3
var marginY = 3

typealias Distance = Double
typealias Projection = Double

data class Center(
    val point: Point,
    val n: Int = -1,
    var neighbors: List<Pair<Center, Distance>>,
    val nx: Int,
    val ny: Int,
    var radius: Double,
) : IPoint {
    constructor(x: Int, y: Int, n: Int, nx: Int = -1, ny: Int = -1) : this(Point(x, y), n, listOf(), nx, ny, -1.0)
    override val x: Int by point::x
    override val y: Int by point::y
    override fun toString(): String {
        return "Center(n=$n, x=$x, y=$y)"
    }

}

private fun hash(neighbors: List<Pair<Center, Distance>>, maxN: Int): Float {
    var p = 1
    var sumOf: Long = 0
    neighbors.forEach { (center, _) ->
        sumOf += center.n * p
        p *= maxN
    }
    val max = maxN.toDouble().pow(neighbors.size).toInt()
    return hash(sumOf, max).toFloat() / max.toFloat()
}

class VoronoiDelaunay {
    val maxMatrixX = PICTURE_WIDTH / pixelHashSizeX
    val maxMatrixY = PICTURE_HEIGHT / pixelHashSizeX
    val centers = ArrayList<Center>()
    val centersMatrix: List<List<Center>> =
        (0 until maxMatrixX).map { nx ->
            (0 until maxMatrixY).map { ny ->
                val x = marginX + hash3(nx, ny, pixelHashSizeX - 2 * marginX)
                val y = marginY + hash(x.toLong(), pixelHashSizeY - 2 * marginY)
                val center = Center(nx * pixelHashSizeX + x, ny * pixelHashSizeY + y, centers.size, nx, ny)
                centers.add(center)
                center
            }
        }

    init {
        centers.forEach {
            it.neighbors = findNeighbors(it.x, it.y, it)
            val d = it.neighbors.first().second
            it.radius = d / 2.0;
        }
    }

    fun colorH(n: Int): Float = 2 * n.toFloat() / (centers.size + 1)
    fun color(n: Int): Color = toHSB(colorH(n))

    val IPoint.nx: Int get() = this.x / pixelHashSizeX
    val IPoint.ny: Int get() = this.y / pixelHashSizeY

    fun findNeighbors(x: Int, y: Int, center: Center): List<Pair<Center, Distance>> {
        val point = Point(x, y)
        return neighborCells(point.nx, point.ny)
            .filter { it != center }
            .map { Pair(it, point.distanceTo(it)) }
            .filter {
                val middle = Point((x + it.first.x) / 2, (y + it.first.y) / 2)
                val (a, b) = findTwoNearestNeighbors(middle).map { pair -> pair.first }
                (a == it.first && b == center ) || (b == it.first && a == center )
            }
            .sortedBy { it.second }
    }

    fun findNearestNeighbor(x: Int, y: Int): Pair<Center, Distance> =
        findNearestNeighbor(Point(x, y))

    fun findNearestNeighbor(point: Point): Pair<Center, Distance> {
        return neighborCells(point.nx, point.ny)
            .map { Pair(it, point.distanceTo(it)) }
            .minBy { it.second}
    }

    fun findTwoNearestNeighbors(point: Point): List<Pair<Center, Distance>> {
        return neighborCells(point.nx, point.ny)
            .map { Pair(it, point.distanceTo(it)) }
            .sortedBy { it.second}
            .slice(0..1)
    }

    private fun neighborCells(nx: Int, ny: Int): List<Center> {
        return listOf(
            Pair(nx - 1, ny - 1), Pair(nx + 0, ny - 1), Pair(nx + 1, ny - 1),
            Pair(nx - 1, ny + 0), Pair(nx + 0, ny + 0), Pair(nx + 1, ny + 0),
            Pair(nx - 1, ny + 1), Pair(nx + 0, ny + 1), Pair(nx + 1, ny + 1),
        ).filter {
            it.first in 0..< maxMatrixX && it.second in 0..< maxMatrixY
        }
            .map { centersMatrix[it.first][it.second] }
    }
}

fun IPoint.distanceTo(x: Int, y: Int): Double {
    return sqrt((sqr(this.x - x) + sqr(this.y - y)).toDouble())
}

fun IPoint.distanceTo(point: IPoint): Distance {
    return this.distanceTo(point.x, point.y)
}

fun IPoint.midway(point: IPoint): IPoint {
    return Point((x + point.x) / 2, (y + point.y) / 2)
}

fun List<Center>.findNearest(x: Int, y: Int): Center {
    var result = this.first()
    var resultDistance = result.distanceTo(x, y)
    forEach {
        val itDistance = it.distanceTo(x, y)
        if (itDistance < resultDistance) {
            result = it
            resultDistance = itDistance
        }
    }
    return result
}

fun sqr(n: Int): Int = n * n

data class Vector(
    val x: Double, val y: Double,
) {
    constructor(x: Int, y: Int) : this(x.toDouble(), y.toDouble())

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

    fun length(): Double {
        return sqrt(x * x + y * y)
    }

    fun ort(): Vector {
        return this / length()
    }

    fun rotate90ccw(): Vector {
        return Vector(-y, x)
    }
}

@Language("Markdown")
val article = Article("src/main/resources/article/bloom/", "output.md") + """
## Визуализация работы фильтра Блума
Я очень люблю визуализации алгоритмов. В основном это интеллектуальное развлечения, вид искуства. И, может быть, помогает лучше понять (или скорее почувствовать) работу алгоритма. Сначала я хотел визуализировать собственно фильтр Блума, но по ходу работы оказалось, что визуализация хешей может быть даже более интересной, так что про хеши тут будет больше.
Про фильтр Блума уже много написано, и кажется зачем снова о нем писать. Но как-то на лекции я увидел графическую иллюстрацию принципа работы фильтра, по которой должно было быть видно, что фильтр гарантированно пропускает все истинные значения, и плюс ещё какие-то. То есть множество истинных значений будет подмножеством того, что пропустит фильтр.

![](https://habrastorage.org/webt/aq/il/tp/aqiltpkd_960owddyf3zx9r1f7u.png)

Здесь заштриховано множество правильных значений, а вокруг него обведена граница множества значений, которые пропускает фильтр (картинка была синим маркером). И я подумал, хорошо бы получить такую картинку "честно", напрямую визуализируя работу какой-то реализации фильтра Блума. Дальше будет рассказ об этом, с попутной визуализацией хеш-функций, ну и витражными пикселами в конце (мой шуточный термин).  

<cut />

Если подзабыли, как работает фильтр Блума, лучше сначала [почитать](https://habr.com/ru/articles/788772/) от этом.
В качестве элементов для фильтра можно взять точки на плоскости, а в качестве истинного множества – какую-нибудь узнаваемую фигуру, например, круг. Построим истинное множество:

""" + Picture { x, y ->
    if (original(x, y)) {
        originalSize++
        BLUE
    } else BLACK
} + """

Для параметров фильтра Блума важно знать коэффициент заполнение истинным множеством диапазона значений. Для выбранного круга он равен ${originalSize * 100.toDouble() / PICTURE_WIDTH / PICTURE_HEIGHT}%.
В качестве хеш - функции хочется взять что-то, похожее на случайное. Псевдослучайный хеш от числа нашел на [Stackoverflow](https://stackoverflow.com/questions/664014/what-integer-hash-function-are-good-that-accepts-an-integer-hash-key). Числом для точки вполне может быть её номер (`x + y * WIDTH`).
Посмотрев на код хеш-функции, я заметил, что в ней трижды повторяется почти одна и та же операция: `v = (v shr 16 xor v) * 0x45d9f3b`. Конечно, сразу возник вопрос, почему именно трижды? Может, можно меньше? Или надо больше?
Для удобства отображения пока пусть значений хеша будет 256 (можно будет показывать одним цветовым каналом). Попробуем вариант с одной операцией. Посмотрим на значения hash-функции в каждой точке, используя для отображения одну компоненту цвета:

""" + Picture { x, y -> toYellow(hash1(x, y)) } + """

Видно, что хеш получился не слишком случайный, а хочется почему-то именно случайный, то есть с большим периодом и равномерно распределенный. Попробуем повторить операцию еще разок.

""" + Picture { x, y -> toYellow(hash2(x, y)) } + """

На глаз уже ничего не разобрать. Возможно, лучше использовать для отображения хеша существенно разные цвета?

""" + Picture { x, y -> toHSB(h = hash2(x, y).toFloat() / filterSize) } + """

Так даже хуже видно. Попробуем подсветить только одно значения хеша.

""" + Picture { x, y -> if (hash2(x, y) == 128) YELLOW else BLACK } + """

Теперь видно, что конкретные значения всё еще заметно периодичны. Придется добавить еще операцию. И снова посмотрим то же значения хеша:

""" + Picture { x, y -> if (hash3(x, y) == 128) YELLOW else BLACK } + """

Кажется, что хеш уже достаточно случаен. Хотя хорошо ли это -- не понятно, и мы ещё вернемся к этому вопросу. Перейдем уже собственно к фильтру.

Обучим фильтр и построим множество, которое он определяет. Точек у нас 300 x 200 = 60000, коэффициент заполнения 2%, и значит элементов фильтра потребуется хотя бы 12000 (20%).

""" + {
    filterSize = 12000
    filter = learnFilter(::original, filterSize, ::hash3)
    Picture { x, y -> if (filter[hash3(x, y)]) YELLOW else BLACK }
} + """

Для наглядности поверх снова нарисуем истинное множество.

""" + Picture { x, y -> if (original(x, y)) BLUE else if (filter[hash3(x, y)]) YELLOW else BLACK } + """

Что ж, визуализация состоялась. Можно посмотреть, как на картинку влияет изменение количества элементов фильтра (оно же -- количество значений хеша). Или увеличить количество разных хеш-функций.
Но хочется сделать картинку более похожей на КДПВ, там то ложно-положительная область фильтра была рядом с истинным множеством, а у нас получилось случайно разбросанная.

Давайте попробуем хеш-функцию сделать локальной. Например, просто разобьем картинку на квадраты и будем считать хешем номер квадрата, в который попала точка. Нарисуем для проверки какое-то количество значений такого хеша разными цветами.

""" + Picture { x, y -> toHSB(h = pixelHash(x, y).toFloat() / pixelHashFilterSize()) } + """

Смотрится отлично! Теперь обучим фильтр с таким хешем. Только квадратики возьмем поменьше, 10 x 10, так что элементов фильтра будет всего 600. И истинный круг сделаем побольше, при локальной хеш-функции коэффициент заполнения на качество фильтра сильно не повлияет.

""" + {
    originalRadius = 75
    pixelHashSizeX = 10
    pixelHashSizeY = 10
    filter = learnFilter(::original, pixelHashFilterSize(), ::pixelHash)
    Picture { x, y -> if (original(x, y)) BLUE else if (filter[pixelHash(x, y)]) YELLOW else BLACK }
} + """

Вот теперь очень наглядно! И даже видно, что картинка похожа на обычное уменьшение разрешение (пикселизацию).

Вообще, фильтр Блума для монохромной картинки можно понимать как алгоритм сжатия данных с потерей информации: данные фильтра занимают меньше места, и по ним можно восстановить похожую картинку. И это первое нетривиальное наблюдение в этой статье.

Но возможности фильтра Блума гораздо шире, чем простая пикселизация. Тут, например, как раз интересно посмотреть, как выглядит использование нескольких хеш-функций (хотя бы двух). Количество хеш-функций даст второе измерение показателю "разрешение" (первое -- количество значений хеша). С другой стороны, можно поиграть с формой "пикселей": они совсем не обязаны быть прямоугольниками. Можно покрыть плоскость шестигранниками (построенные по фильтру изображения были бы менее угловатыми). А можно попытаться приблизиться к круглым пикселям (это челлендж!).

Начнем с нескольких хеш-функций. Вторую хеш-функций возьмём в принципе такую же, но сместим квадраты на половину стороны вправо и вниз. И для отображения значения функции будем использовать только один цветовой канал:

""" + {
    pixelHashSizeX = 50
    pixelHashSizeY = 50
    Picture { x, y -> toRGB(r = 7 * pixelHash2(x, y)) }
} + """

Так мы сможем показать сразу несколько (до трёх, но пока две) хеш-функций на одной картинке:

""" + Picture { x, y -> toRGB(r = 7 * pixelHash2(x, y), g = 10 * pixelHash(x, y)) } + """

Здесь зелёным и красным каналами показаны значения двух хеш-функций -- "пикселизация" и "пикселизация" со смещением. И на вид кажется, что мы получили в два раза большее "разрешение" по обеим осям. Но цвет показывает только где значения функций одинаковое, а где разное. Надо ещё определиться с выбором конкретных значений, которые будут индексами битовой карты фильтра. Если использовать совпадающие значения (прямо номера квадратов), понять, за какую часть картинки отвечает какой хеш, будет трудно. Наиболее наглядно будет разделить диапазоны значений для каждой функции полностью. То есть по-сути сделать два последовательных отдельных фильтра. При этом объём данных фильтра только удвоился, а не учетверился, как было бы при использовании только одной хеш-функции и удвоенного "разрешения". Посмотрим на это всё, построив два фильтра Блума, используя две "пиксельные" хеш-функции с размером квадрата 20x20:

""" + {
    pixelHashSizeX = 20
    pixelHashSizeY = 20
    val filter1 = learnFilter(::original, pixelHashFilterSize(), ::pixelHash)
    val filter2 = learnFilter(::original, pixelHash2FilterSize(), ::pixelHash2)
    Picture { x, y ->
        if (original(x, y)) BLUE else {
            val v1 = filter1[pixelHash(x, y)]
            val v2 = filter2[pixelHash2(x, y)]
            if (v1 && v2) YELLOW else {
                if (v1) darkRed else if (v2) darkGreen else BLACK
            }
        }
    }
} + """

Темно-красным и темно-зелёным показаны результаты каждого фильтра отдельно. А жёлтым -- их пересечение. Выглядит, как будто мы получили картинку как при размере пикселя 10x10, но за вдвое меньший объём данных. Конечно, чудес не бывает, и это не совсем так. Даже на этой картинке можно заметить, что жёлтых пикселей получилось немного больше, чем с одной хеш-функцией (и меньшим пикселем). Но нагляднее будет сравнить немного другую картинку. Давайте в качестве истинного множества возьмем два близко расположенных круга. И построим картинку для фильтра с одной хеш-функцией и пикселем 10x10:

""" + {
    originalRadius = 55
    pixelHashSizeX = 10
    pixelHashSizeY = 10
    filter = learnFilter(::original2, pixelHashFilterSize(), ::pixelHash)
    Picture { x, y -> if (original2(x, y)) BLUE else if (filter[pixelHash(x, y)]) YELLOW else BLACK }

} + """

И вторую картинку, для двух последовательных фильтров с пикселем 20x20.

""" + {
    pixelHashSizeX = 20
    pixelHashSizeY = 20
    val filter1 = learnFilter(::original2, pixelHashFilterSize(), ::pixelHash)
    val filter2 = learnFilter(::original2, pixelHash2FilterSize(), ::pixelHash2)
    Picture { x, y ->
        if (original2(x, y)) BLUE else {
            val v1 = filter1[pixelHash(x, y)]
            val v2 = filter2[pixelHash2(x, y)]
            if (v1 && v2) YELLOW else {
                if (v1) darkRed else if (v2) darkGreen else BLACK
            }
        }
    }
} + """

Теперь видно, что на второй картинке круги слиплись, а на первой "разрешения" хватило, чтобы показать их разделение. Дело в вероятностном характере фильтра Блума. В каких-то ситуациях везёт, и лишние пиксели не образуются. А в других ситуациях -- не везёт, как например между близко расположенными участками истинного множества. Можно сказать, что пикселизация в разных местах картинки с какой-то вероятностью может быть и 10x10, и 20x20. На ум приходят аналогии с квантовой запутанностью или фрактальной размерностью... ой, что это я, статья же не для Рен-тв. Давайте лучше посмотрим, как всё-таки выглядит полноценная экономия. То есть дадим хеш-функциям значения из единого диапазона и построим по ним один общий фильтр Блума:

""" + {
    filter = learnFilter(::original2, pixelHash2FilterSize(), ::pixelHash, ::pixelHash2)
    Picture { x, y ->
        if (original2(x, y)) BLUE else {
            val v1 = filter[pixelHash(x, y)]
            val v2 = filter[pixelHash2(x, y)]
            if (v1 && v2) YELLOW else {
                if (v1) darkRed else if (v2) darkGreen else BLACK
            }
        }
    }
} + """

Что ж, ложно-положительных результатов стало больше. Зато мы ещё в два раза уменьшили объём данных. Поведение конечно запуталось, картинка стала малопонятная. Но и по ней можно заметить кое-какие особенности работы Фильтра Блума. На картинке темно-зелёным и темно-красным всё так же обозначены отдельные совпадения по первой и по второй хеш-функции. Поскольку обе хеш-функции локальные, надо полагать, что далёкие от истинного множества совпадения получились, когда записано в фильтр значение было от одной хеш-функции, а совпало с другой. Вообще, наверно, все дополнительные (по сравнению с отдельными фильтрами) ложно-положительные срабатывания получились именно так, но это уже никак на картинке не видно.

Для очистки совести нарисуем ещё картинку, как выглядит две хеш - функции с пикселем 10x10. Добавление ещё одного хеша должно в теории картинку улучшить, и при этом ничего не стоит в плане объёма данных (по факту немножко добавляет, но это специфика выбора второй функции).

""" + {
    pixelHashSizeX = 10
    pixelHashSizeY = 10
    filter = learnFilter(::original2, pixelHash2FilterSize(), ::pixelHash, ::pixelHash2)
    Picture { x, y ->
        if (original2(x, y)) BLUE else {
            val v1 = filter[pixelHash(x, y)]
            val v2 = filter[pixelHash2(x, y)]
            if (v1 && v2) YELLOW else {
                if (v1) darkRed else if (v2) darkGreen else BLACK
            }
        }
    }
} + """

Да, всё плохо. Если присмотреться, можно даже найти место, где ожидаемый выигрыш случился (между кругами снизу). А в целом, вред от совпадений значений хеш-функций с запасом перевешивает. Возможно, хеш-функции выбраны неудачно. Но в любом случае вероятность совпадений очевидно будем меньше, если меньше будет степень заполнения фильтра, что в свою очереди определяется относительным размером истинного множества. Попробуем сделать круги поменьше.

""" + {
    originalRadius = 25
    filter = learnFilter(::original2, pixelHashFilterSize(), ::pixelHash)
    Picture { x, y -> if (original2(x, y)) BLUE else if (filter[pixelHash(x, y)]) YELLOW else BLACK }

} + """

""" + {
    filter = learnFilter(::original2, pixelHash2FilterSize(), ::pixelHash, ::pixelHash2)
    Picture { x, y ->
        if (original2(x, y)) BLUE else {
            val v1 = filter[pixelHash(x, y)]
            val v2 = filter[pixelHash2(x, y)]
            if (v1 && v2) YELLOW else {
                if (v1) darkRed else if (v2) darkGreen else BLACK
            }
        }
    }
} + """

Лучше стало, но теперь особенно заметно взаимное влияние хешей. Проблема похоже в том, что оба хеша локальные не только в том смысле, что коллизии расположены близко, но и сами значения хеша от соседних областей тоже рядом. А при таком как у нас характере истинного множества (много соседних элементов подряд) это сильно повышает вероятность паразитных совпадений. Попробуем значения одного из хешей перемешать (функционально заменить на псевдослучайные).

""" + {
    filterSize = pixelHash2FilterSize()
    filter = learnFilter(::original2, filterSize, ::pixelHash, ::pixelHash2Rnd)
    Picture { x, y ->
        if (original2(x, y)) BLUE else {
            val v1 = filter[pixelHash(x, y)]
            val v2 = filter[pixelHash2Rnd(x, y)]
            if (v1 && v2) YELLOW else {
                if (v1) darkRed else if (v2) darkGreen else BLACK
            }
        }
    }
} + """

Не то чтобы ложно-положительных результатов стало меньше, но картинка веселее. И в целом она лучше иллюстрирует, что далёкие от истинного множества совпадения -- случайны. Кажется, мы достаточно показали капризный характер фильтра Блума, совесть очистилась, и можно просто порисовать что-нибудь красивое с использованием одной (но не обычной) хеш-функции.

Вроде где-то в середине статьи зашла речь про необычные пиксели, как возможности локальной хеш-функции. Проще всего организовать локальность как близость к какому-то "центру". Давайте накидаем таких центров на картинку случайным образом, и каждой точке сопоставим номер ближайшего центра как хеш. Так можно совместить локальность и случайность в каком-то смысле. И да, получится [диаграмма Вороного](https://en.wikipedia.org/wiki/Voronoi_diagram).
 
""" + {
    val pictureSize = Point(0, 0).distanceTo(PICTURE_WIDTH, PICTURE_HEIGHT)
    val centersCount = 20
    val rnd = Random(42)
    val centers: List<Center> = (0 until centersCount).map { n ->
        Center(rnd.nextInt(PICTURE_WIDTH), rnd.nextInt(PICTURE_HEIGHT), n)
    }
    Picture { x, y ->
        val nearest = centers.findNearest(x, y)
        toHSB(
            h = 0.15f + nearest.n.toFloat() / centersCount / 5f,
            s = 1f,
            b = 1f - (4.5f * nearest.distanceTo(x, y) / pictureSize).toFloat()
        )
    }
} + """

Не смог удержаться и отобразил расстояние от центра яркостью (ну и набор цветов подобрал для красоты). С такой градиентной заливкой, кстати, и центры примерно видны. Но совсем случайные центры могут дать сильно неравномерную сетку, с областями существенно разного размера. А ещё у меня есть идея в пограничной зоне как-то смешать соседние области, чтобы не было резких границ и углов.

 Пытаясь придумать совсем простое решение без резких границ, я какое-то время шёл в сторону хеш-функции на базе двух (а может и больше) ближайших к каждой точке центров. Интересно было бы посмотреть, как это выглядит, этакое развитие диаграммы Вороного в сторону эллипсов должно получаться. Но резкие границы тут всё равно будут (когда один центр из пары меняется на другой). А хочется плавных переходов между всеми соседними областями. А для этого соседей, как ни крути, надо искать.    

 Так что сложность (либо вычислительная, либо в реализации) нужных алгоритмов начинает беспокоить. И вот если вспомнить, что это просто хеш, а расположение центров вообще-то произвольно, хочется воспользоваться выбором центров для улучшения сетки и упрощения алгоритмов.

 Идея такая: возьмем равномерную квадратную сетку (как в уже использовавшемся ранее "пиксельном" хеше), и в каждом квадрате поставим 1 или 2 центра (количество и положение центров выберем псевдослучайно по номеру квадрата). Наверно, для равномерности ещё стоит от краёв квадратов немного отступать. Центры по квадрату можно будет дёшево находить (можно в хеш-таблицу сложить, или налету вычислять). А поиск соседних центров (и друг к другу, и к произвольной точке) можно будет ограничить только соседними квадратами. Соседних квадратов максимум 9, то есть проверять надо максимум 18 центров, и это допустимо сделать простым прямым перебором. Скорее всего такое уже делали, но как называется -- я не нашёл (если кто подскажет, буду признателен).

Построим сетку, центры и их приблизительную [триангуляцию Делоне](https://en.wikipedia.org/wiki/Delaunay_triangulation).

""" + {
    pixelHashSizeX = 50
    pixelHashSizeY = 50
    marginX = 5
    marginY = 5
    val v = VoronoiDelaunay()
    val picture = Picture { x, y ->
        if (x % pixelHashSizeX == 0 || y % pixelHashSizeY == 0) BLUE
        else {
            val (center, distance) = v.findNearestNeighbor(x, y)
            if (distance < center.radius) {
                v.color(center.n)
            } else {
                toHSB(v.colorH(center.n), 1.0f, 0.5f)
            }
        }
    }
    val graphics = picture.image.createGraphics()
    graphics.color = WHITE
    v.centers.forEach { a ->
        a.neighbors.forEach { (neighbor, _) ->
            graphics.drawLine(a.x, a.y, neighbor.x, neighbor.y)
        }
    }
    picture
} + """

Ребра триангуляции соединяют только соседние центры. Для нахождения соседей берём всё центры из соседних ячеек сетки, и проверяем, что для точки на полпути между ними ближайшими центрами они и являются. То есть что середина ребра триангуляции находится на границе соседних областей диаграммы Вороного. Как видно, такой подход иногда дает сбой, если граница между областями не пересекает потенциальное ребро триангуляции. То есть некоторых соседей мы потеряем, но с этим можно смириться. 

""" + {
    val vd = VoronoiDelaunay()
    Picture { x, y ->
        val (center, distance) = vd.findNearestNeighbor(x, y)
        if (distance < center.radius) {
            vd.color(center.n)
        } else {
            val r = Vector(center.x - x, center.y - y).ort()
            val n = r.rotate90ccw()
            val leftNeighbors = ArrayList<Pair<Center, Projection>>()
            val rightNeighbors = ArrayList<Pair<Center, Projection>>()
            center.neighbors.forEach { (neighbor, _) ->
                val v = Vector(neighbor.x - center.x, neighbor.y - center.y).ort()
                if (v * n > 0) {
                    leftNeighbors += Pair(neighbor, v * r)
                } else {
                    rightNeighbors += Pair(neighbor, v * r)
                }
            }
            val resultNeighbors = ArrayList<Center>()
            resultNeighbors += center
            if (leftNeighbors.isNotEmpty()) {
                resultNeighbors += leftNeighbors.maxBy { it.second }.first
            }
            if (rightNeighbors.isNotEmpty()) {
                resultNeighbors += rightNeighbors.maxBy { it.second }.first
            }
            toHSB(resultNeighbors.map { vd.colorH(it.n) }.average().toFloat(), 1.0f, 0.5f)
        }
    }
} + """


kotlin-jupyter-kernel

"""


fun main() {
    println(article)
}