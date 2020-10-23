package d12

import readInputStringFile
import kotlin.math.abs

data class Position3D(var x: Int, var y: Int, var z: Int) {
    fun clone(): Position3D {
        return Position3D(x, y, z)
    }

    fun axisValue(index: Int): Int {
        return when (index) {
            0 -> this.x
            1 -> this.y
            2 -> this.z
            else -> throw RuntimeException("axe invalide")
        }
    }

    fun setAxisValue(index: Int, value: Int) {
        when (index) {
            0 -> this.x = value
            1 -> this.y = value
            2 -> this.z = value
            else -> throw RuntimeException("axe invalide")
        }
    }
}

data class Velocity3D(var x: Int = 0, var y: Int = 0, var z: Int = 0)

class Moon(val index: Int, val position: Position3D, val velocity: Velocity3D) {

    var periodFound = arrayOf(false, false, false)

    fun move() {
        this.position.x += this.velocity.x
        this.position.y += this.velocity.y
        this.position.z += this.velocity.z
    }

    fun allPeriodsFound(): Boolean{
        return periodFound.all { it }
    }

    override fun toString(): String {
        return "pos=<$position>, vel=<$velocity>\r\n"
    }

    override fun equals(other: Any?): Boolean {
        return other is Moon && other.index == this.index
    }
}

class PartOneAndTwo(val input: String) {

    private lateinit var moons: MutableList<Moon>
    private val inputPattern = Regex("<x=([-0-9]+), y=([-0-9]+), z=([-0-9]+)>")

    private fun parseInput(): MutableList<Moon> {
        val moons = mutableListOf<Moon>()
        for ((index, line) in input.lines().withIndex()) {
            val (x, y, z) = inputPattern.find(line)!!.destructured
            val moon = Moon(index, Position3D(x.toInt(), y.toInt(), z.toInt()), Velocity3D())
            moons.add(moon)
        }
        return moons
    }

    private fun coef(a: Int, b: Int): Int {
        val diff = b - a
        return if (diff == 0) 0 else diff.div(abs(diff))
    }

    private fun compareMoons(moon1: Moon, moon2: Moon) {
        val coefX = coef(moon1.position.x, moon2.position.x)
        if (coefX != 0) {
            moon1.velocity.x += coefX
            moon2.velocity.x -= coefX
        }
        val coefY = coef(moon1.position.y, moon2.position.y)
        if (coefY != 0) {
            moon1.velocity.y += coefY
            moon2.velocity.y -= coefY
        }
        val coefZ = coef(moon1.position.z, moon2.position.z)
        if (coefZ != 0) {
            moon1.velocity.z += coefZ
            moon2.velocity.z -= coefZ
        }
    }

    private fun simulateMotion() {
        for ((i, moon) in moons.withIndex()) {
            for (j in i + 1 until moons.size) {
                val otherMoon = moons[j]
                compareMoons(moon, otherMoon)
            }
        }
        moons.forEach { moon -> moon.move() }
    }

    private fun computeEnergy(moon: Moon): Int {
        return (abs(moon.position.x) + abs(moon.position.y) + abs(moon.position.z)) * (abs(moon.velocity.x) + abs(moon.velocity.y) + abs(moon.velocity.z))
    }

    private fun computeTotalEnergy(): Int {
        return moons.sumOf { computeEnergy(it) }
    }

    fun runPartOne(steps: Int) {
        moons = parseInput()
        repeat(steps) {
            simulateMotion()
        }
        println("Part 1 : total energy in the system is ${computeTotalEnergy()}")
    }

    private fun checkPeriod(moon: Moon, step: Int, axisIndex: Int) {
        var axisPossiblePeriod = possiblePeriods[moon]!!.axisValue(axisIndex)
        val moonCurrentAxisValue = moon.position.axisValue(axisIndex)
        val moonFirstAxisValue = positions[moon]!![0].axisValue(axisIndex)
        val latestPossiblePeriod = latestPossiblePeriodStep[moon]!!.axisValue(axisIndex)
        if (axisPossiblePeriod > 0) {
            //println("Check if ${moon.position.x} == ${positions[moon]!![step - possiblePeriods[moon]!!.first].x}, offset = ${step - possiblePeriods[moon]!!.first}")
            if (moonCurrentAxisValue != positions[moon]!![step - axisPossiblePeriod].axisValue(axisIndex)) {
                axisPossiblePeriod = -1
                possiblePeriods[moon]!!.setAxisValue(axisIndex, axisPossiblePeriod)
            } else if (moon.position.axisValue(axisIndex) == moonFirstAxisValue) {
                if(latestPossiblePeriod != -1 && step - 2 * latestPossiblePeriod > 0) {
                    val period = latestPossiblePeriod
                    periods[moon]!!.setAxisValue(axisIndex, period)
                    moon.periodFound[axisIndex] = true
                    //println("Period found for moon ${moon.index} and axis $axisIndex: ${period}")
                    return
                }
            }
        }
        if (axisPossiblePeriod == -1 && moonCurrentAxisValue == moonFirstAxisValue) {
            val possiblePeriod = step
            //println("Found possible period of $possiblePeriod at $step for moon ${moon.index} and axis $axisIndex")
            latestPossiblePeriodStep[moon]!!.setAxisValue(axisIndex, step)
            possiblePeriods[moon]!!.setAxisValue(axisIndex, step)
        }
    }

    private fun storeMoonsPosition() {
        for (moon in moons) {
            if (positions.containsKey(moon)) {
                positions[moon]!!.add(moon.position.clone())
            } else {
                positions[moon] = mutableListOf(moon.position.clone())
            }
        }
    }

    private fun findAxisPeriods() {
        var step = 0
        var allFound = false
        while (!allFound) {
            storeMoonsPosition()
            simulateMotion()
            step++
            for (moon in moons) {
                if(moon.allPeriodsFound()) continue
                for (axisIndex in 0..2) {
                    if(moon.periodFound[axisIndex]) continue
                    checkPeriod(moon, step, axisIndex)
                }
            }
            //println("$step : ${possiblePeriods[moons[0]]}")
            allFound = moons.all{ it.allPeriodsFound() }
        }
    }

    lateinit var periods: HashMap<Moon, Position3D>
    lateinit var possiblePeriods: HashMap<Moon, Position3D>
    lateinit var latestPossiblePeriodStep: HashMap<Moon, Position3D>

    val positions = mutableMapOf<Moon, MutableList<Position3D>>()

    private fun buildPeriodMap(): HashMap<Moon, Position3D> {
        return object : HashMap<Moon, Position3D>() {
            init {
                for (moon in moons) {
                    put(moon, Position3D(-1, -1, -1))
                }
            }
        }
    }

    private fun ppcd(a: Long, b: Long): Long {
        return if (b == 0L) a else ppcd(b, a % b)
    }

    private fun ppcm(a: Long, b: Long): Long {
        return if (a == b) a else (a *b) / ppcd(a,b)
    }

    private fun ppcm(p: Position3D): Long {
        return ppcm(ppcm(p.x.toLong(), p.y.toLong()), p.z.toLong())
    }

    fun runPartTwo() {
        moons = parseInput()
        periods = buildPeriodMap()
        possiblePeriods = buildPeriodMap()
        latestPossiblePeriodStep = buildPeriodMap()
        findAxisPeriods()
        val result = periods.values.fold(1L) { acc, period -> ppcm(acc, ppcm(period)) }
        println("Part 2 : it takes $result steps to reach the first state")
    }
}

fun main() {
    val input = readInputStringFile("d12/input.txt")
    PartOneAndTwo(input).runPartOne(1000)
    PartOneAndTwo(input).runPartTwo()
}