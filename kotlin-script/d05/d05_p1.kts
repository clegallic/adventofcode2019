fun operation(opCode: Int, i: Int, isPosMode1: Boolean, isPosMode2: Boolean, isPosMode3: Boolean, inputInstruction: Int, intCodeProgram: IntArray): Int{
    when(opCode){
        1 -> {
            intCodeProgram[intCodeProgram[i+3]] = (if(isPosMode1) intCodeProgram[intCodeProgram[i+1]] else intCodeProgram[i+1]) + (if(isPosMode2) intCodeProgram[intCodeProgram[i+2]] else intCodeProgram[i+2])
            return 4
        }
        2 -> {
            intCodeProgram[intCodeProgram[i+3]] = (if(isPosMode1) intCodeProgram[intCodeProgram[i+1]] else intCodeProgram[i+1]) * (if(isPosMode2) intCodeProgram[intCodeProgram[i+2]] else intCodeProgram[i+2])
            return 4
        }
        3 -> {
            println("Input : " + inputInstruction)
            intCodeProgram[intCodeProgram[i+1]] = inputInstruction
            return 2
        }
        4 -> {
            println("Output : " + intCodeProgram[intCodeProgram[i+1]])
            return 2
        }
        else -> {
            println("Erreur, opCode non reconnu : $opCode")
            return -1
         }
    }
}

fun compute(intCodeProgram: IntArray, inputInstruction: Int) {
    var i = 0;
	loop@ while(i < intCodeProgram.size) {
        var opCode = intCodeProgram[i]
        when(opCode){
            in 1..4 -> i += operation(opCode, i, true, true, true, inputInstruction, intCodeProgram)
            in 100..10000 -> {
                i += operation(opCode % 1000 % 100, i, (opCode % 1000) / 100 == 0, opCode / 1000 == 0, opCode / 10000 == 0, inputInstruction, intCodeProgram)
            }
            99 -> {
                println("Fin du programme")
                break@loop
            }
            else -> {
                println("Erreur, opCode non reconnu : $opCode")
                break@loop
            }
        }
        if(i>1000) break
	}
}

var input = java.io.File("input.txt").readText()

var intCodeProgram = input.split(",").map{ it.toInt() }.toIntArray()
compute(intCodeProgram, 1)