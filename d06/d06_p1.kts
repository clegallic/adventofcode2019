data class OrbDef(val center: String, val orbit: String)
    
var inputLines = java.io.File("input.txt").readLines()
val defs = mutableListOf<OrbDef>()

for(line in inputLines) {
    defs.add(OrbDef(line.split(")")[0], line.split(")")[1]))
}

var total = 0;
for(def in defs) {
    total += countIndirect(def)
    total++
}
println(total)

fun countIndirect(def: OrbDef): Int {
    for(otherDef in defs) {
        if(def.center.equals(otherDef.orbit)){
            if(otherDef.center.equals("COM")) {
                return 1
            } else {
                return countIndirect(otherDef) + 1
            }
        }
    }
    return 0
}