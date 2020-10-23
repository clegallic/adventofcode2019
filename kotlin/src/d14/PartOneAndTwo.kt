package d14

import readInputStringFile
import kotlin.math.ceil

data class Chemical(val name: String)

open class ChemicalWithQty(val chemical: Chemical, val qty: Long) {
    override fun toString(): String {
        return "${chemical.name} : $qty"
    }
}

class InputChemical(chemical: Chemical, qty: Long, var isOre: Boolean = false) : ChemicalWithQty(chemical, qty)

class OutputChemical(chemical: Chemical, qty: Long) : ChemicalWithQty(chemical, qty)

data class Reaction(val inputs: List<InputChemical>, val output: OutputChemical)

enum class ChemicalType {
    INPUT, OUTPUT
}

class PartOneAndTwo(val input: String) {

    val ORE_NAME = "ORE"
    val FUEL_NAME = "FUEL"
    val reactions = mutableMapOf<Chemical, Reaction>()
    val chemicals = mutableMapOf<String, Chemical>()
    val remainingForChemical = mutableMapOf<Chemical, Long>()
    private lateinit var fuelOutputChemical: OutputChemical

    private fun parseChemical(toParse: String, type: ChemicalType): ChemicalWithQty {
        val splitted = toParse.split(" ")
        val qty = splitted[0].toLong()
        val chemicalName = splitted[1]
        var chemical = chemicals[chemicalName]
        if (chemical == null) {
            chemical = Chemical(chemicalName)
            remainingForChemical[chemical] = 0
        }
        return if (type == ChemicalType.INPUT) {
            InputChemical(chemical, qty, chemicalName == ORE_NAME)
        } else {
            OutputChemical(chemical, qty)
        }
    }

    private fun parseInput() {
        for (line in input.lines()) {
            val inputsString = line.split(" => ")[0].split(", ")
            val outputString = line.split(" => ")[1]
            val inputs = arrayListOf<InputChemical>()
            for (inputAsString in inputsString) {
                val inputChemical = parseChemical(inputAsString, ChemicalType.INPUT) as InputChemical
                inputs.add(inputChemical)
            }
            val output = parseChemical(outputString, ChemicalType.OUTPUT) as OutputChemical
            if (output.chemical.name == FUEL_NAME) {
                fuelOutputChemical = output
            }
            reactions[output.chemical] = Reaction(inputs, output)
        }
    }

    private fun howManyOres(chemical: Chemical, expectedQty: Long): Long {
        val reaction = reactions[chemical]!!
        val factor = ceil(expectedQty.toDouble() / reaction.output.qty).toLong()
        val remaining = factor * reaction.output.qty - expectedQty
        remainingForChemical[chemical] = remainingForChemical[chemical]!!.plus(remaining)
        var sum = 0L
        for (input in reaction.inputs) {
            if (input.isOre) {
                return factor * input.qty
            } else {
                var expectedInputQty = factor * input.qty
                val remainingQty = remainingForChemical[input.chemical]!!
                if (expectedInputQty <= remainingQty) {
                    remainingForChemical[input.chemical] = remainingQty - expectedInputQty
                } else {
                    expectedInputQty -= remainingQty
                    remainingForChemical[input.chemical] = 0
                    sum += howManyOres(input.chemical, expectedInputQty)
                }
            }
        }
        return sum
    }

    fun runPartOne() {
        parseInput()
        var numberOfOres = howManyOres(fuelOutputChemical.chemical, fuelOutputChemical.qty)
        println("$numberOfOres is needed to produce 1 FUEL")
    }

    private fun findFuelForTrillionOre(minFuel: Long, maxFuel: Long, collectedOre: Long): Long{
        if(maxFuel - minFuel == 1L){
            return minFuel
        }
        val medFuel = (maxFuel + minFuel) / 2
        val medOres = howManyOres(fuelOutputChemical.chemical, medFuel)
        return if(medOres < collectedOre){
            findFuelForTrillionOre(medFuel, maxFuel, collectedOre)
        } else {
            findFuelForTrillionOre(minFuel, medFuel, collectedOre)
        }
    }

    fun runPartTwo(){
        parseInput()
        val numberOfOresForOneFuel = howManyOres(fuelOutputChemical.chemical, fuelOutputChemical.qty)
        val collectedOre = 1000000000000
        val minFuelForTrillionOre = collectedOre / numberOfOresForOneFuel
        val maxFuelForTrillionOre = findFuelForTrillionOre(minFuelForTrillionOre, minFuelForTrillionOre * minFuelForTrillionOre, collectedOre)
        println("The maximum amount of fuel I can produce with $collectedOre ore is $maxFuelForTrillionOre")
    }
}

fun main() {
    val input = readInputStringFile("d14/input.txt")
    PartOneAndTwo(input).runPartOne()
    PartOneAndTwo(input).runPartTwo()
}