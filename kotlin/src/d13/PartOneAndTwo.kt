package d13

import LongCodeProgram
import Position2D
import readInputCommaSeparatedFileAsLong

lateinit var input: LongArray

enum class TileType(val value: Long, val symbol: Char) {
    EMPTY(0, ' '), WALL(1, '▉'), BLOCK(2, '▉'), PADDLE(3, '▁'), BALL(4, '●'), UNKNOWN(-1, '?');

    companion object {
        fun ofValue(value: Long): TileType = values().find { it.value == value } ?: UNKNOWN
    }
}

data class Tile(val type: TileType, val position: Position2D)

fun parseScreenInfo(screenInfo: LongArray): List<Tile> {
    val tiles = arrayListOf<Tile>()
    for (i in screenInfo.indices step 3) {
        if (screenInfo[i] == -1L) {
            score = screenInfo[i + 2]
        } else {
            val title = Tile(TileType.ofValue(screenInfo[i + 2]), Position2D(screenInfo[i], screenInfo[i + 1]))
            tiles.add(title)
        }
    }
    return tiles
}

lateinit var screen: Array<Array<TileType>>
var score = 0L

fun drawGameScreen(screenInfo: LongArray) {
    val tiles:List<Tile> = parseScreenInfo(screenInfo)
    if (!::screen.isInitialized) {
        val screenDimension = tiles.fold(Position2D(0, 0)) { acc, tile ->
            val width = if (tile.position.x > acc.x) tile.position.x else acc.x
            val height = if (tile.position.y > acc.y) tile.position.y else acc.y
            Position2D(width, height)
        }
        screen = Array(screenDimension.y.toInt() + 1) { Array(screenDimension.x.toInt() + 1) { TileType.EMPTY } }
    }
    for (tile in tiles) {
        screen[tile.position.y.toInt()][tile.position.x.toInt()] = tile.type
    }
    for (line in screen) {
        println(line.map { it.symbol }.toTypedArray().joinToString(separator = ""))
    }
    println("Score : $score")
}

fun findNumberOfBlockTiles(screenInfo: LongArray): Long {
    var nbBlockTiles = 0L
    for (i in 2 until screenInfo.size step 3) {
        if (screenInfo[i] == TileType.BLOCK.value) nbBlockTiles++
    }
    return nbBlockTiles
}

fun findTilePosition(screenInfo: LongArray, type: TileType): Position2D? {
    for (i in screenInfo.size - 1 downTo 2 step 3) {
        if (screenInfo[i] == type.value) {
            return Position2D(screenInfo[i - 2], screenInfo[i - 1])
        }
    }
    return null
}

fun runPartOne() {
    val gameProgram = LongCodeProgram(input)
    gameProgram.debug = false
    val screenInfo = gameProgram.run().output
    val nbBlockTiles = findNumberOfBlockTiles(screenInfo)
    println("There are $nbBlockTiles of block tiles")
}

fun runPartTwo() {
    input[0] = 2
    val gameProgram = LongCodeProgram(input)
    gameProgram.clearOutputAfterRun = true
    gameProgram.debug = false
    var nextPaddleOffset = 0L
    var gameStopped = false
    while (!gameStopped) {
        gameProgram.addInput(nextPaddleOffset)
        val screenInfo = gameProgram.run().output
        gameStopped = gameProgram.endReached
        if (!gameStopped) {
            val paddlePosition = findTilePosition(screenInfo, TileType.PADDLE)!!
            val ballPosition = findTilePosition(screenInfo, TileType.BALL)!!
            drawGameScreen(screenInfo)
            println("paddle : $paddlePosition, ball : $ballPosition")
            nextPaddleOffset = when {
                paddlePosition.x < ballPosition.x -> 1
                paddlePosition.x > ballPosition.x -> -1
                else -> 0
            }
        }
    }
}

fun main() {
    input = readInputCommaSeparatedFileAsLong("d13/input.txt")
    //runPartOne()
    runPartTwo()
}