@file:Include("intCode_p2.kt")

fun thruster(intCodeProgram: IntArray, combination: IntArray, phaseOffset: Int): Int{
    var result = 0
    var phasesComplete = false
    val amplifiers = Array(combination.size, { Pair(intCodeProgram.copyOf(), 0) })
    var iteration = 0
    var feedbackCode = 0
    while(!phasesComplete) {
        for(i in combination){
            var input: List<Int>
            var ampProgram: Pair<IntArray, Int>
            if(iteration == 0) {
                input = mutableListOf<Int>(i + phaseOffset,result)
            } else {
                input = mutableListOf<Int>(result)
            }
            ampProgram = amplifiers[i]
            println("Amplifier $i, Input : " + input + ", inputPosition : " + ampProgram.second)
            val (lastOutput, amplifierIntCodeProgram, inputPosition ) = compute(ampProgram.first, input, ampProgram.second)
            println(java.util.Arrays.toString(amplifierIntCodeProgram) + " " + lastOutput)
            if(lastOutput == -1){
                phasesComplete = true
                break
            } else {
                amplifiers[i] = Pair(amplifierIntCodeProgram, inputPosition)
                result = lastOutput
            }
        }
        feedbackCode = result
        iteration++
        if(iteration == 5) kotlin.system.exitProcess(-1)
    }
    return result
}

val intCodeProgram = intArrayOf(3,26,1001,26,-4,26,3,27,1002,27,2,27,1,27,26,27,4,27,1001,28,-1,28,1005,28,6,99,0,0,5)
//val intCodeProgram = intArrayOf(3,23,3,24,1002,24,10,24,1002,23,-1,23,101,5,23,23,1,24,23,23,4,23,99,0,0)
//val intCodeProgram = intArrayOf(3,31,3,32,1002,32,10,32,1001,31,-2,31,1007,31,0,33,1002,33,7,33,1,33,31,31,1,32,31,31,4,31,99,0,0,0)
//var input = java.io.File("input.txt").readText()
//var intCodeProgram = input.split(",").map{ it.toInt() }.toIntArray()

var highest = 0
var highestCombination = ""
var phaseOffset = 5
for (i1 in 0..4){
    for (i2 in 0..4){
        if(i2 != i1){
            for (i3 in 0..4){
                if(i3 != i1 && i3 != i2){
                    for (i4 in 0..4){
                        if(i4 != i1 && i4 != i2 && i4 != i3){
                            for (i5 in 0..4){
                                if(i5 != i1 && i5 != i2 && i5 != i3 && i5 != i4){
                                    var total = thruster(intCodeProgram, intArrayOf(i1, i2, i3, i4, i5), phaseOffset)
                                    if(total > highest) {
                                        highest = total
                                        highestCombination = "$i1,$i2,$i3,$i4,$i5"
                                    }
                                    kotlin.system.exitProcess(-1)
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