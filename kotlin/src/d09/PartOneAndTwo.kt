package d09

import LongCodeProgram
import readInputCommaSeparatedFileAsLong

class PartOneAndTwo(private val input: LongArray) {
    fun run(){
        val partOne = LongCodeProgram(input)
        partOne.debug = false
        partOne.addInput(1)
        val partOneOutput = partOne.run()
        println("Part 1 = $partOneOutput")

        val partTwo = LongCodeProgram(input)
        partTwo.debug = false
        partTwo.addInput(2)
        val partTwoOutput = partTwo.run()
        println("Part 2 = $partTwoOutput")
    }
}

fun main() {
    PartOneAndTwo(readInputCommaSeparatedFileAsLong("d09/input.txt")).run()
}