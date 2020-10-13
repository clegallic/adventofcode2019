package d08

import readInputStringFile

class PartOne(private val input: String) {

    private val WIDTH = 25
    private val HEIGHT = 6
    private val OFFSET = WIDTH * HEIGHT

    private fun layerWithFewestZeroDigits(): String{
        var layerWithFewestContent = ""
        var fewestNumberOfZeroFound = Int.MAX_VALUE
        var c = 0
        while(c < input.length){
            val layerContent = input.substring(c, c + OFFSET)
            val numberOfZero = layerContent.count { it == '0' }
            if(numberOfZero < fewestNumberOfZeroFound){
                layerWithFewestContent = layerContent
                fewestNumberOfZeroFound = numberOfZero
            }
            c += OFFSET
        }
        return layerWithFewestContent
    }

    fun run(){
        println("Run with height = $HEIGHT, width=$WIDTH, offset=$OFFSET")
        val layerWithFewestZero = layerWithFewestZeroDigits()
        val result = layerWithFewestZero.count{ it == '1'} * layerWithFewestZero.count{ it == '2' }
        println(result)
    }
}

fun main() {
    val p = PartOne(readInputStringFile("d08/input.txt"))
    p.run()
}