data class OrbDef(val center: String, val orbit: String)

fun buildPath(def: OrbDef, path: MutableList<String>) {
    for(otherDef in defs) {
        if(otherDef.orbit.equals(def.center)){
            path.add(0, otherDef.orbit)
            if(otherDef.center.equals("COM")) {
                path.add(0, "COM")
            } else {
                buildPath(otherDef, path)
            }
        }
    }
}

var inputLines = java.io.File("input.txt").readLines()
val defs = mutableListOf<OrbDef>()

// Build orbit definitions
for(line in inputLines) {
    defs.add(OrbDef(line.split(")")[0], line.split(")")[1]))
}

// Build My and Santa path
var myPath = arrayListOf<String>()
var sanPath = arrayListOf<String>()
for(def in defs) {
    if(def.orbit.equals("YOU") || def.orbit.equals("SAN")) {
        if(def.orbit.equals("YOU")) buildPath(def, myPath)
        if(def.orbit.equals("SAN")) buildPath(def, sanPath)
    }
}

// Keep only different parts of the paths
var myEndPath = ArrayList(myPath)
for (myStep in myPath) {
    if(sanPath.get(0).equals(myStep)){
        sanPath = ArrayList(sanPath.drop(1))
        myEndPath = ArrayList(myEndPath.drop(1))
    } else {
        break
    }
}
val total = sanPath.size + myEndPath.size
println(total)

