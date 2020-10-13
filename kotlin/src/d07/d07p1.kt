package d07

import IntCodeProgram
import readInputCommaSeparatedFile

fun thruster2(intCodeProgram: IntCodeProgram, combination: IntArray): Int {
    var result = 0
    var lastOutput = 0
    for (i in combination) {
        val amplifierProgram = intCodeProgram.clone()
        amplifierProgram.inputs = mutableListOf(i, lastOutput)
        lastOutput = amplifierProgram.run().output
        result = lastOutput
    }
    return result
}

fun main() {
    //val intCodeProgram = intArrayOf(3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0)
    //val intCodeProgram = intArrayOf(3,23,3,24,1002,24,10,24,1002,23,-1,23,101,5,23,23,1,24,23,23,4,23,99,0,0)
    //val intCodeProgram = intArrayOf(3,31,3,32,1002,32,10,32,1001,31,-2,31,1007,31,0,33,1002,33,7,33,1,33,31,31,1,32,31,31,4,31,99,0,0,0)

    val input = readInputCommaSeparatedFile("d07/input.txt")
    val intCodeProgram = IntCodeProgram(input)
    var highest = 0
    var highestCombination = ""
    for (i1 in 0..4) {
        for (i2 in 0..4) {
            if (i2 != i1) {
                for (i3 in 0..4) {
                    if (i3 != i1 && i3 != i2) {
                        for (i4 in 0..4) {
                            if (i4 != i1 && i4 != i2 && i4 != i3) {
                                for (i5 in 0..4) {
                                    if (i5 != i1 && i5 != i2 && i5 != i3 && i5 != i4) {
                                        var total = thruster2(intCodeProgram, intArrayOf(i1, i2, i3, i4, i5))
                                        if (total > highest) {
                                            highest = total
                                            highestCombination = "$i1,$i2,$i3,$i4,$i5"
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    println("Highest = $highest; Highest Combination=$highestCombination")
}