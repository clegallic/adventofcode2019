package d09

import LongCodeProgram
import readInputCommaSeparatedFileAsLong

class PartOne(private val input: LongArray) {
    fun run(){
        val program = LongCodeProgram(input)
        program.debug = false
        program.addInput(2)
        println(program.run())
    }
}

fun main() {
    val p = PartOne(readInputCommaSeparatedFileAsLong("d09/input.txt"))
    p.run()
}