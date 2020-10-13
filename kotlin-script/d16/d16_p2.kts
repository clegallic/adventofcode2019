import kotlin.system.exitProcess

fun sumLine(line: IntArray): IntArray{
    var result = IntArray(line.size)
    result[line.size - 1] = line[line.size - 1]
    for(i in line.size - 2 downTo 0){
        result[i] += line[i] + result[i + 1]
    }
    return result.map { it % 10 }.toIntArray()
}

fun getLevelLine(level: Int, firstInputLine: IntArray): IntArray{
    // On boucle jusqu'au niveau souhaité en additionant les précédents digits du niveau précédent
    var previousLine = firstInputLine;
    var currentLine = IntArray(firstInputLine.size)
    for(currentLevel in 1..level){
        currentLine = sumLine(previousLine)
        previousLine = currentLine
    }
    return currentLine
}


fun Int.sepFormat() = String.format("%,d",this)

//var input = "12345678"

val initialInput = java.io.File("input.txt").readText()

val begin = System.currentTimeMillis()
var repeatedInput = ""
val nbRepeat = 10000
val nbIterations = 100
val offset = initialInput.substring(0,7).toInt()

println("Offset : ${offset.sepFormat()}")

repeat(nbRepeat){
    repeatedInput += initialInput;
}
var offsetInput = repeatedInput.substring(offset, repeatedInput.length)

var inputArray = offsetInput.map { Character.getNumericValue(it) } .toIntArray()
var resultLine = getLevelLine(100, inputArray)
var message = resultLine.joinToString("").substring(0, 8)

val duration = (System.currentTimeMillis() - begin.toDouble()) / 1000

println("Message : $message in $duration seconds")
