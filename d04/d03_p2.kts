var puzzleInput = "193651-649729"

var lower = puzzleInput.split("-")[0].toInt()
var upper = puzzleInput.split("-")[1].toInt()
var validCodes = mutableListOf<String>()
//var regExp = "^(?=[0-9]{6}$)(?=(?:.*([0-9])(?!\\1))?([0-9])\\2(?!\\2))(?:0|1(?!0)|2(?![01])|3(?![0-2])|4(?![0-3])|5(?![0-4])|6(?![0-5])|7(?![0-6])|8(?![0-7])|9(?![0-8]))+$".toRegex()

println(String.format("%1$2s %2$2s" , lower, upper))

for (i in lower..upper) {
    var iS:String = i.toString()
    /*if(regExp.matches(iS)) {
        println(iS)
        validCodes.add(iS)
    }*/
    var previousC = '0'
    var isOk = true
    var repeatingLength = 1
    var hasDouble = false
    for (c in iS) {
        if(c.toInt() < previousC.toInt()) {
            isOk = false
            break
        }
        if(previousC.equals(c)) {
            repeatingLength++
        } else {
            if(repeatingLength == 2) hasDouble = true
            repeatingLength = 1
        }
        previousC = c
    }
    if(repeatingLength == 2) hasDouble = true
    if(isOk && hasDouble){
        println(iS)
        validCodes.add(iS)
    }
}

println(validCodes.size)