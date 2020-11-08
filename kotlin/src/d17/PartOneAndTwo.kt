package d17

import Logger
import LongCodeProgram
import Position2D
import padTwoDigits
import readInputCommaSeparatedFileAsLong
import java.util.*

enum class Turn(val char: Char) { LEFT('L'), RIGHT('R'), NONE('N') }

enum class Orientation(val offset: Position2D) {
    UP(Position2D(0, -1)),
    RIGHT(Position2D(1, 0)),
    DOWN(Position2D(0, 1)),
    LEFT(Position2D(-1, 0));

    fun inverse(): Orientation {
        return when (this) {
            UP -> DOWN
            RIGHT -> LEFT
            DOWN -> UP
            LEFT -> RIGHT
        }
    }

    companion object {
        fun turn(from: Orientation, to: Orientation): Turn {
            return when {
                from == UP && to == RIGHT -> Turn.RIGHT
                from == UP && to == LEFT -> Turn.LEFT
                from == RIGHT && to == DOWN -> Turn.RIGHT
                from == RIGHT && to == UP -> Turn.LEFT
                from == DOWN && to == LEFT -> Turn.RIGHT
                from == DOWN && to == RIGHT -> Turn.LEFT
                from == LEFT && to == UP -> Turn.RIGHT
                from == LEFT && to == DOWN -> Turn.LEFT
                else -> Turn.NONE
            }
        }
    }
}

data class RobotMovement(val from: Position2D, val to: Position2D, val orientation: Orientation, val turn: Turn)

data class Function(val letter: Char, val instructions: List<String>)

val logger = Logger(false)
lateinit var input: LongArray
const val NEW_LINE_CHAR = 10.toChar()
const val NEW_LINE = NEW_LINE_CHAR.toLong()
const val SHARP_CHAR = '#'
const val UP_CHAR = '^'
const val COMMA = ','.toLong()
const val MAX_DEFINITION_CHARS = 20

fun displayCameraOutput(cameraOutput: List<List<Char>>, withAxis: Boolean = false) {
    if(withAxis) logger.debug(cameraOutput[0].foldIndexed("    ") { index, acc, c -> acc.plus(index.padTwoDigits()).plus(" ") })
    cameraOutput.forEachIndexed { index, line -> logger.debug("${if(withAxis) index.padTwoDigits().plus(" | ") else ""}${line.toTypedArray().joinToString(separator = "  ")}") }
}

fun parseCameraOutput(output: LongArray): List<List<Char>> {
    val cameraOutput = mutableListOf<MutableList<Char>>()
    var line = mutableListOf<Char>()
    for (c in output) {
        if (c == NEW_LINE && line.isNotEmpty()) {
            cameraOutput.add(line)
            line = mutableListOf()
        } else {
            line.add(c.toChar())
        }
    }
    return cameraOutput
}

fun sumAlignementParameters(cameraOutput: List<List<Char>>): Long {
    val crossings = arrayListOf<Position2D>()
    for ((y, line) in cameraOutput.dropLast(1).withIndex()) {
        if (y == 0) continue
        for ((x, c) in line.dropLast(1).withIndex()) {
            if (x == 0) continue
            if (c == SHARP_CHAR && cameraOutput[y][x - 1] == SHARP_CHAR && cameraOutput[y - 1][x] == SHARP_CHAR && cameraOutput[y + 1][x] == SHARP_CHAR && cameraOutput[y][x + 1] == SHARP_CHAR) {
                crossings.add(Position2D(x.toLong(), y.toLong()))
            }
        }
    }
    return crossings.fold(0L) { acc, position2D -> acc + position2D.x * position2D.y }
}

fun calibrateCameras(): Long {
    val asciiProgram = LongCodeProgram(input)
    asciiProgram.clearOutputAfterRun = true
    asciiProgram.debug = false
    val output = asciiProgram.run().output
    val cameraOutput = parseCameraOutput(output)
    return sumAlignementParameters(cameraOutput)
}

fun findRobotStartPosition(cameraOutput: List<List<Char>>): Position2D {
    val y = cameraOutput.indexOfFirst { it.contains(UP_CHAR) }
    val x = cameraOutput[y].indexOf(UP_CHAR)
    return Position2D(x, y)
}

fun findNextRobotMovement(current: Position2D, currentOrientation: Orientation, cameraOutput: List<List<Char>>): RobotMovement? {
    var nextPosition: Position2D? = null
    var nextOrientation: Orientation = currentOrientation
    for (orientation in Orientation.values()) {
        if (orientation == currentOrientation.inverse()) continue // Skip where we are coming from
        val adjacent = current.withOffset(orientation.offset)
        if (adjacent.x >= 0 && adjacent.y >= 0 && adjacent.x < cameraOutput[0].size && adjacent.y < cameraOutput.size - 1
                && cameraOutput[adjacent.y.toInt()][adjacent.x.toInt()] == SHARP_CHAR) {
            nextPosition = adjacent
            nextOrientation = orientation
            if (currentOrientation == orientation) { // Priority to current orientation (for crossings)
                break
            }
        }
    }
    return if (nextPosition != null) {
        RobotMovement(current, nextPosition, nextOrientation, Orientation.turn(currentOrientation, nextOrientation))
    } else {
        null // End of the path
    }
}

fun buildVacuumRobotPath(cameraOutput: List<List<Char>>): List<String> {
    val robotStartPosition = findRobotStartPosition(cameraOutput)
    logger.debug("Vacuum robot is at $robotStartPosition")
    val movements = arrayListOf<String>()
    var linearSteps = 0
    var movement: RobotMovement? = RobotMovement(robotStartPosition, robotStartPosition, Orientation.UP, Turn.NONE)
    do {
        movement = findNextRobotMovement(movement!!.to, movement.orientation, cameraOutput)
        if (movement != null) {
            if (movement.turn == Turn.NONE) {
                linearSteps++
            } else {
                if (linearSteps > 0) {
                    movements.add(linearSteps.toString())
                }
                movements.add(movement.turn.char.toString())
                linearSteps = 1
            }
        } else {
            if (linearSteps > 0) {
                movements.add(linearSteps.toString())
            }
        }
    } while (movement != null)
    logger.debug("Movements : ${movements.joinToString()}")
    return movements
}

fun findFunctionInstructions(movementList: List<String>): List<String>? {
    var candidate: List<String>? = null
    var cursorIndex = 4
    var maxCharReached = false
    while (!maxCharReached && cursorIndex < movementList.size) {
        val leftList = movementList.subList(0, cursorIndex)
        if (leftList.joinToString(",").length <= MAX_DEFINITION_CHARS) {
            val rightList = movementList.subList(cursorIndex, movementList.size)
            if (Collections.indexOfSubList(rightList, leftList) != -1) {
                candidate = leftList.toList()
            }
            cursorIndex += 2
        } else {
            maxCharReached = true
        }
    }
    return candidate
}

fun findFunctions(movements: List<String>): List<Function> {
    val functionsNames = mutableListOf('A', 'B', 'C')
    val functions = arrayListOf<Function>()
    val tempList = movements.toMutableList()
    while (functions.size < 3) {
        val part = findFunctionInstructions(tempList)
        if (part != null) {
            functions.add(Function(functionsNames.removeFirst(), part))
            do { // Remove occurence of this function in the movement list
                val partPos = Collections.indexOfSubList(tempList, part)
                if (partPos != -1) {
                    tempList.subList(partPos, partPos + part.size).clear()
                }
            } while (partPos != -1)
        }
    }
    return functions
}

fun buildMainRoutine(movements: List<String>, functions: List<Function>): List<Function> {
    val mainRoutine = arrayListOf<Function>()
    var index = 0
    while (index < movements.size) {
        for (function in functions) {
            if (index + function.instructions.size <= movements.size && movements.subList(index, index + function.instructions.size) == function.instructions) {
                mainRoutine.add(function)
                index += function.instructions.size
            }
        }
    }
    return mainRoutine
}

fun buildInputInstructions(movements: List<String>, functions: List<Function>): List<Long> {
    val mainRoutine = buildMainRoutine(movements, functions)
    logger.debug("Main routine : ${mainRoutine.joinToString()}")
    val instructions = mainRoutine.flatMap { arrayListOf(it.letter.toLong(), COMMA) }.dropLast(1).plus(NEW_LINE).toMutableList()
    for (function in functions) {
        val functionInstructions = function.instructions.flatMap { it.toCharArray().map { c -> c.toLong() }.plus(COMMA) }.dropLast(1).plus(NEW_LINE)
        instructions.addAll(functionInstructions)
    }
    return instructions.plus('n'.toLong()).plus(NEW_LINE)
}

fun wakeUpVacuumRobot(): LongArray {
    val wakeUpRobotInput = input.clone()
    wakeUpRobotInput[0] = 2
    return wakeUpRobotInput
}

fun notifyRobots() {
    val wakeUpRobotInput = wakeUpVacuumRobot()
    val asciiProgram = LongCodeProgram(wakeUpRobotInput)
    asciiProgram.clearOutputAfterRun = true
    asciiProgram.debug = false

    val firstOutput = asciiProgram.run().output
    val cameraOutput = parseCameraOutput(firstOutput)
    displayCameraOutput(cameraOutput)

    val movements = buildVacuumRobotPath(cameraOutput)
    val functions = findFunctions(movements)
    val inputInstructions = buildInputInstructions(movements, functions)

    asciiProgram.setInputs(inputInstructions.toLongArray())
    val finalOutput = asciiProgram.run().output
    val dustCollected = finalOutput.last()
    val finalCameraOutput = parseCameraOutput(finalOutput)
    displayCameraOutput(finalCameraOutput)
    logger.info("The vacuum robot report it has collected $dustCollected dust")
}

fun runPartOneAndTwo() {

    // Part One
    val sumOfAlignementParameters = calibrateCameras()
    logger.info("The sum of the alignment parameters is $sumOfAlignementParameters")

    notifyRobots()
}

fun main() {
    input = readInputCommaSeparatedFileAsLong("d17/input.txt")
    runPartOneAndTwo()
}