import kotlin.system.exitProcess

data class IntCodeProgramOutput(val output: Int, val completed: Boolean) {
    override fun toString(): String {
        return "Completed : $completed, sortie : $output"
    }
}

class IntCodeProgram(private val program: IntArray) {

    var inputs = mutableListOf<Int>()
    var output = 0
    var relativeBase = 0
    var endReached = false
    var debug = true

    private var halted = false
    private var currentPosition = 0

    fun addInput(input: Int) {
        this.inputs.add(input)
    }

    fun run(): IntCodeProgramOutput {
        if(debug) println("Lancement depuis la position $currentPosition avec input = ${inputs.toIntArray().contentToString()}")
        halted = false
        while (currentPosition < program.size && !endReached && !halted) {
            when (val opCode = program[currentPosition]) {
                99 -> {
                    if(debug) println("Fin du programme. Output = $output")
                    endReached = true
                }
                in 1..10000 -> {
                    proceedInstruction(opCode)
                }
                else -> {
                    println("Erreur, opCode non reconnu : $opCode")
                    exitProcess(1)
                }
            }
            if(debug) println(program.contentToString())
        }
        return IntCodeProgramOutput(output, true)
    }

    private fun getValue(parameterOffset: Int, modes: MutableList<ParameterMode>): Int {
        return when (modes[parameterOffset - 1]) {
            ParameterMode.POSITION_MODE -> program[program[currentPosition + parameterOffset]]
            ParameterMode.IMMEDIATE_MODE -> program[currentPosition + parameterOffset]
            ParameterMode.RELATIVE_MODE -> program[relativeBase + program[currentPosition + parameterOffset]]
            ParameterMode.UNKNOWN -> throw RuntimeException("Mode inconnu : ${modes[parameterOffset]}")
        }
    }

    private fun setValue(parameterOffset: Int, value: Int, modes: MutableList<ParameterMode>) {
        when (modes[parameterOffset - 1]) {
            ParameterMode.POSITION_MODE -> program[program[currentPosition + parameterOffset]] = value
            ParameterMode.IMMEDIATE_MODE -> program[currentPosition + parameterOffset] = value
            ParameterMode.RELATIVE_MODE -> program[relativeBase + program[currentPosition + parameterOffset]] = value
            ParameterMode.UNKNOWN -> throw RuntimeException("Mode inconnu : ${modes[parameterOffset]}")
        }
    }

    private fun proceedInstruction(opCode: Int) {
        var instruction = opCode
        val parameterModes = mutableListOf(ParameterMode.POSITION_MODE, ParameterMode.POSITION_MODE, ParameterMode.POSITION_MODE)
        if (opCode > 10) {
            instruction = opCode % 1000 % 100
            val opCodeFiveDigits = opCode.toString().padStart(5, '0')
            parameterModes[0] = ParameterMode.from(opCodeFiveDigits[2].toString().toInt())
            parameterModes[1] = ParameterMode.from(opCodeFiveDigits[1].toString().toInt())
            parameterModes[2] = ParameterMode.from(opCodeFiveDigits[0].toString().toInt())
        }
        val operationType = OperationType.from(instruction)
        if(debug) println("Position : $currentPosition, opCode = $opCode, inputs = ${inputs.toIntArray().contentToString()}, instruction = ${operationType.name}, mode1 = ${parameterModes[0].name}, mode2 = ${parameterModes[1].name}, mode3 = ${parameterModes[2].name}, relativeBase = $relativeBase")
        when (operationType) {
            OperationType.ADD -> {
                val value = getValue(1, parameterModes) + getValue(2, parameterModes)
                setValue(3, value, parameterModes)
                currentPosition += 4
            }
            OperationType.MULTIPLY -> {
                val value = getValue(1, parameterModes) * getValue(2, parameterModes)
                setValue(3, value, parameterModes)
                currentPosition += 4
            }
            OperationType.INPUT -> {
                if (inputs.isNotEmpty()) {
                    setValue(1, inputs[0], parameterModes)
                    inputs.removeAt(0)
                    currentPosition += 2
                } else {
                    halted = true
                }
            }
            OperationType.OUTPUT -> {
                output = getValue(1, parameterModes)
                currentPosition += 2
            }
            OperationType.JUMP_IF_TRUE -> {
                if (getValue(1, parameterModes) != 0) {
                    currentPosition = getValue(2, parameterModes)
                } else {
                    currentPosition += 3
                }
            }
            OperationType.JUMP_IF_FALSE -> {
                if (getValue(1, parameterModes) == 0) {
                    currentPosition = getValue(2, parameterModes)
                } else {
                    currentPosition += 3
                }
            }
            OperationType.LESS_THAN -> {
                if (getValue(1, parameterModes) < getValue(2, parameterModes)) {
                    setValue(3, 1, parameterModes)
                } else {
                    setValue(3, 0, parameterModes)
                }
                currentPosition += 4
            }
            OperationType.EQUALS -> {
                if (getValue(1, parameterModes) == getValue(2, parameterModes)) {
                    setValue(3, 1, parameterModes)
                } else {
                    setValue(3, 0, parameterModes)
                }
                currentPosition += 4
            }
            OperationType.ADJUST_RELATIVE_BASE -> {
                relativeBase += getValue(1, parameterModes)
                currentPosition += 2
            }
            else -> {
                println("Erreur, instruction non reconnue : $instruction")
                exitProcess(1)
            }
        }
    }

    fun clone(): IntCodeProgram{
        val cloned = IntCodeProgram(program.clone())
        cloned.output = 0
        cloned.halted = false
        cloned.currentPosition = 0
        cloned.debug = debug
        cloned.endReached = false
        return cloned
    }
}

fun main() {
    //val D02_01_sample2 = intArrayOf(2,3,0,3,99)
    //val D02_01_sample4 = intArrayOf(1,1,1,4,99,5,6,0,99)
    //val D05_02_sample1 = intArrayOf(3,9,8,9,10,9,4,9,99,-1,8)
    //val D05_01_sample1 = intArrayOf(1002,4,3,4,33)
    //val D05_02_sample2 = intArrayOf(3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99)
    //val intCodeProgram_D05_01_input = readInputCommaSeparatedFile("d05/input.txt")
    val D09_01_sample1 = intArrayOf(109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99)
    val D09_01_sample2 = intArrayOf(1102,34915192,34915192,7,4,7,99,0)
    val intCodeProgram = IntCodeProgram(D09_01_sample2)
    intCodeProgram.addInput(5)
    println(intCodeProgram.run())
}