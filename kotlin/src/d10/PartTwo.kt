package d10

import readInputStringFile
import kotlin.math.*

data class AsteroidInfo(val p: Position, val angle: Double, val distance: Double, var isVaporized: Boolean)

class PartTwo(val input: String, private val objectif: Int) {

    private lateinit var bestPosition: Position
    private lateinit var map: Array<BooleanArray>
    private var asteroidsInfos = mutableListOf<AsteroidInfo>()
    lateinit var debugMap: Array<Array<String>>
    private var vaporized = 0;
    private lateinit var positionObjectif: Position

    private fun getAsteroidInfo(p: Position): AsteroidInfo {
        val offsetX = p.x.toDouble()- bestPosition.x
        val offsetY = bestPosition.y - p.y.toDouble()
        val angleInRadian = atan2(offsetX,offsetY)
        val angleInDegree = Math.toDegrees(angleInRadian)
        val distance = sqrt(offsetX.pow(2.0) + offsetY.pow(2.0))
        return AsteroidInfo(p, if(angleInDegree < 0) angleInDegree + 360 else angleInDegree, distance, false)
    }

    private fun vaporize(asteroid: AsteroidInfo) {
        asteroid.isVaporized = true
        vaporized++
        debugMap[asteroid.p.y][asteroid.p.x] = vaporized.toString()
        if(vaporized == objectif) positionObjectif = asteroid.p
        println("$vaporized : $asteroid vaporized !")
    }

    private fun dumpDebug() {
        for (row in debugMap) {
            println(row.joinToString(" ") { it })
        }
    }

    private fun buildInfoMap() {
        for ((y, row) in map.withIndex()) {
            for (x in row.indices) {
                val p = Position(x, y)
                if (map[y][x] && p != bestPosition) asteroidsInfos.add(getAsteroidInfo(p))
            }
        }
    }

    fun run() {
        val partOne = PartOne(input)
        bestPosition = partOne.run()
        println("Scanning input width=${partOne.mapWidth} height=${partOne.mapHeight}")
        map = input.lines().toTypedArray().map { it.toCharArray().map { c -> c == '#' }.toBooleanArray() }.toTypedArray()
        debugMap = input.lines().toTypedArray().map { it.toCharArray().map { c -> c.toString() }.toTypedArray() }.toTypedArray()
        debugMap[bestPosition.y][bestPosition.x] = "X"
        val totalAsteroids = map.map { it.count { c -> c } }.sum() - 1
        buildInfoMap()
        asteroidsInfos.sortWith(compareBy({ it.angle }, { it.distance }))
        while (vaporized != totalAsteroids) {
            var currentAngle = Double.MIN_VALUE
            for ((i, info) in asteroidsInfos.withIndex()) {
                if (info.isVaporized || info.angle == currentAngle) continue
                vaporize(info)
                dumpDebug()
                currentAngle = info.angle
            }
        }
        println("Objectif $objectif atteint à la position $positionObjectif, soit ${positionObjectif.x * 100 + positionObjectif.y}")
    }
}

fun main() {
    val input = readInputStringFile("d10/input.txt")
    PartTwo(input, 200).run()
}