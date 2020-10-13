import kotlin.system.exitProcess

data class LongCodeProgramOutput(val output: LongArray, val completed: Boolean) {
    override fun toString(): String {
        return "Completed : $completed, sortie : ${output.contentToString()}"
    }
}

class LongCodeProgram(private var program: LongArray) {

    var inputs = mutableListOf<Long>()
    var outputs = mutableListOf<Long>()
    var relativeBase = 0
    var endReached = false
    var debug = true

    private var halted = false
    private var currentPosition = 0

    fun addInput(input: Long) {
        this.inputs.add(input)
    }

    fun run(): LongCodeProgramOutput {
        if(debug) println("Lancement depuis la position $currentPosition avec input = ${inputs.toLongArray().contentToString()}")
        halted = false
        while (currentPosition < program.size && !endReached && !halted) {
            when (val opCode = program[currentPosition]) {
                99L -> {
                    if(debug) println("Fin du programme. Output = $outputs.to")
                    endReached = true
                }
                in 1..99999 -> {
                    proceedInstruction(opCode)
                }
                else -> {
                    println("Erreur, opCode non reconnu : $opCode")
                    exitProcess(1)
                }
            }
            if(debug) println(program.contentToString())
        }
        return LongCodeProgramOutput(outputs.toLongArray(), true)
    }

    private fun ensureProgramSize(position: Int){
        if(position > program.size){
            program = program.plus(LongArray(position - program.size + 2) { 0 })
        }
    }

    private fun getPosition(parameterOffset: Int, modes: MutableList<ParameterMode>): Int{
        return when (modes[parameterOffset - 1]) {
            ParameterMode.POSITION_MODE -> program[currentPosition + parameterOffset].toInt()
            ParameterMode.IMMEDIATE_MODE -> currentPosition + parameterOffset
            ParameterMode.RELATIVE_MODE -> relativeBase + program[currentPosition + parameterOffset].toInt()
            ParameterMode.UNKNOWN -> throw RuntimeException("Mode inconnu : ${modes[parameterOffset]}")
        }
    }

    private fun getValue(parameterOffset: Int, modes: MutableList<ParameterMode>): Long {
        val position = getPosition(parameterOffset, modes)
        ensureProgramSize(position)
        return program[position]
    }

    private fun setValue(parameterOffset: Int, value: Long, modes: MutableList<ParameterMode>) {
        val position = getPosition(parameterOffset, modes)
        ensureProgramSize(position)
        program[position] = value
    }

    private fun proceedInstruction(opCode: Long) {
        var instruction = opCode
        val parameterModes = mutableListOf(ParameterMode.POSITION_MODE, ParameterMode.POSITION_MODE, ParameterMode.POSITION_MODE)
        if (opCode > 10) {
            instruction = opCode % 1000 % 100
            val opCodeFiveDigits = opCode.toString().padStart(5, '0')
            parameterModes[0] = ParameterMode.from(opCodeFiveDigits[2].toString().toInt())
            parameterModes[1] = ParameterMode.from(opCodeFiveDigits[1].toString().toInt())
            parameterModes[2] = ParameterMode.from(opCodeFiveDigits[0].toString().toInt())
        }
        val operationType = OperationType.from(instruction.toInt())
        if(debug) println("Position : $currentPosition, opCode = $opCode, inputs = ${inputs.toLongArray().contentToString()}, instruction = ${operationType.name}, mode1 = ${parameterModes[0].name}, mode2 = ${parameterModes[1].name}, mode3 = ${parameterModes[2].name}, relativeBase = $relativeBase")
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
                outputs.add(getValue(1, parameterModes))
                currentPosition += 2
            }
            OperationType.JUMP_IF_TRUE -> {
                if (getValue(1, parameterModes) != 0L) {
                    currentPosition = getValue(2, parameterModes).toInt()
                } else {
                    currentPosition += 3
                }
            }
            OperationType.JUMP_IF_FALSE -> {
                if (getValue(1, parameterModes) == 0L) {
                    currentPosition = getValue(2, parameterModes).toInt()
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
                relativeBase += getValue(1, parameterModes).toInt()
                currentPosition += 2
            }
            else -> {
                println("Erreur, instruction non reconnue : $instruction")
                exitProcess(1)
            }
        }
    }

    fun clone(): LongCodeProgram{
        val cloned = LongCodeProgram(program.clone())
        cloned.outputs = mutableListOf()
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
    //val D05_01_sample1 = longArrayOf(1002,4,3,4,33)
    //val D05_02_sample2 = intArrayOf(3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99)
    //val intCodeProgram_D05_01_input = readInputCommaSeparatedFile("d05/input.txt")
    val D09_01_sample1 = longArrayOf(109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99)
    val D09_01_sample2 = longArrayOf(1102,34915192,34915192,7,4,7,99,0)
    val D09_01_sample3 = longArrayOf(104,1125899906842624,99)
    val intCodeProgram = LongCodeProgram(D09_01_sample1)
    //intCodeProgram.addInput(5)
    println(intCodeProgram.run())
}