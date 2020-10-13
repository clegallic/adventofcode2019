package d05

import IntCodeProgram
import readInputCommaSeparatedFile

fun main(args: Array<String>) {
    val input = readInputCommaSeparatedFile("d05/input.txt")
    val intCodeProgram = IntCodeProgram(input)
    intCodeProgram.addInput(5)
    println(intCodeProgram.run())
}