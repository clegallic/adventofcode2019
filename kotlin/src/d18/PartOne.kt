package d18

import Logger
import Position2D
import padTwoDigits
import readInputStringFile
import kotlin.math.min

val logger = Logger(false)

enum class Orientation(val offset: Position2D) {
    DOWN(Position2D(0, 1)),
    RIGHT(Position2D(1, 0)),
    UP(Position2D(0, -1)),
    LEFT(Position2D(-1, 0)),
}

enum class MazeLocationType(val symbol: Char) {
    WALL('#'), KEY('⚩'), DOOR('⛩'), EMPTY('.'), ENTRANCE('@')
}

data class Key(val letter: Char) {
    val dependsOn = mutableListOf<Key>()
    lateinit var location: KeyMazeLocation
    override fun toString(): String {
        return letter.toString()
    }
}

data class Door(val letter: Char)
open class MazeLocation(val position: Position2D, val type: MazeLocationType, var visited: Boolean = false) {
    open fun display(): Char {
        return type.symbol
    }
    override fun toString(): String {
        return "$type"
    }
}

class EntranceMazeLocation(position: Position2D) : MazeLocation(position, MazeLocationType.ENTRANCE, true)
class EmptyMazeLocation(position: Position2D) : MazeLocation(position, MazeLocationType.EMPTY) {
    override fun toString(): String {
        return "$type $position"
    }
}

class WallMazeLocation(position: Position2D) : MazeLocation(position, MazeLocationType.WALL)
class KeyMazeLocation(val key: Key, position: Position2D) : MazeLocation(position, MazeLocationType.KEY) {
    override fun display(): Char {
        return key.letter
    }
    override fun toString(): String {
        return "$type '${key.letter}'"
    }
}

class DoorMazeLocation(val door: Door, position: Position2D) : MazeLocation(position, MazeLocationType.DOOR) {
    override fun display(): Char {
        return door.letter
    }
    override fun toString(): String {
        return "$type '${door.letter}'"
    }
}

class Maze(val data: Array<Array<MazeLocation>>) {
    var entrance: EntranceMazeLocation? = null
    val height = data.size
    val width = data[0].size
    val keys = mutableMapOf<Char, Key>()
    val doors = mutableMapOf<Char, Door>()
    val locations = mutableMapOf<Position2D, MazeLocation>()
}

class MazeSolver(val input: String) {

    private lateinit var maze: Maze

    fun solvePartOne(): Int {
        parseMaze()
        printMaze()
        analyzeMaze()
        val shortestPath = findShortestPath()
        logger.info("The shortest path is $shortestPath")
        return shortestPath
    }

    private fun printMaze() {
        println(maze.data[0].foldIndexed("   ") { index, acc, _ -> acc.plus(index.padTwoDigits()).plus(" ") })
        println(maze.data.mapIndexed { index, it -> index.padTwoDigits() + " " + it.joinToString("  ") { mazeLocation -> mazeLocation.display().toString() } + "\r\n" }.joinToString(""))
    }

    private fun parseMaze() {
        val mazeWidth = input.lines()[0].length
        val mazeHeight = input.lines().size
        val mazeData = Array(mazeHeight) { Array<MazeLocation>(mazeWidth) { EmptyMazeLocation(Position2D(-1, -1)) } }
        maze = Maze(mazeData)
        for (line in input.lines().withIndex()) {
            for (char in line.value.withIndex()) {
                val x = char.index
                val y = line.index
                val position = Position2D(char.index, line.index)
                mazeData[y][x] = when (char.value) {
                    MazeLocationType.ENTRANCE.symbol -> {
                        maze.entrance = EntranceMazeLocation(position)
                        maze.entrance!!
                    }
                    MazeLocationType.WALL.symbol -> WallMazeLocation(position)
                    in 'a'..'z' -> {
                        val key = Key(char.value)
                        maze.keys[char.value] = key
                        key.location = KeyMazeLocation(key, position)
                        key.location
                    }
                    in 'A'..'Z' -> {
                        val door = Door(char.value)
                        maze.doors[char.value] = door
                        DoorMazeLocation(door, position)
                    }
                    else -> EmptyMazeLocation(position)
                }
                maze.locations[position] = mazeData[y][x]
            }
        }
        logger.debug("Maze parsed : ${maze.keys.size} keys found, ${maze.doors.size} doors found")
    }

    private fun analyzeMaze() {
        walk(maze.entrance!!, listOf(maze.entrance!!))
    }

    private fun walk(current: MazeLocation, previousLocations: List<MazeLocation>, previousDoors: List<Door> = arrayListOf(), previousReachableKeys: List<Key> = arrayListOf()) {
        val doors = previousDoors.toMutableList()
        val keys = previousReachableKeys.toMutableList()
        val walkableLocations = walkableLocations(current)
        if (walkableLocations.isEmpty()) logger.debug("No more walkable locations")
        for (l in walkableLocations) {
            val locations = previousLocations.toMutableList()
            when (l) {
                is KeyMazeLocation -> {
                    l.key.dependsOn.addAll(keys)
                    keys.add(l.key)
                    logger.debug("$l found at position : ${l.position}, ${previousLocations.size} from entrance. Previous doors : $doors")
                }
                is DoorMazeLocation -> {
                    doors.add(l.door)
                    keys.add(doorToKey(l.door)!!)
                    logger.debug("$l found at position : ${l.position}, ${previousLocations.size} from entrance")
                }
                else -> logger.debug("$l visited, ${locations.size} previous locations found")
            }
            l.visited = true
            locations.add(l)
            walk(l, locations, doors, keys)
        }
    }

    private fun walkableLocations(current: MazeLocation): List<MazeLocation> {
        val walkable = arrayListOf<MazeLocation>()
        for (orientation in Orientation.values()) {
            val targetPosition = current.position.withOffset(orientation.offset)
            val alreadyVisited = maze.locations[targetPosition]!!.visited
            if (targetPosition.x >= 0 && targetPosition.y >= 0 && targetPosition.x < maze.width && targetPosition.y < maze.height && !alreadyVisited) {
                val targetLocation = maze.data[targetPosition.y.toInt()][targetPosition.x.toInt()]
                if (targetLocation.type != MazeLocationType.WALL) {
                    walkable.add(targetLocation)
                }
            }
        }
        return walkable
    }

    private fun findShortestPath(): Int {
        return minDistanceToCollectNextKeys(maze.entrance!!, maze.keys.values.toList())
    }

    private data class CacheEntry(val location: MazeLocation, val nextKeys: List<Key>)

    private val minDistanceToCollectNextKeysCache = mutableMapOf<CacheEntry, Int>()

    private fun minDistanceToCollectNextKeys(currentLocation: MazeLocation, keysToCollect: List<Key>): Int {
        if (keysToCollect.isEmpty()) {
            return 0
        }
        val keyToNextKeys = CacheEntry(currentLocation, keysToCollect)
        if (keyToNextKeys in minDistanceToCollectNextKeysCache.keys) {
            return minDistanceToCollectNextKeysCache[keyToNextKeys]!!
        }
        var minDistance = Int.MAX_VALUE
        reachableKeys(keysToCollect).forEach { reachableKey ->
            val minDistanceToReachable = distance(currentLocation, reachableKey.location) + minDistanceToCollectNextKeys(reachableKey.location, keysToCollect - reachableKey)
            minDistance = min(minDistance, minDistanceToReachable)
        }
        minDistanceToCollectNextKeysCache[keyToNextKeys] = minDistance
        return minDistance
    }

    private fun reachableKeys(keysToCollect: List<Key>): List<Key> {
        return keysToCollect.filter { it.dependsOn.intersect(keysToCollect).isEmpty() }
    }

    private val distanceCache = mutableMapOf<Pair<MazeLocation, MazeLocation>, Int>()

    private fun distance(from: MazeLocation, to: MazeLocation): Int {
        val cacheKey = Pair(from, to)
        if (!distanceCache.containsKey(cacheKey)) {
            distanceCache[cacheKey] = computeDistance(from, to)
        }
        return distanceCache[cacheKey]!!
    }

    private fun computeDistance(from: MazeLocation, to: MazeLocation): Int{
        if (from == to) {
            return 0
        }
        val queue = mutableListOf<Pair<MazeLocation, Int>>()
        val dist = 0
        queue.add(Pair(from, dist))
        val seen = mutableMapOf<MazeLocation, Int>()
        seen[from] = dist
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            for (orientation in Orientation.values()) {
                val otherPosition = current.first.position.withOffset(orientation.offset)
                val otherLocation = maze.data[otherPosition.y.toInt()][otherPosition.x.toInt()]
                if (seen.containsKey(otherLocation) || otherLocation is WallMazeLocation) {
                    continue
                }
                val distT = current.second + 1
                seen[otherLocation] = distT
                if (otherLocation == to) {
                    return distT
                } else {
                    queue.add(Pair(otherLocation, distT))
                }
            }
        }
        throw IllegalStateException()
    }

    private fun doorToKey(door: Door): Key? {
        return maze.keys[door.letter.toLowerCase()]
    }

}

fun runPartOne(input: String): Int {
    val solver = MazeSolver(input)
    return solver.solvePartOne()
}

fun main() {
    for (i in 1..4) {
        val sample = readInputStringFile("d18/sample$i.txt")
        println(runPartOne(sample))
    }
    val input = readInputStringFile("d18/input.txt")
    println(runPartOne(input))
}