package d07

import IntCodeProgram
import IntCodeProgramOutput
import readInputCommaSeparatedFile

class Amplifier(private val index: Int, initialProgram: IntCodeProgram) {
    val program = initialProgram.clone()
    var isLast = false

    lateinit var next: Amplifier

    fun addInput(input: Int) {
        this.program.addInput(input)
    }

    fun run(): IntCodeProgramOutput {
        println("Lancement de l'amplifier $index")
        return this.program.run()
    }
}

fun thruster(intCodeProgram: IntCodeProgram, phases: IntArray): Int {
    val amplifiers = mutableListOf<Amplifier>()

    // Initialisation des amplifiers en ordre inverse
    for ((index, phase) in phases.withIndex().reversed()) {
        val amp = Amplifier(index, intCodeProgram)
        amp.addInput(phase)
        if (index < phases.size - 1) {
            amp.next = amplifiers.first()
        }
        amplifiers.add(0,amp)
    }
    // Boucle dernier amplifier vers premier
    amplifiers.last().isLast = true
    amplifiers.last().next = amplifiers.first()

    var currentAmp = amplifiers.first()
    currentAmp.addInput(0)
    while(!(currentAmp.program.endReached && currentAmp.isLast)){
        currentAmp.next.addInput(currentAmp.run().output)
        currentAmp = currentAmp.next
    }
    return currentAmp.program.output
}

fun main() {
    //val input = intArrayOf(3,52,1001,52,-5,52,3,53,1,52,56,54,1007,54,5,55,1005,55,26,1001,54,-5,54,1105,1,12,1,53,54,53,1008,54,0,55,1001,55,1,55,2,53,55,53,4,53,1001,56,-1,56,1005,56,6,99,0,0,0,0,10)
    val input = readInputCommaSeparatedFile("d07/input.txt")
    val intCodeProgram = IntCodeProgram(input)
    intCodeProgram.debug = false
    var highest = 0
    var highestCombination = ""
    val offset = 5
    for (i1 in 0..4) {
        for (i2 in 0..4) {
            if (i2 != i1) {
                for (i3 in 0..4) {
                    if (i3 != i1 && i3 != i2) {
                        for (i4 in 0..4) {
                            if (i4 != i1 && i4 != i2 && i4 != i3) {
                                for (i5 in 0..4) {
                                    if (i5 != i1 && i5 != i2 && i5 != i3 && i5 != i4) {
                                        var total = thruster(intCodeProgram, intArrayOf(i1 + offset, i2 + offset, i3 + offset, i4 + offset, i5 + offset))
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