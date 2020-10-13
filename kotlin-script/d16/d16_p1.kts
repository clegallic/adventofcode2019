//var initialInput = "123456781234567812345678"
var initialInput = java.io.File("input.txt").readText()

var nbIterations = 100
val begin = System.currentTimeMillis()

// neme ligne : n tous les (n * 2) à partir de n

fun computePhase(phaseInput: String): String{
    var phaseOutput = ""
    var lineNb = 1
    while(lineNb < phaseInput.length + 1){
        var offset = lineNb - 1
        var len = lineNb;
        var shift = lineNb * 2
        var lineResult = 0;
        var parity = 1;
        while (offset < phaseInput.length){
            var digits = phaseInput.substring(offset, Math.min(offset + len, phaseInput.length))
            for(d in digits){
                lineResult += Character.getNumericValue(d) * parity
            }
            parity *= -1
            offset += shift
        }
        phaseOutput += Math.abs(lineResult).toString().last()
        lineNb++
    }
    return phaseOutput
}

var input = initialInput;
var lineNb = 1

repeat(nbIterations){
    input = computePhase(input)
    //println("$index : $input")
}

val duration = (System.currentTimeMillis() - begin.toDouble()) / 1000
val result = input.substring(0,8);
println("$result in ${duration} seconds")