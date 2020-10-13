class Amplifier{
    val intCodeProgram: IntArray = new IntArray()
}

fun operation(opCode: Int, i: Int, isPosMode1: Boolean, isPosMode2: Boolean, isPosMode3: Boolean, inputInstruction: MutableList<Int>, intCodeProgram: IntArray): Int{
    //println("opCode = $opCode, i=$i, intCodeProgram[i]=${intCodeProgram[i]} isPosMode1=$isPosMode1, isPosMode2=$isPosMode2, isPosMode3=$isPosMode3, inputInstruction=$inputInstruction")
    var arg1 = -1; var arg2 = -1;
    if(opCode < 3 || (opCode > 4 && opCode < 9)){
       arg1 = if(isPosMode1) intCodeProgram[intCodeProgram[i + 1]] else intCodeProgram[i + 1]
       arg2 = if(isPosMode2) intCodeProgram[intCodeProgram[i + 2]] else intCodeProgram[i + 2]
    }
    when(opCode){
        1 -> {
            intCodeProgram[intCodeProgram[i+3]] = arg1 + arg2
            //println("intCodeProgram[${intCodeProgram[i + 3]}] = $arg1 + $arg2")
            return i + 4
        }
        2 -> {
            intCodeProgram[intCodeProgram[i+3]] = arg1 * arg2
            //println("intCodeProgram[${intCodeProgram[i + 3]}] = $arg1 * $arg2")
            return i + 4
        }
        3 -> {
            if(inputInstruction.size > 0)
                intCodeProgram[intCodeProgram[i+1]] = inputInstruction[0]
                //println("intCodeProgram[${intCodeProgram[i + 1]}] = ${inputInstruction[0]}")
                inputInstruction.removeAt(0)
                return i + 2
            } else return -1
        }
        4 -> {
            //println("Output : " + intCodeProgram[intCodeProgram[i+1]])
            return intCodeProgram[intCodeProgram[i+1]]
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
                //println("intCodeProgram[${intCodeProgram[i + 3]}] = 1")
            } else {
                intCodeProgram[intCodeProgram[i + 3]] = 0;
                //println("intCodeProgram[${intCodeProgram[i + 3]}] = 0")
            }
            return i + 4
        }
        8 -> {
            if(arg1 == arg2){
                intCodeProgram[intCodeProgram[i + 3]] = 1;
                //println("intCodeProgram[${intCodeProgram[i + 3]}] = 1")
            } else {
                intCodeProgram[intCodeProgram[i + 3]] = 0;
                //println("intCodeProgram[${intCodeProgram[i + 3]}] = 0")
            }
            return i + 4
        }
        else -> {
            println("Erreur, opCode non reconnu : $opCode")
            return -1
         }
    }
}

fun compute(intCodeProgram: IntArray, inputInstruction: MutableList<Int>, startFrom: Int): Triple<Int, IntArray, Int> {
    var i = startFrom;
    var intCodeProgram = intCodeProgram.copyOf()
    var inputPosition = -1;
	loop@ while(i < intCodeProgram.size) {
        var opCode = intCodeProgram[i]
        //println(java.util.Arrays.toString(intCodeProgram))
        when(opCode){
            //4 -> return Triple(intCodeProgram[intCodeProgram[i+1]], intCodeProgram, inputPosition)
            in 1..8 -> {
                i = operation(opCode, i, true, true, true, inputInstruction, intCodeProgram)
                if(i == -1){
                    return Triple(-1, intCodeProgram, inputPosition)
                }
            }
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
        //if(i>1000) break
	}
    return Triple(-1, intArrayOf(), 0)
}