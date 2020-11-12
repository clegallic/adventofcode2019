package d19

import Logger
import LongCodeProgram
import readInputCommaSeparatedFileAsLong

val logger = Logger(false)

fun main() {
    val input = readInputCommaSeparatedFileAsLong("d19/input.txt")
    runPartOne(input)
    runPartTwo(input, 100)
}

fun runPartOne(input: LongArray) {
    val result = beam(input, 49, 49)
    logger.info("Part One : $result points are affected by the tractor beam in the 50x50 area closest to the emitter")
}

fun beam(input: LongArray, width: Long, height: Long): Long{
    return (0..width).fold(0L) { accx, x ->
        if(logger.isDebug) println()
        accx + (0..height).fold(0L) { accy, y ->
            val asciiProgram = LongCodeProgram(input)
            asciiProgram.setInputs(longArrayOf(x,y))
            val out = asciiProgram.run().output.first()
            if(logger.isDebug) print(out)
            accy + out
        }
    }
}

data class RowLimits(var startAt: Int? = null, var endsAt: Int? = null)

fun runPartTwo(input: LongArray, shipDimension: Int) {
    val rows = mutableListOf<RowLimits>()
    var y = shipDimension
    var searchedCoordinates:Pair<Int, Int>? = null
    while(searchedCoordinates == null) {
        val row = RowLimits()
        var x = if(y == shipDimension) 0 else rows[y - shipDimension - 1].startAt!! // skip first positions and go to start of previous line
        while(row.endsAt == null){
            val asciiProgram = LongCodeProgram(input).setInputs(longArrayOf(x.toLong(),y.toLong()))
            val out = asciiProgram.run().output.first().toInt()
            if(row.startAt == null && out == 1) {
                row.startAt = x
                if(y > shipDimension) x = rows[y - shipDimension - 1].endsAt!! // skip next positions to go to the end of previous line
            }
            if(row.startAt != null && row.endsAt == null && out == 0) {
                row.endsAt = x - 1
            }
            if(y > 2 * shipDimension && row.startAt != null
                    && rows[(y - (2 * shipDimension - 1))].endsAt!! == row.startAt!! + shipDimension - 1){
                searchedCoordinates = Pair(row.startAt!!, y - shipDimension + 1)
                break
            }
            x++
        }
        logger.debug("Row $y analyzed : $row")
        rows.add(row)
        y++
    }
    val result = searchedCoordinates.first * 10000 + searchedCoordinates.second
    logger.info("Part Two : the value we get if you take that point's X coordinate, multiply it by 10000, then add the point's Y coordinate is $result")
}