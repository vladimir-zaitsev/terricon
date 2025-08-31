package ru.ya.vsz.terricon.art

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import javax.imageio.ImageIO


class Article(
    private val filePath: String = "src/main/resources/article/",
    mainFileName: String = "output.md",
) {
    private val mainFilePath = Paths.get(filePath, mainFileName)
    private val picturesCounter = AtomicInteger()
    private val fileStarted = AtomicBoolean(false)

    operator fun plus(text: String): Article {
        val bytes = text.toByteArray(StandardCharsets.UTF_8)
        if (fileStarted.compareAndSet(false, true)) {
            Files.write(mainFilePath, bytes, CREATE, TRUNCATE_EXISTING)
        } else {
            Files.write(mainFilePath, bytes, APPEND)
        }
        return this
    }

    open class Picture(
        private val width: Int,
        private val height: Int,
        pixelColorFunction: (x: Int, y: Int) -> Color,
    ) {
        val image: BufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

        init {
            for (y in 0 until height) {
                for (x in 0 until width) {
                    image.setRGB(x, y, pixelColorFunction(x, y).rgb)
                }
            }
        }
    }

    operator fun plus(picture: Picture): Article {
        val fileName = "pic" + picturesCounter.incrementAndGet() + ".png"
        ImageIO.write(picture.image, "png", File(filePath, fileName))
        plus("![]($fileName)")
        return this
    }

    operator fun plus(pictureFactory: () -> Picture) = plus(pictureFactory.invoke())
    override fun toString(): String {
        return "Article saved to file $mainFilePath"
    }
}
