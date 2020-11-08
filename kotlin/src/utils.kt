fun readInputCommaSeparatedFile(inputPath: String): IntArray{
    val content = IntCodeProgram::class.java.getResource(inputPath).readText()
    return content.split(",").map{ it.toInt() }.toIntArray()
}

fun readInputCommaSeparatedFileAsLong(inputPath: String): LongArray{
    val content = IntCodeProgram::class.java.getResource(inputPath).readText()
    return content.split(",").map{ it.toLong() }.toLongArray()
}

fun readInputStringFile(inputPath: String): String{
    return IntCodeProgram::class.java.getResource(inputPath).readText()
}

fun Int.padTwoDigits(): String {
    return this.toString().padStart(2, '0')
}