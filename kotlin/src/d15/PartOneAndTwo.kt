package d15

import Logger
import LongCodeProgram
import Position2D
import readInputCommaSeparatedFileAsLong
import kotlin.math.max

enum class Movement(val value: Long, val offset: Position2D) {
    NORTH(1, Position2D(0, -1)),
    SOUTH(2, Position2D(0, 1)),
    WEST(3, Position2D(-1, 0)),
    EAST(4, Position2D(1, 0));

    fun backward(): Movement {
        return when (this) {
            NORTH -> SOUTH
            SOUTH -> NORTH
            WEST -> EAST
            EAST -> WEST
        }
    }
}

enum class StatusCode(val code: Long) {
    WALL_HIT(0), MOVED(1), OXYGEN_FOUND(2), UNKNOWN(-1);

    companion object {
        fun ofCode(value: Long): StatusCode = values().find { it.code == value } ?: UNKNOWN
    }
}

enum class MapLocationType(val symbol: Char) {
    WALL('▉'), EMPTY('.'), FILLED_WITH_OXYGEN('o'), DROID_START('D'), OXYGEN_SYSTEM('O'), UNEXPLORED(' ');

    companion object {
        fun isFillable(type: MapLocationType): Boolean = type == EMPTY || type == DROID_START
    }
}

class MapLocation(val position: Position2D, var type: MapLocationType) {
    private val movementsMade = arrayListOf<Movement>()
    var hasMadeAllMovements = false
    fun nextMovement(): Movement? {
        if (hasMadeAllMovements) return null
        val next = Movement.values().find { movement -> movementsMade.indexOf(movement) == -1 }!!
        movementsMade.add(next)
        hasMadeAllMovements = movementsMade.size == Movement.values().size
        return next
    }

    override fun toString(): String {
        return "$position : ${type.name}"
    }
}

lateinit var input: LongArray
lateinit var droidProgram: LongCodeProgram
var fewestNumberOfMovementToFindOxygen = -1
val map = arrayListOf<MapLocation>()
val logger = Logger()

fun printShipMap() {
    val boundaries = map.fold(Pair(Position2D(0, 0), Position2D(0, 0))) { acc, location ->
        val lowestX = if (location.position.x < acc.first.x) location.position.x else acc.first.x
        val lowestY = if (location.position.y < acc.first.y) location.position.y else acc.first.y
        val highestX = if (location.position.x > acc.second.x) location.position.x else acc.second.x
        val highestY = if (location.position.y > acc.second.y) location.position.y else acc.second.y
        Pair(Position2D(lowestX, lowestY), Position2D(highestX, highestY))
    }
    val width = (boundaries.second.x - boundaries.first.x).toInt() + 1
    val height = (boundaries.second.y - boundaries.first.y).toInt() + 1
    val mapArray = Array(height) { Array(width) { MapLocationType.UNEXPLORED.symbol } }
    map.forEach { mapArray[(it.position.y - boundaries.first.y).toInt()][(it.position.x - boundaries.first.x).toInt()] = it.type.symbol }
    for (line in mapArray) {
        logger.info(line.joinToString(separator = " "))
    }

}

fun existingMapLocation(targetPosition: Position2D): MapLocation? {
    return map.find { it.position == targetPosition }
}

fun alreadyExplored(targetPosition: Position2D): Boolean {
    return map.find { it.position == targetPosition } != null
}

fun findNextMovement(currentDroidMapLocation: MapLocation, movements: MutableList<Movement>): Movement {
    var nextMovement: Movement? = null
    when {
        movements.isEmpty() -> {
            nextMovement = currentDroidMapLocation.nextMovement()!!
        }
        currentDroidMapLocation.hasMadeAllMovements -> {
            nextMovement = movements.last().backward()
            movements.removeLast()
        }
        else -> {
            do {
                val possibleMovement = currentDroidMapLocation.nextMovement()!!
                val possibleNextPosition = currentDroidMapLocation.position.withOffset(possibleMovement.offset)
                if (!alreadyExplored(possibleNextPosition)) {
                    nextMovement = possibleMovement
                } else if (currentDroidMapLocation.hasMadeAllMovements) {
                    nextMovement = movements.last().backward()
                    movements.removeLast()
                }
            } while (nextMovement == null)
        }
    }
    return nextMovement
}

fun gotoToNextMapLocation(targetPosition: Position2D, type: MapLocationType, movements: MutableList<Movement>, movement: Movement): MapLocation {
    var nextDroidMapLocation = existingMapLocation(targetPosition)
    if (nextDroidMapLocation == null) { // Newly explored area
        nextDroidMapLocation = MapLocation(targetPosition, type)
        map.add(nextDroidMapLocation)
        movements.add(movement)
    }
    return nextDroidMapLocation
}

tailrec fun exploreShip(currentDroidMapLocation: MapLocation, movements: MutableList<Movement>) {
    if (movements.isEmpty() && map.size > 2) {
        logger.debug("All ship explored")
    } else {
        val nextMovement = findNextMovement(currentDroidMapLocation, movements)
        var nextDroidMapLocation: MapLocation = currentDroidMapLocation
        val targetPosition = currentDroidMapLocation.position.withOffset(nextMovement.offset)
        logger.debug("Droid at ${currentDroidMapLocation.position} trying to move to ${nextMovement.name}")
        droidProgram.addInput(nextMovement.value)
        val output = droidProgram.run().output
        when (StatusCode.ofCode(output[0])) {
            StatusCode.WALL_HIT -> {
                logger.debug("Wall found at position $targetPosition")
                val wallMapInfo = MapLocation(targetPosition, MapLocationType.WALL)
                map.add(wallMapInfo)
            }
            StatusCode.MOVED -> {
                nextDroidMapLocation = gotoToNextMapLocation(targetPosition, MapLocationType.EMPTY, movements, nextMovement)
                logger.debug("Droid moved to position $targetPosition")
            }
            StatusCode.OXYGEN_FOUND -> {
                nextDroidMapLocation = gotoToNextMapLocation(targetPosition, MapLocationType.OXYGEN_SYSTEM, movements, nextMovement)
                fewestNumberOfMovementToFindOxygen = movements.size
            }
            else -> logger.info("Status code unknown")
        }
        exploreShip(nextDroidMapLocation, movements)
    }
}

var maxSteps = -1L

fun findAdjacentFillableLocations(mapLocation: MapLocation): List<MapLocation> {
    return map.filter { it.position.isAdjacent(mapLocation.position) && MapLocationType.isFillable(it.type) }
}

fun fillWithOxygen(mapLocation: MapLocation, nbSteps: Long) {
    maxSteps = max(maxSteps, nbSteps)
    mapLocation.type = MapLocationType.FILLED_WITH_OXYGEN
    findAdjacentFillableLocations(mapLocation).forEach { fillWithOxygen(it, nbSteps + 1) }
}

fun fillShipWithOxygen() {
    fillWithOxygen(map.find { it.type == MapLocationType.OXYGEN_SYSTEM }!!, 0)
}

fun runPartOneAndTwo() {
    droidProgram = LongCodeProgram(input)
    droidProgram.clearOutputAfterRun = true
    droidProgram.debug = false
    val startMapInfo = MapLocation(Position2D(0, 0), MapLocationType.DROID_START)
    map.add(startMapInfo)
    exploreShip(startMapInfo, mutableListOf())
    printShipMap()
    logger.info("Oxygen found after $fewestNumberOfMovementToFindOxygen movements")
    fillShipWithOxygen()
    printShipMap()
    logger.info("It take $maxSteps minutes to fill the ship with oxygen ")
}

fun main() {
    input = readInputCommaSeparatedFileAsLong("d15/input.txt")
    runPartOneAndTwo()
}