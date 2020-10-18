package d10

import readInputStringFile
import kotlin.math.abs

class Position(val x: Int, val y: Int) {
    fun offset(o: Pair<Int, Int>): Position{
        return Position(x + o.first, y + o.second)
    }
    override fun toString(): String {
        return "(x=$x,y=$y)"
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Position) return false
        return other.x == this.x && other.y == this.y
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }
}

class PartOne(val input: String) {

    var mapWidth = 0
    var mapHeight = 0
    private lateinit var map: List<List<Boolean>>
    private val sightDirections = arrayOf(Pair(-1, -1), Pair(0, -1), Pair(1, -1), Pair(-1, 0), Pair(1, 0), Pair(-1, 1), Pair(0, 1), Pair(1, 1))

    private fun isInMap(position: Position): Boolean{
        return (position.x in 0 until mapWidth) && (position.y in 0 until mapHeight)
    }

    private fun hasAsteroidInDirection(position: Position, direction: Pair<Int, Int>): Boolean{
        var nextPosition = position.offset(direction)
        while(isInMap(nextPosition) ){
            if(map[nextPosition.y][nextPosition.x]) {
                return true
            }
            nextPosition = nextPosition.offset(direction)
        }
        return false
    }

    private fun asteroidsInDirectSight(p: Position): Int{
        var detectedAsteroids = 0
        for (d in sightDirections) {
            if (hasAsteroidInDirection(p, d)) {
                detectedAsteroids++
            }
        }
        return detectedAsteroids
    }

    // Algorithme d'Euclide : https://en.wikipedia.org/wiki/Euclidean_algorithm
    private fun gcf(a: Int, b: Int): Int {
        return if (b == 0) a else gcf(b, a % b)
    }

    private fun simplify(num: Int, denum: Int): Pair<Int, Int>{
        val gcf = gcf(abs(num), abs(denum))
        return Pair(num / gcf, denum / gcf)
    }

    private fun isNotInDirectSight(p: Position, asteroid: Position): Boolean{
        return p.x != asteroid.x && p.y != asteroid.y
                && abs(p.x - asteroid.x) != abs(p.y - asteroid.y)
    }

    private fun isInSight(p: Position, asteroid: Position): Boolean{
        val offset = simplify(asteroid.x - p.x,  asteroid.y - p.y)
        var nextPosition = p.offset(offset)
        while(!map[nextPosition.y][nextPosition.x] && nextPosition != asteroid){
            nextPosition = nextPosition.offset(offset)
        }
        return nextPosition == asteroid
    }

    private fun asteroidsNotInDirectSight(p: Position): Int{
        var detectedAsteroids = 0
        for((y, row) in map.withIndex()) {
            for ((x, isAsteroid) in row.withIndex()) {
                val a = Position(x, y)
                if(isAsteroid && isNotInDirectSight(p, a) && isInSight(p, a)){
                    detectedAsteroids++
                }
            }
        }
        return detectedAsteroids
    }

    private fun dumpResult(result: Array<IntArray>){
        for(row in result){
            println(row.contentToString())
        }
    }

    fun run(): Position{
        map = input.lines().toTypedArray().map { it.toCharArray().map { c -> c == '#' } }
        mapHeight = map.size
        mapWidth = map.first().size
        var bestPosition = Position(-1,-1)
        var maxAsteroids = 0
        val sightMap = Array(mapHeight){ IntArray(mapWidth){0} }
        for((y, row) in map.withIndex()){
            for((x, isAsteroid) in row.withIndex()){
                if(isAsteroid) {
                    var detectedAsteroids = 0
                    val p = Position(x, y)
                    detectedAsteroids += asteroidsInDirectSight(p) + asteroidsNotInDirectSight(p)
                    sightMap[y][x] = detectedAsteroids
                    if(maxAsteroids < detectedAsteroids) {
                        maxAsteroids = detectedAsteroids
                        bestPosition = p
                    }
                }
            }
        }
        println("Best is '$bestPosition' with '$maxAsteroids' other asteroids detected")
        //dumpResult(sightMap)
        return bestPosition
    }
}

fun main(){
    val input = readInputStringFile("d10/input.txt")
    PartOne(input).run()
}