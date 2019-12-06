var puzzleInput = "193651-649729"

var lower = puzzleInput.split("-")[0].toInt()
var upper = puzzleInput.split("-")[1].toInt()
var validCodes = mutableListOf<String>()

println(String.format("%1$2s %2$2s" , lower, upper))

for (i in lower..upper) {
    var iS:String = i.toString()
    var sameAdjacent = false
    var previousC = '0'
    var isOk = true
    for (c in iS) {
        if(previousC.equals(c)) {
            sameAdjacent = true;
        }
        if(c.toInt() < previousC.toInt()) {
            isOk = false
            break
        }
        previousC = c
    }
    if(isOk && sameAdjacent){
        validCodes.add(iS)
    }
}

println(validCodes.size)