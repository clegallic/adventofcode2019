package d08

import readInputStringFile

enum class Colors(val value: Int, val symbol: String){
    BLACK(0, " "), WHITE(1, "▓"), TRANSPARENT(2, "_"), UNKNOWN(-1, "!");

    companion object {
        fun from(value: Int):Colors = values().find { it.value == value} ?: UNKNOWN
    }
}

class PartTwo(private val input: String) {

    private val WIDTH = 25
    private val HEIGHT = 6
    private val LAYER_OFFSET = WIDTH * HEIGHT

    private fun generateImage(): String{
        var result = ""
        for(c in 0 until LAYER_OFFSET){
            var finalColor = Colors.UNKNOWN
            var layerIndex = 0
            while(finalColor == Colors.UNKNOWN){
                val color = Colors.from(input[c + layerIndex * LAYER_OFFSET].toString().toInt())
                if(color == Colors.TRANSPARENT) {
                    layerIndex++
                } else {
                    finalColor = color
                }
            }
            result += finalColor.symbol
            if((c + 1) % WIDTH == 0) result += "\r\n"
        }
        return result
    }

    fun run(){
        println("Run with height = $HEIGHT, width=$WIDTH, offset=$LAYER_OFFSET")
        println(generateImage())
    }
}

fun main() {
    val p = PartTwo(readInputStringFile("d08/input.txt"))
    p.run()
}