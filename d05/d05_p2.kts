fun operation(opCode: Int, i: Int, isPosMode1: Boolean, isPosMode2: Boolean, isPosMode3: Boolean, inputInstruction: Int, intCodeProgram: IntArray): Int{
    println("opCode = $opCode, i=$i, intCodeProgram[i]=${intCodeProgram[i]} isPosMode1=$isPosMode1, isPosMode2=$isPosMode2, isPosMode3=$isPosMode3, inputInstruction=$inputInstruction")
    var arg1 = -1; var arg2 = -1;
    if(opCode < 3 || (opCode > 4 && opCode < 9)){
       arg1 = if(isPosMode1) intCodeProgram[intCodeProgram[i + 1]] else intCodeProgram[i + 1]
       arg2 = if(isPosMode2) intCodeProgram[intCodeProgram[i + 2]] else intCodeProgram[i + 2]
    }
    when(opCode){
        1 -> {
            intCodeProgram[intCodeProgram[i+3]] = arg1 + arg2
            println("intCodeProgram[${intCodeProgram[i + 3]}] = $arg1 + $arg2")
            return i + 4
        }
        2 -> {
            intCodeProgram[intCodeProgram[i+3]] = arg1 * arg2
            println("intCodeProgram[${intCodeProgram[i + 3]}] = $arg1 * $arg2")
            return i + 4
        }
        3 -> {
            intCodeProgram[intCodeProgram[i+1]] = inputInstruction
            println("intCodeProgram[${intCodeProgram[i + 1]}] = $inputInstruction")
            return i + 2
        }
        4 -> {
            println("Output : " + intCodeProgram[intCodeProgram[i+1]])
            return i + 2
        }
        5 -> {
            if(arg1 != 0){
                return arg2
            } else {
                return i + 3
            }
        }
        6 -> {
            if(arg1 == 0){
                return arg2
            } else {
                return i + 3
            }
        }
        7 -> {
            if(arg1 < arg2){
                intCodeProgram[intCodeProgram[i + 3]] = 1;
                println("intCodeProgram[${intCodeProgram[i + 3]}] = 1")
            } else {
                intCodeProgram[intCodeProgram[i + 3]] = 0;
                println("intCodeProgram[${intCodeProgram[i + 3]}] = 0")
            }
            return i + 4
        }
        8 -> {
            if(arg1 == arg2){
                intCodeProgram[intCodeProgram[i + 3]] = 1;
                println("intCodeProgram[${intCodeProgram[i + 3]}] = 1")
            } else {
                intCodeProgram[intCodeProgram[i + 3]] = 0;
                println("intCodeProgram[${intCodeProgram[i + 3]}] = 0")
            }
            return i + 4
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
        println(java.util.Arrays.toString(intCodeProgram))
        when(opCode){
            in 1..8 -> i = operation(opCode, i, true, true, true, inputInstruction, intCodeProgram)
            in 100..10000 -> {
                i = operation(opCode % 1000 % 100, i, (opCode % 1000) / 100 == 0, opCode / 1000 == 0, opCode / 10000 == 0, inputInstruction, intCodeProgram)
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


// TEST
//var intCodeProgram = intArrayOf(3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99)
//compute(intCodeProgram, 9090)

// Real Input
var input = java.io.File("input.txt").readText()
var intCodeProgram = input.split(",").map{ it.toInt() }.toIntArray()
compute(intCodeProgram, 5)